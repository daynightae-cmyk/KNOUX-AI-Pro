package com.knoux.ai.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.LayoutDirection
import com.knoux.ai.BuildConfig
import com.knoux.ai.R
import com.knoux.ai.auth.KnouxAuthConfig
import com.knoux.ai.auth.KnouxAuthController
import com.knoux.ai.auth.KnouxAuthException
import com.knoux.ai.ui.auth.AuthMethodButton
import com.knoux.ai.ui.auth.AuthVisualState
import com.knoux.ai.ui.auth.FingerprintOrb
import com.knoux.ai.ui.v2.CrownGold
import com.knoux.ai.ui.components.rememberMotionEnabled
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProTextPrimary
import com.knoux.ai.ui.theme.ProBlue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.compose.ui.semantics.Role

private val ProEditionGoldLogin = Color(0xFFFFCF68)

private enum class AuthSection { Main, EmailLogin, EmailSignup, OTP, Recovery }

@Composable
fun WelcomeScreen(
    onContinueAsGuest: () -> Unit,
    onSignedIn: () -> Unit,
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
    val scroll = rememberScrollState()
    var biometricJob by remember { mutableStateOf<Job?>(null) }
    val isFingerprintEntry = section == AuthSection.Main
    val isVerification = section == AuthSection.OTP
    val backToFingerprint: () -> Unit = {
        section = AuthSection.Main
        message = null
    }

    BackHandler(enabled = !isFingerprintEntry && !inProgress) {
        if (section == AuthSection.OTP || section == AuthSection.Recovery) {
            section = AuthSection.EmailLogin
            message = null
        } else {
            backToFingerprint()
        }
    }
    var appeared by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }

    LaunchedEffect(section) {
        scroll.scrollTo(0)
        if (section != AuthSection.OTP) otpCode = ""
        if (isFingerprintEntry) password = ""
    }

    LaunchedEffect(Unit) { appeared = true }

    val scanning = state == AuthVisualState.Scanning
    val heading = if (isVerification) "أكد هويتك" else "مرحبًا بك في KNOUX AI"
    val subheading = if (isVerification) "أدخل رمز التحقق المرسل إلى بريدك." else "وصول آمن لذكاء جهازك."

    fun cancelBiometric() {
        biometricJob?.cancel()
        state = AuthVisualState.Idle
        inProgress = false
        message = "تم إلغاء التحقق بالبصمة."
    }

    fun unlockWithBiometric() {
        if (inProgress) return
        if (activity == null) {
            message = "التحقق يحتاج واجهة نشطة."
            return
        }
        inProgress = true
        state = AuthVisualState.Scanning
        message = null
        biometricJob = scope.launch {
            try {
                if (controller.biometric(activity)) {
                    // Real BiometricPrompt success + live session confirmed by the
                    // controller: hold the green verified state until the user
                    // taps "المتابعة" (reference 2), then enter the app.
                    state = AuthVisualState.Success
                    message = null
                } else {
                    state = AuthVisualState.Idle
                    message = "لم يكتمل التحقق بالبصمة."
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: KnouxAuthException) {
                state = AuthVisualState.Idle
                message = when (e.code) {
                    "no_session" -> "سجّل الدخول أولًا بحساب Google أو مفتاح المرور أو البريد."
                    "biometric_unavailable" -> "البصمة القوية غير مسجّلة على هذا الجهاز."
                    else -> "تعذر التحقق بالبصمة. جرّب طريقة دخول أخرى."
                }
            } catch (_: Exception) {
                state = AuthVisualState.Idle
                message = "فشل التحقق بالبصمة."
            } finally {
                inProgress = false
            }
        }
    }

    BackHandler(enabled = scanning) { cancelBiometric() }
    LaunchedEffect(scanning) {
        if (scanning) scroll.scrollTo(0)
    }

    val reveal by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(if (motion) 480 else 0, easing = FastOutSlowInEasing),
        label = "auth-reveal",
    )

    BoxWithConstraints(
        Modifier.fillMaxSize().background(Color(0xFF07051C)).statusBarsPadding()
            .navigationBarsPadding().imePadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        val heroHeight = (maxHeight * if (scanning) 0.16f else 0.235f).coerceIn(150.dp, 210.dp)
        Column(
            Modifier.widthIn(max = 520.dp).fillMaxWidth().verticalScroll(scroll).padding(bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        LoginBrand()
        if (isFingerprintEntry) {
            LoginCosmicScene(heroHeight, reveal, overlapHeader = true)
        } else if (isVerification) {
            Box(Modifier.fillMaxWidth().height(118.dp), contentAlignment = Alignment.Center) {
                VerificationShield()
            }
        }

        if (scanning || state == AuthVisualState.Success) {
            val verified = state == AuthVisualState.Success
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp).loginGlass().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FingerprintOrb(
                    state = state,
                    onClick = {},
                    enabled = false,
                    size = 186.dp,
                    accessibilityLabel = if (verified) "تم التحقق بنجاح" else "جارٍ التحقق من بصمتك",
                    showStateBadge = false,
                )
                if (verified) {
                    // Green success pill flanked by gold laurels — reference (2).
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        LoginLaurel(mirror = false)
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF059669), Color(0xFF10B981)),
                                    )
                                )
                                .border(1.dp, Color(0xFF3DFFB0).copy(alpha = .8f), RoundedCornerShape(50))
                                .padding(horizontal = 22.dp, vertical = 9.dp),
                        ) {
                            Text(
                                "تم التحقق بنجاح",
                                color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                            )
                        }
                        LoginLaurel(mirror = true)
                    }
                    Text(
                        "مرحبًا بك إلى KNOUX AI Pro",
                        color = ProTextPrimary, fontSize = 14.sp, textAlign = TextAlign.Center,
                    )
                    Box(
                        Modifier.width(120.dp).height(1.5.dp).background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, ProEditionGoldLogin.copy(alpha = .8f), Color(0xFF3DFFB0).copy(alpha = .8f), Color.Transparent)
                            )
                        )
                    )
                    NeonLoginButton(
                        label = "المتابعة",
                        icon = Icons.Rounded.ChevronLeft,
                        green = true,
                        onClick = onSignedIn,
                    )
                } else {
                    Text(
                        "جارٍ التحقق من بصمتك…",
                        color = ProTextPrimary, fontSize = 21.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    )
                    NeonLoginButton("إلغاء", Icons.Rounded.Close, onClick = ::cancelBiometric)
                }
            }
            return@Column
        }

        if (!isFingerprintEntry) {
            Text(heading, color = ProTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(subheading, color = ProTextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
        message?.let { notice ->
            Text(
                notice, color = ProArcticCyan, fontSize = 13.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        if (inProgress) {
            LinearProgressIndicator(
                Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 4.dp),
                color = ProArcticCyan, trackColor = ProBlue.copy(alpha = 0.22f),
            )
        }

        if (isFingerprintEntry) {
            Box(Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                        .loginGlass()
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF241A57).copy(alpha = .8f))
                            .border(1.dp, ProArcticCyan.copy(alpha = .45f), RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Person, null, tint = ProArcticCyan, modifier = Modifier.size(18.dp))
                    }
                    Text("تسجيل الدخول", color = ProTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Login to KNOUX AI Pro", color = ProTextSecondary, fontSize = 10.sp)
                    Box(
                        Modifier.width(150.dp).height(1.dp).background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, ProEditionGoldLogin.copy(alpha = .75f), Color.Transparent)
                            )
                        )
                    )
                    GoogleButton(scope, inProgress, googleConfigured, controller, activity, hero = true) { result ->
                        inProgress = result.first
                        message = result.second
                        if (result.third) onSignedIn()
                    }
                    PasskeyButton(scope, inProgress, controller, activity, hero = true) { result ->
                        inProgress = result.first
                        message = result.second
                        if (result.third) onSignedIn()
                    }
                    LoginOrDivider()
                    Box(contentAlignment = Alignment.Center) {
                        FingerprintOrb(
                            state = state,
                            onClick = ::unlockWithBiometric,
                            size = 58.dp,
                            enabled = !inProgress,
                            accessibilityLabel = "تسجيل الدخول بالبصمة",
                            showStateBadge = false,
                        )
                        FingerprintBrackets(Modifier.size(78.dp))
                    }
                    Text("تسجيل الدخول باستخدام البصمة", color = ProTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Login with Biometrics", color = ProTextSecondary, fontSize = 9.sp)
                }
            }
        } else {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1B1248).copy(alpha = 0.62f))
                    .border(1.dp, ProBlue.copy(alpha = 0.50f), RoundedCornerShape(24.dp))
                    .padding(14.dp),
            ) {
                if (section == AuthSection.EmailLogin || section == AuthSection.EmailSignup) {
                    TextButton("العودة إلى البصمة", enabled = !inProgress, onClick = backToFingerprint)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White.copy(alpha = 0.04f)),
                    ) {
                        Text(
                            "تسجيل الدخول",
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (section == AuthSection.EmailLogin) ProTextPrimary else ProTextSecondary,
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        if (section == AuthSection.EmailLogin) {
                                            listOf(Color(0xFFA936FF), Color(0xFF147BFF))
                                        } else {
                                            listOf(Color.Transparent, Color.Transparent)
                                        }
                                    )
                                )
                                .clickable { section = AuthSection.EmailLogin }
                                .padding(vertical = 11.dp),
                        )
                        Text(
                            "إنشاء حساب",
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (section == AuthSection.EmailSignup) ProTextPrimary else ProTextSecondary,
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        if (section == AuthSection.EmailSignup) {
                                            listOf(Color(0xFFA936FF), Color(0xFF147BFF))
                                        } else {
                                            listOf(Color.Transparent, Color.Transparent)
                                        }
                                    )
                                )
                                .clickable { section = AuthSection.EmailSignup }
                                .padding(vertical = 11.dp),
                        )
                    }
                } else if (section == AuthSection.Recovery) {
                    Text(
                        "استعادة الوصول",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ProTextPrimary),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "سنرسل رابط استعادة آمنًا إلى بريدك.",
                        style = TextStyle(fontSize = 10.sp, color = ProTextSecondary),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(12.dp))
                EmailSection(
                    section = section,
                    email = email, onEmail = { email = it },
                    password = password, onPassword = { password = it },
                    otpCode = otpCode, onOtp = { otpCode = it },
                    inProgress = inProgress,
                    controller = controller,
                    activity = activity,
                    scope = scope,
                    onBack = { section = AuthSection.EmailLogin; message = null },
                    onNavigate = { section = it; message = null },
                    onAuth = onSignedIn,
                    onProgress = { inProgress = it },
                    onMessage = { message = it },
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    "أو تابع باستخدام",
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 10.sp, color = ProTextSecondary),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                if (isVerification) {
                    AuthMethodButton(
                        label = "استخدام البصمة",
                        icon = Icons.Rounded.Fingerprint,
                        accent = ProArcticCyan,
                        enabled = !inProgress,
                        onClick = backToFingerprint,
                    )
                    Spacer(Modifier.height(8.dp))
                    AuthMethodButton(
                        label = "استخدام كلمة المرور",
                        icon = Icons.Rounded.Key,
                        accent = ProArcticCyan,
                        enabled = !inProgress,
                        onClick = { section = AuthSection.EmailLogin; message = null },
                    )
                    Spacer(Modifier.height(8.dp))
                }
                GoogleButton(scope, inProgress, googleConfigured, controller, activity) { v ->
                    inProgress = v.first; message = v.second
                    if (v.third) onSignedIn()
                }
                Spacer(Modifier.height(8.dp))
                PasskeyButton(scope, inProgress, controller, activity) { v ->
                    inProgress = v.first; message = v.second
                    if (v.third) onSignedIn()
                }
                Spacer(Modifier.height(8.dp))
                GuestButton(onContinueAsGuest, enabled = !inProgress)
            }
        }

        Spacer(Modifier.height(14.dp))
        // Reference (7): three trust pillars — آمن ومحمي / سريع وفعال / بياناتك
        // خاصة. Tapping the band still opens the real account & privacy info.
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF43147A).copy(alpha = 0.48f), Color(0xFF10174B).copy(alpha = 0.58f))
                    )
                )
                .border(1.dp, ProBlue.copy(alpha = 0.48f), RoundedCornerShape(18.dp))
                .clickable(onClick = onOpenAccountInfo)
                .padding(horizontal = 8.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LoginTrustPillar(Icons.Rounded.Shield, "آمن ومحمي", "Secure & Protected", ProArcticCyan, Modifier.weight(1f))
            Box(Modifier.width(1.dp).height(34.dp).background(Color(0xFF5226B5).copy(alpha = .6f)))
            LoginTrustPillar(Icons.Rounded.Bolt, "سريع وفعال", "Fast & Efficient", ProEditionGoldLogin, Modifier.weight(1f))
            Box(Modifier.width(1.dp).height(34.dp).background(Color(0xFF5226B5).copy(alpha = .6f)))
            LoginTrustPillar(Icons.Rounded.Layers, "بياناتك خاصة", "Your Data is Private", ProArcticCyan, Modifier.weight(1f))
        }

        if (isFingerprintEntry) {
            // Secondary entry points sit below the reference stack: the first
            // screen keeps the approved composition, while email/OTP, sign-up
            // and guest access stay reachable by scrolling.
            Spacer(Modifier.height(10.dp))
            NeonLoginButton(
                label = "الدخول بالبريد الإلكتروني أو رمز التحقق",
                icon = Icons.Rounded.Email,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                subtitle = "Email / OTP Login",
                enabled = !inProgress,
                onClick = { section = AuthSection.EmailLogin; message = null },
            )
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("ليس لديك حساب؟", color = ProTextSecondary, fontSize = 13.sp)
                Row(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .border(1.2.dp, ProEditionGoldLogin, RoundedCornerShape(50))
                        .clickable(enabled = !inProgress, role = Role.Button) {
                            section = AuthSection.EmailSignup; message = null
                        }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(Icons.Rounded.PersonAdd, null, tint = ProEditionGoldLogin, modifier = Modifier.size(16.dp))
                    Text("إنشاء حساب جديد", color = ProEditionGoldLogin, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            TextButton("المتابعة كضيف", enabled = !inProgress, onClick = onContinueAsGuest)
        }

        Spacer(Modifier.height(14.dp))
      }
    }
}

