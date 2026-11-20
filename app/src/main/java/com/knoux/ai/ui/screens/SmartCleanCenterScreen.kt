package com.knoux.ai.ui.screens

import androidx.annotation.DrawableRes
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.components.GlowPrimaryButton
import com.knoux.ai.ui.components.GlowRing
import com.knoux.ai.ui.components.KnouxTopBar
import com.knoux.ai.ui.components.PremiumGhostButton
import com.knoux.ai.ui.components.PremiumGlassCard
import com.knoux.ai.ui.components.PremiumHeroImage
import com.knoux.ai.ui.components.ProBadge
import com.knoux.ai.ui.components.RasterGlowIcon
import com.knoux.ai.ui.components.ScanCompleteBadge
import com.knoux.ai.ui.components.ScreenTitle
import com.knoux.ai.ui.components.SelectCheckbox
import com.knoux.ai.ui.components.SettingsIconButton
import com.knoux.ai.ui.components.TinyPreviewLabel
import com.knoux.ai.ui.components.TopLeading
import com.knoux.ai.ui.theme.ProWarning
import com.knoux.ai.ui.theme.ProBlue
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProDeepInk
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProTextPrimary
import com.knoux.ai.ui.theme.ProBlue
import java.util.Locale

/* ------------------------------------------------------------------ *
 * 11 — Smart Clean Center
 *
 * Visual reference: design/refs20_complete/11_Smart_Clean_Center.png
 *
 * The cleanup itself stays preview-only in this phase. Every number is
 * derived from the preview fixture list, is visibly labelled PREVIEW, and
 * no file is touched. Selecting rows updates the selected total live.
 * ------------------------------------------------------------------ */

private data class CleanItem(
    val title: String,
    val subtitle: String,
    val sizeLabel: String,
    val sizeMb: Double,
    val itemCount: String,
    @DrawableRes val icon: Int,
    val accent: Color,
)

private val cleanItems = listOf(
    CleanItem(
        "Cache Files",
        "App cache and temporary data",
        "1.27 GB",
        1300.0,
        "2,913 items",
        R.drawable.knoux_clean_icon_cache,
        ProArcticCyan,
    ),
    CleanItem(
        "Temporary Files",
        "System and app temporary files",
        "982 MB",
        982.0,
        "1,207 items",
        R.drawable.knoux_clean_icon_temp,
        ProBlue,
    ),
    CleanItem(
        "APK Leftovers",
        "Unused installation packages",
        "426 MB",
        426.0,
        "88 items",
        R.drawable.knoux_clean_icon_apk,
        ProArcticCyan,
    ),
    CleanItem(
        "Empty Folders",
        "Folders with no useful files",
        "148 MB",
        148.0,
        "312 items",
        R.drawable.knoux_clean_icon_empty,
        ProBlue,
    ),
    CleanItem(
        "Recycle Items",
        "Files in recycle bin / trash",
        "70 MB",
        70.0,
        "822 items",
        R.drawable.knoux_clean_icon_recycle,
        ProArcticCyan,
    ),
)

private enum class CleanDialog { PREVIEW, CLEAN }

