package com.knoux.ai.ui.v2

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.theme.KnouxAmber
import com.knoux.ai.ui.theme.KnouxArabic
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxText
import com.knoux.ai.ui.theme.KnouxViolet
import kotlin.math.cos
import kotlin.math.sin

/* ------------------------------------------------------------------ *
 * KNOUX AI V2 — Arabic reference palette
 * ------------------------------------------------------------------ */

val V2Deep = Color(0xFF050317)
val V2Deep2 = Color(0xFF0C0627)
val V2Panel = Color(0xFF160C36)
val V2Panel2 = Color(0xFF0D0728)
val V2GlassTop = Color(0xFF2A195A)
val V2GlassBottom = Color(0xFF0B0626)
val V2Border = Color(0xFF7B4BFF)
val V2Violet = Color(0xFF9D4DFF)
val V2Pink = Color(0xFFFF3FC8)
val V2Cyan = Color(0xFF17E6FF)
val V2Blue = Color(0xFF3F8CFF)
val V2Green = Color(0xFF37F3B4)
val V2Red = Color(0xFFFF5677)
val V2Amber = Color(0xFFFFC857)
val V2Soft = Color(0xFFBDB0E4)
val V2Muted = Color(0xFF8E82B4)
val V2GoldText = Color(0xFFFFD37B)

/** The Arabic UI face used by every V2 text node. */
val V2Font = KnouxArabic

private val ArabicLetters =
    Regex("[\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF\\uFB50-\\uFDFF\\uFE70-\\uFEFF]")

/**
 * Keeps latin units glued to their number inside Arabic sentences, so "2.8 GB"
 * never flips to "GB 2.8" when the paragraph direction is right-to-left.
 */
private val LatinUnitToken =
    Regex("(\\d+(?:[.,]\\d+)?)\\s?(%|°C?|GB|MB|KB|TB|mAh|MHz|GHz|fps)(?![\\p{L}\\p{N}])")

fun String.v2Bidi(): String {
    if (!ArabicLetters.containsMatchIn(this)) return this
    return LatinUnitToken.replace(this) { "\u2066${it.value}\u2069" }
}

fun kStyle(
    size: TextUnit = 12.sp,
    weight: FontWeight = FontWeight.Normal,
    color: Color = KnouxText,
    lineHeight: TextUnit = (size.value * 1.35f).sp,
    letterSpacing: TextUnit = 0.sp,
): TextStyle = TextStyle(
    fontFamily = V2Font,
    fontSize = size,
    fontWeight = weight,
    color = color,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
    textDirection = TextDirection.Content,
)

@Composable
fun KText(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 12.sp,
    weight: FontWeight = FontWeight.Normal,
    color: Color = KnouxText,
    lineHeight: TextUnit = (size.value * 1.35f).sp,
    align: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    letterSpacing: TextUnit = 0.sp,
) {
    val resolved = text.v2Bidi()
    val direction = if (ArabicLetters.containsMatchIn(text)) TextDirection.Rtl else TextDirection.Ltr
    Text(
        text = resolved,
        modifier = modifier,
        style = kStyle(size, weight, color, lineHeight, letterSpacing)
            .copy(textDirection = direction),
        textAlign = align,
        maxLines = maxLines,
        overflow = overflow,
    )
}

/* ------------------------------------------------------------------ *
 * Background
 * ------------------------------------------------------------------ */

@Composable
fun V2Background(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()) {
        drawRect(
            Brush.verticalGradient(
                0f to Color(0xFF060318),
                0.30f to V2Deep2,
                0.67f to Color(0xFF10062F),
                1f to Color(0xFF170940),
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(V2Cyan.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(size.width * 0.82f, size.height * 0.12f),
                radius = size.width * 0.68f,
            ),
            radius = size.width * 0.68f,
            center = Offset(size.width * 0.82f, size.height * 0.12f),
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(V2Pink.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(size.width * 0.08f, size.height * 0.35f),
                radius = size.width * 0.70f,
            ),
            radius = size.width * 0.70f,
            center = Offset(size.width * 0.08f, size.height * 0.35f),
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF7A2DFF).copy(alpha = 0.24f), Color.Transparent),
                center = Offset(size.width * 0.50f, size.height * 0.95f),
                radius = size.width * 0.90f,
            ),
            radius = size.width * 0.90f,
            center = Offset(size.width * 0.50f, size.height * 0.95f),
        )

        drawMountains()
        drawStars()
        drawEnergyRibbon()
    }
}

