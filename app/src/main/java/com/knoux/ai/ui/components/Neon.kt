package com.knoux.ai.ui.components

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.theme.KnouxAmber
import com.knoux.ai.ui.theme.KnouxBackdrop
import com.knoux.ai.ui.theme.KnouxBackdropDeep
import com.knoux.ai.ui.theme.KnouxBlue
import com.knoux.ai.ui.theme.KnouxBorder
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxMutedText
import com.knoux.ai.ui.theme.KnouxPanel
import com.knoux.ai.ui.theme.KnouxPanelAlt
import com.knoux.ai.ui.theme.KnouxPanelDeep
import com.knoux.ai.ui.theme.KnouxPink
import com.knoux.ai.ui.theme.KnouxSoftText
import com.knoux.ai.ui.theme.KnouxText
import com.knoux.ai.ui.theme.KnouxViolet
import com.knoux.ai.ui.theme.KnouxWave
import kotlin.math.cos
import kotlin.math.sin

/* ------------------------------------------------------------------ *
 * Background
 * ------------------------------------------------------------------ */

@Composable
fun KnouxBackground(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()) {
        drawRect(
            Brush.verticalGradient(
                0f to KnouxBackdropDeep,
                0.45f to KnouxBackdrop,
                1f to Color(0xFF120A33)
            )
        )
        // left and right purple wave glows
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(KnouxWave.copy(alpha = 0.85f), Color.Transparent),
                center = Offset(size.width * 0.06f, size.height * 0.86f),
                radius = size.minDimension * 0.85f
            ),
            radius = size.minDimension * 0.85f,
            center = Offset(size.width * 0.06f, size.height * 0.86f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF3A1070).copy(alpha = 0.7f), Color.Transparent),
                center = Offset(size.width * 0.97f, size.height * 0.72f),
                radius = size.minDimension * 0.7f
            ),
            radius = size.minDimension * 0.7f,
            center = Offset(size.width * 0.97f, size.height * 0.72f)
        )
    }
}

/* ------------------------------------------------------------------ *
 * Brand
 * ------------------------------------------------------------------ */

@Composable
fun BrandSymbol(modifier: Modifier = Modifier, size: Dp = 30.dp) {
    Image(
        painter = painterResource(R.drawable.knoux_symbol_alpha),
        contentDescription = "KNOUX AI",
        modifier = modifier.size(size),
    )
}

@Composable
fun BrandWordmark(modifier: Modifier = Modifier, height: Dp = 17.dp) {
    Image(
        painter = painterResource(R.drawable.knoux_wordmark_alpha),
        contentDescription = "knoux ai",
        modifier = modifier.height(height),
    )
}

@Composable
fun BrandLockup(modifier: Modifier = Modifier, symbolSize: Dp = 28.dp, wordmarkHeight: Dp = 15.dp) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        BrandSymbol(size = symbolSize)
        Spacer(Modifier.width(7.dp))
        BrandWordmark(height = wordmarkHeight)
    }
}

/* ------------------------------------------------------------------ *
 * Top bar
 * ------------------------------------------------------------------ */

enum class TopLeading { Back, Menu, None }

@Composable
fun KnouxTopBar(
    modifier: Modifier = Modifier,
    leading: TopLeading = TopLeading.Back,
    onLeadingClick: () -> Unit = {},
    right: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (leading) {
            TopLeading.Back -> IconCircle(Icons.Rounded.ArrowBack, "Back", onLeadingClick)
            TopLeading.Menu -> IconCircle(Icons.Rounded.Menu, "Menu", onLeadingClick)
            TopLeading.None -> Spacer(Modifier.width(38.dp))
        }
        Spacer(Modifier.width(10.dp))
        BrandLockup()
        Spacer(Modifier.weight(1f))
        right()
    }
}

@Composable
fun IconCircle(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    tint: Color = KnouxText,
    size: Dp = 34.dp,
) {
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription, tint = tint, modifier = Modifier.size(size * 0.62f))
    }
}

