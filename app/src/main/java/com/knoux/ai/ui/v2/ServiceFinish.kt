package com.knoux.ai.ui.v2

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/* ------------------------------------------------------------------ *
 * KNOUX AI Pro — reusable service finishing layer
 *
 * One header, one hero art system, one sub-service navigation grid and
 * one honest capability vocabulary shared by the ten owner blueprints.
 * It only renders presentation: every screen keeps its existing data
 * providers, dialogs and Android-side actions.
 * ------------------------------------------------------------------ */

/** Navigation provided once by MainActivity so any service screen can open an internal tool. */
val LocalServiceNavigate = staticCompositionLocalOf<(String) -> Unit> { {} }

enum class ServiceState { LIVE, PERMISSION, RESTRICTED, PREVIEW, READY }

data class ServiceSubItem(
    val title: String,
    val subtitle: String,
    val route: String,
    val icon: ImageVector,
    val accent: Color = V2Cyan,
    val state: ServiceState = ServiceState.READY,
    val proOnly: Boolean = false,
    @DrawableRes val art: Int? = null,
)

data class KnouxServiceSpec(
    val key: String,
    val order: String,
    val title: String,
    val english: String,
    val tagline: String,
    val accent: Color,
    val accent2: Color,
    val glyph: ImageVector,
    val chips: List<String>,
    val subItems: List<ServiceSubItem>,
    val pro: Boolean = false,
) {
    val hasProServices: Boolean get() = pro || subItems.any { it.proOnly }
}

private fun live(
    title: String,
    subtitle: String,
    route: String,
    icon: ImageVector,
    accent: Color,
    proOnly: Boolean = false,
) = ServiceSubItem(title, subtitle, route, icon, accent, ServiceState.LIVE, proOnly)

private fun preview(
    title: String,
    subtitle: String,
    route: String,
    icon: ImageVector,
    accent: Color,
    proOnly: Boolean = false,
) = ServiceSubItem(title, subtitle, route, icon, accent, ServiceState.PREVIEW, proOnly)

object ServiceSpecs {

    val smartClean = KnouxServiceSpec(
        key = "smartclean",
        order = "01",
        title = "التنظيف الذكي",
        english = "AI SMART CLEAN",
        tagline = "مراجعة الكاش والملفات المؤقتة قبل أي حذف",
        accent = V2Cyan,
        accent2 = V2Pink,
        glyph = Icons.Rounded.CleaningServices,
        chips = listOf("كاش KNOUX فقط", "تأكيد قبل الحذف"),
        subItems = CleanTools.items,
    )

    val duplicates = KnouxServiceSpec(
        key = "duplicates",
        order = "02",
        title = "الملفات المكررة",
        english = "DUPLICATE FINDER",
        tagline = "مطابقة الصور والفيديو ببصمة SHA-256",
        accent = V2Violet,
        accent2 = V2Cyan,
        glyph = Icons.Rounded.Layers,
        chips = listOf("MediaStore", "حذف عبر تأكيد Android"),
        subItems = SectionTools.items("duplicates"),
    )

    val residue = KnouxServiceSpec(
        key = "residue",
        order = "03",
        title = "بقايا التطبيقات",
        english = "APP RESIDUE",
        tagline = "كاش KNOUX وملفاته المؤقتة، وما يمنعه Android بوضوح",
        accent = V2Pink,
        accent2 = V2Violet,
        glyph = Icons.Rounded.Folder,
        chips = listOf("قراءة مباشرة", "حدود Scoped Storage"),
        subItems = SectionTools.items("residue"),
    )

    val privacy = KnouxServiceSpec(
        key = "privacy",
        order = "04",
        title = "الخصوصية الذكية",
        english = "PRIVACY SWEEP",
        tagline = "أذونات حساسة مقروءة من PackageManager على هذا الهاتف",
        accent = V2Amber,
        accent2 = V2Violet,
        glyph = Icons.Rounded.Shield,
        chips = listOf("PackageManager", "إعدادات Android الرسمية"),
        subItems = SectionTools.items("privacy"),
    )

