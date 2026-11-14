package com.knoux.ai.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** Encrypted at rest using non-exportable Android Keystore key; biometric app lock is separate. */
internal data class KnouxTokens(
    val accessToken: String, val refreshToken: String, val expiresAtMs: Long
)

internal class KnouxSessionStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("knoux_auth_secure_v1", Context.MODE_PRIVATE)
    private val alias = "knoux_auth_session_aes_v1"
    private val key: SecretKey get() {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val existing = store.getKey(alias, null) as? SecretKey
        if (existing != null) return existing
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(KeyGenParameterSpec.Builder(alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true).build())
        return generator.generateKey()
    }
    @Synchronized fun save(t: KnouxTokens) {
        val serialized = JSONObject().put("access_token",t.accessToken)
            .put("refresh_token",t.refreshToken).put("expires_at_ms",t.expiresAtMs).toString()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE,key) }
        val encrypted = cipher.doFinal(serialized.toByteArray(Charsets.UTF_8))
        val payload = Base64.encodeToString(cipher.iv + encrypted,Base64.NO_WRAP)
        check(prefs.edit().putString("blob",payload).commit()) { "Session persistence failed" }
    }
    @Synchronized fun read(): KnouxTokens? {
        val text = prefs.getString("blob",null) ?: return null
        return try {
            val raw=Base64.decode(text,Base64.NO_WRAP)
            require(raw.size>12+16)
            val cipher=Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE,key,GCMParameterSpec(128,raw.copyOfRange(0,12)))
            val obj=JSONObject(String(cipher.doFinal(raw.copyOfRange(12,raw.size)),Charsets.UTF_8))
            KnouxTokens(obj.getString("access_token"),obj.getString("refresh_token"),obj.getLong("expires_at_ms"))
        } catch (e: Exception) {
            // Do not return damaged/mismatched credentials; require genuine reauthentication.
            clear();null
        }
    }
    @Synchronized fun clear() { prefs.edit().remove("blob").commit() }
}