@Composable
fun ProBadge(modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0x33FFC24B), Color(0x1AFFC24B), Color(0x33FFC24B))
                )
            )
            .border(1.dp, KnouxAmber.copy(alpha = 0.75f), RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            Icons.Rounded.AutoAwesome, null,
            tint = KnouxAmber, modifier = Modifier.size(11.dp)
        )
        Text(
            "Pro",
            style = TextStyle(
                fontWeight = FontWeight.Bold, fontSize = 11.sp, color = KnouxAmber
            )
        )
    }
}

/* ------------------------------------------------------------------ *
 * Panels and buttons
 * ------------------------------------------------------------------ */

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    padding: Dp = 14.dp,
    radius: Dp = 20.dp,
    borderColor: Color = KnouxBorder.copy(alpha = 0.55f),
    content: @Composable () -> Unit,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(radius))
            .background(
                Brush.verticalGradient(
                    listOf(KnouxPanel.copy(alpha = 0.95f), KnouxPanelDeep.copy(alpha = 0.92f))
                )
            )
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(radius))
            .padding(padding),
        content = { content() },
    )
}

@Composable
fun GradientActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trailingChevron: Boolean = false,
    compact: Boolean = false,
) {
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(
                    listOf(KnouxViolet, Color(0xFF6A5BFF), KnouxCyan)
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = if (compact) 14.dp else 22.dp, vertical = if (compact) 8.dp else 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(if (compact) 13.dp else 18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 11.sp else 16.sp,
                color = Color.White
            )
        )
        if (trailingChevron) {
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Rounded.ChevronRight, null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    compact: Boolean = false,
) {
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, KnouxViolet.copy(alpha = 0.75f), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = if (compact) 14.dp else 20.dp, vertical = if (compact) 8.dp else 13.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = KnouxText, modifier = Modifier.size(if (compact) 13.dp else 17.dp))
            Spacer(Modifier.width(7.dp))
        }
        Text(
            text,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 11.sp else 14.sp,
                color = KnouxText
            )
        )
    }
}

/** Small cyan action pill used inside cards ("Clean", "Review", ...). */
@Composable
fun MiniPill(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(listOf(KnouxCyan, Color(0xFF48A8FF)))
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF032033))
        )
    }
}

/* ------------------------------------------------------------------ *
 * Metrics
 * ------------------------------------------------------------------ */

@Composable
fun ProgressDonut(
    progress: Float,
    centerText: String,
    caption: String? = null,
    arcColors: List<Color> = listOf(KnouxCyan, KnouxMagenta),
    size: Dp = 78.dp,
    stroke: Dp = 9.dp,
    modifier: Modifier = Modifier,
) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = stroke.toPx()
            val inset = sw / 2
            val arcRect = Rect(inset, inset, size.toPx() - inset, size.toPx() - inset)
            drawArc(
                color = Color(0x33FFFFFF),
                startAngle = -90f, sweepAngle = 360f, useCenter = false,
                topLeft = Offset(arcRect.left, arcRect.top), size = Size(arcRect.width, arcRect.height),
                style = Stroke(width = sw, cap = StrokeCap.Round)
            )
            var start = -90f
            val total = arcColors.size
            arcColors.forEachIndexed { index, c ->
                val sweep = 360f * progress / total
                drawArc(
                    color = c,
                    startAngle = start, sweepAngle = sweep - 4f, useCenter = false,
                    topLeft = Offset(arcRect.left, arcRect.top),
                    size = Size(arcRect.width, arcRect.height),
                    style = Stroke(width = sw, cap = StrokeCap.Round)
                )
                start += sweep
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                centerText,
                style = TextStyle(
                    fontWeight = FontWeight.Bold, fontSize = 19.sp, color = KnouxText
                )
            )
            if (caption != null) {
                Text(
                    caption,
                    style = TextStyle(fontSize = 8.5.sp, color = KnouxSoftText),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun GradientProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(Color(0x22FFFFFF))
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxSize()
                .clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(listOf(KnouxMagenta, KnouxViolet, KnouxCyan)))
        )
    }
}

@Composable
fun SectionHeader(title: String, trailing: @Composable (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            style = TextStyle(
                fontWeight = FontWeight.Bold, fontSize = 16.sp, color = KnouxText
            )
        )
        Spacer(Modifier.weight(1f))
        trailing?.invoke()
    }
}

/* ------------------------------------------------------------------ *
 * Icons tiles
 * ------------------------------------------------------------------ */

