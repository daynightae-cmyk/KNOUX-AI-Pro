package com.knoux.ai.auth

import android.content.Intent
import java.util.concurrent.ConcurrentHashMap

/**
 * Routes a browser OAuth redirect back to the provider instance that started
 * the flow. The redirect activity is launched by the browser in a new task, so
 * it cannot use startActivityForResult; the `state` parameter is the only
 * value that survives that boundary.
 *
 * A handler is single-use and keyed by the cryptographically random OAuth
 * state. If the process was killed while the browser was open the map is empty;
 * the redirect then finishes silently and the user retries — no token is ever
 * accepted without a matching state.
 */
object OAuthRedirectRelay {
    private val handlers = ConcurrentHashMap<String, (Intent) -> Unit>()

    fun register(state: String, handler: (Intent) -> Unit) {
        handlers[state] = handler
    }

    fun unregister(state: String) {
        handlers.remove(state)
    }

    /** @return true when a waiting flow consumed the redirect. */
    fun dispatch(intent: Intent?): Boolean {
        val data = intent?.data ?: return false
        val state = data.getQueryParameter("state") ?: return false
        val handler = handlers.remove(state) ?: return false
        handler(intent)
        return true
    }
}
