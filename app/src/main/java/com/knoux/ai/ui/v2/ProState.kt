package com.knoux.ai.ui.v2

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.knoux.ai.BuildConfig

private const val PRO_PREFS = "knoux_pro_v1"
private const val PRO_KEY = "pro_active"

/**
 * Local Pro switch used by the preview build. There is no store, purchase or
 * licence check yet: the flag only changes the in-app presentation so the Pro
 * identity can be reviewed on a real device.
 *
 * Debug/preview builds default to the golden identity so the owner sees the
 * actual product world immediately after installing. This is a build preview,
 * not an entitlement: the Pro screen says so explicitly and the switch stays
 * user-controlled. A release build defaults to the standard identity.
 */
object KnouxProState {
    var active by mutableStateOf(false)
        private set

    fun load(context: Context) {
        active = prefs(context).getBoolean(PRO_KEY, BuildConfig.DEBUG)
    }

    fun setActive(context: Context, value: Boolean) {
        active = value
        prefs(context).edit().putBoolean(PRO_KEY, value).apply()
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PRO_PREFS, Context.MODE_PRIVATE)
}

val LocalProActive = staticCompositionLocalOf { false }
