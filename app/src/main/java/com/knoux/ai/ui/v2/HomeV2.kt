package com.knoux.ai.ui.v2

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.components.rememberMotionEnabled

/*
 * KNOUX AI PRO — Home, rebuilt 1:1 to the owner's approved reference
 * (KNOUX_AI_PRO_HOME_APPROVED.png, 941×1672). Presentation only:
 * every metric comes from DeviceFacts (real Android APIs), every card
 * navigates to its existing functional destination. Geometry is derived
 * from the reference artboard and expressed in dp so it scales with the
 * window instead of hardcoding device pixels.
 */

// Reference palette samples (see GEOMETRY.md in the QA evidence pack).
private val RefSkyTop = Color(0xFF01031B)
private val RefSkyMid = Color(0xFF0A0630)
private val RefSkyBase = Color(0xFF12082E)
// Card glass: deep navy core (#090F37 sampled mid-card) with a violet-lit
// top facet (#1B1D51) — not a flat purple slab.
private val RefCardFillTop = Color(0xFF1E1B55)
private val RefCardFillBottom = Color(0xFF070A22)
private val RefPanelFill = Color(0xFF101243)
internal val RefGold = Color(0xFFFFC857)
internal val RefGoldBright = Color(0xFFFFE9A8)

/** One service card in the approved 2×7 RTL grid. */
private data class RefCard(
    val title: String,
    val subtitle: String,
    val route: String,
    val accent: Color,
    val accent2: Color,
    val art: Int? = null,
    val glyph: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val mark: RefMark = RefMark.NONE,
    val goldEdge: Boolean = false,
    val latin: Boolean = false,
)

private enum class RefMark { NONE, MICROSOFT }

private val refCards: List<RefCard> = listOf(
    // Row 1 — right then left, exactly as the reference.
    // Icon art is lifted straight from the approved artboard (knoux_refcard_*):
    // the exact broom/magnifier/3D glyphs the owner approved, alpha-cleaned.
    RefCard("التنظيف الذكي", "تنظيف متقدم وآمن", "hub-smartclean", V2Pink, V2Violet, art = R.drawable.knoux_refcard_clean),
    RefCard("الفحص العميق", "تحليل شامل وموثوق", "hub-deepscan", V2Cyan, V2Pink, art = R.drawable.knoux_refcard_deepscan),
    // Row 2
    RefCard("الملفات المكررة", "العثور على النسخ المكررة", "hub-duplicates", V2Blue, V2Violet, art = R.drawable.knoux_refcard_duplicates),
    RefCard("بقايا التطبيقات", "إزالة الملفات المتبقية", "hub-residue", V2Violet, V2Pink, art = R.drawable.knoux_refcard_residue),
    // Row 3
    RefCard("تحليل التخزين", "استخدام المساحة بالتفصيل", "hub-storage", V2Cyan, V2Blue, art = R.drawable.knoux_refcard_storage),
    RefCard("مدير التطبيقات", "إدارة وتحديث التطبيقات", "hub-appmanager", V2Violet, V2Blue, art = R.drawable.knoux_refcard_appmanager),
    // Row 4
    RefCard("الخصوصية", "حماية البيانات والهوية", "hub-privacy", V2Violet, V2Cyan, art = R.drawable.knoux_refcard_privacy),
    RefCard("البطارية", "تحسين العمر والأداء", "hub-battery", V2Green, V2Cyan, art = R.drawable.knoux_refcard_battery),
    // Row 5
    RefCard("الذكاء الاصطناعي", "أدوات ذكية لتحسين الجهاز", "hub-ai", V2Violet, V2Cyan, art = R.drawable.knoux_refcard_ai),
    RefCard("التقارير", "تقارير ذكية عن حالة الجهاز", "hub-reports", V2Violet, V2Pink, art = R.drawable.knoux_refcard_reports),
    // Row 6 — gold-edged developer / cloud pair.
    RefCard("استوديو المطور", "أدوات متقدمة للمطورين", "developer-studio", V2Violet, V2Cyan, art = R.drawable.knoux_refcard_developer, goldEdge = true),
    RefCard("البريد والسحابة", "إدارة البريد والتخزين السحابي", "cloud-cleaner", V2Blue, V2Cyan, art = R.drawable.knoux_refcard_cloud, goldEdge = true),
    // Row 7 — official ecosystem marks, gold edges.
    RefCard("Google Center", "خدمات وتكامل Google", "google-storage", RefGold, V2Cyan, art = R.drawable.knoux_google_g, goldEdge = true, latin = true),
    RefCard("Microsoft Center", "خدمات وتكامل Microsoft", "microsoft-storage", RefGold, V2Blue, mark = RefMark.MICROSOFT, goldEdge = true, latin = true),
)

