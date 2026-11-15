package com.knoux.ai.ui.v2

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ------------------------------------------------------------------ *
 * Deep scan
 * ------------------------------------------------------------------ */

@Composable
fun V2DeepScanScreen(onBack: () -> Unit, onOpenSummary: () -> Unit, onPro: () -> Unit) {
    var running by remember { mutableStateOf(true) }
    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item { ScanScreenHeader(running = running) }
                item { ScanOrbCard(running = running, onToggle = { running = !running }) }
                item { ScanSteps() }
                item { ScanResultsCard() }
                item {
                    V2GradientButton(
                        "عرض التفصيل",
                        onOpenSummary,
                        icon = Icons.Rounded.Radar,
                        trailingChevron = true,
                    )
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                KText(
                    "نتائج المعاينة ليست قياسًا مباشرًا من هذا الجهاز",
                    size = 7.4.sp,
                    color = V2Muted,
                )
            }
        }
    }
}

@Composable
private fun ScanScreenHeader(running: Boolean) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        androidx.compose.material3.Text(
            "الفحص العميق",
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = V2Font,
                fontWeight = FontWeight.Black,
                fontSize = 30.sp,
                color = Color.White,
            )
        )
        KText(
            "تحليل شامل للجهاز واكتشاف الفرص للتحسين",
            size = 10.5.sp,
            color = V2Soft,
            align = TextAlign.Center,
        )
        Spacer(Modifier.height(2.dp))
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(if (running) V2Cyan.copy(alpha = .10f) else V2Green.copy(alpha = .10f))
                .border(
                    1.dp,
                    if (running) V2Cyan.copy(alpha = .35f) else V2Green.copy(alpha = .35f),
                    RoundedCornerShape(50),
                )
                .padding(horizontal = 9.dp, vertical = 3.dp)
        ) {
            KText(
                if (running) "جاري الفحص الآن" else "تم إيقاف الفحص مؤقتًا",
                size = 7.8.sp,
                weight = FontWeight.SemiBold,
                color = if (running) V2Cyan else V2Green,
            )
        }
    }
}

@Composable
private fun ScanOrbCard(running: Boolean, onToggle: () -> Unit) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .48f,
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(176.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(V2Cyan.copy(alpha = .17f), V2Pink.copy(alpha = .10f), Color.Transparent),
                            radius = size.width * .52f,
                        ),
                        radius = size.width * .52f,
                    )
                    drawArc(
                        color = V2Pink.copy(alpha = .46f),
                        startAngle = 21f,
                        sweepAngle = 58f,
                        useCenter = false,
                        style = Stroke(2.dp.toPx(), cap = StrokeCap.Round),
                    )
                    drawArc(
                        color = V2Cyan.copy(alpha = .50f),
                        startAngle = 201f,
                        sweepAngle = 52f,
                        useCenter = false,
                        style = Stroke(2.dp.toPx(), cap = StrokeCap.Round),
                    )
                }
                V2NeonRing(
                    progress = .72f,
                    size = 152.dp,
                    stroke = 7.dp,
                    colors = listOf(V2Pink, V2Violet, V2Blue, V2Cyan),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ScanWave()
                        KText("72%", size = 36.sp, weight = FontWeight.Black)
                        KText(if (running) "جاري الفحص..." else "متوقف مؤقتًا", size = 9.5.sp, color = V2Soft)
                        Spacer(Modifier.height(3.dp))
                        Icon(Icons.Rounded.Radar, null, tint = V2Cyan, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(Modifier.height(5.dp))
            V2NeonRing(
                progress = .72f,
                size = 34.dp,
                stroke = 3.dp,
                colors = listOf(V2Cyan, V2Pink),
            ) {
                Icon(
                    if (running) Icons.Rounded.Pause else Icons.Rounded.Radar,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp).clickable(onClick = onToggle),
                )
            }
            Spacer(Modifier.height(3.dp))
            KText("فحص 5 وحدات · 72%", size = 8.5.sp, color = V2Muted)
        }
    }
}

