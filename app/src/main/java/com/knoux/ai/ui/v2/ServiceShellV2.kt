package com.knoux.ai.ui.v2

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.components.rememberMotionEnabled
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/* ------------------------------------------------------------------ *
 * KNOUX AI Pro — service hero
 *
 * Owner direction (17 Nov): the old small glass header and the tiny stag
 * are gone. Every service now opens on a full-bleed night stage: the
 * official neon stag stands on the right, the service instrument floats
 * on the left, both on a glowing floor under a nebula + aurora sky.
 *
 * The cover is fixed above the results. It is the only element carrying
 * the service identity, so the stag, title and instrument stay in place
 * while the operator reviews a long list.
 * ------------------------------------------------------------------ */

/**
 * Height of the scrollable service hero. The whole service page now scrolls
 * like the Home screen, so the hero can be genuinely large instead of being
 * squeezed into the leftover viewport. On a 360dp phone it is 374dp; on a
 * 412dp phone it is ~427dp. The results and tools flow underneath it.
 */
internal fun serviceHeroHeight(maxWidth: Dp): Dp =
    (maxWidth * 1.04f).coerceIn(340.dp, 430.dp)

/**
 * How much of a service hero the reader keeps on screen once the page scrolls.
 *
 * Board 00 asks for one permanent header per service: the top bar stays, the
 * hero never scrolls entirely away, only its inside moves. So the hero folds
 * from its full height down to a compact bar — the stag, the wordmark and the
 * sonar stay visible at a readable scale — and the fold is driven by the list
 * scroll offset, not by a timer.
 *
 * [full] is the hero height at rest, [compactHeight] the folded height, and
 * [remaining] the part of the hero still visible above the content. The
 * progress below is the 0..1 fold factor reversed: 0 fully open, 1 fully
 * folded. Never NaN, never negative.
 */
internal fun serviceHeroFold(full: Dp, compactHeight: Dp, scrollOffsetPx: Float, density: Float): Float {
    if (!scrollOffsetPx.isFinite() || density <= 0f) return 0f
    val travelPx = (full - compactHeight).value * density
    if (travelPx <= 0f) return 1f
    return (scrollOffsetPx / travelPx).coerceIn(0f, 1f)
}

/** Hero height after folding: full height at rest, compact bar once folded. */
internal fun foldedHeroHeight(full: Dp, compactHeight: Dp, fold: Float): Dp =
    (full.value + (compactHeight.value - full.value) * fold.coerceIn(0f, 1f)).dp

/**
 * The folded header keeps the service identity legible: at rest the hero shows
 * the full stage; while the reader scrolls, the stag and the instrument scale
 * with the fold but never disappear.
 */
internal fun heroStageScale(fold: Float): Float =
    (1f - .72f * fold.coerceIn(0f, 1f)).coerceIn(.28f, 1f)

/**
 * Height of the stage lane (stag + instrument) inside the header. Open, it takes
 * the room left after the title block; folded, it keeps a fixed lane so the stag
 * and the radar still have their own space instead of being squeezed into a
 * 12dp strip behind the title. [narrow] covers the compact phones.
 */
internal fun serviceStageHeight(compact: Boolean, available: Dp): Dp {
    val floor = if (compact) 96.dp else 112.dp
    return available.coerceAtLeast(0.dp).coerceAtMost(420.dp).coerceAtLeast(floor)
}


/** Title size that keeps every service name on one line without shrinking the hero. */
internal fun serviceTitleSize(titleLength: Int): Float = when {
    titleLength <= 14 -> 28f
    titleLength <= 21 -> 25f
    titleLength <= 27 -> 22.5f
    else -> 20.5f
}

/**
 * The shared service page. It follows the Home screen: one scrollable
 * LazyColumn under the app top bar, opening with a full-bleed hero and then
 * the measured results, cards and tool grid. Only the primary action stays
 * pinned so a running scan can always be cancelled.
 */
@Composable
fun FixedServiceScreen(
    spec: KnouxServiceSpec,
    status: String,
    statusColor: Color,
    metric: String?,
    scene: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
    listState: LazyListState = rememberLazyListState(),
    content: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val compact = maxHeight < 560.dp || maxWidth < 340.dp
        val heroHeight = serviceHeroHeight(maxWidth)
        val settledHeight = (heroHeight * .55f).coerceAtLeast(170.dp)
        val density = LocalDensity.current
        val motion = rememberMotionEnabled()

        // Board 00 — "header ثابت أثناء التمرير". The header is not an item of
        // the list: the list starts underneath it and the header folds from its
        // full height down to a compact bar as the reader scrolls. Whatever the
        // scroll position, the stag, the wordmark and the instrument stay on
        // screen, and nothing above the list moves the content.
        val scrollOffsetPx = listState.firstVisibleItemScrollOffset.toFloat()
        val foldTarget = serviceHeroFold(heroHeight, settledHeight, scrollOffsetPx, density.density)
        val fold by animateFloatAsState(
            targetValue = foldTarget,
            animationSpec = if (motion) tween(220) else snap(),
            label = "serviceHeaderFold",
        )
        val visibleHeader = foldedHeroHeight(heroHeight, settledHeight, fold)
        // The stage rises inside the header while it folds; the title, the
        // wordmark and the live status line stay where they are, so the header
        // never turns into an empty panel. The travel stops well before the top
        // edge (-34dp) so the stag and the radar are never clipped away.
        val stageTravel = ((heroHeight - settledHeight) * .55f - 34.dp).coerceAtLeast(0.dp)
        val stageScroll = (stageTravel * fold).coerceIn(0.dp, heroHeight - visibleHeader)

        Column(
            Modifier
                .fillMaxSize()
                .testTag("service_header_fold_${"%.2f".format(fold)}"),
        ) {
            ServiceHero(
                spec = spec,
                status = status,
                statusColor = statusColor,
                metric = metric,
                compact = compact,
                fold = fold,
                stageScroll = stageScroll,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(visibleHeader)
                    .testTag("service_fixed_cover"),
                scene = scene,
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("service_results_list"),
                state = listState,
                contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        content = content,
                    )
                }
            }
            if (action != null) {
                Spacer(Modifier.height(6.dp))
                Box(Modifier.padding(horizontal = 16.dp)) { action() }
            }
        }
    }
}

