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
import androidx.compose.foundation.layout.offset
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
 * KNOUX AI Pro — compact service header
 *
 * Owner direction (17 Nov, second pass): the intro Home screen is the
 * reference. Every service opens on a short header now — the official
 * stag floats behind the identity block, the service instrument sits in
 * a small glass window, and the measured status ribbon stays readable.
 * The results get the screen back.
 * ------------------------------------------------------------------ */

/**
 * Height of the compact service header. On a 360dp phone it is 176dp, so the
 * header never behaves like a landing page. Landscape uses a shorter lane so
 * the results list still has usable room.
 */
internal fun serviceHeaderHeight(maxWidth: Dp, compact: Boolean): Dp =
    if (compact) 120.dp else (maxWidth * .49f).coerceIn(166.dp, 196.dp)

/**
 * The stag owns the header height, with a width cap so the text keeps a safe
 * lane. It is deliberately larger than the old 44dp badge: the owner asked for
 * the stag to sit behind the header, not beside the title as a decoration.
 */
internal fun serviceStagSize(headerHeight: Dp, maxWidth: Dp): Dp =
    minOf(headerHeight * .98f, maxWidth * .52f).coerceIn(104.dp, 194.dp)

/** Title size that keeps every service name readable inside the short header. */
internal fun serviceTitleSize(titleLength: Int): Float = when {
    titleLength <= 14 -> 27f
    titleLength <= 21 -> 24f
    titleLength <= 27 -> 21.5f
    else -> 19.5f
}

/** Title size for the shorter landscape lane. */
internal fun compactServiceTitleSize(titleLength: Int): Float =
    serviceTitleSize(titleLength) - 2.5f

/**
 * The shared service page. The compact header is fixed above the results; the
 * list beneath it scrolls, so the stag and the live status never disappear.
 * Only the primary action stays pinned so a running scan can always be
 * cancelled.
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
        val headerHeight = serviceHeaderHeight(maxWidth, compact)
        Column(
            Modifier
                .fillMaxSize()
                .testTag("service_header_fixed"),
        ) {
            ServiceHero(
                spec = spec,
                status = status,
                statusColor = statusColor,
                metric = metric,
                compact = compact,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
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
 * The compact service header used by every service page and by the fixed Deep
 * Scan screen. The stag floats behind the identity block; the service
 * instrument sits in a small glass window; the measured status ribbon is the
 * only fixed strip and it never lies about a running scan.
 */
