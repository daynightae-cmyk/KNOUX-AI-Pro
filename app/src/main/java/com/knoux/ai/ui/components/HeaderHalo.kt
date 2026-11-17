package com.knoux.ai.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

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

    Canvas(modifier) {
        val alpha = pulse?.value ?: 0.12f
        val radius = size.minDimension * .49f
        drawCircle(
            brush = Brush.radialGradient(listOf(color.copy(alpha = alpha), Color.Transparent), center, radius),
            radius = radius,
        )
        drawCircle(color.copy(alpha = alpha * .75f), radius * .91f, style = Stroke(.7.dp.toPx()))
    }
}
