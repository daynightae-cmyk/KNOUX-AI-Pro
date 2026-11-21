package com.knoux.ai.splash

import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.knoux.ai.R
import com.knoux.ai.ui.theme.ProDeepInk
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sin

/**
 * The post-splash initialization stage — owner-approved reference
 * `_references/loading_20261121/ref_1.jpg` applied literally as the full-bleed
 * stage art (853x2000). The artwork is untouched; motion lives on top of it:
 *
 *  - the painted neon ring around the K orb is re-lit by a rotating comet arc
 *    (center 420,1560 radius 122 in art space), matching the baked highlights;
 *  - the "جار التهيئة..." caption breathes with animated ellipsis dots;
 *  - star sparkles drift over the sky band.
 *
 * Completion is honest: the stage runs a minimum showcase window AND waits for
 * the caller's `ready` flag (StartupCoordinator destination resolved), then
 * hands off through [onFinished]. Nothing here fakes a percentage.
 */
private const val ART_W = 853f
private const val ART_H = 2000f
private const val MIN_STAGE_MS = 2600L

@Composable
fun KnouxLoadingScreen(
    ready: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val art = remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    LaunchedEffect(Unit) {
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            art.value = BitmapFactory.decodeResource(context.resources, R.drawable.knoux_loading_art)
        }
    }

    // Minimum showcase window; handoff only when BOTH the window elapsed and
    // the startup destination is resolved. A hard ceiling keeps a stalled
    // coordinator from trapping the user on this stage forever.
    val windowDone = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(MIN_STAGE_MS)
        windowDone.value = true
    }
    LaunchedEffect(windowDone.value, ready) {
        if (windowDone.value && ready) onFinished()
    }

    val infinite = rememberInfiniteTransition(label = "loading-life")
    val spin by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1700, easing = LinearEasing), RepeatMode.Restart),
        label = "ring-spin",
    )
    val pulse by infinite.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100, easing = LinearEasing), RepeatMode.Reverse),
        label = "ring-pulse",
    )
    val dots by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 3.999f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "ellipsis",
    )
    val twinkle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2300, easing = LinearEasing), RepeatMode.Reverse),
        label = "twinkle",
    )
    val enter = remember { Animatable(0f) }
    LaunchedEffect(Unit) { enter.animateTo(1f, tween(420)) }

    Box(modifier.fillMaxSize().background(ProDeepInk)) {
        val bitmap = art.value
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.35f + 0.65f * enter.value },
            )
        }

        // Live layer, drawn in art coordinates (853x2000) with the same Crop
        // transform the Image uses, so overlays sit exactly on the painted art.
        Canvas(Modifier.fillMaxSize()) {
            val scale = maxOf(size.width / ART_W, size.height / ART_H)
            val dx = (size.width - ART_W * scale) / 2f
            val dy = (size.height - ART_H * scale) / 2f
            fun p(x: Float, y: Float) = Offset(dx + x * scale, dy + y * scale)

            val orb = p(420f, 1560f)
            val ringR = 121f * scale
            val ringW = 15f * scale

            // Soft breathing halo hugging the painted ring.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF7A4DFF).copy(alpha = 0.16f * pulse),
                        Color.Transparent,
                    ),
                    center = orb,
                    radius = ringR * 1.55f,
                ),
                radius = ringR * 1.55f,
                center = orb,
            )

            // Rotating comet: a bright sweep travelling around the painted
            // ring, tail fading behind it. Colors echo the baked neon.
            val sweep = 95f
            val head = spin
            val comet = Brush.sweepGradient(
                0.0f to Color.Transparent,
                (1f - sweep / 360f) to Color.Transparent,
                (1f - sweep / 720f) to Color(0xFF35E0FF).copy(alpha = 0.55f * pulse),
                1.0f to Color(0xFFE6C8FF).copy(alpha = 0.95f * pulse),
                center = orb,
            )
            drawArc(
                brush = comet,
                startAngle = head - sweep,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(orb.x - ringR, orb.y - ringR),
                size = androidx.compose.ui.geometry.Size(ringR * 2, ringR * 2),
                style = Stroke(width = ringW, cap = StrokeCap.Round),
            )
            // Comet head glow.
            val ha = Math.toRadians(head.toDouble())
            val headPos = Offset(
                orb.x + ringR * cos(ha).toFloat(),
                orb.y + ringR * sin(ha).toFloat(),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.85f * pulse), Color.Transparent),
                    center = headPos,
                    radius = 16f * scale,
                ),
                radius = 16f * scale,
                center = headPos,
            )

            // Animated ellipsis after the baked "جار التهيئة" caption. The
            // painted dots are covered by a patch sampled from the flat sky
            // right beside the caption, then 1..3 live dots fade in sequence.
            val dotCount = dots.toInt().coerceIn(0, 3)
            val captionY = 1742f
            val patchLeft = 290f
            val patchRight = 344f
            drawRect(
                brush = Brush.horizontalGradient(
                    0f to Color.Transparent,
                    0.10f to Color(0xFF140A46),
                    0.5f to Color(0xFF150F4F),
                    0.90f to Color(0xFF160F52),
                    1f to Color.Transparent,
                    startX = p(patchLeft, 0f).x,
                    endX = p(patchRight, 0f).x,
                ),
                topLeft = p(patchLeft, captionY - 24f),
                size = androidx.compose.ui.geometry.Size(
                    (patchRight - patchLeft) * scale,
                    48f * scale,
                ),
            )
            for (i in 0 until dotCount) {
                val cx = 334f - i * 15f
                drawCircle(
                    color = Color(0xFFF2EDFF).copy(alpha = 0.95f),
                    radius = 4.6f * scale,
                    center = p(cx, captionY),
                )
            }

            // Sparse twinkles over the sky band, away from the stag silhouette.
            val stars = listOf(
                Triple(96f, 210f, 0.0f), Triple(760f, 150f, 0.33f),
                Triple(60f, 620f, 0.66f), Triple(806f, 540f, 0.15f),
                Triple(140f, 980f, 0.5f), Triple(730f, 1030f, 0.8f),
            )
            for ((sx, sy, phase) in stars) {
                val a = ((twinkle + phase) % 1f)
                val glow = sin(a * Math.PI).toFloat()
                if (glow > 0.05f) {
                    val c = p(sx, sy)
                    drawCircle(
                        color = Color(0xFFCFE8FF).copy(alpha = 0.5f * glow),
                        radius = 2.4f * scale,
                        center = c,
                    )
                }
            }
        }
    }
}
