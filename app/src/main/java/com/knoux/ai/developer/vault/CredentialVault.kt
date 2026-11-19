package com.knoux.ai.developer.vault

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.knoux.ai.developer.model.ProviderConfig
import com.knoux.ai.developer.model.ProviderConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Secure API Key Vault.
 *
 * All credentials are encrypted with AES-256-GCM using a key protected by
 * Android Keystore. The raw key material never leaves the TEE/KEE. Regular
 * SharedPreferences store only the Base64-encoded ciphertext and a masked
 * display value.
 *
 * Biometric re-authentication is required before revealing or exporting a key.
 */
class CredentialVault(private val context: Context) {

    private companion object {
        const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_IV_LENGTH = 12
        const val GCM_TAG_BITS = 128
        const val PREFS_NAME = "knoux_developer_vault"
        const val ENC_KEY_PREFIX = "enc_"
        const val ENABLED_KEY_PREFIX = "enabled_"
    }

    private val masterKeyAlias: String
    private val vaultPrefs: android.content.SharedPreferences

    init {
        masterKeyAlias = generateOrGetMasterKey()
        vaultPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /** Create or retrieve the AES-256-GCM master key in Android Keystore. */
    private fun generateOrGetMasterKey(): String {
        val keyAlias = "knoux_developer_vault_master"
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)
        if (keyStore.containsAlias(keyAlias)) return keyAlias

        val keyGen = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER
        )
        keyGen.init(
            KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(false)
                .build()
        )
        keyGen.generateKey()
        return keyAlias
    }

    /** Encrypt a plaintext API key and return the Base64-encoded ciphertext. */
    fun encrypt(apiKey: String): String {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)
        val secretKey = keyStore.getKey(masterKeyAlias, null) as SecretKey

        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        val iv = ByteArray(GCM_IV_LENGTH)
        java.security.SecureRandom().nextBytes(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_BITS, iv))

        val ciphertext = cipher.doFinal(apiKey.toByteArray(charset = java.nio.charset.StandardCharsets.UTF_8))
        val combined = ByteArray(GCM_IV_LENGTH + ciphertext.size)
        System.arraycopy(iv, 0, combined, 0, GCM_IV_LENGTH)
        System.arraycopy(ciphertext, 0, combined, GCM_IV_LENGTH, ciphertext.size)

        return android.util.Base64.encodeToString(combined, android.util.Base64.NO_WRAP)
    }

    /** Decrypt a Base64-encoded ciphertext back to the plaintext API key. */
    fun decrypt(ciphertextB64: String): String {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)
        val secretKey = keyStore.getKey(masterKeyAlias, null) as SecretKey

        val combined = android.util.Base64.decode(ciphertextB64, android.util.Base64.NO_WRAP)
        val iv = ByteArray(GCM_IV_LENGTH)
        System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH)
        val ciphertext = ByteArray(combined.size - GCM_IV_LENGTH)
        System.arraycopy(combined, GCM_IV_LENGTH, ciphertext, 0, ciphertext.size)

        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_BITS, iv))

        val plaintext = cipher.doFinal(ciphertext)
        return String(plaintext, charset = java.nio.charset.StandardCharsets.UTF_8)
    }

    /** Mask a key for display: show only first 4 and last 4 chars. */
    fun maskKey(apiKey: String): String {
        if (apiKey.length <= 8) return "****"
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length - 4)
    }

    /** Store a provider credential (encrypted) and masked display value. */
    suspend fun storeCredential(config: ProviderConfig, plaintextKey: String) {
        withContext(Dispatchers.IO) {
            val encrypted = encrypt(plaintextKey)
            vaultPrefs.edit()
                .putString("${ENC_KEY_PREFIX}${config.providerId}", encrypted)
                .putBoolean("${ENABLED_KEY_PREFIX}${config.providerId}", config.isEnabled)
                .apply()
        }
    }

    /** Retrieve and decrypt a provider credential. Requires caller to have
     *  already performed biometric re-authentication. */
    suspend fun getCredential(providerId: String): String? {
        return withContext(Dispatchers.IO) {
            val encrypted = vaultPrefs.getString("${ENC_KEY_PREFIX}$providerId", null)
            if (encrypted.isNullOrBlank()) null else decrypt(encrypted)
        }
    }

    /** Get the masked key for display (no decryption needed). */
    fun getMaskedKey(config: ProviderConfig): String = config.maskedKey

    /** Check whether a credential exists for the provider. */
    fun hasCredential(providerId: String): Boolean =
        vaultPrefs.getString("${ENC_KEY_PREFIX}$providerId", null) != null

    /** Remove a provider credential. */
    suspend fun removeCredential(providerId: String) {
        withContext(Dispatchers.IO) {
            vaultPrefs.edit()
                .remove("${ENC_KEY_PREFIX}$providerId")
                .remove("${ENABLED_KEY_PREFIX}$providerId")
                .apply()
        }
    }

    /** Enable/disable a credential. */
    suspend fun setEnabled(providerId: String, enabled: Boolean) {
        withContext(Dispatchers.IO) {
            vaultPrefs.edit().putBoolean("${ENABLED_KEY_PREFIX}$providerId", enabled).apply()
        }
    }

    fun isEnabled(providerId: String): Boolean =
        vaultPrefs.getBoolean("${ENABLED_KEY_PREFIX}$providerId", false)

    /** Mask a key for logging/crash reports - never log real keys. */
    fun safeLogKey(providerId: String): String = "***REDACTED***"
}

/**
 * Biometric gate for credential viewing/exporting.
 * Returns true only after the user authenticates with biometrics.
 */
class BiometricGate private constructor(private val activity: androidx.fragment.app.FragmentActivity) {

    companion object {
        fun authenticate(
            activity: androidx.fragment.app.FragmentActivity,
            onResult: (Boolean) -> Unit
        ) {
            val executor = java.util.concurrent.Executor { command -> command.run() }
            val prompt = androidx.biometric.BiometricPrompt(
                activity,
                executor,
                object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(
                        result: androidx.biometric.BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        onResult(true)
                    }

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        onResult(false)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        onResult(false)
                    }
                }
            )

            val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                .setTitle("تأكيد الهوية")
                .setDescription("يُطلب المصادقة البيومترية لعرض المفتاح أو تصديره.")
                .setNegativeButtonText("إلغاء")
                .build()

            prompt.authenticate(promptInfo)
        }
    }
}