private fun DrawScope.drawMountains() {
    val back = Path().apply {
        moveTo(0f, size.height * 0.20f)
        lineTo(size.width * 0.11f, size.height * 0.13f)
        lineTo(size.width * 0.19f, size.height * 0.19f)
        lineTo(size.width * 0.31f, size.height * 0.09f)
        lineTo(size.width * 0.43f, size.height * 0.18f)
        lineTo(size.width * 0.55f, size.height * 0.12f)
        lineTo(size.width * 0.69f, size.height * 0.21f)
        lineTo(size.width * 0.84f, size.height * 0.11f)
        lineTo(size.width, size.height * 0.17f)
        lineTo(size.width, 0f)
        lineTo(0f, 0f)
        close()
    }
    drawPath(
        back,
        Brush.verticalGradient(
            listOf(Color(0xFF2A1661).copy(alpha = 0.62f), Color(0xFF0A0524).copy(alpha = 0f)),
            startY = 0f,
            endY = size.height * 0.25f,
        )
    )
    val front = Path().apply {
        moveTo(0f, size.height * 0.26f)
        lineTo(size.width * 0.09f, size.height * 0.21f)
        lineTo(size.width * 0.18f, size.height * 0.25f)
        lineTo(size.width * 0.30f, size.height * 0.16f)
        lineTo(size.width * 0.45f, size.height * 0.26f)
        lineTo(size.width * 0.60f, size.height * 0.19f)
        lineTo(size.width * 0.77f, size.height * 0.28f)
        lineTo(size.width * 0.89f, size.height * 0.20f)
        lineTo(size.width, size.height * 0.25f)
        lineTo(size.width, 0f)
        lineTo(0f, 0f)
        close()
    }
    drawPath(
        front,
        Brush.verticalGradient(
            listOf(Color(0xFF10072E).copy(alpha = 0.92f), Color(0xFF07031A).copy(alpha = 0f)),
            startY = 0f,
            endY = size.height * 0.30f,
        )
    )
}

private fun DrawScope.drawStars() {
    val stars = listOf(
        .05f to .06f, .13f to .15f, .21f to .05f, .29f to .18f, .38f to .08f,
        .49f to .14f, .58f to .04f, .66f to .20f, .74f to .09f, .84f to .16f,
        .92f to .06f, .97f to .22f, .08f to .30f, .17f to .39f, .28f to .34f,
        .36f to .46f, .47f to .33f, .59f to .44f, .71f to .36f, .82f to .47f,
        .94f to .35f, .09f to .57f, .23f to .63f, .41f to .58f, .57f to .68f,
        .75f to .61f, .88f to .70f, .12f to .81f, .33f to .88f, .63f to .84f,
        .86f to .91f,
    )
    stars.forEachIndexed { index, (x, y) ->
        drawCircle(
            color = if (index % 5 == 0) V2Cyan.copy(alpha = .40f) else Color.White.copy(alpha = .18f),
            radius = if (index % 7 == 0) 1.2.dp.toPx() else .7.dp.toPx(),
            center = Offset(size.width * x, size.height * y),
        )
    }
}

private fun DrawScope.drawEnergyRibbon() {
    val path = Path().apply {
        moveTo(-size.width * .1f, size.height * .57f)
        cubicTo(
            size.width * .18f, size.height * .44f,
            size.width * .38f, size.height * .64f,
            size.width * .62f, size.height * .51f,
        )
        cubicTo(
            size.width * .82f, size.height * .41f,
            size.width * .94f, size.height * .47f,
            size.width * 1.1f, size.height * .32f,
        )
    }
    drawPath(
        path = path,
        brush = Brush.linearGradient(
            listOf(Color.Transparent, V2Pink.copy(alpha = .20f), V2Cyan.copy(alpha = .15f), Color.Transparent),
            start = Offset(0f, size.height * .60f),
            end = Offset(size.width, size.height * .38f),
        ),
        style = Stroke(width = 1.2.dp.toPx()),
    )
}