@Composable
fun V2HomeScreen(
    onNavigate: (String) -> Unit,
    onMenu: () -> Unit,
    onPro: () -> Unit,
    onProfile: () -> Unit,
    onNotifications: () -> Unit,
) {
    val facts = rememberDeviceFacts()
    // The approved Home is night-lit cosmic art: like splash/auth it keeps
    // its full night world even when the shell behind it is porcelain day.
    // The composition is pinned LTR exactly like the approved artboard:
    // stag RIGHT, glass panel + gauge LEFT, laurels in their drawn sides.
    // Arabic strings keep their own RTL shaping and explicit alignments.
    CompositionLocalProvider(
        LocalDayMode provides false,
        LocalKnouxOnDark provides true,
        LocalLayoutDirection provides LayoutDirection.Ltr,
    ) {
    Box(Modifier.fillMaxSize()) {
        RefHomeBackground()
        // Official stag, free-floating upper right. Both the approved art and
        // the official asset face LEFT (toward the panel) — no mirroring.
        // knoux_stag_home.png is the alpha-tight crop of the official asset
        // (959×1203); reference bbox ≈ 241×410px → ~105×179dp at 411dp width.
        val motion = rememberMotionEnabled()
        val breath = rememberInfiniteTransition(label = "refStag")
        val stagFloat by breath.animateFloat(
            initialValue = 0f,
            targetValue = if (motion) -4f else 0f,
            animationSpec = infiniteRepeatable(tween(4600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "stagFloat",
        )
        Image(
            // Tight-cropped official stag (knoux_stag_home.png derived from
            // knoux_stag_official.png, alpha bbox only — art unchanged).
            painter = painterResource(R.drawable.knoux_stag_home),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.statusBars)
                .offset(x = (-2).dp, y = 52.dp)
                .size(width = 124.dp, height = 156.dp)
                .graphicsLayer {
                    // Official asset already faces left (toward the panel),
                    // exactly like the approved art — no mirroring.
                    translationY = stagFloat.dp.toPx()
                    alpha = .97f
                },
        )
        // Grounded stag: a faceted low-poly rocky outcrop drawn OVER the hoof
        // tips so the stag stands ON believable terrain (owner: «كأنه واقف بين
        // أرض»). Hoof line ≈ status inset (~26dp) + 52dp offset + 156dp ≈ 234dp;
        // the lit ridge meets it ~228dp so only the hoof tips tuck behind.
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val ridge = 228.dp.toPx()
            // cyan ground glow pooling beneath the outcrop
            drawCircle(
                Brush.radialGradient(listOf(V2Cyan.withAlpha(.22f), Color(0xFF7A2DFF).withAlpha(.14f), Color.Transparent), center = Offset(w * .82f, ridge + 14.dp.toPx()), radius = w * .40f),
                radius = w * .40f, center = Offset(w * .82f, ridge + 14.dp.toPx()),
            )
            // Jagged top ridge (left→right). Peaks sit just under the hooves.
            val top = listOf(.50f to 240f, .58f to 229f, .66f to 235f, .74f to 225f, .82f to 231f, .90f to 223f, 1f to 229f)
            fun ridgePath() = Path().apply {
                moveTo(w * top[0].first, top[0].second.dp.toPx())
                top.drop(1).forEach { (px, dy) -> lineTo(w * px, dy.dp.toPx()) }
            }
            // Rock body: dark indigo mass fading into the night below.
            val body = ridgePath().apply {
                lineTo(w, ridge + 74.dp.toPx()); lineTo(w * .50f, ridge + 74.dp.toPx()); close()
            }
            drawPath(body, Brush.verticalGradient(listOf(Color(0xFF33176B), Color(0xFF1A0C44), Color(0xFF0B0626).withAlpha(.55f), Color.Transparent), startY = ridge - 6.dp.toPx(), endY = ridge + 74.dp.toPx()))
            // Lit top plane (the surface the stag stands on) — a thin cyan-lit
            // band just below the ridge to read as a real ground plane.
            val plane = ridgePath().apply {
                lineTo(w, ridge + 12.dp.toPx())
                listOf(.90f to 235f, .82f to 243f, .74f to 237f, .66f to 247f, .58f to 241f, .50f to 252f).forEach { (px, dy) -> lineTo(w * px, dy.dp.toPx()) }
                close()
            }
            drawPath(plane, Brush.verticalGradient(listOf(Color(0xFF4A2590), Color(0xFF2A1157)), startY = ridge - 4.dp.toPx(), endY = ridge + 14.dp.toPx()))
            // Low-poly facet seams on the front face for a believable rock.
            val seams = listOf(
                listOf(.58f to 229f, .62f to 268f),
                listOf(.74f to 225f, .70f to 272f),
                listOf(.82f to 231f, .86f to 270f),
                listOf(.90f to 223f, .94f to 266f),
            )
            seams.forEach { seg ->
                val p = Path().apply {
                    moveTo(w * seg[0].first, seg[0].second.dp.toPx())
                    lineTo(w * seg[1].first, seg[1].second.dp.toPx())
                }
                drawPath(p, Color(0xFF4DD9FF).withAlpha(.22f), style = Stroke(1.dp.toPx()))
            }
            // Contact shadow ON the ground plane under the hooves.
            drawOval(
                Color.Black.withAlpha(.5f),
                topLeft = Offset(w * .66f, ridge + 2.dp.toPx()),
                size = Size(w * .30f, 6.dp.toPx()),
            )
            // Cyan-lit ridge crest (the glowing edge in the reference).
            drawPath(ridgePath(), Brush.horizontalGradient(listOf(Color.Transparent, V2Cyan.withAlpha(.7f), Color(0xFF4DD9FF).withAlpha(.9f))), style = Stroke(1.8.dp.toPx(), cap = StrokeCap.Round))
        }
        LazyColumn(
            Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 14.dp),
        ) {
            item { RefHomeHeader(onMenu, onProfile, onNotifications, onPro) }
            item { RefHomeIdentity() }
            item { Spacer(Modifier.height(2.dp)) }
            item {
                // Reference: the glass panel spans ~67% of the artboard width
                // from the left edge; the stag owns the right third.
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    RefMetricsPanel(facts, Modifier.fillMaxWidth(.72f))
                }
            }
            // The orb's upper rings overlap the panel's lower edge in the
            // approved art — pull it up instead of leaving a clean gap. The
            // orb+grid+banner share one item so negative offsets compose as
            // real layout overlap instead of leaving phantom gaps.
            item {
                Column {
                    RefHomeOrb(onStart = { onNavigate("hub-deepscan") }, Modifier.offset(y = (-10).dp))
                    RefServiceGrid(onNavigate, Modifier.offset(y = (-30).dp))
                    Spacer(Modifier.height(6.dp))
                    RefProBanner(onPro, Modifier.offset(y = (-30).dp))
                }
            }
        }
    }
    }
}

/* ------------------------------------------------------------------ *
 * Shared live-read notice (used by the section hubs).
 * ------------------------------------------------------------------ */

