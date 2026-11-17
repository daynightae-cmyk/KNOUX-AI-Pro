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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Lock
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.components.BrandSymbol
import com.knoux.ai.ui.components.HeaderHalo
import com.knoux.ai.ui.components.rememberMotionEnabled
import com.knoux.ai.ui.screens.LoginCosmicScene
import com.knoux.ai.ui.screens.LoginLaurel
import androidx.compose.ui.zIndex

private const val ONBOARDING_PREFS = "knoux_prefs"
private const val ONBOARDING_KEY = "knoux_onboarding_seen_v1"
private enum class OnboardingStep { WELCOME, COMPANY, ECOSYSTEM, FOUNDER }

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
    val steps = OnboardingStep.entries
    val ecosystem = steps[page] == OnboardingStep.ECOSYSTEM
    BackHandler(enabled = page > 0) { page-- }

    Box(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 22.dp)
    ) {
        if (ecosystem) EcosystemRails(Modifier.fillMaxSize())
        Row(
            Modifier.fillMaxWidth().zIndex(1f).padding(top = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (ecosystem) {
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
                Spacer(Modifier.width(5.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(V2Amber.withAlpha(.12f))
                        .border(1.dp, V2Amber.copy(alpha = .55f), RoundedCornerShape(50))
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                ) {
                    KText("PRO", size = 7.sp, weight = FontWeight.Black, color = V2GoldText)
                }
            }
            Spacer(Modifier.weight(1f))
            if (!ecosystem) {
                KText("${page + 1} / ${steps.size}", size = 11.sp, color = V2Cyan)
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
                KText("تخطي", size = if (ecosystem) 14.sp else 10.sp, color = V2Soft)
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
                    when (steps[index]) {
                        OnboardingStep.WELCOME -> OnboardingWelcomePage()
                        OnboardingStep.COMPANY -> OnboardingCompanyPage()
                        OnboardingStep.ECOSYSTEM -> OnboardingEcosystemPage()
                        OnboardingStep.FOUNDER -> OnboardingFounderPage()
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(steps.size) { index ->
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
                text = if (page < steps.lastIndex) "التالي" else "ابدأ الآن",
                onClick = { if (page < steps.lastIndex) page++ else onFinish() },
                icon = if (ecosystem) Icons.Rounded.ChevronRight else null,
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
        LoginCosmicScene(232.dp, reveal = 1f)
        Spacer(Modifier.height(2.dp))
        KText("مرحبًا بك في", size = 12.sp, color = V2Cyan, align = TextAlign.Center)
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            LoginLaurel(mirror = false)
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
                style = TextStyle(fontFamily = V2Font, fontWeight = FontWeight.Black, fontSize = 28.sp),
            )
            LoginLaurel(mirror = true)
        }
        Spacer(Modifier.height(2.dp))
        KText(
            "مساعدك العربي لإدارة جهازك بأمان وكفاءة أعلى",
            size = 11.sp,
            color = V2Soft,
            align = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OnboardingFeature(Icons.Rounded.Shield, "حماية أذكى", V2Cyan)
            OnboardingFeature(Icons.Rounded.Bolt, "أداء أعلى", V2Pink)
            OnboardingFeature(Icons.Rounded.Layers, "إدارة شاملة", V2Violet)
        }
        Spacer(Modifier.height(7.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OnboardingChip(Icons.Rounded.Language, "عربي أولًا")
            OnboardingChip(Icons.Rounded.PersonOff, "ضيف اختياري")
            OnboardingChip(Icons.Rounded.CloudOff, "أدوات محلية")
        }
        Spacer(Modifier.height(11.dp))
        OnboardingPromiseCard()
        Spacer(Modifier.height(7.dp))
        OnboardingStatBand()
    }
}

@Composable
private fun OnboardingCompanyPage() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.fillMaxWidth().height(240.dp)) {
            V2DeerHeader(
                Modifier.align(Alignment.TopEnd)
                    .offset(x = 34.dp, y = (-8).dp)
                    .size(width = 232.dp, height = 240.dp)
                    .graphicsLayer { alpha = .30f },
            )
            Box(
                Modifier.align(Alignment.CenterStart).padding(start = 6.dp).size(124.dp),
                contentAlignment = Alignment.Center,
            ) {
                HeaderHalo(Modifier.matchParentSize(), color = V2Cyan)
                BrandSymbol(size = 86.dp)
            }
        }
        OnboardingCompanyDetails()
        Spacer(Modifier.height(8.dp))
        OnboardingStatBand()
        Spacer(Modifier.height(8.dp))
        OnboardingJourneyStrip()
    }
}

@Composable
private fun OnboardingCompanyDetails() {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            LoginLaurel(mirror = false)
            Text(
                "عن KNOUX",
                style = TextStyle(fontFamily = V2Font, fontWeight = FontWeight.Black, fontSize = 24.sp, color = Color.White),
            )
            LoginLaurel(mirror = true)
        }
        Spacer(Modifier.height(5.dp))
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
    }
}

