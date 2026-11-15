package com.knoux.ai.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.components.KnouxTopBar
import com.knoux.ai.ui.components.PreviewDisclosure
import com.knoux.ai.ui.components.PremiumGlassCard
import com.knoux.ai.ui.components.ProBadge
import com.knoux.ai.ui.components.RasterGlowIcon
import com.knoux.ai.ui.components.ReferenceSectionLabel
import com.knoux.ai.ui.components.rememberMotionEnabled
import com.knoux.ai.ui.components.ScreenSubtitle
import com.knoux.ai.ui.components.ScreenTitle
import com.knoux.ai.ui.components.SettingsIconButton
import com.knoux.ai.ui.components.TopLeading
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProDeepInk
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProTextPrimary
import com.knoux.ai.ui.theme.ProBlue

/**
 * One service inside a hub.
 *
 * [art] is decorative artwork cut from the matching reference screen; every
 * word, value and control stays native Compose.
 */
data class HubService(
    val title: String,
    val subtitle: String,
    val route: String,
    @DrawableRes val art: Int,
    val accent: Color,
    val value: String,
    val valueLabel: String,
)

/**
 * Clean / Tools hub.
 *
 * Every tool gets its own 3D artwork, its own preview value and its own
 * destination, so the hub reads like a designed product surface instead of a
 * generic list of links.
 */
@Composable
fun ServiceHubScreen(
    title: String,
    accent: String,
    subtitle: String,
    featured: HubService,
    services: List<HubService>,
    onOpen: (String) -> Unit,
    onBack: () -> Unit,
    onOpenSettings: (() -> Unit)? = null,
) {
    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                HubConstellation(
                    arts = listOf(featured.art) + services.take(3).map { it.art },
                )
            }
            item {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ScreenTitle(title, accent = accent)
                    ScreenSubtitle(subtitle, Modifier.padding(horizontal = 12.dp))
                    Spacer(Modifier.height(5.dp))
                    PreviewDisclosure()
                }
            }
            item {
                StaggeredReveal(index = 0) {
                    FeaturedHubCard(featured = featured, onClick = { onOpen(featured.route) })
                }
            }
            item {
                ReferenceSectionLabel("All services", "preview values")
            }
            items(services.size) { index ->
                StaggeredReveal(index = index + 1) {
                    HubServiceCard(
                        service = services[index],
                        onClick = { onOpen(services[index].route) },
                    )
                }
            }
        }
    }
}

/**
 * Staggered entrance: every card lifts in on its own beat so the hub feels
 * designed rather than dumped on screen.
 */
@Composable
private fun StaggeredReveal(index: Int, content: @Composable () -> Unit) {
    val motion = rememberMotionEnabled()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (motion) 420 else 0,
            delayMillis = if (motion) index * 70 else 0,
            easing = androidx.compose.animation.core.FastOutSlowInEasing,
        ),
        label = "reveal-$index",
    )
    Box(
        Modifier.graphicsLayer {
            alpha = progress
            translationY = (1f - progress) * 22f
        },
    ) {
        content()
    }
}

/**
 * Hero for the hub: the tool artwork floats on one orbit instead of a flat
 * banner, so every hub looks different from every other screen.
 */
@Composable
private fun HubConstellation(arts: List<Int>) {
    val transition = rememberInfiniteTransition(label = "hub-float")
    Box(
        Modifier
            .fillMaxWidth()
            .height(138.dp),
    ) {
        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ProArcticCyan.copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.56f),
                    radius = size.width * 0.52f,
                ),
                center = Offset(size.width * 0.5f, size.height * 0.56f),
                radius = size.width * 0.52f,
            )
            drawOval(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        ProArcticCyan.copy(alpha = 0.45f),
                        ProArcticCyan.copy(alpha = 0.45f),
                        Color.Transparent,
                    )
                ),
                topLeft = Offset(size.width * 0.16f, size.height * 0.70f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.68f, 2.dp.toPx()),
            )
        }
        val layout = listOf(
            Triple(-0.24f, -6.dp, 62.dp),
            Triple(0f, -26.dp, 82.dp),
            Triple(0.24f, -2.dp, 58.dp),
        )
        arts.take(3).forEachIndexed { index, art ->
            val (xFactor, yOffset, iconSize) = layout[index]
            val phase by transition.animateFloat(
                initialValue = 0f,
                targetValue = (index + 1) * 1.6f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4200 + index * 600, easing = androidx.compose.animation.core.LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "float-$index",
            )
            RasterGlowIcon(
                image = art,
                size = iconSize,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (iconSize.value * xFactor).dp, y = yOffset)
                    .graphicsLayer {
                        translationY = -4.dp.toPx() + (kotlin.math.sin(phase.toDouble()).toFloat() * 5f)
                        val s = 0.97f + 0.03f * kotlin.math.sin(phase.toDouble()).toFloat()
                        scaleX = s
                        scaleY = s
                    },
            )
        }
    }
}

@Composable
private fun FeaturedHubCard(featured: HubService, onClick: () -> Unit) {
    PremiumGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        padding = 12.dp,
        radius = 22.dp,
        accent = featured.accent,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = null,
                        tint = featured.accent,
                        modifier = Modifier.size(11.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "FEATURED",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            letterSpacing = 1.2.sp,
                            color = featured.accent,
                        ),
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    featured.title,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        lineHeight = 20.sp,
                        color = ProTextPrimary,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    featured.subtitle,
                    style = TextStyle(fontSize = 9.5.sp, lineHeight = 12.5.sp, color = ProTextSecondary),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(featured.accent.copy(alpha = 0.16f))
                            .border(1.dp, featured.accent.copy(alpha = 0.5f), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            featured.value,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = featured.accent,
                            ),
                        )
                    }
                    Spacer(Modifier.width(5.dp))
                    Text(
                        featured.valueLabel,
                        style = TextStyle(fontSize = 8.5.sp, color = ProTextSecondary),
                        maxLines = 1,
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Box(contentAlignment = Alignment.Center) {
                RasterGlowIcon(
                    image = featured.art,
                    size = 96.dp,
                    glow = featured.accent,
                )
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = ProTextPrimary.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun HubServiceCard(service: HubService, onClick: () -> Unit) {
    PremiumGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        padding = 10.dp,
        radius = 18.dp,
        accent = service.accent,
        borderAlpha = 0.38f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RasterGlowIcon(
                image = service.art,
                size = 54.dp,
                glow = service.accent,
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    service.title,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = ProTextPrimary,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    service.subtitle,
                    style = TextStyle(fontSize = 9.5.sp, lineHeight = 12.sp, color = ProTextSecondary),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        service.accent.copy(alpha = 0.28f),
                                        ProDeepInk.copy(alpha = 0.7f),
                                    )
                                )
                            )
                            .border(1.dp, service.accent.copy(alpha = 0.45f), RoundedCornerShape(50))
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                    ) {
                        Text(
                            service.value,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = ProTextPrimary,
                            ),
                        )
                    }
                    Spacer(Modifier.width(5.dp))
                    Text(
                        service.valueLabel,
                        style = TextStyle(fontSize = 8.sp, color = ProTextSecondary),
                        maxLines = 1,
                    )
                }
            }
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = ProTextSecondary,
                modifier = Modifier.size(15.dp),
            )
        }
    }
}
