package com.knoux.ai.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun HeaderHalo(modifier: Modifier, color: Color) {
    val pulse = if (rememberMotionEnabled()) {
        rememberInfiniteTransition(label = "header halo").animateFloat(
            initialValue = 0.12f,
            targetValue = 0.34f,
            animationSpec = infiniteRepeatable(tween(2300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "header halo opacity",
        )
    } else null
    val orbit = if (rememberMotionEnabled()) {
        rememberInfiniteTransition(label = "header orbit").animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)),
            label = "header orbit angle",
        )
    } else null

    Canvas(modifier) {
        val alpha = pulse?.value ?: 0.12f
        val radius = size.minDimension * .49f
        drawCircle(
            brush = Brush.radialGradient(listOf(color.copy(alpha = alpha), Color.Transparent), center, radius),
            radius = radius,
        )
        drawCircle(color.copy(alpha = alpha * .75f), radius * .91f, style = Stroke(.7.dp.toPx()))
        drawArc(
            color.copy(alpha = .55f),
            startAngle = orbit?.value ?: -45f,
            sweepAngle = 64f,
            useCenter = false,
            topLeft = center - Offset(radius * .91f, radius * .91f),
            size = androidx.compose.ui.geometry.Size(radius * 1.82f, radius * 1.82f),
            style = Stroke(1.dp.toPx()),
        )
        val angle = Math.toRadians((orbit?.value ?: -45f).toDouble())
        val point = center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * (radius * .91f)
        drawCircle(color.copy(alpha = .22f), 2.5.dp.toPx(), point)
        drawCircle(Color.White.copy(alpha = .80f), .9.dp.toPx(), point)
    }
}
