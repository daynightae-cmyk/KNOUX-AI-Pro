package com.knoux.ai.ui.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialUnsupportedException
import androidx.credentials.exceptions.publickeycredential.GetPublicKeyCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.knoux.ai.R

/**
 * Authentication entry points for the post-splash gate.
 *
 * Everything runs locally unless the owner fills in the production keys:
 *  - biometric unlock uses the device's own authenticator (works today),
 *  - passkey needs a relying-party id (your domain),
 *  - "Sign in with Google" needs a web client id from the Cloud console.
 *
 * While the keys are placeholders the app never pretends a sign-in happened:
 * it explains what is missing and keeps guest mode available.
 */
object AuthKeys {
    fun googleWebClientId(context: Context): String =
        context.getString(R.string.google_web_client_id)

    fun passkeyRpId(context: Context): String = context.getString(R.string.passkey_rp_id)

    fun googleConfigured(context: Context): Boolean =
        !googleWebClientId(context).startsWith("REPLACE")

    fun passkeyConfigured(context: Context): Boolean =
        !passkeyRpId(context).startsWith("REPLACE")
}

enum class AuthMethod { Biometric, Passkey, Google, Guest }

data class AuthOutcome(
    val method: AuthMethod,
    val success: Boolean,
    val message: String? = null,
)

/** True when the device can actually run a biometric or device-credential check. */
fun biometricAvailable(context: Context): Boolean {
    val manager = BiometricManager.from(context)
    val allowed = BiometricManager.Authenticators.BIOMETRIC_STRONG or
        BiometricManager.Authenticators.DEVICE_CREDENTIAL
    return manager.canAuthenticate(allowed) == BiometricManager.BIOMETRIC_SUCCESS
}

/**
 * Runs the system biometric prompt.
 *
 * [onResult] is called with the outcome; a cancelled or failed check is
 * reported honestly and never treated as a successful sign-in.
 */
fun runBiometric(
    context: Context,
    onResult: (AuthOutcome) -> Unit,
) {
    val activity = context as? androidx.fragment.app.FragmentActivity
    if (activity == null) {
        onResult(AuthOutcome(AuthMethod.Biometric, false, "Biometrics need an activity context."))
        return
    }
    val executor = ContextCompat.getMainExecutor(activity)
    val prompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onResult(AuthOutcome(AuthMethod.Biometric, true, "Device identity confirmed."))
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onResult(
                    AuthOutcome(
                        AuthMethod.Biometric,
                        false,
                        errString.toString(),
                    )
                )
            }

            override fun onAuthenticationFailed() {
                // The system keeps the prompt open; nothing to report yet.
            }
        },
    )
    val allowed = BiometricManager.Authenticators.BIOMETRIC_STRONG or
        BiometricManager.Authenticators.DEVICE_CREDENTIAL
    prompt.authenticate(
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock KNOUX AI")
            .setSubtitle("Confirm it is you to open your device tools")
            .setAllowedAuthenticators(allowed)
            .build()
    )
}

/**
 * Google sign-in through the Credential Manager.
 *
 * Returns a clear failure when the web client id is still a placeholder, so
 * the UI can explain the missing key instead of showing a broken dialog.
 */
suspend fun runGoogleSignIn(context: Context): AuthOutcome {
    if (!AuthKeys.googleConfigured(context)) {
        return AuthOutcome(
            AuthMethod.Google,
            false,
            "Google sign-in needs the web client id from the Cloud console (res/values/strings.xml).",
        )
    }
    return try {
        val manager = CredentialManager.create(context)
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption.Builder(AuthKeys.googleWebClientId(context))
                    .build()
            )
            .build()
        manager.getCredential(context, request)
        AuthOutcome(
            AuthMethod.Google,
            true,
            "Google account attached to this device session.",
        )
    } catch (error: NoCredentialException) {
        AuthOutcome(AuthMethod.Google, false, "No Google account is available on this device.")
    } catch (error: GetCredentialException) {
        AuthOutcome(AuthMethod.Google, false, error.message ?: error.errorMessage?.toString())
    } catch (error: Exception) {
        AuthOutcome(AuthMethod.Google, false, error.message ?: "Google sign-in failed.")
    }
}

/**
 * Passkey sign-in through the Credential Manager.
 *
 * A passkey is bound to a relying party (your domain), so the flow stays
 * disabled until the rp id is configured.
 */
suspend fun runPasskeySignIn(context: Context): AuthOutcome {
    if (!AuthKeys.passkeyConfigured(context)) {
        return AuthOutcome(
            AuthMethod.Passkey,
            false,
            "Passkeys need a relying-party id on your domain (res/values/strings.xml).",
        )
    }
    return try {
        val manager = CredentialManager.create(context)
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetPublicKeyCredentialOption(AuthKeys.passkeyRpId(context))
            )
            .build()
        manager.getCredential(context, request)
        AuthOutcome(AuthMethod.Passkey, true, "Passkey verified on this device.")
    } catch (error: NoCredentialException) {
        AuthOutcome(AuthMethod.Passkey, false, "No passkey is registered on this device yet.")
    } catch (error: CreateCredentialException) {
        AuthOutcome(AuthMethod.Passkey, false, error.message ?: error.errorMessage?.toString())
    } catch (error: CreateCredentialUnsupportedException) {
        AuthOutcome(AuthMethod.Passkey, false, "This device cannot create passkeys yet.")
    } catch (error: GetPublicKeyCredentialException) {
        AuthOutcome(AuthMethod.Passkey, false, error.message ?: error.errorMessage?.toString())
    } catch (error: GetCredentialException) {
        AuthOutcome(AuthMethod.Passkey, false, error.message ?: error.errorMessage?.toString())
    } catch (error: Exception) {
        AuthOutcome(AuthMethod.Passkey, false, error.message ?: "Passkey sign-in failed.")
    }
}
