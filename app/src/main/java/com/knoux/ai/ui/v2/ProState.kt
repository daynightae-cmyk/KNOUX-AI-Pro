package com.knoux.ai.ui.v2

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.knoux.ai.BuildConfig

private const val PRO_PREFS = "knoux_pro_v1"
private const val PREVIEW_KEY = "pro_visual_preview"

/** A debug-only visual preference for the Pro destination. */
object KnouxProState {
    var previewEnabled by mutableStateOf(false)
        private set

    fun load(context: Context) {
        previewEnabled = BuildConfig.DEBUG && prefs(context).getBoolean(PREVIEW_KEY, false)
    }

    fun setPreviewEnabled(context: Context, value: Boolean) {
        if (!BuildConfig.DEBUG) return
        previewEnabled = value
        prefs(context).edit().putBoolean(PREVIEW_KEY, value).apply()
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PRO_PREFS, Context.MODE_PRIVATE)
}

/** Reserved for a verified entitlement supplied by the host when billing is integrated. */
val LocalProActive = staticCompositionLocalOf { false }