@Composable
fun SmartCleanCenterScreen(onBack: () -> Unit, onOpenDeepScan: () -> Unit) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf(setOf(0, 1, 2, 4)) }
    var dialog by remember { mutableStateOf<CleanDialog?>(null) }

    val totalMb = cleanItems.sumOf { it.sizeMb }
    val totalLabel = formatStorage(totalMb)
    val selectedMb = cleanItems.filterIndexed { index, _ -> selected.contains(index) }.sumOf { it.sizeMb }
    val selectedLabel = formatStorage(selectedMb)
    val selectedCount = selected.size

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            item {
                SmartCleanHeader(selectedTab = tab, onSelect = { tab = it })
            }
            item {
                ScanSummaryCard(
                    totalLabel = totalLabel,
                    onOpenDeepScan = onOpenDeepScan,
                )
            }

            when (tab) {
                0 -> {
                    item {
                        SelectHeader(
                            selectedCount = selectedCount,
                            selectedLabel = selectedLabel,
                            onToggleAll = {
                                selected = if (selected.size == cleanItems.size) {
                                    emptySet()
                                } else {
                                    cleanItems.indices.toSet()
                                }
                            },
                        )
                    }
                    items(cleanItems.size) { index ->
                        CleanItemRow(
                            item = cleanItems[index],
                            checked = selected.contains(index),
                            onToggle = {
                                selected = if (selected.contains(index)) {
                                    selected - index
                                } else {
                                    selected + index
                                }
                            },
                        )
                    }
                    item {
                        AiRecommendationCard(
                            selectedLabel = selectedLabel,
                            hasSelection = selectedCount > 0,
                        )
                    }
                    item {
                        StorageGainCard(totalLabel = selectedLabel)
                    }
                }

                1 -> {
                    item { SafeCleanInfoCard() }
                    item {
                        SelectHeader(
                            selectedCount = selectedCount,
                            selectedLabel = selectedLabel,
                            onToggleAll = {
                                selected = if (selected.size == cleanItems.size) {
                                    emptySet()
                                } else {
                                    cleanItems.indices.toSet()
                                }
                            },
                        )
                    }
                    items(cleanItems.size) { index ->
                        CleanItemRow(
                            item = cleanItems[index],
                            checked = selected.contains(index),
                            onToggle = {
                                selected = if (selected.contains(index)) {
                                    selected - index
                                } else {
                                    selected + index
                                }
                            },
                        )
                    }
                    item {
                        StorageGainCard(totalLabel = selectedLabel)
                    }
                }

                else -> {
                    item {
                        AiAnalysisCard(
                            totalLabel = totalLabel,
                            selectedLabel = selectedLabel,
                        )
                    }
                    item {
                        AiRecommendationCard(
                            selectedLabel = selectedLabel,
                            hasSelection = selectedCount > 0,
                        )
                    }
                    item {
                        StorageGainCard(totalLabel = selectedLabel)
                    }
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(1f)) {
                PremiumGhostButton(
                    text = "Preview Items",
                    onClick = { dialog = CleanDialog.PREVIEW },
                    icon = Icons.Rounded.Visibility,
                    compact = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Box(Modifier.weight(1.24f)) {
                GlowPrimaryButton(
                    text = "Clean Selected ($selectedLabel)",
                    onClick = { dialog = CleanDialog.CLEAN },
                    icon = Icons.Rounded.CleaningServices,
                    compact = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    when (dialog) {
        CleanDialog.PREVIEW -> CleanPreviewDialog(
            selected = cleanItems.filterIndexed { index, _ -> selected.contains(index) },
            selectedLabel = selectedLabel,
            onDismiss = { dialog = null },
        )

        CleanDialog.CLEAN -> CleanConfirmationDialog(
            selectedLabel = selectedLabel,
            selectedCount = selectedCount,
            onDismiss = { dialog = null },
        )

        null -> Unit
    }
}

@Composable
private fun SmartCleanHeader(selectedTab: Int, onSelect: (Int) -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(126.dp),
    ) {
        PremiumHeroImage(
            image = R.drawable.knoux_clean_hero,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 16.dp, y = (-6).dp)
                .size(width = 138.dp, height = 104.dp),
            contentDescription = "Holographic cleanup orb",
            glow = ProArcticCyan,
        )
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .padding(end = 108.dp),
                ) {
                    ScreenTitle(
                        "Smart Clean ",
                        accent = "Center",
                        accentColors = listOf(ProArcticCyan, ProArcticCyan),
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "AI-powered junk cleanup for a faster,\ncleaner, and more storage space.",
                        style = TextStyle(
                            fontSize = 10.6.sp,
                            lineHeight = 14.sp,
                            color = ProTextSecondary,
                        ),
                        maxLines = 2,
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            SmartCleanTabRow(selectedTab = selectedTab, onSelect = onSelect)
        }
    }
}

@Composable
private fun SmartCleanTabRow(selectedTab: Int, onSelect: (Int) -> Unit) {
    val tabs = listOf(
        Triple("Deep Scan", Icons.Rounded.Search, ProArcticCyan),
        Triple("Safe Clean", Icons.Rounded.Shield, ProBlue),
        Triple("AI Analysis", Icons.Rounded.AutoAwesome, ProArcticCyan),
    )
    Row(
        Modifier
            .fillMaxWidth()
            .height(30.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEachIndexed { index, (label, icon, accent) ->
            if (index > 0) {
                Box(
                    Modifier
                        .width(1.dp)
                        .height(14.dp)
                        .background(Color.White.copy(alpha = 0.14f)),
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .clickable { onSelect(index) }
                    .padding(vertical = 1.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        icon,
                        null,
                        tint = if (selectedTab == index) accent else accent.copy(alpha = 0.72f),
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        label,
                        style = TextStyle(
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.4.sp,
                            color = if (selectedTab == index) ProTextPrimary else ProTextSecondary,
                        ),
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.height(2.dp))
                Box(
                    Modifier
                        .width(if (selectedTab == index) 24.dp else 0.dp)
                        .height(1.5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (selectedTab == index) {
                                Brush.horizontalGradient(listOf(accent, ProArcticCyan))
                            } else {
                                Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                            }
                        ),
                )
            }
        }
    }
}

@Composable
private fun ScanSummaryCard(totalLabel: String, onOpenDeepScan: () -> Unit) {
    PremiumGlassCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 9.dp,
        radius = 18.dp,
        accent = ProArcticCyan,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlowRing(
                progress = 0.92f,
                centerText = totalLabel,
                caption = "Junk\nFound",
                size = 58.dp,
                stroke = 6.dp,
                colors = listOf(ProArcticCyan, ProBlue, ProBlue, ProArcticCyan),
                centerTextSize = 15,
                captionSize = 6,
            )
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ScanCompleteBadge(fontSize = 10.5.sp)
                    Spacer(Modifier.weight(1f))
                    TinyPreviewLabel()
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Rescan",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = ProArcticCyan,
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable(onClick = onOpenDeepScan)
                            .padding(horizontal = 3.dp, vertical = 2.dp),
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    "Analyzed 128,430 files in 14.2s",
                    style = TextStyle(fontSize = 9.sp, color = ProTextSecondary),
                    maxLines = 1,
                )
                Spacer(Modifier.height(5.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ScanStat(Icons.Rounded.Folder, "5,342", "Junk Items", Modifier.weight(1f))
                    ScanStatDivider()
                    ScanStat(Icons.Rounded.Storage, "23.6 GB", "Total Scanned", Modifier.weight(1f))
                    ScanStatDivider()
                    ScanStat(
                        Icons.Rounded.PhoneAndroid,
                        "92%",
                        "Device Score",
                        Modifier.weight(1f),
                        highlight = "+6%",
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanStat(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    highlight: String? = null,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = ProArcticCyan.copy(alpha = 0.9f), modifier = Modifier.size(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                value,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.8.sp,
                    color = ProTextPrimary,
                ),
                maxLines = 1,
            )
            if (highlight != null) {
                Spacer(Modifier.width(2.dp))
                Text(
                    highlight,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = Color(0xFF7CFFD8),
                    ),
                )
            }
        }
        Text(
            label,
            style = TextStyle(fontSize = 9.sp, color = ProTextSecondary),
            maxLines = 1,
        )
    }
}

@Composable
private fun ScanStatDivider() {
    Box(
        Modifier
            .width(1.dp)
            .height(24.dp)
            .background(Color.White.copy(alpha = 0.12f)),
    )
}

@Composable
private fun SelectHeader(
    selectedCount: Int,
    selectedLabel: String,
    onToggleAll: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Select Items to Clean",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp,
                color = ProTextPrimary,
            ),
            maxLines = 1,
        )
        Spacer(Modifier.weight(1f))
        Text(
            "$selectedCount of ${cleanItems.size} selected",
            style = TextStyle(fontSize = 9.sp, color = ProTextSecondary),
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable(onClick = onToggleAll)
                .padding(horizontal = 4.dp, vertical = 2.dp),
        )
        Text(" · ", style = TextStyle(fontSize = 9.sp, color = ProTextSecondary))
        Text(
            selectedLabel,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 9.8.sp,
                color = ProArcticCyan,
            ),
        )
    }
}