@Composable
fun NeonIconTile(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    tint: Color = KnouxCyan,
    iconSize: Dp = 20.dp,
    radius: Dp = 12.dp,
) {
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(radius))
            .background(
                Brush.linearGradient(
                    listOf(KnouxViolet.copy(alpha = 0.38f), KnouxPanelAlt.copy(alpha = 0.9f))
                )
            )
            .border(1.dp, KnouxViolet.copy(alpha = 0.35f), RoundedCornerShape(radius)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(iconSize))
    }
}

/* ------------------------------------------------------------------ *
 * Hero artwork (all drawn with Compose Canvas - no bitmap backgrounds)
 * ------------------------------------------------------------------ */

enum class HeroKind { Orb, Shield, Images, Radar, Bars, Layers, Grid, Disks, Battery, Chip }

@Composable
fun NeonHero(
    kind: HeroKind,
    modifier: Modifier = Modifier,
    height: Dp = 168.dp,
    animate: Boolean = true,
) {
    val transition = rememberInfiniteTransition(label = "hero")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(modifier.fillMaxWidth().height(height), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawHeroBackdrop(kind, pulse)
            drawHero(kind, phase, pulse)
        }
        if (kind == HeroKind.Orb) {
            Image(
                painter = painterResource(R.drawable.knoux_symbol_alpha),
                contentDescription = null,
                modifier = Modifier.size(92.dp),
            )
        }
    }
}

private fun DrawScope.drawHeroBackdrop(kind: HeroKind, pulse: Float) {
    val c = center
    val r = size.minDimension * 0.34f * pulse
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(KnouxViolet.copy(alpha = 0.42f), Color.Transparent),
            center = c, radius = r * 1.9f
        ),
        radius = r * 1.9f, center = c
    )
}

