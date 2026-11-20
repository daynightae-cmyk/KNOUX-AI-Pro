package com.knoux.ai.ui.v2

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PermMedia
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.components.rememberMotionEnabled
import kotlin.math.roundToInt

/* ------------------------------------------------------------------ *
 * KNOUX AI Pro — shared visual service outputs
 *
 * The owner asked for service screens that end in a visual answer, not a
 * wall of text. These components take only measured DeviceFacts and draw
 * the same kind of dashboard the Home screen uses: a live gauge, real
 * thumbnails, real app icons, permission bars and scan history bars.
 * Nothing here invents a number.
 * ------------------------------------------------------------------ */

/**
 * A compact dashboard for any service section. It is used by the 135
 * section-tool pages and by the Smart Clean tool pages, so every route opens
 * with the same visual grammar instead of a list of labels.
 */
@Composable
internal fun ServiceVisualDeck(
    section: String,
    facts: DeviceFacts,
    accent: Color,
    accent2: Color,
    modifier: Modifier = Modifier,
    history: List<ScanHistoryEntry> = emptyList(),
) {
    val identity = remember(section, facts) { sectionIdentity(section, facts) }
    val live = facts.computedAt > 0L
    // Before the first measured read there is no data: never print a zero that
    // the device did not report. Values stay as an ellipsis and the strip shows
    // a live-read notice instead of an empty chart.
    val stats = remember(identity, live) {
        if (live) identity.stats else identity.stats.map { it.copy(value = "…") }
    }
    V2GlassCard(
        modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 22.dp,
        glow = accent,
        borderAlpha = .38f,
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ServiceGauge(
                    percent = identity.gaugePercent,
                    label = identity.gaugeLabel,
                    live = live,
                    accent = accent,
                    accent2 = accent2,
                    valueText = if (live && section == "duplicates" && !facts.media.permissionGranted) "—" else null,
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KText(
                            dashboardTitle(section),
                            size = 13.sp,
                            weight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.width(5.dp))
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (live) V2Green.withAlpha(.14f) else V2Amber.withAlpha(.14f))
                                .border(
                                    1.dp,
                                    if (live) V2Green.withAlpha(.34f) else V2Amber.withAlpha(.34f),
                                    RoundedCornerShape(50),
                                )
                                .padding(horizontal = 5.dp, vertical = 1.dp),
                        ) {
                            KText(
                                if (live) "LIVE" else "…",
                                size = 9.sp,
                                weight = FontWeight.Black,
                                color = if (live) V2Green else V2Amber,
                            )
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    KText(
                        dashboardCaption(section, facts),
                        size = 9.sp,
                        lineHeight = 10.6.sp,
                        color = V2Soft,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(Modifier.height(9.dp))
            SectionVisualStrip(
                section = section,
                facts = facts,
                accent = accent,
                accent2 = accent2,
                history = history,
            )
            Spacer(Modifier.height(10.dp))
            SectionStatGrid(stats, Modifier.fillMaxWidth())
        }
    }
}

