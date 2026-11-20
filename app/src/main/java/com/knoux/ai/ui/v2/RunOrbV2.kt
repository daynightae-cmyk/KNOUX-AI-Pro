package com.knoux.ai.ui.v2

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * KNOUX circular run control — the "K orb".
 *
 * A free-floating neon sphere with no card, panel or square container: the
 * artwork itself is the button. Three orbital rings spin around a glassy
 * violet→cyan core that carries the K mark, and a thin progress arc hugs the
 * rim while a service runs. The idle state breathes; the running state spins
 * faster and fills the arc with real progress.
 *
 * The composable is intentionally self-contained so every service screen can
 * place it without inheriting a background from the parent layout.
 */
@Composable
internal fun KnouxRunOrb(
    progress: Float? = null,
    running: Boolean = false,
    enabled: Boolean = true,
    accent: Color = V2Cyan,
    accent2: Color = V2Violet,
    size: Dp = 168.dp,
    contentDescription: String? = null,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "knoux-orb")
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (running) 1600 else 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "orb-spin",
    )
    val counterSpin by transition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (running) 2400 else 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "orb-counter-spin",
    )
    val breath by transition.animateFloat(
        initialValue = .92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (running) 620 else 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orb-breath",
    )
    val spark by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (running) 1100 else 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "orb-spark",
    )
    val pulse = if (running) breath else 1f
    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier
            .size(size)
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
                // A running orb must stay fully lit: the halo and progress arc
                // are the evidence that work is happening. Only a genuinely
                // unavailable orb fades back.
                alpha = if (enabled || running) 1f else .45f
            }
            .clip(CircleShape)
            .semantics {
                this.contentDescription = contentDescription
                    ?: if (running) "جارٍ تنفيذ الخدمة" else "تشغيل الخدمة"
                stateDescription = when {
                    !enabled -> "غير متاح الآن"
                    running -> "قيد التشغيل"
                    else -> "جاهز للتشغيل"
                }
            }
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClickLabel = if (running) "قيد التنفيذ" else "بدء الخدمة",
                onClick = onTap,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val r = this.size.minDimension / 2f
            drawHalo(r, running, accent, accent2)
            drawOrbits(r, spin, counterSpin, running, accent, accent2)
            drawCore(r * .62f, accent, accent2)
            drawKMark(r * .62f, accent, accent2)
            drawSpark(r * .92f, spark, accent)
            if (running && progress != null) drawProgress(r, progress, accent)
            if (running && progress == null) drawIndeterminate(r, spin, accent)
        }
    }
}

/* ------------------------------------------------------------------ */

/**
 * The universal start block: the free-floating K orb plus one status line.
 *
 * Every service screen renders this block so the run affordance is literally
 * the same control everywhere. The orb is not tied to any one service; it is
 * the single gateway that starts whatever service the user is looking at.
 */
@Composable
internal fun KnouxRunOrbBlock(
    progress: Float?,
    running: Boolean,
    enabled: Boolean,
    label: String,
    accent: Color = V2Cyan,
    accent2: Color = V2Violet,
    size: Dp = 148.dp,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // One spoken sentence per orb: what the control does and its live state.
    // The visible caption is cleared from the accessibility tree below so
    // TalkBack never announces the same status twice.
    val spoken = when {
        !enabled -> "$label. غير متاح الآن"
        running -> progress?.let {
            "$label. قيد التشغيل، ${(it.coerceIn(0f, 1f) * 100f).roundToInt()} بالمئة"
        } ?: "$label. قيد التشغيل"
        else -> "$label. جاهز للتشغيل"
    }
    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        KnouxRunOrb(
            progress = progress,
            running = running,
            enabled = enabled,
            accent = accent,
            accent2 = accent2,
            size = size,
            contentDescription = spoken,
            onTap = onTap,
        )
        KText(
            label,
            modifier = Modifier.clearAndSetSemantics { },
            size = 11.sp,
            color = if (running) accent else V2Soft,
            weight = if (running) FontWeight.Bold else FontWeight.Medium,
            align = TextAlign.Center,
        )
    }
}

private fun DrawScope.drawHalo(radius: Float, running: Boolean, accent: Color, accent2: Color) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                accent2.copy(alpha = if (running) .34f else .20f),
                accent.copy(alpha = .10f),
                Color.Transparent,
            ),
            center = center,
            radius = radius,
        ),
        radius = radius,
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(accent.copy(alpha = .18f), Color.Transparent),
            center = Offset(center.x, center.y + radius * .55f),
            radius = radius * .72f,
        ),
        radius = radius,
    )
}

