package com.knoux.ai.ui.v2

import androidx.annotation.DrawableRes
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
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R

private data class HubV2(
    val title: String,
    val subtitle: String,
    val route: String,
    val art: Int,
    val accent: Color,
    val metric: String,
    val metricLabel: String,
)

private val cleanServices = listOf(
    HubV2(
        "الفحص العميق", "تحليل شامل للجهاز في فحص واحد", "deepscan",
        R.drawable.knoux_svc_deepscan, V2Cyan, "5", "وحدات",
    ),
    HubV2(
        "الملفات المكررة", "اكتشاف الصور والفيديو والمستندات المكررة", "duplicates",
        R.drawable.knoux_svc_duplicates, V2Violet, "2.8 GB", "مكرر",
    ),
    HubV2(
        "بقايا التطبيقات", "تنظيف الملفات والمجلدات المتبقية", "residue",
        R.drawable.knoux_svc_residue, V2Pink, "2.84 GB", "قابل للتنظيف",
    ),
    HubV2(
        "التنظيف الذكي", "مراجعة الملفات المؤقتة والكاش", "smartclean",
        R.drawable.knoux_home_icon_clean, V2Cyan, "2.92 GB", "مقترح",
    ),
    HubV2(
        "الملفات الكبيرة", "تحديد أكبر الملفات واستعادة المساحة", "large-files",
        R.drawable.knoux_home_icon_storage, V2Amber, "6.2 GB", "قابلة للاستعادة",
    ),
    HubV2(
        "نسخ احتياطية قديمة", "حذف النسخ الاحتياطية غير المستخدمة", "old-backups",
        R.drawable.knoux_svc_residue, V2Violet, "3.1 GB", " قابلة للحذف",
    ),
    HubV2(
        "سجلات النظام", "مسح سجلات التطبيقات والنظام القديمة", "system-logs",
        R.drawable.knoux_clean_icon_empty, V2Cyan, "78 MB", "قابل للإزالة",
    ),
    HubV2(
        "ملفات التحميل", "تنظيف مجلد التنزيلات من الملفات القديمة", "download-cleanup",
        R.drawable.knoux_svc_apps, V2Pink, "4.5 GB", "في التنزيلات",
    ),
)

private val toolServices = listOf(
    HubV2(
        "مدير التطبيقات", "إدارة التطبيقات والمساحة والاستخدام", "appmanager",
        R.drawable.knoux_svc_apps, V2Cyan, "54", "تطبيق",
    ),
    HubV2(
        "فحص الخصوصية", "تحليل الأذونات والمخاطر", "privacy",
        R.drawable.knoux_home_icon_privacy, V2Violet, "86%", "درجة الخصوصية",
    ),
    HubV2(
        "تحليل التخزين", "معرفة أين تذهب كل مساحة", "storage",
        R.drawable.knoux_home_icon_storage, V2Cyan, "67%", "مستخدم",
    ),
    HubV2(
        "توفير البطارية", "تقليل الاستهلاك وإدارة الخلفية", "battery",
        R.drawable.knoux_home_icon_battery, V2Green, "93%", "صحة البطارية",
    ),
    HubV2(
        "تحسين الأداء", "توصيات ذكية للذاكرة والسرعة", "ai",
        R.drawable.knoux_home_icon_ai, V2Pink, "+32%", "تحسن متوقع",
    ),
    HubV2(
        "مستكشف الملفات", "تصفح الملفات والمجلدات على الجهاز", "file-explorer",
        R.drawable.knoux_svc_residue, V2Cyan, "جميع", "المجلدات",
    ),
    HubV2(
        "معلومات الجهاز", "عرض مواصفات الجهاز وإصدار النظام", "device-info",
        R.drawable.knoux_home_icon_ai, V2Violet, "كامل", "المواصفات",
    ),
    HubV2(
        "فحص الشبكة", "فحص الاتصالات واستهلاك البيانات", "network-scan",
        R.drawable.knoux_svc_deepscan, V2Cyan, "الشبكة", "الحالة",
    ),
    HubV2(
        "فحص الأمان", "فحص الثغرات والمخاطر الأمنية", "security-scan",
        R.drawable.knoux_home_icon_privacy, V2Pink, "الأمان", "الحالة",
    ),
    HubV2(
        "إدارة الإشعارات", "التحكم في إشعارات التطبيقات", "notification-manager",
        R.drawable.knoux_svc_duplicates, V2Amber, "الإشعارات", "المدارة",
    ),
)