@Composable
private fun OnboardingFounderPage() {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginCosmicScene(232.dp, reveal = 1f)
        V2GlassCard(
            Modifier.fillMaxWidth(),
            padding = 13.dp,
            radius = 22.dp,
            glow = V2Cyan,
            borderAlpha = .48f,
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                KText("FOUNDER", size = 10.sp, weight = FontWeight.Bold, color = V2Cyan, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    LoginLaurel(mirror = false)
                    Text(
                        "Eng. Sadek Elgazar",
                        style = TextStyle(
                            fontSize = 21.sp, color = Color.White, fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center, textDirection = TextDirection.Ltr,
                        ),
                    )
                    LoginLaurel(mirror = true)
                }
                Spacer(Modifier.height(3.dp))
                KText(
                    "FOUNDER & SOFTWARE ENGINEER",
                    size = 8.sp,
                    weight = FontWeight.Bold,
                    color = V2Soft,
                    letterSpacing = 1.2.sp,
                )
                Spacer(Modifier.height(9.dp))
                Box(
                    Modifier
                        .fillMaxWidth(.72f)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, V2Cyan.copy(alpha = .55f), Color.Transparent)
                            )
                        )
                )
                Spacer(Modifier.height(8.dp))
                KText("KNOUX AI — A Product of KNOUX", size = 10.sp, weight = FontWeight.Bold, color = V2Cyan)
            }
        }
        Spacer(Modifier.height(12.dp))
        OnboardingVisionCard()
        Spacer(Modifier.height(10.dp))
        OnboardingValueRow()
        Spacer(Modifier.height(9.dp))
        OnboardingStatBand()
    }
}

@Composable
private fun OnboardingFeature(icon: ImageVector, label: String, accent: Color) {
    Column(
        Modifier
            .width(96.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(accent.withAlpha(.13f), Color.White.withAlpha(.02f))))
            .border(1.dp, accent.withAlpha(.36f), RoundedCornerShape(16.dp))
            .padding(horizontal = 6.dp, vertical = 9.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        V2IconTile(icon, accent, size = 30.dp, iconSize = 16.dp)
        Spacer(Modifier.height(5.dp))
        KText(label, size = 8.sp, weight = FontWeight.Bold, color = Color.White, align = TextAlign.Center)
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

/* ------------------------------------------------------------------ *
 * Lower-page onboarding content.
 *
 * These blocks describe the real product structure (10 sections, 100
 * navigable service cards) and its review-before-execute path. They do not
 * display device measurements, scan results or entitlement claims.
 * ------------------------------------------------------------------ */

@Composable
private fun OnboardingPromiseCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 20.dp,
        glow = V2Cyan,
        borderAlpha = .34f,
    ) {
        Column {
            V2SectionLabel("ماذا تجد داخل KNOUX", trailing = "أدوات جهاز")
            Spacer(Modifier.height(7.dp))
            OnboardingPromiseRow(
                Icons.Rounded.CleaningServices, V2Cyan,
                "فحص وتنظيف", "قراءات من مساحة جهازك، بدون أرقام تقديرية",
            )
            Spacer(Modifier.height(6.dp))
            OnboardingPromiseRow(
                Icons.Rounded.Lock, V2Pink,
                "خصوصية وأذونات", "لا وصول خارج ما تسمح به أنت",
            )
            Spacer(Modifier.height(6.dp))
            OnboardingPromiseRow(
                Icons.Rounded.BarChart, V2Green,
                "تقارير وتحسين", "نتائج مفهومة وقابلة للمراجعة",
            )
        }
    }
}

@Composable
private fun OnboardingPromiseRow(
    icon: ImageVector,
    accent: Color,
    title: String,
    body: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        V2IconTile(icon, accent, size = 28.dp, iconSize = 15.dp)
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            KText(title, size = 9.6.sp, weight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(1.dp))
            KText(body, size = 7.8.sp, color = V2Muted, lineHeight = 11.sp)
        }
    }
}

