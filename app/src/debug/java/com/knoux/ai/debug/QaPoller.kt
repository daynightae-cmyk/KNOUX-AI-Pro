package com.knoux.ai.debug

import com.knoux.ai.ui.debug.QaCommandBus
import java.net.HttpURLConnection
import java.net.URL

/**
 * Debug-only loopback poller for visual QA.
 *
 * The dev server exposes /qa/next. Enqueue routes there and the screen is
 * navigated automatically; the existing PixelCopy uploader then posts a real
 * on-device screenshot. No adb connection is required.
 */
object QaPoller {
    @Volatile
    private var started = false

    fun start() {
        if (started) return
        started = true
        Thread({ loop() }, "knoux-qa-poller").apply { isDaemon = true }.start()
    }

    private fun loop() {
        while (true) {
            val next = runCatching { fetchNext() }.getOrNull()
            if (next != null) {
                QaCommandBus.post(next)
                // Leave enough time for the splash hand-off, route crossfade and
                // the 900 ms screenshot trigger before asking for the next route.
                Thread.sleep(2100)
            } else {
                Thread.sleep(1200)
            }
        }
    }

    private fun fetchNext(): String? {
        val conn = (URL("http://127.0.0.1:8099/qa/next").openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 1500
            readTimeout = 1500
        }
        return try {
            when (conn.responseCode) {
                200 -> conn.inputStream.bufferedReader().use { it.readText() }.trim().ifBlank { null }
                else -> null
            }
        } finally {
            conn.disconnect()
        }
    }
}