@Composable
fun V2CleanHubScreen(
    onNavigate: (String) -> Unit,
    onMenu: () -> Unit,
    onPro: () -> Unit,
    onProfile: () -> Unit,
    onNotifications: () -> Unit,
) {
    Box(
        Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(
                V2TopMode.HOME,
                onMenu = onMenu,
                onPro = onPro,
                onProfile = onProfile,
                onNotifications = onNotifications,
            )
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item { TabTitle("التنظيف الذكي", "كل ما يحتاجه جهازك في مكان واحد") }
                item { CleanHeroCard(onStart = { onNavigate("deepscan") }) }
                item { V2SectionLabel("أدوات التنظيف", trailing = "8 خدمات") }
                item { ServiceGrid(cleanServices, onNavigate) }
                item { BestNextAction(onClick = { onNavigate("smartclean") }) }
                item { SmartHint() }
            }
        }
    }
}

@Composable
fun V2ToolsHubScreen(
    onNavigate: (String) -> Unit,
    onMenu: () -> Unit,
    onPro: () -> Unit,
    onProfile: () -> Unit,
    onNotifications: () -> Unit,
) {
    Box(
        Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(
                V2TopMode.HOME,
                onMenu = onMenu,
                onPro = onPro,
                onProfile = onProfile,
                onNotifications = onNotifications,
            )
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item { TabTitle("أدوات الجهاز", "تحكم كامل في الأداء والخصوصية والمساحة") }
                item { ToolsSummaryCard() }
                item { V2SectionLabel("كل الأدوات", trailing = "10 أدوات") }
                item { ToolsGrid(toolServices, onNavigate) }
                item { ToolsTip() }
            }
        }
    }
}

@Composable
fun TabTitle(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(width = 3.dp, height = 28.dp).clip(RoundedCornerShape(4.dp)).background(
                Brush.verticalGradient(listOf(V2Pink, V2Cyan))
            ))
            Spacer(Modifier.width(7.dp))
            androidx.compose.material3.Text(
                title,
                style = TextStyle(
                    fontFamily = V2Font,
                    fontWeight = FontWeight.Black,
                    fontSize = 27.sp,
                    color = Color.White,
                    lineHeight = 34.sp,
                )
            )
        }
        KText(subtitle, size = 10.5.sp, color = V2Soft, align = TextAlign.Center)
        Spacer(Modifier.height(2.dp))
    }
}

@Composable
private fun CleanHeroCard(onStart: () -> Unit) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 22.dp,
        glow = V2Cyan,
        borderAlpha = .55f,
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                V2NeonRing(
                    progress = .92f,
                    size = 92.dp,
                    stroke = 7.dp,
                    colors = listOf(V2Pink, V2Violet, V2Blue, V2Cyan),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        KText("92%", size = 23.sp, weight = FontWeight.Black)
                        KText("صحة الجهاز", size = 7.5.sp, color = V2Soft)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        KText("جاهز للتحسين", size = 11.sp, weight = FontWeight.Bold, color = V2Green)
                    }
                    Spacer(Modifier.height(4.dp))
                    KText("3.42 GB", size = 25.sp, weight = FontWeight.Black, color = Color.White)
                    KText("مساحة قابلة للاستعادة بعد المراجعة", size = 8.5.sp, color = V2Soft)
                    Spacer(Modifier.height(7.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MiniTag("11 " + "ملفًا مؤقتًا", V2Cyan)
                        MiniTag("3 مجموعات", V2Violet)
                    }
                }
            }
            Spacer(Modifier.height(11.dp))
            V2GradientButton("بدء الفحص الذكي", onStart, icon = Icons.Rounded.Radar, trailingChevron = true)
        }
    }
}

