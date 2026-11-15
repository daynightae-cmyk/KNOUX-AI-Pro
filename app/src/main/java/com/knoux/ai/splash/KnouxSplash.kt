package com.knoux.ai.splash

import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.knoux.ai.R
import com.knoux.ai.ui.theme.ProDeepInk
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/* Storyboard (DESIGN_TOKENS ms): bloom 0-180, K 180-1120, wordmark 1030-1680,
 * smile 1600-2420, single click 2440, settle + exit 2420-3100. */
private const val TOTAL_MS = 3100f
private const val CLICK_AT_MS = 2440f

private fun seg(t: Float, start: Float, end: Float): Float =
    ((t - start) / (end - start)).coerceIn(0f, 1f)

@Composable
fun KnouxSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val calm = remember { reducedMotion(context) }
    val kBitmap = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.knoux_symbol_alpha)?.asImageBitmap()
    }
    val wordBitmap = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.knoux_wordmark_alpha)?.asImageBitmap()
    }

    val clock = remember { Animatable(0f) }
    val click = remember { SplashClickPlayer(context) }
    DisposableEffect(Unit) {
        click.preload()
        onDispose { click.release() }
    }

    LaunchedEffect(Unit) {
        if (calm) {
            clock.snapTo(1f)
            delay(700)
            onFinished()
            return@LaunchedEffect
        }
        launch {
            clock.animateTo(1f, tween(TOTAL_MS.toInt(), easing = LinearEasing))
            onFinished()
        }
        launch {
            snapshotFlow { clock.value }.first { it * TOTAL_MS >= CLICK_AT_MS }
            click.playOnce()
        }
    }

    val t = clock.value * TOTAL_MS
    val exit = seg(t, 2720f, 3100f)
    val stageAlpha = 1f - 0.9f * exit
    val stageScale = 1f - 0.035f * exit

    Box(modifier.fillMaxSize().background(ProDeepInk)) {
        Canvas(Modifier.fillMaxSize()) {
            val bloom = if (calm) 1f else seg(t, 0f, 260f)
            val center = Offset(size.width / 2f, size.height * 0.44f)
            val radius = size.minDimension * 0.62f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ProBlue.copy(alpha = 0.34f * bloom), Color.Transparent),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )
            // ambient spectral waves (brand atmosphere, not UI chrome)
            val w = size.width
            val h = size.height
            val topWave = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.52f, 0f)
                cubicTo(w * 0.70f, h * 0.04f, w * 0.86f, h * 0.15f, w, h * 0.21f)
            }
            drawPath(
                path = topWave,
                brush = Brush.linearGradient(
                    colors = listOf(ProArcticCyan.copy(alpha = 0.50f), Color.Transparent),
                    start = Offset(w * 0.60f, 0f),
                    end = Offset(w, h * 0.28f),
                ),
                style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round),
            )
            val bottomWave = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, h * 0.86f)
                cubicTo(w * 0.28f, h * 0.92f, w * 0.55f, h * 0.73f, w, h * 0.79f)
            }
            drawPath(
                path = bottomWave,
                brush = Brush.linearGradient(
                    colors = listOf(ProArcticCyan.copy(alpha = 0.45f), ProArcticCyan.copy(alpha = 0.45f)),
                    start = Offset(0f, h * 0.9f),
                    end = Offset(w, h * 0.75f),
                ),
                style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round),
            )
            drawPath(
                path = bottomWave,
                brush = Brush.linearGradient(
                    colors = listOf(ProArcticCyan.copy(alpha = 0.14f), ProArcticCyan.copy(alpha = 0.12f)),
                    start = Offset(0f, h * 0.9f),
                    end = Offset(w, h * 0.75f),
                ),
                style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round),
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = stageAlpha
                    scaleX = stageScale
                    scaleY = stageScale
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // --- authentic K raster, revealed stroke by stroke with a light tip ---
            Canvas(Modifier.size(width = 200.dp, height = 171.dp)) {
                val bmp = kBitmap ?: return@Canvas
                val dst = Size(size.width, size.height)
                if (calm || t >= 1120f) {
                    // Draw the approved raster exactly once after the reveal.
                    // Drawing the overlapping wipe bands a second time would
                    // accumulate their soft alpha glow and leave horizontal
                    // seams across the K.
                    drawImage(
                        image = bmp,
                        srcOffset = IntOffset.Zero,
                        srcSize = IntSize(bmp.width, bmp.height),
                        dstOffset = IntOffset.Zero,
                        dstSize = IntSize(dst.width.toInt(), dst.height.toInt()),
                    )
                } else {
                    val m1 = seg(t, 180f, 620f)
                    val m2 = seg(t, 520f, 980f)
                    val m3 = seg(t, 760f, 1120f)
                    drawWipeBand(bmp, dst, 0.00f, 0.48f, m1, ProArcticCyan, true)
                    drawWipeBand(bmp, dst, 0.28f, 0.78f, m2, ProBlue, true)
                    drawWipeBand(bmp, dst, 0.58f, 1.00f, m3, ProArcticCyan, false)
                }
            }

            Spacer(Modifier.height(10.dp))

            // --- lowercase wordmark write-on ---
            Canvas(Modifier.fillMaxWidth().height(52.dp)) {
                val bmp = wordBitmap ?: return@Canvas
                val h = size.height * 0.88f
                val w = h * (bmp.width.toFloat() / bmp.height.toFloat())
                val left = (size.width - w) / 2f
                val top = (size.height - h) / 2f
                val p = if (calm) 1f else seg(t, 1030f, 1680f)
                if (p <= 0f) return@Canvas
                clipRect(left = left, right = left + w * p) {
                    drawImage(
                        image = bmp,
                        srcOffset = IntOffset.Zero,
                        srcSize = IntSize(bmp.width, bmp.height),
                        dstOffset = IntOffset(left.toInt(), top.toInt()),
                        dstSize = IntSize(w.toInt(), h.toInt()),
                    )
                }
                if (p < 1f && !calm) {
                    drawLine(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.White, Color.Transparent),
                            startY = top,
                            endY = top + h,
                        ),
                        start = Offset(left + w * p, top),
                        end = Offset(left + w * p, top + h),
                        strokeWidth = 2.2.dp.toPx(),
                    )
                }
            }

            Spacer(Modifier.height(2.dp))

            // --- luminous smile, drawn left to right under the name ---
            Canvas(Modifier.fillMaxWidth().height(40.dp)) {
                val p = if (calm) 1f else seg(t, 1600f, 2420f)
                if (p <= 0f) return@Canvas
                val sw = size.width * 0.62f
                val left = (size.width - sw) / 2f
                val h = sw * 0.22f
                val cy = 4.dp.toPx()
                val top = cy - h / 2f
                val sweep = 180f * p
                val brush = Brush.horizontalGradient(listOf(ProArcticCyan, ProBlue, ProArcticCyan))
                drawArc(
                    brush = brush, alpha = 0.34f,
                    startAngle = 180f, sweepAngle = -sweep, useCenter = false,
                    topLeft = Offset(left, top), size = Size(sw, h),
                    style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round),
                )
                drawArc(
                    brush = brush,
                    startAngle = 180f, sweepAngle = -sweep, useCenter = false,
                    topLeft = Offset(left, top), size = Size(sw, h),
                    style = Stroke(width = 6.2.dp.toPx(), cap = StrokeCap.Round),
                )
                drawArc(
                    color = Color.White, alpha = 0.55f,
                    startAngle = 180f, sweepAngle = -sweep, useCenter = false,
                    topLeft = Offset(left, top), size = Size(sw, h),
                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round),
                )
                if (!calm && p < 1f) {
                    val ang = Math.toRadians((180f - sweep).toDouble())
                    val tip = Offset(
                        left + sw / 2f + (sw / 2f) * cos(ang).toFloat(),
                        top + h / 2f + (h / 2f) * sin(ang).toFloat(),
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color.White, ProArcticCyan.copy(alpha = 0.65f), Color.Transparent)
                        ),
                        radius = 17.dp.toPx(), center = tip,
                    )
                    drawCircle(Color.White, radius = 2.6.dp.toPx(), center = tip)
                }
                // one-time sparkle at the final tip (click frame)
                val s = seg(t, 2420f, 2780f)
                if (!calm && t >= 2420f) {
                    val fade = if (t < 2560f) 1f else 1f - seg(t, 2560f, 2780f)
                    val tip = Offset(left + sw, cy)
                    val ray = (8.dp + 24.dp * s).toPx()
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color.White.copy(alpha = 0.55f * fade), Color.Transparent)
                        ),
                        radius = 20.dp.toPx(), center = tip,
                    )
                    // Four-point star: long vertical + horizontal axes.
                    repeat(2) { i ->
                        val a = Math.toRadians((i * 90).toDouble())
                        val dx = cos(a).toFloat() * ray
                        val dy = sin(a).toFloat() * ray
                        drawLine(
                            color = Color.White.copy(alpha = 0.95f * fade),
                            start = tip - Offset(dx, dy),
                            end = tip + Offset(dx, dy),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    drawCircle(Color.White.copy(alpha = fade), radius = 3.6.dp.toPx(), center = tip)
                }
            }
        }
    }
}