@Composable
private fun CleanItemRow(
    item: CleanItem,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    if (checked) {
                        listOf(item.accent.copy(alpha = 0.14f), ProDeepInk.copy(alpha = 0.96f))
                    } else {
                        listOf(Color.White.copy(alpha = 0.045f), ProDeepInk.copy(alpha = 0.96f))
                    }
                )
            )
            .border(
                1.dp,
                if (checked) item.accent.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.12f),
                shape,
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 9.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RasterGlowIcon(
            image = item.icon,
            size = 32.dp,
            contentDescription = item.title,
            glow = item.accent,
        )
        Spacer(Modifier.width(7.dp))
        Column(Modifier.weight(1f)) {
            Text(
                item.title,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.6.sp,
                    color = ProTextPrimary,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                item.subtitle,
                style = TextStyle(fontSize = 9.sp, color = ProTextSecondary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                item.sizeLabel,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = ProTextPrimary,
                ),
                maxLines = 1,
            )
            Text(
                item.itemCount,
                style = TextStyle(fontSize = 9.sp, color = ProTextSecondary),
                maxLines = 1,
            )
        }
        Spacer(Modifier.width(7.dp))
        SelectCheckbox(
            checked = checked,
            onToggle = onToggle,
            accent = item.accent,
            size = 19.dp,
        )
    }
}