@Composable
private fun MiniTag(text: String, accent: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(accent.copy(alpha = .10f))
            .border(1.dp, accent.copy(alpha = .25f), RoundedCornerShape(50))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        KText(text, size = 7.5.sp, color = captionColor(accent))
    }
}

private fun captionColor(accent: Color): Color = when (accent) {
    V2Cyan -> V2Cyan
    V2Violet -> Color(0xFFD5ACFF)
    V2Pink -> Color(0xFFFFA4DF)
    else -> V2Soft
}

@Composable
private fun ServiceGrid(services: List<HubV2>, onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        services.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pair.forEach { service ->
                    HubCard(service, onClick = { onNavigate(service.route) }, modifier = Modifier.weight(1f))
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun HubCard(service: HubV2, onClick: () -> Unit, modifier: Modifier = Modifier) {
    V2GlassCard(
        modifier = modifier.height(153.dp).clickable(onClick = onClick),
        padding = 10.dp,
        radius = 18.dp,
        glow = service.accent,
        borderAlpha = .36f,
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                V2RasterTile(service.art, size = 58.dp, accent = service.accent)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Rounded.ChevronLeft, null, tint = V2Soft.copy(alpha = .75f), modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(1.dp))
            KText(service.title, size = 12.5.sp, weight = FontWeight.Bold, maxLines = 1)
            KText(service.subtitle, size = 8.sp, lineHeight = 11.sp, color = V2Soft, maxLines = 2)
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(service.accent.copy(alpha = .12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    KText(service.metric, size = 8.5.sp, weight = FontWeight.Bold, color = captionColor(service.accent))
                }
                Spacer(Modifier.width(4.dp))
                KText(service.metricLabel, size = 7.sp, color = V2Muted, maxLines = 1)
            }
        }
    }
}

@Composable
private fun BestNextAction(onClick: () -> Unit) {
    V2GlassCard(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        padding = 11.dp,
        radius = 18.dp,
        glow = V2Amber,
        borderAlpha = .35f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                    .background(V2Amber.copy(alpha = .12f))
                    .border(1.dp, V2Amber.copy(alpha = .35f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.AutoAwesome, null, tint = V2Amber, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText("الخطوة التالية المقترحة", size = 11.sp, weight = FontWeight.Bold)
                KText("ابدأ بمراجعة 2.92 GB من الملفات المؤقتة قبل أي حذف.", size = 8.3.sp, color = V2Soft)
            }
            Icon(Icons.Rounded.ChevronLeft, null, tint = V2Soft, modifier = Modifier.size(17.dp))
        }
    }
}

@Composable
private fun SmartHint() {
    Row(Modifier.fillMaxWidth().padding(horizontal = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.Shield, null, tint = V2Cyan, modifier = Modifier.size(13.dp))
        Spacer(Modifier.width(5.dp))
        KText("كل عملية حذف تعرض الملفات أولًا وتطلب تأكيدك. لا شيء يُحذف تلقائيًا.", size = 8.sp, color = V2Muted)
    }
}

@Composable
private fun ToolsSummaryCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 22.dp,
        glow = V2Cyan,
        borderAlpha = .50f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = .67f,
                size = 78.dp,
                stroke = 6.dp,
                colors = listOf(V2Cyan, V2Blue, V2Violet),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText("67%", size = 20.sp, weight = FontWeight.Black)
                    KText("تخزين", size = 7.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                KText("مساحة التطبيقات", size = 11.sp, weight = FontWeight.Bold)
                KText("46.8 GB / 72.4 GB", size = 15.sp, weight = FontWeight.Bold, color = V2Cyan)
                Spacer(Modifier.height(5.dp))
                V2ProgressBar(progress = .67f)
                Spacer(Modifier.height(5.dp))
                KText("يمكن تحرير مساحة أكبر بمراجعة التطبيقات غير المستخدمة.", size = 7.9.sp, color = V2Soft)
            }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Rounded.Storage, null, tint = V2Cyan, modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