/**
 * A stylised neon stag. The supplied reference uses a rendered deer; this
 * native vector keeps the same silhouette and lighting language without
 * adding a 2 MB bitmap or claiming to reproduce artwork we do not own.
 */
@Composable
fun StagCrest(
    modifier: Modifier = Modifier,
    tint: Color = V2Cyan,
    accent: Color = V2Violet,
    glowAlpha: Float = .20f,
) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val neck = Path().apply {
            moveTo(w * .56f, h)
            cubicTo(w * .55f, h * .76f, w * .58f, h * .57f, w * .51f, h * .43f)
            cubicTo(w * .45f, h * .30f, w * .39f, h * .24f, w * .32f, h * .25f)
            cubicTo(w * .24f, h * .26f, w * .20f, h * .33f, w * .25f, h * .38f)
            cubicTo(w * .29f, h * .42f, w * .34f, h * .39f, w * .35f, h * .34f)
            cubicTo(w * .43f, h * .44f, w * .43f, h * .65f, w * .32f, h)
            close()
        }
        drawPath(neck, Brush.verticalGradient(listOf(accent.copy(alpha = .88f), Color(0xFF1A0C3B))))
        drawPath(neck, Color.White.copy(alpha = .35f), style = Stroke(1.2.dp.toPx()))

        // ears
        val ear = Path().apply {
            moveTo(w * .33f, h * .28f)
            quadraticTo(w * .25f, h * .17f, w * .20f, h * .22f)
            quadraticTo(w * .24f, h * .30f, w * .32f, h * .32f)
            close()
        }
        drawPath(ear, tint.copy(alpha = .75f))

        // antlers
        fun antler(points: List<Pair<Float, Float>>) {
            val p = Path().apply {
                moveTo(w * .33f, h * .26f)
                points.forEach { (x, y) -> lineTo(w * x, h * y) }
            }
            drawPath(
                p,
                brush = Brush.linearGradient(listOf(tint, accent)),
                style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            drawPath(
                p,
                color = tint.copy(alpha = glowAlpha),
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
        antler(listOf(.34f to .15f, .24f to .10f, .22f to .03f))
        antler(listOf(.34f to .16f, .42f to .10f, .46f to .02f))
        antler(listOf(.31f to .19f, .18f to .16f, .12f to .08f))
        antler(listOf(.36f to .18f, .48f to .15f, .56f to .07f))
        antler(listOf(.28f to .22f, .16f to .24f, .10f to .19f))
        antler(listOf(.39f to .21f, .52f to .24f, .61f to .20f))
        antler(listOf(.24f to .10f, .15f to .04f))
        antler(listOf(.46f to .02f, .53f to .00f))

        // eye / nose lights
        drawCircle(Color.White.copy(alpha = .92f), radius = w * .012f, center = Offset(w * .27f, h * .32f))
        drawCircle(tint, radius = 2.dp.toPx(), center = Offset(w * .23f, h * .35f))
        drawCircle(tint.copy(alpha = .55f), radius = 18.dp.toPx(), center = Offset(w * .33f, h * .28f))
    }
}

/* ------------------------------------------------------------------ *
 * Header
 * ------------------------------------------------------------------ */

enum class V2TopMode { HOME, INTERIOR }

@Composable
fun V2TopBar(
    mode: V2TopMode,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onProfile: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onMenu: () -> Unit = {},
    onPro: () -> Unit = {},
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 16.dp),
    ) {
        Row(
            Modifier.align(Alignment.CenterStart),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CrownHexButton(onClick = onPro)
            HeaderIcon(Icons.Rounded.Menu, "القائمة", onMenu)
        }

        Row(
            Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            BrandLockupV2()
        }

        if (mode == V2TopMode.HOME) {
            Row(
                Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    HeaderIcon(Icons.Rounded.Person, "الحساب", onProfile)
                    Spacer(Modifier.width(7.dp))
                    Box {
                        HeaderIcon(Icons.Rounded.NotificationsNone, "التنبيهات", onNotifications)
                        Box(
                            Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(V2Red)
                                .border(1.dp, V2Deep2, CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = (-6).dp, y = 6.dp)
                        )
                    }
                }
            }
        } else {
            Box(Modifier.align(Alignment.CenterEnd)) {
                HeaderIcon(Icons.Rounded.ArrowBack, "رجوع", onBack)
            }
        }
    }
}