/**
 * The full service hero used both as the fixed header (Deep Scan) and as the
 * scrolling banner on the other service pages.
 */
@Composable
fun ServiceHero(
    spec: KnouxServiceSpec,
    status: String,
    statusColor: Color,
    metric: String?,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    fold: Float = 0f,
    stageScroll: Dp = 0.dp,
    scene: @Composable (Modifier) -> Unit,
) {
    val motion = rememberMotionEnabled()
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    val revealAlpha by animateFloatAsState(
        targetValue = if (entered || !motion) 1f else 0f,
        animationSpec = if (motion) tween(320) else snap(),
        label = "serviceHeroAlpha",
    )
    val revealRise by animateFloatAsState(
        targetValue = if (entered || !motion) 0f else 14f,
        animationSpec = if (motion) tween(320) else snap(),
        label = "serviceHeroRise",
    )
    val titleSize = (serviceTitleSize(spec.title.length) - if (compact) 2.5f else 0f).sp
    val folded = fold.coerceIn(0f, 1f)
    val wordmarkAlpha by animateFloatAsState(
        targetValue = 1f - folded,
        animationSpec = if (motion) tween(220) else snap(),
        label = "serviceHeaderWordmark",
    )

    Box(
        modifier
            .clip(
                RoundedCornerShape(
                    topStart = 26.dp,
                    topEnd = 26.dp,
                    bottomStart = 30.dp,
                    bottomEnd = 30.dp,
                )
            )
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF120633),
                        Color(0xFF1C0A46),
                        Color(0xFF0A0424),
                    )
                )
            )
            .drawBehind { HeroSky(spec) },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = revealAlpha
                    translationY = revealRise.dp.toPx()
                }
                .padding(horizontal = if (compact) 12.dp else 16.dp),
        ) {
            Spacer(Modifier.height(if (compact) 9.dp else 12.dp))
            // Wordmark row — the first thing board 00 shows inside the header.
            // It fades out while the header folds, so the stag and the service
            // instrument keep the room that the title used to take.
            Row(
                Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = wordmarkAlpha }
                    .testTag("service_cover_wordmark"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(width = 16.dp, height = 1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, spec.accent2.copy(alpha = .85f))
                            )
                        ),
                )
                Spacer(Modifier.width(7.dp))
                KText(
                    "${spec.order} · ${spec.english}",
                    size = if (compact) 6.8.sp else 7.6.sp,
                    weight = FontWeight.Bold,
                    color = spec.accent2,
                    letterSpacing = 2.2.sp,
                    maxLines = 1,
                )
                Spacer(Modifier.width(7.dp))
                Box(
                    Modifier
                        .size(width = 16.dp, height = 1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(spec.accent2.copy(alpha = .85f), Color.Transparent)
                            )
                        ),
                )
            }
            Spacer(Modifier.height(if (compact) 2.dp else 3.dp))
            KText(
                spec.title,
                size = titleSize,
                weight = FontWeight.Black,
                color = Color.White,
                lineHeight = (titleSize.value * 1.16f).sp,
                align = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { heading() }
                    .testTag("service_cover_title"),
            )
            // The folded bar stays light: the title remains, the tagline and the
            // sub-line give their space back to the stage.
            if (folded < .98f) {
                Spacer(Modifier.height(2.dp))
                KText(
                    spec.tagline,
                    size = if (compact) 10.sp else 11.5.sp,
                    lineHeight = if (compact) 13.sp else 15.sp,
                    color = V2Soft,
                    align = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { alpha = 1f - folded }
                        .padding(horizontal = 6.dp),
                )
            }
            Spacer(Modifier.height(if (compact) 2.dp else 4.dp))
            BoxWithConstraints(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                // Folded bar: the stage keeps a small but readable lane of its
                // own between the title and the live status line.
                val stageHeight = serviceStageHeight(compact, maxHeight)
                val stageScale = heroStageScale(folded)
                HeroStage(
                    spec = spec,
                    compact = compact,
                    motion = motion,
                    stageScale = stageScale,
                    scene = scene,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(stageHeight)
                        .graphicsLayer { translationY = -stageScroll.toPx() }
                        .clipToBounds(),
                )
            }
            HeroStatusStrip(
                status = status,
                statusColor = statusColor,
                metric = metric,
                compact = compact,
                collapsed = folded > .6f,
            )
        }
    }
}