private fun ToolsGrid(services: List<HubV2>, onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        services.chunked(2).forEachIndexed { index, pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (pair.size == 1 && index % 2 == 0) Spacer(Modifier.weight(1f))
                pair.forEach { service ->
                    HubCard(service, onClick = { onNavigate(service.route) }, modifier = Modifier.weight(1f))
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ToolsTip() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 11.dp,
        radius = 17.dp,
        glow = V2Pink,
        borderAlpha = .28f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Speed, null, tint = V2Pink, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText("نصيحة الأداء", size = 10.5.sp, weight = FontWeight.Bold)
                KText("أغلق التطبيقات الثقيلة غير المستخدمة لتحرير الذاكرة وتحسين الاستجابة.", size = 8.sp, color = V2Soft)
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Reports
 * ------------------------------------------------------------------ */

@Composable
fun V2ReportsScreen(
    onOpenDetail: () -> Unit,
    onMenu: () -> Unit,
    onPro: () -> Unit,
    onProfile: () -> Unit,
    onNotifications: () -> Unit,
) {
    Box(
        Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(
                V2TopMode.HOME,
                onMenu = onMenu,
                onPro = onPro,
                onProfile = onProfile,
                onNotifications = onNotifications,
            )
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item { TabTitle("التقارير الذكية", "ملخص واضح لصحة جهازك وتطوره") }
                item { ReportScoreCard() }
                item { WeekTrendCard() }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReportMiniStat("26.4 GB", "تمت استعادته", Icons.Rounded.Storage, V2Cyan, Modifier.weight(1f))
                        ReportMiniStat("7", "إصلاحات خصوصية", Icons.Rounded.Shield, V2Violet, Modifier.weight(1f))
                        ReportMiniStat("+32%", "تحسن الأداء", Icons.Rounded.RocketLaunch, V2Pink, Modifier.weight(1f))
                    }
                }
                item { V2SectionLabel("التقارير الأخيرة", trailing = "عرض الكل") }
                item {
                    ReportRow(
                        "تقرير الأداء الأسبوعي",
                        "منذ 2 ساعة · تحسن إجمالي +32%",
                        Icons.Rounded.BarChart,
                        V2Cyan,
                        onOpenDetail,
                    )
                }
                item {
                    ReportRow(
                        "سجل عمليات التنظيف",
                        "آخر 7 أيام · 31 عملية",
                        Icons.Rounded.CleaningServices,
                        V2Pink,
                        onOpenDetail,
                    )
                }
                item {
                    ReportRow(
                        "حالة الخصوصية",
                        "آخر فحص · 43 تطبيقًا",
                        Icons.Rounded.Shield,
                        V2Violet,
                        onOpenDetail,
                    )
                }
                item {
                    Spacer(Modifier.height(1.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        KText("القيم المعروضة هي بيانات معاينة وليست قياسًا مباشرًا من الجهاز.", size = 7.4.sp, color = V2Muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportScoreCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 22.dp,
        glow = V2Cyan,
        borderAlpha = .52f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = .92f,
                size = 105.dp,
                stroke = 8.dp,
                colors = listOf(V2Pink, V2Violet, V2Blue, V2Cyan),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText("92%", size = 26.sp, weight = FontWeight.Black)
                    KText("صحة الجهاز", size = 8.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    KText("أداء ممتاز", size = 11.sp, weight = FontWeight.Bold, color = V2Green)
                }
                Spacer(Modifier.height(4.dp))
                KText("جهازك أسرع بنسبة 32% خلال الأسبوع الماضي، مع استهلاك طاقة أقل بنسبة 24%.", size = 9.sp, lineHeight = 13.sp, color = V2Soft)
                Spacer(Modifier.height(7.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KText("+32%", size = 15.sp, weight = FontWeight.Black, color = V2Cyan)
                    Spacer(Modifier.width(4.dp))
                    KText("تحسن الأداء", size = 8.sp, color = V2Muted)
                }
            }
        }
    }
}

@Composable
private fun WeekTrendCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 20.dp,
        glow = V2Violet,
        borderAlpha = .35f,
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                KText("تطور الأداء", size = 12.sp, weight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(V2Cyan))
                    Spacer(Modifier.width(4.dp))
                    KText("هذا الأسبوع", size = 8.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.height(8.dp))
            PerformanceChart()
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("الجمعة", "الخميس", "الأربعاء", "الثلاثاء", "الاثنين", "الأحد", "السبت").forEach {
                    KText(it, size = 7.sp, color = V2Muted)
                }
            }
        }
    }
}