@Composable
private fun PasskeyButton(
    scope: kotlinx.coroutines.CoroutineScope,
    inProgress: Boolean,
    controller: KnouxAuthController,
    activity: androidx.fragment.app.FragmentActivity?,
    hero: Boolean = false,
    onResult: (Triple<Boolean, String?, Boolean>) -> Unit,
) {
    val onClick: () -> Unit = launchProvider@{
        if (activity == null) { onResult(Triple(false, "التحقق يحتاج واجهة نشطة.", false)); return@launchProvider }
        scope.launch {
            onResult(Triple(true, null, false))
            try {
                val ok = controller.passkey(activity)
                onResult(Triple(false, if (ok) null else "تعذر تسجيل الدخول بمفتاح المرور.", ok))
            } catch (e: CancellationException) {
                throw e
            } catch (e: KnouxAuthException) {
                onResult(Triple(false, e.message ?: "تعذر تسجيل الدخول بمفتاح المرور.", false))
            } catch (e: Exception) {
                onResult(Triple(false, "مفتاح المرور غير متاح أو غير مُعد.", false))
            }
        }
    }
    if (hero) {
        NeonLoginButton(
            label = "تسجيل الدخول باستخدام مفاتيح المرور",
            icon = Icons.Rounded.Key,
            subtitle = "Continue with Passkeys",
            enabled = !inProgress,
            onClick = onClick,
        )
    } else {
        AuthMethodButton(
            label = "استخدام مفتاح المرور",
            icon = Icons.Rounded.Key,
            accent = ProArcticCyan,
            enabled = !inProgress,
            onClick = onClick,
        )
    }
}

