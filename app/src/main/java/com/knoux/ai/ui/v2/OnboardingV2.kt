package com.knoux.ai.ui.v2

import android.content.Context
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.PersonOff
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R

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
    var page by remember { mutableStateOf(0) }

    Box(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 22.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(R.drawable.knoux_symbol_alpha),
                contentDescription = "KNOUX AI",
                modifier = Modifier.size(26.dp),
            )
            Spacer(Modifier.width(6.dp))
            androidx.compose.foundation.Image(
                painter = painterResource(R.drawable.knoux_wordmark_alpha),
                contentDescription = "knoux ai",
                modifier = Modifier.height(13.dp),
            )
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, Color.White.copy(alpha = .14f), RoundedCornerShape(50))
                    .clickable(onClick = onFinish)
                    .padding(horizontal = 11.dp, vertical = 6.dp)
            ) {
                KText("تخطي", size = 9.sp, color = V2Soft)
            }
        }

        Column(
            Modifier.fillMaxSize().padding(top = 52.dp, bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Crossfade(
                targetState = page,
                animationSpec = tween(320),
                label = "onboarding-page",
            ) { index ->
                when (index) {
                    0 -> OnboardingWelcomePage()
                    1 -> OnboardingCompanyPage()
                    else -> OnboardingReadyPage()
                }
            }

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { index ->
                    Box(
                        Modifier
                            .height(6.dp)
                            .width(if (index == page) 22.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == page) V2Amber
                                else Color.White.copy(alpha = .18f)
                            )
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            V2BrightGoldButton(
                text = if (page < 2) "التالي" else "ابدأ الآن",
                onClick = { if (page < 2) page++ else onFinish() },
            )
            Spacer(Modifier.height(4.dp))
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(enabled = page > 0) { if (page > 0) page-- }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                KText(
                    if (page > 0) "رجوع" else " ",
                    size = 9.5.sp,
                    color = if (page > 0) V2Soft else Color.Transparent,
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
        V2DeerHeader(Modifier.size(width = 196.dp, height = 308.dp))
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
private fun OnboardingCompanyPage() {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(20.dp))
        CrownGold(size = 58.dp)
        Spacer(Modifier.height(10.dp))
        Text(
            "صُنع في KNOUX",
            style = TextStyle(fontFamily = V2Font, fontWeight = FontWeight.Black, fontSize = 26.sp, color = Color.White),
        )
        Spacer(Modifier.height(4.dp))
        KText(
            "شركة KNOUX للتقنية",
            size = 10.5.sp,
            weight = FontWeight.Bold,
            color = V2GoldText,
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
                    "فريق عربي مستقل يبني أدوات نظام تعمل بالكامل على جهازك: فحص، تنظيف، خصوصية وتقارير — دون رفع بياناتك إلى أي خادم.",
                    size = 9.6.sp,
                    lineHeight = 15.sp,
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
                KText("نسخة المعاينة V2 · جميع القياسات تصميمية حتى تفعيل الخدمات.", size = 7.6.sp, color = V2Muted)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OnboardingChip(Icons.Rounded.Shield, "خصوصية أولًا")
            OnboardingChip(Icons.Rounded.CloudOff, "محلي بالكامل")
        }
    }
}

@Composable
private fun OnboardingReadyPage() {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            "كل ما تحتاجه في مكان واحد",
            style = TextStyle(fontFamily = V2Font, fontWeight = FontWeight.Black, fontSize = 24.sp, color = Color.White),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        KText("أربعة أقسام أساسية تصل لكل الأدوات", size = 10.sp, color = V2Soft)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OnboardingServiceTile("فحص عميق", Icons.Rounded.Radar, V2Cyan, Modifier.weight(1f))
            OnboardingServiceTile("تنظيف ذكي", Icons.Rounded.CleaningServices, V2Violet, Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OnboardingServiceTile("خصوصية", Icons.Rounded.Shield, V2Pink, Modifier.weight(1f))
            OnboardingServiceTile("تقارير", Icons.Rounded.BarChart, V2Amber, Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        V2GlassCard(
            Modifier.fillMaxWidth(),
            padding = 12.dp,
            radius = 18.dp,
            glow = V2Amber,
            borderAlpha = .34f,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.CheckCircle, null, tint = V2Amber, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(7.dp))
                KText(
                    "يمكن تفعيل وضع Pro التجريبي لاحقًا من قسم Pro، وفتح لوحة المطور بخمس نقرات على رقم الإصدار.",
                    size = 8.sp,
                    lineHeight = 12.sp,
                    color = V2Soft,
                )
            }
        }
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

@Composable
private fun OnboardingServiceTile(
    title: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    V2GlassCard(
        modifier.height(92.dp),
        padding = 10.dp,
        radius = 18.dp,
        glow = accent,
        borderAlpha = .34f,
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            V2IconTile(icon, accent, size = 34.dp, iconSize = 18.dp)
            Spacer(Modifier.height(6.dp))
            KText(title, size = 9.sp, weight = FontWeight.Bold)
        }
    }
}
