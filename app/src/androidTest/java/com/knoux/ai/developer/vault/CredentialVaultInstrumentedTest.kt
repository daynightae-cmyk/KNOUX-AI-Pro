package com.knoux.ai.developer.vault

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.knoux.ai.developer.model.ProviderConfig
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented proof that the credential vault uses the real Android Keystore:
 * AES-256-GCM round-trips, a fresh Keystore-generated nonce per encryption,
 * persistence across vault instances, and complete deletion.
 */
@RunWith(AndroidJUnit4::class)
class CredentialVaultInstrumentedTest {

    private lateinit var context: Context
    private lateinit var vault: CredentialVault

    private val providerId = "test-provider-instrumented"
    private val secret = "sk-test-abcdefghijklmnopqrstuvwxyz-0123456789"

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        vault = CredentialVault(context)
        runBlocking { vault.removeCredential(providerId) }
    }

    @After
    fun tearDown() {
        runBlocking { vault.removeCredential(providerId) }
    }

    @Test
    fun encryptDecrypt_roundTripsThroughKeystore() {
        val encrypted = vault.encrypt(secret)
        assertNotEquals(secret, encrypted)
        assertEquals(secret, vault.decrypt(encrypted))
    }

    @Test
    fun encrypt_usesFreshNonceForEveryCall() {
        val first = vault.encrypt(secret)
        val second = vault.encrypt(secret)
        assertNotEquals(
            "GCM nonce must never repeat for the same plaintext",
            first,
            second,
        )
        assertEquals(secret, vault.decrypt(first))
        assertEquals(secret, vault.decrypt(second))
    }

    @Test
    fun storeAndRead_persistsAcrossVaultInstances() = runBlocking {
        vault.storeCredential(
            ProviderConfig(
                providerId = providerId,
                keystoreAlias = "knoux_key_$providerId",
                maskedKey = vault.maskKey(secret),
                isConfigured = true,
                isEnabled = true,
            ),
            secret,
        )

        assertTrue(vault.hasCredential(providerId))

        val reopened = CredentialVault(context)
        assertTrue(reopened.hasCredential(providerId))
        assertEquals(secret, reopened.getCredential(providerId))
        assertTrue(reopened.isEnabled(providerId))
    }

    @Test
    fun maskedValue_neverContainsFullSecret() = runBlocking {
        vault.storeCredential(
            ProviderConfig(
                providerId = providerId,
                keystoreAlias = "knoux_key_$providerId",
                maskedKey = vault.maskKey(secret),
                isConfigured = true,
                isEnabled = true,
            ),
            secret,
        )
        val masked = vault.getMaskedKey(providerId)
        assertNotEquals(secret, masked)
        assertFalse(masked.contains(secret))
        assertTrue(masked.contains("****"))
    }

    @Test
    fun removeCredential_erasesCiphertextAndMetadata() = runBlocking {
        vault.storeCredential(
            ProviderConfig(
                providerId = providerId,
                keystoreAlias = "knoux_key_$providerId",
                maskedKey = vault.maskKey(secret),
                isConfigured = true,
                isEnabled = true,
            ),
            secret,
        )
        vault.removeCredential(providerId)

        assertFalse(vault.hasCredential(providerId))
        assertNull(vault.getCredential(providerId))
        assertEquals("****", vault.getMaskedKey(providerId))
        assertFalse(vault.isEnabled(providerId))
    }

    @Test
    fun corruptedCiphertext_returnsNullAndPurgesEntry() = runBlocking {
        context.getSharedPreferences("knoux_developer_vault", Context.MODE_PRIVATE)
            .edit()
            .putString("enc_$providerId", "not-a-valid-base64-blob")
            .putString("masked_$providerId", "****")
            .commit()

        assertNull(vault.getCredential(providerId))
        assertFalse(
            "Unreadable ciphertext must be deleted, not retried forever",
            vault.hasCredential(providerId),
        )
    }

    @Test
    fun wipeAll_removesEveryCredentialButKeepsVaultUsable() = runBlocking {
        vault.storeCredential(
            ProviderConfig(
                providerId = providerId,
                keystoreAlias = "knoux_key_$providerId",
                maskedKey = vault.maskKey(secret),
                isConfigured = true,
                isEnabled = true,
            ),
            secret,
        )
        vault.wipeAll()

        assertFalse(vault.hasCredential(providerId))
        assertNull(vault.getCredential(providerId))

        // The Keystore master key must still work after a logical wipe.
        val encrypted = vault.encrypt(secret)
        assertEquals(secret, vault.decrypt(encrypted))
    }
}
