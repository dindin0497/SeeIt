package com.accessibility.seeit.asl

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.accessibility.seeit.R
import com.accessibility.seeit.asl.service.EnglishPOSTagger

class ASLActivity : AppCompatActivity() {

    private val TAG="ASLActivity"

    private lateinit var player: ExoPlayer

    private lateinit var speechRecognizer: SpeechRecognizer

    lateinit var tv: TextView

    lateinit var btn: Button

    val speechListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {

        }

        override fun onBeginningOfSpeech() {
            Log.d("Speech", "Speech started")
        }

        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {
            Log.d("$TAG Speech", "Speech ended")
            applyUi(false)
        }

        override fun onError(error: Int) {
            Log.d("$TAG Speech", "onError $error")
        }

        override fun onResults(results: Bundle?) {
            val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            Log.d("$TAG Speech", "onResults $data")
            translate(data?.get(0) ?: "")
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asl)

        initPlayer()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        speechRecognizer.setRecognitionListener(speechListener)

        tv = findViewById<Button>(R.id.tv)

        btn = findViewById<Button>(R.id.btnRecord)

        btn.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")   // force English (US)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                applyUi(true)
            }
            speechRecognizer.startListening(intent)
        }


        Thread(){
            EnglishPOSTagger.init(this)
        }.start()
    }

    private fun applyUi(recording: Boolean) {
        if (recording) {
            btn.text = "Stop"
            btn.isEnabled = true
            btn.setTextColor(Color.WHITE)
            btn.background?.mutate()
            btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E53935")) // red
            btn.contentDescription = "Recording, double-tap to stop"
            btn.animate().scaleX(0.98f).scaleY(0.98f).setDuration(80).withEndAction {
                btn.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
            }.start()
        } else {
            btn.text = "Record"
            btn.isEnabled = true
            btn.setTextColor(Color.WHITE)
            btn.backgroundTintList = null // back to theme default
            btn.contentDescription = "Record"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

    fun translate(sen: String?){
        Log.d(TAG,"translate $sen")
        if (sen!=null && sen.isNotBlank() && sen.isNotEmpty()) {
            runOnUiThread { tv.text=sen }
            Thread() {
                val list = Text2ASL.getUrl(applicationContext, sen)
                runOnUiThread { play(list) }

            }.start()
        }
    }

    fun initPlayer() {
        val httpFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Media3-ExoPlayer")
            .setDefaultRequestProperties(
                mapOf(
                    "Referer" to "https://www.signingsavvy.com/",
                    "Accept" to "*/*"
                )
            )

        // 2) Build player with a MediaSourceFactory that uses our data source
        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(httpFactory))
            .build()

        findViewById<PlayerView>(R.id.playerView).player = player
    }

    fun play(lists: List<String>){
        Log.d(TAG,"play $lists")
        val uris = lists.map { url-> Uri.parse(url) }

        val items = uris.map { MediaItem.fromUri(it) }

        player.setMediaItems(items)
        player.prepare()
        player.playWhenReady = true  // start automatically

        // 4) After playlist ends, seek back to first item so Play restarts the whole list
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    player.seekTo(0, 0)  // first media item, position 0
                    player.pause()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                Log.d(TAG,
                    "play index=${player.currentMediaItemIndex} uri=${uris.getOrNull(player.currentMediaItemIndex)}")
            }
        })

        // player.repeatMode = Player.REPEAT_MODE_ALL

    }

    override fun onStop() {
        super.onStop()
        findViewById<PlayerView>(R.id.playerView).player = null
        player.release()
    }



}