@Composable
private fun ScanWave() {
    Canvas(Modifier.fillMaxWidth(.45f).height(18.dp)) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, size.height * .55f)
            cubicTo(size.width * .18f, size.height * .1f, size.width * .22f, size.height, size.width * .38f, size.height * .45f)
            cubicTo(size.width * .53f, 0f, size.width * .62f, size.height, size.width * .75f, size.height * .55f)
            cubicTo(size.width * .87f, size.height * .25f, size.width * .92f, size.height * .65f, size.width, size.height * .48f)
        }
        drawPath(
            path,
            Brush.horizontalGradient(listOf(V2Cyan, V2Pink)),
            style = Stroke(1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
private fun ScanSteps() {
    val steps = listOf(
        Triple("التخزين", Icons.Rounded.Storage, true),
        Triple("التطبيقات", Icons.Rounded.Layers, true),
        Triple("الخصوصية", Icons.Rounded.Shield, false),
        Triple("الأداء", Icons.Rounded.RocketLaunch, false),
    )
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 11.dp,
        radius = 18.dp,
        glow = V2Cyan,
        borderAlpha = .28f,
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            steps.forEachIndexed { index, (label, icon, done) ->
                if (index > 0) {
                    Box(
                        Modifier
                            .weight(.38f)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    if (done) listOf(V2Cyan.copy(alpha = .65f), V2Blue.copy(alpha = .30f))
                                    else listOf(Color.White.copy(alpha = .16f), Color.White.copy(alpha = .08f))
                                )
                            )
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(if (done) V2Cyan.copy(alpha = .12f) else V2Violet.copy(alpha = .16f))
                            .border(
                                1.dp,
                                if (done) V2Cyan.copy(alpha = .50f) else V2Pink.copy(alpha = .55f),
                                CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (done) Icons.Rounded.Check else icon,
                            null,
                            tint = if (done) V2Cyan else Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Spacer(Modifier.height(3.dp))
                    KText(label, size = 8.sp, color = if (done) V2Cyan else V2Soft, weight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ScanResultsCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .50f,
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(width = 3.dp, height = 17.dp).clip(RoundedCornerShape(4.dp)).background(V2Pink))
                    Spacer(Modifier.width(6.dp))
                    KText("نتائج الفحص", size = 15.sp, weight = FontWeight.Bold)
                }
                Spacer(Modifier.weight(1f))
                Icon(Icons.Rounded.AutoAwesome, null, tint = V2Amber, modifier = Modifier.size(15.dp))
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                V2NeonRing(
                    progress = .72f,
                    size = 92.dp,
                    stroke = 7.dp,
                    colors = listOf(V2Pink, V2Violet, V2Cyan),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Storage, null, tint = V2Cyan, modifier = Modifier.size(22.dp))
                        KText("256 GB", size = 13.sp, weight = FontWeight.Bold)
                        KText("المجموع", size = 6.5.sp, color = V2Soft)
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    ResultRow(Icons.Rounded.Storage, "التخزين", "مستخدم 182 GB", "72%", V2Cyan)
                    ResultRow(Icons.Rounded.Layers, "ملفات كبيرة", "14.8 GB", "36", V2Violet)
                    ResultRow(Icons.Rounded.Description, "ملفات مكررة", "2.6 GB", "118", V2Pink)
                    ResultRow(Icons.Rounded.Shield, "ملاحظات الخصوصية", "5 ملاحظات", "!", V2Amber)
                }
            }
        }
    }
}

