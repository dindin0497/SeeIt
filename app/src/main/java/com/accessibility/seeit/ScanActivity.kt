package com.accessibility.seeit

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.seeit.seeit.databinding.ActivityMainBinding
import java.util.Locale
import android.net.Uri
import android.provider.Settings
import com.seeit.seeit.R


class ScanActivity : AppCompatActivity() {


    private var tts: TextToSpeech? = null
    private var IsInitialVoiceFinshed = false
    private val numberOfClicks = 0


    private val textToSpeech: TextToSpeech? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var objectDetector: ObjectDetector
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>


    private val executor = java.util.concurrent.Executors.newSingleThreadExecutor()



    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)


        ensureCameraPermissionAndStart()

        val localModel = LocalModel.Builder()
            .setAssetFilePath("object_detection.tflite")
            .build()

        val customObjectDetectorOptions =
            CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                .enableClassification()
                .setClassificationConfidenceThreshold(0.5f)
                .setMaxPerObjectLabelCount(3)
                .build()

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts!!.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported")
                }
                IsInitialVoiceFinshed = true
            }
        }

        objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)
    }

    private fun startCamera() {
        cameraProviderFuture.addListener({
            // get() is used to get the instance of the future.

            val cameraProvider = cameraProviderFuture.get()
            bindpreview(cameraProvider = cameraProvider)

            // Here, we will bind the preview
        }, ContextCompat.getMainExecutor(this))
    }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCamera()
            } else {
                // If user checked “Don’t ask again”, show Settings
                val permanentlyDenied =
                    !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                if (permanentlyDenied) {
                    AlertDialog.Builder(this)
                        .setTitle("Camera permission needed")
                        .setMessage("Please enable Camera permission in Settings to continue.")
                        .setPositiveButton("Open Settings") { _, _ ->
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", packageName, null)
                            )
                            startActivity(intent)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                } else {
                    // User just denied. You could show a rationale or retry later.
                }
            }
        }

    private fun ensureCameraPermissionAndStart() {
        val hasPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            startCamera()
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            AlertDialog.Builder(this)
                .setTitle("Camera permission")
                .setMessage("We need access to your camera to capture images.")
                .setPositiveButton("Allow") { _, _ ->
                    requestCameraPermission.launch(Manifest.permission.CAMERA)
                }
                .setNegativeButton("Not now", null)
                .show()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    @SuppressLint("UnsafeOptInUsageError", "SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun bindpreview(cameraProvider: ProcessCameraProvider)
    {
        val preview : androidx.camera.core.Preview = androidx.camera.core.Preview.Builder().build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)

        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val point = Point()
        val size = display?.getRealSize(point)
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(point.x, point.y))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(executor) { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val image = imageProxy.image
            if (image != null) {

                val inputImage = InputImage.fromMediaImage(image, rotationDegrees)
                objectDetector
                    .process(inputImage)
                    .addOnFailureListener {
                        imageProxy.close()
                    }.addOnSuccessListener { objects ->

                        for(i in objects){
                            val item = i.labels.firstOrNull();
                            item?.let {
                                Log.d("scan", item.text+" "+item.confidence)
                                if(binding.layout.childCount > 1)
                                    binding.layout.removeViewAt(1)
                                val element = ItemView(conext = this,
                                    rect = i.boundingBox,
                                    text = item.text )

                                i.labels.firstOrNull()?.text?.let { outputdata(it) }

                                binding.layout.addView(element,1)
                            }
                           // i.labels.firstOrNull().confidence

                        }
                        imageProxy.close()
                    }.addOnFailureListener{
                        Log.v("MainActivity","Error - ${it.message}")
                        imageProxy.close()
                    }
            }
        }
        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageAnalysis, preview)
    }

    private fun outputdata(data: String) {
        if (!OutputGate.shouldEmit(data))
            return
        Toast.makeText(applicationContext,data.toString(), Toast.LENGTH_LONG).show()
        speak("Object is $data")
    }

    private fun speak(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        executor.shutdown()

    }


}