@Composable
private fun GoogleButton(
    scope: kotlinx.coroutines.CoroutineScope,
    inProgress: Boolean,
    configured: Boolean,
    controller: KnouxAuthController,
    activity: androidx.fragment.app.FragmentActivity?,
    hero: Boolean = false,
    onResult: (Triple<Boolean, String?, Boolean>) -> Unit,
) {
    val onClick: () -> Unit = launchProvider@{
        if (activity == null) { onResult(Triple(false, "التحقق يحتاج واجهة نشطة.", false)); return@launchProvider }
        scope.launch {
            onResult(Triple(true, null, false))
            try {
                val ok = controller.google(activity)
                onResult(Triple(false, if (ok) null else "تعذر تسجيل الدخول بحساب Google.", ok))
            } catch (e: CancellationException) {
                throw e
            } catch (e: KnouxAuthException) {
                onResult(Triple(false, e.message ?: "تعذر تسجيل الدخول بحساب Google.", false))
            } catch (e: Exception) {
                onResult(Triple(false, "أُلغي تسجيل الدخول أو حدث خطأ في الشبكة.", false))
            }
        }
    }
    if (hero) {
        NeonLoginButton(
            label = "المتابعة باستخدام جوجل",
            icon = Icons.Rounded.Person,
            subtitle = "Continue with Google",
            // The approved login reference draws Google as a dark glass row
            // with the real mark, not a gold primary button.
            leadingPainter = R.drawable.knoux_google_g,
            enabled = configured && !inProgress,
            onClick = onClick,
        )
    } else {
        AuthMethodButton(
            label = "المتابعة بحساب Google",
            icon = Icons.Rounded.Person,
            accent = ProArcticCyan,
            enabled = configured && !inProgress,
            onClick = onClick,
        )
    }
}

