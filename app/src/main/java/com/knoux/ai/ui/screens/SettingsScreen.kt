package com.knoux.ai.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MotionPhotosAuto
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.components.KnouxTopBar
import com.knoux.ai.ui.components.PremiumGlassCard
import com.knoux.ai.ui.components.ReferenceSectionLabel
import com.knoux.ai.ui.components.SettingsIconButton
import com.knoux.ai.ui.components.TopLeading
import com.knoux.ai.ui.theme.ProWarning
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProTextPrimary
import com.knoux.ai.ui.theme.ProBlue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Settings — a real section inside the product, not a shortcut to Android.
 *
 * Every toggle writes to the app's own preferences, the storage numbers are
 * measured from this app's own folders, and the OS links use documented
 * intents. Nothing here claims a capability the platform does not give us.
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onShowWelcome: () -> Unit,
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("knoux_prefs", Context.MODE_PRIVATE) }

    var splashSound by remember { mutableStateOf(prefs.getBoolean("sound_enabled", true)) }
    var haptics by remember { mutableStateOf(prefs.getBoolean("haptics_enabled", true)) }
    var motion by remember { mutableStateOf(prefs.getBoolean("motion_enabled", true)) }
    var previewLabels by remember { mutableStateOf(prefs.getBoolean("preview_labels", true)) }
    var cacheBytes by remember { mutableStateOf(0L) }
    var dataBytes by remember { mutableStateOf(0L) }
    var packageInfo by remember { mutableStateOf(context.packageManager.getPackageInfo(context.packageName, 0)) }

    LaunchedEffect(Unit) {
        val cache = withContext(Dispatchers.IO) { directorySize(context.cacheDir) }
        val data = withContext(Dispatchers.IO) { directorySize(context.filesDir) }
        cacheBytes = cache
        dataBytes = data
    }

    Column(Modifier.fillMaxWidth()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Settings",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            color = ProTextPrimary,
                        ),
                    )
                    Text(
                        "Tune the app, check what it measures, and open the system controls Android owns.",
                        style = TextStyle(fontSize = 10.5.sp, lineHeight = 14.sp, color = ProTextSecondary),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            item { ReferenceSectionLabel("Experience", "applies instantly") }
            item {
                PremiumGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = 10.dp,
                    radius = 18.dp,
                    accent = ProBlue,
                ) {
                    Column {
                        SettingsToggleRow(
                            icon = Icons.Rounded.GraphicEq,
                            title = "Splash sound",
                            caption = "One short click when the logo lockup finishes.",
                            checked = splashSound,
                            tint = ProBlue,
                            onChange = {
                                splashSound = it
                                prefs.edit().putBoolean("sound_enabled", it).apply()
                            },
                        )
                        SettingsDivider()
                        SettingsToggleRow(
                            icon = Icons.Rounded.Vibration,
                            title = "Haptic feedback",
                            caption = "Short vibration on primary actions.",
                            checked = haptics,
                            tint = ProArcticCyan,
                            onChange = {
                                haptics = it
                                prefs.edit().putBoolean("haptics_enabled", it).apply()
                            },
                        )
                        SettingsDivider()
                        SettingsToggleRow(
                            icon = Icons.Rounded.MotionPhotosAuto,
                            title = "Motion & animation",
                            caption = "Orbit, float and transition animations.",
                            checked = motion,
                            tint = ProArcticCyan,
                            onChange = {
                                motion = it
                                prefs.edit().putBoolean("motion_enabled", it).apply()
                            },
                        )
                        SettingsDivider()
                        SettingsToggleRow(
                            icon = Icons.Rounded.AutoAwesome,
                            title = "Preview labels",
                            caption = "Keep the PREVIEW badge on every estimated value.",
                            checked = previewLabels,
                            tint = ProWarning,
                            onChange = {
                                previewLabels = it
                                prefs.edit().putBoolean("preview_labels", it).apply()
                            },
                        )
                    }
                }
            }

            item { ReferenceSectionLabel("Storage on this phone") }
            item {
                PremiumGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = 12.dp,
                    radius = 18.dp,
                    accent = ProArcticCyan,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        SettingsInfoRow(
                            icon = Icons.Rounded.CleaningServices,
                            title = "App cache",
                            value = formatBytes(cacheBytes),
                            tint = ProArcticCyan,
                        )
                        SettingsInfoRow(
                            icon = Icons.Rounded.Storage,
                            title = "App files",
                            value = formatBytes(dataBytes),
                            tint = ProBlue,
                        )
                        SettingsDivider()
                        SettingsActionRow(
                            icon = Icons.Rounded.CleaningServices,
                            title = "Clear app cache",
                            caption = "Removes only this app's temporary cache — nothing else.",
                            tint = ProArcticCyan,
                            onClick = {
                                runCatching { context.cacheDir.listFiles()?.forEach { it.deleteRecursively() } }
                            },
                        )
                        SettingsActionRow(
                            icon = Icons.Rounded.PhoneAndroid,
                            title = "System storage settings",
                            caption = "Opens Android's own storage manager.",
                            tint = ProArcticCyan,
                            onClick = {
                                runCatching {
                                    context.startActivity(
                                        Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                }
                            },
                        )
                    }
                }
            }

            item { ReferenceSectionLabel("Privacy & safety") }
            item {
                PremiumGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = 12.dp,
                    radius = 18.dp,
                    accent = ProArcticCyan,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        SettingsInfoRow(
                            icon = Icons.Rounded.CloudOff,
                            title = "Data stays on device",
                            value = "No account, no upload",
                            tint = ProArcticCyan,
                        )
                        SettingsDivider()
                        SettingsActionRow(
                            icon = Icons.Rounded.Security,
                            title = "App permissions",
                            caption = "Camera, storage and notification access granted to KNOUX AI.",
                            tint = ProArcticCyan,
                            onClick = {
                                runCatching {
                                    context.startActivity(
                                        Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                }
                            },
                        )
                        SettingsActionRow(
                            icon = Icons.Rounded.Bolt,
                            title = "Battery saver",
                            caption = "Hands off to Android's battery settings.",
                            tint = ProBlue,
                            onClick = {
                                runCatching {
                                    context.startActivity(
                                        Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                }
                            },
                        )
                    }
                }
            }

            item { ReferenceSectionLabel("About") }
            item {
                PremiumGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = 12.dp,
                    radius = 18.dp,
                    accent = ProBlue,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        SettingsInfoRow(
                            icon = Icons.Rounded.Info,
                            title = "Version",
                            value = "${packageInfo.versionName ?: "1.0"} · ${packageInfo.versionCode}",
                            tint = ProBlue,
                        )
                        SettingsInfoRow(
                            icon = Icons.Rounded.AutoAwesome,
                            title = "Design system",
                            value = "20 reference screens",
                            tint = ProArcticCyan,
                        )
                        SettingsDivider()
                        SettingsActionRow(
                            icon = Icons.Rounded.AutoAwesome,
                            title = "Show the welcome screen again",
                            caption = "Replays the post-splash entry screen.",
                            tint = ProArcticCyan,
                            onClick = onShowWelcome,
                        )
                        SettingsActionRow(
                            icon = Icons.Rounded.Info,
                            title = "App info",
                            caption = "Version, storage and uninstall in Android settings.",
                            tint = ProArcticCyan,
                            onClick = {
                                runCatching {
                                    context.startActivity(
                                        Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.parse("package:${context.packageName}"),
                                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                }
                            },
                        )
                    }
                }
            }

            item {
                Text(
                    "KNOUX AI · all cleanup, optimization and report values are design previews until the measurement engines are connected.",
                    style = TextStyle(fontSize = 8.5.sp, lineHeight = 12.sp, color = ProTextSecondary),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    caption: String,
    checked: Boolean,
    tint: Color,
    onChange: (Boolean) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onChange(!checked) }
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsIconBubble(icon, tint)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ProTextPrimary),
            )
            Text(
                caption,
                style = TextStyle(fontSize = 9.sp, lineHeight = 12.sp, color = ProTextSecondary),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = tint.copy(alpha = 0.75f),
                uncheckedThumbColor = ProTextSecondary,
                uncheckedTrackColor = Color.White.copy(alpha = 0.10f),
                uncheckedBorderColor = Color.White.copy(alpha = 0.18f),
            ),
        )
    }
}