@Composable
private fun ResultRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    badge: String,
    accent: Color,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = .035f))
            .border(1.dp, Color.White.copy(alpha = .08f), RoundedCornerShape(10.dp))
            .padding(horizontal = 7.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(13.dp))
        Spacer(Modifier.width(5.dp))
        KText(title, size = 8.3.sp, color = Color.White, weight = FontWeight.Medium, maxLines = 1)
        Spacer(Modifier.weight(1f))
        TextSmall(subtitle)
        Spacer(Modifier.width(4.dp))
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(accent.copy(alpha = .14f))
                .padding(horizontal = 5.dp, vertical = 1.dp)
        ) {
            KText(badge, size = 7.5.sp, weight = FontWeight.Bold, color = accent, maxLines = 1)
        }
    }
}

@Composable
private fun TextSmall(text: String) {
    KText(text, size = 7.3.sp, color = V2Soft, maxLines = 1)
}

/* ------------------------------------------------------------------ *
 * Duplicate files
 * ------------------------------------------------------------------ */

private data class DuplicateGroup(
    val title: String,
    val meta: String,
    val count: Int,
    val kind: String,
    val accent: Color,
)

private val duplicateGroups = listOf(
    DuplicateGroup("صور متطابقة", "12.4 MB · 4 نسخ", 4, "photo", V2Violet),
    DuplicateGroup("فيديو متكرر", "328.6 MB · 2 نسخ", 2, "video", V2Pink),
    DuplicateGroup("ملف مستند", "5.1 MB · 3 نسخ", 3, "document", V2Cyan),
)

@Composable
fun V2DuplicatesScreen(onBack: () -> Unit, onOpenDetail: () -> Unit, onPro: () -> Unit) {
    var filter by remember { mutableStateOf(0) }
    val selected = remember { mutableStateListOf<Pair<Int, Int>>(0 to 0) }
    val filters = listOf("الكل (48)", "الصور (28)", "الفيديو (12)", "أخرى (8)")

    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            androidx.compose.material3.Text(
                                "الملفات المكررة",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = V2Font,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 27.sp,
                                    color = Color.White,
                                )
                            )
                            KText("اكتشف وراجع النسخ المتطابقة بأمان", size = 10.sp, color = V2Soft)
                        }
                        Box(
                            Modifier
                                .size(43.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.linearGradient(listOf(V2Cyan.copy(alpha = .30f), V2Violet.copy(alpha = .28f))))
                                .border(1.dp, V2Cyan.copy(alpha = .46f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Rounded.Layers, null, tint = Color.White, modifier = Modifier.size(23.dp))
                        }
                    }
                }
                item { DuplicateSummaryCard() }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        filters.forEachIndexed { index, label ->
                            Box(Modifier.weight(1f)) {
                                V2Chip(label, active = filter == index, onClick = { filter = index })
                            }
                        }
                    }
                }
                items(duplicateGroups.size) { groupIndex ->
                    val group = duplicateGroups[groupIndex]
                    DuplicateGroupCard(
                        group = group,
                        groupIndex = groupIndex,
                        selected = selected,
                        onToggle = { item ->
                            val pair = groupIndex to item
                            if (selected.contains(pair)) selected.remove(pair) else selected.add(pair)
                        },
                        onOpenDetail = onOpenDetail,
                    )
                }
                item { PreviewNote("عدد المجموعات وأحجام الملفات هنا بيانات معاينة وليست قراءة مباشرة.") }
            }
            V2GradientButton(
                "مراجعة وحذف المكرر (2.8 GB)",
                onOpenDetail,
                icon = Icons.Rounded.CheckCircle,
                trailingChevron = true,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
            )
        }
    }
}

@Composable
private fun DuplicateSummaryCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .52f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = .82f,
                size = 94.dp,
                stroke = 7.dp,
                colors = listOf(V2Pink, V2Violet, V2Cyan),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText("2.8 GB", size = 19.sp, weight = FontWeight.Black)
                    KText("مكرر", size = 8.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.width(15.dp))
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                SummaryInline(Icons.Rounded.Description, "156", "ملفًا", V2Violet, Modifier.weight(1f))
                Box(Modifier.width(1.dp).height(37.dp).background(Color.White.copy(alpha = .14f)))
                Spacer(Modifier.width(12.dp))
                SummaryInline(Icons.Rounded.Folder, "48", "مجموعة", V2Cyan, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SummaryInline(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(25.dp))
        KText(value, size = 18.sp, weight = FontWeight.Black, color = Color.White)
        KText(label, size = 8.5.sp, color = V2Soft)
    }
}