@Composable
fun HeaderIcon(icon: ImageVector, description: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = .035f))
            .border(1.dp, Color.White.copy(alpha = .14f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, description, tint = Color.White.copy(alpha = .92f), modifier = Modifier.size(19.dp))
    }
}

@Composable
fun BrandLockupV2() {
    // A brand lockup stays LTR even inside an RTL product shell.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.knoux_symbol_alpha),
                contentDescription = "KNOUX AI",
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.width(6.dp))
            Image(
                painter = painterResource(R.drawable.knoux_wordmark_alpha),
                contentDescription = "knoux ai",
                modifier = Modifier.height(14.5.dp),
            )
            Spacer(Modifier.width(7.dp))
            ProPill()
        }
    }
}

@Composable
fun ProPill(modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(50))
            .background(Brush.horizontalGradient(listOf(Color(0x33FFC857), Color(0x0EFFC857))))
            .border(1.dp, V2Amber.copy(alpha = .72f), RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 3.dp),
    ) {
        KText("PRO", size = 8.sp, weight = FontWeight.Bold, color = V2Amber, letterSpacing = .6.sp)
    }
}

@Composable
fun CrownHexButton(onClick: () -> Unit = {}) {
    Canvas(
        Modifier
            .size(34.dp)
            .clickable(onClick = onClick)
    ) {
        val path = hexagonPath(size.width * .5f, size.height * .5f, size.width * .47f)
        drawPath(path, Brush.linearGradient(listOf(Color(0x44FFC857), Color(0x0CFF9D00))))
        drawPath(path, V2Amber.copy(alpha = .85f), style = Stroke(1.dp.toPx()))
        drawCrown(
            center = Offset(size.width * .5f, size.height * .50f),
            width = size.width * .50f,
            height = size.height * .38f,
            color = V2Amber,
        )
    }
}

@Composable
fun CrownGold(size: Dp = 30.dp, color: Color = V2Amber) {
    Canvas(Modifier.size(size)) {
        drawCrown(
            Offset(this.size.width / 2f, this.size.height * .48f),
            this.size.width * .92f,
            this.size.height * .56f,
            color,
        )
    }
}

