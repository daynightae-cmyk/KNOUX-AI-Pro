package com.knoux.ai.startup

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.knoux.ai.BuildConfig
import com.knoux.ai.auth.KnouxAuthConfig
import com.knoux.ai.auth.KnouxAuthController
import com.knoux.ai.ui.v2.hasSeenKnouxOnboarding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Post-splash entry destination, resolved once while the approved splash artwork
 * and sound timeline run unchanged.
 *
 * Decision order:
 *   1. First launch (onboarding not seen) -> Onboarding, exactly once.
 *   2. Returning user with a valid LOCAL session -> Shell, no login flash.
 *   3. Otherwise -> Auth (manual sign-in).
 *
 * All three inputs (onboarding flag, encrypted local session) are read off
 * Dispatchers.IO so a slow storage read can never block the first frame. Cloud
 * verification (/user) is deliberately NOT part of the entry decision: a slow
 * or offline network must never freeze startup. It runs asynchronously after the
 * UI is interactive, and a definitively rejected session is handled from the
 * shell without composing a transient login screen.
 */
enum class StartDestination { Onboarding, Auth, Shell }

class StartupCoordinator(
    private val context: Context,
    private val scope: CoroutineScope,
) {
    val controller = KnouxAuthController(
        context,
        KnouxAuthConfig(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabasePublishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            googleWebClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID,
        ),
    )

    private val _destination = mutableStateOf<StartDestination?>(null)
    val destination: androidx.compose.runtime.State<StartDestination?> = _destination

    /** Run once at composition: resolve the destination entirely off-main I/O. */
    fun resolve() {
        scope.launch {
            val seen = try {
                withContext(Dispatchers.IO) { hasSeenKnouxOnboarding(context.applicationContext) }
            } catch (_: Exception) {
                false
            }
            val valid = try {
                withContext(Dispatchers.IO) { controller.localSessionValid() }
            } catch (_: Exception) {
                false
            }
            _destination.value = when {
                !seen -> StartDestination.Onboarding
                valid -> StartDestination.Shell
                else -> StartDestination.Auth
            }
        }
    }

    @Composable
    fun remember(context: Context, scope: CoroutineScope): StartupCoordinator {
        return remember(context, scope) {
            StartupCoordinator(context.applicationContext, scope).also { it.resolve() }
        }
    }
}