@Composable
internal fun LiveReadNotice() {
    V2LuxurySurface(
        Modifier.fillMaxWidth(),
        radius = 16.dp,
        accent = V2Green,
        borderAlpha = .30f,
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText("قراءة حقيقية من هذا الجهاز", size = 11.sp, weight = FontWeight.Bold, color = V2Green)
                Spacer(Modifier.height(1.dp))
                KText(
                    "كل الأرقام أعلاه من مصادر Android المتاحة على هاتفك، ولا يتم رفع أي بيانات.",
                    size = 9.5.sp,
                    lineHeight = 13.sp,
                    color = V2Soft,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Background — cosmic midnight, mountains, stars (reference region 1)
 * ------------------------------------------------------------------ */

@Composable
internal fun RefHomeBackground() {
    // Living night world, matching the approved art: aurora ribbons drift,
    // stars twinkle, the stag nebula breathes. One infinite transition drives
    // everything; reduced motion freezes the scene at its base phase.
    val motion = rememberMotionEnabled()
    val ambient = rememberInfiniteTransition(label = "refAmbient")
    val aurora by ambient.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(16000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "auroraDrift",
    )
    val twinkle by ambient.animateFloat(
        initialValue = .55f,
        targetValue = if (motion) 1f else .8f,
        animationSpec = infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "starTwinkle",
    )
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawRect(
            Brush.verticalGradient(
                0f to RefSkyTop,
                .28f to RefSkyMid,
                .55f to RefSkyBase,
                .80f to Color(0xFF170A38),
                1f to Color(0xFF1E0D41),
            )
        )
        // Aurora ribbons sweeping the upper sky (drift with `aurora`).
        val drift = (aurora - .5f) * w * .10f
        val ribbonA = Path().apply {
            moveTo(-w * .1f + drift, h * .10f)
            cubicTo(w * .25f + drift, h * .02f, w * .55f + drift, h * .16f, w * 1.1f + drift, h * .05f)
        }
        drawPath(ribbonA, Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF7A2DFF).withAlpha(.22f), V2Pink.withAlpha(.16f), Color.Transparent)), style = Stroke(14.dp.toPx(), cap = StrokeCap.Round))
        val ribbonB = Path().apply {
            moveTo(-w * .1f - drift, h * .20f)
            cubicTo(w * .30f - drift, h * .12f, w * .60f - drift, h * .26f, w * 1.1f - drift, h * .14f)
        }
        drawPath(ribbonB, Brush.horizontalGradient(listOf(Color.Transparent, V2Cyan.withAlpha(.10f), Color(0xFF7A2DFF).withAlpha(.18f), Color.Transparent)), style = Stroke(10.dp.toPx(), cap = StrokeCap.Round))
        // Violet nebula behind the stag (upper right) and magenta wash left.
        drawCircle(
            Brush.radialGradient(listOf(Color(0xFF7A2DFF).withAlpha(.34f + .06f * twinkle), Color.Transparent), center = Offset(w * .86f, h * .16f), radius = w * .62f),
            radius = w * .62f, center = Offset(w * .86f, h * .16f),
        )
        drawCircle(
            Brush.radialGradient(listOf(V2Pink.withAlpha(.20f), Color.Transparent), center = Offset(w * .02f, h * .26f), radius = w * .55f),
            radius = w * .55f, center = Offset(w * .02f, h * .26f),
        )
        drawCircle(
            Brush.radialGradient(listOf(V2Cyan.withAlpha(.10f), Color.Transparent), center = Offset(w * .5f, h * .42f), radius = w * .5f),
            radius = w * .5f, center = Offset(w * .5f, h * .42f),
        )
        // Left glowing mountain massif (reference: lit violet ridges).
        val leftFarPts = listOf(0f to .115f, .09f to .150f, .17f to .125f, .26f to .185f, .34f to .160f, .42f to .225f)
        val leftFar = Path().apply {
            moveTo(0f, h * .115f)
            leftFarPts.drop(1).forEach { (px, py) -> lineTo(w * px, h * py) }
            lineTo(w * .42f, h * .34f); lineTo(0f, h * .34f); close()
        }
        drawPath(leftFar, Brush.verticalGradient(listOf(Color(0xFF8A3FFF).withAlpha(.50f), Color(0xFF54268D).withAlpha(.30f), Color.Transparent), startY = h * .11f, endY = h * .34f))
        val leftFarRidge = Path().apply {
            moveTo(0f, h * .115f)
            leftFarPts.drop(1).forEach { (px, py) -> lineTo(w * px, h * py) }
        }
        drawPath(leftFarRidge, Brush.horizontalGradient(listOf(Color(0xFFB46BFF).withAlpha(.55f), Color.Transparent)), style = Stroke(1.4.dp.toPx()))
        val leftNearPts = listOf(0f to .205f, .11f to .245f, .20f to .215f, .31f to .285f, .38f to .26f, .46f to .33f)
        val leftNear = Path().apply {
            moveTo(0f, h * .205f)
            leftNearPts.drop(1).forEach { (px, py) -> lineTo(w * px, h * py) }
            lineTo(w * .46f, h * .46f); lineTo(0f, h * .46f); close()
        }
        drawPath(leftNear, Brush.verticalGradient(listOf(Color(0xFF2A1157).withAlpha(.95f), Color(0xFF12082E).withAlpha(.75f), Color.Transparent), startY = h * .20f, endY = h * .46f))
        val leftNearRidge = Path().apply {
            moveTo(0f, h * .205f)
            leftNearPts.drop(1).forEach { (px, py) -> lineTo(w * px, h * py) }
        }
        drawPath(leftNearRidge, Brush.horizontalGradient(listOf(V2Pink.withAlpha(.40f), Color.Transparent)), style = Stroke(1.1.dp.toPx()))
        // Right massif under the stag: dark peaks, cyan-lit edges. Kept in the
        // stag zone (ends ~213dp) so no ridges stray beside the orb.
        val rightFarPts = listOf(1f to .19f, .90f to .225f, .80f to .20f, .70f to .25f, .62f to .23f, .56f to .27f)
        val rightFar = Path().apply {
            moveTo(w, h * .19f)
            rightFarPts.drop(1).forEach { (px, py) -> lineTo(w * px, h * py) }
            lineTo(w * .56f, h * .34f); lineTo(w, h * .34f); close()
        }
        drawPath(rightFar, Brush.verticalGradient(listOf(Color(0xFF3A1B6E).withAlpha(.85f), Color(0xFF150A38).withAlpha(.7f), Color.Transparent), startY = h * .19f, endY = h * .34f))
        val rightFarRidge = Path().apply {
            moveTo(w, h * .19f)
            rightFarPts.drop(1).forEach { (px, py) -> lineTo(w * px, h * py) }
        }
        drawPath(rightFarRidge, Brush.horizontalGradient(listOf(Color.Transparent, V2Cyan.withAlpha(.45f))), style = Stroke(1.3.dp.toPx()))
        val rightNearPts = listOf(1f to .26f, .88f to .295f, .78f to .27f, .68f to .32f, .60f to .30f, .54f to .35f)
        val rightNear = Path().apply {
            moveTo(w, h * .26f)
            rightNearPts.drop(1).forEach { (px, py) -> lineTo(w * px, h * py) }
            lineTo(w * .54f, h * .40f); lineTo(w, h * .40f); close()
        }
        drawPath(rightNear, Brush.verticalGradient(listOf(Color(0xFF1B0D45).withAlpha(.95f), Color(0xFF0B0626).withAlpha(.8f), Color.Transparent), startY = h * .26f, endY = h * .40f))
        val rightNearRidge = Path().apply {
            moveTo(w, h * .26f)
            rightNearPts.drop(1).forEach { (px, py) -> lineTo(w * px, h * py) }
        }
        drawPath(rightNearRidge, Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF4DD9FF).withAlpha(.35f))), style = Stroke(1.dp.toPx()))
        // Low horizon glow behind the service grid.
        drawCircle(
            Brush.radialGradient(listOf(Color(0xFF54268D).withAlpha(.28f), Color.Transparent), center = Offset(w * .5f, h * .78f), radius = w * .75f),
            radius = w * .75f, center = Offset(w * .5f, h * .78f),
        )
        // Deterministic starfield (no randomness per frame), twinkling.
        for (i in 0 until 90) {
            val x = ((i * 137.508) % 360.0) / 360.0 * w
            val y = (((i * 89.73) % 100.0) / 100.0) * h * .55f
            val r = if (i % 9 == 0) 1.5.dp.toPx() else .8.dp.toPx()
            val base = if (i % 5 == 0) .85f else .40f
            val phase = .70f + .30f * (((i * 37) % 10) / 10f)
            drawCircle(Color.White.withAlpha((base * twinkle * phase).coerceIn(0f, 1f)), radius = r, center = Offset(x.toFloat(), y.toFloat()))
        }
    }
}