@Composable
private fun DuplicateGroupCard(
    group: DuplicateGroup,
    groupIndex: Int,
    selected: MutableList<Pair<Int, Int>>,
    onToggle: (Int) -> Unit,
    onOpenDetail: () -> Unit,
) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 18.dp,
        glow = group.accent,
        borderAlpha = .46f,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                V2Check(checked = false, accent = group.accent)
                Spacer(Modifier.width(7.dp))
                Column(Modifier.weight(1f)) {
                    KText(group.title, size = 11.5.sp, weight = FontWeight.Bold)
                    KText(group.meta, size = 8.sp, color = V2Soft)
                }
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(group.accent.copy(alpha = .10f))
                        .border(1.dp, group.accent.copy(alpha = .28f), RoundedCornerShape(50))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Layers, null, tint = group.accent, modifier = Modifier.size(10.dp))
                        Spacer(Modifier.width(3.dp))
                        KText("${group.count}", size = 8.5.sp, weight = FontWeight.Bold, color = group.accent)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(group.count) { index ->
                    val itemSelected = selected.contains(groupIndex to index)
                    Box(Modifier.weight(1f).height(68.dp).clickable(onClick = onOpenDetail)) {
                        when (group.kind) {
                            "photo" -> MiniMountainThumb(Modifier.fillMaxSize(), if (index == 0) V2Violet else V2Cyan)
                            "video" -> {
                                MiniMountainThumb(Modifier.fillMaxSize(), V2Pink, showCity = true)
                                Box(
                                    Modifier
                                        .align(Alignment.Center)
                                        .size(25.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = .52f))
                                        .border(1.dp, Color.White.copy(alpha = .30f), CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Rounded.Movie, null, tint = Color.White, modifier = Modifier.size(13.dp))
                                }
                                KText(
                                    "02:18",
                                    size = 6.5.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(Color.Black.copy(alpha = .58f))
                                        .padding(horizontal = 3.dp, vertical = 1.dp)
                                )
                            }
                            else -> {
                                Box(
                                    Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF2A1030), Color(0xFF140B28))
                                            )
                                        )
                                        .border(
                                            1.dp,
                                            Color(0xFFE43D4A).copy(alpha = .34f),
                                            RoundedCornerShape(12.dp),
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Rounded.Description, null, tint = Color(0xFFFF6B7A), modifier = Modifier.size(26.dp))
                                        KText("PDF", size = 7.sp, weight = FontWeight.Bold, color = Color(0xFFFF8A96))
                                    }
                                }
                            }
                        }
                        if (index == 0) {
                            Box(
                                Modifier
                                    .align(Alignment.TopStart)
                                    .padding(4.dp)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = .46f))
                                    .border(1.dp, V2Amber.copy(alpha = .80f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                CrownGold(size = 12.dp)
                            }
                        }
                        V2Check(
                            checked = itemSelected,
                            accent = group.accent,
                            onToggle = { onToggle(index) },
                            modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun V2Check(
    checked: Boolean,
    accent: Color,
    onToggle: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .size(21.dp)
            .clip(RoundedCornerShape(7.dp))
            .then(
                if (checked) {
                    Modifier.background(Brush.linearGradient(listOf(accent, V2Violet)))
                        .border(1.dp, Color.White.copy(alpha = .30f), RoundedCornerShape(7.dp))
                } else {
                    Modifier.background(Color.Black.copy(alpha = .20f))
                        .border(1.dp, Color.White.copy(alpha = .38f), RoundedCornerShape(7.dp))
                }
            )
            .then(if (onToggle != null) Modifier.clickable(onClick = onToggle) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
    }
}

/* ------------------------------------------------------------------ *
 * App residue
 * ------------------------------------------------------------------ */

private data class ResidueCategory(
    val title: String,
    val size: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accent: Color,
)

private val residueCategories = listOf(
    ResidueCategory("ذاكرة التخزين المؤقت", "1.73 GB", Icons.Rounded.Layers, V2Violet),
    ResidueCategory("الملفات المؤقتة", "684 MB", Icons.Rounded.Description, V2Pink),
    ResidueCategory("ملفات التثبيت", "356 MB", Icons.Rounded.Folder, V2Violet),
    ResidueCategory("سجلات التطبيقات", "78 MB", Icons.Rounded.Description, V2Violet),
)

@Composable
fun V2ResidueScreen(onBack: () -> Unit, onReview: () -> Unit, onPro: () -> Unit) {
    var selected by remember { mutableStateOf(setOf(0, 1)) }

    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                item { ResidueTitle() }
                item { ResidueHeroCard() }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        residueCategories.forEachIndexed { index, category ->
                            ResidueCategoryTile(
                                category = category,
                                checked = selected.contains(index),
                                modifier = Modifier.weight(1f),
                                onToggle = {
                                    selected = if (selected.contains(index)) selected - index else selected + index
                                },
                            )
                        }
                    }
                }
                item { SelectedFoldersCard() }
                item { ResidueSafetyCard() }
            }
            V2GradientButton(
                "مراجعة وتنظيف",
                onReview,
                icon = Icons.Rounded.Delete,
                trailingChevron = true,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
            )
        }
    }
}

