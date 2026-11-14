package com.knoux.ai.ui.screens

import com.knoux.ai.R
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.components.FeatureRow
import com.knoux.ai.ui.components.GlassPanel
import com.knoux.ai.ui.components.GradientActionButton
import com.knoux.ai.ui.components.GhostButton
import com.knoux.ai.ui.components.HeroKind
import com.knoux.ai.ui.components.InsightBanner
import com.knoux.ai.ui.components.ReferenceSectionLabel
import com.knoux.ai.ui.components.SignalTile
import com.knoux.ai.ui.components.SignalTileGrid
import com.knoux.ai.ui.components.MiniStatStrip
import com.knoux.ai.ui.components.NeonIconTile
import com.knoux.ai.ui.components.ProgressDonut
import com.knoux.ai.ui.components.SummaryLine
import com.knoux.ai.ui.data.Fixtures
import com.knoux.ai.ui.theme.KnouxAmber
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxSoftText
import com.knoux.ai.ui.theme.KnouxText
import com.knoux.ai.ui.theme.KnouxViolet

@Composable
fun BatterySaverScreen(onBack: () -> Unit, onOpenDetail: () -> Unit) {
    ToolScreenScaffold(
        hero = HeroKind.Battery,
        title = "Battery ",
        accent = "Saver",
        subtitle = "Reduce drain, control background activity, and extend battery life.",
        rightIcon = Icons.Rounded.BatteryChargingFull,
        onBack = onBack,
        heroImage = R.drawable.knoux_hero_battery,
        bottomActions = {
            Box(Modifier.weight(1f)) { GhostButton("View Details", onOpenDetail) }
            Box(Modifier.weight(1f)) { GradientActionButton("Save Battery", onOpenDetail, Modifier.fillMaxWidth()) }
        },
    ) {
        item {
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NeonIconTile(Icons.Rounded.Schedule, size = 42.dp, iconSize = 21.dp, tint = KnouxMagenta)
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "6h 42m",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 23.sp, color = KnouxText)
                        )
                        Text(
                            "estimated gain",
                            style = TextStyle(fontSize = 11.sp, color = KnouxSoftText)
                        )
                    }
                    Box(
                        Modifier
                            .width(1.dp)
                            .height(38.dp)
                            .background(Color.White.copy(alpha = 0.15f))
                    )
                    Spacer(Modifier.width(14.dp))
                    NeonIconTile(Icons.Rounded.Favorite, size = 42.dp, iconSize = 21.dp, tint = KnouxCyan)
                    Spacer(Modifier.width(11.dp))
                    Column {
                        Text(
                            "91%",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 23.sp, color = KnouxCyan)
                        )
                        Text(
                            "Battery health",
                            style = TextStyle(fontSize = 11.sp, color = KnouxSoftText)
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 12.dp) {
                MiniStatStrip(Fixtures.batteryMetrics)
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            InsightBanner(
                title = "Healthy Battery",
                message = "Your battery is in good condition. Minor optimization can extend life a little further.",
                icon = Icons.Rounded.CheckCircle,
                tint = KnouxCyan,
                badge = "GOOD",
            )
        }

        item {
            Spacer(Modifier.height(12.dp))
            ReferenceSectionLabel("Device health signals", "preview values")
            Spacer(Modifier.height(6.dp))
            SignalTileGrid(
                tiles = listOf(
                    SignalTile(Icons.Rounded.BatteryChargingFull, "6h 42m", "Estimated gain", KnouxMagenta),
                    SignalTile(Icons.Rounded.Bolt, "5", "Drain sources", KnouxMagenta),
                    SignalTile(Icons.Rounded.Event, "287", "Charging cycles", KnouxCyan),
                    SignalTile(Icons.Rounded.Thermostat, "37 °C", "Peak temperature", KnouxAmber),
                    SignalTile(Icons.Rounded.PhoneAndroid, "5h 18m", "Screen-on time", KnouxViolet),
                    SignalTile(Icons.Rounded.Memory, "18", "Background apps", KnouxViolet),
                ),
            )
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(
                        progress = 0.91f,
                        centerText = "91%",
                        caption = "Battery\nHealth",
                        arcColors = listOf(KnouxCyan, KnouxMagenta),
                        size = 92.dp,
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        SummaryLine(Icons.Rounded.WarningAmber, "Critical drain apps:", "3", KnouxAmber)
                        SummaryLine(Icons.Rounded.CheckCircle, "Safe actions:", "5 available", KnouxCyan)
                        SummaryLine(Icons.Rounded.Event, "Last analysis:", Fixtures.LAST_RUN, KnouxViolet)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "Optimization & Actions",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = KnouxText),
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(Fixtures.batteryActions.size) { index ->
            Spacer(Modifier.height(8.dp))
            FeatureRow(
                row = Fixtures.batteryActions[index],
                actionLabel = when (index) {
                    0 -> "Auto Optimize"; 1 -> "Restrict Sync"; 2 -> "Limit Wakeups"
                    3 -> "Use While Active"; else -> "Smart Charging"
                },
                onAction = onOpenDetail,
            )
        }
    }
}
