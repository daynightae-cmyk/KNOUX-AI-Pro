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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.produceState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
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
import com.knoux.ai.ui.v2.V2Blue
import com.knoux.ai.ui.v2.V2Cyan
import com.knoux.ai.ui.v2.V2Pink
import com.knoux.ai.ui.v2.V2Violet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sin

/* Storyboard (DESIGN_TOKENS ms): bloom 0-180, K 180-1120, wordmark 1030-1680,
 * smile 1600-2420, single click 2440, settle + exit 2420-3100. */
private const val TOTAL_MS = 3100f
private const val CLICK_AT_MS = 2440f

private fun seg(t: Float, start: Float, end: Float): Float =
    ((t - start) / (end - start)).coerceIn(0f, 1f)

/** y of the lower aurora cubic at fraction fx of the width (same control pts). */
private fun waveY(fx: Float, h: Float): Float {
    // Cubic from (0,.86) c1(.28,.94) c2(.55,.74) to (1,.80) in x≈linear param.
    val u = fx
    val iu = 1f - u
    val y = iu * iu * iu * 0.86f + 3f * iu * iu * u * 0.94f + 3f * iu * u * u * 0.74f + u * u * u * 0.80f
    return h * y
}

/** Neon saturation lift: official rasters stay the SAME artwork, only the
 *  color energy is raised to the approved splash gloss (pink→cyan V2 family). */
private val NeonGloss = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(1.55f) })

/** Preloads the splash click sound off the main thread. Created once, kept for splash lifetime. */
@Composable
private fun splashSoundPlayer(context: Context): SplashClickPlayer {
    val scope = rememberCoroutineScope()
    return remember(context) {
        SplashClickPlayer(context).also { player ->
            scope.launch {
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                    player.preload()
                }
            }
        }
    }
}