@Composable
private fun ResidueTitle() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(43.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(V2Cyan.copy(alpha = .28f), V2Violet.copy(alpha = .30f))))
                .border(1.dp, V2Cyan.copy(alpha = .42f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Layers, null, tint = Color.White, modifier = Modifier.size(23.dp))
        }
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            androidx.compose.material3.Text(
                "منظف بقايا التطبيقات",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = V2Font, fontWeight = FontWeight.Black, fontSize = 24.sp, color = Color.White
                )
            )
            KText("البقايا والملفات المؤقتة غير الضرورية", size = 9.sp, color = V2Soft)
        }
    }
}

@Composable
private fun ResidueHeroCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .52f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = .85f,
                size = 113.dp,
                stroke = 8.dp,
                colors = listOf(V2Cyan, V2Blue, V2Violet, V2Pink),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText("2.84 GB", size = 20.sp, weight = FontWeight.Black)
                    KText("قابلة للتنظيف", size = 8.sp, color = V2Soft)
                    Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(11.dp))
                }
            }
            Spacer(Modifier.weight(1f))
            NeonTrashCan(Modifier.size(78.dp))
            Spacer(Modifier.width(6.dp))
        }
    }
}

@Composable
private fun NeonTrashCan(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(listOf(V2Violet.copy(alpha = .25f), Color.Transparent)),
                radius = size.width * .56f,
            )
            drawOval(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, V2Pink.copy(alpha = .52f), V2Cyan.copy(alpha = .30f), Color.Transparent)
                ),
                topLeft = Offset(size.width * .10f, size.height * .78f),
                size = androidx.compose.ui.geometry.Size(size.width * .80f, 3.dp.toPx()),
            )
            val body = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width * .27f, size.height * .28f)
                lineTo(size.width * .73f, size.height * .28f)
                lineTo(size.width * .67f, size.height * .83f)
                lineTo(size.width * .33f, size.height * .83f)
                close()
            }
            drawPath(
                body,
                Brush.verticalGradient(listOf(V2Violet.copy(alpha = .85f), Color(0xFF150B33)))
            )
            drawPath(
                body,
                brush = Brush.verticalGradient(listOf(V2Cyan.copy(alpha = .85f), V2Pink.copy(alpha = .55f))),
                style = Stroke(1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            drawRoundRect(
                color = V2Cyan.copy(alpha = .85f),
                topLeft = Offset(size.width * .22f, size.height * .20f),
                size = androidx.compose.ui.geometry.Size(size.width * .56f, size.height * .07f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
            )
            drawRoundRect(
                color = V2Pink.copy(alpha = .85f),
                topLeft = Offset(size.width * .41f, size.height * .14f),
                size = androidx.compose.ui.geometry.Size(size.width * .18f, size.height * .06f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
            )
            for (i in 0..2) {
                drawLine(
                    color = Color.White.copy(alpha = .32f),
                    start = Offset(size.width * (.40f + i * .10f), size.height * .38f),
                    end = Offset(size.width * (.38f + i * .10f), size.height * .72f),
                    strokeWidth = 1.4.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun ResidueCategoryTile(
    category: ResidueCategory,
    checked: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit,
) {
    val shape = RoundedCornerShape(15.dp)
    Column(
        modifier
            .height(105.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    if (checked) listOf(category.accent.copy(alpha = .30f), V2Panel2)
                    else listOf(V2GlassTop.copy(alpha = .70f), V2Panel2)
                )
            )
            .border(1.dp, if (checked) category.accent.copy(alpha = .70f) else Color.White.copy(alpha = .13f), shape)
            .clickable(onClick = onToggle)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        V2IconTile(category.icon, category.accent, size = 31.dp, iconSize = 16.dp)
        Spacer(Modifier.height(4.dp))
        KText(category.title, size = 7.4.sp, color = Color.White, align = TextAlign.Center, maxLines = 2, lineHeight = 9.sp)
        Spacer(Modifier.height(2.dp))
        KText(category.size, size = 9.sp, weight = FontWeight.Bold, color = V2Cyan)
        Box(Modifier.align(Alignment.End)) {
            V2Check(checked = checked, accent = category.accent)
        }
    }
}

@Composable
private fun SelectedFoldersCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 17.dp,
        glow = V2Cyan,
        borderAlpha = .28f,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Folder, null, tint = V2Cyan, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                KText("المجلدات المحددة", size = 11.sp, weight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                KText("3 مجلدات", size = 8.sp, color = V2Violet)
                Icon(Icons.Rounded.ChevronLeft, null, tint = V2Soft, modifier = Modifier.size(15.dp))
            }
            Spacer(Modifier.height(7.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                listOf("Android/cache", "Download/temp", "Apps/logs").forEach {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = .045f))
                            .border(1.dp, V2Cyan.copy(alpha = .24f), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Folder, null, tint = V2Cyan, modifier = Modifier.size(11.dp))
                            Spacer(Modifier.width(4.dp))
                            KText(it, size = 7.5.sp, color = V2Soft, maxLines = 1)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Rounded.Close, null, tint = V2Muted, modifier = Modifier.size(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResidueSafetyCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 17.dp,
        glow = V2Green,
        borderAlpha = .28f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(7.dp))
            Column(Modifier.weight(1f)) {
                KText("آمن", size = 10.5.sp, weight = FontWeight.Bold, color = V2Green)
                KText("لا يمس بياناتك المهمة", size = 8.sp, color = V2Soft)
            }
            Spacer(Modifier.width(10.dp))
            Box(Modifier.width(1.dp).height(31.dp).background(Color.White.copy(alpha = .13f)))
            Spacer(Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.End) {
                KText("2.84 GB", size = 12.sp, weight = FontWeight.Bold, color = V2Cyan)
                KText("إجمالي قابل للإزالة", size = 7.sp, color = V2Soft)
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Privacy
 * ------------------------------------------------------------------ */

@Composable
fun V2PrivacyScreen(onBack: () -> Unit, onOpenDetail: () -> Unit, onPro: () -> Unit) {
    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        // The reference puts the stag on the opposite shoulder for privacy.
        V2DeerHeader(
            Modifier
                .align(Alignment.TopEnd)
                .absoluteOffset(x = (-6).dp, y = 56.dp)
                .size(width = 158.dp, height = 248.dp),
            mirrored = true,
        )
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item { PrivacyTitle() }
                item { PrivacyHeroCard() }
                item { V2SectionLabel("مجالات الخصوصية", trailing = "آخر 7 أيام") }
                item { PrivacyDomainGrid(onOpenDetail) }
                item { PrivacyChangesCard() }
                item { PrivacyPreviewNote() }
            }
            V2BrightGoldButton(
                "مراجعة الأذونات",
                onClick = onOpenDetail,
                icon = Icons.Rounded.Shield,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
            )
        }
    }
}

@Composable
private fun PrivacyTitle() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .width(4.dp)
                .height(46.dp)
                .clip(RoundedCornerShape(50))
                .background(Brush.verticalGradient(listOf(V2Violet, V2Pink)))
        )
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            androidx.compose.material3.Text(
                "الخصوصية الذكية",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = V2Font, fontWeight = FontWeight.Black, fontSize = 27.sp, color = Color.White
                )
            )
            KText("تحكم كامل في أذوناتك وخصوصيتك بذكاء متقدم.", size = 10.sp, color = V2Soft)
        }
    }
}

