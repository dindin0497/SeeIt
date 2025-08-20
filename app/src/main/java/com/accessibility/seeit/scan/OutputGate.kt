package com.accessibility.seeit.scan

import android.os.SystemClock

object OutputGate {
    private const val THROTTLE_MS = 5000L
    private val lastByData = HashMap<String, Long>()

    fun shouldEmit(data: String): Boolean {
        val now = SystemClock.elapsedRealtime()
        val last = lastByData[data] ?: 0
        if (now - last < THROTTLE_MS)
            return false
        lastByData[data] = now
        return true
    }
}