@Composable
private fun GuestButton(onContinueAsGuest: () -> Unit, enabled: Boolean) {
    AuthMethodButton(
        label = "المتابعة كضيف",
        icon = Icons.Rounded.Person,
        accent = ProBlue,
        enabled = enabled,
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
    onNavigate: (AuthSection) -> Unit,
    onAuth: () -> Unit,
    onProgress: (Boolean) -> Unit,
    onMessage: (String?) -> Unit,
) {
    val btn = @Composable { label: String, action: suspend () -> Boolean ->
        AuthMethodButton(
            label = label, icon = Icons.Rounded.Email, accent = ProArcticCyan,
            enabled = !inProgress && email.isNotBlank(),
            onClick = {
                if (activity == null) return@AuthMethodButton
                scope.launch {
                    onProgress(true)
                    try {
                        if (action()) onAuth() else onMessage("تحقق من بريدك لرابط التأكيد.")
                    } catch (e: KnouxAuthException) { onMessage(e.message) }
                    catch (e: Exception) { onMessage("خطأ في الشبكة. حاول مرة أخرى.") }
                    finally { onProgress(false) }
                }
            },
        )
    }

    Column(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = email, onValueChange = onEmail,
            label = { Text("البريد الإلكتروني") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            singleLine = true, enabled = !inProgress,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = ProTextPrimary, unfocusedTextColor = ProTextSecondary,
                focusedBorderColor = ProArcticCyan, unfocusedBorderColor = ProBlue.copy(alpha = 0.3f),
                focusedLabelColor = ProArcticCyan, unfocusedLabelColor = ProTextSecondary,
                cursorColor = ProArcticCyan,
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        )

        if (section == AuthSection.EmailLogin || section == AuthSection.EmailSignup) {
            OutlinedTextField(
                value = password, onValueChange = onPassword,
                label = { Text("كلمة المرور") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                singleLine = true, enabled = !inProgress,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = ProTextPrimary, unfocusedTextColor = ProTextSecondary,
                    focusedBorderColor = ProArcticCyan, unfocusedBorderColor = ProBlue.copy(alpha = 0.3f),
                    focusedLabelColor = ProArcticCyan, unfocusedLabelColor = ProTextSecondary,
                    cursorColor = ProArcticCyan,
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            )
        }

        if (section == AuthSection.OTP) {
            VerificationCodeField(otpCode, onOtp, enabled = !inProgress)
            Spacer(Modifier.height(14.dp))
        }

        when (section) {
            AuthSection.EmailLogin -> {
                btn("تسجيل الدخول") { controller.login(email, password) }
                Spacer(Modifier.height(6.dp))
                TextButton("إعادة تعيين كلمة المرور") { onNavigate(AuthSection.Recovery) }
                TextButton("استخدام رمز التحقق بالبريد") { onNavigate(AuthSection.OTP) }
            }
            AuthSection.EmailSignup -> {
                btn("إنشاء حساب") { controller.signup(email, password) }
                Spacer(Modifier.height(6.dp))
                TextButton("لديك حساب بالفعل؟") { onNavigate(AuthSection.EmailLogin) }
            }
            AuthSection.OTP -> {
                var codeSent by remember(email) { mutableStateOf(false) }
                AuthMethodButton(
                    label = "تحقق من الرمز", icon = Icons.Rounded.Lock, accent = ProArcticCyan,
                    enabled = !inProgress && email.isNotBlank() && otpCode.isNotBlank(),
                    onClick = {
                        scope.launch {
                            onProgress(true)
                            try {
                                if (controller.verifyOtp(email, otpCode)) onAuth()
                                else onMessage("الرمز غير صالح أو منتهي.")
                            } catch (e: KnouxAuthException) { onMessage(e.message) }
                            catch (e: Exception) { onMessage("فشل التحقق.") }
                            finally { onProgress(false) }
                        }
                    },
                )
                Spacer(Modifier.height(6.dp))
                TextButton(
                    label = if (codeSent) "إعادة إرسال الرمز" else "إرسال الرمز",
                    enabled = !inProgress && email.isNotBlank(),
                ) {
                    scope.launch {
                        onProgress(true)
                        try {
                            controller.sendOtp(email)
                            codeSent = true
                            onMessage("تم إرسال الرمز.")
                        }
                        catch (e: KnouxAuthException) { onMessage(e.message) }
                        catch (e: Exception) { onMessage("تعذر إرسال الرمز.") }
                        finally { onProgress(false) }
                    }
                }
            }
            AuthSection.Recovery -> {
                AuthMethodButton(
                    label = "إرسال رابط الاستعادة", icon = Icons.Rounded.Email, accent = ProArcticCyan,
                    enabled = !inProgress && email.isNotBlank(),
                    onClick = {
                        scope.launch {
                            onProgress(true)
                            try { controller.recover(email); onMessage("تحقق من بريدك.") }
                            catch (e: KnouxAuthException) { onMessage(e.message) }
                            catch (e: Exception) { onMessage("تعذر طلب الاستعادة.") }
                            finally { onProgress(false) }
                        }
                    },
                )
            }
            else -> {}
        }

        if (section == AuthSection.Recovery) {
            Spacer(Modifier.height(10.dp))
            TextButton("استخدام كلمة المرور", enabled = !inProgress, onClick = onBack)
        }
    }
}

@Composable
private fun TextButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            label,
            style = TextStyle(fontSize = 12.sp, color = if (enabled) ProArcticCyan else ProTextSecondary),
        )
    }
}

