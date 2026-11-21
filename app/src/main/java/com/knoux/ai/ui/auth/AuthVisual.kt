package com.knoux.ai.ui.auth

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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.theme.ProWarning
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProTextPrimary
import com.knoux.ai.ui.theme.ProBlue

/**
 * The visual states the identity gateway can present.
 *
 * Scanning palette follows the approved biometric reference: magenta ridges
 * on the upper half melting into cyan on the lower half, inside a violet→cyan
 * halo ring. Green is reserved exclusively for real verified success.
 */
private val ScanMagenta = Color(0xFFE879F9)
private val ScanViolet = Color(0xFFC084FC)
private val ScanCyan = Color(0xFF22D3EE)
private val VerifiedGreen = Color(0xFF3DFFB0)

enum class AuthVisualState {
    /** Ready — the sensor is available and waiting for the user. */
    Idle,

    /** The system prompt is open and the sensor is reading. */
    Scanning,

    /** The authenticator confirmed the user. */
    Success,

    /** No usable biometric hardware or the check was cancelled/failed. */
    Unavailable,
}

/**
 * The large circular fingerprint centerpiece of the KNOUX Identity Gateway.
 *
 * The ridges are drawn natively (vector arcs, not a bitmap) so they stay sharp
 * at every density, and the holographic rings orbit the core continuously.
 */