/* ------------------------------------------------------------------ *
 * Header — profile + bell left, K lockup + PRO center, hamburger right
 * ------------------------------------------------------------------ */

@Composable
private fun RefHomeHeader(
    onMenu: () -> Unit,
    onProfile: () -> Unit,
    onNotifications: () -> Unit,
    onPro: () -> Unit,
) {
    // Absolute placement so the lockup stays optically centered no matter
    // how wide the side clusters are (reference: lockup center ≈ artboard mid).
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(Modifier.fillMaxWidth().height(52.dp)) {
            Row(Modifier.align(Alignment.CenterStart), verticalAlignment = Alignment.CenterVertically) {
                RefHeaderMenuPill("القائمة", onMenu)
                Spacer(Modifier.width(8.dp))
                RefHeaderActionPill(Icons.Rounded.NotificationsNone, "التنبيهات", onNotifications)
            }
            // Center lockup: official geometric K + knoux ai wordmark + gold PRO capsule.
            Row(Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.knoux_symbol_alpha),
                    contentDescription = "KNOUX",
                    modifier = Modifier.height(24.dp),
                )
                Spacer(Modifier.width(5.dp))
                Image(
                    painter = painterResource(R.drawable.knoux_wordmark_alpha),
                    contentDescription = "knoux ai",
                    modifier = Modifier.height(15.dp),
                )
                Spacer(Modifier.width(7.dp))
                Row(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF3A2404), Color(0xFF241503))))
                        .border(1.2.dp, Brush.horizontalGradient(listOf(RefGold, RefGoldBright, RefGold)), RoundedCornerShape(50))
                        .clickable(onClickLabel = "Pro", onClick = onPro)
                        .padding(horizontal = 9.dp, vertical = 3.5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CrownGold(size = 13.dp, color = RefGold)
                    Spacer(Modifier.width(4.dp))
                    CompositionLocalProvider(LocalKnouxOnDark provides true) {
                        KText("PRO", size = 10.sp, weight = FontWeight.Black, color = RefGoldBright, letterSpacing = .8.sp)
                    }
                }
            }
            // Right: flat profile pill + hamburger.
            Row(Modifier.align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
                RefHeaderActionPill(Icons.Rounded.Person, "الحساب", onProfile)
                Spacer(Modifier.width(8.dp))
                RefHeaderMenuPill("القائمة", onMenu, isRight = true)
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Identity — metallic KNOUX AI PRO, laurels, Arabic headline + capabilities
 * ------------------------------------------------------------------ */

@Composable
private fun RefHomeIdentity() {
    Column(Modifier.fillMaxWidth().padding(top = 2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RefLaurel(mirror = false, w = 21.dp, h = 36.dp)
            Spacer(Modifier.width(9.dp))
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr, LocalKnouxOnDark provides true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "KNOUX AI",
                        style = androidx.compose.ui.text.TextStyle(
                            brush = Brush.verticalGradient(
                                listOf(Color.White, Color(0xFFE6E0F5), Color(0xFFB9ACD8)),
                            ),
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            letterSpacing = 1.2.sp,
                        ),
                        maxLines = 1,
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "PRO",
                        style = androidx.compose.ui.text.TextStyle(
                            brush = Brush.verticalGradient(listOf(RefGoldBright, RefGold, Color(0xFFD99A2B))),
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        ),
                        maxLines = 1,
                    )
                }
            }
            Spacer(Modifier.width(9.dp))
            RefLaurel(mirror = true, w = 21.dp, h = 36.dp)
        }
        Spacer(Modifier.height(2.dp))
        CompositionLocalProvider(LocalKnouxOnDark provides true) {
            Text(
                "إدارة ذكية وشاملة لجهازك",
                style = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                ),
                maxLines = 1,
            )
            Spacer(Modifier.height(2.dp))
            KText("تنظيف · فحص · تحليل · حماية · تسريع", size = 10.sp, color = V2Soft, maxLines = 1)
        }
    }
}