/**
 * Cosmic night sky — deep space nebula with electric aurora ribbons,
 * crystalline star field, and glowing mountain silhouettes.
 * This is the full-bleed backdrop for the service hero: the stag stands
 * on the right peak, the instrument floats on the left, both under a
 * living sky that pulses with the service's accent colors.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.HeroSky(spec: KnouxServiceSpec) {
    val w = size.width
    val h = size.height
    val accent = spec.accent
    val accent2 = spec.accent2
    val gold = Color(0xFFFFD700)

    // 1) Deep space base — layered radial nebulae
    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF050210),
                Color(0xFF0D0424),
                Color(0xFF1A0638),
                Color(0xFF0F0320),
            ),
            startY = 0f,
            endY = h,
        ),
        size = Size(w, h),
    )

    // 2) Primary nebula cores — large soft glows in service colors
    drawCircle(
        brush = Brush.radialGradient(
            listOf(
                accent.copy(alpha = .28f),
                accent.copy(alpha = .18f),
                accent.copy(alpha = .06f),
                Color.Transparent,
            ),
            center = Offset(w * .15f, h * .35f),
            radius = w * .75f,
        ),
        radius = w * .75f,
        center = Offset(w * .15f, h * .35f),
    )

    drawCircle(
        brush = Brush.radialGradient(
            listOf(
                accent2.copy(alpha = .24f),
                accent2.copy(alpha = .14f),
                accent2.copy(alpha = .04f),
                Color.Transparent,
            ),
            center = Offset(w * .85f, h * .25f),
            radius = w * .65f,
        ),
        radius = w * .65f,
        center = Offset(w * .85f, h * .25f),
    )

    // 3) Gold accent nebula (PRO identity) — top center crown glow
    drawCircle(
        brush = Brush.radialGradient(
            listOf(
                gold.copy(alpha = .18f),
                gold.copy(alpha = .08f),
                Color.Transparent,
            ),
            center = Offset(w * .5f, h * .15f),
            radius = w * .55f,
        ),
        radius = w * .55f,
        center = Offset(w * .5f, h * .15f),
    )

    // 4) Electric aurora ribbons — sweeping energy trails
    val aurora1 = Path().apply {
        moveTo(-w * .2f, h * .65f)
        cubicTo(w * .15f, h * .25f, w * .45f, h * .85f, w * .7f, h * .55f)
        cubicTo(w * .9f, h * .35f, w * 1.1f, h * .7f, w * 1.3f, h * .45f)
    }
    drawPath(
        aurora1,
        brush = Brush.linearGradient(
            listOf(
                Color.Transparent,
                accent.copy(alpha = .35f),
                accent2.copy(alpha = .45f),
                gold.copy(alpha = .25f),
                accent2.copy(alpha = .35f),
                Color.Transparent,
            ),
            start = Offset(0f, h * .65f),
            end = Offset(w, h * .45f),
        ),
        style = Stroke(width = w * .18f, cap = StrokeCap.Round),
    )
    drawPath(
        aurora1,
        brush = Brush.linearGradient(
            listOf(
                Color.Transparent,
                Color.White.copy(alpha = .6f),
                accent.copy(alpha = .8f),
                Color.White.copy(alpha = .4f),
                Color.Transparent,
            ),
            start = Offset(0f, h * .65f),
            end = Offset(w, h * .45f),
        ),
        style = Stroke(width = w * .025f, cap = StrokeCap.Round),
    )

    val aurora2 = Path().apply {
        moveTo(-w * .1f, h * .85f)
        cubicTo(w * .2f, h * .55f, w * .55f, h * .95f, w * .8f, h * .65f)
        cubicTo(w * 1.0f, h * .45f, w * 1.2f, h * .8f, w * 1.4f, h * .55f)
    }
    drawPath(
        aurora2,
        brush = Brush.linearGradient(
            listOf(
                Color.Transparent,
                accent2.copy(alpha = .28f),
                accent.copy(alpha = .35f),
                Color.Transparent,
            ),
            start = Offset(0f, h * .85f),
            end = Offset(w, h * .55f),
        ),
        style = Stroke(width = w * .14f, cap = StrokeCap.Round),
    )

    // 5) Crystalline star field — deterministic, multi-layer depth
    data class StarLayer(val count: Int, val baseAlpha: Float, val sizeMul: Float, val yMul: Float)
    val starLayers = listOf(
        StarLayer(40, 0.9f, 1.4f, 0.75f),  // Bright foreground stars
        StarLayer(60, 0.55f, 1.0f, 0.85f), // Mid stars
        StarLayer(80, 0.3f, 0.7f, 0.95f)   // Distant stars
    )
    for ((idx, layer) in starLayers.withIndex()) {
        val count = layer.count
        val baseAlpha = layer.baseAlpha
        val sizeMul = layer.sizeMul
        val yMul = layer.yMul
        for (i in 0 until count) {
            val x = ((i * 97 + 13 + idx * 31) % 101) / 101f
            val y = ((i * 61 + 19 + idx * 17) % 97) / 97f
            val r = (.4f + ((i * 37 + idx * 11) % 8) / 8f * 1.2f) * sizeMul
            val twinkle = ((i * 13 + idx * 7) % 5) / 5f * 0.4f
            drawCircle(
                color = Color.White.copy(alpha = (baseAlpha + twinkle).coerceAtMost(1f)),
                radius = r.dp.toPx(),
                center = Offset(x * w, y * h * yMul),
            )
            // Star cross glints on brightest layer
            if (idx == 0 && i % 7 == 0) {
                val glintLen = (r * 2.5f).dp.toPx()
                drawLine(
                    color = Color.White.copy(alpha = .7f),
                    start = Offset(x * w - glintLen, y * h * yMul),
                    end = Offset(x * w + glintLen, y * h * yMul),
                    strokeWidth = 0.5.dp.toPx(),
                )
                drawLine(
                    color = Color.White.copy(alpha = .7f),
                    start = Offset(x * w, y * h * yMul - glintLen),
                    end = Offset(x * w, y * h * yMul + glintLen),
                    strokeWidth = 0.5.dp.toPx(),
                )
            }
        }
    }

    // 6) Glowing mountain ridge — silhouetted peaks with neon rim light
    val ridge = Path().apply {
        moveTo(0f, h)
        lineTo(0f, h * .78f)
        lineTo(w * .08f, h * .62f)
        lineTo(w * .18f, h * .75f)
        lineTo(w * .32f, h * .55f)
        lineTo(w * .44f, h * .72f)
        lineTo(w * .58f, h * .58f)
        lineTo(w * .7f, h * .76f)
        lineTo(w * .85f, h * .52f)
        lineTo(w * .95f, h * .7f)
        lineTo(w, h * .65f)
        lineTo(w, h)
        close()
    }
    // Mountain base fill
    drawPath(
        ridge,
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF1A0A3A).copy(alpha = .95f),
                Color(0xFF0A0318).copy(alpha = .98f),
            ),
            startY = h * .5f,
            endY = h,
        ),
    )
    // Neon rim light on ridge peaks
    val rimPath = Path().apply {
        moveTo(0f, h * .78f)
        lineTo(w * .08f, h * .62f)
        lineTo(w * .18f, h * .75f)
        lineTo(w * .32f, h * .55f)
        lineTo(w * .44f, h * .72f)
        lineTo(w * .58f, h * .58f)
        lineTo(w * .7f, h * .76f)
        lineTo(w * .85f, h * .52f)
        lineTo(w * .95f, h * .7f)
        lineTo(w, h * .65f)
    }
    drawPath(
        rimPath,
        brush = Brush.linearGradient(
            listOf(
                accent.copy(alpha = .6f),
                accent2.copy(alpha = .7f),
                gold.copy(alpha = .5f),
                accent2.copy(alpha = .6f),
                accent.copy(alpha = .5f),
            ),
        ),
        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
    )
    // Inner glow line
    drawPath(
        rimPath,
        color = Color.White.copy(alpha = .35f),
        style = Stroke(width = 0.8.dp.toPx(), cap = StrokeCap.Round),
    )

    // 7) Energy scan lines — subtle horizontal beams across the sky
    for (i in 0 until 4) {
        val y = h * (0.18f + i * 0.18f)
        val alpha = 0.04f + (i % 2) * 0.03f
        drawRect(
            brush = Brush.horizontalGradient(
                listOf(
                    Color.Transparent,
                    accent.copy(alpha = alpha),
                    accent2.copy(alpha = alpha * 1.5f),
                    Color.Transparent,
                ),
            ),
            topLeft = Offset(0f, y),
            size = Size(w, 1.5.dp.toPx()),
        )
    }

    // 8) Top edge highlight — joins hero to status bar
    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = .12f),
                accent.copy(alpha = .06f),
                Color.Transparent,
            ),
            startY = 0f,
            endY = h * .08f,
        ),
        size = Size(w, h * .08f),
    )

    // 9) Bottom floor glow preview — hints at the stage floor
    val floorY = h * .88f
    drawOval(
        brush = Brush.radialGradient(
            listOf(
                accent.copy(alpha = .22f),
                accent2.copy(alpha = .12f),
                Color.Transparent,
            ),
            center = Offset(w * .5f, floorY),
            radius = w * .55f,
        ),
        topLeft = Offset(w * .05f, floorY - h * .08f),
        size = Size(w * .9f, h * .18f),
    )
}

@Composable
private fun HeroStage(
    spec: KnouxServiceSpec,
    compact: Boolean,
    motion: Boolean,
    scene: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    stageScale: Float = 1f,
) {
    val transition = rememberInfiniteTransition(label = "heroStage")
    val float by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) -6f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "heroStagFloat",
    )
    // Pulse glow for the stag's energy field
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "heroStagPulse",
    )
    // Orbiting energy ring around stag
    val orbit by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
        ),
        label = "heroStagOrbit",
    )

    BoxWithConstraints(modifier) {
        val stageHeight = maxHeight
        // Stag is now MASSIVE - takes up ~70% of stage height, prominent on right
        val stagHeight = (stageHeight * .75f).coerceAtMost(320.dp).coerceAtLeast(200.dp)
        // Instrument on left - substantial but not overwhelming
        val instrument = (stageHeight * .55f)
            .coerceAtMost(maxWidth * .42f)
            .coerceAtMost(200.dp)
            .coerceAtLeast(120.dp)
        val scale = stageScale.coerceIn(.25f, 1f)
        val accent = spec.accent
        val accent2 = spec.accent2
        val gold = Color(0xFFFFD700)
        // Width/height for layout calculations (available in BoxWithConstraints scope)
        val w = maxWidth.value.toFloat()
        val h = maxHeight.value.toFloat()
        val floorY = h * .92f
        val stagX = w * .78f
        val stagY = h * .42f
        val instrumentX = w * .18f

        Canvas(Modifier.fillMaxSize()) {

            // ===== FLOOR: Deep glowing platform with concentric neon rings =====
            // Base floor glow - large radial
            drawOval(
                brush = Brush.radialGradient(
                    listOf(
                        accent.copy(alpha = .45f),
                        accent2.copy(alpha = .25f),
                        gold.copy(alpha = .12f),
                        Color.Transparent,
                    ),
                    center = Offset(w * .5f, floorY),
                    radius = w * .65f,
                ),
                topLeft = Offset(w * .02f, floorY - h * .12f),
                size = Size(w * .96f, h * .22f),
            )

            // Concentric neon rings on floor - animated pulse
            for (i in 0 until 5) {
                val ringPulse = (pulse * 0.3f + i * 0.2f) % 1f
                val ringAlpha = (0.5f - ringPulse * 0.35f).coerceIn(0.08f, 0.5f)
                val ringInset = w * (.04f + i * .045f)
                val ringColor = when (i % 3) {
                    0 -> accent
                    1 -> accent2
                    else -> gold
                }
                drawOval(
                    color = ringColor.copy(alpha = ringAlpha),
                    topLeft = Offset(ringInset, floorY - h * (.015f + i * .006f)),
                    size = Size(w - ringInset * 2f, h * (.045f + i * .01f)),
                    style = Stroke(width = (1.8f + pulse * 1.2f).dp.toPx(), cap = StrokeCap.Round),
                )
            }

            // Inner bright core ring
            drawOval(
                color = Color.White.copy(alpha = .25f + pulse * .15f),
                topLeft = Offset(w * .12f, floorY - h * .025f),
                size = Size(w * .76f, h * .06f),
                style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round),
            )

            // ===== LEFT SIDE: Energy beam rising under instrument =====
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        accent.copy(alpha = .18f + pulse * .1f),
                        accent2.copy(alpha = .12f + pulse * .08f),
                        Color.Transparent,
                    ),
                ),
                topLeft = Offset(instrumentX - w * .08f, h * .08f),
                size = Size(w * .16f, h * .82f),
            )

            // Particle sparkles rising in beam
            if (motion) {
                for (i in 0 until 12) {
                    val sparkY = h * (.15f + (i * 7.3f % 100) / 100f * .7f + (pulse * 0.3f) % 1f * .1f)
                    val sparkX = instrumentX + (w * .06f * cos((i * 31 + pulse * 100) * PI / 180)).toFloat()
                    val sparkAlpha = (.3f + (i * 17 % 10) / 10f * .5f) * (1f - (sparkY - h * .15f) / (h * .7f))
                    drawCircle(
                        color = Color.White.copy(alpha = sparkAlpha),
                        radius = (1.2f + (i % 3) * 0.8f).dp.toPx(),
                        center = Offset(sparkX, sparkY),
                    )
                }
            }

            // ===== RIGHT SIDE: Stag's energy halo & orbiting ring =====

            // Large pulsing halo behind stag
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        accent2.copy(alpha = .35f + pulse * .15f),
                        accent.copy(alpha = .22f + pulse * .1f),
                        gold.copy(alpha = .1f + pulse * .05f),
                        Color.Transparent,
                    ),
                    center = Offset(stagX, stagY),
                    radius = w * .42f,
                ),
                radius = w * .42f,
                center = Offset(stagX, stagY),
            )

            // Secondary tighter halo
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        Color.White.copy(alpha = .18f + pulse * .1f),
                        accent2.copy(alpha = .12f),
                        Color.Transparent,
                    ),
                    center = Offset(stagX, stagY),
                    radius = w * .28f,
                ),
                radius = w * .28f,
                center = Offset(stagX, stagY),
            )

            // Orbiting energy ring around stag position
            val orbitRadius = w * .22f
            val orbitAngle = orbit * PI / 180f
            val orbitX = stagX + (orbitRadius * cos(orbitAngle)).toFloat()
            val orbitY = stagY + (orbitRadius * sin(orbitAngle)).toFloat()
            drawCircle(
                color = Color.White.copy(alpha = .6f),
                radius = 3.dp.toPx(),
                center = Offset(orbitX, orbitY),
            )
            // Orbit trail
            drawCircle(
                color = accent.copy(alpha = .25f),
                radius = orbitRadius,
                center = Offset(stagX, stagY),
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round),
            )

            // Energy crosshair at stag center
            val crossLen = (w * .15f).dp.toPx()
            drawLine(
                color = accent.copy(alpha = .4f),
                start = Offset(stagX - crossLen, stagY),
                end = Offset(stagX + crossLen, stagY),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawLine(
                color = accent2.copy(alpha = .4f),
                start = Offset(stagX, stagY - crossLen),
                end = Offset(stagX, stagY + crossLen),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round,
            )

            // ===== CONTACT SHADOWS =====
            // Stag shadow
            drawOval(
                color = Color.Black.copy(alpha = .5f),
                topLeft = Offset(stagX - w * .18f, floorY - h * .03f),
                size = Size(w * .36f, h * .06f),
            )
            // Instrument shadow
            drawOval(
                color = Color.Black.copy(alpha = .35f),
                topLeft = Offset(instrumentX - w * .08f, floorY - h * .025f),
                size = Size(w * .16f, h * .05f),
            )

            // ===== SCAN LINES across stage =====
            for (i in 0 until 3) {
                val y = h * (0.25f + i * 0.22f)
                drawLine(
                    color = accent.copy(alpha = .06f),
                    start = Offset(w * .05f, y),
                    end = Offset(w * .95f, y),
                    strokeWidth = 0.5.dp.toPx(),
                )
            }
        }

        // ===== STAG: Massive, prominent, glowing =====
        val stagEndPadding = (w * .02f).dp
        val stagHeightScaled = (stagHeight.value * scale).dp
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = stagEndPadding)
                .height(stagHeightScaled)
                .aspectRatio(1f)
                .graphicsLayer {
                    translationY = float.dp.toPx()
                    translationX = (1f - scale) * 12.dp.toPx()
                }
                .testTag("service_cover_stag"),
        ) {
            // Stag with intense glow layers
            Box(Modifier.fillMaxSize()) {
                // Outer glow aura
                Box(
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // Pulsing scale for breathing effect
                            val breathScale = 1f + pulse * 0.02f
                            scaleX = breathScale
                            scaleY = breathScale
                        }
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    accent2.copy(alpha = .25f + pulse * .15f),
                                    accent.copy(alpha = .15f + pulse * .1f),
                                    Color.Transparent,
                                ),
                                center = Offset.Unspecified,
                                radius = 0.7f,
                            )
                        )
                        .clip(CircleShape),
                )
                // Actual stag image
                V2DeerHeader(Modifier.fillMaxSize(), animated = motion)
            }
        }

        // ===== INSTRUMENT: Service scanner/radar on left =====
        val instrumentStartPadding = (w * .03f).dp
        val instrumentSizeScaled = (instrument.value * scale).dp
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .padding(start = instrumentStartPadding, bottom = if (compact) 4.dp else 8.dp)
                .size(instrumentSizeScaled)
                .testTag("service_scene"),
        ) {
            Box(Modifier.fillMaxSize()) {
                // Instrument base glow
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    accent.copy(alpha = .2f + pulse * .1f),
                                    accent2.copy(alpha = .12f),
                                    Color.Transparent,
                                ),
                                center = Offset.Unspecified,
                                radius = 0.8f,
                            )
                        )
                        .clip(CircleShape)
                        .graphicsLayer {
                            // Subtle rotation hint
                            rotationZ = (pulse * 2f - 1f) * 1.5f
                        },
                )
                scene(Modifier.fillMaxSize().padding(8.dp))
            }
        }
    }
}

@Composable
private fun HeroStatusStrip(
    status: String,
    statusColor: Color,
    metric: String?,
    compact: Boolean,
    collapsed: Boolean = false,
    modifier: Modifier = Modifier,
) {
    // The live status line is the one element that must never be folded away:
    // while a scan runs, "جاري الفحص · 3 من 6" stays readable in the folded bar.
    val live = collapsed
    val motion = rememberMotionEnabled()
    val pulse by rememberInfiniteTransition(label = "statusPulse").animateFloat(
        initialValue = 0f,
        targetValue = if (motion && live) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Column(modifier.fillMaxWidth()) {
        // Neon divider line
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            statusColor.copy(alpha = .6f + pulse * .3f),
                            statusColor.copy(alpha = .8f + pulse * .2f),
                            statusColor.copy(alpha = .6f + pulse * .3f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
        // Status bar with glassmorphism + neon border
        Row(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = if (live) .55f else .4f),
                            Color(0xFF0A0318).copy(alpha = if (live) .65f else .5f),
                        ),
                    ),
                )
                .border(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            statusColor.copy(alpha = .7f + pulse * .3f),
                            statusColor.copy(alpha = .9f + pulse * .1f),
                            statusColor.copy(alpha = .7f + pulse * .3f),
                            Color.Transparent,
                        ),
                    ),
                    RoundedCornerShape(0.dp),
                )
                .padding(horizontal = if (compact) 12.dp else 16.dp, vertical = if (compact) 6.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Live indicator dot with pulse
            Box(
                Modifier
                    .size(if (compact) 7.dp else 9.dp)
                    .clip(CircleShape)
                    .background(statusColor)
                    .graphicsLayer {
                        val s = 1f + pulse * 0.3f
                        scaleX = s
                        scaleY = s
                        alpha = 0.7f + pulse * 0.3f
                    },
            )
            Spacer(Modifier.width(8.dp))
            KText(
                status,
                size = when {
                    live -> 9.5.sp
                    compact -> 8.sp
                    else -> 8.8.sp
                },
                weight = FontWeight.Bold,
                color = statusColor.copy(alpha = 1f),
                maxLines = 1,
                letterSpacing = 0.3.sp,
            )
            Spacer(Modifier.weight(1f))
            // Metric in neon accent color
            Box(
                Modifier
                    .padding(horizontal = 10.dp, vertical = 3.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                statusColor.copy(alpha = .18f),
                                Color.Transparent,
                            ),
                        ),
                    )
                    .border(
                        1.dp,
                        statusColor.copy(alpha = .4f + pulse * .2f),
                        RoundedCornerShape(8.dp),
                    ),
            ) {
                KText(
                    metric ?: "قراءة محلية",
                    size = when {
                        live -> 8.8.sp
                        compact -> 7.5.sp
                        else -> 8.2.sp
                    },
                    weight = FontWeight.SemiBold,
                    color = statusColor.copy(alpha = .95f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Service instrument scene
 *
 * Reuses the official 200x200 service art inside a glowing circular port,
 * with orbit rings and a pulsing halo. Under Reduce Motion the ring and
 * dot snap to their static frame.
 * ------------------------------------------------------------------ */

