package com.knoux.ai.ui.v2

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import kotlin.math.cos
import kotlin.math.sin

/*
 * Presentation only. The owner's reference is a hub, not a screenshot: real
 * routes enter each primary/side action and DeviceFacts supplies every number.
 * The module is deliberately independent from any scan controller.
 */

internal const val LUXURY_SERVICE_COLUMNS = 4

/** Small deliberate hierarchy instead of a poster-sized title. */
@Composable
internal fun V2LuxuryIdentity(
    title: String,
    english: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier,
    home: Boolean = false,
) {
    val day = LocalDayMode.current
    val titleColor = if (day) DayInk else Color.White
    Column(
        modifier.fillMaxWidth().padding(start = 76.dp, top = 2.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (home) {
            // The official seal crowns the home wordmark like the reference
            // hub: pearl disc, breathing halo, gold laurels below.
            com.knoux.ai.ui.components.BrandSeal(size = 44.dp, haloColor = accent)
            Spacer(Modifier.height(4.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            LuxuryLaurel(V2GoldText, mirror = false)
            Spacer(Modifier.width(7.dp))
            if (home) {
                // Keep the Latin wordmark reading correctly inside the RTL shell:
                // KNOUX AI (white) then Pro (gold), never "PRO KNOUX AI".
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KText(
                            "KNOUX AI",
                            size = 23.sp,
                            weight = FontWeight.Black,
                            color = titleColor,
                            maxLines = 1,
                        )
                        Spacer(Modifier.width(5.dp))
                        KText(
                            "Pro",
                            size = 21.sp,
                            weight = FontWeight.Black,
                            color = V2GoldText,
                            maxLines = 1,
                        )
                    }
                }
            } else {
                KText(
                    title,
                    size = 21.sp,
                    weight = FontWeight.Black,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(7.dp))
            LuxuryLaurel(V2GoldText, mirror = true)
        }
        Spacer(Modifier.height(1.dp))
        if (!home) {
            KText(
                english,
                size = 9.sp,
                weight = FontWeight.Bold,
                color = accent,
                letterSpacing = 1.25.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(1.dp))
        }
        KText(
            subtitle,
            size = 10.5.sp,
            lineHeight = 14.sp,
            color = V2Soft,
            align = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun LuxuryLaurel(color: Color, mirror: Boolean) {
    Canvas(Modifier.size(width = 14.dp, height = 25.dp).graphicsLayer { scaleX = if (mirror) -1f else 1f }) {
        val stem = Path().apply {
            moveTo(size.width * .75f, size.height * .06f)
            cubicTo(size.width * .31f, size.height * .34f, size.width * .25f, size.height * .63f, size.width * .62f, size.height * .94f)
        }
        drawPath(stem, color.copy(alpha = .85f), style = Stroke(1.4.dp.toPx(), cap = StrokeCap.Round))
        for (index in 0 until 5) {
            val y = size.height * (.20f + index * .13f)
            val leaf = Path().apply {
                moveTo(size.width * .53f, y)
                cubicTo(size.width * .07f, y - size.height * .10f, size.width * .10f, y + size.height * .03f, size.width * .49f, y + size.height * .13f)
            }
            drawPath(leaf, color.copy(alpha = .72f))
        }
    }
}

/** Official transparent stag, never a service screenshot or boxed mascot. */
@Composable
internal fun V2LuxuryHeaderScenery(accent: Color, modifier: Modifier = Modifier) {
    val theme = LocalProTheme.current
    Box(modifier.fillMaxWidth().height(228.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                Brush.radialGradient(
                    listOf(accent.copy(alpha = .23f), Color.Transparent),
                    center = Offset(size.width * .10f, size.height * .35f),
                    radius = size.width * .57f,
                ),
                radius = size.width * .57f,
                center = Offset(size.width * .10f, size.height * .35f),
            )
            drawPath(
                Path().apply {
                    moveTo(0f, size.height * .36f)
                    lineTo(size.width * .14f, size.height * .19f)
                    lineTo(size.width * .30f, size.height * .37f)
                    lineTo(size.width * .40f, size.height * .22f)
                    lineTo(size.width * .58f, size.height * .41f)
                    lineTo(0f, size.height * .46f)
                    close()
                },
                Brush.verticalGradient(
                    listOf(theme.mountainTint.copy(alpha = .48f), Color.Transparent),
                    startY = size.height * .15f,
                    endY = size.height * .52f,
                ),
            )
        }
        V2DeerHeader(
            Modifier.align(Alignment.TopStart)
                .offset(x = (-4).dp, y = 33.dp)
                .size(width = 133.dp, height = 174.dp)
                .graphicsLayer { alpha = .98f },
            animated = true,
        )
    }
}

/** A luminous health instrument with three individually measured inputs. */
@Composable
internal fun V2LuxuryHealthPanel(
    gaugeLabel: String,
    gaugePercent: Int,
    lines: List<SectionLine>,
    accent: Color,
    accent2: Color,
    modifier: Modifier = Modifier,
) {
    V2LuxurySurface(modifier.fillMaxWidth(), radius = 23.dp, accent = accent, borderAlpha = .58f) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // RTL row: put the measured lines first so the gauge lands on the
            // visual left, matching the reference instrument layout.
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                lines.take(3).forEach { V2LuxuryHealthLine(it) }
            }
            Spacer(Modifier.width(10.dp))
            Box(Modifier.width(1.dp).height(72.dp).background(
                Brush.verticalGradient(listOf(Color.Transparent, accent.copy(alpha = .40f), Color.Transparent)),
            ))
            Spacer(Modifier.width(10.dp))
            V2LuxuryGauge(gaugePercent, gaugeLabel, accent, accent2, size = 88.dp)
        }
    }
}

@Composable
private fun V2LuxuryGauge(labelPercent: Int, label: String, accent: Color, accent2: Color, size: Dp) {
    val theme = LocalProTheme.current
    val day = LocalDayMode.current
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = this.size.minDimension * .465f
            drawCircle(
                Brush.radialGradient(
                    // Porcelain face in day mode; the neon arc keeps its punch.
                    listOf(if (day) DayCardTop else theme.gaugeCore, if (day) DayCardBottom else theme.gaugeRim),
                    center = center,
                    radius = r * .90f,
                ),
                radius = r * .90f,
            )
            drawCircle(
                Brush.radialGradient(listOf(accent.copy(alpha = .26f), Color.Transparent), center = center, radius = r * 1.12f),
                radius = r * 1.12f,
            )
            val ring = Stroke(4.dp.toPx(), cap = StrokeCap.Round)
            drawCircle(accent.copy(alpha = .13f), radius = r, style = ring)
            drawArc(
                Brush.sweepGradient(listOf(V2Pink, V2Violet, V2Cyan, V2GoldText, V2Pink), center),
                startAngle = -90f,
                sweepAngle = 360f * labelPercent.coerceIn(0, 100) / 100f,
                useCenter = false,
                style = ring,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2f, r * 2f),
            )
            drawCircle((if (day) DayBorderSoft else Color.White).copy(alpha = if (day) .55f else .09f), radius = r * .84f, style = Stroke(.75.dp.toPx()))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            KText(
                if (labelPercent > 0) "$labelPercent%" else "—",
                size = 23.sp,
                weight = FontWeight.Black,
                color = Color.White,
                maxLines = 1,
            )
            KText(label, size = 9.sp, color = V2Soft, maxLines = 2, align = TextAlign.Center, lineHeight = 11.5.sp)
            Spacer(Modifier.height(2.dp))
            Icon(Icons.Rounded.Verified, null, tint = if (day) DayGreen else V2Green, modifier = Modifier.size(10.dp))
        }
    }
}