/** Live neon gauge shared by the deck and the Deep Scan result tiles. */
@Composable
internal fun ServiceGauge(
    percent: Int,
    label: String,
    live: Boolean,
    accent: Color,
    accent2: Color,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    valueText: String? = null,
) {
    val motion = rememberMotionEnabled()
    val theme = LocalProTheme.current
    val target = if (live) percent.coerceIn(0, 100) / 100f else 0f
    val progress by animateFloatAsState(
        targetValue = target,
        animationSpec = if (motion) tween(700) else snap(),
        label = "serviceGauge",
    )
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = this.size.minDimension / 2f * .92f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(accent.copy(alpha = .26f), accent2.copy(alpha = .10f), Color.Transparent),
                    center = center,
                    radius = radius * 1.34f,
                ),
                radius = radius * 1.34f,
                center = center,
            )
            drawCircle(theme.ridgeBase.copy(alpha = .88f), radius = radius * .80f, center = center)
            drawCircle(
                color = Color.White.copy(alpha = .08f),
                radius = radius * .94f,
                center = center,
                style = Stroke(width = 7.dp.toPx()),
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(accent2, accent, V2GoldText, accent2), center),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(center.x - radius * .94f, center.y - radius * .94f),
                size = Size(radius * 1.88f, radius * 1.88f),
                style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round),
            )
            if (progress > .01f) {
                val angle = Math.toRadians((-90f + 360f * progress).toDouble())
                val pointRadius = radius * .94f
                drawCircle(
                    color = Color.White.copy(alpha = .95f),
                    radius = 2.4.dp.toPx(),
                    center = Offset(
                        center.x + (pointRadius * kotlin.math.cos(angle)).toFloat(),
                        center.y + (pointRadius * kotlin.math.sin(angle)).toFloat(),
                    ),
                )
            }
            for (tick in 0 until 28) {
                val angle = tick * (2.0 * Math.PI / 28.0)
                val inner = radius * if (tick % 7 == 0) .72f else .77f
                drawLine(
                    color = accent.copy(alpha = if (tick % 7 == 0) .42f else .18f),
                    start = Offset(
                        center.x + (inner * kotlin.math.cos(angle)).toFloat(),
                        center.y + (inner * kotlin.math.sin(angle)).toFloat(),
                    ),
                    end = Offset(
                        center.x + (radius * .82f * kotlin.math.cos(angle)).toFloat(),
                        center.y + (radius * .82f * kotlin.math.sin(angle)).toFloat(),
                    ),
                    strokeWidth = if (tick % 7 == 0) 1.dp.toPx() else .6.dp.toPx(),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // The gauge face stays night-lit: it is an instrument, not chrome.
            CompositionLocalProvider(LocalKnouxOnDark provides true) {
            KText(
                valueText ?: if (live) "$percent%" else "—",
                size = if (valueText != null) 15.sp else 20.sp,
                weight = FontWeight.Black,
                color = Color.White,
                maxLines = 1,
            )
            KText(
                label,
                size = 9.sp,
                lineHeight = 10.6.sp,
                color = V2Soft,
                align = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.width(size * .62f),
            )
            }
        }
    }
}