@Composable
private fun PerformanceChart() {
    // RTL time axis: the newest day sits on the left, the oldest on the right.
    val bars = listOf(.42f, .55f, .51f, .66f, .60f, .76f, .90f).reversed()
    Canvas(Modifier.fillMaxWidth().height(85.dp)) {
        val maxBar = size.height * .82f
        val gap = size.width * .025f
        val barWidth = (size.width - gap * 6) / 7f
        val points = mutableListOf<Offset>()
        bars.forEachIndexed { index, value ->
            val x = index * (barWidth + gap) + barWidth / 2f
            val h = maxBar * value
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(if (index == 0) V2Cyan else V2Violet.copy(alpha = .65f), Color.Transparent)
                ),
                topLeft = Offset(x - barWidth * .36f, size.height - h),
                size = Size(barWidth * .72f, h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth * .36f),
            )
            points.add(Offset(x, size.height - h))
        }
        for (i in 0 until points.lastIndex) {
            drawLine(
                brush = Brush.horizontalGradient(listOf(V2Pink.copy(alpha = .75f), V2Cyan.copy(alpha = .9f))),
                start = points[i],
                end = points[i + 1],
                strokeWidth = 1.8.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
        points.forEach {
            drawCircle(V2Cyan, radius = 2.2.dp.toPx(), center = it)
        }
    }
}

@Composable
private fun ReportMiniStat(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    V2GlassCard(modifier, padding = 9.dp, radius = 15.dp, glow = accent, borderAlpha = .28f) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(19.dp))
            Spacer(Modifier.height(3.dp))
            KText(value, size = 12.sp, weight = FontWeight.Bold)
            KText(label, size = 7.5.sp, color = V2Soft, maxLines = 1, align = TextAlign.Center)
        }
    }
}

@Composable
private fun ReportRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    onClick: () -> Unit,
) {
    V2GlassCard(Modifier.fillMaxWidth().clickable(onClick = onClick), padding = 10.dp, radius = 16.dp, glow = accent, borderAlpha = .28f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 38.dp, iconSize = 19.dp)
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 11.sp, weight = FontWeight.Bold)
                KText(subtitle, size = 8.sp, color = V2Soft)
            }
            Icon(Icons.Rounded.ChevronLeft, null, tint = V2Soft, modifier = Modifier.size(17.dp))
        }
    }
}

/* ------------------------------------------------------------------ *
 * Smart Clean
 * ------------------------------------------------------------------ */

private data class CleanCategory(val name: String, val sub: String, val size: String, val icon: Int, val accent: Color)

private val cleanCategories = listOf(
    CleanCategory("ذاكرة التخزين المؤقت", "بيانات مؤقتة من التطبيقات", "1.73 GB", R.drawable.knoux_clean_icon_cache, V2Cyan),
    CleanCategory("الملفات المؤقتة", "ملفات النظام غير الضرورية", "684 MB", R.drawable.knoux_clean_icon_temp, V2Violet),
    CleanCategory("ملفات التثبيت", "حزم APK قديمة", "356 MB", R.drawable.knoux_clean_icon_apk, V2Pink),
    CleanCategory("سجلات التطبيقات", "تقارير أعطال قديمة", "78 MB", R.drawable.knoux_clean_icon_empty, V2Amber),
    CleanCategory("سلة المحذوفات", "ملفات في سلة المهملات", "70 MB", R.drawable.knoux_clean_icon_recycle, V2Cyan),
)

private val cleanSizesGb = listOf(1.73, .684, .356, .078, .070)
private val cleanTotalGb = cleanSizesGb.sum()

private fun gbText(value: Double): String =
    String.format(java.util.Locale.US, "%.2f GB", value)