@Composable
internal fun RefLaurel(mirror: Boolean, modifier: Modifier = Modifier, w: androidx.compose.ui.unit.Dp = 18.dp, h: androidx.compose.ui.unit.Dp = 32.dp) {
    // Reference laurel: a curved golden branch with paired pointed leaves,
    // convex side facing the headline. Left branch drawn; right mirrored.
    Canvas(modifier.size(width = w, height = h).graphicsLayer { scaleX = if (mirror) -1f else 1f }) {
        val stem = Path().apply {
            moveTo(size.width * .82f, size.height * .02f)
            cubicTo(size.width * .22f, size.height * .30f, size.width * .18f, size.height * .66f, size.width * .62f, size.height * .98f)
        }
        drawPath(stem, Brush.verticalGradient(listOf(RefGoldBright, RefGold, Color(0xFFB87A1E))), style = Stroke(1.8.dp.toPx(), cap = StrokeCap.Round))
        for (index in 0 until 6) {
            val t = .12f + index * .145f
            // point on the stem curve (approximate)
            val sx = size.width * (.82f - .55f * t)
            val sy = size.height * t
            val leaf = Path().apply {
                moveTo(sx, sy)
                cubicTo(sx - size.width * .42f, sy - size.height * .10f, sx - size.width * .50f, sy + size.height * .02f, sx - size.width * .06f, sy + size.height * .10f)
                cubicTo(sx - size.width * .20f, sy + size.height * .04f, sx - size.width * .10f, sy + size.height * .01f, sx, sy)
                close()
            }
            drawPath(leaf, Brush.linearGradient(listOf(RefGoldBright, RefGold, Color(0xFFD99A2B))))
        }
    }
}

/* ------------------------------------------------------------------ *
 * Metrics panel — real storage / RAM / battery + computed health gauge
 * ------------------------------------------------------------------ */

@Composable
private fun RefMetricsPanel(facts: DeviceFacts, modifier: Modifier = Modifier) {
    val ram = facts.ram
    val storage = facts.storage
    val battery = facts.battery
    val live = facts.computedAt > 0L
    // Reference: 168px panel on 941px artboard ≈ 73dp; heightIn lets larger
    // font scales grow the panel instead of clipping the metric rows.
    Box(
        modifier
            .heightIn(min = 70.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.verticalGradient(listOf(RefPanelFill.withAlpha(.92f), Color(0xFF0A0C33).withAlpha(.88f))))
            .border(
                1.4.dp,
                Brush.linearGradient(listOf(Color(0xFF3D55E0), V2Violet.withAlpha(.8f), V2Cyan.withAlpha(.7f), Color(0xFF3D55E0))),
                RoundedCornerShape(22.dp),
            )
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Reference: gauge on the LEFT, metric rows on the RIGHT.
            RefHealthGauge(
                percent = if (live) facts.healthEstimate else 0,
                modifier = Modifier.size(62.dp),
            )
            Spacer(Modifier.width(8.dp))
            Box(Modifier.width(1.dp).height(56.dp).background(Brush.verticalGradient(listOf(Color.Transparent, V2Cyan.withAlpha(.45f), Color.Transparent))))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                RefMetricRow(
                    icon = Icons.Rounded.Storage,
                    tint = V2Cyan,
                    label = "التخزين",
                    // Reference order: used / total (72.4 GB / 256 GB).
                    value = if (live) "${formatBytes(storage.usedBytes)} / ${formatBytes(storage.totalBytes)}" else "جاري القراءة",
                    progress = storage.usedFraction,
                    bar = listOf(V2Cyan, V2Blue),
                )
                RefMetricRow(
                    icon = Icons.Rounded.Memory,
                    tint = V2Violet,
                    label = "الذاكرة",
                    value = if (ram.totalBytes > 0L) "${formatBytes(ram.usedBytes)} / ${formatBytes(ram.totalBytes)}" else "غير متاح",
                    progress = ram.usedFraction,
                    bar = listOf(V2Violet, V2Cyan),
                )
                RefMetricRow(
                    icon = Icons.Rounded.Bolt,
                    tint = V2Green,
                    label = "البطارية",
                    value = battery.levelPercent?.let { "$it%" } ?: "غير متاح",
                    progress = (battery.levelPercent ?: 0) / 100f,
                    bar = listOf(V2Green, V2Cyan),
                )
            }
        }
    }
}

@Composable
private fun RefMetricRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    label: String,
    value: String,
    progress: Float,
    bar: List<Color>,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        // Reference rows (panel crop): icon LEFT, label next, value far RIGHT,
        // gradient bar underneath spanning the row.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            // Reference: icon sits in a small glowing rounded chip.
            Box(
                Modifier
                    .size(19.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(tint.withAlpha(.16f))
                    .border(.8.dp, tint.withAlpha(.45f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(12.dp))
            }
            Spacer(Modifier.width(6.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    // Reference: Arabic label sits just right of the icon;
                    // the numeric value is pinned to the far right edge.
                    KText(label, size = 10.sp, weight = FontWeight.Bold, color = Color.White, maxLines = 1)
                    Spacer(Modifier.weight(1f))
                    KText(value, size = 9.sp, weight = FontWeight.SemiBold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.height(2.dp))
                Box(Modifier.fillMaxWidth().height(3.5.dp).clip(RoundedCornerShape(50)).background(Color.White.withAlpha(.10f))) {
                    Box(
                        Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).height(3.5.dp).clip(RoundedCornerShape(50))
                            .background(Brush.horizontalGradient(bar))
                    )
                }
            }
        }
    }
}