private fun reducedMotion(context: Context): Boolean = try {
    Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f,
    ) == 0f
} catch (_: Exception) {
    false
}

/** One 90 ms synthesized click, loaded once, honoring mute / DND / app preference. */
private class SplashClickPlayer(private val context: Context) {
    private var pool: SoundPool? = null
    private var soundId = 0
    private var loaded = false

    fun preload() {
        if (pool != null) return
        val p = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
        p.setOnLoadCompleteListener { _, _, status -> loaded = status == 0 }
        soundId = p.load(context, R.raw.knoux_click, 1)
        pool = p
    }

    fun playOnce() {
        if (!soundEnabled()) return
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        if (audio.ringerMode != AudioManager.RINGER_MODE_NORMAL) return
        val notifications = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        if (notifications != null) {
            val filter = notifications.currentInterruptionFilter
            if (filter != NotificationManager.INTERRUPTION_FILTER_ALL &&
                filter != NotificationManager.INTERRUPTION_FILTER_UNKNOWN
            ) return
        }
        if (loaded) pool?.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        pool?.release()
        pool = null
    }

    private fun soundEnabled(): Boolean {
        val prefs = context.getSharedPreferences("knoux_prefs", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("sound_enabled", true)) return false
        return try {
            Settings.System.getInt(
                context.contentResolver,
                Settings.System.SOUND_EFFECTS_ENABLED,
                1,
            ) == 1
        } catch (_: Exception) {
            true
        }
    }
}

/** Reveals one horizontal band of the brand raster with a luminous leading edge. */
private fun DrawScope.drawWipeBand(
    bitmap: ImageBitmap,
    dst: Size,
    from: Float,
    to: Float,
    progress: Float,
    edge: Color,
    glow: Boolean,
) {
    if (progress <= 0f) return
    val top = dst.height * from
    val bottom = dst.height * to
    val right = dst.width * progress
    clipRect(left = 0f, right = right, top = top, bottom = bottom) {
        drawImage(
            image = bitmap,
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(bitmap.width, bitmap.height),
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(dst.width.toInt(), dst.height.toInt()),
        )
    }
    if (glow && progress < 1f) {
        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, edge.copy(alpha = 0.9f), Color.Transparent),
                startY = top,
                endY = bottom,
            ),
            start = Offset(right, top),
            end = Offset(right, bottom),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}
