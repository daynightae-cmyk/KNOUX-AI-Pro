package com.knoux.ai.ui.v2

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.PersonOff
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.components.HeaderHalo
import com.knoux.ai.ui.components.rememberMotionEnabled
import com.knoux.ai.ui.screens.LoginCosmicScene
import androidx.compose.ui.zIndex

private const val ONBOARDING_PREFS = "knoux_prefs"
private const val ONBOARDING_KEY = "knoux_onboarding_seen_v1"

fun hasSeenKnouxOnboarding(context: Context): Boolean =
    context.applicationContext
        .getSharedPreferences(ONBOARDING_PREFS, Context.MODE_PRIVATE)
        .getBoolean(ONBOARDING_KEY, false)

fun markKnouxOnboardingSeen(context: Context, seen: Boolean = true) {
    context.applicationContext
        .getSharedPreferences(ONBOARDING_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(ONBOARDING_KEY, seen)
        .apply()
}

/**
 * First-launch introduction for KNOUX. It is shown once per install, before
 * the shell, and can be replayed from the developer panel in Settings.
 */
@Composable
fun KnouxOnboarding(onFinish: () -> Unit) {
    var page by rememberSaveable { mutableStateOf(0) }
    val motion = rememberMotionEnabled()
    BackHandler(enabled = page > 0) { page-- }

    Box(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 22.dp)
    ) {
        if (page == 1) EcosystemRails(Modifier.fillMaxSize())
        Row(
            Modifier.fillMaxWidth().zIndex(1f).padding(top = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (page == 1) {
                Box(
                    Modifier.heightIn(min = 44.dp).clickable { page-- }.padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    KText("رجوع  →", size = 14.sp, color = Color.White)
                }
            } else {
                Box(Modifier.size(26.dp), contentAlignment = Alignment.Center) {
                    HeaderHalo(Modifier.matchParentSize(), color = V2Cyan)
                    androidx.compose.foundation.Image(
                        painter = painterResource(R.drawable.knoux_symbol_alpha),
                        contentDescription = "KNOUX AI",
                        modifier = Modifier.matchParentSize(),
                    )
                }
                Spacer(Modifier.width(6.dp))
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.knoux_wordmark_alpha),
                    contentDescription = "knoux ai",
                    modifier = Modifier.height(13.dp),
                )
            }
            Spacer(Modifier.weight(1f))
            if (page != 1) {
                KText("${page + 1} / 3", size = 11.sp, color = V2Cyan)
                Spacer(Modifier.width(8.dp))
            }
            Box(
                Modifier
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, V2Cyan.copy(alpha = .40f), RoundedCornerShape(50))
                    .clickable(onClick = onFinish)
                    .padding(horizontal = 11.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                KText("تخطي", size = if (page == 1) 14.sp else 10.sp, color = V2Soft)
            }
        }

        Column(
            Modifier.fillMaxSize().padding(top = 52.dp, bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Crossfade(
                targetState = page,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                animationSpec = tween(if (motion) 320 else 0),
                label = "onboarding-page",
            ) { index ->
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    when (index) {
                        0 -> OnboardingWelcomePage()
                        1 -> OnboardingEcosystemPage()
                        else -> OnboardingFounderPage()
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { index ->
                    Box(
                        Modifier
                            .height(6.dp)
                            .width(if (index == page) 22.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == page) V2Cyan
                                else Color.White.copy(alpha = .18f)
                            )
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            V2GradientButton(
                text = if (page < 2) "التالي" else "ابدأ الآن",
                onClick = { if (page < 2) page++ else onFinish() },
                icon = if (page == 1) Icons.Rounded.ChevronRight else null,
            )
            Spacer(Modifier.height(4.dp))
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(enabled = page == 2) { if (page == 2) page-- }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                KText(
                    if (page == 2) "رجوع" else " ",
                    size = 9.5.sp,
                    color = if (page == 2) V2Soft else Color.Transparent,
                )
            }
        }
    }
}

@Composable
private fun OnboardingWelcomePage() {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginCosmicScene(280.dp, reveal = 1f)
        Spacer(Modifier.height(2.dp))
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) { append("KNOUX AI ") }
                withStyle(
                    SpanStyle(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFFFE7AA), V2Amber, Color(0xFFFFB043))
                        )
                    )
                ) { append("Pro") }
            },
            style = TextStyle(fontFamily = V2Font, fontWeight = FontWeight.Black, fontSize = 30.sp),
        )
        Spacer(Modifier.height(3.dp))
        KText(
            "مساعدك العربي الذكي لصحة الجهاز وخصوصيته",
            size = 11.sp,
            color = V2Soft,
            align = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OnboardingChip(Icons.Rounded.PersonOff, "بدون حساب")
            OnboardingChip(Icons.Rounded.CloudOff, "بدون سحابة")
            OnboardingChip(Icons.Rounded.Language, "عربي أولًا")
        }
    }
}

