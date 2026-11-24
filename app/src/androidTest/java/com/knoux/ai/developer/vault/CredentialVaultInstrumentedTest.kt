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
 * AES-256-GCM round-trips, a fresh Keystore-generated nonce per encryption and
 * persistence across vault instances.
 *
 * Safety contract: the tests run only against `com.knoux.ai.debug` and only
 * ever touch the fixture id [providerId]. They never call the production
 * `wipeAll()`: that method would erase real developer keys and Google/Microsoft
 * OAuth tokens on this debug install while account metadata still listed the
 * accounts as connected (they live in different preferences). A whole-vault
 * wipe is not "test cleanup".
 */
@RunWith(AndroidJUnit4::class)
class CredentialVaultInstrumentedTest {

    private lateinit var context: Context
    private lateinit var vault: CredentialVault

    private val providerId = "test-provider-instrumented"
    private val secret = "sk-test-abcdefghijklmnopqrstuvwxyz-0123456789"

    private fun fixture(): ProviderConfig = ProviderConfig(
        providerId = providerId,
        keystoreAlias = "knoux_key_$providerId",
        maskedKey = CredentialMask.mask(secret),
        isConfigured = true,
        isEnabled = true,
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Never run destructive vault checks against the production install.
        assertEquals("com.knoux.ai.debug", context.packageName)
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
        vault.storeCredential(fixture(), secret)

        assertTrue(vault.hasCredential(providerId))

        val reopened = CredentialVault(context)
        assertTrue(reopened.hasCredential(providerId))
        assertEquals(secret, reopened.getCredential(providerId))
        assertTrue(reopened.isEnabled(providerId))
    }

    @Test
    fun maskedValue_neverContainsFullSecret() = runBlocking {
        vault.storeCredential(fixture(), secret)
        val masked = vault.getMaskedKey(providerId)
        assertNotEquals(secret, masked)
        assertFalse(masked.contains(secret))
        assertTrue(masked.contains("****"))
    }

    @Test
    fun removeCredential_erasesCiphertextAndMetadata() = runBlocking {
        vault.storeCredential(fixture(), secret)
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
}
