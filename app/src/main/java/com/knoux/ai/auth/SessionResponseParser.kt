package com.knoux.ai.auth

import org.json.JSONObject

/**
 * Pure session-response policy shared by every sign-in path (password, OTP,
 * Google ID token, passkey, refresh). No Android dependencies: the exact
 * acceptance rules are executable in JVM contract tests.
 *
 * A session is accepted ONLY when the server response actually contains both
 * an access token and a refresh token. Signup-with-email-confirmation and
 * challenge-only responses are not sessions and must never be reported as
 * sign-in success.
 */
internal object SessionResponseParser {

    /** Milliseconds of head-room before the server expiry when we refresh early. */
    const val EXPIRY_SKEW_MS = 60_000L

    data class AcceptedSession(
        val accessToken: String,
        val refreshToken: String,
        val expiresAtMs: Long,
    )

    /**
     * Parses a GoTrue token response. [nowMs] is injected for deterministic
     * tests; production callers pass System.currentTimeMillis().
     * Returns null when the payload is not a complete session.
     */
    fun parse(payload: JSONObject, nowMs: Long): AcceptedSession? {
        val data = payload.optJSONObject("session") ?: payload
        val access = data.optString("access_token")
        val refresh = data.optString("refresh_token")
        if (access.isBlank() || refresh.isBlank()) return null
        val expiresIn = data.optLong("expires_in", 3600).coerceIn(1, 86_400)
        return AcceptedSession(access, refresh, nowMs + expiresIn * 1000)
    }

    /** Local-only validity check: token exists and is not within the skew window. */
    fun isLocallyValid(expiresAtMs: Long, nowMs: Long): Boolean =
        nowMs < expiresAtMs - EXPIRY_SKEW_MS
}