@Composable
private fun OnboardingStatBand() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        OnboardingStatTile("10", "أقسام ذكية", V2Cyan, Modifier.weight(1f))
        OnboardingStatTile("100", "خدمة قابلة للفتح", V2Violet, Modifier.weight(1f))
        OnboardingStatTile("عربي", "واجهة أولًا", V2Pink, Modifier.weight(1f))
    }
}

@Composable
private fun OnboardingStatTile(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(accent.withAlpha(.13f), Color.White.withAlpha(.02f))))
            .border(1.dp, accent.withAlpha(.30f), RoundedCornerShape(14.dp))
            .padding(vertical = 7.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        KText(value, size = 15.sp, weight = FontWeight.Black, color = accent, maxLines = 1)
        Spacer(Modifier.height(1.dp))
        KText(label, size = 7.sp, color = V2Soft, align = TextAlign.Center, maxLines = 1)
    }
}

@Composable
private fun OnboardingJourneyStrip() {
    val steps = listOf("النطاق", "القراءة", "المراجعة", "النتيجة")
    val accents = listOf(V2Cyan, V2Violet, V2Amber, V2Green)
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 20.dp,
        glow = V2Violet,
        borderAlpha = .32f,
    ) {
        Column {
            V2SectionLabel("مسار الخدمة", trailing = "قبل أي إجراء")
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                steps.forEachIndexed { index, step ->
                    val accent = accents[index]
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accent.withAlpha(.07f))
                            .border(1.dp, accent.withAlpha(.26f), RoundedCornerShape(12.dp))
                            .padding(vertical = 7.dp, horizontal = 3.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(accent.withAlpha(.16f))
                                .border(1.dp, accent.withAlpha(.52f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            KText("${index + 1}", size = 8.5.sp, weight = FontWeight.Black, color = accent)
                        }
                        Spacer(Modifier.height(5.dp))
                        KText(step, size = 8.4.sp, weight = FontWeight.Bold, color = Color.White, maxLines = 1)
                    }
                }
            }
            Spacer(Modifier.height(7.dp))
            KText(
                "كل خطوة تعرض ما قرأته فعلًا، والقرار النهائي لك.",
                size = 7.8.sp,
                color = V2Muted,
                align = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun OnboardingVisionCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .34f,
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Icon(Icons.Rounded.AutoAwesome, null, tint = V2Amber, modifier = Modifier.size(13.dp))
                KText("الرؤية", size = 9.sp, weight = FontWeight.Bold, color = V2Amber, letterSpacing = 1.sp)
            }
            Spacer(Modifier.height(8.dp))
            KText(
                "«أدوات تفهم جهازك، وتترك القرار لك.»",
                size = 15.sp,
                weight = FontWeight.Bold,
                color = Color.White,
                align = TextAlign.Center,
                lineHeight = 24.sp,
            )
            Spacer(Modifier.height(6.dp))
            KText(
                "نبني أدوات عربية تقرأ من Android مباشرة، وتعرض ما وجدته قبل أي خطوة.",
                size = 9.sp,
                color = V2Soft,
                align = TextAlign.Center,
                lineHeight = 14.sp,
            )
            Spacer(Modifier.height(9.dp))
            Box(
                Modifier
                    .width(92.dp)
                    .height(1.5.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, V2Amber.copy(alpha = .75f), Color.Transparent)
                        )
                    )
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "Eng. Sadek Elgazar",
                modifier = Modifier.graphicsLayer { rotationZ = -2f },
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Cursive,
                    fontStyle = FontStyle.Italic,
                    color = V2Cyan.copy(alpha = .9f),
                    letterSpacing = .4.sp,
                    textDirection = TextDirection.Ltr,
                ),
            )
        }
    }
}

@Composable
private fun OnboardingValueRow() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        OnboardingValue(Icons.Rounded.CloudOff, "أدوات محلية", V2Cyan, Modifier.weight(1f))
        OnboardingValue(Icons.Rounded.Shield, "القرار لك", V2Violet, Modifier.weight(1f))
        OnboardingValue(Icons.Rounded.Language, "عربي أولًا", V2Pink, Modifier.weight(1f))
    }
}

@Composable
private fun OnboardingValue(
    icon: ImageVector,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = .04f))
            .border(1.dp, accent.withAlpha(.24f), RoundedCornerShape(14.dp))
            .padding(horizontal = 5.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        KText(label, size = 8.sp, weight = FontWeight.Bold, color = V2Soft, maxLines = 1)
    }
}