@Composable
private fun VerificationCodeField(
    code: String,
    onCode: (String) -> Unit,
    enabled: Boolean,
) {
    var focused by remember { mutableStateOf(false) }
    BasicTextField(
        value = code,
        onValueChange = { onCode(it.filter { digit -> digit in '0'..'9' }) },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused }
            .semantics { contentDescription = "Verification code" },
        enabled = enabled,
        singleLine = true,
        textStyle = TextStyle(color = Color.Transparent, fontSize = 20.sp),
        cursorBrush = SolidColor(Color.Transparent),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                innerTextField()
                Row(
                    modifier = Modifier.clearAndSetSemantics {},
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    repeat(6) { index ->
                        val shape = RoundedCornerShape(12.dp)
                        val digits = if (index == 5) code.drop(index) else code.getOrNull(index)?.toString().orEmpty()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(Color(0xFF17133C), shape)
                                .border(
                                    if (focused && index == code.length.coerceAtMost(5)) 2.dp else 1.dp,
                                    Brush.linearGradient(listOf(Color(0xFFCE69FF), ProArcticCyan)),
                                    shape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                digits,
                                style = TextStyle(fontSize = if (digits.length <= 1) 20.sp else 12.sp, color = ProTextPrimary),
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun VerificationShield() {
    val violet = Color(0xFFC65BFF)
    Box(Modifier.size(width = 188.dp, height = 128.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val shield = Path().apply {
                moveTo(w * 0.5f, h * 0.08f)
                quadraticTo(w * 0.61f, h * 0.24f, w * 0.75f, h * 0.25f)
                lineTo(w * 0.75f, h * 0.49f)
                cubicTo(w * 0.74f, h * 0.70f, w * 0.64f, h * 0.83f, w * 0.5f, h * 0.93f)
                cubicTo(w * 0.36f, h * 0.83f, w * 0.26f, h * 0.70f, w * 0.25f, h * 0.49f)
                lineTo(w * 0.25f, h * 0.25f)
                quadraticTo(w * 0.39f, h * 0.24f, w * 0.5f, h * 0.08f)
                close()
            }
            val gradient = Brush.horizontalGradient(listOf(violet, ProArcticCyan))
            drawCircle(
                Brush.radialGradient(listOf(ProBlue.copy(alpha = 0.43f), Color.Transparent)),
                radius = h * 0.50f,
            )
            drawPath(
                shield,
                Brush.horizontalGradient(listOf(violet.copy(alpha = 0.26f), ProArcticCyan.copy(alpha = 0.20f))),
            )
            drawPath(shield, gradient, style = Stroke(2.dp.toPx()))
            rotate(-12f) {
                drawArc(
                    gradient, startAngle = 5f, sweepAngle = 172f, useCenter = false,
                    topLeft = Offset(w * 0.06f, h * 0.39f),
                    size = Size(w * 0.88f, h * 0.35f),
                    style = Stroke(2.dp.toPx()),
                )
            }
        }
        Icon(Icons.Rounded.Lock, contentDescription = null, tint = ProTextPrimary, modifier = Modifier.size(41.dp))
    }
}
