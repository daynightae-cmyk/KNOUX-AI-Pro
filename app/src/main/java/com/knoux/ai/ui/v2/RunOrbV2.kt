package com.knoux.ai.ui.v2

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.knoux.ai.ui.components.rememberMotionEnabled
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
    val motion = rememberMotionEnabled()
    val transition = rememberInfiniteTransition(label = "knoux-orb")
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (running) 1700 else 11000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "orb-spin",
    )
    val counterSpin by transition.animateFloat(
        initialValue = 360f,
        targetValue = if (motion) 0f else 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (running) 2500 else 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "orb-counter-spin",
    )
    val breath by transition.animateFloat(
        initialValue = .96f,
        targetValue = if (motion) 1.045f else .96f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (running) 720 else 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orb-breath",
    )
    val spark by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (running) 1300 else 7600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "orb-spark",
    )
    val jewelYaw by transition.animateFloat(
        initialValue = if (motion) -14f else 0f,
        targetValue = if (motion) 14f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (running) 1700 else 4200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orb-jewel-yaw",
    )
    val jewelFloat by transition.animateFloat(
        initialValue = -1f,
        targetValue = if (motion) 1f else -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (running) 1300 else 2800, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orb-jewel-float",
    )
    val pulse = when {
        !motion -> 1f
        running -> breath
        else -> 1f + (breath - 1f) * .16f
    }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) .91f else 1f,
        animationSpec = tween(if (pressed) 80 else 260),
        label = "orb-press",
    )
    val pressGlow by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = tween(150),
        label = "orb-press-glow",
    )

    Box(
        modifier
            .size(size)
            .graphicsLayer {
                val s = pulse * pressScale
                scaleX = s
                scaleY = s
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
                onClick = {
                    if (motion) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onTap()
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val r = this.size.minDimension / 2f
            drawHalo(r, running, accent, accent2)
            drawOrbits(r, spin, counterSpin, running, accent, accent2)
            // The one service control: a dark glass heart, a single luminous
            // orbital family and the official K. No second radar/play button.
            drawCore(r * .70f, accent, accent2)
            drawSpark(r * .91f, spark, accent)
            if (pressGlow > 0f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color.White.copy(alpha = .20f * pressGlow), Color.Transparent),
                        center = center,
                        radius = r * .88f,
                    ),
                    radius = r * .88f,
                )
            }
            if (progress != null) drawProgress(r, progress, accent)
            if (running && progress == null) drawIndeterminate(r, spin, accent)
        }
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(com.knoux.ai.R.drawable.knoux_symbol_alpha),
            contentDescription = null,
            modifier = Modifier
                .size(size * .47f)
                .graphicsLayer {
                    rotationY = jewelYaw
                    rotationX = 4f * jewelFloat + 13f * pressGlow
                    translationY = jewelFloat * size.toPx() * .018f
                    cameraDistance = 12f * density
                    val depth = 1f + .025f * jewelFloat
                    scaleX = depth
                    scaleY = depth
                },
        )
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
fun KnouxRunOrbBlock(
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
    val day = LocalDayMode.current
    val motion = rememberMotionEnabled()
    // A tap sends one visible ripple out of the jewel — the service heard you.
    var rippleTick by remember { mutableIntStateOf(0) }
    val ripple = remember { androidx.compose.animation.core.Animatable(0f) }
    androidx.compose.runtime.LaunchedEffect(rippleTick) {
        if (rippleTick == 0) return@LaunchedEffect
        ripple.snapTo(0f)
        ripple.animateTo(1f, tween(620))
    }
    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (motion) {
                Canvas(Modifier.size(size * 1.32f)) {
                    if (ripple.value > 0f && ripple.value < 1f) {
                        val t = ripple.value
                        drawCircle(
                            color = accent.copy(alpha = (1f - t) * .45f),
                            radius = size.toPx() * (.42f + t * .34f),
                            style = Stroke(width = (2.4f * (1f - t) + .6f).dp.toPx()),
                        )
                    }
                }
            }
            KnouxRunOrb(
                progress = progress,
                running = running,
                enabled = enabled,
                accent = accent,
                accent2 = accent2,
                size = size,
                contentDescription = spoken,
                onTap = {
                    rippleTick++
                    onTap()
                },
            )
        }
        // The caption is a clear pill, not floating text: the everyday user
        // always sees what a tap will do.
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(
                    if (running) accent.copy(alpha = if (day) .14f else .16f)
                    else if (day) Color.White.copy(alpha = .78f)
                    else Color.White.copy(alpha = .06f)
                )
                .border(
                    1.dp,
                    if (running) accent.copy(alpha = .55f)
                    else if (day) dayAccent(accent).copy(alpha = .38f)
                    else accent.copy(alpha = .30f),
                    RoundedCornerShape(50),
                )
                .padding(horizontal = 14.dp, vertical = 5.dp),
        ) {
            KText(
                label,
                modifier = Modifier.clearAndSetSemantics { },
                size = 11.sp,
                color = if (running) accent else if (enabled) V2Soft else V2Muted,
                weight = if (running) FontWeight.Bold else FontWeight.SemiBold,
                align = TextAlign.Center,
                maxLines = 1,
            )
        }
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