@Composable
private fun PrivacyHeroCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 24.dp,
        glow = V2Violet,
        borderAlpha = .55f,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                V2NeonRing(
                    progress = .76f,
                    size = 112.dp,
                    stroke = 8.dp,
                    colors = listOf(V2Amber, V2Pink, V2Violet, V2Cyan),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Shield, null, tint = V2Amber, modifier = Modifier.size(20.dp))
                        KText("76%", size = 23.sp, weight = FontWeight.Black)
                        KText("مستوى الحماية", size = 7.sp, color = V2Soft)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(V2Red.copy(alpha = .18f))
                            .border(1.dp, V2Red.copy(alpha = .55f), RoundedCornerShape(50))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Shield, null, tint = V2Red, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            KText("خطر مرتفع", size = 8.sp, weight = FontWeight.Bold, color = V2Red)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    KText(
                        "تم اكتشاف مخاطر خصوصية تحتاج مراجعة فورية.",
                        size = 8.6.sp,
                        lineHeight = 12.5.sp,
                        color = V2Soft,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PrivacyCount("5", "أذونات حساسة", Icons.Rounded.Shield, V2Amber)
                        Spacer(Modifier.width(12.dp))
                        Box(Modifier.width(1.dp).height(26.dp).background(Color.White.copy(alpha = .10f)))
                        Spacer(Modifier.width(12.dp))
                        PrivacyCount("12", "تطبيقات متتبِّعة", Icons.Rounded.Radar, V2Violet)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            V2ProgressBar(.76f, height = 5.dp, colors = listOf(V2Amber, V2Pink, V2Violet, V2Cyan))
            Spacer(Modifier.height(4.dp))
            KText("إصلاح الأذونات الثلاثة الأعلى خطورة يرفع الدرجة إلى 92%.", size = 7.4.sp, color = V2Muted)
        }
    }
}