private fun hexagonPath(cx: Float, cy: Float, radius: Float): Path = Path().apply {
    for (i in 0 until 6) {
        val a = Math.toRadians((60.0 * i - 30.0))
        val x = cx + (radius * cos(a)).toFloat()
        val y = cy + (radius * sin(a)).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

private fun DrawScope.drawCrown(center: Offset, width: Float, height: Float, color: Color) {
    val left = center.x - width / 2f
    val top = center.y - height * .35f
    val p = Path().apply {
        moveTo(left, top + height * .64f)
        lineTo(left + width * .07f, top + height * .16f)
        lineTo(left + width * .34f, top + height * .40f)
        lineTo(left + width * .50f, top)
        lineTo(left + width * .66f, top + height * .40f)
        lineTo(left + width * .93f, top + height * .16f)
        lineTo(left + width, top + height * .64f)
        close()
    }
    drawPath(p, color)
    drawRoundRect(
        color = color,
        topLeft = Offset(left + width * .06f, top + height * .76f),
        size = Size(width * .88f, height * .20f),
        cornerRadius = CornerRadius(width * .04f),
    )
}

/* ------------------------------------------------------------------ *
 * Bottom bar — five destinations, elevated Pro crown.
 * ------------------------------------------------------------------ */

data class V2Tab(
    val route: String,
    val label: String,
    val icon: ImageVector?,
)

val V2Tabs = listOf(
    V2Tab("home", "الرئيسية", Icons.Rounded.Home),
    V2Tab("clean", "تنظيف", Icons.Rounded.CleaningServices),
    V2Tab("pro", "Pro", null),
    V2Tab("tools", "الأدوات", Icons.Rounded.GridView),
    V2Tab("reports", "التقارير", Icons.Rounded.BarChart),
)

@Composable
fun V2BottomBar(
    selectedRoute: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = when (selectedRoute) {
        "home" -> "home"
        "clean", "smartclean", "deepscan", "duplicates", "residue", "duplicate-group",
        "deep-scan-summary", "residue-item" -> "clean"
        "tools", "storage", "appmanager", "app-detail", "privacy", "battery", "ai",
        "storage-breakdown", "installed-apps", "battery-diagnostics",
        "ai-recommendations", "privacy-risk" -> "tools"
        "reports", "performance-report" -> "reports"
        "pro" -> "pro"
        else -> "home"
    }

    Box(modifier.fillMaxWidth().height(88.dp).padding(horizontal = 12.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val top = 15.dp.toPx()
            val shape = Path().apply {
                addRoundRect(
                    androidx.compose.ui.geometry.RoundRect(
                        0f, top, size.width, size.height,
                        CornerRadius(30.dp.toPx()),
                    )
                )
            }
            drawPath(shape, Brush.verticalGradient(listOf(Color(0xFF291453), Color(0xFF0D0728))))
            drawPath(shape, V2Border.copy(alpha = .48f), style = Stroke(1.dp.toPx()))
            drawPath(
                shape,
                Brush.verticalGradient(
                    listOf(Color.Transparent, V2Cyan.copy(alpha = .10f), Color.Transparent),
                    0f,
                    size.height,
                )
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            V2Tabs.forEach { tab ->
                if (tab.route == "pro") {
                    Spacer(Modifier.weight(1f))
                } else {
                    BottomItem(
                        tab = tab,
                        active = selected == tab.route,
                        onClick = { onSelect(tab.route) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        Box(
            Modifier
                .align(Alignment.TopCenter)
                .size(74.dp)
                .clickable(
                    interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() },
                    indication = null,
                ) { onSelect("pro") },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(V2Amber.copy(alpha = .38f), Color.Transparent),
                        radius = size.minDimension * .5f,
                    ),
                    radius = size.minDimension * .5f,
                )
            }
            Box(
                Modifier
                    .size(66.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF4A2A20), Color(0xFF1D0F0B))
                        )
                    )
                    .border(2.dp, V2Amber.copy(alpha = .9f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = .22f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CrownGold(size = 24.dp)
                    KText("Pro", size = 10.sp, weight = FontWeight.Bold, color = V2GoldText)
                }
            }
        }
    }
}

@Composable
private fun RowScope.BottomItem(
    tab: V2Tab,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(19.dp))
            .then(
                if (active) {
                    Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF6F34C9).copy(alpha = .82f), Color(0xFF31145F).copy(alpha = .66f))
                            )
                        )
                        .border(1.dp, V2Violet.copy(alpha = .52f), RoundedCornerShape(19.dp))
                } else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                tab.icon ?: return@Column,
                contentDescription = tab.label,
                tint = if (active) Color.White else V2Soft.copy(alpha = .82f),
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.height(2.dp))
            KText(
                tab.label,
                size = 9.5.sp,
                weight = if (active) FontWeight.Bold else FontWeight.Medium,
                color = if (active) Color.White else V2Soft.copy(alpha = .85f),
                maxLines = 1,
            )
        }
    }
}

/* ------------------------------------------------------------------ *
 * Surfaces and controls
 * ------------------------------------------------------------------ */

