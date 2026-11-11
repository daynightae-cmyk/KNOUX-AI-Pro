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
import com.knoux.ai.ui.theme.KnouxBackdrop
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxViolet
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

    Box(modifier.fillMaxSize().background(KnouxBackdrop)) {
        Canvas(Modifier.fillMaxSize()) {
            val bloom = if (calm) 1f else seg(t, 0f, 260f)
            val center = Offset(size.width / 2f, size.height * 0.44f)
            val radius = size.minDimension * 0.62f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(KnouxViolet.copy(alpha = 0.34f * bloom), Color.Transparent),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
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
            Canvas(Modifier.size(196.dp)) {
                val bmp = kBitmap ?: return@Canvas
                val dst = Size(size.width, size.height)
                val m1 = if (calm) 1f else seg(t, 180f, 620f)
                val m2 = if (calm) 1f else seg(t, 520f, 980f)
                val m3 = if (calm) 1f else seg(t, 760f, 1120f)
                drawWipeBand(bmp, dst, 0.00f, 0.48f, m1, KnouxMagenta, true)
                drawWipeBand(bmp, dst, 0.28f, 0.78f, m2, KnouxViolet, true)
                drawWipeBand(bmp, dst, 0.58f, 1.00f, m3, KnouxCyan, false)
            }

            Spacer(Modifier.height(10.dp))

            // --- lowercase wordmark write-on ---
            Canvas(Modifier.fillMaxWidth().height(46.dp)) {
                val bmp = wordBitmap ?: return@Canvas
                val h = size.height * 0.86f
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
            Canvas(Modifier.fillMaxWidth().height(46.dp)) {
                val p = if (calm) 1f else seg(t, 1600f, 2420f)
                if (p <= 0f) return@Canvas
                val sw = size.width * 0.44f
                val left = (size.width - sw) / 2f
                val h = sw * 0.44f
                val cy = 5.dp.toPx()
                val top = cy - h / 2f
                val sweep = 180f * p
                val brush = Brush.horizontalGradient(listOf(KnouxMagenta, KnouxViolet, KnouxCyan))
                drawArc(
                    brush = brush, alpha = 0.30f,
                    startAngle = 180f, sweepAngle = -sweep, useCenter = false,
                    topLeft = Offset(left, top), size = Size(sw, h),
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round),
                )
                drawArc(
                    brush = brush,
                    startAngle = 180f, sweepAngle = -sweep, useCenter = false,
                    topLeft = Offset(left, top), size = Size(sw, h),
                    style = Stroke(width = 5.5.dp.toPx(), cap = StrokeCap.Round),
                )
                drawArc(
                    color = Color.White, alpha = 0.55f,
                    startAngle = 180f, sweepAngle = -sweep, useCenter = false,
                    topLeft = Offset(left, top), size = Size(sw, h),
                    style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round),
                )
                if (!calm && p < 1f) {
                    val ang = Math.toRadians((180f - sweep).toDouble())
                    val tip = Offset(
                        left + sw / 2f + (sw / 2f) * cos(ang).toFloat(),
                        top + h / 2f + (h / 2f) * sin(ang).toFloat(),
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color.White, KnouxCyan.copy(alpha = 0.65f), Color.Transparent)
                        ),
                        radius = 17.dp.toPx(), center = tip,
                    )
                    drawCircle(Color.White, radius = 2.6.dp.toPx(), center = tip)
                }
                // one-time sparkle at the final tip (click frame)
                val s = seg(t, 2420f, 2780f)
                if (!calm && t >= 2420f) {
                    val fade = 1f - s
                    val tip = Offset(left + sw, cy)
                    val ray = (6.dp + 20.dp * s).toPx()
                    repeat(4) { i ->
                        val a = Math.toRadians((i * 45).toDouble())
                        val dx = cos(a).toFloat() * ray
                        val dy = sin(a).toFloat() * ray
                        drawLine(
                            color = Color.White.copy(alpha = 0.85f * fade),
                            start = tip - Offset(dx, dy),
                            end = tip + Offset(dx, dy),
                            strokeWidth = 1.6.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    drawCircle(Color.White.copy(alpha = fade), radius = 3.2.dp.toPx(), center = tip)
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

/** One 70 ms synthesized click, loaded once, honoring mute / DND / app preference. */
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
        if (loaded) pool?.play(soundId, 0.9f, 0.9f, 1, 0, 1f)
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
