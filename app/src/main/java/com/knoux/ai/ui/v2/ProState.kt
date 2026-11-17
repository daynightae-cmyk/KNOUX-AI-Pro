package com.knoux.ai.ui.v2

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.knoux.ai.BuildConfig

private const val PRO_PREFS = "knoux_pro_v1"
private const val PREVIEW_KEY = "pro_visual_preview"

/**
 * Local Pro visual preview. There is no store, purchase or licence check yet:
 * the flag only changes the in-app presentation (golden theme + the red PRO
 * markers on the 50 paid services) so the Pro identity can be reviewed on a
 * real device.
 *
 * Debug/preview builds default to the golden identity so the owner sees the
 * actual product world immediately after installing. This is a build preview,
 * not an entitlement: the Pro screen says so explicitly and the switch stays
 * user-controlled. A release build defaults to the standard identity and the
 * preview cannot be enabled until billing supplies a verified entitlement.
 */
object KnouxProState {
    var previewEnabled by mutableStateOf(false)
        private set

    fun load(context: Context) {
        previewEnabled = BuildConfig.DEBUG && prefs(context).getBoolean(PREVIEW_KEY, true)
    }

    fun setPreviewEnabled(context: Context, value: Boolean) {
        if (!BuildConfig.DEBUG) return
        previewEnabled = value
        prefs(context).edit().putBoolean(PREVIEW_KEY, value).apply()
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PRO_PREFS, Context.MODE_PRIVATE)
}

/**
 * Drives the red PRO markers and Pro-gated UI. In debug/preview builds the host
 * provides the visual preview value so the owner can review the Pro world; in
 * release it stays false until a verified entitlement is supplied by billing.
 */
val LocalProActive = staticCompositionLocalOf { false }