@Composable
private fun RefHealthGauge(percent: Int, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension * .46f
            drawCircle(Brush.radialGradient(listOf(Color(0xFF141034), Color(0xFF080620)), center = center, radius = r * .92f), radius = r * .92f)
            val sweep = Brush.sweepGradient(listOf(V2Cyan, Color(0xFFFBF980), V2Pink, V2Violet, V2Cyan), center)
            // Soft rainbow bloom behind the ring (reference glow).
            drawArc(
                sweep,
                startAngle = -90f,
                sweepAngle = 360f * percent.coerceIn(0, 100) / 100f,
                useCenter = false,
                style = Stroke(9.dp.toPx(), cap = StrokeCap.Round),
                alpha = .30f,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2f, r * 2f),
            )
            drawCircle(Color.White.withAlpha(.08f), radius = r, style = Stroke(6.dp.toPx(), cap = StrokeCap.Round))
            drawArc(
                // Reference ring: cyan at 3 o'clock, yellow at 6, magenta at
                // 9, violet at 12 — sweepGradient starts at 3 o'clock.
                sweep,
                startAngle = -90f,
                sweepAngle = 360f * percent.coerceIn(0, 100) / 100f,
                useCenter = false,
                style = Stroke(6.dp.toPx(), cap = StrokeCap.Round),
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2f, r * 2f),
            )
        }
        CompositionLocalProvider(LocalKnouxOnDark provides true) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                KText(if (percent > 0) "$percent%" else "—", size = 16.sp, weight = FontWeight.Black, color = Color.White, maxLines = 1)
                KText("صحة الجهاز", size = 7.sp, color = V2Soft, maxLines = 1)
                Spacer(Modifier.height(1.dp))
                Icon(Icons.Rounded.Verified, null, tint = RefGold, modifier = Modifier.size(9.dp))
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Central orb — house glyph, الرئيسية, gold CTA, orbit base
 * ------------------------------------------------------------------ */

@Composable
private fun RefHomeOrb(onStart: () -> Unit, modifier: Modifier = Modifier) {
    val motion = rememberMotionEnabled()
    val transition = rememberInfiniteTransition(label = "refOrb")
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) 360f else 0f,
        animationSpec = infiniteRepeatable(tween(26000, easing = androidx.compose.animation.core.LinearEasing), RepeatMode.Restart),
        label = "orbSpin",
    )
    val pulse by transition.animateFloat(
        initialValue = .82f,
        targetValue = if (motion) 1f else .9f,
        animationSpec = infiniteRepeatable(tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "orbPulse",
    )
    // Reference orb: 304px diameter on a 941px artboard → ~133dp at 411dp
    // width. Rings + orbit base extend ~45dp below the sphere.
    Box(modifier.fillMaxWidth().height(178.dp), contentAlignment = Alignment.TopCenter) {
        Canvas(Modifier.fillMaxSize()) {
            val cx = size.width * .5f
            val orbR = 66.dp.toPx()
            val orbCy = orbR + 2.dp.toPx()
            // Orbit base: luminous pooled disc + concentric ellipses (reference).
            val baseCy = size.height * .90f
            drawOval(
                Brush.radialGradient(
                    listOf(V2Cyan.withAlpha(.34f), V2Violet.withAlpha(.26f), Color.Transparent),
                    center = Offset(cx, baseCy),
                    radius = size.width * .44f,
                ),
                topLeft = Offset(cx - size.width * .44f, baseCy - 17.dp.toPx()),
                size = Size(size.width * .88f, 34.dp.toPx()),
            )
            for (i in 0 until 5) {
                drawOval(
                    brush = Brush.sweepGradient(
                        listOf(Color.Transparent, V2Cyan, V2Violet, RefGold, V2Pink, Color.Transparent),
                        center = Offset(cx, baseCy),
                    ),
                    topLeft = Offset(cx - size.width * (.28f + i * .045f), baseCy - (6 + i * 4).dp.toPx()),
                    size = Size(size.width * (.56f + i * .09f), (12 + i * 8).dp.toPx()),
                    style = Stroke((2.0f - i * .32f).dp.toPx()),
                    alpha = .9f - i * .13f,
                )
            }
            // Tilted orbit lines sweeping behind the sphere (reference detail).
            for (i in 0 until 2) {
                drawOval(
                    brush = Brush.sweepGradient(
                        listOf(Color.Transparent, V2Cyan.withAlpha(.5f), Color.Transparent, V2Violet.withAlpha(.45f), Color.Transparent),
                        center = Offset(cx, orbCy + orbR * .45f),
                    ),
                    topLeft = Offset(cx - orbR * (1.75f + i * .35f), orbCy + orbR * .45f - orbR * (.62f + i * .16f)),
                    size = Size(orbR * (3.5f + i * .7f), orbR * (1.24f + i * .32f)),
                    style = Stroke(.9.dp.toPx()),
                    alpha = .55f - i * .2f,
                )
            }
            // Flanking gold sparks on thin arcs (reference detail).
            for (side in listOf(-1f, 1f)) {
                val sx = cx + side * size.width * .335f
                val sy = orbCy + orbR * .28f
                drawCircle(RefGold, radius = 2.4.dp.toPx(), center = Offset(sx, sy))
                drawCircle(RefGold.withAlpha(.35f), radius = 5.dp.toPx(), center = Offset(sx, sy))
                drawArc(
                    Brush.sweepGradient(listOf(Color.Transparent, RefGold.withAlpha(.6f), Color.Transparent), Offset(sx, sy)),
                    startAngle = if (side < 0) 120f else -60f,
                    sweepAngle = 120f,
                    useCenter = false,
                    style = Stroke(.9.dp.toPx()),
                    topLeft = Offset(sx - 26.dp.toPx(), sy - 26.dp.toPx()),
                    size = Size(52.dp.toPx(), 52.dp.toPx()),
                )
            }
            // Outer halo — strong luminous bloom like the approved art.
            drawCircle(
                Brush.radialGradient(listOf(V2Violet.withAlpha(.5f * pulse), V2Cyan.withAlpha(.24f), Color.Transparent), center = Offset(cx, orbCy), radius = orbR * 1.6f),
                radius = orbR * 1.6f, center = Offset(cx, orbCy),
            )
            // Multi-ring neon rim: white-hot outer edge, violet→cyan→gold sweep.
            // center MUST match the core (orbCy), not the Canvas midpoint.
            for (i in 0 until 4) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(Color.White, V2Cyan, V2Violet, V2Pink, RefGold, Color.White),
                        center = Offset(cx, orbCy),
                    ),
                    radius = orbR + 1.5.dp.toPx() - i * 3.4.dp.toPx(),
                    center = Offset(cx, orbCy),
                    style = Stroke((4.6f - i * 1.05f).dp.toPx(), cap = StrokeCap.Round),
                    alpha = 1f - i * .18f,
                )
            }
            // Rotating specular arc.
            drawArc(
                Brush.sweepGradient(listOf(Color.Transparent, Color.White.withAlpha(.95f), Color.Transparent), Offset(cx, orbCy)),
                startAngle = spin,
                sweepAngle = 70f,
                useCenter = false,
                style = Stroke(2.6.dp.toPx(), cap = StrokeCap.Round),
                topLeft = Offset(cx - orbR, orbCy - orbR),
                size = Size(orbR * 2, orbR * 2),
            )
            // Dark glass core.
            drawCircle(
                Brush.radialGradient(listOf(Color(0xFF1C1240), Color(0xFF0B0724), Color(0xFF060414)), center = Offset(cx, orbCy), radius = orbR * .88f),
                radius = orbR * .88f, center = Offset(cx, orbCy),
            )
        }
        // Interactive core: house + title + gold CTA, all one real button.
        Column(
            Modifier
                .padding(top = 6.dp)
                .size(124.dp)
                .semantics { contentDescription = "الرئيسية — ابدأ الإدارة الذكية" }
                .clickable(onClickLabel = "ابدأ الإدارة الذكية", onClick = onStart),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CompositionLocalProvider(LocalKnouxOnDark provides true) {
                RefHouseOutline(modifier = Modifier.size(44.dp))
                Spacer(Modifier.height(2.dp))
                KText("الرئيسية", size = 16.sp, weight = FontWeight.Black, color = Color.White, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Row(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF8A5A12), RefGold, Color(0xFF8A5A12))))
                        .border(1.dp, RefGoldBright.withAlpha(.9f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Reference: chevron › sits at the RIGHT end of the pill,
                    // text to its left — pin LTR so RTL locale can't flip it.
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        KText("ابدأ الإدارة الذكية", size = 10.sp, weight = FontWeight.Black, color = Color(0xFF241503), maxLines = 1)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Rounded.ChevronRight, null, tint = Color(0xFF241503), modifier = Modifier.size(13.dp))
                    }
                }
            }
        }
    }
}

