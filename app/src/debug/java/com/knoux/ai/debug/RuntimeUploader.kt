package com.knoux.ai.debug

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

/**
 * Captures the app's own window with PixelCopy and POSTs the PNG to the
 * loopback dev server that runs next to the project on the phone
 * (http://127.0.0.1:8099/upload). The result lands in qa-evidence/runtime/ and
 * is real on-device evidence of what the UI renders.
 */
object RuntimeUploader {
    private val executor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "knoux-capture").apply { isDaemon = true }
    }

    private const val ENDPOINT = "http://127.0.0.1:8099/upload"

    fun captureAndUpload(activity: Activity, label: String) {
        val window = activity.window ?: return
        val decor = window.decorView ?: return
        val width = decor.width
        val height = decor.height
        if (width <= 0 || height <= 0) return

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val rect = Rect(0, 0, width, height)
        PixelCopy.request(
            window,
            rect,
            bitmap,
            { result ->
                if (result != PixelCopy.SUCCESS) return@request
                executor.execute { upload(bitmap, label) }
            },
            Handler(Looper.getMainLooper()),
        )
    }

    private fun upload(bitmap: Bitmap, label: String) {
        val safe = label.replace(Regex("[^A-Za-z0-9._-]"), "_").take(48).ifBlank { "shot" }
        val tmp = File.createTempFile("knoux_shot", ".png")
        try {
            FileOutputStream(tmp).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            val url = URL("$ENDPOINT?name=$safe&t=${System.currentTimeMillis()}")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.connectTimeout = 4000
            conn.readTimeout = 8000
            conn.setRequestProperty("Content-Type", "image/png")
            conn.setFixedLengthStreamingMode(tmp.length())
            tmp.inputStream().use { input ->
                conn.outputStream.use { output -> input.copyTo(output) }
            }
            val code = conn.responseCode
            conn.disconnect()
            android.util.Log.i("KnouxCapture", "upload $safe -> HTTP $code")
        } catch (t: Throwable) {
            android.util.Log.w("KnouxCapture", "upload $safe failed: ${t.message}")
        } finally {
            tmp.delete()
        }
    }
}