@Composable
internal fun V2LuxuryHealthLine(line: SectionLine) {
    val day = LocalDayMode.current
    val tint = if (day) dayAccent(line.accent) else line.accent
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(22.dp).clip(RoundedCornerShape(8.dp))
                .background(Brush.verticalGradient(listOf(tint.copy(alpha = if (day) .16f else .22f), tint.copy(alpha = .04f))))
                .border(1.dp, tint.copy(alpha = if (day) .40f else .28f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(line.icon, null, tint = tint, modifier = Modifier.size(13.dp))
        }
        Spacer(Modifier.width(7.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                KText(line.label, size = 10.5.sp, weight = FontWeight.SemiBold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.width(4.dp))
                Spacer(Modifier.weight(1f))
                KText(line.value, size = 9.2.sp, color = V2Soft, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(3.dp))
            V2LuxuryProgress(line.progress, line.accent)
        }
    }
}

@Composable
internal fun V2LuxuryProgress(progress: Float, accent: Color, modifier: Modifier = Modifier) {
    val day = LocalDayMode.current
    Box(modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(50)).background(if (day) DayBorderSoft.withAlpha(.30f) else Color.White.copy(alpha = .09f))) {
        Box(
            Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).height(4.dp).clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(listOf(accent, V2Cyan, V2Green.copy(alpha = .80f)))),
        )
    }
}