private fun DrawScope.drawHero(kind: HeroKind, phase: Float, pulse: Float) {
    val c = center
    val r = size.minDimension * 0.30f
    when (kind) {
        HeroKind.Orb -> {
            drawCircle(
                brush = Brush.radialGradient(
                    0.00f to Color(0xFF9FF6FF),
                    0.28f to KnouxCyan.copy(alpha = 0.85f),
                    0.58f to KnouxViolet.copy(alpha = 0.80f),
                    0.86f to KnouxMagenta.copy(alpha = 0.55f),
                    1.00f to Color(0x22060B2E)
                ),
                radius = r, center = c
            )
            // segmented orbital ring
            drawCircle(
                color = KnouxCyan.copy(alpha = 0.75f),
                radius = r * 1.22f, center = c,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(46f, 26f), phase)
            )
            drawCircle(
                color = KnouxMagenta.copy(alpha = 0.55f),
                radius = r * 1.40f, center = c,
                style = Stroke(width = 2.dp.toPx()),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 34f), -phase)
            )
            drawArc(
                color = Color.White.copy(alpha = 0.35f),
                startAngle = 200f, sweepAngle = 80f, useCenter = false,
                topLeft = Offset(c.x - r, c.y - r), size = Size(r * 2, r * 2),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        HeroKind.Shield -> {
            val w = r * 1.25f
            val h = r * 1.55f
            val path = Path().apply {
                moveTo(c.x, c.y - h)
                lineTo(c.x + w, c.y - h * 0.72f)
                quadraticBezierTo(c.x + w, c.y + h * 0.35f, c.x, c.y + h)
                quadraticBezierTo(c.x - w, c.y + h * 0.35f, c.x - w, c.y - h * 0.72f)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    listOf(KnouxCyan.copy(alpha = 0.85f), KnouxViolet.copy(alpha = 0.9f), KnouxMagenta.copy(alpha = 0.85f))
                )
            )
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.55f),
                style = Stroke(width = 2.dp.toPx())
            )
            // padlock
            val bw = w * 0.52f
            val bh = bw * 0.78f
            drawRoundRect(
                color = Color.White.copy(alpha = 0.92f),
                topLeft = Offset(c.x - bw / 2, c.y - bh * 0.10f),
                size = Size(bw, bh),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
            )
            drawArc(
                color = Color.White.copy(alpha = 0.92f),
                startAngle = 180f, sweepAngle = 180f, useCenter = false,
                topLeft = Offset(c.x - bw * 0.30f, c.y - bh * 0.62f),
                size = Size(bw * 0.60f, bw * 0.60f),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            drawCircle(KnouxBackdrop, radius = 4.dp.toPx(), center = Offset(c.x, c.y + bh * 0.25f))
        }

        HeroKind.Images -> {
            // back frame
            drawRoundRect(
                brush = Brush.linearGradient(listOf(KnouxMagenta.copy(alpha = 0.75f), KnouxViolet.copy(alpha = 0.75f))),
                topLeft = Offset(c.x - r * 0.95f, c.y - r * 0.95f),
                size = Size(r * 1.25f, r * 1.45f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx())
            )
            // front frame
            val fx = c.x - r * 0.45f
            val fy = c.y - r * 0.35f
            drawRoundRect(
                brush = Brush.linearGradient(listOf(KnouxCyan.copy(alpha = 0.85f), KnouxBlue.copy(alpha = 0.85f))),
                topLeft = Offset(fx, fy),
                size = Size(r * 1.30f, r * 1.50f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
            )
            drawRoundRect(
                color = KnouxBackdrop.copy(alpha = 0.55f),
                topLeft = Offset(fx + 8.dp.toPx(), fy + 8.dp.toPx()),
                size = Size(r * 1.30f - 16.dp.toPx(), r * 1.50f - 16.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx())
            )
            // mountain glyph
            val m = Path().apply {
                moveTo(fx + 14.dp.toPx(), fy + r * 1.05f)
                lineTo(fx + r * 0.65f, fy + r * 0.55f)
                lineTo(fx + r * 1.05f, fy + r * 1.05f)
                close()
            }
            drawPath(m, KnouxCyan.copy(alpha = 0.9f))
            drawCircle(
                KnouxPink.copy(alpha = 0.95f), radius = 6.dp.toPx(),
                center = Offset(fx + r * 0.85f, fy + r * 0.35f)
            )
        }

        HeroKind.Radar -> {
            drawCircle(
                brush = Brush.radialGradient(
                    0f to KnouxCyan.copy(alpha = 0.55f),
                    0.6f to KnouxViolet.copy(alpha = 0.55f),
                    1f to Color(0x22060B2E)
                ),
                radius = r, center = c
            )
            for (i in 1..3) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.22f),
                    radius = r * i / 3f, center = c,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            // latitude arcs
            for (i in 1..2) {
                val rr = r * (i / 3f)
                drawArc(
                    color = Color.White.copy(alpha = 0.18f),
                    startAngle = 200f, sweepAngle = 140f, useCenter = false,
                    topLeft = Offset(c.x - rr, c.y - rr * 0.55f),
                    size = Size(rr * 2, rr * 1.1f),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            // sweep
            val rad = Math.toRadians(phase.toDouble())
            drawLine(
                color = KnouxCyan.copy(alpha = 0.85f),
                start = c,
                end = Offset(c.x + (r * cos(rad)).toFloat(), c.y + (r * sin(rad)).toFloat()),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            listOf(
                Offset(c.x + r * 0.45f, c.y - r * 0.30f),
                Offset(c.x - r * 0.40f, c.y + r * 0.35f),
                Offset(c.x + r * 0.15f, c.y + r * 0.62f),
                Offset(c.x - r * 0.62f, c.y - r * 0.42f),
            ).forEach {
                drawCircle(KnouxCyan, radius = 3.5.dp.toPx(), center = it)
            }
            drawCircle(Color.White.copy(alpha = 0.85f), radius = 3.dp.toPx(), center = c)
        }

        HeroKind.Bars -> {
            val barW = r * 0.32f
            val gap = r * 0.22f
            val base = c.y + r * 0.75f
            for (i in 0..3) {
                val h = r * (0.55f + 0.38f * i)
                val x = c.x - (barW * 3f + gap * 2f) / 2f + i * (barW + gap)
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(KnouxMagenta, KnouxViolet)),
                    topLeft = Offset(x, base - h),
                    size = Size(barW, h),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(barW / 2)
                )
            }
            // rising arrow
            val p = Path().apply {
                moveTo(c.x - r * 0.85f, c.y + r * 0.30f)
                lineTo(c.x + r * 0.10f, c.y - r * 0.05f)
                lineTo(c.x + r * 0.35f, c.y + r * 0.10f)
                lineTo(c.x + r * 0.85f, c.y - r * 0.55f)
            }
            drawPath(
                p, KnouxCyan,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawLine(
                KnouxCyan, Offset(c.x + r * 0.60f, c.y - r * 0.55f), Offset(c.x + r * 0.86f, c.y - r * 0.55f),
                strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round
            )
        }

        HeroKind.Layers -> {
            for (i in 0..2) {
                val w = r * (1.25f - i * 0.14f)
                val h = w * 0.55f
                val y = c.y + r * 0.45f - i * h * 0.78f
                val path = Path().apply {
                    moveTo(c.x - w / 2, y)
                    lineTo(c.x, y - h / 2)
                    lineTo(c.x + w / 2, y)
                    lineTo(c.x, y + h / 2)
                    close()
                }
                drawPath(
                    path,
                    brush = Brush.linearGradient(listOf(KnouxCyan.copy(alpha = 0.9f), KnouxViolet.copy(alpha = 0.9f)))
                )
                drawPath(path, Color.White.copy(alpha = 0.45f), style = Stroke(width = 1.5.dp.toPx()))
            }
        }

        HeroKind.Grid -> {
            val s = r * 0.86f
            val gap = r * 0.22f
            for (row in 0..1) {
                for (col in 0..1) {
                    val x = c.x - s - gap / 2 + col * (s + gap)
                    val y = c.y - s - gap / 2 + row * (s + gap)
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            listOf(KnouxCyan.copy(alpha = 0.85f), KnouxMagenta.copy(alpha = 0.85f))
                        ),
                        topLeft = Offset(x, y),
                        size = Size(s, s),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
                    )
                }
            }
        }

        HeroKind.Disks -> {
            for (i in 0..2) {
                val w = r * 1.5f
                val h = w * 0.36f
                val y = c.y - r * 0.62f + i * h * 0.92f
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(KnouxCyan.copy(alpha = 0.9f), KnouxViolet.copy(alpha = 0.9f))
                    ),
                    topLeft = Offset(c.x - w / 2, y),
                    size = Size(w, h),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                )
                drawOval(
                    color = Color.White.copy(alpha = 0.75f),
                    topLeft = Offset(c.x - w / 2, y - h * 0.30f),
                    size = Size(w, h * 0.60f),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }

        HeroKind.Battery -> {
            val bw = r * 1.05f
            val bh = r * 1.55f
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(KnouxViolet.copy(alpha = 0.9f), KnouxCyan.copy(alpha = 0.9f))),
                topLeft = Offset(c.x - bw / 2, c.y - bh / 2),
                size = Size(bw, bh),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
            )
            drawRoundRect(
                color = KnouxCyan.copy(alpha = 0.9f),
                topLeft = Offset(c.x - bw * 0.16f, c.y - bh / 2 - 10.dp.toPx()),
                size = Size(bw * 0.32f, 10.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
            )
            val bolt = Path().apply {
                moveTo(c.x + 6.dp.toPx(), c.y - bh * 0.34f)
                lineTo(c.x - bw * 0.20f, c.y + bh * 0.02f)
                lineTo(c.x - 2.dp.toPx(), c.y + bh * 0.02f)
                lineTo(c.x - 8.dp.toPx(), c.y + bh * 0.36f)
                lineTo(c.x + bw * 0.22f, c.y - bh * 0.02f)
                lineTo(c.x + 4.dp.toPx(), c.y - bh * 0.02f)
                close()
            }
            drawPath(bolt, Color.White.copy(alpha = 0.95f))
        }

        HeroKind.Chip -> {
            val s = r * 1.25f
            // pins
            for (i in 0..3) {
                val off = -s / 2 + s * (i + 0.5f) / 4f
                drawRoundRect(
                    color = KnouxViolet.copy(alpha = 0.9f),
                    topLeft = Offset(c.x + off - 3.dp.toPx(), c.y - s / 2 - 12.dp.toPx()),
                    size = Size(6.dp.toPx(), 14.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                )
                drawRoundRect(
                    color = KnouxViolet.copy(alpha = 0.9f),
                    topLeft = Offset(c.x + off - 3.dp.toPx(), c.y + s / 2 - 2.dp.toPx()),
                    size = Size(6.dp.toPx(), 14.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                )
                drawRoundRect(
                    color = KnouxMagenta.copy(alpha = 0.9f),
                    topLeft = Offset(c.x - s / 2 - 12.dp.toPx(), c.y + off - 3.dp.toPx()),
                    size = Size(14.dp.toPx(), 6.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                )
                drawRoundRect(
                    color = KnouxCyan.copy(alpha = 0.9f),
                    topLeft = Offset(c.x + s / 2 - 2.dp.toPx(), c.y + off - 3.dp.toPx()),
                    size = Size(14.dp.toPx(), 6.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                )
            }
            drawRoundRect(
                brush = Brush.linearGradient(listOf(KnouxMagenta.copy(alpha = 0.92f), KnouxViolet.copy(alpha = 0.92f))),
                topLeft = Offset(c.x - s / 2, c.y - s / 2),
                size = Size(s, s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx())
            )
            drawRoundRect(
                color = KnouxBackdrop.copy(alpha = 0.35f),
                topLeft = Offset(c.x - s * 0.34f, c.y - s * 0.34f),
                size = Size(s * 0.68f, s * 0.68f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx())
            )
        }
    }
}

/* ------------------------------------------------------------------ *
 * Bottom navigation
 * ------------------------------------------------------------------ */

enum class MainTab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Rounded.Home),
    Clean("Clean", Icons.Rounded.Build),
    Tools("Tools", Icons.Rounded.HealthAndSafety),
    Reports("Reports", Icons.Rounded.BarChart),
}