@Composable
fun ServiceArtScene(
    spec: KnouxServiceSpec,
    modifier: Modifier = Modifier,
    animated: Boolean = true,
) {
    val motion = rememberMotionEnabled() && animated
    val transition = rememberInfiniteTransition(label = "serviceArt")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "serviceArtPulse",
    )
    val orbit by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing),
        ),
        label = "serviceArtOrbit",
    )
    val orbit2 by transition.animateFloat(
        initialValue = 180f,
        targetValue = if (motion) 540f else 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = LinearEasing),
        ),
        label = "serviceArtOrbit2",
    )
    val gold = Color(0xFFFFD700)
    val accent = spec.accent
    val accent2 = spec.accent2

    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = min(size.width, size.height) / 2f * .96f

            // ===== LAYER 1: Deep outer glow aura =====
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        accent.copy(alpha = .35f + pulse * .15f),
                        accent2.copy(alpha = .22f + pulse * .1f),
                        gold.copy(alpha = .1f + pulse * .05f),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = radius * 1.15f,
                ),
                radius = radius * 1.15f,
                center = center,
            )

            // ===== LAYER 2: Mid glow ring =====
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        accent.copy(alpha = .25f + pulse * .12f),
                        accent2.copy(alpha = .15f),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )

            // ===== LAYER 3: Concentric neon rings (static) =====
            for (i in 0 until 4) {
                val ringRadius = radius * (0.92f - i * 0.07f)
                val ringAlpha = 0.18f - i * 0.035f
                val ringColor = when (i % 3) {
                    0 -> accent
                    1 -> accent2
                    else -> gold
                }
                drawCircle(
                    color = ringColor.copy(alpha = ringAlpha),
                    radius = ringRadius,
                    center = center,
                    style = Stroke(width = (1.2f + i * 0.3f).dp.toPx(), cap = StrokeCap.Round),
                )
            }

            // ===== LAYER 4: Pulsing bright ring =====
            drawCircle(
                color = Color.White.copy(alpha = .25f + pulse * .2f),
                radius = radius * .95f,
                center = center,
                style = Stroke(width = (1.5f + pulse * 1.2f).dp.toPx(), cap = StrokeCap.Round),
            )

            // ===== LAYER 5: Orbiting energy arcs =====
            // Primary arc
            val arc1Start = -60f - orbit * 0.15f
            val arc1Sweep = 80f
            drawArc(
                color = accent.copy(alpha = .85f),
                startAngle = arc1Start,
                sweepAngle = arc1Sweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            )
            // Arc glow trail
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        Color.Transparent,
                        accent.copy(alpha = .6f),
                        Color.White.copy(alpha = .9f),
                        accent.copy(alpha = .6f),
                        Color.Transparent,
                    ),
                    center = center,
                ),
                startAngle = arc1Start - 10f,
                sweepAngle = arc1Sweep + 20f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round),
            )

            // Secondary arc (opposite direction)
            val arc2Start = 120f + orbit2 * 0.1f
            val arc2Sweep = 60f
            drawArc(
                color = accent2.copy(alpha = .7f),
                startAngle = arc2Start,
                sweepAngle = arc2Sweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round),
            )

            // Tertiary thin arc
            val arc3Start = -orbit * 0.08f
            val arc3Sweep = 40f
            drawArc(
                color = gold.copy(alpha = .55f),
                startAngle = arc3Start,
                sweepAngle = arc3Sweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round),
            )

            // ===== LAYER 6: Orbiting particles =====
            if (motion) {
                // Main orbiting particle
                val p1Angle = Math.toRadians(orbit.toDouble())
                val p1X = center.x + (radius * .9f * cos(p1Angle)).toFloat()
                val p1Y = center.y + (radius * .9f * sin(p1Angle)).toFloat()
                drawCircle(
                    color = Color.White.copy(alpha = .95f),
                    radius = 3.5.dp.toPx(),
                    center = Offset(p1X, p1Y),
                )
                // Particle glow
                drawCircle(
                    color = accent.copy(alpha = .6f),
                    radius = 8.dp.toPx(),
                    center = Offset(p1X, p1Y),
                )

                // Secondary particle
                val p2Angle = Math.toRadians((orbit + 120f).toDouble())
                val p2X = center.x + (radius * .75f * cos(p2Angle)).toFloat()
                val p2Y = center.y + (radius * .75f * sin(p2Angle)).toFloat()
                drawCircle(
                    color = accent2.copy(alpha = .85f),
                    radius = 2.5.dp.toPx(),
                    center = Offset(p2X, p2Y),
                )

                // Tertiary particle
                val p3Angle = Math.toRadians((orbit2 + 60f).toDouble())
                val p3X = center.x + (radius * .6f * cos(p3Angle)).toFloat()
                val p3Y = center.y + (radius * .6f * sin(p3Angle)).toFloat()
                drawCircle(
                    color = gold.copy(alpha = .8f),
                    radius = 2.dp.toPx(),
                    center = Offset(p3X, p3Y),
                )
            }

            // ===== LAYER 7: Crosshair center lines =====
            val crossLen = radius * .85f
            drawLine(
                color = accent.copy(alpha = .35f + pulse * .15f),
                start = Offset(center.x - crossLen, center.y),
                end = Offset(center.x + crossLen, center.y),
                strokeWidth = 1.2.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawLine(
                color = accent2.copy(alpha = .35f + pulse * .15f),
                start = Offset(center.x, center.y - crossLen),
                end = Offset(center.x, center.y + crossLen),
                strokeWidth = 1.2.dp.toPx(),
                cap = StrokeCap.Round,
            )

            // ===== LAYER 8: Tick marks around circumference =====
            for (tick in 0 until 32) {
                val angle = tick * (2.0 * PI / 32.0)
                val isMajor = tick % 4 == 0
                val inner = radius * (if (isMajor) .78f else .85f)
                val outer = radius * .93f
                val tickAlpha = if (isMajor) .55f else .25f
                drawLine(
                    color = accent.copy(alpha = tickAlpha),
                    start = Offset(
                        center.x + (inner * cos(angle)).toFloat(),
                        center.y + (inner * sin(angle)).toFloat(),
                    ),
                    end = Offset(
                        center.x + (outer * cos(angle)).toFloat(),
                        center.y + (outer * sin(angle)).toFloat(),
                    ),
                    strokeWidth = if (isMajor) 1.5.dp.toPx() else 0.8.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }

        // ===== CENTER CONTENT: Service artwork/icon =====
        Box(
            Modifier
                .fillMaxSize(.72f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFF1A0A3A),
                            Color(0xFF0D0318),
                            Color(0xFF050108),
                        ),
                    ),
                )
                .border(
                    2.dp,
                    Brush.linearGradient(
                        listOf(
                            accent.copy(alpha = .8f),
                            accent2.copy(alpha = .9f),
                            gold.copy(alpha = .7f),
                            accent2.copy(alpha = .8f),
                            accent.copy(alpha = .7f),
                        ),
                    ),
                    CircleShape,
                )
                .graphicsLayer {
                    // Subtle breathing scale
                    val s = 1f + pulse * 0.015f
                    scaleX = s
                    scaleY = s
                },
            contentAlignment = Alignment.Center,
        ) {
            val artwork = spec.artwork
            if (artwork != null) {
                Image(
                    painter = painterResource(artwork),
                    contentDescription = spec.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .padding(6.dp),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        spec.glyph,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(48.dp)
                            .graphicsLayer {
                                rotationZ = pulse * 2f - 1f
                            },
                    )
                    // Glyph glow
                    Box(
                        Modifier
                            .size(60.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        accent.copy(alpha = .2f + pulse * .1f),
                                        Color.Transparent,
                                    ),
                                    center = Offset.Unspecified,
                                    radius = 0.6f,
                                ),
                            )
                            .clip(CircleShape),
                    )
                }
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Measured progress (real totals only)
 * ------------------------------------------------------------------ */