@Composable
fun V2SmartCleanScreen(onBack: () -> Unit, onOpenDeepScan: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    var confirm by remember { mutableStateOf(false) }
    var lastFreed by remember { mutableStateOf<Long?>(null) }

    val cacheBytes = facts.ownCacheBytes
    val cacheText = formatBytes(cacheBytes)
    val progress = if (facts.storage.freeBytes > 0L) {
        (cacheBytes.toFloat() / (facts.storage.freeBytes + cacheBytes).toFloat()).coerceIn(0f, 1f)
    } else 0f

    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { androidx.compose.material3.Text("تنظيف كاش KNOUX") },
            text = {
                androidx.compose.material3.Text(
                    "سيتم حذف $cacheText من ملفات كاش KNOUX فقط (${formatCount(facts.ownCacheFiles)} ملف). " +
                        "لن تُحذف صورك أو مستنداتك أو بيانات التطبيقات الأخرى."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirm = false
                    lastFreed = clearOwnCache(context)
                    bump()
                }) { androidx.compose.material3.Text("حذف الكاش") }
            },
            dismissButton = {
                TextButton(onClick = { confirm = false }) {
                    androidx.compose.material3.Text("إلغاء")
                }
            },
        )
    }

    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                item { SmartCleanHeader() }
                item {
                    SmartCleanSummary(
                        selectedSize = cacheText,
                        totalSize = formatBytes(facts.storage.usedBytes),
                        selectedProgress = progress,
                        cacheFiles = facts.ownCacheFiles,
                        storageFree = formatBytes(facts.storage.freeBytes),
                        health = facts.healthEstimate,
                    )
                }
                item {
                    RealCleanRow(
                        title = "ذاكرة KNOUX المؤقتة",
                        subtitle = "${formatCount(facts.ownCacheFiles)} ملفًا داخل مجلدات الكاش",
                        value = cacheText,
                        accent = V2Cyan,
                        icon = Icons.Rounded.CleaningServices,
                    )
                }
                item {
                    RealCleanRow(
                        title = "ذاكرة التطبيقات الأخرى",
                        subtitle = "يحتاج صلاحية وصول خاصة — لا يُقدَّر بدل الجهاز",
                        value = "مقيد",
                        accent = V2Amber,
                        icon = Icons.Rounded.Storage,
                    )
                }
                item {
                    RealCleanRow(
                        title = "ملفات النظام المؤقتة",
                        subtitle = "يديرها Android ويحذفها تلقائيًا",
                        value = "تلقائي",
                        accent = V2Violet,
                        icon = Icons.Rounded.Shield,
                    )
                }
                if (lastFreed != null) {
                    item {
                        V2GlassCard(
                            Modifier.fillMaxWidth(),
                            padding = 10.dp,
                            radius = 16.dp,
                            glow = V2Green,
                            borderAlpha = .45f,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) {
                                    KText("تم حذف كاش KNOUX فعليًا", size = 10.sp, weight = FontWeight.Bold, color = V2Green)
                                    KText(
                                        "المساحة المحررة: ${formatBytes(lastFreed ?: 0L)} · أعد القراءة للتأكد",
                                        size = 8.sp,
                                        color = V2Soft,
                                    )
                                }
                                Icon(
                                    Icons.Rounded.Refresh,
                                    null,
                                    tint = V2Cyan,
                                    modifier = Modifier.size(20.dp).clickable { lastFreed = null; bump() },
                                )
                            }
                        }
                    }
                }
                item { CleanSafetyCard() }
                item {
                    PreviewNote(
                        "الأحجام هنا مقروءة من كاش KNOUX على هذا الهاتف. " +
                            "المعالجة المزيفة أو أرقام المعاينة غير مستخدمة في هذه الشاشة."
                    )
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(Modifier.weight(.82f)) {
                    V2GhostButton(
                        "تحديث",
                        onClick = { lastFreed = null; bump() },
                        icon = Icons.Rounded.Refresh,
                        height = 46.dp,
                    )
                }
                Box(Modifier.weight(1.18f)) {
                    V2GradientButton(
                        if (cacheBytes > 0L) "تنظيف $cacheText" else "لا يوجد كاش",
                        onClick = { if (cacheBytes > 0L) confirm = true else bump() },
                        icon = Icons.Rounded.CleaningServices,
                        height = 46.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun RealCleanRow(
    title: String,
    subtitle: String,
    value: String,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = accent, borderAlpha = .25f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 37.dp, iconSize = 19.dp)
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 10.5.sp, weight = FontWeight.Bold)
                KText(subtitle, size = 7.6.sp, color = V2Soft)
            }
            KText(value, size = 9.5.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

@Composable
private fun SmartCleanSummary(
    selectedSize: String,
    totalSize: String,
    selectedProgress: Float,
    cacheFiles: Int,
    storageFree: String,
    health: Int,
) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 22.dp,
        glow = V2Cyan,
        borderAlpha = .55f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = selectedProgress,
                size = 103.dp,
                stroke = 7.dp,
                colors = listOf(V2Cyan, V2Blue, V2Violet, V2Pink),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText(selectedSize, size = 17.sp, weight = FontWeight.Black)
                    KText("كاش KNOUX", size = 7.5.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    KText("قراءة مباشرة", size = 9.5.sp, weight = FontWeight.Bold, color = V2Green)
                }
                Spacer(Modifier.height(4.dp))
                KText("$cacheFiles ملفًا", size = 21.sp, weight = FontWeight.Black)
                KText("داخل كاش KNOUX فقط · صحة تقديرية $health%", size = 8.sp, color = V2Soft)
                Spacer(Modifier.height(5.dp))
                V2ProgressBar(selectedProgress, height = 5.dp)
                Spacer(Modifier.height(5.dp))
                KText("$storageFree متاحة · مستخدم الجهاز $totalSize", size = 7.3.sp, color = V2Muted)
            }
        }
    }
}