@Composable
private fun OnboardingCompanyDetails() {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "عن KNOUX",
            style = TextStyle(fontFamily = V2Font, fontWeight = FontWeight.Black, fontSize = 26.sp, color = Color.White),
        )
        Spacer(Modifier.height(4.dp))
        KText(
            "KNOUX AI — A Product of KNOUX",
            size = 13.sp,
            weight = FontWeight.Bold,
            color = V2Cyan,
        )
        Spacer(Modifier.height(12.dp))
        V2GlassCard(
            Modifier.fillMaxWidth(),
            padding = 14.dp,
            radius = 22.dp,
            glow = V2Violet,
            borderAlpha = .42f,
        ) {
            Column {
                KText(
                    "KNOUX تطوّر أدوات لإدارة جهازك. الفحص والتنظيف والخصوصية تعتمد على البيانات والأذونات اللي يتيحها Android.",
                    size = 12.sp,
                    lineHeight = 19.sp,
                    color = V2Soft,
                    align = TextAlign.Center,
                )
                Spacer(Modifier.height(10.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = .08f)))
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = V2Green, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(6.dp))
                    KText("تطوير: KNOUX Studio", size = 9.sp, weight = FontWeight.Bold)
                }
                Spacer(Modifier.height(3.dp))
                KText("تقدر تستخدم أدوات الجهاز كضيف. تسجيل الدخول الاختياري يستخدم مزوّد المصادقة.", size = 11.sp, lineHeight = 17.sp, color = V2Soft)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OnboardingChip(Icons.Rounded.Shield, "خصوصية أولًا")
            OnboardingChip(Icons.Rounded.CloudOff, "أدوات الجهاز محليًا")
        }
    }
}

@Composable
private fun OnboardingFounderPage() {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginCosmicScene(194.dp, reveal = 1f)
        V2GlassCard(
            Modifier.fillMaxWidth(),
            padding = 13.dp,
            radius = 22.dp,
            glow = V2Cyan,
            borderAlpha = .48f,
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                KText("المؤسس", size = 11.sp, color = V2Cyan)
                Spacer(Modifier.height(5.dp))
                Text(
                    "Eng. Sadek Elgazar",
                    style = TextStyle(
                        fontSize = 23.sp, color = Color.White, fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center, textDirection = TextDirection.Ltr,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                KText("م. صادق الجزار", size = 17.sp, weight = FontWeight.Bold, align = TextAlign.Center)
            }
        }
        Spacer(Modifier.height(14.dp))
        OnboardingCompanyDetails()
    }
}

@Composable
private fun OnboardingChip(icon: ImageVector, text: String) {
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = .05f))
            .border(1.dp, Color.White.copy(alpha = .12f), RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = V2Cyan, modifier = Modifier.size(12.dp))
        Spacer(Modifier.width(4.dp))
        KText(text, size = 7.6.sp, color = V2Soft)
    }
}