    val deepScan = KnouxServiceSpec(
        key = "deepscan",
        order = "05",
        title = "الفحص العميق",
        english = "DEEP SCAN",
        tagline = "قياس التخزين والتطبيقات والخصوصية والبطارية في مسار واحد",
        accent = V2Cyan,
        accent2 = V2Pink,
        glyph = Icons.Rounded.Radar,
        chips = listOf("قراءة لحظية", "سجل محلي"),
        subItems = SectionTools.items("deepscan"),
    )

    val reports = KnouxServiceSpec(
        key = "reports",
        order = "06",
        title = "التقارير الذكية",
        english = "REPORTS & INSIGHTS",
        tagline = "ملخص صحة الجهاز وتطوره من قراءات هذا الهاتف",
        accent = V2Cyan,
        accent2 = V2Amber,
        glyph = Icons.Rounded.BarChart,
        chips = listOf("قراءة مباشرة", "بدون رفع سحابي"),
        subItems = SectionTools.items("reports"),
    )

    val appManager = KnouxServiceSpec(
        key = "appmanager",
        order = "07",
        title = "مدير التطبيقات",
        english = "APP MANAGER",
        tagline = "التطبيقات المثبتة وأحجامها وأذوناتها على هذا الهاتف",
        accent = V2Violet,
        accent2 = V2Cyan,
        glyph = Icons.Rounded.Apps,
        chips = listOf("PackageManager", "إعدادات النظام"),
        subItems = SectionTools.items("appmanager"),
    )

    val storage = KnouxServiceSpec(
        key = "storage",
        order = "08",
        title = "تحليل التخزين",
        english = "STORAGE ANALYZER",
        tagline = "StatFs يوضح المساحة الكلية والمستخدمة والمتاحة",
        accent = V2Cyan,
        accent2 = V2Blue,
        glyph = Icons.Rounded.Storage,
        chips = listOf("StatFs", "قراءة مباشرة"),
        subItems = SectionTools.items("storage"),
    )

    val battery = KnouxServiceSpec(
        key = "battery",
        order = "09",
        title = "توفير البطارية",
        english = "BATTERY SAVER",
        tagline = "مستوى البطارية والحرارة والحالة من BatteryManager",
        accent = V2Green,
        accent2 = V2Cyan,
        glyph = Icons.Rounded.BatteryFull,
        chips = listOf("BatteryManager", "لا تغيير تلقائي"),
        subItems = SectionTools.items("battery"),
    )

    val ai = KnouxServiceSpec(
        key = "ai",
        order = "10",
        title = "تحسين الأداء",
        english = "AI OPTIMIZATION",
        tagline = "توصيات مبنية على قراءة التخزين والبطارية والأذونات",
        accent = V2Pink,
        accent2 = V2Violet,
        glyph = Icons.Rounded.AutoAwesome,
        chips = listOf("توصيات محلية", "تنفيذ يدوي"),
        subItems = SectionTools.items("ai"),
    )
}

/* ------------------------------------------------------------------ *
 * Header banner
 * ------------------------------------------------------------------ */

