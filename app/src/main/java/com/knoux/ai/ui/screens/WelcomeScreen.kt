package com.knoux.ai.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.BuildConfig
import com.knoux.ai.auth.KnouxAuthConfig
import com.knoux.ai.auth.KnouxAuthController
import com.knoux.ai.auth.KnouxAuthException
import com.knoux.ai.ui.auth.AuthMethodButton
import com.knoux.ai.ui.auth.AuthVisualState
import com.knoux.ai.ui.auth.FingerprintOrb
import com.knoux.ai.ui.components.BrandLockup
import com.knoux.ai.ui.components.rememberMotionEnabled
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxMutedText
import com.knoux.ai.ui.theme.KnouxSoftText
import com.knoux.ai.ui.theme.KnouxText
import com.knoux.ai.ui.theme.KnouxViolet
import kotlinx.coroutines.launch

private enum class AuthSection { Main, EmailLogin, EmailSignup, OTP, Recovery }

@Composable
fun WelcomeScreen(
    onContinueAsGuest: () -> Unit,
    onOpenAccountInfo: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val motion = rememberMotionEnabled()

    val config = remember {
        KnouxAuthConfig(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabasePublishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            googleWebClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID,
        )
    }
    val controller = remember { KnouxAuthController(context, config) }
    val activity = context as? androidx.fragment.app.FragmentActivity
    val googleConfigured = remember { BuildConfig.GOOGLE_WEB_CLIENT_ID.isNotBlank() }

    var state by remember { mutableStateOf(AuthVisualState.Idle) }
    var message by remember { mutableStateOf<String?>(null) }
    var inProgress by remember { mutableStateOf(false) }
    var section by remember { mutableStateOf(AuthSection.Main) }
    var appeared by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { appeared = true }
    LaunchedEffect(Unit) {
        val valid = controller.resumeIfVerified()
        if (valid) onContinueAsGuest()
    }

    val heading = when {
        state == AuthVisualState.Success -> "Identity verified"
        state == AuthVisualState.Scanning -> "Scanning biometrics"
        else -> "Unlock KNOUX AI"
    }
    val subheading = when {
        state == AuthVisualState.Success -> "Opening your device workspace."
        state == AuthVisualState.Scanning -> "Keep your finger on the sensor."
        else -> "Confirm your identity to open the app."
    }

    val reveal by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(if (motion) 480 else 0, easing = FastOutSlowInEasing),
        label = "auth-reveal",
    )

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(14.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF1C1046).copy(alpha = 0.86f),
                            Color(0xFF0C0826).copy(alpha = 0.82f),
                        )
                    )
                )
                .border(1.dp, KnouxViolet.copy(alpha = 0.30f), RoundedCornerShape(22.dp))
                .padding(horizontal = 16.dp, vertical = 11.dp),
        ) {
            BrandLockup(symbolSize = 32.dp, wordmarkHeight = 16.dp)
        }

        Spacer(Modifier.height(18.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(276.dp),
            contentAlignment = Alignment.Center,
        ) {
            FingerprintOrb(
                state = state,
                onClick = {
                    if (inProgress) return@FingerprintOrb
                    if (activity == null) {
                        message = "Authentication requires an active UI context."
                        return@FingerprintOrb
                    }
                    inProgress = true
                    state = AuthVisualState.Scanning
                    message = null
                    scope.launch {
                        try {
                            val ok = controller.biometric(activity)
                            if (ok) {
                                state = AuthVisualState.Success
                                onContinueAsGuest()
                            } else {
                                state = AuthVisualState.Idle
                                message = "Biometric cancelled."
                            }
                        } catch (e: KnouxAuthException) {
                            state = AuthVisualState.Idle
                            message = when (e.code) {
                                "no_session" -> "Sign in first using Google, Passkey or email."
                                "biometric_unavailable" -> "Strong biometric not enrolled on this device."
                                else -> e.message
                            }
                        } catch (e: Exception) {
                            state = AuthVisualState.Idle
                            message = "Biometric check failed."
                        } finally { inProgress = false }
                    }
                },
                modifier = Modifier.graphicsLayer { alpha = reveal },
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            heading,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                fontSize = 27.sp,
                lineHeight = 33.sp,
                color = KnouxText,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = reveal
                    translationY = (1f - reveal) * 12f
                },
        )
        Spacer(Modifier.height(4.dp))
        Text(
            subheading,
            style = TextStyle(fontSize = 12.sp, lineHeight = 17.sp, color = KnouxSoftText),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().graphicsLayer { alpha = reveal },
        )

        if (message != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                message!!,
                style = TextStyle(fontSize = 9.5.sp, lineHeight = 13.sp, color = KnouxCyan),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(16.dp))

        if (section == AuthSection.Main) {
            PasskeyButton(scope, inProgress, controller, activity) { v ->
                inProgress = v.first; message = v.second
                if (v.third) onContinueAsGuest()
            }
            Spacer(Modifier.height(9.dp))
            GoogleButton(scope, inProgress, googleConfigured, controller, activity) { v ->
                inProgress = v.first; message = v.second
                if (v.third) onContinueAsGuest()
            }
            Spacer(Modifier.height(9.dp))
            GuestButton(onContinueAsGuest)

            Spacer(Modifier.height(14.dp))

            AuthMethodButton(
                label = "Continue with Email",
                icon = Icons.Rounded.Email,
                accent = KnouxMagenta,
                enabled = !inProgress,
                onClick = { section = AuthSection.EmailLogin },
                modifier = Modifier.graphicsLayer { alpha = reveal },
            )
        } else {
            EmailSection(
                section = section,
                email = email, onEmail = { email = it },
                password = password, onPassword = { password = it },
                otpCode = otpCode, onOtp = { otpCode = it },
                inProgress = inProgress,
                controller = controller,
                activity = activity,
                scope = scope,
                onBack = { section = AuthSection.Main; message = null },
                onSection = { section = it; message = null },
                onNavigate = { section = it; message = null },
                onAuth = { onContinueAsGuest() },
                onProgress = { inProgress = it },
                onMessage = { message = it },
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.04f))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                .clickable(onClick = onOpenAccountInfo)
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Info, contentDescription = null, tint = KnouxMutedText, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "Guest mode keeps every tool on this device. No account, no upload, no tracking.",
                style = TextStyle(fontSize = 9.sp, lineHeight = 12.5.sp, color = KnouxMutedText),
            )
        }

        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun PasskeyButton(
    scope: kotlinx.coroutines.CoroutineScope,
    inProgress: Boolean,
    controller: KnouxAuthController,
    activity: androidx.fragment.app.FragmentActivity?,
    onResult: (Triple<Boolean, String?, Boolean>) -> Unit,
) {
    AuthMethodButton(
        label = "Use a passkey",
        icon = Icons.Rounded.Key,
        accent = KnouxMagenta,
        enabled = !inProgress,
        onClick = {
            if (activity == null) { onResult(Triple(false, "Authentication requires an active UI context.", false)); return@AuthMethodButton }
            scope.launch {
                onResult(Triple(true, null, false))
                try {
                    val ok = controller.passkey(activity)
                    onResult(Triple(false, null, ok))
                } catch (e: KnouxAuthException) {
                    onResult(Triple(false, e.message ?: "Passkey sign-in failed.", false))
                } catch (e: Exception) {
                    onResult(Triple(false, "Passkey unavailable or misconfigured.", false))
                }
            }
        },
    )
}

