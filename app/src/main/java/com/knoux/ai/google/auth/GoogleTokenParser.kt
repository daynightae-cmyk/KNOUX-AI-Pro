package com.knoux.ai.google.auth

import org.json.JSONObject

/**
 * Pure Google OAuth token-response parsing. No Android dependencies, so the
 * exact production rules are executable in JVM contract tests.
 * A response without an access_token is rejected, never a blank "success".
 */
internal object GoogleTokenParser {
    fun parse(json: String, nowMs: Long): GoogleAuthProvider.GoogleTokenResponse {
        val obj = JSONObject(json)
        val accessToken = obj.optString("access_token")
        require(accessToken.isNotBlank()) { "Token response contained no access_token" }
        return GoogleAuthProvider.GoogleTokenResponse(
            accessToken = accessToken,
            refreshToken = obj.optString("refresh_token").takeIf { it.isNotBlank() },
            expiresInSeconds = obj.optLong("expires_in", 3600L),
            tokenType = obj.optString("token_type", "Bearer"),
            scope = obj.optString("scope", ""),
            obtainedAtMs = nowMs,
        )
    }
}