@Composable
private fun SmartCleanHeader() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(Icons.Rounded.CleaningServices, V2Pink, size = 38.dp, iconSize = 20.dp)
            Spacer(Modifier.width(8.dp))
            androidx.compose.material3.Text(
                "التنظيف الذكي",
                style = TextStyle(fontFamily = V2Font, fontWeight = FontWeight.Black, fontSize = 26.sp, color = Color.White)
            )
        }
        KText("مراجعة الملفات المؤقتة والكاش قبل أي حذف", size = 10.sp, color = V2Soft, align = TextAlign.Center)
    }
}

@Composable
private fun CleanCategoryRow(item: CleanCategory, checked: Boolean, onToggle: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    if (checked) listOf(item.accent.copy(alpha = .12f), V2Panel2)
                    else listOf(Color.White.copy(alpha = .035f), V2Panel2)
                )
            )
            .border(1.dp, if (checked) item.accent.copy(alpha = .48f) else Color.White.copy(alpha = .10f), shape)
            .clickable(onClick = onToggle)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        V2RasterTile(item.icon, size = 37.dp, accent = item.accent)
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            KText(item.name, size = 11.sp, weight = FontWeight.Bold)
            KText(item.sub, size = 8.sp, color = V2Soft)
        }
        KText(item.size, size = 10.sp, weight = FontWeight.Bold, color = captionColor(item.accent))
        Spacer(Modifier.width(9.dp))
        V2Check(checked, item.accent, onToggle)
    }
}