@Composable
fun KnouxSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val calm = remember { reducedMotion(context) }

    // Bitmaps decoded on Dispatchers.IO, not during composition.
    // Keep the android.graphics.Bitmap to read width/height without main-thread cost,
    // convert to Compose ImageBitmap only at draw time.
    val kBitmapState = remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val wordBitmapState = remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val sealBitmapState = remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    LaunchedEffect(R.drawable.knoux_symbol_alpha) {
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.knoux_symbol_alpha)
            kBitmapState.value = bitmap
        }
    }
    LaunchedEffect(R.drawable.knoux_wordmark_alpha) {
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.knoux_wordmark_alpha)
            wordBitmapState.value = bitmap
        }
    }
    LaunchedEffect(R.drawable.knoux_seal_official) {
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.knoux_seal_official)
            sealBitmapState.value = bitmap
        }
    }

    // Sound preloaded once on Dispatchers.IO; plays once at the click frame.
    val click = splashSoundPlayer(context)

    val clock = remember { Animatable(0f) }
    DisposableEffect(Unit) {
        onDispose { click.release() }
    }

    // Leave only when: (a) the animation timeline is complete, AND
    // (b) the entry destination has been resolved by StartupCoordinator.
    // The caller gates onFinished on destination != null, so we never
    // transition before both the artwork and the session decision are ready.
    LaunchedEffect(Unit) {
        if (calm) {
            clock.snapTo(1f)
            delay(700)
            onFinished()
            return@LaunchedEffect
        }
        clock.animateTo(1f, tween(TOTAL_MS.toInt(), easing = LinearEasing))
        launch {
            snapshotFlow { clock.value }.first { it * TOTAL_MS >= CLICK_AT_MS }
            click.playOnce()
        }
        onFinished()
    }

    val t = clock.value * TOTAL_MS
    val exit = seg(t, 2720f, 3100f)
    val stageAlpha = 1f - 0.9f * exit
    val stageScale = 1f - 0.035f * exit

    Box(modifier.fillMaxSize().background(Color(0xFF070312))) {
        Canvas(Modifier.fillMaxSize()) {
            val bloom = if (calm) 1f else seg(t, 0f, 260f)
            val w = size.width
            val h = size.height
            // Approved splash: deep night ink with violet/magenta bloom upper
            // LEFT and electric cyan bloom upper RIGHT — the same neon family
            // as Home and the hubs (V2 tokens), replacing the arctic palette.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(V2Violet.copy(alpha = 0.30f * bloom), Color.Transparent),
                    center = Offset(w * 0.22f, h * 0.16f), radius = w * 0.85f,
                ),
                radius = w * 0.85f, center = Offset(w * 0.22f, h * 0.16f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(V2Pink.copy(alpha = 0.16f * bloom), Color.Transparent),
                    center = Offset(w * 0.05f, h * 0.30f), radius = w * 0.60f,
                ),
                radius = w * 0.60f, center = Offset(w * 0.05f, h * 0.30f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(V2Cyan.copy(alpha = 0.14f * bloom), Color.Transparent),
                    center = Offset(w * 0.95f, h * 0.12f), radius = w * 0.70f,
                ),
                radius = w * 0.70f, center = Offset(w * 0.95f, h * 0.12f),
            )
            // Lower aurora: magenta rising left → cyan sweeping right, with
            // the dotted particle field of the approved art along its crest.
            val bottomWave = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, h * 0.86f)
                cubicTo(w * 0.28f, h * 0.94f, w * 0.55f, h * 0.74f, w, h * 0.80f)
            }
            val aurora = Brush.horizontalGradient(
                colors = listOf(V2Pink.copy(alpha = 0.75f), V2Violet.copy(alpha = 0.65f), V2Cyan.copy(alpha = 0.85f)),
                startX = 0f, endX = w,
            )
            drawPath(bottomWave, brush = aurora, style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round), alpha = 0.16f)
            drawPath(bottomWave, brush = aurora, style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round))
            // Deterministic dot field hugging the wave (no per-frame random).
            for (i in 0 until 70) {
                val fx = (i * 0.618034f) % 1f
                val wy = waveY(fx, h)
                val dy = ((i * 37) % 23 - 11) * 0.9.dp.toPx()
                val c = if (fx < 0.45f) V2Pink else if (fx < 0.7f) V2Violet else V2Cyan
                drawCircle(c.copy(alpha = 0.10f + (i % 5) * 0.05f), radius = (0.7f + (i % 3) * 0.5f).dp.toPx(), center = Offset(w * fx, wy + dy - 6.dp.toPx()))
            }
            // Upper streaks: violet descending from the left, cyan from the right.
            val topLeft = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, h * 0.10f)
                cubicTo(w * 0.14f, h * 0.05f, w * 0.30f, h * 0.10f, w * 0.44f, h * 0.035f)
            }
            drawPath(
                topLeft,
                brush = Brush.horizontalGradient(listOf(V2Pink.copy(alpha = 0.65f), V2Violet.copy(alpha = 0.45f), Color.Transparent), startX = 0f, endX = w * 0.5f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
            )
            val topRight = androidx.compose.ui.graphics.Path().apply {
                moveTo(w, h * 0.07f)
                cubicTo(w * 0.86f, h * 0.12f, w * 0.74f, h * 0.10f, w * 0.62f, h * 0.17f)
            }
            drawPath(
                topRight,
                brush = Brush.horizontalGradient(listOf(V2Cyan.copy(alpha = 0.85f), V2Blue.copy(alpha = 0.35f), Color.Transparent), startX = w, endX = w * 0.55f),
                style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round),
            )
            // Orbit rings around the K zone (the approved swirl), faint.
            val kc = Offset(w * 0.5f, h * 0.40f)
            drawOval(
                brush = Brush.sweepGradient(listOf(Color.Transparent, V2Violet.copy(alpha = 0.35f), Color.Transparent, V2Cyan.copy(alpha = 0.30f), Color.Transparent), center = kc),
                topLeft = Offset(kc.x - w * 0.44f, kc.y - h * 0.045f),
                size = Size(w * 0.88f, h * 0.09f),
                style = Stroke(width = 1.2.dp.toPx()),
                alpha = bloom,
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
            Canvas(Modifier.size(width = 236.dp, height = 202.dp)) {
                val androidBmp = kBitmapState.value ?: return@Canvas
                val bmp = androidBmp.asImageBitmap()
                val dst = Size(size.width, size.height)
                if (calm || t >= 1120f) {
                    // Draw the approved raster exactly once after the reveal.
                    // Drawing the overlapping wipe bands a second time would
                    // accumulate their soft alpha glow and leave horizontal
                    // seams across the K.
                    drawImage(
                        image = bmp,
                        srcOffset = IntOffset.Zero,
                        srcSize = IntSize(androidBmp.width, androidBmp.height),
                        dstOffset = IntOffset.Zero,
                        dstSize = IntSize(dst.width.toInt(), dst.height.toInt()),
                        colorFilter = NeonGloss,
                    )
                } else {
                    val m1 = seg(t, 180f, 620f)
                    val m2 = seg(t, 520f, 980f)
                    val m3 = seg(t, 760f, 1120f)
                    drawWipeBand(bmp, dst, 0.00f, 0.48f, m1, V2Pink, true)
                    drawWipeBand(bmp, dst, 0.28f, 0.78f, m2, V2Violet, true)
                    drawWipeBand(bmp, dst, 0.58f, 1.00f, m3, V2Cyan, false)
                }
            }

            Spacer(Modifier.height(10.dp))

            // --- lowercase wordmark write-on ---
            Canvas(Modifier.fillMaxWidth().height(52.dp)) {
                val androidBmp = wordBitmapState.value ?: return@Canvas
                val bmp = androidBmp.asImageBitmap()
                val h = size.height * 0.88f
                val w = h * (androidBmp.width.toFloat() / androidBmp.height.toFloat())
                val left = (size.width - w) / 2f
                val top = (size.height - h) / 2f
                val p = if (calm) 1f else seg(t, 1030f, 1680f)
                if (p <= 0f) return@Canvas
                clipRect(left = left, right = left + w * p) {
                    drawImage(
                        image = bmp,
                        srcOffset = IntOffset.Zero,
                        srcSize = IntSize(androidBmp.width, androidBmp.height),
                        dstOffset = IntOffset(left.toInt(), top.toInt()),
                        dstSize = IntSize(w.toInt(), h.toInt()),
                        colorFilter = NeonGloss,
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
                val brush = Brush.horizontalGradient(listOf(V2Pink, V2Violet, V2Cyan))
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
                            listOf(Color.White, V2Cyan.copy(alpha = 0.65f), Color.Transparent)
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

        // Official company seal — kept, blooming small at the TOP edge so the
        // center stage matches the approved artwork (K + wordmark + smile).
        Canvas(
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 34.dp)
                .size(56.dp)
                .graphicsLayer { alpha = stageAlpha },
        ) {
            val seal = sealBitmapState.value ?: return@Canvas
            val p = if (calm) 1f else seg(t, 60f, 760f)
            if (p <= 0f) return@Canvas
            val bmp = seal.asImageBitmap()
            val r = size.minDimension / 2f
            drawArc(
                color = V2Cyan.copy(alpha = .55f * p),
                startAngle = -90f,
                sweepAngle = 360f * p,
                useCenter = false,
                topLeft = Offset(center.x - r * .98f, center.y - r * .98f),
                size = Size(r * 1.96f, r * 1.96f),
                style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round),
            )
            val disc = r * .84f * (0.90f + 0.10f * p)
            val off = center - Offset(disc, disc)
            drawImage(
                image = bmp,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(seal.width, seal.height),
                dstOffset = IntOffset(off.x.toInt(), off.y.toInt()),
                dstSize = IntSize((disc * 2f).toInt(), (disc * 2f).toInt()),
                alpha = p,
            )
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
            colorFilter = NeonGloss,
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
