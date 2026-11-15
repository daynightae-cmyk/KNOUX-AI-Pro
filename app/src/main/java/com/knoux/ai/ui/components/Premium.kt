package com.knoux.ai.ui.components

import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.theme.ProDeepInk
import com.knoux.ai.ui.theme.ProBlue
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProDeepInk
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProTextPrimary
import com.knoux.ai.ui.theme.ProBlue
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/* ------------------------------------------------------------------ *
 * A small, opinionated layer of premium surfaces.
 *
 * The references rely on three things rather than raw brightness:
 *  1. a deep, quiet glass body with a bright, directional edge;
 *  2. one controlled accent light per object; and
 *  3. a clear separation between the object and its glow.
 *
 * These helpers keep those rules consistent across screens.
 * ------------------------------------------------------------------ */

private val GlassTop = Color(0xFF241558)
private val GlassBottom = Color(0xFF0D0828)

@Composable
fun rememberMotionEnabled(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f,
            ) > 0.01f
        }.getOrDefault(true)
    }
}

@Composable
fun PremiumGlassCard(
    modifier: Modifier = Modifier,
    padding: Dp = 14.dp,
    radius: Dp = 20.dp,
    accent: Color = ProBlue,
    borderAlpha: Float = 0.46f,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    val border = Brush.linearGradient(
        listOf(
            accent.copy(alpha = borderAlpha),
            Color.White.copy(alpha = 0.14f),
            accent.copy(alpha = 0.18f),
        )
    )
    Box(
        modifier
            .clip(shape)
            .drawBehind {
                val glowCenter = Offset(size.width * 0.10f, size.height * 0.02f)
                val glowRadius = size.maxDimension * 0.95f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.20f), Color.Transparent),
                        center = glowCenter,
                        radius = glowRadius,
                    ),
                    center = glowCenter,
                    radius = glowRadius,
                )
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            accent.copy(alpha = 0.75f),
                            Color.Transparent,
                        )
                    ),
                    start = Offset(size.width * 0.08f, 1.dp.toPx()),
                    end = Offset(size.width * 0.92f, 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .background(
                Brush.verticalGradient(
                    listOf(GlassTop.copy(alpha = 0.97f), GlassBottom.copy(alpha = 0.98f))
                )
            )
            .border(BorderStroke(1.dp, border), shape)
            .padding(padding),
        content = content,
    )
}

@Composable
fun PremiumHeroImage(
    @DrawableRes image: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    glow: Color = ProBlue,
    animate: Boolean = true,
) {
    val motion = rememberMotionEnabled()
    val transition = rememberInfiniteTransition(label = "hero-float")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (animate && motion) 1.025f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "hero-scale",
    )
    val bitmap = ImageBitmap.imageResource(image)
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glow.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.46f),
                    radius = size.maxDimension * 0.62f,
                ),
                center = Offset(size.width * 0.5f, size.height * 0.46f),
                radius = size.maxDimension * 0.62f,
            )
        }
        val artModifier = if (contentDescription == null) {
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        } else {
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .semantics { this.contentDescription = contentDescription }
        }
        Canvas(artModifier) {
            val scaleFactor = min(
                size.width / bitmap.width.toFloat(),
                size.height / bitmap.height.toFloat(),
            )
            val dstWidth = (bitmap.width * scaleFactor).roundToInt()
            val dstHeight = (bitmap.height * scaleFactor).roundToInt()
            drawImage(
                image = bitmap,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(bitmap.width, bitmap.height),
                dstOffset = IntOffset(
                    ((size.width - dstWidth) / 2f).roundToInt(),
                    ((size.height - dstHeight) / 2f).roundToInt(),
                ),
                dstSize = IntSize(dstWidth, dstHeight),
                alpha = 1f,
                style = Fill,
                colorFilter = null,
                blendMode = BlendMode.Screen,
                filterQuality = FilterQuality.High,
            )
        }
    }
}

/**
 * Full-width reference-derived hero band for the inner tool screens.
 *
 * Only the decorative artwork is raster. Titles, metrics, rows and actions are
 * native Compose on top of / below it. The bitmap is composited with Screen so
 * its dark backdrop dissolves into the app canvas instead of showing a box.
 */