@Composable
fun KnouxBottomBar(
    selected: MainTab,
    onSelect: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    listOf(KnouxPanel.copy(alpha = 0.96f), KnouxPanelDeep.copy(alpha = 0.96f))
                )
            )
            .border(1.dp, KnouxBorder.copy(alpha = 0.45f), RoundedCornerShape(26.dp))
            .padding(horizontal = 10.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MainTab.entries.forEach { tab ->
            val active = tab == selected
            Row(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .then(
                        if (active)
                            Modifier.background(
                                Brush.horizontalGradient(listOf(KnouxViolet, KnouxMagenta))
                            )
                        else Modifier
                    )
                    .clickable { onSelect(tab) }
                    .padding(horizontal = if (active) 14.dp else 8.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    tab.icon, tab.label,
                    tint = if (active) Color.White else KnouxSoftText,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    tab.label,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = if (active) Color.White else KnouxSoftText
                    )
                )
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Screen titles
 * ------------------------------------------------------------------ */

@Composable
fun ScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
    accent: String? = null,
    accentColors: List<Color> = listOf(KnouxMagenta, KnouxViolet),
) {
    androidx.compose.foundation.text.BasicText(
        modifier = modifier,
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontSize = 27.sp,
            lineHeight = 32.sp,
            color = KnouxText
        ),
    ) {
        if (accent == null) {
            append(text)
        } else {
            append(text)
            withStyle(
                androidx.compose.ui.text.SpanStyle(
                    brush = Brush.horizontalGradient(accentColors)
                )
            ) {
                append(accent)
            }
        }
    }
}

@Composable
fun ScreenSubtitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier = modifier,
        style = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 12.5.sp,
            lineHeight = 17.sp,
            color = KnouxSoftText
        ),
        textAlign = TextAlign.Center,
    )
}

/* ------------------------------------------------------------------ *
 * Preview disclosure (fixtures must never look like real telemetry)
 * ------------------------------------------------------------------ */

@Composable
fun PreviewDisclosure(modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0x22FFC24B))
            .border(1.dp, KnouxAmber.copy(alpha = 0.6f), RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            Modifier.size(5.dp).clip(CircleShape).background(KnouxAmber)
        )
        Text(
            "DESIGN PREVIEW",
            style = TextStyle(
                fontWeight = FontWeight.Bold, fontSize = 8.sp, letterSpacing = 0.6.sp,
                color = KnouxAmber
            )
        )
    }
}

/** Convenience for the reference-capture build: hide the preview chip. */
@Composable
fun PreviewChipIfNeeded() {
    if (!com.knoux.ai.ui.theme.LocalReferenceCapture.current) {
        PreviewDisclosure()
    }
}