@Composable
fun V2GlassCard(
    modifier: Modifier = Modifier,
    padding: Dp = 14.dp,
    radius: Dp = 20.dp,
    glow: Color = V2Violet,
    borderAlpha: Float = .48f,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    Box(
        modifier
            .clip(shape)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(glow.copy(alpha = .16f), Color.Transparent),
                        center = Offset(size.width * .12f, 0f),
                        radius = size.width * .85f,
                    ),
                    radius = size.width * .85f,
                    center = Offset(size.width * .12f, 0f),
                )
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, glow.copy(alpha = .52f), Color.Transparent)
                    ),
                    start = Offset(size.width * .10f, 1.dp.toPx()),
                    end = Offset(size.width * .90f, 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .background(Brush.verticalGradient(listOf(V2GlassTop.copy(alpha = .93f), V2GlassBottom.copy(alpha = .98f))))
            .border(1.dp, glow.copy(alpha = borderAlpha), shape)
            .padding(padding),
        content = content,
    )
}

@Composable
fun V2SectionLabel(title: String, modifier: Modifier = Modifier, trailing: String? = null) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        KText(title, size = 15.sp, weight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        if (trailing != null) KText(trailing, size = 10.sp, color = V2Muted)
    }
}

@Composable
fun V2GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    height: Dp = 48.dp,
    contentColor: Color = Color.White,
    trailingChevron: Boolean = false,
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(V2Pink, V2Violet, V2Blue, V2Cyan)))
            .border(1.dp, Color.White.copy(alpha = .28f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = contentColor, modifier = Modifier.size(19.dp))
            Spacer(Modifier.width(8.dp))
        }
        KText(text, size = 15.sp, weight = FontWeight.Bold, color = contentColor)
        if (trailingChevron) {
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Rounded.ArrowBack, null, tint = contentColor, modifier = Modifier.size(17.dp))
        }
    }
}

@Composable
fun V2GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(Color(0xFF4C2C17), Color(0xFF20110B), Color(0xFF6C4420))))
            .border(1.dp, V2Amber.copy(alpha = .78f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = V2GoldText, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(7.dp))
        }
        KText(text, size = 14.sp, weight = FontWeight.Bold, color = V2GoldText)
    }
}

/** Bright champagne CTA used by the privacy / Pro reference screens. */
@Composable
fun V2BrightGoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFFFE9B0), Color(0xFFD79A34), Color(0xFFFFD37B), Color(0xFFB8741A))
                )
            )
            .border(1.dp, Color(0xFFFFE9B0).copy(alpha = .85f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = Color(0xFF2A1705), modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(7.dp))
        }
        KText(text, size = 15.sp, weight = FontWeight.Black, color = Color(0xFF2A1705))
    }
}

@Composable
fun V2GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    height: Dp = 44.dp,
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(Color.White.copy(alpha = .035f))
            .border(1.dp, V2Border.copy(alpha = .50f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = V2Soft, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
        }
        KText(text, size = 12.5.sp, weight = FontWeight.Bold, color = KnouxText, maxLines = 1)
    }
}

@Composable
fun V2NeonRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 92.dp,
    stroke: Dp = 8.dp,
    colors: List<Color> = listOf(V2Cyan, V2Blue, V2Violet, V2Pink),
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = stroke.toPx()
            val inset = sw / 2f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(colors.last().copy(alpha = .20f), Color.Transparent),
                    radius = this.size.minDimension * .53f,
                ),
                radius = this.size.minDimension * .53f,
            )
            drawArc(
                color = Color.White.copy(alpha = .08f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(this.size.width - sw, this.size.height - sw),
                style = Stroke(sw, cap = StrokeCap.Round),
            )
            val sweep = 360f * progress.coerceIn(0f, 1f)
            val brush = Brush.sweepGradient(colors, center)
            drawArc(
                brush = brush,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(this.size.width - sw, this.size.height - sw),
                style = Stroke(sw * 3.3f, cap = StrokeCap.Round),
                alpha = .18f,
            )
            drawArc(
                brush = brush,
                startAngle = -90f,
                sweepAngle = (sweep - 4f).coerceAtLeast(0f),
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(this.size.width - sw, this.size.height - sw),
                style = Stroke(sw, cap = StrokeCap.Round),
            )
            if (progress > .02f) {
                val angle = Math.toRadians((-90f + sweep).toDouble())
                val r = (this.size.width - sw) / 2f
                drawCircle(
                    Color.White.copy(alpha = .95f),
                    radius = sw * .34f,
                    center = Offset(center.x + (r * cos(angle)).toFloat(), center.y + (r * sin(angle)).toFloat()),
                )
            }
        }
        content()
    }
}

