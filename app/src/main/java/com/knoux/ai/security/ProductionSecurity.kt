package com.knoux.ai.security

import java.net.URI
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.Locale
import javax.net.ssl.HttpsURLConnection
import kotlinx.coroutines.delay

/**
 * Production security utilities for KNOUX AI Pro.
 *
 * TLS policy
 * ----------
 * This object deliberately does **not** install a global [javax.net.ssl.SSLSocketFactory]
 * or [javax.net.ssl.HostnameVerifier]. Replacing the process-wide defaults disables
 * platform certificate-chain validation for every library in the app (including
 * Google, Microsoft, OpenAI, NVIDIA, OpenRouter and the Android media stack) and is
 * not an acceptable pinning mechanism.
 *
 * Connections therefore use the Android platform defaults:
 *  - full RFC 5280 chain building and revocation-aware platform trust anchors,
 *  - hostname verification against the leaf certificate SANs,
 *  - TLS 1.2/1.3 negotiated by the platform provider.
 *
 * Optional certificate pinning, when a provider publishes verifiable SPKI pins, is
 * performed per host by [HostPinPolicy]. Pins are never fabricated: an empty pin set
 * means "platform validation only". The policy supports backup pins so a planned
 * certificate rotation does not brick the app, and it rejects pins for hosts other
 * than the one they were issued for.
 *
 * Other responsibilities: redacted logging, URL/path validation, idempotency keys,
 * bounded exponential backoff, and a retry contract that never replays a
 * non-idempotent destructive request.
 */
object ProductionSecurity {

    private const val TAG = "KnouxSecurity"
    private const val MAX_RETRIES = 3
    private const val BASE_BACKOFF_MS = 1_000L
    private const val MAX_BACKOFF_MS = 30_000L

    /**
     * The single Supabase project host this build talks to. Kept in sync with
     * `BuildConfig.SUPABASE_URL`; checked by `ProductionSecurityTest`.
     */
    const val SUPABASE_HOST = "tduofkizwkjtqcbklfah.supabase.co"

    private val random = SecureRandom()

    /**
     * Retained for source compatibility with earlier startup code.
     *
     * Intentionally performs no global TLS mutation. It exists so a stale call site
     * cannot silently reintroduce the old trust-all/pin-by-SubjectDN behaviour.
     * Prefer deleting call sites; this method only asserts the platform defaults are
     * still in place.
     */
    @Deprecated("Platform TLS defaults are used; no initialization is required.")
    fun initializeSslPinning() {
        // No-op by design. Do NOT call HttpsURLConnection.setDefaultSSLSocketFactory
        // or setDefaultHostnameVerifier here.
        if (HttpsURLConnection.getDefaultHostnameVerifier() == null) {
            // Defensive: a null verifier would mean someone else broke the defaults.
            logSafe(android.util.Log.ERROR, TAG, "Platform hostname verifier is missing")
        }
    }

    /**
     * Log a message safely: tokens, keys, emails, and PII are redacted.
     * Use this instead of Log.d/e/w/i for any network/auth related logging.
     */
    fun logSafe(level: Int, tag: String, message: String) {
        val redacted = redact(message)
        when (level) {
            android.util.Log.VERBOSE -> android.util.Log.v(tag, redacted)
            android.util.Log.DEBUG -> android.util.Log.d(tag, redacted)
            android.util.Log.INFO -> android.util.Log.i(tag, redacted)
            android.util.Log.WARN -> android.util.Log.w(tag, redacted)
            android.util.Log.ERROR -> android.util.Log.e(tag, redacted)
            else -> android.util.Log.d(tag, redacted)
        }
    }

    private val jwtPattern = Regex("eyJ[A-Za-z0-9_-]{5,}\\.[A-Za-z0-9_-]{5,}\\.[A-Za-z0-9_-]{5,}")
    private val googleAccessToken = Regex("ya29\\.[A-Za-z0-9_-]+")
    private val googleRefreshToken = Regex("1//[A-Za-z0-9_-]{10,}")
    private val microsoftToken = Regex("Ew[Bw][A-Za-z0-9_-]{20,}")
    private val openAiKey = Regex("sk-[A-Za-z0-9_-]{16,}")
    private val bearerToken = Regex("(?i)Bearer\\s+[^\\s,;\"']+")
    private val authorizationHeader = Regex("(?i)(Authorization\\s*[:=]\\s*)[^\\r\\n]+")
    private val email = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
    private val publishableKey = Regex("sb_publishable_[A-Za-z0-9_-]+")
    private val secretKey = Regex("sb_secret_[A-Za-z0-9_-]+")
    private val genericApiKey = Regex("(?i)(api[_-]?key|apikey)(\\s*[=:]\\s*)[A-Za-z0-9_\\-]{8,}")
    private val password = Regex("(?i)(password|passwd|pwd)(\\s*[=:]\\s*)\\S+")
    private val refreshToken = Regex("(?i)(refresh_token)(\\s*[=:]\\s*)[A-Za-z0-9._\\-]+")
    private val accessToken = Regex("(?i)(access_token|id_token)(\\s*[=:]\\s*)[A-Za-z0-9._\\-]+")
    private val oauthCode = Regex("(?i)([?&]code=)[^&\\s]+")

