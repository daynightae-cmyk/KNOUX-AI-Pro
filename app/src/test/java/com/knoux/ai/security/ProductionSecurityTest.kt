package com.knoux.ai.security

import java.io.IOException
import java.net.SocketTimeoutException
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLHandshakeException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class ProductionSecurityTest {

    // ---------------------------------------------------------------- TLS policy

    @Suppress("DEPRECATION")
    @Test
    fun `initialization never replaces process-wide TLS defaults`() {
        val factoryBefore = HttpsURLConnection.getDefaultSSLSocketFactory()
        val verifierBefore = HttpsURLConnection.getDefaultHostnameVerifier()
        assertNotNull(factoryBefore)
        assertNotNull(verifierBefore)

        ProductionSecurity.initializeSslPinning()

        assertSame(
            "Global SSLSocketFactory must not be replaced",
            factoryBefore,
            HttpsURLConnection.getDefaultSSLSocketFactory(),
        )
        assertSame(
            "Global HostnameVerifier must not be replaced",
            verifierBefore,
            HttpsURLConnection.getDefaultHostnameVerifier(),
        )
    }

    // ------------------------------------------------------------- URL validation

    @Test
    fun `only the exact supabase project host is trusted`() {
        assertTrue(ProductionSecurity.validateSupabaseUrl("https://tduofkizwkjtqcbklfah.supabase.co/auth/v1/token"))
        assertTrue(ProductionSecurity.validateSupabaseUrl("https://TDUOFKIZWKJTQCBKLFAH.SUPABASE.CO/rest/v1/"))
        assertTrue(ProductionSecurity.validateSupabaseUrl("https://tduofkizwkjtqcbklfah.supabase.co:443/"))
    }

    @Test
    fun `other supabase tenants and lookalike hosts are rejected`() {
        assertFalse(ProductionSecurity.validateSupabaseUrl("https://attacker.supabase.co/auth/v1/token"))
        assertFalse(ProductionSecurity.validateSupabaseUrl("https://supabase.co/auth"))
        assertFalse(ProductionSecurity.validateSupabaseUrl("https://tduofkizwkjtqcbklfah.supabase.co.evil.example/"))
        assertFalse(ProductionSecurity.validateSupabaseUrl("https://tduofkizwkjtqcbklfah.supabase.co@evil.example/"))
        assertFalse(ProductionSecurity.validateSupabaseUrl("https://tduofkizwkjtqcbklfah.supabase.co:8443/"))
        assertFalse(ProductionSecurity.validateSupabaseUrl("http://tduofkizwkjtqcbklfah.supabase.co/"))
        assertFalse(ProductionSecurity.validateSupabaseUrl("https://tduofkizwkjtqcbklfah.supabase.co/#fragment"))
        assertFalse(ProductionSecurity.isTrustedHost("https://attacker.supabase.co/"))
    }

    @Test
    fun `request paths reject traversal and protocol-relative forms`() {
        assertTrue(ProductionSecurity.validateRequestPath("/auth/v1/token?grant_type=password"))
        assertTrue(ProductionSecurity.validateRequestPath("/rest/v1/duplicate_groups"))
        assertFalse(ProductionSecurity.validateRequestPath("auth/v1/token"))
        assertFalse(ProductionSecurity.validateRequestPath("//evil.example/path"))
        assertFalse(ProductionSecurity.validateRequestPath("/../etc/passwd"))
        assertFalse(ProductionSecurity.validateRequestPath("/a/../../b"))
        assertFalse(ProductionSecurity.validateRequestPath("/a\\..\\b"))
        assertFalse(ProductionSecurity.validateRequestPath("/" + "a".repeat(3000)))
    }

    @Test
    fun `publishable key validation rejects secrets`() {
        assertTrue(ProductionSecurity.validatePublishableKey("sb_publishable_19JCfWlBOdUng5LHAoMfRg_hlFl7YnC"))
        assertFalse(ProductionSecurity.validatePublishableKey("sb_secret_19JCfWlBOdUng5LHAoMfRg_hlFl7YnC"))
        assertFalse(ProductionSecurity.validatePublishableKey("service_role_19JCfWlBOdUng5LHAoMfRg"))
        assertFalse(ProductionSecurity.validatePublishableKey("short"))
    }

    // ----------------------------------------------------------------- redaction

    @Test
    fun `redaction removes bearer and JWT tokens`() {
        val jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U"
        val text = "Authorization: Bearer $jwt and raw=$jwt"
        val redacted = ProductionSecurity.redact(text)
        assertFalse(redacted.contains(jwt))
        assertFalse(redacted.contains("Bearer eyJ"))
        assertTrue(redacted.contains("[REDACTED]") || redacted.contains("[JWT_REDACTED]"))
    }

    @Test
    fun `redaction removes provider specific token shapes`() {
        val googleAccess = "ya29.a0AfH6SMBexampleGoogleAccessToken"
        val googleRefresh = "1//0gExampleGoogleRefreshTokenValue"
        val microsoft = "EwBwA8l6BAAUexampleMicrosoftAccessTokenValue"
        val openAi = "sk-proj-abcdefghijklmnopqrstuvwxyz123456"
        val supabaseSecret = "sb_secret_abcdefghijklmnopqrstuvwxyz"
        val supabasePublishable = "sb_publishable_abcdefghijklmnopqrstuvwxyz"

        val text = listOf(googleAccess, googleRefresh, microsoft, openAi, supabaseSecret, supabasePublishable)
            .joinToString(" | ")
        val redacted = ProductionSecurity.redact(text)

        listOf(googleAccess, googleRefresh, microsoft, openAi, supabaseSecret, supabasePublishable)
            .forEach { secret -> assertFalse("Leaked: $secret", redacted.contains(secret)) }
    }

    @Test
    fun `redaction removes emails passwords and query codes`() {
        val text = "user@example.com password=hunter2 access_token=abc123def refresh_token=def456 " +
            "https://callback.example/cb?code=4/0AeanExample&state=ok api_key=ABCDEF1234567890"
        val redacted = ProductionSecurity.redact(text)
        assertFalse(redacted.contains("user@example.com"))
        assertFalse(redacted.contains("hunter2"))
        assertFalse(redacted.contains("abc123def"))
        assertFalse(redacted.contains("def456"))
        assertFalse(redacted.contains("4/0AeanExample"))
        assertFalse(redacted.contains("ABCDEF1234567890"))
        assertTrue(redacted.contains("state=ok"))
    }

    @Test
    fun `sanitizeForLog strips control characters and truncates`() {
        val value = "abc\u0000def\n" + "x".repeat(300)
        val sanitized = ProductionSecurity.sanitizeForLog(value, maxLength = 20)
        assertFalse(sanitized.contains("\u0000"))
        assertTrue(sanitized.contains("...[truncated]"))
    }

    // --------------------------------------------------------------- retry policy

    @Test
    fun `backoff is bounded and non-negative`() {
        for (attempt in 0..20) {
            val delay = ProductionSecurity.calculateBackoffMs(attempt)
            assertTrue("attempt=$attempt delay=$delay", delay in 1_000..30_000)
        }
        assertTrue(ProductionSecurity.calculateBackoffMs(-5) >= 1_000)
    }

    @Test
    fun `only transient statuses are retryable`() {
        assertTrue(ProductionSecurity.isRetryableStatus(408))
        assertTrue(ProductionSecurity.isRetryableStatus(425))
        assertTrue(ProductionSecurity.isRetryableStatus(429))
        assertTrue(ProductionSecurity.isRetryableStatus(500))
        assertTrue(ProductionSecurity.isRetryableStatus(503))
        assertFalse(ProductionSecurity.isRetryableStatus(400))
        assertFalse(ProductionSecurity.isRetryableStatus(401))
        assertFalse(ProductionSecurity.isRetryableStatus(403))
        assertFalse(ProductionSecurity.isRetryableStatus(404))
        assertFalse(ProductionSecurity.isRetryableStatus(409))
        assertFalse(ProductionSecurity.isRetryableStatus(422))
    }

    @Test
    fun `certificate and parsing failures are not retryable`() {
        assertFalse(ProductionSecurity.isRetryableException(SSLHandshakeException("bad cert")))
        assertFalse(ProductionSecurity.isRetryableException(java.io.FileNotFoundException("404")))
        assertFalse(ProductionSecurity.isRetryableException(java.net.MalformedURLException("bad")))
        assertFalse(ProductionSecurity.isRetryableException(IllegalArgumentException("bad json")))
        assertTrue(ProductionSecurity.isRetryableException(SocketTimeoutException("timeout")))
        assertTrue(ProductionSecurity.isRetryableException(IOException("connection reset")))
    }

    @Test
    fun `non idempotent request without key is executed exactly once`() = runBlocking {
        var attempts = 0
        try {
            secureRequest(maxRetries = 3, idempotent = false, delayForTest = {}) {
                attempts++
                throw IOException("transient")
            }
            fail("Expected exception")
        } catch (_: IOException) {
            // expected
        }
        assertEquals(1, attempts)
    }

    @Test
    fun `idempotent request retries transient failures`() = runBlocking {
        var attempts = 0
        try {
            secureRequest(maxRetries = 3, idempotent = true, delayForTest = {}) {
                attempts++
                throw IOException("transient")
            }
            fail("Expected exception")
        } catch (_: IOException) {
            // expected
        }
        assertEquals(4, attempts)
    }

    @Test
    fun `durable idempotency key allows retry of a mutating request`() = runBlocking {
        var attempts = 0
        val result = secureRequest(
            maxRetries = 3,
            idempotent = false,
            idempotencyKey = ProductionSecurity.generateIdempotencyKey(),
            delayForTest = {},
        ) {
            attempts++
            if (attempts < 3) throw IOException("transient") else "ok"
        }
        assertEquals("ok", result)
        assertEquals(3, attempts)
    }

    @Test
    fun `tls failures are never retried`() = runBlocking {
        var attempts = 0
        try {
            secureRequest(maxRetries = 3, idempotent = true, delayForTest = {}) {
                attempts++
                throw SSLHandshakeException("untrusted")
            }
            fail("Expected exception")
        } catch (_: SSLHandshakeException) {
            // expected
        }
        assertEquals(1, attempts)
    }

    // ------------------------------------------------------------------ redirects

    @Test
    fun `redirects must stay https and on the same host`() {
        assertTrue(ProductionSecurity.isSafeRedirect("https://api.example.com/a", "https://api.example.com/b"))
        assertFalse(ProductionSecurity.isSafeRedirect("https://api.example.com/a", "http://api.example.com/b"))
        assertFalse(ProductionSecurity.isSafeRedirect("https://api.example.com/a", "https://evil.example/b"))
        assertFalse(ProductionSecurity.isSafeRedirect("https://api.example.com/a", "https://user:pass@api.example.com/b"))
        assertFalse(ProductionSecurity.isSafeRedirect("https://api.example.com/a", "//evil.example/b"))
    }

    @Test
    fun `authorization header is stripped on cross host redirect`() {
        val headers = mapOf(
            "Authorization" to "Bearer secret",
            "Cookie" to "session=secret",
            "Accept" to "application/json",
        )
        val sameHost = ProductionSecurity.headersForRedirect(
            "https://api.example.com/a", "https://api.example.com/b", headers,
        )
        assertEquals(headers, sameHost)

        val crossHost = ProductionSecurity.headersForRedirect(
            "https://api.example.com/a", "https://evil.example/b", headers,
        )
        assertFalse(crossHost.containsKey("Authorization"))
        assertFalse(crossHost.containsKey("Cookie"))
        assertEquals("application/json", crossHost["Accept"])
    }

    // --------------------------------------------------------------- pin rotation

    @Test
    fun `pin policy accepts primary and backup pins only for its host`() {
        val current = "CURRENT_PIN_BASE64="
        val backup = "BACKUP_PIN_BASE64="
        val policy = HostPinPolicy(
            host = "api.example.com",
            primaryPins = setOf(current),
            backupPins = setOf(backup),
        )
        assertTrue(policy.isEnforcing)
        assertTrue(policy.verify("api.example.com", current))
        assertTrue(policy.verify("API.EXAMPLE.COM", backup))
        assertFalse(policy.verify("api.example.com", "UNKNOWN_PIN="))
        assertFalse(policy.verify("evil.example.com", current))
    }

    @Test
    fun `planned certificate rotation succeeds with backup pin then new primary`() {
        val oldPin = "OLD_PIN="
        val newPin = "NEW_PIN="
        // Before rotation: old is primary, new is the pre-published backup.
        val before = HostPinPolicy("api.example.com", setOf(oldPin), setOf(newPin))
        assertTrue(before.verify("api.example.com", oldPin))
        assertTrue(before.verify("api.example.com", newPin))
        // After rotation: new becomes primary, old is retained as backup.
        val after = HostPinPolicy("api.example.com", setOf(newPin), setOf(oldPin))
        assertTrue(after.verify("api.example.com", newPin))
        assertTrue(after.verify("api.example.com", oldPin))
        // An unrelated pin is never accepted.
        assertFalse(after.verify("api.example.com", "ROGUE_PIN="))
    }

    @Test
    fun `empty pin set means platform validation only`() {
        val policy = HostPinPolicy("api.example.com", emptySet())
        assertFalse(policy.isEnforcing)
        assertTrue(policy.verify("api.example.com", "ANYTHING="))
        assertFalse(policy.verify("other.example.com", "ANYTHING="))
    }

    @Test
    fun `spki hash matches known sha256 vector`() {
        assertEquals(
            "ungWv48Bz+pBQUDeXa4iI7ADYaOWF3qctBD/YfIAFa0=",
            HostPinPolicy.spkiSha256Base64("abc".toByteArray(Charsets.UTF_8)),
        )
    }
}