@Composable
private fun AiRecommendationCard(selectedLabel: String, hasSelection: Boolean) {
    PremiumGlassCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 9.dp,
        radius = 18.dp,
        accent = ProArcticCyan,
    ) {
        Row(verticalAlignment = Alignment.Top) {
            RasterGlowIcon(
                image = R.drawable.knoux_clean_icon_ai,
                size = 36.dp,
                contentDescription = "AI recommendation",
                glow = ProArcticCyan,
            )
            Spacer(Modifier.width(7.dp))
            Column(Modifier.weight(1f)) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "AI Recommendation",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.8.sp,
                            color = ProTextPrimary,
                        ),
                        maxLines = 1,
                    )
                    Spacer(Modifier.weight(1f))
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(ProArcticCyan.copy(alpha = 0.10f))
                            .border(1.dp, ProArcticCyan.copy(alpha = 0.45f), RoundedCornerShape(50))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Rounded.AutoAwesome,
                            null,
                            tint = ProArcticCyan,
                            modifier = Modifier.size(8.dp),
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            "Recommended",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = ProArcticCyan,
                            ),
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                if (hasSelection) {
                    Text(
                        buildAnnotatedString {
                            append("Reviewing ")
                            withStyle(SpanStyle(color = ProArcticCyan, fontWeight = FontWeight.Bold)) {
                                append(selectedLabel)
                            }
                            append(" is safe. Nothing is deleted until you confirm the exact files. Estimated performance gain: up to ")
                            withStyle(SpanStyle(color = ProArcticCyan, fontWeight = FontWeight.Bold)) {
                                append("28%")
                            }
                            append(".")
                        },
                        style = TextStyle(
                            fontSize = 9.sp,
                            lineHeight = 12.2.sp,
                            color = ProTextSecondary,
                        ),
                    )
                } else {
                    Text(
                        "Select at least one category to preview a safe cleanup. No files are removed in this visual preview.",
                        style = TextStyle(
                            fontSize = 9.sp,
                            lineHeight = 12.sp,
                            color = ProTextSecondary,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun StorageGainCard(totalLabel: String) {
    PremiumGlassCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 9.dp,
        radius = 18.dp,
        accent = ProArcticCyan,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RasterGlowIcon(
                image = R.drawable.knoux_clean_icon_gain,
                size = 32.dp,
                contentDescription = "Estimated storage gain",
                glow = ProArcticCyan,
            )
            Spacer(Modifier.width(7.dp))
            Column(Modifier.width(88.dp)) {
                Text(
                    "Estimated Storage Gain",
                    style = TextStyle(fontSize = 9.sp, lineHeight = 9.4.sp, color = ProTextSecondary),
                    maxLines = 2,
                )
                Text(
                    totalLabel,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        brush = Brush.horizontalGradient(listOf(ProArcticCyan, ProArcticCyan)),
                    ),
                    maxLines = 1,
                )
            }
            Box(
                Modifier
                    .width(1.dp)
                    .height(34.dp)
                    .background(Color.White.copy(alpha = 0.12f)),
            )
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                GainBenefit(Icons.Rounded.RocketLaunch, "Faster Performance")
                GainBenefit(Icons.Rounded.BatteryChargingFull, "Longer Battery Life")
                GainBenefit(Icons.Rounded.Layers, "More Usable Space")
            }
        }
    }
}