    /**
     * Redact sensitive values from a string before logging.
     * Order matters: structured headers and token shapes first, then generic values.
     */
    fun redact(input: String?): String {
        if (input.isNullOrBlank()) return "[empty]"
        var result = input
        result = result.replace(jwtPattern, "[JWT_REDACTED]")
        result = result.replace(bearerToken, "Bearer [REDACTED]")
        result = result.replace(authorizationHeader, "$1[REDACTED]")
        result = result.replace(googleAccessToken, "[GOOGLE_TOKEN_REDACTED]")
        result = result.replace(googleRefreshToken, "[GOOGLE_REFRESH_REDACTED]")
        result = result.replace(microsoftToken, "[MICROSOFT_TOKEN_REDACTED]")
        result = result.replace(openAiKey, "[OPENAI_KEY_REDACTED]")
        result = result.replace(secretKey, "sb_secret_[REDACTED]")
        result = result.replace(publishableKey, "sb_publishable_[REDACTED]")
        result = result.replace(genericApiKey, "$1$2[REDACTED]")
        result = result.replace(password, "$1$2[REDACTED]")
        result = result.replace(refreshToken, "$1$2[REDACTED]")
        result = result.replace(accessToken, "$1$2[REDACTED]")
        result = result.replace(oauthCode, "$1[REDACTED]")
        result = result.replace(email, "[EMAIL_REDACTED]")
        return result
    }