private fun DrawScope.drawOrbits(
    radius: Float,
    spin: Float,
    counterSpin: Float,
    running: Boolean,
    accent: Color,
    accent2: Color,
) {
    val ringAlpha = if (running) .85f else .55f
    val stroke = Stroke(width = radius * .018f, cap = StrokeCap.Round)
    // Tilted outer orbit.
    rotate(spin, pivot = center) {
        drawArc(
            brush = Brush.sweepGradient(listOf(Color.Transparent, accent2.copy(alpha = ringAlpha), Color.Transparent)),
            startAngle = 20f,
            sweepAngle = 240f,
            useCenter = false,
            topLeft = Offset(center.x - radius * .94f, center.y - radius * .94f),
            size = androidx.compose.ui.geometry.Size(radius * 1.88f, radius * 1.88f),
            style = stroke,
        )
    }
    // Thin inner counter-orbit.
    rotate(counterSpin, pivot = center) {
        drawArc(
            brush = Brush.sweepGradient(listOf(Color.Transparent, accent.copy(alpha = ringAlpha * .9f), Color.Transparent)),
            startAngle = 160f,
            sweepAngle = 200f,
            useCenter = false,
            topLeft = Offset(center.x - radius * .78f, center.y - radius * .78f),
            size = androidx.compose.ui.geometry.Size(radius * 1.56f, radius * 1.56f),
            style = Stroke(width = radius * .012f, cap = StrokeCap.Round),
        )
    }
    // Dashed equator hint.
    rotate(spin * .5f, pivot = center) {
        drawArc(
            color = Color.White.copy(alpha = .10f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(center.x - radius * .70f, center.y - radius * .34f),
            size = androidx.compose.ui.geometry.Size(radius * 1.40f, radius * .68f),
            style = Stroke(width = radius * .008f),
        )
    }
}

private fun DrawScope.drawCore(radius: Float, accent: Color, accent2: Color) {
    // Drop shadow / lift.
    drawCircle(color = Color.Black.copy(alpha = .35f), radius = radius * 1.04f, center = Offset(center.x, center.y + radius * .08f))
    // Glass sphere.
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = .22f),
                accent2.copy(alpha = .95f),
                V2Deep2,
                V2Deep,
            ),
            center = Offset(center.x - radius * .30f, center.y - radius * .34f),
            radius = radius * 1.5f,
        ),
        radius = radius,
    )
    // Rim light.
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(accent.copy(alpha = .85f), Color.Transparent, accent2.copy(alpha = .55f)),
            start = Offset(center.x - radius, center.y - radius),
            end = Offset(center.x + radius, center.y + radius),
        ),
        radius = radius,
        style = Stroke(width = radius * .05f),
    )
    // Specular highlight.
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = .30f), Color.Transparent),
            center = Offset(center.x - radius * .34f, center.y - radius * .40f),
            radius = radius * .58f,
        ),
        radius = radius * .58f,
        center = Offset(center.x - radius * .34f, center.y - radius * .40f),
    )
}

/**
 * The K mark: two angled strokes and a leg, drawn as filled paths so it stays
 * crisp at every orb size instead of depending on a font glyph.
 */
private fun DrawScope.drawKMark(radius: Float, accent: Color, accent2: Color) {
    val w = radius * .30f          // stroke width
    val h = radius * .98f          // full height
    val cx = center.x
    val cy = center.y
    val top = cy - h / 2f
    val bottom = cy + h / 2f

    // The reference mark runs magenta at the top into violet and finishes cyan
    // at the lower leg, so the three-stop gradient reads as one lit solid.
    val brush = Brush.linearGradient(
        colors = listOf(V2Pink, accent2, accent),
        start = Offset(cx - w * 1.4f, top),
        end = Offset(cx + w * 1.9f, bottom),
    )

    // Vertical spine, slightly slanted like the reference mark.
    val spine = Path().apply {
        moveTo(cx - w * .55f, top)
        lineTo(cx + w * .45f, top)
        lineTo(cx + w * .05f, bottom)
        lineTo(cx - w * .95f, bottom)
        close()
    }
    // Upper arm.
    val upper = Path().apply {
        moveTo(cx + w * .10f, cy - h * .06f)
        lineTo(cx + w * 1.25f, top)
        lineTo(cx + w * 1.85f, top + h * .20f)
        lineTo(cx + w * .52f, cy + h * .02f)
        close()
    }
    // Lower leg.
    val lower = Path().apply {
        moveTo(cx + w * .16f, cy + h * .02f)
        lineTo(cx + w * 1.55f, bottom)
        lineTo(cx + w * .92f, bottom)
        lineTo(cx - w * .20f, cy + h * .30f)
        close()
    }
    drawPath(spine, brush)
    drawPath(upper, brush)
    drawPath(lower, brush)

    // Inner glow edge.
    drawPath(
        spine,
        Brush.linearGradient(listOf(Color.White.copy(alpha = .38f), Color.Transparent)),
        style = Stroke(width = radius * .012f),
    )
}

private fun DrawScope.drawSpark(radius: Float, angle: Float, accent: Color) {
    val a = Math.toRadians(angle.toDouble())
    val pos = Offset(
        center.x + (radius * kotlin.math.cos(a)).toFloat(),
        center.y + (radius * kotlin.math.sin(a)).toFloat(),
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = .9f), accent.copy(alpha = .4f), Color.Transparent),
            center = pos,
            radius = radius * .18f,
        ),
        radius = radius * .18f,
        center = pos,
    )
    drawCircle(color = Color.White, radius = radius * .022f, center = pos)
}

private fun DrawScope.drawProgress(radius: Float, progress: Float, accent: Color) {
    val sweep = progress.coerceIn(0f, 1f) * 360f
    drawArc(
        color = Color.White.copy(alpha = .10f),
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = Offset(center.x - radius * .985f, center.y - radius * .985f),
        size = androidx.compose.ui.geometry.Size(radius * 1.97f, radius * 1.97f),
        style = Stroke(width = radius * .035f, cap = StrokeCap.Round),
    )
    drawArc(
        brush = Brush.sweepGradient(listOf(accent, V2Pink, accent)),
        startAngle = -90f,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(center.x - radius * .985f, center.y - radius * .985f),
        size = androidx.compose.ui.geometry.Size(radius * 1.97f, radius * 1.97f),
        style = Stroke(width = radius * .035f, cap = StrokeCap.Round),
    )
}

private fun DrawScope.drawIndeterminate(radius: Float, spin: Float, accent: Color) {
    drawArc(
        brush = Brush.sweepGradient(listOf(Color.Transparent, accent, Color.Transparent)),
        startAngle = spin,
        sweepAngle = 110f,
        useCenter = false,
        topLeft = Offset(center.x - radius * .985f, center.y - radius * .985f),
        size = androidx.compose.ui.geometry.Size(radius * 1.97f, radius * 1.97f),
        style = Stroke(width = radius * .035f, cap = StrokeCap.Round),
    )
}
