package com.knoux.ai.splash

import android.graphics.BitmapFactory
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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

/**
 * Branded splash sequence (approved design):
 * darkness -> K formation in three movements -> left-to-right wordmark ->
 * neon smile -> hand over to Home.
 * No spinner, no fake percentage, no typing cursor, no cartoon mouth.
 */
@Composable
fun KnouxSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val kBitmap = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.knoux_symbol_alpha)?.asImageBitmap()
    }
    val wordBitmap = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.knoux_wordmark_alpha)?.asImageBitmap()
    }

    val movement1 = remember { Animatable(0f) }
    val movement2 = remember { Animatable(0f) }
    val movement3 = remember { Animatable(0f) }
    val wordmark = remember { Animatable(0f) }
    val smile = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(220)
        movement1.animateTo(1f, tween(330, easing = LinearEasing))
        movement2.animateTo(1f, tween(330, easing = LinearEasing))
        movement3.animateTo(1f, tween(330, easing = LinearEasing))
        delay(140)
        wordmark.animateTo(1f, tween(520, easing = LinearEasing))
        delay(140)
        smile.animateTo(1f, tween(560, easing = LinearEasing))
        delay(380)
        onFinished()
    }

    Box(modifier.fillMaxSize().background(KnouxBackdrop)) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(KnouxViolet.copy(alpha = 0.30f), Color.Transparent),
                    center = Offset(size.width / 2f, size.height * 0.42f),
                    radius = size.minDimension * 0.6f
                ),
                radius = size.minDimension * 0.6f,
                center = Offset(size.width / 2f, size.height * 0.42f)
            )
        }

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Canvas(Modifier.size(190.dp)) {
                val bmp = kBitmap
                if (bmp != null) {
                    val dst = Size(size.width, size.height)
                    // three movements: upper blade, diagonal blade, lower blade
                    drawWipeBand(bmp, dst, 0.00f, 0.46f, movement1.value)
                    drawWipeBand(bmp, dst, 0.30f, 0.76f, movement2.value)
                    drawWipeBand(bmp, dst, 0.62f, 1.00f, movement3.value)
                }
            }

            Spacer(Modifier.height(14.dp))

            Canvas(Modifier.fillMaxWidth().height(46.dp)) {
                val bmp = wordBitmap
                if (bmp != null) {
                    val w = size.width * 0.62f
                    val h = w * (bmp.height.toFloat() / bmp.width.toFloat())
                    val left = (size.width - w) / 2f
                    val top = (size.height - h) / 2f
                    clipRect(left = left, right = left + w * wordmark.value) {
                        drawImage(
                            image = bmp,
                            srcOffset = IntOffsetZero,
                            srcSize = IntSize(bmp.width, bmp.height),
                            dstOffset = IntOffset(left.toInt(), top.toInt()),
                            dstSize = IntSize(w.toInt(), h.toInt())
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Canvas(Modifier.fillMaxWidth().height(34.dp)) {
                if (smile.value > 0f) {
                    val w = size.width * 0.34f
                    val left = (size.width - w) / 2f
                    val rect = Size(w, w * 0.9f)
                    val sweep = 150f * smile.value
                    // glow pass
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(KnouxViolet, KnouxCyan, KnouxMagenta, KnouxViolet),
                            center = Offset(left + w / 2f, rect.height)
                        ),
                        startAngle = 195f, sweepAngle = sweep, useCenter = false,
                        topLeft = Offset(left, -rect.height * 0.72f),
                        size = rect,
                        style = Stroke(width = 9f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    )
                    drawArc(
                        color = Color.White.copy(alpha = 0.85f),
                        startAngle = 195f, sweepAngle = sweep, useCenter = false,
                        topLeft = Offset(left, -rect.height * 0.72f),
                        size = rect,
                        style = Stroke(width = 3.2f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    )
                }
            }
        }
    }
}

private val IntOffsetZero = IntOffset(0, 0)

/** Draws one horizontal band of the brand bitmap, wiped in from the left. */
private fun DrawScope.drawWipeBand(
    bitmap: ImageBitmap,
    dst: Size,
    from: Float,
    to: Float,
    progress: Float,
) {
    if (progress <= 0f) return
    clipRect(
        left = 0f,
        right = dst.width * progress,
        top = dst.height * from,
        bottom = dst.height * to,
    ) {
        drawImage(
            image = bitmap,
            srcOffset = IntOffsetZero,
            srcSize = IntSize(bitmap.width, bitmap.height),
            dstOffset = IntOffsetZero,
            dstSize = IntSize(dst.width.toInt(), dst.height.toInt()),
        )
    }
}