    /**
     * Validate a Supabase URL: HTTPS, the exact project host, no user-info and no
     * non-default port. Wildcard `*.supabase.co` is intentionally rejected: any
     * Supabase tenant must not be able to impersonate this project.
     */
    fun validateSupabaseUrl(url: String): Boolean {
        return try {
            val uri = URI(url)
            uri.scheme?.equals("https", ignoreCase = true) == true &&
                uri.host?.lowercase(Locale.ROOT) == SUPABASE_HOST &&
                uri.userInfo == null &&
                (uri.port == -1 || uri.port == 443) &&
                uri.fragment == null
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Validate a publishable key: must start with sb_publishable_ and must not be a
     * secret/service_role key.
     */
    fun validatePublishableKey(key: String): Boolean {
        return key.startsWith("sb_publishable_") &&
            !key.contains("service_role", ignoreCase = true) &&
            !key.startsWith("sb_secret_") &&
            key.length > 20
    }

    /**
     * Calculate exponential backoff delay with jitter for retry attempts.
     * retryCount starts at 0 for the first retry. The result is always in
     * [BASE_BACKOFF_MS, MAX_BACKOFF_MS] and is never negative.
     */
    fun calculateBackoffMs(retryCount: Int): Long {
        if (retryCount < 0) return BASE_BACKOFF_MS
        val exponential = BASE_BACKOFF_MS * (1L shl retryCount.coerceIn(0, 5))
        val capped = exponential.coerceAtMost(MAX_BACKOFF_MS)
        val jitterBound = (capped / 4).coerceAtLeast(1)
        val jitter = random.nextLong(jitterBound)
        return (capped + jitter).coerceAtMost(MAX_BACKOFF_MS)
    }

    /**
     * Validate a request path: must start with '/', not contain '..', not be a
     * protocol-relative double slash. Prevents path traversal and host confusion.
     */
    fun validateRequestPath(path: String): Boolean {
        return path.startsWith("/") &&
            !path.startsWith("//") &&
            !path.contains("..") &&
            !path.contains('\\') &&
            path.length <= 2048
    }

    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /** Generate a unique idempotency key for safe retries of network operations. */
    fun generateIdempotencyKey(): String {
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * True only for the exact configured Supabase project host (HTTPS).
     * Never accepts arbitrary `*.supabase.co` tenants.
     */
    fun isTrustedHost(url: String): Boolean = validateSupabaseUrl(url)

    /**
     * Sanitize a user-provided string for safe logging/display.
     */
    fun sanitizeForLog(input: String?, maxLength: Int = 200): String {
        if (input.isNullOrBlank()) return "[empty]"
        var result = input.replace(Regex("[\\p{Cntrl}&&[^\r\n\t]]"), "")
        if (result.length > maxLength) {
            result = result.substring(0, maxLength) + "...[truncated]"
        }
        return redact(result)
    }

    fun getMaxRetries(): Int = MAX_RETRIES

    /**
     * Decide whether an HTTP status may be retried.
     * 4xx responses other than 408/425/429 are permanent: retrying them can duplicate
     * side effects and will never succeed.
     */
    fun isRetryableStatus(status: Int): Boolean = when (status) {
        408, 425, 429 -> true
        in 500..599 -> true
        else -> false
    }

    /**
     * Decide whether a network exception is transient. Authentication, parsing and
     * certificate failures are permanent.
     */
    fun isRetryableException(throwable: Throwable): Boolean {
        if (throwable is javax.net.ssl.SSLException) return false
        if (throwable is java.io.FileNotFoundException) return false
        if (throwable is java.net.MalformedURLException) return false
        return throwable is java.io.IOException
    }

    /**
     * A redirect is followed only when it stays on HTTPS and on the same host.
     * This prevents an attacker-controlled redirect from receiving the
     * `Authorization` header or downgrading the connection to cleartext.
     */
    fun isSafeRedirect(originalUrl: String, location: String): Boolean {
        return try {
            val original = URI(originalUrl)
            val target = URI(location)
            val originalHost = original.host?.lowercase(Locale.ROOT) ?: return false
            val targetHost = target.host?.lowercase(Locale.ROOT) ?: return false
            target.scheme?.equals("https", ignoreCase = true) == true &&
                originalHost == targetHost &&
                target.userInfo == null
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Strip credentials from a header map when a redirect leaves the original host.
     * Used by the HTTP helpers; keeps provider credentials isolated per host.
     */
    fun headersForRedirect(
        originalUrl: String,
        location: String,
        headers: Map<String, String>,
    ): Map<String, String> {
        if (isSafeRedirect(originalUrl, location)) return headers
        return headers.filterKeys { key ->
            !key.equals("Authorization", ignoreCase = true) &&
                !key.equals("Cookie", ignoreCase = true) &&
                !key.equals("Proxy-Authorization", ignoreCase = true)
        }
    }
}

/**
 * Host-scoped SPKI certificate pin policy.
 *
 * Usage is opt-in per provider. The Android platform still validates the full chain
 * and hostname first; this policy is an additional check on the verified leaf SPKI.
 * Pins must be obtained from the live certificate chain (for example with
 * `openssl s_client -connect host:443 | openssl x509 -pubkey -noout` then SHA-256 of
 * the DER SubjectPublicKeyInfo), never invented.
 *
 * Rotation: keep the next certificate's pin in [backupPins] before rotating. A chain
 * is accepted when the leaf SPKI matches a primary or backup pin. Unknown pins are
 * rejected.
 */
class HostPinPolicy(
    private val host: String,
    primaryPins: Set<String>,
    backupPins: Set<String> = emptySet(),
) {
    private val normalizedHost = host.lowercase(Locale.ROOT)
    private val acceptedPins: Set<String> = (primaryPins + backupPins)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .toSet()

    /** True when this policy has at least one real pin to enforce. */
    val isEnforcing: Boolean get() = acceptedPins.isNotEmpty()

    fun verify(requestHost: String, leafSpkiSha256Base64: String): Boolean {
        if (requestHost.lowercase(Locale.ROOT) != normalizedHost) return false
        if (acceptedPins.isEmpty()) return true // platform validation only
        return leafSpkiSha256Base64 in acceptedPins
    }

    companion object {
        /** SHA-256 of the DER SubjectPublicKeyInfo, Base64 (no wrapping). */
        fun spkiSha256Base64(spkiDer: ByteArray): String {
            val digest = MessageDigest.getInstance("SHA-256").digest(spkiDer)
            return Base64.getEncoder().encodeToString(digest)
        }
    }
}

/**
 * Wraps a network request with bounded retry logic.
 *
 * Contract:
 *  - retries happen only for transient failures ([ProductionSecurity.isRetryableException]
 *    or a retryable HTTP status surfaced by the block),
 *  - a request is retried only when it is idempotent or carries a durable
 *    idempotency key. Non-idempotent destructive operations (delete/trash/send)
 *    must supply a key that the server records; otherwise they are executed once.
 */
suspend fun <T> secureRequest(
    maxRetries: Int = ProductionSecurity.getMaxRetries(),
    idempotent: Boolean = false,
    idempotencyKey: String? = null,
    delayForTest: (suspend (Long) -> Unit)? = null,
    block: suspend (attempt: Int) -> T,
): T {
    val mayRetry = idempotent || !idempotencyKey.isNullOrBlank()
    var lastException: Exception? = null
    val attempts = if (mayRetry) maxRetries + 1 else 1
    for (attempt in 0 until attempts) {
        try {
            return block(attempt)
        } catch (e: Exception) {
            lastException = e
            val retryable = ProductionSecurity.isRetryableException(e)
            if (!retryable || attempt == attempts - 1) throw e
            val waitMs = ProductionSecurity.calculateBackoffMs(attempt)
            if (delayForTest != null) delayForTest(waitMs) else delay(waitMs)
        }
    }
    throw lastException ?: Exception("Request failed after $maxRetries retries")
}