@Composable
fun ServiceBanner(
    spec: KnouxServiceSpec,
    status: String,
    statusColor: Color,
    modifier: Modifier = Modifier,
    metric: String? = null,
) {
    val proActive = LocalProActive.current
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    val iconScale by animateFloatAsState(
        targetValue = if (entered) 1f else .58f,
        animationSpec = spring(dampingRatio = .52f, stiffness = 210f),
        label = "sectionIconScale",
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(520),
        label = "sectionIconAlpha",
    )
    val iconTilt by animateFloatAsState(
        targetValue = if (entered) 0f else -14f,
        animationSpec = spring(dampingRatio = .55f, stiffness = 180f),
        label = "sectionIconTilt",
    )
    val loop = rememberInfiniteTransition(label = "sectionHeaderLoop")
    val pulse by loop.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sectionPulse",
    )
    val orbit by loop.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing)),
        label = "sectionOrbit",
    )

    V2GlassCard(
        modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 24.dp,
        glow = spec.accent,
        borderAlpha = .55f,
    ) {
        Box(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .width(210.dp)
                    .height(110.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(spec.accent.copy(alpha = .18f), Color.Transparent)
                        )
                    )
            )
            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    KText(
                        "KNOUX AI",
                        size = 7.5.sp,
                        weight = FontWeight.Black,
                        color = spec.accent,
                        letterSpacing = 1.8.sp,
                    )
                    Spacer(Modifier.width(5.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(spec.accent.withAlpha(.14f))
                            .border(1.dp, spec.accent.withAlpha(.37f), RoundedCornerShape(50))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        KText(spec.order, size = 7.sp, weight = FontWeight.Bold, color = spec.accent)
                    }
                    if (proActive) {
                        Spacer(Modifier.width(5.dp))
                        if (spec.hasProServices) ProBadge() else ProPill()
                    }
                    Spacer(Modifier.weight(1f))
                    ServiceStatusChip(status, statusColor)
                }
                Spacer(Modifier.height(3.dp))
                Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    V2DeerHeader(
                        Modifier
                            .align(if (LocalLayoutDirection.current == LayoutDirection.Rtl) Alignment.TopStart else Alignment.TopEnd)
                            .size(width = 78.dp, height = 76.dp)
                            .graphicsLayer {
                                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 0f)
                                scaleX = 1.24f
                                scaleY = 1.24f
                            },
                        animated = false,
                    )
                    Canvas(Modifier.size(106.dp)) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.minDimension / 2f
                        drawCircle(
                            color = spec.accent.copy(alpha = .10f + .20f * pulse),
                            radius = radius,
                            center = center,
                            style = Stroke(1.4.dp.toPx()),
                        )
                        drawCircle(
                            color = spec.accent2.copy(alpha = .16f),
                            radius = radius * .85f,
                            center = center,
                            style = Stroke(.9.dp.toPx()),
                        )
                        val angle = Math.toRadians(orbit.toDouble())
                        drawCircle(
                            color = Color.White.copy(alpha = .85f),
                            radius = 2.1.dp.toPx(),
                            center = Offset(
                                center.x + (radius * .92f * cos(angle)).toFloat(),
                                center.y + (radius * .92f * sin(angle)).toFloat(),
                            ),
                        )
                    }
                    ServiceArt(
                        spec,
                        Modifier
                            .size(92.dp)
                            .graphicsLayer {
                                scaleX = iconScale
                                scaleY = iconScale
                                alpha = iconAlpha
                                rotationZ = iconTilt
                            },
                    )
                }
                Spacer(Modifier.height(2.dp))
                KText(spec.title, size = 23.sp, weight = FontWeight.Black, color = Color.White, modifier = Modifier.fillMaxWidth(), align = TextAlign.Center)
                KText(
                    spec.english,
                    size = 7.6.sp,
                    weight = FontWeight.Bold,
                    color = spec.accent2,
                    letterSpacing = 1.4.sp,
                    modifier = Modifier.fillMaxWidth(),
                    align = TextAlign.Center,
                )
                Spacer(Modifier.height(3.dp))
                KText(
                    spec.tagline,
                    size = 8.6.sp,
                    lineHeight = 12.sp,
                    color = V2Soft,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    align = TextAlign.Center,
                )
                if (metric != null) {
                    Spacer(Modifier.height(5.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        ServiceGlassChip(metric)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    spec.chips.take(2).forEach { chip ->
                        Box(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .background(Color.White.withAlpha(.045f))
                                .border(1.dp, spec.accent.withAlpha(.20f), RoundedCornerShape(9.dp))
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            KText(chip, size = 7.4.sp, color = V2Soft, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceStatusChip(text: String, accent: Color) {
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(accent.withAlpha(.13f))
            .border(1.dp, accent.withAlpha(.42f), RoundedCornerShape(50))
            .padding(horizontal = 7.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(4.dp).clip(androidx.compose.foundation.shape.CircleShape).background(accent))
        Spacer(Modifier.width(4.dp))
        KText(text, size = 7.4.sp, weight = FontWeight.Bold, color = accent, maxLines = 1)
    }
}

@Composable
private fun ServiceGlassChip(text: String) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.withAlpha(.05f))
            .border(1.dp, Color.White.withAlpha(.12f), RoundedCornerShape(50))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        KText(text, size = 7.4.sp, weight = FontWeight.SemiBold, color = V2Soft, maxLines = 1)
    }
}

/* ------------------------------------------------------------------ *
 * Service-specific hero art (drawn, no bitmap, no fake data)
 * ------------------------------------------------------------------ */

@Composable
fun ServiceArt(spec: KnouxServiceSpec, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)
        val radius = minOf(w, h) / 2f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(spec.accent.withAlpha(.34f), spec.accent2.withAlpha(.12f), Color.Transparent),
                center = center,
                radius = radius,
            ),
            radius = radius,
            center = center,
        )
        drawCircle(spec.accent.withAlpha(.34f), radius * .93f, center, style = Stroke(1.dp.toPx()))
        drawCircle(spec.accent2.withAlpha(.28f), radius * .76f, center, style = Stroke(.8.dp.toPx()))
        drawCircle(Color.White.withAlpha(.035f), radius * .62f, center)
        drawCircle(spec.accent2.withAlpha(.12f), radius * .42f, center)

        drawServiceMotif(spec, center, radius * .58f)

        // Owner-approved sparkle rhythm around the orb.
        listOf(
            0.30f to (center.x + radius * .68f),
            -0.5f to (center.x + radius * .12f),
            2.2f to (center.x - radius * .55f),
        ).forEachIndexed { index, (angle, x) ->
            val y = center.y + sin(angle) * radius * .58f
            drawCircle(
                color = (if (index % 2 == 0) Color.White else spec.accent2).copy(alpha = .55f),
                radius = (1.1f + index * .35f).dp.toPx(),
                center = Offset(x, y),
            )
        }
    }
}