@Composable
internal fun ServiceMeter(
    progress: Float,
    tint: Color,
    motion: Boolean,
    modifier: Modifier = Modifier,
) {
    val value by animateFloatAsState(
        targetValue = if (progress.isFinite()) progress.coerceIn(0f, 1f) else 0f,
        animationSpec = if (motion) tween(200) else snap(),
        label = "service-measured-progress",
    )
    Canvas(modifier) {
        val stroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        drawArc(tint.copy(alpha = .22f), -90f, 360f, false, style = stroke)
        drawArc(tint, -90f, 360f * value, false, style = stroke)
    }
}

/* ------------------------------------------------------------------ *
 * Deep Scan sonar instrument
 *
 * The sweep is a UI value driven by the Compose frame clock and only while
 * the controller reports a real running scan. Cancel / result leaves the
 * instrument frozen. Reduce Motion keeps the same static frame; the textual
 * state still explains what the service is doing.
 * ------------------------------------------------------------------ */

@Composable
internal fun DeepScanSonar(state: DeepScanUiState, modifier: Modifier = Modifier) {
    val motion = rememberMotionEnabled()
    val running = state is DeepScanUiState.Running
    var sweep by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(running, motion) {
        if (!running || !motion) return@LaunchedEffect
        var previous = 0L
        while (true) {
            withFrameNanos { now ->
                if (previous != 0L) {
                    val deltaMs = ((now - previous) / 1_000_000f).coerceIn(0f, 64f)
                    sweep = sonarNextAngle(sweep, deltaMs, SONAR_DEGREES_PER_MS)
                }
                previous = now
            }
        }
    }

    val progressTarget = when (state) {
        is DeepScanUiState.Running -> state.completed.toFloat() / DeepScanStage.values().size
        is DeepScanUiState.Completed -> 1f
        else -> 0f
    }
    val progress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = if (motion) tween(220) else snap(),
        label = "deepScanProgress",
    )
    val accent = when (state) {
        is DeepScanUiState.Completed -> V2Green
        is DeepScanUiState.Cancelled -> V2Amber
        is DeepScanUiState.Failed -> V2Red
        is DeepScanUiState.Running -> V2Cyan
        else -> V2Violet
    }
    val description = when (state) {
        is DeepScanUiState.Idle -> "رادار الفحص العميق — جاهز"
        is DeepScanUiState.Running -> "رادار الفحص العميق — جارٍ ${state.completed} من 6"
        is DeepScanUiState.Completed -> "رادار الفحص العميق — اكتمل الفحص"
        is DeepScanUiState.Cancelled -> "رادار الفحص العميق — أُلغي الفحص"
        is DeepScanUiState.Failed -> "رادار الفحص العميق — تعذّر إكمال الفحص"
    }

    Box(modifier.testTag("deepscan_sonar"), contentAlignment = Alignment.Center) {
        Canvas(
            Modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = description
                    stateDescription = description
                    if (state is DeepScanUiState.Running) {
                        progressBarRangeInfo = ProgressBarRangeInfo(
                            current = state.completed.toFloat(),
                            range = 0f..DeepScanStage.values().size.toFloat(),
                        )
                    }
                },
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = min(size.width, size.height) / 2f * .96f
            val ring = 1.dp.toPx()

            // Dark glass orb behind the instrument face.
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF261152).copy(alpha = .92f), Color(0xFF070218).copy(alpha = .98f)),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(accent.copy(alpha = .30f), accent.copy(alpha = .08f), Color.Transparent),
                    center = center,
                    radius = radius * .96f,
                ),
                radius = radius * .96f,
                center = center,
            )
            listOf(.34f, .52f, .70f, .86f, 1f).forEachIndexed { index, fraction ->
                drawCircle(
                    color = accent.copy(alpha = if (index == 4) .48f else .19f),
                    radius = radius * fraction,
                    center = center,
                    style = Stroke(if (index == 4) ring * 1.4f else ring),
                )
            }
            drawLine(
                accent.copy(alpha = .14f),
                Offset(center.x - radius, center.y),
                Offset(center.x + radius, center.y),
                strokeWidth = ring,
            )
            drawLine(
                accent.copy(alpha = .14f),
                Offset(center.x, center.y - radius),
                Offset(center.x, center.y + radius),
                strokeWidth = ring,
            )

            // Real stage progress, kept outside the sweep.
            drawArc(
                color = Color.Black.copy(alpha = .34f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round),
            )

            // Tick marks around the instrument face.
            for (tick in 0 until 48) {
                val angle = tick * (2.0 * PI / 48.0)
                val inner = radius * if (tick % 4 == 0) .81f else .87f
                drawLine(
                    color = accent.copy(alpha = if (tick % 4 == 0) .50f else .20f),
                    start = Offset(
                        center.x + (inner * cos(angle)).toFloat(),
                        center.y + (inner * sin(angle)).toFloat(),
                    ),
                    end = Offset(
                        center.x + (radius * .95f * cos(angle)).toFloat(),
                        center.y + (radius * .95f * sin(angle)).toFloat(),
                    ),
                    strokeWidth = if (tick % 4 == 0) 1.2.dp.toPx() else .7.dp.toPx(),
                )
            }

            val drawSweep = running && motion
            if (drawSweep) {
                rotate(sweep, center) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                accent.copy(alpha = .0f),
                                accent.copy(alpha = .18f),
                                accent.copy(alpha = .48f),
                                Color.White.copy(alpha = .62f),
                            ),
                            center = center,
                        ),
                        startAngle = -58f,
                        sweepAngle = 58f,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2f, radius * 2f),
                    )
                    drawLine(
                        brush = Brush.linearGradient(
                            listOf(Color.Transparent, accent.copy(alpha = .85f), Color.White.copy(alpha = .95f)),
                            start = center,
                            end = Offset(center.x + radius, center.y),
                        ),
                        start = center,
                        end = Offset(center.x + radius, center.y),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
            } else {
                drawLine(
                    color = accent.copy(alpha = .38f),
                    start = center,
                    end = Offset(center.x + radius, center.y),
                    strokeWidth = 1.3.dp.toPx(),
                )
            }

            // Fixed blips. They only light up while the real sweep passes them.
            listOf(34f, 142f, 233f, 311f).forEach { blipAngle ->
                val alpha = sonarBlipAlpha(blipAngle, sweep, drawSweep)
                val radians = blipAngle * PI / 180.0
                val distance = radius * .62f
                val point = Offset(
                    center.x + (distance * cos(radians)).toFloat(),
                    center.y + (distance * sin(radians)).toFloat(),
                )
                drawCircle(accent.copy(alpha = alpha * .30f), radius = 7.dp.toPx(), center = point)
                drawCircle(accent.copy(alpha = alpha), radius = 2.6.dp.toPx(), center = point)
            }
        }
        Box(
            Modifier
                .size(if (running) 54.dp else 48.dp)
                .clip(CircleShape)
                .background(Color(0xFF070218).copy(alpha = .94f))
                .border(1.dp, accent.copy(alpha = .55f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.knoux_symbol_alpha),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().padding(6.dp),
            )
        }
    }
}

internal const val SONAR_DEGREES_PER_MS = .075f

internal fun sonarNextAngle(current: Float, deltaMs: Float, degreesPerMs: Float): Float {
    if (!deltaMs.isFinite() || deltaMs <= 0f) return current
    val next = current + deltaMs * degreesPerMs
    return ((next % 360f) + 360f) % 360f
}

internal fun sonarBlipAlpha(blipAngle: Float, sweepAngle: Float, active: Boolean): Float {
    if (!active) return .16f
    val delta = ((sweepAngle - blipAngle) % 360f + 360f) % 360f
    return if (delta < 46f) .18f + .82f * (1f - delta / 46f) else .10f
}