@Composable
private fun GainBenefit(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = ProArcticCyan, modifier = Modifier.size(10.dp))
        Spacer(Modifier.width(4.dp))
        Text(
            label,
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 9.sp, color = ProTextSecondary),
            maxLines = 1,
        )
    }
}

@Composable
private fun SafeCleanInfoCard() {
    PremiumGlassCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 20.dp,
        accent = ProArcticCyan,
    ) {
        Column {
            Text(
                "Safe Clean review",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ProTextPrimary,
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "A real cleanup will keep one valid copy, show every path, and ask for Android storage consent before deleting anything.",
                style = TextStyle(fontSize = 9.5.sp, lineHeight = 13.5.sp, color = ProTextSecondary),
            )
            Spacer(Modifier.height(9.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SafetyChip("Protected originals")
                SafetyChip("Preview only")
            }
        }
    }
}

@Composable
private fun SafetyChip(text: String) {
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0x141DE9B6))
            .border(1.dp, Color(0x441DE9B6), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Rounded.Check,
            null,
            tint = Color(0xFF7CFFD8),
            modifier = Modifier.size(10.dp),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text,
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 9.sp, color = Color(0xFF7CFFD8)),
            maxLines = 1,
        )
    }
}

@Composable
private fun AiAnalysisCard(totalLabel: String, selectedLabel: String) {
    PremiumGlassCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 20.dp,
        accent = ProArcticCyan,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "AI Analysis",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ProTextPrimary,
                    ),
                )
                Spacer(Modifier.weight(1f))
                TinyPreviewLabel()
            }
            Spacer(Modifier.height(5.dp))
            Text(
                "Deterministic preview signals from the current design fixtures. No inference model is bundled in this build.",
                style = TextStyle(fontSize = 9.sp, lineHeight = 13.sp, color = ProTextSecondary),
            )
            Spacer(Modifier.height(9.dp))
            AnalysisLine(Icons.Rounded.Storage, "Storage pressure", totalLabel, ProArcticCyan)
            Spacer(Modifier.height(6.dp))
            AnalysisLine(Icons.Rounded.Folder, "Selected for review", selectedLabel, ProArcticCyan)
            Spacer(Modifier.height(6.dp))
            AnalysisLine(Icons.Rounded.BatteryChargingFull, "Estimated performance upside", "up to 28%", ProBlue)
        }
    }
}

@Composable
private fun AnalysisLine(icon: ImageVector, label: String, value: String, accent: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(13.dp))
        }
        Spacer(Modifier.width(7.dp))
        Text(
            label,
            style = TextStyle(fontSize = 9.5.sp, color = ProTextSecondary),
            modifier = Modifier.weight(1f),
        )
        Text(
            value,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp, color = accent),
            maxLines = 1,
        )
    }
}

@Composable
private fun CleanPreviewDialog(
    selected: List<CleanItem>,
    selectedLabel: String,
    onDismiss: () -> Unit,
) {
    val previewText = if (selected.isEmpty()) {
        "Nothing is selected. Choose at least one category first."
    } else {
        selected.joinToString(separator = "\n") { "• ${it.title} — ${it.sizeLabel}" }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF140C36),
        title = {
            Text(
                "Cleanup preview",
                color = ProTextPrimary,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                Text(
                    previewText,
                    color = ProTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "Selected total: $selectedLabel\n\nThis is a visual preview. No files are removed.",
                    color = ProArcticCyan,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = ProArcticCyan)
            }
        },
    )
}

@Composable
private fun CleanConfirmationDialog(
    selectedLabel: String,
    selectedCount: Int,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF140C36),
        title = {
            Text(
                "Confirmation required",
                color = ProTextPrimary,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                if (selectedCount == 0) {
                    "Select at least one category before starting a cleanup."
                } else {
                    "You selected $selectedCount categories ($selectedLabel).\n\n" +
                        "This build is a visual preview: it will not delete files. A real cleanup will list the exact files, preserve one valid copy per duplicate group, and require Android storage consent plus an explicit confirmation."
                },
                color = ProTextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp,
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Understood", color = ProArcticCyan)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = ProTextSecondary)
            }
        },
    )
}

private fun formatStorage(mb: Double): String {
    val gb = mb / 1024.0
    return if (gb >= 1.0) {
        String.format(Locale.US, "%.2f GB", gb)
    } else {
        String.format(Locale.US, "%.0f MB", mb)
    }
}