@Composable
private fun CleanSafetyCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 9.dp,
        radius = 15.dp,
        glow = V2Green,
        borderAlpha = .25f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Shield, null, tint = V2Green, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(7.dp))
            Column(Modifier.weight(1f)) {
                KText("آمن للملفات الشخصية", size = 9.5.sp, weight = FontWeight.Bold, color = V2Green)
                KText("لن تلمس الصور أو المستندات أو بيانات التطبيقات الأساسية.", size = 7.5.sp, color = V2Soft)
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Pro
 * ------------------------------------------------------------------ */

@Composable
fun V2ProScreen(onBack: () -> Unit, onNavigate: (String) -> Unit) {
    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(V2Amber.copy(alpha = .16f), Color.Transparent),
                    center = Offset(size.width * .5f, size.height * .24f),
                    radius = size.width * .80f,
                ),
                radius = size.width * .80f,
                center = Offset(size.width * .5f, size.height * .24f),
            )
        }
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = { })
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        CrownGold(size = 62.dp)
                        androidx.compose.material3.Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = Color.White)) { append("KNOUX AI ") }
                                withStyle(SpanStyle(color = V2GoldText)) { append("Pro") }
                            },
                            style = TextStyle(fontFamily = V2Font, fontWeight = FontWeight.Black, fontSize = 30.sp),
                        )
                        KText("أدوات أعمق .. ذكاء أقوى .. بدون قيود", size = 11.sp, color = V2Soft)
                    }
                }
                item { ProHighlightCard() }
                item {
                    V2GlassCard(Modifier.fillMaxWidth(), padding = 13.dp, radius = 21.dp, glow = V2Amber, borderAlpha = .38f) {
                        Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                            ProFeature(Icons.Rounded.AutoAwesome, "توصيات ذكاء اصطناعي متقدمة", "تحليل أعمق وتقارير مخصصة حسب استخدامك")
                            ProFeature(Icons.Rounded.Radar, "فحص عميق غير محدود", "فحص كامل لكل الأقسام مع مقارنة النتائج")
                            ProFeature(Icons.Rounded.Shield, "حماية فورية من المخاطر", "مراقبة مستمرة للأذونات والتهديدات")
                            ProFeature(Icons.Rounded.CleaningServices, "تنظيف بلا حدود", "قوائم تنظيف أوسع وأدوات احترافية")
                            ProFeature(Icons.Rounded.BarChart, "تقارير وتاريخ كامل", "تتبع كل التحسينات على مدى الأشهر")
                        }
                    }
                }
                item {
                    V2GoldButton(
                        text = "استكشاف مزايا Pro",
                        onClick = { onNavigate("reports") },
                        icon = Icons.Rounded.Star,
                    )
                }
                item {
                    V2GhostButton(
                        text = "العودة إلى الرئيسية",
                        onClick = { onNavigate("home") },
                        icon = Icons.Rounded.Shield,
                    )
                }
                item {
                    KText(
                        "هذه الشاشة تعرض مزايا الإصدار Pro في وضع المعاينة. لا يوجد شراء فعلي أو خصم في هذا البناء.",
                        size = 7.8.sp,
                        color = V2Muted,
                        align = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProHighlightCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 14.dp,
        radius = 22.dp,
        glow = V2Amber,
        borderAlpha = .55f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                KText("تجربة كاملة", size = 10.sp, weight = FontWeight.Bold, color = V2GoldText)
                KText("كل شيء مفتوح", size = 25.sp, weight = FontWeight.Black)
                KText("جميع الأدوات والمزايا في نسخة واحدة", size = 9.sp, color = V2Soft)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Check, null, tint = V2Green, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    KText("بدون إعلانات", size = 8.5.sp, color = V2Soft)
                    Spacer(Modifier.width(10.dp))
                    Icon(Icons.Rounded.Check, null, tint = V2Green, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    KText("حماية أعمق", size = 8.5.sp, color = V2Soft)
                }
            }
            Box(
                Modifier.size(76.dp).clip(CircleShape)
                    .background(Brush.radialGradient(listOf(V2Amber.copy(alpha = .25f), Color(0xFF20100A))))
                    .border(1.dp, V2Amber.copy(alpha = .65f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CrownGold(size = 45.dp)
            }
        }
    }
}

@Composable
private fun ProFeature(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(11.dp))
                .background(V2Amber.copy(alpha = .10f))
                .border(1.dp, V2Amber.copy(alpha = .26f), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = V2Amber, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            KText(title, size = 10.5.sp, weight = FontWeight.Bold)
            KText(subtitle, size = 8.sp, color = V2Soft)
        }
        Icon(Icons.Rounded.Check, null, tint = V2Green, modifier = Modifier.size(14.dp))
    }
}