@Composable
private fun GoogleButton(
    scope: kotlinx.coroutines.CoroutineScope,
    inProgress: Boolean,
    configured: Boolean,
    controller: KnouxAuthController,
    activity: androidx.fragment.app.FragmentActivity?,
    onResult: (Triple<Boolean, String?, Boolean>) -> Unit,
) {
    AuthMethodButton(
        label = "Continue with Google",
        icon = Icons.Rounded.Person,
        accent = KnouxCyan,
        enabled = configured && !inProgress,
        trailing = if (configured) null else "Setup pending",
        onClick = {
            if (activity == null) { onResult(Triple(false, "Authentication requires an active UI context.", false)); return@AuthMethodButton }
            scope.launch {
                onResult(Triple(true, null, false))
                try {
                    val ok = controller.google(activity)
                    onResult(Triple(false, null, ok))
                } catch (e: KnouxAuthException) {
                    onResult(Triple(false, e.message ?: "Google sign-in failed.", false))
                } catch (e: Exception) {
                    onResult(Triple(false, "Sign-in cancelled or network error.", false))
                }
            }
        },
    )
}

@Composable
private fun GuestButton(onContinueAsGuest: () -> Unit) {
    AuthMethodButton(
        label = "Continue as Guest",
        icon = Icons.Rounded.Person,
        accent = KnouxViolet,
        onClick = onContinueAsGuest,
    )
}