@Composable
fun FingerprintOrb(
    state: AuthVisualState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 232.dp,
    enabled: Boolean = true,
    accessibilityLabel: String = "Biometric unlock",
    showStateBadge: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val motion = com.knoux.ai.ui.components.rememberMotionEnabled()

    val transition = rememberInfiniteTransition(label = "auth-orb")
    val orbit by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (motion) 14000 else 1, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "orbit",
    )
    val pulse by transition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (motion) 3000 else 1, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    // The scanning beam only travels while the sensor is actually reading.
    val scan by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (motion && state == AuthVisualState.Scanning) 1400 else 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scan",
    )
    val halo by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (motion) 2200 else 1, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "halo",
    )

    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.955f else 1f,
        animationSpec = tween(140),
        label = "orb-press",
    )
    val coreColor = when (state) {
        AuthVisualState.Success -> VerifiedGreen
        AuthVisualState.Unavailable -> ProWarning
        AuthVisualState.Scanning -> ScanMagenta
        AuthVisualState.Idle -> ScanViolet
    }
    val ringColor = when (state) {
        AuthVisualState.Success -> VerifiedGreen
        AuthVisualState.Unavailable -> ProWarning
        else -> ScanCyan
    }

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = pressScale * (if (motion) pulse else 1f)
                scaleY = pressScale * (if (motion) pulse else 1f)
            }
            .semantics { contentDescription = accessibilityLabel }
            .clip(CircleShape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val radius = kotlin.math.min(this.size.width, this.size.height) / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // ambient bloom
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(coreColor.copy(alpha = 0.26f), Color.Transparent),
                    center = center,
                    radius = radius * 0.98f,
                ),
                center = center,
                radius = radius * 0.98f,
            )

            // two holographic orbit rings, counter-rotating — violet→cyan per
            // the approved scanner reference (green only after real success).
            val orbitA = if (state == AuthVisualState.Success) VerifiedGreen else ScanCyan
            val orbitB = if (state == AuthVisualState.Success) VerifiedGreen else ScanViolet
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        Color.Transparent,
                        orbitA.copy(alpha = 0.85f),
                        orbitB.copy(alpha = 0.75f),
                        Color.Transparent,
                    ),
                    center = center,
                ),
                startAngle = orbit,
                sweepAngle = 230f,
                useCenter = false,
                topLeft = Offset(center.x - radius * 0.95f, center.y - radius * 0.95f),
                size = Size(radius * 1.9f, radius * 1.9f),
                style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        Color.Transparent,
                        orbitB.copy(alpha = 0.70f),
                        orbitA.copy(alpha = 0.70f),
                        Color.Transparent,
                    ),
                    center = center,
                ),
                startAngle = -orbit * 0.72f,
                sweepAngle = 190f,
                useCenter = false,
                topLeft = Offset(center.x - radius * 0.86f, center.y - radius * 0.86f),
                size = Size(radius * 1.72f, radius * 1.72f),
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round),
            )

            // dotted inner ring
            val dots = 46
            for (index in 0 until dots) {
                val angle = (index * 360f / dots + orbit * 0.25f) * (Math.PI / 180.0)
                val dotRadius = radius * 0.72f
                val dot = Offset(
                    center.x + (dotRadius * kotlin.math.cos(angle)).toFloat(),
                    center.y + (dotRadius * kotlin.math.sin(angle)).toFloat(),
                )
                drawCircle(
                    color = ProArcticCyan.copy(alpha = 0.30f),
                    radius = 1.1.dp.toPx(),
                    center = dot,
                )
            }

            // the glass sphere that holds the ridges
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ProBlue.copy(alpha = 0.30f),
                        ProArcticCyan.copy(alpha = 0.12f),
                        Color.Transparent,
                    ),
                    center = Offset(center.x - radius * 0.18f, center.y - radius * 0.22f),
                    radius = radius * 0.86f,
                ),
                center = center,
                radius = radius * 0.72f,
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.14f),
                radius = radius * 0.72f,
                center = center,
                style = Stroke(width = 1.2.dp.toPx()),
            )

            // fingerprint ridges — magenta on the upper half melting into cyan
            // below, exactly like the reference scanner dome. Success keeps the
            // ridges violet so the green check reads as the verified overlay.
            val ridges = 9
            for (index in 0 until ridges) {
                val t = index / (ridges - 1f)
                val ridgeRadius = radius * (0.13f + 0.50f * t)
                val start = 128f + (index % 3) * 5f
                val arcSweep = 286f - (index % 4) * 16f
                val alpha = (0.95f - 0.42f * t).coerceIn(0.22f, 0.95f)
                val ridgeTop = when (state) {
                    AuthVisualState.Success -> ScanViolet
                    AuthVisualState.Unavailable -> ProWarning
                    else -> ScanMagenta
                }
                val ridgeBottom = when (state) {
                    AuthVisualState.Success -> ScanViolet.copy(alpha = 0.8f)
                    AuthVisualState.Unavailable -> ProWarning.copy(alpha = 0.8f)
                    else -> ScanCyan
                }
                drawArc(
                    brush = Brush.verticalGradient(
                        listOf(
                            ridgeTop.copy(alpha = alpha),
                            ridgeTop.copy(alpha = alpha * 0.92f),
                            ridgeBottom.copy(alpha = alpha * 0.88f),
                        ),
                        startY = center.y - ridgeRadius,
                        endY = center.y + ridgeRadius,
                    ),
                    startAngle = start,
                    sweepAngle = arcSweep,
                    useCenter = false,
                    topLeft = Offset(center.x - ridgeRadius, center.y - ridgeRadius),
                    size = Size(ridgeRadius * 2f, ridgeRadius * 2f),
                    style = Stroke(width = 3.1.dp.toPx(), cap = StrokeCap.Round),
                )
            }

            // the core whorl
            drawArc(
                brush = Brush.sweepGradient(
                    when (state) {
                        AuthVisualState.Success -> listOf(VerifiedGreen, VerifiedGreen, ScanViolet, VerifiedGreen)
                        else -> listOf(ScanCyan, ScanMagenta, ScanViolet, ScanCyan)
                    }
                ),
                startAngle = 40f,
                sweepAngle = 300f,
                useCenter = false,
                topLeft = Offset(center.x - radius * 0.10f, center.y - radius * 0.13f),
                size = Size(radius * 0.20f, radius * 0.26f),
                style = Stroke(width = 3.1.dp.toPx(), cap = StrokeCap.Round),
            )

            if (state == AuthVisualState.Scanning) {
                val beamY = center.y + scan * radius * 0.58f
                drawRect(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            ProArcticCyan.copy(alpha = 0.85f),
                            Color.Transparent,
                        )
                    ),
                    topLeft = Offset(center.x - radius * 0.62f, beamY - 1.6.dp.toPx()),
                    size = Size(radius * 1.24f, 3.2.dp.toPx()),
                )
            }

            // success shockwave
            if (state == AuthVisualState.Success) {
                drawCircle(
                    color = Color(0xFF3DFFB0).copy(alpha = 0.55f * halo),
                    radius = radius * (0.72f + 0.26f * halo),
                    center = center,
                    style = Stroke(width = 3.dp.toPx()),
                )

                // The verified overlay: a thick green check drawn over the
                // ridges, per the success reference. Only reachable after a
                // real BiometricPrompt confirmation + live session.
                val check = Path().apply {
                    moveTo(center.x - radius * 0.30f, center.y + radius * 0.02f)
                    lineTo(center.x - radius * 0.08f, center.y + radius * 0.24f)
                    lineTo(center.x + radius * 0.34f, center.y - radius * 0.22f)
                }
                drawPath(
                    check,
                    VerifiedGreen.copy(alpha = 0.35f),
                    style = Stroke(
                        width = 13.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round,
                    ),
                )
                drawPath(
                    check,
                    VerifiedGreen,
                    style = Stroke(
                        width = 7.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round,
                    ),
                )
            }
        }

        if (showStateBadge) {
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 26.dp, bottom = 26.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                when (state) {
                                    AuthVisualState.Success -> Color(0xFF3DFFB0).copy(alpha = 0.30f)
                                    AuthVisualState.Unavailable -> ProWarning.copy(alpha = 0.30f)
                                    else -> ProArcticCyan.copy(alpha = 0.26f)
                                },
                                Color.Black.copy(alpha = 0.18f),
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        when (state) {
                            AuthVisualState.Success -> Color(0xFF3DFFB0)
                            AuthVisualState.Unavailable -> ProWarning
                            else -> ProArcticCyan.copy(alpha = 0.85f)
                        },
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = when (state) {
                        AuthVisualState.Success -> Icons.Rounded.CheckCircle
                        AuthVisualState.Unavailable -> Icons.Rounded.WarningAmber
                        else -> Icons.Rounded.Fingerprint
                    },
                    contentDescription = null,
                    tint = when (state) {
                        AuthVisualState.Success -> Color(0xFF3DFFB0)
                        AuthVisualState.Unavailable -> ProWarning
                        else -> ProTextPrimary
                    },
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

/**
 * Full-width authentication method button.
 *
 * The label never truncates: the text column wraps and the row grows, so
 * "Continue with Google" always renders completely.
 */
@Composable
fun AuthMethodButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailing: String? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = tween(120),
        label = "auth-button-press",
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        accent.copy(alpha = if (enabled) 0.26f else 0.10f),
                        accent.copy(alpha = if (enabled) 0.08f else 0.04f),
                    )
                )
            )
            .border(
                1.dp,
                accent.copy(alpha = if (enabled) 0.70f else 0.24f),
                RoundedCornerShape(18.dp),
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(accent.copy(alpha = 0.30f), accent.copy(alpha = 0.10f))
                    )
                )
                .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (enabled) accent else ProTextSecondary,
                modifier = Modifier.size(21.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Box(
            Modifier
                .width(1.dp)
                .height(24.dp)
                .background(Color.White.copy(alpha = 0.18f)),
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                label,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 19.sp,
                    color = if (enabled) ProTextPrimary else ProTextSecondary,
                ),
            )
            if (trailing != null) {
                Text(
                    trailing,
                    style = TextStyle(fontSize = 9.sp, color = ProTextSecondary),
                )
            }
        }
        if (trailing == null) {
            Icon(
                Icons.Rounded.Key,
                contentDescription = null,
                tint = Color.Transparent,
                modifier = Modifier.size(0.dp),
            )
        }
    }
}