/** Two independently routed side actions and a free central service instrument. */
@Composable
internal fun V2LuxuryStage(
    spec: KnouxServiceSpec,
    actionLabel: String,
    left: SectionSide,
    right: SectionSide,
    onPrimary: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth().height(205.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width * .50f, size.height * .73f)
            for (index in 0 until 4) {
                drawOval(
                    brush = Brush.sweepGradient(
                        listOf(Color.Transparent, spec.accent, V2GoldText, spec.accent2, Color.Transparent),
                        center = center,
                    ),
                    topLeft = Offset(size.width * (.25f - index * .018f), size.height * (.78f - index * .019f)),
                    size = Size(size.width * (.50f + index * .036f), size.height * (.14f + index * .038f)),
                    style = Stroke((1.5f - index * .20f).dp.toPx()),
                )
            }
            drawCircle(
                Brush.radialGradient(
                    listOf(spec.accent.copy(alpha = .30f), spec.accent2.copy(alpha = .12f), Color.Transparent),
                    center = Offset(size.width * .5f, size.height * .49f),
                    radius = size.width * .33f,
                ),
                radius = size.width * .33f,
                center = Offset(size.width * .5f, size.height * .49f),
            )
        }
        V2LuxurySideCard(left, spec.accent2, Modifier.align(Alignment.CenterStart).offset(y = 16.dp), onLeft)
        V2LuxurySideCard(right, V2GoldText, Modifier.align(Alignment.CenterEnd).offset(y = 16.dp), onRight)
        V2LuxuryOrb(
            spec = spec,
            actionLabel = actionLabel,
            onClick = onPrimary,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun V2LuxurySideCard(
    side: SectionSide,
    accent: Color,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    V2LuxurySurface(
        modifier.width(94.dp).heightIn(min = 90.dp)
            .semantics { contentDescription = "${side.title}، ${side.subtitle}" }
            .clickable(role = Role.Button, onClickLabel = side.title, onClick = onClick),
        radius = 19.dp,
        accent = accent,
        borderAlpha = .66f,
    ) {
        Column(
            Modifier.fillMaxWidth().padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            V2LuxuryGlyph(side.icon, accent, 36.dp)
            Spacer(Modifier.height(2.dp))
            KText(side.title, size = 10.sp, weight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            KText(side.subtitle, size = 9.sp, color = V2Soft, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun V2LuxuryOrb(spec: KnouxServiceSpec, actionLabel: String, onClick: () -> Unit, modifier: Modifier) {
    val theme = LocalProTheme.current
    Box(
        modifier.size(160.dp).semantics { contentDescription = "$actionLabel ${spec.title}" }
            .clickable(role = Role.Button, onClickLabel = actionLabel, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val r = this.size.minDimension * .47f
            drawCircle(
                Brush.radialGradient(listOf(Color.White.copy(alpha = .10f), spec.accent.copy(alpha = .27f), Color.Transparent), center = center, radius = r * 1.12f),
                radius = r * 1.12f,
            )
            drawCircle(theme.ridgeBase.copy(alpha = .92f), radius = r * .86f)
            for (index in 0 until 3) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(V2GoldText, spec.accent2, spec.accent, Color.White, spec.accent2, V2GoldText),
                        center = center,
                    ),
                    radius = r * (.98f - index * .06f),
                    style = Stroke((2.6f - index * .65f).dp.toPx(), cap = StrokeCap.Round),
                )
            }
            drawArc(
                Brush.sweepGradient(listOf(Color.Transparent, Color.White.copy(alpha = .95f), Color.Transparent), center),
                startAngle = 224f,
                sweepAngle = 79f,
                useCenter = false,
                style = Stroke(3.2.dp.toPx(), cap = StrokeCap.Round),
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2f, r * 2f),
            )
            drawArc(
                Brush.sweepGradient(listOf(Color.Transparent, spec.accent, Color.White, spec.accent2, Color.Transparent), center),
                startAngle = 24f,
                sweepAngle = 98f,
                useCenter = false,
                style = Stroke(1.4.dp.toPx(), cap = StrokeCap.Round),
                topLeft = Offset(center.x - r * 1.02f, center.y - r * 1.02f),
                size = Size(r * 2.04f, r * 2.04f),
            )
            for (index in 0 until 24) {
                val angle = index * Math.PI * 2.0 / 24.0
                val inner = r * .83f
                val outer = r * (if (index % 6 == 0) .89f else .86f)
                drawLine(
                    spec.accent.copy(alpha = if (index % 6 == 0) .54f else .21f),
                    start = Offset(center.x + (inner * cos(angle)).toFloat(), center.y + (inner * sin(angle)).toFloat()),
                    end = Offset(center.x + (outer * cos(angle)).toFloat(), center.y + (outer * sin(angle)).toFloat()),
                    strokeWidth = (if (index % 6 == 0) 1.1f else .65f).dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
        Column(
            Modifier.fillMaxSize(.72f).clip(CircleShape)
                .background(Brush.radialGradient(listOf(theme.orbCore, theme.orbMid, theme.ridgeBase)))
                .padding(top = 6.dp, bottom = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // The orb jewel is a night-lit island in every world.
            CompositionLocalProvider(LocalKnouxOnDark provides true) {
            if (spec.key == "deepscan" && spec.artwork == R.drawable.knoux_svc_deepscan) {
                // The home orb jewel is the official company seal, breathing
                // inside its night-lit island in every world.
                com.knoux.ai.ui.components.BrandSeal(size = 40.dp, haloColor = spec.accent)
            } else {
                V2RasterTile(SectionArt.emblem(spec.key), size = 33.dp, accent = spec.accent)
            }
            Spacer(Modifier.height(2.dp))
            KText(spec.title, size = 12.5.sp, weight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            KText(spec.english, size = 9.sp, color = V2Soft, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(3.dp))
            Box(
                Modifier.clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(theme.bronzeEdge, theme.bronzeMid, theme.bronzeEdge)))
                    .border(1.dp, V2GoldText.copy(alpha = .82f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 2.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KText(actionLabel, size = 9.5.sp, weight = FontWeight.Bold, color = V2GoldText, maxLines = 1, lineHeight = 12.sp)
                    Spacer(Modifier.width(3.dp))
                    Icon(Icons.Rounded.ChevronLeft, null, tint = V2GoldText, modifier = Modifier.size(9.dp))
                }
            }
            }
        }
    }
}

/** Section headers are small, legible and separated from the service tiles. */
@Composable
internal fun V2LuxurySectionTitle(title: String, trailing: String, accent: Color, modifier: Modifier = Modifier) {
    val day = LocalDayMode.current
    Row(modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.width(3.dp).height(17.dp).clip(RoundedCornerShape(50))
                .background(Brush.verticalGradient(listOf(if (day) dayAccent(accent) else accent, if (day) DayViolet else V2Violet))),
        )
        Spacer(Modifier.width(7.dp))
        KText(title, size = 15.sp, weight = FontWeight.Bold, color = Color.White, maxLines = 1)
        Spacer(Modifier.weight(1f))
        KText(trailing, size = 9.5.sp, color = V2Soft, maxLines = 1)
    }
}

@Composable
internal fun V2LuxuryServiceGrid(items: List<ServiceSubItem>, onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(LUXURY_SERVICE_COLUMNS).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                row.forEach { item -> V2LuxuryServiceTile(item, Modifier.weight(1f)) { onNavigate(item.route) } }
                repeat(LUXURY_SERVICE_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
internal fun V2LuxuryServiceTile(item: ServiceSubItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val proActive = LocalProActive.current
    // Minimum height keeps the approved grid rhythm, but the tile can grow if
    // accessibility font scale makes the two-line title taller.
    Box(modifier.heightIn(min = 102.dp)) {
        V2LuxurySurface(
            Modifier.fillMaxWidth().semantics { contentDescription = item.title }
                .clickable(role = Role.Button, onClickLabel = item.title, onClick = onClick),
            radius = 19.dp,
            accent = item.accent,
            borderAlpha = .48f,
        ) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (item.art == null || item.art == 0) {
                    V2LuxuryGlyph(item.icon, item.accent, size = 52.dp)
                } else {
                    V2RasterTile(item.art, size = 52.dp, accent = item.accent)
                }
                Spacer(Modifier.height(2.dp))
                KText(
                    item.title,
                    size = 10.5.sp,
                    lineHeight = 13.sp,
                    weight = FontWeight.SemiBold,
                    color = Color.White,
                    align = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        if (proActive && item.proOnly) {
            ProBadge(Modifier.align(Alignment.TopEnd).offset(x = 1.dp, y = 1.dp))
        }
    }
}

/** Shared neon glyph retained for the older service-spec tiles. */
@Composable
internal fun V2NeonGlyph(icon: ImageVector, accent: Color, size: Dp) {
    V2LuxuryGlyph(icon, accent, size)
}

@Composable
private fun V2LuxuryGlyph(icon: ImageVector, accent: Color, size: Dp) {
    val day = LocalDayMode.current
    val tint = if (day) dayAccent(accent) else accent
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                Brush.radialGradient(listOf(tint.copy(alpha = if (day) .22f else .37f), tint.copy(alpha = .06f), Color.Transparent), center = center, radius = this.size.minDimension * .52f),
                radius = this.size.minDimension * .52f,
            )
            drawCircle(
                Brush.linearGradient(listOf(Color.White.copy(alpha = if (day) .55f else .14f), tint.copy(alpha = .16f), Color.Transparent)),
                radius = this.size.minDimension * .31f,
            )
            drawOval(
                Brush.horizontalGradient(listOf(Color.Transparent, tint.copy(alpha = .8f), (if (day) DayGold else V2GoldText).copy(alpha = .4f), Color.Transparent)),
                topLeft = Offset(this.size.width * .16f, this.size.height * .86f),
                size = Size(this.size.width * .68f, .8.dp.toPx()),
            )
        }
        Icon(icon, null, tint = tint, modifier = Modifier.size(size * .45f))
    }
}

/**
 * Layered metallic glass: cool core, tinted top facet, precise inner highlight
 * and an outer luminous seam. It is not a flat purple rectangle with a glow.
 */
@Composable
internal fun V2LuxurySurface(
    modifier: Modifier = Modifier,
    radius: Dp = 20.dp,
    accent: Color = V2Violet,
    borderAlpha: Float = .45f,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    val theme = LocalProTheme.current
    val day = LocalDayMode.current
    Box(
        modifier.clip(shape).drawBehind {
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(
                        if (day) DayCardTop else theme.surfaceTop,
                        if (day) DayCardTop else theme.surfaceMid,
                        if (day) DayCardBottom else theme.surfaceBase,
                    ),
                ),
                cornerRadius = CornerRadius(radius.toPx(), radius.toPx()),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf((if (day) dayAccent(accent) else accent).copy(alpha = if (day) .12f else .22f), Color.Transparent),
                    center = Offset(size.width * .10f, 0f),
                    radius = size.width * .78f,
                ),
                radius = size.width * .78f,
                center = Offset(size.width * .10f, 0f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf((if (day) DayCyan else V2Cyan).copy(alpha = if (day) .06f else .09f), Color.Transparent),
                    center = Offset(size.width * .96f, size.height * .72f),
                    radius = size.width * .68f,
                ),
                radius = size.width * .68f,
                center = Offset(size.width * .96f, size.height * .72f),
            )
            drawLine(
                Brush.horizontalGradient(listOf(accent.copy(alpha = .10f), (if (day) dayAccent(accent) else Color.White).copy(alpha = if (day) .55f else .74f), accent.copy(alpha = .36f), Color.Transparent)),
                start = Offset(radius.toPx(), .6.dp.toPx()),
                end = Offset(size.width - radius.toPx(), .6.dp.toPx()),
                strokeWidth = .65.dp.toPx(),
            )
            drawLine(
                Brush.horizontalGradient(listOf(Color.White.copy(alpha = .12f), Color.Transparent)),
                start = Offset(.65.dp.toPx(), radius.toPx()),
                end = Offset(.65.dp.toPx(), size.height - radius.toPx()),
                strokeWidth = .65.dp.toPx(),
            )
            drawLine(
                Brush.horizontalGradient(listOf(Color.Transparent, (if (day) dayAccent(accent) else accent).copy(alpha = if (day) .40f else .58f), (if (day) DayGold else V2GoldText).copy(alpha = .13f), Color.Transparent)),
                start = Offset(size.width * .10f, size.height - .7.dp.toPx()),
                end = Offset(size.width * .90f, size.height - .7.dp.toPx()),
                strokeWidth = .75.dp.toPx(),
            )
        }.border(
            .8.dp,
            Brush.linearGradient(listOf((if (day) dayAccent(accent) else accent).copy(alpha = borderAlpha), Color.White.copy(alpha = if (day) .55f else .12f), if (day) DayCardSeam else theme.surfaceSeam, accent.copy(alpha = borderAlpha * .75f))),
            shape,
        ),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.fillMaxSize().padding(1.dp), contentAlignment = Alignment.Center) { content() }
    }
}

@Composable
internal fun V2LuxuryProBanner(
    onClick: () -> Unit,
    title: String = "KNOUX AI Pro",
    subtitle: String = "مزايا أكثر تقدمًا · تنفيذ يظل بموافقتك",
) {
    V2LuxurySurface(
        Modifier.fillMaxWidth().height(68.dp).semantics { contentDescription = title }
            .clickable(role = Role.Button, onClickLabel = title, onClick = onClick),
        radius = 20.dp,
        accent = V2GoldText,
        borderAlpha = .64f,
    ) {
        val day = LocalDayMode.current
        Canvas(Modifier.fillMaxSize()) {
            val aurora = Path().apply {
                moveTo(-10f, size.height * .68f)
                cubicTo(size.width * .16f, size.height * .10f, size.width * .42f, size.height * .90f, size.width * .69f, size.height * .44f)
                cubicTo(size.width * .88f, size.height * .14f, size.width, size.height * .45f, size.width + 10f, size.height * .20f)
            }
            drawPath(aurora, Brush.horizontalGradient(listOf(Color.Transparent, (if (day) DayPink else V2Pink).copy(alpha = .50f), (if (day) DayCyan else V2Cyan).copy(alpha = .36f), Color.Transparent)), style = Stroke(1.2.dp.toPx(), cap = StrokeCap.Round))
        }
        Row(Modifier.fillMaxSize().padding(horizontal = 11.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CrownGold(size = 29.dp, color = if (LocalDayMode.current) DayGold else V2GoldText)
                KText("PRO", size = 9.sp, weight = FontWeight.Black, color = V2GoldText, letterSpacing = .7.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                if (title == "KNOUX AI Pro") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KText("KNOUX AI", size = 16.sp, weight = FontWeight.Black, color = Color.White)
                        Spacer(Modifier.width(4.dp))
                        KText("Pro", size = 16.sp, weight = FontWeight.Black, color = V2GoldText)
                    }
                } else {
                    KText(title, size = 13.5.sp, weight = FontWeight.Black, color = V2GoldText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.height(1.dp))
                KText(subtitle, size = 9.5.sp, color = V2Soft, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier.size(32.dp).clip(CircleShape).background((if (LocalDayMode.current) DayGold else V2GoldText).copy(alpha = .12f))
                    .border(1.dp, (if (LocalDayMode.current) DayGold else V2GoldText).copy(alpha = .70f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.ChevronLeft, null, tint = if (LocalDayMode.current) DayGold else V2GoldText, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
internal fun V2LuxuryStatStrip(stats: List<SectionStat>, modifier: Modifier = Modifier) {
    val shown = stats.take(4)
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        shown.forEach { stat ->
            V2LuxurySurface(
                Modifier.weight(1f).height(66.dp),
                radius = 17.dp,
                accent = stat.accent,
                borderAlpha = .38f,
            ) {
                Column(
                    Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(stat.icon, null, tint = if (LocalDayMode.current) dayAccent(stat.accent) else stat.accent, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        KText(stat.value, size = 11.5.sp, weight = FontWeight.Black, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(Modifier.height(1.dp))
                    KText(
                        stat.label,
                        size = 9.sp,
                        lineHeight = 11.sp,
                        color = V2Soft,
                        align = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        repeat(4 - shown.size) { Spacer(Modifier.weight(1f)) }
    }
}

@Composable
internal fun V2LuxuryStat(icon: ImageVector, value: String, label: String, accent: Color, modifier: Modifier = Modifier) {
    Row(modifier.padding(horizontal = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = if (LocalDayMode.current) dayAccent(accent) else accent, modifier = Modifier.size(19.dp))
        Spacer(Modifier.width(6.dp))
        Column(Modifier.weight(1f)) {
            KText(value, size = 11.5.sp, weight = FontWeight.Black, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            KText(label, size = 9.sp, color = V2Soft, maxLines = 2, lineHeight = 11.sp, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
internal fun V2LuxuryStatDivider() {
    Box(Modifier.width(1.dp).height(25.dp).background(
        Brush.verticalGradient(listOf(Color.Transparent, (if (LocalDayMode.current) DayBorderSoft else V2Border).copy(alpha = if (LocalDayMode.current) .60f else .34f), Color.Transparent)),
    ))
}

/** Kept local so visual polish cannot invent a target layout for tiny phones. */
internal fun luxuryGridRows(itemCount: Int): List<Int> {
    require(itemCount >= 0)
    return (0 until itemCount).chunked(LUXURY_SERVICE_COLUMNS).map { it.size }
}