/** Reference house glyph: pure pink outline (roof + body + door), no fill. */
@Composable
private fun RefHouseOutline(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.6.dp.toPx()
        val pink = V2Pink
        // Soft bloom behind the glyph.
        drawCircle(
            Brush.radialGradient(listOf(pink.withAlpha(.22f), Color.Transparent), center = Offset(w * .5f, h * .5f), radius = w * .7f),
            radius = w * .7f, center = Offset(w * .5f, h * .5f),
        )
        val roof = Path().apply {
            moveTo(w * .06f, h * .46f)
            lineTo(w * .5f, h * .06f)
            lineTo(w * .94f, h * .46f)
        }
        drawPath(roof, pink, style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val body = Path().apply {
            moveTo(w * .16f, h * .40f)
            lineTo(w * .16f, h * .92f)
            lineTo(w * .84f, h * .92f)
            lineTo(w * .84f, h * .40f)
        }
        drawPath(body, pink, style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val door = Path().apply {
            moveTo(w * .40f, h * .92f)
            lineTo(w * .40f, h * .62f)
            lineTo(w * .60f, h * .62f)
            lineTo(w * .60f, h * .92f)
        }
        drawPath(door, pink, style = Stroke(stroke * .85f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/* ------------------------------------------------------------------ *
 * Service grid — exactly 14 cards, 2 columns × 7 RTL rows
 * ------------------------------------------------------------------ */

@Composable
private fun RefServiceGrid(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    // Absolute artboard order: index 0 = right column, index 1 = left column.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        // Larger cards at 64dp → slightly wider row pitch so they breathe.
        Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            refCards.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // row[0] is the RIGHT card in the reference; place it last
                    // in LTR so it lands on the right edge.
                    listOf(row[1], row[0]).forEach { card ->
                        RefServiceCard(card, Modifier.weight(1f)) { onNavigate(card.route) }
                    }
                }
            }
        }
    }
}

@Composable
private fun RefServiceCard(card: RefCard, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shape = RoundedCornerShape(22.dp)
    val edge = if (card.goldEdge) listOf(RefGoldBright, RefGold, Color(0xFFB87A1E)) else listOf(card.accent, card.accent2, card.accent)
    // Larger cards: 64dp height, 44dp icons, stronger touch targets.
    Box(
        modifier
            .height(64.dp)
            .scale(if (pressed) .97f else 1f)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(RefCardFillTop.withAlpha(.92f), RefCardFillBottom.withAlpha(.94f))))
            .drawBehind {
                // Subtle top sheen only — no accent circle (orb is the only circle).
                drawLine(
                    Brush.horizontalGradient(listOf(Color.Transparent, Color.White.withAlpha(.30f), Color.Transparent)),
                    start = Offset(size.width * .10f, .8.dp.toPx()),
                    end = Offset(size.width * .90f, .8.dp.toPx()),
                    strokeWidth = .8.dp.toPx(),
                )
            }
            .border(1.3.dp, Brush.linearGradient(edge.map { it.withAlpha(if (pressed) 1f else .8f) }), shape)
            .semantics { contentDescription = "${card.title}، ${card.subtitle}" }
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClickLabel = card.title, onClick = onClick),
    ) {
        // No divider between text and icon — cleaner anatomy, larger touch area.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RefCardText(card, Modifier.weight(1f), alignEnd = !card.latin)
                RefCardIcon(card)
            }
        }
    }
}

