package com.knoux.ai.cloud

import com.knoux.ai.developer.provider.OllamaEndpointPolicy
import com.knoux.ai.developer.vault.CredentialMask
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * JVM contract tests for the Developer Studio security layer (P6):
 * credential masking, Ollama endpoint policy (no cleartext to remote hosts),
 * and source contracts pinning that provider adapters never leak API keys or
 * raw provider error bodies, and that the vault never logs plaintext.
 */
class DeveloperStudioContractTest {

    // ------------------------------------------------------- credential mask

    @Test
    fun shortKeysAreFullyHidden() {
        assertEquals("****", CredentialMask.mask(""))
        assertEquals("****", CredentialMask.mask("abc"))
        assertEquals("****", CredentialMask.mask("12345678")) // exactly 8
    }

    @Test
    fun longKeysShowOnlyFirst4AndLast4() {
        val key = "sk-proj-abcdefgh1234567890"
        val masked = CredentialMask.mask(key)
        assertEquals("sk-p****7890", masked)
        assertFalse("mask must never contain the full key", masked.contains(key))
        assertFalse(masked.contains("abcdefgh"))
    }

    // ---------------------------------------------------- ollama endpoint policy

    @Test
    fun httpsIsAllowedForAnyHost() {
        assertEquals("https://ollama.example.com:11434",
            OllamaEndpointPolicy.validateBaseUrl("https://ollama.example.com:11434/"))
        assertEquals("https://192.168.1.50:11434",
            OllamaEndpointPolicy.validateBaseUrl("https://192.168.1.50:11434"))
    }

    @Test
    fun cleartextHttpIsAllowedOnlyOnLoopback() {
        listOf(
            "http://localhost:11434",
            "http://127.0.0.1:11434",
            "http://127.0.0.9:11434",
            "http://10.0.2.2:11434", // Android emulator -> host loopback
            "http://[::1]:11434",
        ).forEach { url ->
            assertEquals(url.trimEnd('/'), OllamaEndpointPolicy.validateBaseUrl(url))
        }
    }

    @Test
    fun cleartextHttpToRemoteHostIsRejected() {
        listOf(
            "http://ollama.example.com:11434",
            "http://192.168.1.50:11434",
            "http://10.0.2.3:11434",
        ).forEach { url ->
            assertThrows("must reject cleartext remote: $url", IllegalArgumentException::class.java) {
                OllamaEndpointPolicy.validateBaseUrl(url)
            }
        }
    }

    @Test
    fun malformedUrlsAreRejected() {
        listOf("", "   ", "ollama.local:11434", "ftp://localhost:11434", "http://").forEach { url ->
            assertThrows("must reject: '$url'", IllegalArgumentException::class.java) {
                OllamaEndpointPolicy.validateBaseUrl(url)
            }
        }
    }

    // ---------------------------------------------------------- source contracts

    private fun contractFile(relativePath: String): String {
        val wd = java.io.File(System.getProperty("user.dir")).absoluteFile
        return listOf(wd, wd.parentFile)
            .map { java.io.File(it, "app/$relativePath").absoluteFile }
            .firstOrNull { it.isFile }
            ?.readText()
            ?: error("Cannot locate app contract: $relativePath from $wd")
    }

    private val adapterFiles = listOf(
        "src/main/java/com/knoux/ai/developer/provider/OpenRouterAdapter.kt",
        "src/main/java/com/knoux/ai/developer/provider/OpenAIAdapter.kt",
        "src/main/java/com/knoux/ai/developer/provider/NvidiaAdapter.kt",
        "src/main/java/com/knoux/ai/developer/provider/GeminiAdapter.kt",
        "src/main/java/com/knoux/ai/developer/provider/OllamaAdapter.kt",
    )

    @Test
    fun adaptersNeverSendKeysInUrlsOrEchoProviderBodies() {
        adapterFiles.forEach { path ->
            val text = contractFile(path)
            assertFalse("$path must not put the key in the URL", text.contains("?key=\$apiKey") || text.contains("key=\$apiKey"))
            assertFalse("$path must not echo raw provider error bodies", text.contains("\"HTTP \$status: \$payload\""))
            assertFalse("$path must not set auth headers on raw connections", text.contains("setRequestProperty(\"Authorization\""))
            assertTrue("$path must go through CloudHttp", text.contains("CloudHttp.getJson"))
            assertTrue("$path must map CloudApiException by status", text.contains("throwable is CloudApiException"))
        }
    }

    @Test
    fun geminiKeyGoesInHeaderNotUrl() {
        val text = contractFile("src/main/java/com/knoux/ai/developer/provider/GeminiAdapter.kt")
        assertTrue(text.contains("x-goog-api-key"))
        assertFalse(text.contains("?key="))
    }

    @Test
    fun cloudHttpIsPublicAndHardensConnections() {
        val text = contractFile("src/main/java/com/knoux/ai/cloud/CloudHttp.kt")
        assertTrue("CloudHttp must be usable by developer adapters", text.contains("object CloudHttp") && !text.contains("internal object CloudHttp"))
        assertTrue("redirects must never carry credentials", text.contains("instanceFollowRedirects = false"))
        assertTrue("empty credential must send no auth header", text.contains("credential.isNotBlank()"))
        assertTrue("error bodies must be redacted before throwing", text.contains("sanitizeForLog"))
    }

    @Test
    fun connectorIsTrashedUsesCloudHttpAndKeeps404Semantics() {
        listOf(
            "src/main/java/com/knoux/ai/google/drive/GoogleDriveConnector.kt",
            "src/main/java/com/knoux/ai/google/gmail/GmailConnector.kt",
        ).forEach { path ->
            val text = contractFile(path)
            val section = text.substringAfter("suspend fun isTrashed")
            assertTrue("$path isTrashed must use CloudHttp", section.contains("CloudHttp.getJson"))
            assertTrue("$path must keep 404 = gone semantics", section.contains("e.status == 404"))
            assertFalse("$path isTrashed must not use raw connections", section.contains("openConnection()"))
        }
    }

    @Test
    fun vaultNeverLogsPlaintextAndCleansUnreadableEntries() {
        val text = contractFile("src/main/java/com/knoux/ai/developer/vault/CredentialVault.kt")
        assertTrue(text.contains("fun safeLogKey(providerId: String): String = \"***REDACTED***\""))
        assertTrue("masking must delegate to the pure CredentialMask", text.contains("CredentialMask.mask(apiKey)"))
        assertTrue("unreadable ciphertext must be removed", text.contains("remove(\"\${ENC_KEY_PREFIX}\$providerId\")"))
        assertTrue("keystore key must require randomized encryption (fresh IV per encryption)", text.contains("setRandomizedEncryptionRequired(true)"))
        assertTrue(text.contains("AES/GCM/NoPadding"))
    }
}