@Composable
fun V2ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    colors: List<Color> = listOf(V2Cyan, V2Blue),
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = .09f))
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxSize()
                .clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(colors))
        )
    }
}

@Composable
fun V2MetricRow(
    icon: ImageVector,
    label: String,
    value: String,
    progress: Float,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accent.copy(alpha = .10f))
                .border(1.dp, accent.copy(alpha = .28f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(7.dp))
        KText(label, size = 10.sp, weight = FontWeight.Medium, color = V2Soft, modifier = Modifier.width(52.dp))
        Column(Modifier.weight(1f)) {
            V2ProgressBar(progress, height = 5.dp, colors = listOf(accent, V2Cyan))
        }
        Spacer(Modifier.width(7.dp))
        KText(value, size = 8.8.sp, color = Color.White, weight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
fun V2SearchField(
    placeholder: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = .045f))
            .border(1.dp, V2Border.copy(alpha = .32f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.Search, null, tint = V2Soft, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(7.dp))
        KText(placeholder, size = 10.5.sp, color = V2Muted)
    }
}

@Composable
fun V2IconTile(
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    iconSize: Dp = 18.dp,
) {
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(size * .31f))
            .background(Brush.linearGradient(listOf(accent.copy(alpha = .28f), Color(0xFF1B1040))))
            .border(1.dp, accent.copy(alpha = .50f), RoundedCornerShape(size * .31f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(iconSize))
    }
}

@Composable
fun V2RasterTile(
    @DrawableRes image: Int,
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    accent: Color = V2Violet,
) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(accent.copy(alpha = .34f), Color.Transparent),
                    radius = this.size.minDimension * .54f,
                ),
                radius = this.size.minDimension * .54f,
            )
            drawOval(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, accent.copy(alpha = .58f), Color.Transparent)
                ),
                topLeft = Offset(this.size.width * .13f, this.size.height * .92f),
                size = Size(this.size.width * .74f, 2.dp.toPx()),
            )
        }
        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(.94f),
            contentScale = ContentScale.Fit,
            colorFilter = null,
            alpha = 1f,
        )
    }
}

@Composable
fun V2Chip(
    text: String,
    active: Boolean = false,
    accent: Color = V2Cyan,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier
            .clip(shape)
            .background(
                if (active) Brush.horizontalGradient(listOf(V2Cyan.copy(alpha = .85f), V2Blue.copy(alpha = .90f)))
                else Brush.horizontalGradient(listOf(Color.White.copy(alpha = .05f), Color.White.copy(alpha = .02f)))
            )
            .border(if (active) 0.dp else 1.dp, if (active) Color.Transparent else accent.copy(alpha = .28f), shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 13.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KText(
            text,
            size = 10.5.sp,
            weight = if (active) FontWeight.Bold else FontWeight.Medium,
            color = if (active) V2Deep else V2Soft,
        )
    }
}

@Composable
fun V2StatCell(value: String, label: String, accent: Color, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        KText(value, size = 12.5.sp, weight = FontWeight.Bold, color = accent)
        Spacer(Modifier.height(1.dp))
        KText(label, size = 8.5.sp, color = V2Soft, maxLines = 1)
    }
}

/**
 * The KNOUX stag is the product mascot. The reference artwork is kept as a
 * raster asset so the antler filaments and holographic fur survive at phone
 * density; a very slow float/breath loop gives it life without distracting.
 */
@Composable
fun V2DeerHeader(
    modifier: Modifier = Modifier,
    mirrored: Boolean = false,
    animated: Boolean = true,
) {
    val transition = rememberInfiniteTransition(label = "knouxDeer")
    val float by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animated) -7f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "deerFloat",
    )
    val breath by transition.animateFloat(
        initialValue = .985f,
        targetValue = if (animated) 1.012f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "deerBreath",
    )
    Image(
        painter = painterResource(R.drawable.knoux_deer_mascot),
        contentDescription = null,
        modifier = modifier.graphicsLayer {
            translationY = float.dp.toPx()
            scaleX = if (mirrored) -breath else breath
            scaleY = breath
        },
        contentScale = ContentScale.Fit,
    )
}

