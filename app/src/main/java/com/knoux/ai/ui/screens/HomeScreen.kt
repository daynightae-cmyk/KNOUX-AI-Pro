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
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.knoux.ai.ui.components.SettingsIconButton
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
                Modifier.fillMaxWidth().height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HomeMenuButton(onNavigate)
                Spacer(Modifier.width(8.dp))
                BrandLockup(symbolSize = 26.dp, wordmarkHeight = 14.dp)
                Spacer(Modifier.weight(1f))
                ProBadge()
                Spacer(Modifier.width(6.dp))
                SettingsIconButton()
            }
        }

        item { NeonHero(com.knoux.ai.ui.components.HeroKind.Orb, height = 160.dp) }

        item {
            ScreenTitle("AI Smart ", accent = "Clean")
            Spacer(Modifier.height(2.dp))
            ScreenSubtitle("The All-in-One AI Cleaner, Optimizer & Device Care")
        }

        item {
            Spacer(Modifier.height(10.dp))
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
            Spacer(Modifier.height(14.dp))
            GradientActionButton(
                text = "Start Deep Scan",
                onClick = { onNavigate("deepscan") },
                icon = Icons.Rounded.Search,
                trailingChevron = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 11.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(
                        progress = 0.92f,
                        centerText = "92%",
                        caption = "Device\nScore",
                        arcColors = listOf(KnouxCyan, KnouxMagenta),
                        size = 60.dp,
                        stroke = 6.dp,
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            HomeStat(Icons.Rounded.Layers, "12.6 GB", "Space saved", KnouxCyan)
                            HomeStat(Icons.Rounded.Security, "7", "Privacy fixes", KnouxViolet)
                            HomeStat(Icons.Rounded.Bolt, "+18%", "Battery gain", KnouxMagenta)
                        }
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Design preview · sample values, not measured",
                            style = TextStyle(fontSize = 8.sp, color = KnouxMutedText),
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        items(Fixtures.homeServices.chunked(2)) { pair ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun routeFor(title: String): String = when (title) {
    "Clean" -> "smartclean"
    "Duplicate Files" -> "duplicates"
    "Storage" -> "storage"
    "Privacy" -> "privacy"
    "Battery" -> "battery"
    "AI Optimization" -> "ai"
    else -> "home"
}

@Composable
private fun HomeMenuButton(onNavigate: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        IconCircle(Icons.Rounded.Menu, "Menu", { open = true })
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            val items = listOf(
                "Smart Clean" to "smartclean",
                "Deep Scan" to "deepscan",
                "Duplicate Review" to "duplicates",
                "Reports" to "reports",
                "Tools" to "tools",
            )
            items.forEach { (label, route) ->
                DropdownMenuItem(
                    text = { Text(label, color = KnouxText) },
                    onClick = { open = false; onNavigate(route) },
                )
            }
        }
    }
}

@Composable
private fun HomeStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    tint: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(15.dp))
        Text(value, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = KnouxText))
        Text(label, style = TextStyle(fontSize = 8.sp, color = KnouxSoftText))
    }
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
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            NeonIconTile(icon, size = 32.dp, iconSize = 16.dp, tint = accent)
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = KnouxText)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    description,
                    style = TextStyle(fontSize = 9.sp, lineHeight = 12.sp, color = KnouxSoftText),
                    maxLines = 2,
                )
            }
            Icon(
                Icons.Rounded.ChevronRight, null,
                tint = KnouxMutedText, modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.height(7.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    metric,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = accent)
                )
                if (metricCaption.isNotBlank()) {
                    Text(
                        metricCaption,
                        style = TextStyle(fontSize = 9.sp, color = KnouxSoftText)
                    )
                }
            }
            if (action != null) {
                MiniPill(action, onClick)
            }
        }
    }
}