@Composable
private fun PrivacyCount(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accent: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(5.dp))
        Column {
            KText(value, size = 14.sp, weight = FontWeight.Black, color = accent)
            KText(label, size = 7.2.sp, color = V2Soft, maxLines = 1)
        }
    }
}

private data class PrivacyDomain(
    val title: String,
    val count: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accent: Color,
)

private val privacyDomains = listOf(
    PrivacyDomain("الكاميرا", "3 تطبيقات", Icons.Rounded.CameraAlt, V2Pink),
    PrivacyDomain("الموقع", "4 تطبيقات", Icons.Rounded.LocationOn, V2Cyan),
    PrivacyDomain("النشاط في الخلفية", "6 تطبيقات", Icons.Rounded.Layers, V2Violet),
    PrivacyDomain("أدوات التتبع", "2 تطبيقات", Icons.Rounded.Radar, V2Blue),
    PrivacyDomain("الإشعارات", "7 تطبيقات", Icons.Rounded.NotificationsActive, V2Amber),
    PrivacyDomain("الأذونات الحساسة", "5 أذونات", Icons.Rounded.Shield, V2Green),
)

@Composable
private fun PrivacyDomainGrid(onOpenDetail: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        privacyDomains.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                row.forEach { domain ->
                    PrivacyDomainCard(domain, Modifier.weight(1f), onOpenDetail)
                }
            }
        }
    }
}