@Composable
private fun SettingsInfoRow(
    icon: ImageVector,
    title: String,
    value: String,
    tint: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SettingsIconBubble(icon, tint)
        Spacer(Modifier.width(10.dp))
        Text(
            title,
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.5.sp, color = ProTextPrimary),
            modifier = Modifier.weight(1f),
        )
        Text(
            value,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp, color = tint),
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    caption: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsIconBubble(icon, tint)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = ProTextPrimary),
            )
            Text(
                caption,
                style = TextStyle(fontSize = 9.sp, lineHeight = 12.sp, color = ProTextSecondary),
            )
        }
        Icon(
            Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = ProTextSecondary,
            modifier = Modifier.size(15.dp),
        )
    }
}

@Composable
private fun SettingsIconBubble(icon: ImageVector, tint: Color) {
    Box(
        Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(tint.copy(alpha = 0.22f), tint.copy(alpha = 0.08f))
                )
            )
            .border(1.dp, tint.copy(alpha = 0.35f), RoundedCornerShape(11.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.09f)),
    )
}

private fun directorySize(dir: java.io.File): Long {
    if (!dir.exists()) return 0L
    var total = 0L
    dir.walkTopDown().forEach { file ->
        if (file.isFile) total += file.length()
    }
    return total
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1024L * 1024L * 1024L -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
    bytes >= 1024L * 1024L -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    bytes >= 1024L -> String.format("%.0f KB", bytes / 1024.0)
    else -> "$bytes B"
}