@Composable
private fun EmailSection(
    section: AuthSection,
    email: String, onEmail: (String) -> Unit,
    password: String, onPassword: (String) -> Unit,
    otpCode: String, onOtp: (String) -> Unit,
    inProgress: Boolean,
    controller: KnouxAuthController,
    activity: androidx.fragment.app.FragmentActivity?,
    scope: kotlinx.coroutines.CoroutineScope,
    onBack: () -> Unit,
    onSection: (AuthSection) -> Unit,
    onNavigate: (AuthSection) -> Unit,
    onAuth: () -> Unit,
    onProgress: (Boolean) -> Unit,
    onMessage: (String?) -> Unit,
) {
    val btn = @Composable { label: String, action: suspend () -> Boolean ->
        AuthMethodButton(
            label = label, icon = Icons.Rounded.Email, accent = KnouxCyan,
            enabled = !inProgress && email.isNotBlank(),
            onClick = {
                if (activity == null) return@AuthMethodButton
                scope.launch {
                    onProgress(true)
                    try {
                        if (action()) onAuth() else onMessage("Check your email for a confirmation link.")
                    } catch (e: KnouxAuthException) { onMessage(e.message) }
                    catch (e: Exception) { onMessage("Network error. Try again.") }
                    finally { onProgress(false) }
                }
            },
        )
    }

    Column(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = email, onValueChange = onEmail,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            singleLine = true, enabled = !inProgress,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = KnouxText, unfocusedTextColor = KnouxSoftText,
                focusedBorderColor = KnouxCyan, unfocusedBorderColor = KnouxViolet.copy(alpha = 0.3f),
                focusedLabelColor = KnouxCyan, unfocusedLabelColor = KnouxMutedText,
                cursorColor = KnouxCyan,
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        )

        if (section == AuthSection.EmailLogin || section == AuthSection.EmailSignup) {
            OutlinedTextField(
                value = password, onValueChange = onPassword,
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                singleLine = true, enabled = !inProgress,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = KnouxText, unfocusedTextColor = KnouxSoftText,
                    focusedBorderColor = KnouxCyan, unfocusedBorderColor = KnouxViolet.copy(alpha = 0.3f),
                    focusedLabelColor = KnouxCyan, unfocusedLabelColor = KnouxMutedText,
                    cursorColor = KnouxCyan,
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            )
        }

        if (section == AuthSection.OTP) {
            OutlinedTextField(
                value = otpCode, onValueChange = onOtp,
                label = { Text("Verification code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                singleLine = true, enabled = !inProgress,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = KnouxText, unfocusedTextColor = KnouxSoftText,
                    focusedBorderColor = KnouxCyan, unfocusedBorderColor = KnouxViolet.copy(alpha = 0.3f),
                    focusedLabelColor = KnouxCyan, unfocusedLabelColor = KnouxMutedText,
                    cursorColor = KnouxCyan,
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            )
        }

        when (section) {
            AuthSection.EmailLogin -> {
                btn("Sign In") { controller.login(email, password) }
                Spacer(Modifier.height(6.dp))
                TextButton("Create account") { onNavigate(AuthSection.EmailSignup) }
                TextButton("Reset password") { onNavigate(AuthSection.Recovery) }
            }
            AuthSection.EmailSignup -> {
                btn("Create Account") { controller.signup(email, password) }
                Spacer(Modifier.height(6.dp))
                TextButton("Already have an account?") { onNavigate(AuthSection.EmailLogin) }
            }
            AuthSection.OTP -> {
                AuthMethodButton(
                    label = "Verify Code", icon = Icons.Rounded.Email, accent = KnouxCyan,
                    enabled = !inProgress && otpCode.isNotBlank(),
                    onClick = {
                        scope.launch {
                            onProgress(true)
                            try {
                                if (controller.verifyOtp(email, otpCode)) onAuth()
                                else onMessage("Invalid or expired code.")
                            } catch (e: KnouxAuthException) { onMessage(e.message) }
                            catch (e: Exception) { onMessage("Verification failed.") }
                            finally { onProgress(false) }
                        }
                    },
                )
                Spacer(Modifier.height(6.dp))
                TextButton("Resend code") {
                    scope.launch {
                        onProgress(true)
                        try { controller.sendOtp(email); onMessage("Code sent.") }
                        catch (e: KnouxAuthException) { onMessage(e.message) }
                        catch (e: Exception) { onMessage("Failed to send code.") }
                        finally { onProgress(false) }
                    }
                }
            }
            AuthSection.Recovery -> {
                AuthMethodButton(
                    label = "Send Reset Link", icon = Icons.Rounded.Email, accent = KnouxMagenta,
                    enabled = !inProgress && email.isNotBlank(),
                    onClick = {
                        scope.launch {
                            onProgress(true)
                            try { controller.recover(email); onMessage("Check your inbox.") }
                            catch (e: KnouxAuthException) { onMessage(e.message) }
                            catch (e: Exception) { onMessage("Recovery request failed.") }
                            finally { onProgress(false) }
                        }
                    },
                )
            }
            else -> {}
        }

        Spacer(Modifier.height(10.dp))
        TextButton("Back") { onBack() }
    }
}

@Composable
private fun TextButton(label: String, onClick: () -> Unit) {
    Text(
        label,
        style = TextStyle(fontSize = 11.sp, color = KnouxCyan),
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
    )
}