/** Stronger typography: 15sp title / 11sp subtitle for readability. */
@Composable
private fun RefCardText(card: RefCard, modifier: Modifier = Modifier, alignEnd: Boolean = false) {
    Column(modifier, horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        CompositionLocalProvider(LocalKnouxOnDark provides true) {
            if (card.latin) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    KText(card.title, size = 15.sp, weight = FontWeight.Black, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis, align = if (alignEnd) TextAlign.End else TextAlign.Start)
                }
            } else {
                KText(card.title, size = 15.sp, weight = FontWeight.Black, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis, align = if (alignEnd) TextAlign.End else TextAlign.Start)
            }
            Spacer(Modifier.height(2.dp))
            KText(card.subtitle, size = 11.sp, color = V2Soft, maxLines = 1, overflow = TextOverflow.Ellipsis, align = if (alignEnd) TextAlign.End else TextAlign.Start)
        }
    }
}

/** Larger 44dp icon with no circle wash behind it. */
@Composable
private fun RefCardIcon(card: RefCard) {
    Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
        when {
            card.mark == RefMark.MICROSOFT -> RefMicrosoftMark(Modifier.size(32.dp))
            card.art != null -> Image(
                painter = painterResource(card.art),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(.85f),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
            )
            card.glyph != null -> {
                Icon(card.glyph, null, tint = card.accent, modifier = Modifier.size(30.dp))
            }
        }
    }
}

/** Official Microsoft four-square mark, drawn with the brand colors. */
@Composable
private fun RefMicrosoftMark(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val gap = size.width * .06f
        val cell = (size.width - gap) / 2f
        drawRect(Color(0xFFF25022), topLeft = Offset.Zero, size = Size(cell, cell))
        drawRect(Color(0xFF7FBA00), topLeft = Offset(cell + gap, 0f), size = Size(cell, cell))
        drawRect(Color(0xFF00A4EF), topLeft = Offset(0f, cell + gap), size = Size(cell, cell))
        drawRect(Color(0xFFFFB900), topLeft = Offset(cell + gap, cell + gap), size = Size(cell, cell))
    }
}

/* ------------------------------------------------------------------ *
 * PRO banner — crown + laurels left, مزايا KNOUX AI Pro right, gold arrow
 * ------------------------------------------------------------------ */

@Composable
private fun RefProBanner(onPro: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier
            .fillMaxWidth()
            .heightIn(min = 46.dp)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(Color(0xFF2A10AB), Color(0xFF121768), Color(0xFF1D1170))))
            .drawBehind {
                // Aurora ribbons sweeping the banner, as in the reference.
                val ribbon = Path().apply {
                    moveTo(-8f, size.height * .78f)
                    cubicTo(size.width * .18f, size.height * .05f, size.width * .45f, size.height * .95f, size.width * .72f, size.height * .35f)
                    cubicTo(size.width * .9f, size.height * .05f, size.width, size.height * .5f, size.width + 8f, size.height * .25f)
                }
                drawPath(ribbon, Brush.horizontalGradient(listOf(Color.Transparent, V2Pink.withAlpha(.55f), V2Cyan.withAlpha(.4f), Color.Transparent)), style = Stroke(1.4.dp.toPx(), cap = StrokeCap.Round))
                drawCircle(Brush.radialGradient(listOf(Color(0xFF3510EB).withAlpha(.5f), Color.Transparent), center = Offset(size.width * .08f, size.height * .5f), radius = size.width * .3f), radius = size.width * .3f, center = Offset(size.width * .08f, size.height * .5f))
            }
            .border(1.3.dp, Brush.linearGradient(listOf(V2Violet.withAlpha(.9f), RefGold.withAlpha(.7f), V2Cyan.withAlpha(.7f))), shape)
            .semantics { contentDescription = "مزايا KNOUX AI Pro" }
            .clickable(role = Role.Button, onClickLabel = "مزايا KNOUX AI Pro", onClick = onPro),
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
            // RTL: text block first (right), arrow last (left)… reference has
            // the arrow on the RIGHT and crown on the LEFT, so pin LTR order.
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                // Left: gold crown + laurels + PRO pill.
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RefLaurel(mirror = false)
                    Spacer(Modifier.width(3.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CrownGold(size = 22.dp, color = RefGold)
                        Box(
                            Modifier.clip(RoundedCornerShape(50))
                                .background(Brush.horizontalGradient(listOf(Color(0xFF8A5A12), RefGold, Color(0xFF8A5A12))))
                                .padding(horizontal = 7.dp, vertical = 1.dp),
                        ) {
                            CompositionLocalProvider(LocalKnouxOnDark provides true) {
                                KText("PRO", size = 7.5.sp, weight = FontWeight.Black, color = Color(0xFF241503), letterSpacing = .6.sp)
                            }
                        }
                    }
                    Spacer(Modifier.width(3.dp))
                    RefLaurel(mirror = true)
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.width(1.dp).height(30.dp).background(Brush.verticalGradient(listOf(Color.Transparent, Color.White.withAlpha(.35f), Color.Transparent))))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    CompositionLocalProvider(LocalKnouxOnDark provides true) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            KText("مزايا", size = 13.sp, weight = FontWeight.Black, color = Color.White, maxLines = 1)
                            Spacer(Modifier.width(4.dp))
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Text(
                                    "KNOUX AI",
                                    style = androidx.compose.ui.text.TextStyle(
                                        brush = Brush.verticalGradient(listOf(Color.White, Color(0xFFE6E0F5), Color(0xFFB9ACD8))),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    ),
                                    maxLines = 1,
                                )
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    "Pro",
                                    style = androidx.compose.ui.text.TextStyle(
                                        brush = Brush.verticalGradient(listOf(RefGoldBright, RefGold, Color(0xFFD99A2B))),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    ),
                                    maxLines = 1,
                                )
                            }
                        }
                        KText("أداء أعلى · تحليل أعمق · تنظيف أذكى", size = 9.sp, color = V2Soft, maxLines = 1)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.size(26.dp).clip(CircleShape)
                        .border(1.4.dp, RefGold, CircleShape)
                        .background(Color(0xFF241503).withAlpha(.6f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.ChevronRight, null, tint = RefGoldBright, modifier = Modifier.size(15.dp))
                }
            }
        }
    }
}