@Composable
fun ReferenceHeroBand(
    @DrawableRes image: Int,
    modifier: Modifier = Modifier,
    height: Dp = 116.dp,
    contentDescription: String? = null,
    glow: Color = ProBlue,
) {
    val bitmap = ImageBitmap.imageResource(image)
    Box(
        modifier
            .fillMaxWidth()
            .height(height),
    ) {
        Canvas(
            Modifier
                .fillMaxSize()
                .then(
                    if (contentDescription == null) Modifier
                    else Modifier.semantics { this.contentDescription = contentDescription }
                ),
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glow.copy(alpha = 0.16f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.52f),
                    radius = size.width * 0.44f,
                ),
                center = Offset(size.width * 0.5f, size.height * 0.52f),
                radius = size.width * 0.44f,
            )
            val dstWidth = size.width.roundToInt()
            val dstHeight = (size.width * bitmap.height / bitmap.width.toFloat()).roundToInt()
            drawImage(
                image = bitmap,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(bitmap.width, bitmap.height),
                dstOffset = IntOffset(
                    0,
                    ((size.height - dstHeight) / 2f).roundToInt(),
                ),
                dstSize = IntSize(dstWidth, dstHeight),
                alpha = 1f,
                style = Fill,
                colorFilter = null,
                blendMode = BlendMode.Screen,
                filterQuality = FilterQuality.High,
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.42f to Color.Transparent,
                        0.80f to ProDeepInk.copy(alpha = 0.10f),
                        1f to ProDeepInk.copy(alpha = 0.94f),
                    )
                )
        )
    }
}

@Composable
fun GlowIconTile(
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 25.dp,
) {
    Box(
        modifier
            .size(size)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.42f), Color.Transparent),
                        center = Offset(size.toPx() * 0.5f, size.toPx() * 0.54f),
                        radius = size.toPx() * 0.80f,
                    ),
                    center = Offset(size.toPx() * 0.5f, size.toPx() * 0.54f),
                    radius = size.toPx() * 0.80f,
                )
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.30f), Color.Transparent),
                        center = Offset(size.toPx() * 0.5f, size.toPx() * 0.92f),
                        radius = size.toPx() * 0.46f,
                    ),
                    topLeft = Offset(size.toPx() * 0.10f, size.toPx() * 0.76f),
                    size = Size(size.toPx() * 0.80f, size.toPx() * 0.30f),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(size * 0.30f))
                .background(
                    Brush.linearGradient(
                        listOf(
                            accent.copy(alpha = 0.36f),
                            Color(0xFF1C1245).copy(alpha = 0.94f),
                            Color(0xFF0B0826).copy(alpha = 0.94f),
                        )
                    )
                )
                .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(size * 0.30f)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .clip(RoundedCornerShape(size * 0.24f))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.10f), Color.Transparent)
                        )
                    )
            )
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}

@Composable
fun RasterIconTile(
    @DrawableRes image: Int,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    contentDescription: String? = null,
) {
    Box(
        modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
    }
}

/**
 * Draws a decorative raster glyph as light rather than as a rectangle.
 *
 * The reference icons were cut from a dark card background. Screen blending
 * discards that dark surround, so the object floats over the native glass
 * surface and its neon edge stays crisp.
 */
