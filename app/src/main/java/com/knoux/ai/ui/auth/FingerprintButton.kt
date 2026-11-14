package com.knoux.ai.ui.auth

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxViolet

/**
 * Circular biometric unlock control.
 *
 * The ridges are drawn natively (no bitmap) so they stay sharp on every
 * density, and the whole ring breathes while the sensor is idle.
 */
@Composable
fun FingerprintButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 132.dp,
    enabled: Boolean = true,
    success: Boolean = false,
    contentDescription: String = "Unlock with fingerprint",
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val transition = rememberInfiniteTransition(label = "fingerprint")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ring-sweep",
    )
    val scan by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scan-line",
    )
    val breathe by transition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathe",
    )

    var flash by remember { mutableStateOf(false) }
    LaunchedEffect(success) { if (success) flash = true }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.955f else 1f,
        animationSpec = tween(120),
        label = "press",
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .semantics { this.contentDescription = contentDescription }
            .clip(androidx.compose.foundation.shape.CircleShape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val radius = this.size.minDimension / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val accent = if (success) KnouxCyan else KnouxMagenta
            val secondary = if (success) Color(0xFF7CFFD8) else KnouxCyan

            // ambient bloom
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accent.copy(alpha = 0.30f), Color.Transparent),
                    center = center,
                    radius = radius * 0.98f,
                ),
                center = center,
                radius = radius * 0.98f,
            )

            // rotating outer ring
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        Color.Transparent,
                        accent.copy(alpha = 0.85f),
                        secondary.copy(alpha = 0.85f),
                        Color.Transparent,
                    ),
                    center = center,
                ),
                startAngle = sweep,
                sweepAngle = 260f,
                useCenter = false,
                topLeft = Offset(center.x - radius * 0.93f, center.y - radius * 0.93f),
                size = Size(radius * 1.86f, radius * 1.86f),
                style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round),
            )

            // static guide ring
            drawCircle(
                color = Color.White.copy(alpha = 0.10f),
                radius = radius * 0.86f,
                center = center,
                style = Stroke(width = 1.dp.toPx()),
            )

            // fingerprint ridges
            val ridges = 8
            for (index in 0 until ridges) {
                val t = index / (ridges - 1f)
                val ridgeRadius = radius * (0.20f + 0.52f * t)
                val start = 118f + (index % 3) * 6f
                val arcSweep = 300f - (index % 4) * 18f
                val alpha = (0.85f - 0.45f * t).coerceIn(0.18f, 0.9f)
                drawArc(
                    brush = Brush.linearGradient(
                        listOf(
                            accent.copy(alpha = alpha),
                            secondary.copy(alpha = alpha * 0.85f),
                        ),
                        start = Offset(center.x - ridgeRadius, center.y - ridgeRadius),
                        end = Offset(center.x + ridgeRadius, center.y + ridgeRadius),
                    ),
                    startAngle = start,
                    sweepAngle = arcSweep,
                    useCenter = false,
                    topLeft = Offset(center.x - ridgeRadius, center.y - ridgeRadius),
                    size = Size(ridgeRadius * 2f, ridgeRadius * 2f),
                    style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round),
                )
            }

            // core
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(secondary.copy(alpha = 0.55f), Color.Transparent),
                    center = center,
                    radius = radius * 0.22f,
                ),
                center = center,
                radius = radius * 0.22f,
            )

            // scanning highlight
            val scanY = center.y + scan * radius * 0.62f
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        secondary.copy(alpha = 0.55f),
                        Color.Transparent,
                    )
                ),
                topLeft = Offset(center.x - radius * 0.6f, scanY - 1.2.dp.toPx()),
                size = Size(radius * 1.2f, 2.4.dp.toPx()),
            )

            if (flash) {
                drawCircle(
                    color = KnouxCyan.copy(alpha = 0.55f),
                    radius = radius * 0.92f,
                    center = center,
                    style = Stroke(width = 3.dp.toPx()),
                )
            }
        }
    }
}
