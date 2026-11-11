package com.knoux.ai.debug

import android.app.Application
import com.knoux.ai.ui.debug.RuntimeProbe

/**
 * Debug-variant entry point. Installs the runtime screenshot uploader so the
 * agent can verify what the app actually renders on the phone (no adb, no
 * screen-capture API in the proot environment).
 *
 * Release builds never contain this class: the debug source set is not part of
 * the release variant and the manifest entry lives in src/debug.
 */
class KnouxDebugApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RuntimeProbe.hook = { activity, label -> RuntimeUploader.captureAndUpload(activity, label) }
    }
}