@Composable
fun RasterGlowIcon(
    @DrawableRes image: Int,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    contentDescription: String? = null,
    glow: Color = Color.Transparent,
) {
    val bitmap = ImageBitmap.imageResource(image)
    val dimension = size
    val semanticsModifier = if (contentDescription == null) {
        modifier
    } else {
        modifier.semantics { this.contentDescription = contentDescription }
    }
    Box(semanticsModifier.size(size), contentAlignment = Alignment.Center) {
        if (glow != Color.Transparent) {
            Canvas(Modifier.matchParentSize()) {
                val radius = dimension.toPx() * 0.50f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(glow.copy(alpha = 0.28f), Color.Transparent),
                        center = Offset(dimension.toPx() * 0.50f, dimension.toPx() * 0.54f),
                        radius = radius,
                    ),
                    center = Offset(dimension.toPx() * 0.50f, dimension.toPx() * 0.54f),
                    radius = radius,
                )
            }
        }
        Canvas(Modifier.fillMaxSize()) {
            val scale = min(
                this.size.width / bitmap.width.toFloat(),
                this.size.height / bitmap.height.toFloat(),
            )
            val dstWidth = (bitmap.width * scale).roundToInt()
            val dstHeight = (bitmap.height * scale).roundToInt()
            drawImage(
                image = bitmap,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(bitmap.width, bitmap.height),
                dstOffset = IntOffset(
                    ((this.size.width - dstWidth) / 2f).roundToInt(),
                    ((this.size.height - dstHeight) / 2f).roundToInt(),
                ),
                dstSize = IntSize(dstWidth, dstHeight),
                alpha = 1f,
                style = Fill,
                colorFilter = null,
                blendMode = BlendMode.Screen,
                filterQuality = FilterQuality.High,
            )
        }
    }
}

@Composable
fun GlowPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trailingChevron: Boolean = false,
    trailingDoubleChevron: Boolean = false,
    compact: Boolean = false,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.982f else 1f, label = "press-scale")
    val shape = RoundedCornerShape(50)
    Box(
        modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                val inset = 2.dp.toPx()
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(
                            ProArcticCyan.copy(alpha = 0.08f),
                            ProBlue.copy(alpha = 0.38f),
                            ProBlue.copy(alpha = 0.34f),
                            ProArcticCyan.copy(alpha = 0.10f),
                        )
                    ),
                    topLeft = Offset(-inset, -inset),
                    size = Size(size.width + inset * 2, size.height + inset * 2),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f),
                )
            },
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(
                    Brush.horizontalGradient(
                        listOf(ProArcticCyan, ProBlue, ProBlue, ProArcticCyan)
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.24f), shape)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClick = onClick,
                )
                .padding(
                    horizontal = if (compact) 10.dp else 20.dp,
                    vertical = if (compact) 10.dp else 11.dp,
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(if (compact) 14.dp else 18.dp))
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 11.5.sp else 16.5.sp,
                    color = Color.White,
                ),
                maxLines = 1,
            )
            if (trailingChevron) {
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Rounded.ChevronRight, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            if (trailingDoubleChevron) {
                Spacer(Modifier.width(8.dp))
                Box(Modifier.size(width = 26.dp, height = 18.dp)) {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null,
                        tint = Color.White.copy(alpha = 0.95f),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(18.dp)
                    )
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null,
                        tint = Color.White.copy(alpha = 0.72f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    compact: Boolean = false,
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.055f), Color.White.copy(alpha = 0.012f))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.22f), shape)
            .clickable(onClick = onClick)
            .padding(
                horizontal = if (compact) 14.dp else 18.dp,
                vertical = if (compact) 10.dp else 13.dp,
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = ProTextPrimary, modifier = Modifier.size(if (compact) 15.dp else 18.dp))
            Spacer(Modifier.width(7.dp))
        }
        Text(
            text,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 12.sp else 14.sp,
                color = ProTextPrimary,
            ),
            maxLines = 1,
        )
    }
}

