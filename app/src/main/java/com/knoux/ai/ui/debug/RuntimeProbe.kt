package com.knoux.ai.ui.debug

import android.app.Activity

/**
 * Debug-only hook.
 *
 * The main source set must never reference code that only exists in the debug
 * source set, so the capture trigger lives here as a no-op and the debug
 * variant installs a real implementation at process start.
 */
object RuntimeProbe {
    @Volatile
    var hook: ((Activity, String) -> Unit)? = null

    fun capture(activity: Activity?, label: String) {
        val h = hook ?: return
        if (activity == null) return
        h(activity, label)
    }
}