/* ------------------------------------------------------------------ *
 * Custom raster-free app marks
 * ------------------------------------------------------------------ */

enum class AppMark { TikTok, WhatsApp, YouTube, Telegram, Photos, Generic }

@Composable
fun AppLogo(mark: AppMark, size: Dp = 42.dp, modifier: Modifier = Modifier) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        when (mark) {
            AppMark.TikTok -> {
                Box(
                    Modifier.fillMaxSize().clip(CircleShape).background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        val w = this.size.width
                        val h = this.size.height
                        val base = Offset(w * .50f, h * .52f)
                        drawCircle(V2Cyan.copy(alpha = .9f), radius = w * .14f, center = base + Offset(-w * .075f, 0f))
                        drawCircle(V2Pink.copy(alpha = .85f), radius = w * .14f, center = base + Offset(w * .075f, 0f))
                        drawCircle(Color.White, radius = w * .10f, center = base)
                    }
                }
            }
            AppMark.WhatsApp -> {
                Box(
                    Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFF25D366)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Call,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(size * .52f),
                    )
                }
            }
            AppMark.YouTube -> {
                Box(
                    Modifier.fillMaxSize().clip(RoundedCornerShape(size * .28f)).background(Color(0xFFFF0033)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(size * .56f),
                    )
                }
            }
            AppMark.Telegram, AppMark.Photos, AppMark.Generic -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            when (mark) {
                                AppMark.Telegram -> Brush.linearGradient(listOf(Color(0xFF2AABEE), Color(0xFF229ED9)))
                                AppMark.Photos -> Brush.linearGradient(listOf(V2Pink, V2Amber, V2Cyan, Color(0xFF33D17A)))
                                else -> Brush.linearGradient(listOf(Color(0xFF6C7AFF), V2Cyan))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (mark == AppMark.Telegram) Icons.AutoMirrored.Rounded.Send else Icons.Rounded.GridView,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(size * .47f),
                    )
                }
            }
        }
    }
}

@Composable
fun MiniMountainThumb(
    modifier: Modifier = Modifier,
    accent: Color = V2Violet,
    showCity: Boolean = false,
) {
    Canvas(modifier.clip(RoundedCornerShape(12.dp))) {
        drawRect(
            Brush.verticalGradient(
                listOf(Color(0xFF2A155C), Color(0xFF131036), accent.copy(alpha = .34f))
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFFD882).copy(alpha = .55f), Color.Transparent),
                radius = size.width * .53f,
            ),
            radius = size.width * .53f,
            center = Offset(size.width * .55f, size.height * .60f),
        )
        val ridge1 = Path().apply {
            moveTo(0f, size.height * .64f)
            lineTo(size.width * .26f, size.height * .44f)
            lineTo(size.width * .43f, size.height * .62f)
            lineTo(size.width * .61f, size.height * .32f)
            lineTo(size.width * .82f, size.height * .60f)
            lineTo(size.width, size.height * .48f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(ridge1, Color(0xFF23104A))
        if (showCity) {
            for (i in 0..5) {
                drawRect(
                    color = Color(0xFF0D0824),
                    topLeft = Offset(size.width * (.48f + i * .07f), size.height * (.50f + (i % 2) * .08f)),
                    size = Size(size.width * .055f, size.height * .30f),
                )
            }
        }
        val ridge2 = Path().apply {
            moveTo(0f, size.height * .83f)
            lineTo(size.width * .30f, size.height * .65f)
            lineTo(size.width * .50f, size.height * .82f)
            lineTo(size.width * .70f, size.height * .63f)
            lineTo(size.width, size.height * .84f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(
            ridge2,
            Brush.verticalGradient(listOf(Color(0xFF130A31), Color(0xFF09051C)))
        )
    }
}