@Composable
fun GlowRing(
    progress: Float,
    centerText: String,
    caption: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 88.dp,
    stroke: Dp = 9.dp,
    colors: List<Color> = listOf(ProArcticCyan, ProBlue, ProBlue, ProArcticCyan),
    centerTextSize: Int = 19,
    captionSize: Int = 8,
) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = stroke.toPx()
            val inset = sw / 2f
            val diameter = size.toPx() - sw
            val topLeft = Offset(inset, inset)
            val arcSize = Size(diameter, diameter)
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
            val sweep = 360f * progress.coerceIn(0f, 1f)
            drawArc(
                color = Color.White.copy(alpha = 0.09f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(sw, cap = StrokeCap.Round),
            )
            val brush = Brush.sweepGradient(colors, center)
            drawArc(
                brush = brush,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(sw * 2.5f, cap = StrokeCap.Round),
                alpha = 0.16f,
            )
            drawArc(
                brush = brush,
                startAngle = -90f,
                sweepAngle = (sweep - 2f).coerceAtLeast(0f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(sw, cap = StrokeCap.Round),
            )
            if (progress > 0.01f) {
                val angle = Math.toRadians((-90f + sweep).toDouble())
                val radius = diameter / 2f
                val dot = Offset(
                    center.x + (radius * cos(angle)).toFloat(),
                    center.y + (radius * sin(angle)).toFloat(),
                )
                drawCircle(Color.White.copy(alpha = 0.95f), radius = sw * 0.34f, center = dot)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                centerText,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = centerTextSize.sp,
                    color = ProTextPrimary,
                ),
                maxLines = 1,
            )
            if (caption != null) {
                Text(
                    caption,
                    style = TextStyle(
                        fontSize = captionSize.sp,
                        lineHeight = (captionSize + 2).sp,
                        color = ProTextSecondary,
                        textAlign = TextAlign.Center,
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
fun MetricPill(
    value: String,
    caption: String? = null,
    accent: Color = ProBlue,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(
                        listOf(accent.copy(alpha = 0.92f), accent.copy(alpha = 0.48f))
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(50))
                .padding(
                    horizontal = if (compact) 8.dp else 9.dp,
                    vertical = if (compact) 3.dp else 4.dp,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                value,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 9.5.sp else 10.5.sp,
                    color = Color.White,
                ),
                maxLines = 1,
            )
        }
        if (caption != null) {
            Spacer(Modifier.width(if (compact) 5.dp else 6.dp))
            Text(
                caption,
                style = TextStyle(
                    fontSize = if (compact) 7.5.sp else 8.5.sp,
                    color = ProTextSecondary,
                ),
                maxLines = 1,
            )
        }
    }
}

@Composable
fun SelectCheckbox(
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = ProBlue,
    size: Dp = 22.dp,
) {
    val shape = RoundedCornerShape(size * 0.32f)
    Box(
        modifier
            .size(size)
            .clip(shape)
            .then(
                if (checked) {
                    Modifier
                        .background(Brush.linearGradient(listOf(accent, ProArcticCyan)))
                        .border(1.dp, Color.White.copy(alpha = 0.30f), shape)
                } else {
                    Modifier
                        .background(Color.White.copy(alpha = 0.025f))
                        .border(1.dp, Color.White.copy(alpha = 0.30f), shape)
                }
            )
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                Icons.Rounded.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(15.dp),
            )
        }
    }
}

@Composable
fun PremiumSectionTitle(
    title: String,
    caption: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            title,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 15.5.sp,
                color = ProTextPrimary,
            ),
        )
        if (caption != null) {
            Text(
                caption,
                style = TextStyle(fontSize = 9.5.sp, color = ProTextSecondary),
            )
        }
    }
}

@Composable
fun TinyPreviewLabel(modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0x18FFC24B))
            .border(1.dp, Color(0x55FFC24B), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            Icons.Rounded.AutoAwesome,
            contentDescription = null,
            tint = com.knoux.ai.ui.theme.ProWarning,
            modifier = Modifier.size(9.dp),
        )
        Text(
            "PREVIEW",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 7.5.sp,
                letterSpacing = 0.7.sp,
                color = com.knoux.ai.ui.theme.ProWarning,
            ),
        )
    }
}

/** Small cyan/green confirmation chip used by the scan summary. */
@Composable
fun ScanCompleteBadge(
    text: String = "Scan Complete",
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 12.5.sp,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(Color(0xFF1DE9B6).copy(alpha = 0.16f))
                .border(1.dp, Color(0xFF1DE9B6).copy(alpha = 0.65f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Rounded.Check,
                null,
                tint = Color(0xFF7CFFD8),
                modifier = Modifier.size(10.dp),
            )
        }
        Spacer(Modifier.width(5.dp))
        Text(
            text,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                color = ProTextPrimary,
            ),
        )
    }
}

/** Kept for callers that want the app's dark surface colour inside a preview. */
val PremiumBackdrop: Color = ProDeepInk
val PremiumSoftText: Color = ProTextSecondary
val PremiumPanel: Color = ProDeepInk