@Composable
private fun SectionStatGrid(stats: List<SectionStat>, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        stats.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { stat ->
                    SectionStatTile(stat, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SectionStatTile(stat: SectionStat, modifier: Modifier = Modifier) {
    val theme = LocalProTheme.current
    val day = LocalDayMode.current
    val tint = if (day) dayAccent(stat.accent) else stat.accent
    Row(
        modifier
            .height(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(tint.withAlpha(if (day) .12f else .16f), if (day) DayCardBottom else theme.deep2.withAlpha(.88f)),
                ),
            )
            .border(1.dp, tint.withAlpha(if (day) .42f else .30f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(tint.withAlpha(if (day) .14f else .16f))
                .border(1.dp, tint.withAlpha(if (day) .42f else .34f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(stat.icon, null, tint = tint, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(7.dp))
        Column(Modifier.weight(1f)) {
            KText(
                stat.value,
                size = 12.5.sp,
                weight = FontWeight.Black,
                color = stat.accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            KText(
                stat.label,
                size = 9.sp,
                color = V2Soft,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Turns the reading rows into a metric grid. The same values are shown, but
 * as a dashboard of tiles with a visual hierarchy instead of a paragraph.
 */
@Composable
internal fun ServiceRowTileGrid(rows: List<ToolRow>, modifier: Modifier = Modifier) {
    if (rows.isEmpty()) return
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        rows.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                pair.forEach { row -> ServiceRowTile(row, Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun ServiceRowTile(row: ToolRow, modifier: Modifier = Modifier) {
    val theme = LocalProTheme.current
    val day = LocalDayMode.current
    val tint = if (day) dayAccent(row.accent) else row.accent
    Column(
        modifier
            .height(86.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(tint.withAlpha(if (day) .12f else .15f), if (day) DayCardBottom else theme.glassBottom.withAlpha(.90f)),
                ),
            )
            .border(1.dp, tint.withAlpha(if (day) .42f else .30f), RoundedCornerShape(16.dp))
            .padding(horizontal = 9.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(25.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tint.withAlpha(if (day) .14f else .17f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(row.icon, null, tint = tint, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = .72f)),
            )
        }
        Spacer(Modifier.height(4.dp))
        KText(
            row.label,
            size = 9.sp,
            lineHeight = 10.6.sp,
            color = V2Soft,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        KText(
            row.value,
            size = 13.sp,
            lineHeight = 16.sp,
            weight = FontWeight.Black,
            color = row.accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/* ----------------------------- visual strips ----------------------------- */

@Composable
private fun SectionVisualStrip(
    section: String,
    facts: DeviceFacts,
    accent: Color,
    accent2: Color,
    history: List<ScanHistoryEntry>,
) {
    if (facts.computedAt <= 0L) {
        PermissionInlineStrip(
            icon = Icons.Rounded.Radar,
            title = "جارٍ قراءة مصادر الجهاز",
            subtitle = "تظهر المؤشرات والصور الحقيقية بعد اكتمال أول قراءة.",
            accent = accent,
        )
        return
    }
    when (section) {
        "duplicates" -> DuplicateThumbStrip(facts, accent)
        "storage", "smartclean", "residue" -> StorageVisualStrip(facts, accent)
        "privacy" -> PermissionVisualStrip(facts, accent)
        "appmanager" -> AppIconStrip(facts.installedApps, accent)
        "battery" -> BatteryVisualStrip(facts.battery, accent)
        "reports", "deepscan", "ai" -> HistoryVisualStrip(history, accent)
        else -> {
            if (facts.media.permissionGranted && facts.media.largestFiles.isNotEmpty()) {
                MediaThumbStrip(facts.media.largestFiles, accent)
            } else {
                AppIconStrip(facts.installedApps, accent)
            }
        }
    }
}

@Composable
private fun DuplicateThumbStrip(facts: DeviceFacts, accent: Color) {
    val groups = facts.media.duplicateGroups
    if (!facts.media.permissionGranted) {
        PermissionInlineStrip(
            icon = Icons.Rounded.PermMedia,
            title = "صلاحية الوسائط مطلوبة",
            subtitle = "بعد المنح تظهر هنا نسخ حقيقية من صور وفيديو هذا الهاتف.",
            accent = V2Amber,
        )
        return
    }
    if (groups.isEmpty()) {
        PermissionInlineStrip(
            icon = Icons.Rounded.ContentCopy,
            title = "لا مجموعات تكرار مقروءة",
            subtitle = "نطاق المقارنة الحالي لم يجد نسخًا متطابقة.",
            accent = accent,
        )
        return
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            groups.take(4).forEach { group ->
                Box(Modifier.size(58.dp)) {
                    V2MediaThumb(
                        uri = group.sampleUri,
                        isVideo = group.isVideo,
                        accent = accent,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Box(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Black.copy(alpha = .72f))
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                    ) {
                        CompositionLocalProvider(LocalKnouxOnDark provides true) {
                            KText("×${group.count}", size = 9.sp, weight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            KText(
                formatBytes(groups.sumOf { it.reclaimableBytes }),
                size = 14.sp,
                weight = FontWeight.Black,
                color = V2Green,
                maxLines = 1,
            )
            KText("قابل للاستعادة", size = 9.sp, color = V2Soft, maxLines = 1)
        }
    }
}

@Composable
private fun MediaThumbStrip(files: List<MediaFileFacts>, accent: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            files.take(4).forEach { file ->
                Box(Modifier.size(52.dp)) {
                    V2MediaThumb(
                        uri = file.uriString,
                        isVideo = file.isVideo,
                        accent = accent,
                        modifier = Modifier.fillMaxSize(),
                    )
                    if (file.isVideo) {
                        Box(
                            Modifier
                                .align(Alignment.Center)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = .52f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(10.dp))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            KText("${files.size}", size = 14.sp, weight = FontWeight.Black, color = accent, maxLines = 1)
            KText("وسيط مقروء", size = 9.sp, color = V2Soft, maxLines = 1)
        }
    }
}

@Composable
private fun StorageVisualStrip(facts: DeviceFacts, accent: Color) {
    val storage = facts.storage
    val media = facts.media
    val total = storage.totalBytes.coerceAtLeast(1L)
    val mediaBytes = media.imagesBytes + media.videosBytes
    val cache = facts.ownCacheBytes
    val other = (storage.usedBytes - mediaBytes - cache).coerceAtLeast(0L)
    val showCache = cache > 0L
    val segments = buildList {
        add((media.imagesBytes.toFloat() / total).coerceIn(0f, 1f) to V2Pink)
        add((media.videosBytes.toFloat() / total).coerceIn(0f, 1f) to V2Violet)
        if (showCache) add((cache.toFloat() / total).coerceIn(0f, 1f) to V2Cyan)
        add((other.toFloat() / total).coerceIn(0f, 1f) to V2Amber)
    }
    SegmentedBar(segments, height = 9)
    Spacer(Modifier.height(6.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        LegendChip("صور", if (media.permissionGranted) formatBytes(media.imagesBytes) else "بعد المنح", V2Pink, Modifier.weight(1f))
        LegendChip("فيديو", if (media.permissionGranted) formatBytes(media.videosBytes) else "بعد المنح", V2Violet, Modifier.weight(1f))
        if (showCache) {
            LegendChip("كاش", formatBytes(cache), V2Cyan, Modifier.weight(1f))
        }
        LegendChip("أخرى", formatBytes(other), V2Amber, Modifier.weight(1f))
    }
}

@Composable
private fun LegendChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    val tint = if (LocalDayMode.current) dayAccent(color) else color
    Row(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tint.withAlpha(if (LocalDayMode.current) .12f else .10f))
            .border(1.dp, tint.withAlpha(if (LocalDayMode.current) .40f else .24f), RoundedCornerShape(8.dp))
            .padding(horizontal = 5.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(tint))
        Spacer(Modifier.width(4.dp))
        Column {
            KText(label, size = 9.sp, color = V2Muted, maxLines = 1)
            KText(value, size = 9.sp, weight = FontWeight.Bold, color = color, maxLines = 1)
        }
    }
}

@Composable
private fun AppIconStrip(apps: List<InstalledAppFacts>, accent: Color) {
    val visible = remember(apps) {
        apps.sortedWith(compareByDescending<InstalledAppFacts> { it.sensitivePermissions.isNotEmpty() }.thenBy { it.label }).take(6)
    }
    if (visible.isEmpty()) {
        PermissionInlineStrip(
            icon = Icons.Rounded.Apps,
            title = "لم تُقرأ قائمة التطبيقات بعد",
            subtitle = "ستظهر أيقونات التطبيقات المثبتة هنا بعد اكتمال القراءة.",
            accent = accent,
        )
        return
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            visible.forEach { app ->
                val icon = rememberAppIcon(app.packageName)
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.withAlpha(.06f))
                        .border(1.dp, accent.withAlpha(.24f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (icon != null) {
                        Image(bitmap = icon, contentDescription = null, modifier = Modifier.size(28.dp))
                    } else {
                        Icon(Icons.Rounded.Apps, null, tint = accent, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            KText(formatCount(apps.size), size = 13.sp, weight = FontWeight.Black, color = accent)
            KText("تطبيق مثبت", size = 9.sp, color = V2Soft)
        }
    }
}

@Composable
private fun PermissionVisualStrip(facts: DeviceFacts, accent: Color) {
    val groups = remember(facts.privacyGroups) {
        facts.privacyGroups
            .sortedByDescending { group -> if (group.appCount <= 0) 0f else group.grantedCount.toFloat() / group.appCount }
            .take(2)
    }
    if (groups.isEmpty()) {
        PermissionInlineStrip(
            icon = Icons.Rounded.Shield,
            title = "مجموعات الأذونات قيد القراءة",
            subtitle = "PackageManager لم يُعد مجموعات بعد.",
            accent = accent,
        )
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        groups.forEach { group ->
            val ratio = if (group.appCount <= 0) 0f else group.grantedCount.toFloat() / group.appCount
            Row(verticalAlignment = Alignment.CenterVertically) {
                KText(group.label, size = 9.sp, color = V2Soft, modifier = Modifier.width(66.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.width(5.dp))
                V2ProgressBar(
                    progress = ratio,
                    modifier = Modifier.weight(1f),
                    height = 6.dp,
                    colors = listOf(accent, V2Cyan),
                )
                Spacer(Modifier.width(5.dp))
                KText("${group.grantedCount}/${group.appCount}", size = 9.sp, weight = FontWeight.Bold, color = accent)
            }
        }
    }
}

@Composable
private fun BatteryVisualStrip(battery: BatteryFacts, accent: Color) {
    val level = (battery.levelPercent ?: 0).coerceIn(0, 100)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.width(116.dp).height(38.dp)) {
            val bodyW = size.width - 8.dp.toPx()
            val bodyH = size.height * .66f
            val top = (size.height - bodyH) / 2f
            val radius = CornerRadius(7.dp.toPx(), 7.dp.toPx())
            drawRoundRect(
                color = Color.White.copy(alpha = .08f),
                topLeft = Offset(0f, top),
                size = Size(bodyW, bodyH),
                cornerRadius = radius,
            )
            drawRoundRect(
                brush = Brush.horizontalGradient(listOf(V2Green, accent, V2Cyan)),
                topLeft = Offset(0f, top),
                size = Size(bodyW * level / 100f, bodyH),
                cornerRadius = radius,
            )
            drawRoundRect(
                color = Color.White.copy(alpha = .26f),
                topLeft = Offset(0f, top),
                size = Size(bodyW, bodyH),
                cornerRadius = radius,
                style = Stroke(width = 1.2.dp.toPx()),
            )
            drawRoundRect(
                color = Color.White.copy(alpha = .20f),
                topLeft = Offset(bodyW, top + bodyH * .28f),
                size = Size(4.dp.toPx(), bodyH * .44f),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            )
        }
        Spacer(Modifier.width(10.dp))
        Column {
            KText("$level% · ${battery.statusLabel}", size = 9.sp, weight = FontWeight.Bold, color = accent)
            KText(
                listOfNotNull(
                    battery.temperatureC?.let { "حرارة $it°" },
                    battery.voltageMv?.let { "جهد ${it}mV" },
                    battery.healthLabel,
                ).joinToString(" · "),
                size = 9.sp,
                color = V2Soft,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun HistoryVisualStrip(history: List<ScanHistoryEntry>, accent: Color) {
    if (history.isEmpty()) {
        PermissionInlineStrip(
            icon = Icons.Rounded.Schedule,
            title = "لا سجل محلي بعد",
            subtitle = "سيُحفظ الفحص القادم هنا وتظهر أعمدة المقارنة.",
            accent = accent,
        )
        return
    }
    val recent = remember(history) { history.take(8).reversed() }
    val maxCache = recent.maxOfOrNull { it.cacheBytes }?.coerceAtLeast(1L) ?: 1L
    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(
            Modifier.height(46.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            recent.forEach { entry ->
                val fraction = (entry.cacheBytes.toFloat() / maxCache.toFloat()).coerceIn(.08f, 1f)
                Box(
                    Modifier
                        .width(13.dp)
                        .height((46f * fraction).dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(accent, accent.copy(alpha = .20f)),
                            ),
                        ),
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Column {
            KText(formatBytes(history.first().cacheBytes), size = 11.sp, weight = FontWeight.Black, color = accent)
            KText("آخر كاش مقروء", size = 9.sp, color = V2Soft)
        }
    }
}

@Composable
private fun PermissionInlineStrip(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accent.withAlpha(.08f))
            .border(1.dp, accent.withAlpha(.24f), RoundedCornerShape(12.dp))
            .padding(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(7.dp))
        Column(Modifier.weight(1f)) {
            KText(title, size = 9.sp, weight = FontWeight.Bold, color = accent, maxLines = 1, overflow = TextOverflow.Ellipsis)
            KText(subtitle, size = 9.sp, lineHeight = 10.6.sp, color = V2Soft, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

/* ----------------------------- result cards ----------------------------- */

@Composable
internal fun ServiceFileCard(
    file: MediaFileFacts,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    V2GlassCard(modifier.fillMaxWidth(), padding = 8.dp, radius = 16.dp, glow = accent, borderAlpha = .24f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2MediaThumb(
                uri = file.uriString,
                isVideo = file.isVideo,
                accent = accent,
                modifier = Modifier.size(width = 64.dp, height = 52.dp),
            )
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(file.name, size = 9.sp, weight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                KText(
                    "${file.bucket ?: if (file.isVideo) "فيديو" else "صورة"} · ${if (file.isVideo) "فيديو" else "صورة"}",
                    size = 9.sp,
                    color = V2Soft,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(accent.withAlpha(.12f))
                    .border(1.dp, accent.withAlpha(.28f), RoundedCornerShape(50))
                    .padding(horizontal = 7.dp, vertical = 2.dp),
            ) {
                KText(formatBytes(file.sizeBytes), size = 9.sp, weight = FontWeight.Bold, color = accent, maxLines = 1)
            }
        }
    }
}

@Composable
internal fun ServiceGroupCard(
    group: MediaDuplicateGroup,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    V2GlassCard(modifier.fillMaxWidth(), padding = 9.dp, radius = 17.dp, glow = accent, borderAlpha = .28f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(width = 104.dp, height = 54.dp)) {
                group.uris.take(3).forEachIndexed { index, uri ->
                    V2MediaThumb(
                        uri = uri,
                        isVideo = group.isVideo,
                        accent = accent,
                        modifier = Modifier
                            .offset(x = (index * 25).dp)
                            .size(54.dp)
                            .graphicsLayer { alpha = 1f - index * .16f },
                    )
                }
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = .76f))
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                ) {
                    CompositionLocalProvider(LocalKnouxOnDark provides true) {
                        KText("×${group.count}", size = 9.sp, weight = FontWeight.Black, color = Color.White)
                    }
                }
            }
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(group.name, size = 9.sp, weight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                KText(
                    "${if (group.isVideo) "فيديو" else "صور"} · ${group.count} نسخة · ${formatBytes(group.sizeBytes)} للملف",
                    size = 9.sp,
                    color = V2Soft,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                KText(formatBytes(group.reclaimableBytes), size = 10.5.sp, weight = FontWeight.Black, color = V2Green, maxLines = 1)
                KText("توفير", size = 9.sp, color = V2Muted, maxLines = 1)
            }
        }
    }
}

@Composable
internal fun ServicePermissionCard(
    group: AppPermissionGroup,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val ratio = if (group.appCount <= 0) 0f else group.grantedCount.toFloat() / group.appCount
    V2GlassCard(modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = accent, borderAlpha = .24f) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                V2IconTile(Icons.Rounded.Lock, accent, size = 30.dp, iconSize = 15.dp)
                Spacer(Modifier.width(7.dp))
                Column(Modifier.weight(1f)) {
                    KText(group.label, size = 9.sp, weight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    KText("${group.grantedCount} ممنوحة من ${group.appCount} تطبيقًا", size = 9.sp, color = V2Soft)
                }
                KText("${(ratio * 100).roundToInt()}%", size = 9.sp, weight = FontWeight.Black, color = accent)
            }
            Spacer(Modifier.height(6.dp))
            V2ProgressBar(ratio, height = 5.dp, colors = listOf(accent, V2Cyan))
        }
    }
}

@Composable
internal fun ServiceHistoryEntryCard(
    entry: ScanHistoryEntry,
    maxCache: Long,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val fraction = if (maxCache <= 0L) 0f else (entry.cacheBytes.toFloat() / maxCache.toFloat()).coerceIn(0f, 1f)
    V2GlassCard(modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = accent, borderAlpha = .24f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(Icons.Rounded.Radar, accent, size = 32.dp, iconSize = 16.dp)
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText(formatScanTime(entry.timestamp), size = 9.sp, weight = FontWeight.Bold)
                KText(
                    "${formatCount(entry.appsScanned)} تطبيق · ${formatBytes(entry.storageUsedBytes)} مستخدمة",
                    size = 9.sp,
                    color = V2Soft,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                V2ProgressBar(fraction, height = 4.dp, colors = listOf(accent, V2Cyan))
            }
            Spacer(Modifier.width(8.dp))
            KText(formatBytes(entry.cacheBytes), size = 9.sp, weight = FontWeight.Black, color = V2Pink)
        }
    }
}

/** Big visual answer for the Deep Scan result, replacing the text-only list. */
@Composable
internal fun DeepScanResultTiles(
    facts: DeviceFacts,
    measuredStages: Int,
    modifier: Modifier = Modifier,
) {
    val media = facts.media
    val storageRead = measuredStages >= DeepScanStage.STORAGE.ordinal + 1
    val appsRead = measuredStages >= DeepScanStage.APPS.ordinal + 1
    val privacyRead = measuredStages >= DeepScanStage.PRIVACY.ordinal + 1
    val cacheRead = measuredStages >= DeepScanStage.CACHE.ordinal + 1
    val mediaRead = measuredStages >= DeepScanStage.MEDIA.ordinal + 1
    val reclaimable = media.duplicateGroups.sumOf { it.reclaimableBytes }
    val stats = listOf(
        SectionStat(
            Icons.Rounded.Storage,
            if (storageRead) formatBytes(facts.storage.freeBytes) else "—",
            "مساحة متاحة",
            V2Cyan,
        ),
        SectionStat(
            Icons.Rounded.Apps,
            if (appsRead) formatCount(facts.installedCount) else "—",
            "تطبيق مثبت",
            V2Violet,
        ),
        SectionStat(
            Icons.Rounded.Shield,
            if (privacyRead) formatCount(facts.sensitiveAppCount) else "—",
            "تطبيق حساس",
            V2Amber,
        ),
        SectionStat(
            Icons.Rounded.ContentCopy,
            when {
                !mediaRead -> "—"
                !media.permissionGranted -> "إذن"
                else -> formatBytes(reclaimable)
            },
            "قابل للاستعادة",
            V2Pink,
        ),
        SectionStat(
            Icons.Rounded.CleaningServices,
            if (cacheRead) formatBytes(facts.ownCacheBytes) else "—",
            "كاش KNOUX",
            V2Blue,
        ),
    )
    Column(modifier.fillMaxWidth()) {
        if (storageRead) {
            val used = facts.storage.usedFraction.coerceIn(0f, 1f)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                KText("${(used * 100).roundToInt()}%", size = 11.sp, weight = FontWeight.Black, color = V2Cyan)
                Spacer(Modifier.width(6.dp))
                V2ProgressBar(
                    used,
                    modifier = Modifier.weight(1f),
                    height = 7.dp,
                    colors = listOf(V2Cyan, V2Pink),
                )
                Spacer(Modifier.width(6.dp))
                KText(formatBytes(facts.storage.usedBytes), size = 9.sp, color = V2Soft)
            }
            Spacer(Modifier.height(8.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            stats.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { stat -> SectionStatTile(stat, Modifier.weight(1f)) }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

/* ----------------------------- labels ----------------------------- */

internal fun dashboardTitle(section: String): String = when (section) {
    "smartclean" -> "لوحة التنظيف"
    "duplicates" -> "لوحة التكرار"
    "residue" -> "لوحة البقايا"
    "privacy" -> "لوحة الخصوصية"
    "deepscan" -> "لوحة الفحص"
    "reports" -> "لوحة التقارير"
    "appmanager" -> "لوحة التطبيقات"
    "storage" -> "لوحة التخزين"
    "battery" -> "لوحة البطارية"
    "ai" -> "لوحة التحسين"
    else -> "لوحة الخدمة"
}

private fun dashboardCaption(section: String, facts: DeviceFacts): String {
    if (facts.computedAt <= 0L) return "جارٍ قراءة مصادر هذا الجهاز…"
    val base = "قراءة مباشرة · ${formatCount(facts.installedCount)} تطبيق · " +
        "${formatBytes(facts.storage.usedBytes)} مستخدمة"
    return when (section) {
        "duplicates" ->
            if (facts.media.permissionGranted) {
                "$base · ${formatCount(facts.media.duplicateGroups.size)} مجموعة تكرار"
            } else {
                "$base · صلاحية الوسائط مطلوبة"
            }
        "storage", "smartclean", "residue" ->
            "$base · ${formatBytes(facts.storage.freeBytes)} متاحة"
        "privacy" ->
            "$base · ${formatCount(facts.sensitiveAppCount)} تطبيقًا بأذونات حساسة"
        "battery" ->
            facts.battery.levelPercent?.let { "$base · البطارية $it%" } ?: base
        "appmanager" ->
            "$base · ${formatCount(facts.userAppCount)} تطبيق مستخدم"
        else -> base
    }
}