private fun DrawScope.drawServiceMotif(spec: KnouxServiceSpec, c: Offset, r: Float) {
    val bright = Color.White.copy(alpha = .92f)
    val accent = spec.accent
    val accent2 = spec.accent2
    when (spec.key) {
        "smartclean" -> rotate(-28f, c) {
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = .95f), accent)),
                topLeft = Offset(c.x - r * .10f, c.y - r * .84f),
                size = Size(r * .20f, r * .88f),
                cornerRadius = CornerRadius(r * .10f),
            )
            drawRoundRect(
                brush = Brush.linearGradient(listOf(accent, accent2)),
                topLeft = Offset(c.x - r * .38f, c.y + r * .02f),
                size = Size(r * .76f, r * .46f),
                cornerRadius = CornerRadius(r * .10f, r * .10f),
            )
            for (i in 0..2) {
                drawLine(
                    color = Color.White.copy(alpha = .30f),
                    start = Offset(c.x - r * .24f + i * r * .20f, c.y + r * .10f),
                    end = Offset(c.x - r * .24f + i * r * .20f, c.y + r * .40f),
                    strokeWidth = 1.2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }

        "duplicates" -> {
            for (i in 2 downTo 0) {
                val dx = (i - 1) * r * .18f
                val dy = (i - 1) * r * .16f
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = .12f + i * .05f), accent.copy(alpha = .30f))
                    ),
                    topLeft = Offset(c.x - r * .48f + dx, c.y - r * .52f + dy),
                    size = Size(r * .78f, r * .88f),
                    cornerRadius = CornerRadius(r * .12f),
                )
                drawRoundRect(
                    color = if (i == 0) bright.copy(alpha = .72f) else accent2.copy(alpha = .45f),
                    topLeft = Offset(c.x - r * .48f + dx, c.y - r * .52f + dy),
                    size = Size(r * .78f, r * .88f),
                    cornerRadius = CornerRadius(r * .12f),
                    style = Stroke(1.2.dp.toPx()),
                )
            }
            drawCircle(accent, r * .10f, Offset(c.x + r * .30f, c.y - r * .34f))
        }

        "residue" -> {
            for (i in 0..2) {
                drawArc(
                    brush = Brush.sweepGradient(listOf(accent, accent2, Color.Transparent, accent)),
                    startAngle = 25f + i * 55f,
                    sweepAngle = 78f,
                    useCenter = false,
                    topLeft = Offset(c.x - r * (.92f - i * .15f), c.y - r * (.92f - i * .15f)),
                    size = Size(r * 2 * (.92f - i * .15f), r * 2 * (.92f - i * .15f)),
                    style = Stroke(2.dp.toPx(), cap = StrokeCap.Round),
                )
            }
            listOf(
                Offset(c.x - r * .30f, c.y + r * .44f),
                Offset(c.x + r * .08f, c.y + r * .56f),
                Offset(c.x + r * .42f, c.y + r * .30f),
            ).forEachIndexed { i, p -> drawCircle(if (i == 1) accent2 else accent, (2.2f - i * .4f).dp.toPx(), p) }
        }

        "privacy" -> {
            val shield = Path().apply {
                moveTo(c.x, c.y - r * .82f)
                lineTo(c.x + r * .62f, c.y - r * .48f)
                lineTo(c.x + r * .52f, c.y + r * .24f)
                quadraticBezierTo(c.x + r * .38f, c.y + r * .62f, c.x, c.y + r * .84f)
                quadraticBezierTo(c.x - r * .38f, c.y + r * .62f, c.x - r * .52f, c.y + r * .24f)
                lineTo(c.x - r * .62f, c.y - r * .48f)
                close()
            }
            drawPath(
                shield,
                brush = Brush.verticalGradient(listOf(accent.copy(alpha = .42f), accent2.copy(alpha = .50f))),
            )
            drawPath(
                shield,
                brush = Brush.verticalGradient(listOf(bright, accent)),
                style = Stroke(1.7.dp.toPx(), join = StrokeJoin.Round),
            )
            val check = Path().apply {
                moveTo(c.x - r * .32f, c.y + r * .02f)
                lineTo(c.x - r * .08f, c.y + r * .26f)
                lineTo(c.x + r * .38f, c.y - r * .28f)
            }
            drawPath(check, bright, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }

        "deepscan" -> {
            drawCircle(accent.copy(alpha = .16f), r * .60f, c)
            drawCircle(accent.copy(alpha = .55f), r * .60f, c, style = Stroke(1.1.dp.toPx()))
            drawCircle(accent2.copy(alpha = .55f), r * .36f, c, style = Stroke(1.dp.toPx()))
            drawLine(
                brush = Brush.linearGradient(listOf(accent, Color.Transparent)),
                start = c,
                end = Offset(c.x + r * .52f, c.y - r * .38f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
            val sweep = Path().apply {
                moveTo(c.x, c.y)
                lineTo(c.x + r * .60f, c.y)
                arcTo(
                    androidx.compose.ui.geometry.Rect(c.x - r * .60f, c.y - r * .60f, c.x + r * .60f, c.y + r * .60f),
                    -38f,
                    -44f,
                    false,
                )
                close()
            }
            drawPath(sweep, Brush.radialGradient(listOf(accent.copy(alpha = .38f), Color.Transparent), center = c, radius = r * .60f))
            drawCircle(bright, r * .07f, c)
            drawCircle(accent2, r * .055f, Offset(c.x - r * .34f, c.y + r * .30f))
        }

        "reports" -> {
            val base = c.y + r * .58f
            val heights = listOf(.42f, .72f, .54f, .94f)
            heights.forEachIndexed { index, hgt ->
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(accent, accent2.copy(alpha = .35f))),
                    topLeft = Offset(c.x - r * .72f + index * r * .40f, base - r * hgt),
                    size = Size(r * .24f, r * hgt),
                    cornerRadius = CornerRadius(r * .08f),
                )
            }
            val trend = Path().apply {
                moveTo(c.x - r * .64f, c.y + r * .10f)
                lineTo(c.x - r * .26f, c.y - r * .20f)
                lineTo(c.x + r * .12f, c.y - r * .02f)
                lineTo(c.x + r * .56f, c.y - r * .62f)
            }
            drawPath(trend, bright, style = Stroke(1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawCircle(bright, r * .06f, Offset(c.x + r * .56f, c.y - r * .62f))
        }

        "appmanager" -> {
            for (row in 0..2) for (col in 0..2) {
                val x = c.x - r * .58f + col * r * .42f
                val y = c.y - r * .58f + row * r * .42f
                val alpha = if ((row + col) % 2 == 0) .62f else .30f
                drawRoundRect(
                    brush = Brush.linearGradient(listOf(accent.copy(alpha = alpha), accent2.copy(alpha = alpha * .55f))),
                    topLeft = Offset(x, y),
                    size = Size(r * .30f, r * .30f),
                    cornerRadius = CornerRadius(r * .075f),
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = .24f),
                    topLeft = Offset(x, y),
                    size = Size(r * .30f, r * .30f),
                    cornerRadius = CornerRadius(r * .075f),
                    style = Stroke(.7.dp.toPx()),
                )
            }
        }

        "storage" -> {
            for (i in 0..2) {
                val yy = c.y + r * .44f - i * r * .34f
                drawOval(
                    brush = Brush.horizontalGradient(listOf(accent.copy(alpha = .18f), accent2.copy(alpha = .34f))),
                    topLeft = Offset(c.x - r * .58f, yy - r * .16f),
                    size = Size(r * 1.16f, r * .32f),
                )
                drawOval(
                    color = if (i == 2) bright.copy(alpha = .72f) else accent.copy(alpha = .55f),
                    topLeft = Offset(c.x - r * .58f, yy - r * .16f),
                    size = Size(r * 1.16f, r * .32f),
                    style = Stroke(1.1.dp.toPx()),
                )
                if (i < 2) {
                    drawRect(
                        brush = Brush.verticalGradient(listOf(accent.copy(alpha = .22f), accent.copy(alpha = .10f))),
                        topLeft = Offset(c.x - r * .58f, yy),
                        size = Size(r * 1.16f, r * .18f),
                    )
                }
            }
            drawCircle(accent2.copy(alpha = .65f), r * .055f, Offset(c.x + r * .24f, c.y + r * .40f))
        }

        "battery" -> {
            val bodyTop = c.y - r * .44f
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(accent.copy(alpha = .30f), accent2.copy(alpha = .20f))),
                topLeft = Offset(c.x - r * .52f, bodyTop),
                size = Size(r * .94f, r * .88f),
                cornerRadius = CornerRadius(r * .16f),
            )
            drawRoundRect(
                color = bright.copy(alpha = .70f),
                topLeft = Offset(c.x - r * .52f, bodyTop),
                size = Size(r * .94f, r * .88f),
                cornerRadius = CornerRadius(r * .16f),
                style = Stroke(1.6.dp.toPx()),
            )
            drawRoundRect(
                color = accent.copy(alpha = .80f),
                topLeft = Offset(c.x + r * .44f, c.y - r * .18f),
                size = Size(r * .12f, r * .36f),
                cornerRadius = CornerRadius(r * .05f),
            )
            val bolt = Path().apply {
                moveTo(c.x - r * .02f, c.y - r * .30f)
                lineTo(c.x - r * .28f, c.y + r * .04f)
                lineTo(c.x - r * .07f, c.y + r * .04f)
                lineTo(c.x - r * .13f, c.y + r * .32f)
                lineTo(c.x + r * .26f, c.y - r * .10f)
                lineTo(c.x + r * .05f, c.y - r * .10f)
                close()
            }
            drawPath(bolt, Brush.verticalGradient(listOf(Color.White, accent2)))
        }

        "ai" -> {
            val star = Path().apply {
                moveTo(c.x, c.y - r * .82f)
                quadraticBezierTo(c.x + r * .12f, c.y - r * .14f, c.x + r * .80f, c.y)
                quadraticBezierTo(c.x + r * .12f, c.y + r * .14f, c.x, c.y + r * .82f)
                quadraticBezierTo(c.x - r * .12f, c.y + r * .14f, c.x - r * .80f, c.y)
                quadraticBezierTo(c.x - r * .12f, c.y - r * .14f, c.x, c.y - r * .82f)
                close()
            }
            drawPath(star, Brush.radialGradient(listOf(Color.White, accent, accent2), center = c, radius = r * .85f))
            drawCircle(accent2.copy(alpha = .55f), r * .62f, c, style = Stroke(.9.dp.toPx()))
            drawCircle(bright, r * .06f, Offset(c.x + r * .58f, c.y - r * .44f))
            drawCircle(accent, r * .045f, Offset(c.x - r * .48f, c.y + r * .46f))
        }

        else -> {
            drawCircle(accent.copy(alpha = .40f), r * .62f, c)
            drawCircle(bright, r * .30f, c, style = Stroke(1.4.dp.toPx()))
        }
    }
}