@Composable
fun ServiceHero(
    spec: KnouxServiceSpec,
    status: String,
    statusColor: Color,
    metric: String?,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    scene: @Composable (Modifier) -> Unit,
) {
    val motion = rememberMotionEnabled()
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    val revealAlpha by animateFloatAsState(
        targetValue = if (entered || !motion) 1f else 0f,
        animationSpec = if (motion) tween(380) else snap(),
        label = "serviceHeaderAlpha",
    )
    val revealRise by animateFloatAsState(
        targetValue = if (entered || !motion) 0f else 12f,
        animationSpec = if (motion) tween(380) else snap(),
        label = "serviceHeaderRise",
    )
    val transition = rememberInfiniteTransition(label = "serviceHeaderStage")
    val float by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) -5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "serviceStagFloat",
    )
    val breath by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "serviceStagBreath",
    )
    val titleSize = (if (compact) compactServiceTitleSize(spec.title.length) else serviceTitleSize(spec.title.length)).sp

    BoxWithConstraints(
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
            .drawBehind { CompactHeroSky(spec) }
            .clipToBounds(),
    ) {
        val stagSize = serviceStagSize(maxHeight, maxWidth)
        val glowScale = 1.04f + breath * .035f

        // The halo travels with the stag, so the mascot reads as one object
        // instead of a cut-out pasted on the sky.
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 12.dp, y = 6.dp)
                .size(stagSize * 1.25f)
                .graphicsLayer {
                    scaleX = glowScale
                    scaleY = glowScale
                    alpha = .70f + breath * .16f
                }
                .background(
                    Brush.radialGradient(
                        listOf(
                            spec.accent2.copy(alpha = .34f),
                            spec.accent.copy(alpha = .18f),
                            Color.Transparent,
                        ),
                    ),
                    CircleShape,
                ),
        )
        Image(
            painter = painterResource(R.drawable.knoux_stag_official),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 10.dp, y = 8.dp)
                .size(stagSize)
                .graphicsLayer {
                    translationY = float.dp.toPx()
                    rotationZ = -1.2f + breath * .7f
                    scaleX = .995f + breath * .012f
                    scaleY = .995f + breath * .012f
                    alpha = .96f
                }
                .testTag("service_cover_stag"),
            contentScale = ContentScale.Fit,
        )

        // Text keeps the left lane bright while the stag stays visible on the
        // right. The gradient is intentionally soft; the stag is a background
        // layer, not a cropped card.
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        0f to V2Deep.copy(alpha = .90f),
                        .42f to V2Deep.copy(alpha = .62f),
                        .70f to V2Deep.copy(alpha = .16f),
                        1f to Color.Transparent,
                    )
                ),
        )

        Column(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = revealAlpha
                    translationY = revealRise.dp.toPx()
                }
                .padding(horizontal = if (compact) 12.dp else 14.dp)
                .padding(top = if (compact) 8.dp else 10.dp, bottom = if (compact) 7.dp else 9.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Small service instrument window. The same composable that
                // used to fill the old hero is now a badge, so Deep Scan keeps
                // its live radar without stealing the results area.
                Box(
                    Modifier
                        .size(if (compact) 42.dp else 54.dp)
                        .clip(RoundedCornerShape(if (compact) 13.dp else 16.dp))
                        .background(Color.Black.copy(alpha = .30f))
                        .border(
                            1.dp,
                            spec.accent.copy(alpha = .52f),
                            RoundedCornerShape(if (compact) 13.dp else 16.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    scene(Modifier.fillMaxSize().padding(if (compact) 3.dp else 4.dp))
                    Box(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(3.dp)
                            .size(if (compact) 13.dp else 15.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(V2Deep.copy(alpha = .94f))
                            .border(1.dp, spec.accent.copy(alpha = .66f), RoundedCornerShape(5.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            spec.glyph,
                            contentDescription = null,
                            tint = spec.accent,
                            modifier = Modifier.size(if (compact) 8.dp else 9.dp),
                        )
                    }
                }
                Spacer(Modifier.width(9.dp))
                Column(Modifier.weight(1f)) {
                    KText(
                        "${spec.order} · ${spec.english}",
                        size = if (compact) 7.2.sp else 8.sp,
                        weight = FontWeight.Bold,
                        color = spec.accent2,
                        letterSpacing = 1.8.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(1.dp))
                    KText(
                        if (spec.pro) "KNOUX AI · PRO" else "KNOUX AI",
                        size = if (compact) 5.8.sp else 6.4.sp,
                        weight = FontWeight.SemiBold,
                        color = V2Muted,
                        letterSpacing = 1.4.sp,
                        maxLines = 1,
                    )
                }
                if (spec.pro) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(V2GoldText.copy(alpha = .16f))
                            .border(1.dp, V2GoldText.copy(alpha = .50f), RoundedCornerShape(50))
                            .padding(horizontal = 7.dp, vertical = 3.dp),
                    ) {
                        KText("PRO", size = 6.6.sp, weight = FontWeight.Black, color = V2GoldText)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Column(
                Modifier
                    .fillMaxWidth(if (compact) .86f else .68f)
                    .testTag("service_cover_title_block"),
            ) {
                KText(
                    spec.title,
                    size = titleSize,
                    weight = FontWeight.Black,
                    color = Color.White,
                    lineHeight = (titleSize.value * 1.12f).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { heading() }
                        .testTag("service_cover_title"),
                )
                if (!compact) {
                    Spacer(Modifier.height(2.dp))
                    KText(
                        spec.tagline,
                        size = 9.8.sp,
                        lineHeight = 12.5.sp,
                        color = V2Soft.copy(alpha = .94f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 4.dp),
                    )
                }
            }

            Spacer(Modifier.height(if (compact) 4.dp else 6.dp))
            HeroStatusRibbon(
                status = status,
                statusColor = statusColor,
                metric = metric,
                compact = compact,
            )
        }
    }
}

/**
 * Short cosmic sky for the compact header. It keeps the Home/Deep Scan
 * identity — nebula, aurora, star field, mountain ridge — but at a header
 * scale where it reads as a live backdrop instead of a poster.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.CompactHeroSky(spec: KnouxServiceSpec) {
    val w = size.width
    val h = size.height
    val accent = spec.accent
    val accent2 = spec.accent2
    val gold = Color(0xFFFFD700)

    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF050210),
                Color(0xFF0D0424),
                Color(0xFF1A0638),
                Color(0xFF0C0322),
            ),
            startY = 0f,
            endY = h,
        ),
        size = Size(w, h),
    )

    drawCircle(
        brush = Brush.radialGradient(
            listOf(accent.copy(alpha = .26f), accent.copy(alpha = .08f), Color.Transparent),
            center = Offset(w * .18f, h * .35f),
            radius = w * .62f,
        ),
        radius = w * .62f,
        center = Offset(w * .18f, h * .35f),
    )
    drawCircle(
        brush = Brush.radialGradient(
            listOf(accent2.copy(alpha = .24f), accent2.copy(alpha = .07f), Color.Transparent),
            center = Offset(w * .86f, h * .28f),
            radius = w * .56f,
        ),
        radius = w * .56f,
        center = Offset(w * .86f, h * .28f),
    )
    drawCircle(
        brush = Brush.radialGradient(
            listOf(gold.copy(alpha = .12f), Color.Transparent),
            center = Offset(w * .52f, h * .08f),
            radius = w * .40f,
        ),
        radius = w * .40f,
        center = Offset(w * .52f, h * .08f),
    )

    // Soft aurora ribbon.
    val aurora = Path().apply {
        moveTo(-w * .1f, h * .62f)
        cubicTo(w * .18f, h * .30f, w * .46f, h * .86f, w * .72f, h * .54f)
        cubicTo(w * .90f, h * .34f, w * 1.05f, h * .64f, w * 1.2f, h * .44f)
    }
    drawPath(
        aurora,
        brush = Brush.linearGradient(
            listOf(
                Color.Transparent,
                accent.copy(alpha = .30f),
                accent2.copy(alpha = .38f),
                gold.copy(alpha = .20f),
                Color.Transparent,
            ),
            start = Offset(0f, h * .62f),
            end = Offset(w, h * .44f),
        ),
        style = Stroke(width = h * .24f, cap = StrokeCap.Round),
    )
    drawPath(
        aurora,
        brush = Brush.linearGradient(
            listOf(
                Color.Transparent,
                Color.White.copy(alpha = .32f),
                accent.copy(alpha = .48f),
                Color.Transparent,
            ),
        ),
        style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round),
    )

    // Deterministic stars — small enough not to compete with the title.
    for (i in 0 until 34) {
        val x = ((i * 67 + 11) % 101) / 101f
        val y = ((i * 43 + 7) % 97) / 97f
        val r = (.35f + ((i * 19) % 7) / 7f * .8f).dp.toPx()
        val alpha = .22f + ((i * 13) % 5) / 5f * .40f
        drawCircle(Color.White.copy(alpha = alpha), r, Offset(x * w, y * h))
    }

    // Mountain ridge, matching the reference silhouette.
    val ridge = Path().apply {
        moveTo(0f, h)
        lineTo(0f, h * .82f)
        lineTo(w * .10f, h * .66f)
        lineTo(w * .23f, h * .80f)
        lineTo(w * .38f, h * .60f)
        lineTo(w * .52f, h * .78f)
        lineTo(w * .66f, h * .63f)
        lineTo(w * .80f, h * .80f)
        lineTo(w * .92f, h * .67f)
        lineTo(w, h * .74f)
        lineTo(w, h)
        close()
    }
    drawPath(
        ridge,
        brush = Brush.verticalGradient(
            listOf(Color(0xFF1B0A3C).copy(alpha = .90f), Color(0xFF080214).copy(alpha = .98f)),
            startY = h * .55f,
            endY = h,
        ),
    )
    val rim = Path().apply {
        moveTo(0f, h * .82f)
        lineTo(w * .10f, h * .66f)
        lineTo(w * .23f, h * .80f)
        lineTo(w * .38f, h * .60f)
        lineTo(w * .52f, h * .78f)
        lineTo(w * .66f, h * .63f)
        lineTo(w * .80f, h * .80f)
        lineTo(w * .92f, h * .67f)
        lineTo(w, h * .74f)
    }
    drawPath(
        rim,
        brush = Brush.linearGradient(
            listOf(accent.copy(alpha = .55f), accent2.copy(alpha = .62f), gold.copy(alpha = .42f), accent2.copy(alpha = .48f)),
        ),
        style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round),
    )

    // Floor glow under the stag.
    drawOval(
        brush = Brush.radialGradient(
            listOf(accent2.copy(alpha = .26f), accent.copy(alpha = .10f), Color.Transparent),
            center = Offset(w * .82f, h * .94f),
            radius = w * .38f,
        ),
        topLeft = Offset(w * .48f, h * .82f),
        size = Size(w * .52f, h * .18f),
    )

    drawRect(
        brush = Brush.verticalGradient(
            listOf(Color.White.copy(alpha = .10f), Color.Transparent),
            startY = 0f,
            endY = h * .09f,
        ),
        size = Size(w, h * .09f),
    )
}

@Composable
private fun HeroStatusRibbon(
    status: String,
    statusColor: Color,
    metric: String?,
    compact: Boolean,
) {
    val motion = rememberMotionEnabled()
    val pulse by rememberInfiniteTransition(label = "serviceStatusPulse").animateFloat(
        initialValue = 0f,
        targetValue = if (motion) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (compact) 9.dp else 10.dp))
            .background(Color.Black.copy(alpha = .40f))
            .border(
                1.dp,
                statusColor.copy(alpha = .28f + pulse * .16f),
                RoundedCornerShape(if (compact) 9.dp else 10.dp),
            )
            .padding(horizontal = if (compact) 8.dp else 10.dp, vertical = if (compact) 4.dp else 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(if (compact) 6.dp else 7.dp)
                .clip(CircleShape)
                .background(statusColor)
                .graphicsLayer {
                    val s = 1f + pulse * .22f
                    scaleX = s
                    scaleY = s
                    alpha = .78f + pulse * .22f
                },
        )
        Spacer(Modifier.width(7.dp))
        KText(
            status,
            size = if (compact) 8.4.sp else 8.8.sp,
            weight = FontWeight.Bold,
            color = statusColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.weight(1f))
        Box(
            Modifier
                .padding(start = 8.dp)
                .clip(RoundedCornerShape(50))
                .background(statusColor.copy(alpha = .12f))
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            KText(
                metric ?: "قراءة محلية",
                size = if (compact) 7.2.sp else 7.6.sp,
                weight = FontWeight.SemiBold,
                color = statusColor.copy(alpha = .95f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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