@Composable
private fun PrivacyDomainCard(domain: PrivacyDomain, modifier: Modifier, onClick: () -> Unit) {
    V2GlassCard(
        modifier.height(93.dp).clickable(onClick = onClick),
        padding = 8.dp,
        radius = 16.dp,
        glow = domain.accent,
        borderAlpha = .35f,
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            V2IconTile(domain.icon, domain.accent, size = 31.dp, iconSize = 16.dp)
            Spacer(Modifier.height(4.dp))
            KText(domain.title, size = 8.4.sp, weight = FontWeight.Bold, maxLines = 1, align = TextAlign.Center)
            KText(domain.count, size = 7.sp, color = V2Soft, maxLines = 1)
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            Icon(Icons.Rounded.ChevronLeft, null, tint = V2Soft.copy(alpha = .8f), modifier = Modifier.size(13.dp))
        }
    }
}

private data class PrivacyChange(val app: String, val action: String, val whenText: String, val mark: AppMark, val accent: Color)

private val privacyChanges = listOf(
    PrivacyChange("صور Google", "تم منح إذن الكاميرا", "منذ 6 ساعات", AppMark.Photos, V2Blue),
    PrivacyChange("خرائط Google", "تم منح إذن الموقع", "منذ 18 ساعة", AppMark.Generic, V2Green),
    PrivacyChange("واتساب", "تم تعديل إذن الميكروفون", "منذ يوم واحد", AppMark.WhatsApp, V2Green),
)

@Composable
private fun PrivacyChangesCard() {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .42f) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Schedule, null, tint = V2Amber, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(6.dp))
                KText("آخر تغييرات الأذونات", size = 12.sp, weight = FontWeight.Black)
                Spacer(Modifier.weight(1f))
                KText("آخر 24 ساعة", size = 7.6.sp, color = V2Muted)
            }
            Spacer(Modifier.height(9.dp))
            privacyChanges.forEachIndexed { index, change ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.width(12.dp), contentAlignment = Alignment.Center) {
                        Box(
                            Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(change.accent)
                                .border(1.dp, Color.White.copy(alpha = .35f), CircleShape)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    if (change.mark == AppMark.Generic) {
                        V2IconTile(Icons.Rounded.LocationOn, change.accent, size = 34.dp, iconSize = 17.dp)
                    } else {
                        AppLogo(change.mark, size = 34.dp)
                    }
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        KText(change.app, size = 9.2.sp, weight = FontWeight.Bold)
                        KText(change.action, size = 7.3.sp, color = V2Muted)
                    }
                    SummaryPill(change.whenText, change.accent)
                }
                if (index < privacyChanges.lastIndex) {
                    Spacer(Modifier.height(6.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = .07f))
                    )
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun PrivacyPreviewNote() {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.Check, null, tint = V2Amber, modifier = Modifier.size(11.dp))
        Spacer(Modifier.width(5.dp))
        KText(
            "درجة الخصوصية والأذونات هنا بيانات معاينة وليست قراءة مباشرة من الجهاز.",
            size = 7.2.sp,
            color = V2Muted,
        )
    }
}