/* ------------------------------------------------------------------ *
 * Sub-service grid
 * ------------------------------------------------------------------ */

@Composable
fun ServiceSubGrid(
    spec: KnouxServiceSpec,
    modifier: Modifier = Modifier,
    title: String = "أدوات الخدمة",
) {
    val navigate = LocalServiceNavigate.current
    val proActive = LocalProActive.current
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            KText(title, size = 15.sp, weight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            if (proActive && spec.hasProServices) {
                ProBadge()
                Spacer(Modifier.width(5.dp))
            }
            KText("${spec.subItems.size} أدوات", size = 10.sp, color = V2Muted)
        }
        Spacer(Modifier.height(7.dp))
        // Exactly the Home grid: ten services in two rows of five, 91.dp tall.
        spec.subItems.chunked(5).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { item ->
                    SectionServiceTile(item, Modifier.weight(1f)) { navigate(item.route) }
                }
                repeat(5 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun SectionServiceTile(item: ServiceSubItem, modifier: Modifier, onClick: () -> Unit) {
    val proActive = LocalProActive.current
    V2GlassCard(
        modifier = modifier
            .height(91.dp)
            .clickable(onClick = onClick),
        padding = 5.dp,
        radius = 15.dp,
        glow = item.accent,
        borderAlpha = .36f,
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (item.art != null) {
                V2RasterTile(image = item.art, size = 52.dp, accent = item.accent)
            } else {
                V2NeonGlyph(item.icon, item.accent, size = 50.dp)
            }
            Spacer(Modifier.height(2.dp))
            KText(
                item.title,
                size = 8.2.sp,
                weight = FontWeight.SemiBold,
                color = Color.White,
                lineHeight = 10.5.sp,
                align = TextAlign.Center,
                maxLines = 2,
            )
        }
        if (proActive && item.proOnly) {
            ProBadge(Modifier.align(Alignment.TopEnd))
        }
    }
}

/* ------------------------------------------------------------------ *
 * Shared review / capability cards
 * ------------------------------------------------------------------ */

@Composable
fun ServiceCapabilityCard(
    title: String,
    body: String,
    state: ServiceState,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val accent = when (state) {
        ServiceState.LIVE -> V2Green
        ServiceState.PERMISSION, ServiceState.RESTRICTED -> V2Amber
        ServiceState.PREVIEW -> V2Violet
        ServiceState.READY -> V2Cyan
    }
    val stateText = when (state) {
        ServiceState.LIVE -> "متحقق من الجهاز"
        ServiceState.PERMISSION -> "يحتاج صلاحية"
        ServiceState.RESTRICTED -> "مقيد من Android"
        ServiceState.PREVIEW -> "معاينة غير مرتبطة"
        ServiceState.READY -> "جاهز"
    }
    V2GlassCard(modifier.fillMaxWidth(), padding = 10.dp, radius = 17.dp, glow = accent, borderAlpha = .30f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(
                when (state) {
                    ServiceState.LIVE -> Icons.Rounded.Verified
                    ServiceState.PREVIEW -> Icons.Rounded.Info
                    else -> Icons.Rounded.WarningAmber
                },
                accent,
                size = 33.dp,
                iconSize = 17.dp,
            )
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KText(title, size = 9.6.sp, weight = FontWeight.Bold)
                    Spacer(Modifier.width(5.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(accent.withAlpha(.12f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        KText(stateText, size = 6.4.sp, weight = FontWeight.Bold, color = accent)
                    }
                }
                Spacer(Modifier.height(2.dp))
                KText(body, size = 7.6.sp, lineHeight = 11.sp, color = V2Soft)
            }
            if (actionLabel != null && onAction != null) {
                Spacer(Modifier.width(5.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(accent.withAlpha(.16f))
                        .border(1.dp, accent.withAlpha(.38f), RoundedCornerShape(9.dp))
                        .clickable(onClick = onAction)
                        .padding(horizontal = 7.dp, vertical = 5.dp)
                ) {
                    KText(actionLabel, size = 7.sp, weight = FontWeight.Bold, color = accent)
                }
            }
        }
    }
}

