package com.knoux.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.components.BrandLockup
import com.knoux.ai.ui.components.GlassPanel
import com.knoux.ai.ui.components.GradientActionButton
import com.knoux.ai.ui.components.IconCircle
import com.knoux.ai.ui.components.MiniPill
import com.knoux.ai.ui.components.NeonHero
import com.knoux.ai.ui.components.NeonIconTile
import com.knoux.ai.ui.components.ProBadge
import com.knoux.ai.ui.components.ProgressDonut
import com.knoux.ai.ui.components.ScreenSubtitle
import com.knoux.ai.ui.components.ScreenTitle
import com.knoux.ai.ui.components.TopLeading
import com.knoux.ai.ui.components.iconFor
import com.knoux.ai.ui.data.Fixtures
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxMutedText
import com.knoux.ai.ui.theme.KnouxPanelAlt
import com.knoux.ai.ui.theme.KnouxPanelDeep
import com.knoux.ai.ui.theme.KnouxSoftText
import com.knoux.ai.ui.theme.KnouxText
import com.knoux.ai.ui.theme.KnouxViolet

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp, end = 16.dp, top = 4.dp, bottom = 18.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Row(
                Modifier.fillMaxWidth().height(52.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconCircle(Icons.Rounded.Bolt, "Menu", { })
                Spacer(Modifier.width(8.dp))
                BrandLockup(symbolSize = 26.dp, wordmarkHeight = 14.dp)
                Spacer(Modifier.weight(1f))
                ProBadge()
                Spacer(Modifier.width(6.dp))
                IconCircle(Icons.Rounded.Settings, "Settings", { })
            }
        }

        item { NeonHero(com.knoux.ai.ui.components.HeroKind.Orb, height = 196.dp) }

        item {
            ScreenTitle("AI Smart ", accent = "Clean")
            Spacer(Modifier.height(2.dp))
            ScreenSubtitle("Intelligent Cleaning & Device Optimization")
        }

        item {
            Spacer(Modifier.height(14.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val benefitIcons = listOf(
                    Icons.Rounded.Bolt, Icons.Rounded.RocketLaunch, Icons.Rounded.Security, Icons.Rounded.Layers
                )
                Fixtures.benefits.forEachIndexed { index, label ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (index > 0) {
                            Box(
                                Modifier
                                    .width(1.dp)
                                    .height(20.dp)
                                    .background(Color.White.copy(alpha = 0.18f))
                            )
                            Spacer(Modifier.width(10.dp))
                        }
                        Icon(benefitIcons[index], null, tint = KnouxCyan, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(5.dp))
                        Text(
                            label,
                            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, color = KnouxText)
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(18.dp))
            GradientActionButton(
                text = "Start Deep Scan",
                onClick = { onNavigate("deepscan") },
                icon = Icons.Rounded.Search,
                trailingChevron = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            Spacer(Modifier.height(16.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 14.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(
                        progress = 0.93f,
                        centerText = "93%",
                        arcColors = listOf(KnouxCyan, KnouxMagenta),
                        size = 58.dp,
                        stroke = 6.dp,
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            "Optimization\nScore",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 11.sp, color = KnouxText)
                        )
                        Text(
                            "↑ Excellent",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF3BE8A0))
                        )
                    }
                    Box(
                        Modifier
                            .padding(horizontal = 10.dp)
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color.White.copy(alpha = 0.15f))
                    )
                    NeonIconTile(Icons.Rounded.Layers, size = 34.dp, iconSize = 17.dp)
                    Spacer(Modifier.width(9.dp))
                    Column {
                        Text(
                            "12.6 GB",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = KnouxText)
                        )
                        Text(
                            "Storage Saved",
                            style = TextStyle(fontSize = 10.sp, color = KnouxSoftText)
                        )
                    }
                    Box(
                        Modifier
                            .padding(horizontal = 10.dp)
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color.White.copy(alpha = 0.15f))
                    )
                    ProgressDonut(
                        progress = 0.72f,
                        centerText = "72%",
                        arcColors = listOf(KnouxMagenta, KnouxCyan),
                        size = 58.dp,
                        stroke = 6.dp,
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            "Scan Progress",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 11.sp, color = KnouxText)
                        )
                        Text(
                            "Analyzing device...",
                            style = TextStyle(fontSize = 9.5.sp, color = KnouxSoftText)
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        items(Fixtures.homeServices.chunked(2)) { pair ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                pair.forEach { service ->
                    Box(Modifier.weight(1f)) {
                        ServiceCard(
                            title = service.title,
                            description = service.description,
                            metric = service.metric,
                            metricCaption = service.metricCaption,
                            action = service.action,
                            icon = iconFor(service.icon),
                            accent = service.accent,
                            onClick = { onNavigate(routeFor(service.title)) },
                        )
                    }
                }
                if (pair.size == 1) Box(Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

private fun routeFor(title: String): String = when (title) {
    "Temporary Files" -> "residue"
    "Duplicate Files" -> "duplicates"
    "Storage Analyzer" -> "storage"
    "App Residue" -> "residue"
    "Privacy Sweep" -> "privacy"
    "AI Optimization" -> "ai"
    else -> "home"
}

@Composable
private fun ServiceCard(
    title: String,
    description: String,
    metric: String,
    metricCaption: String,
    action: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(KnouxPanelAlt.copy(alpha = 0.92f), KnouxPanelDeep.copy(alpha = 0.92f))
                )
            )
            .border(1.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            NeonIconTile(icon, size = 36.dp, iconSize = 18.dp, tint = accent)
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KnouxText)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    description,
                    style = TextStyle(fontSize = 9.5.sp, lineHeight = 13.sp, color = KnouxSoftText),
                    maxLines = 3,
                )
            }
            Icon(
                Icons.Rounded.ChevronRight, null,
                tint = KnouxMutedText, modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.height(9.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    metric,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = accent)
                )
                if (metricCaption.isNotBlank()) {
                    Text(
                        metricCaption,
                        style = TextStyle(fontSize = 9.sp, color = KnouxSoftText)
                    )
                }
            }
            if (action != null) {
                MiniPill(action, { })
            }
        }
    }
}
