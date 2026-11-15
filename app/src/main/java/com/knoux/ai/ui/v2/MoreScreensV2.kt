package com.knoux.ai.ui.v2

import android.content.Context
import com.knoux.ai.ui.v2.KnouxProState
import com.knoux.ai.ui.v2.markKnouxOnboardingSeen
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Report
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ------------------------------------------------------------------ *
 * Shared interior scaffold
 * ------------------------------------------------------------------ */

@Composable
private fun InteriorScaffold(
    onBack: () -> Unit,
    onPro: () -> Unit,
    bottomBar: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content,
            )
            if (bottomBar != null) {
                Box(Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                    bottomBar()
                }
            }
        }
    }
}

@Composable
private fun PageHeading(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = TextStyle(
                    fontFamily = V2Font,
                    fontWeight = FontWeight.Black,
                    fontSize = 27.sp,
                    lineHeight = 34.sp,
                    color = Color.White,
                )
            )
            KText(subtitle, size = 10.sp, color = V2Soft)
        }
        if (trailing != null) {
            trailing()
        } else {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Brush.linearGradient(listOf(accent.withAlpha( .34f), V2Violet.withAlpha( .22f))))
                    .border(1.dp, accent.withAlpha( .52f), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String, accent: Color = V2Soft, onClick: (() -> Unit)? = null) {
    val modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp).then(
        if (onClick != null) Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
            .clickable(onClick = onClick)
            .background(Color.White.withAlpha( .02f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp)
        else Modifier.fillMaxWidth().padding(vertical = 7.dp)
    )
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KText(label, size = 9.5.sp, color = V2Soft)
        Spacer(Modifier.weight(1f))
        KText(value, size = 9.5.sp, weight = FontWeight.SemiBold, color = accent)
        if (onClick != null) {
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Rounded.Build, null, tint = V2Amber.withAlpha( .6f), modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun HairLine() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.withAlpha( .08f))
    )
}

@Composable
internal fun PreviewNote(text: String) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Rounded.Info, null, tint = V2Muted, modifier = Modifier.size(11.dp))
        Spacer(Modifier.width(4.dp))
        KText(text, size = 7.4.sp, color = V2Muted, align = TextAlign.Center)
    }
}

@Composable
internal fun V2Toggle(checked: Boolean, onChange: (Boolean) -> Unit, accent: Color = V2Cyan) {
    Box(
        Modifier
            .size(width = 42.dp, height = 24.dp)
            .clip(RoundedCornerShape(50))
            .background(
                if (checked) Brush.horizontalGradient(listOf(accent.withAlpha( .90f), V2Violet))
                else Brush.horizontalGradient(listOf(Color.White.withAlpha( .10f), Color.White.withAlpha( .06f)))
            )
            .border(1.dp, if (checked) Color.White.withAlpha( .28f) else Color.White.withAlpha( .18f), RoundedCornerShape(50))
            .clickable { onChange(!checked) }
            .padding(3.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
private fun SegmentedBar(segments: List<Pair<Float, Color>>, height: Int = 9) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(50)),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        segments.forEach { (fraction, color) ->
            Box(
                Modifier
                    .weight(fraction.coerceAtLeast(.01f))
                    .fillMaxSize()
                    .background(color)
            )
        }
    }
}

/* ------------------------------------------------------------------ *
 * App manager
 * ------------------------------------------------------------------ */

private data class ManagedAppV2(
    val name: String,
    val packageName: String,
    val mark: AppMark,
    val size: String,
    val data: String,
    val cache: String,
    val lastUsed: String,
    val permissions: Int,
    val accent: Color,
)

private val managedAppsV2 = listOf(
    ManagedAppV2("TikTok", "com.zhiliaoapp.musically", AppMark.TikTok, "842 MB", "486 MB", "128 MB", "منذ 12 دقيقة", 12, V2Cyan),
    ManagedAppV2("WhatsApp", "com.whatsapp", AppMark.WhatsApp, "412 MB", "1.8 GB", "64 MB", "منذ 3 ساعات", 9, V2Green),
    ManagedAppV2("YouTube", "com.google.android.youtube", AppMark.YouTube, "356 MB", "920 MB", "212 MB", "منذ ساعة", 8, V2Pink),
    ManagedAppV2("Telegram", "org.telegram.messenger", AppMark.Telegram, "228 MB", "2.4 GB", "310 MB", "منذ 5 دقائق", 7, V2Blue),
    ManagedAppV2("الصور", "com.google.android.apps.photos", AppMark.Photos, "186 MB", "1.2 GB", "96 MB", "منذ يوم", 6, V2Violet),
)

private val appFilters = listOf("الكل (54)", "ثقيلة (8)", "نادرًا (12)", "حديثة (6)")

@Composable
fun V2AppManagerScreen(
    onBack: () -> Unit,
    onOpenDetail: () -> Unit,
    onPro: () -> Unit,
) {
    var filter by remember { mutableStateOf(0) }
    val selected = remember { mutableStateOf(setOf(0, 2)) }
    val selectedCount = selected.value.size

    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton(
                "مراجعة $selectedCount تطبيقات محددة",
                onOpenDetail,
                icon = Icons.Rounded.Apps,
                trailingChevron = true,
            )
        },
    ) {
        item {
            PageHeading(
                "مدير التطبيقات",
                "إدارة المساحة والاستخدام والأذونات لكل تطبيق",
                Icons.Rounded.Apps,
                V2Violet,
            )
        }
        item { AppManagerSummary() }
        item { V2SearchField("ابحث عن تطبيق أو حزمة…") }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                appFilters.forEachIndexed { index, label ->
                    AppFilterChip(
                        text = label,
                        active = filter == index,
                        modifier = Modifier.weight(1f),
                        onClick = { filter = index },
                    )
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                KText("التطبيقات المثبتة", size = 12.5.sp, weight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                KText("مرتبة حسب الحجم", size = 8.sp, color = V2Muted)
            }
        }
        items(managedAppsV2.size) { index ->
            val app = managedAppsV2[index]
            ManagedAppRow(
                app = app,
                checked = selected.value.contains(index),
                onToggle = {
                    selected.value = if (selected.value.contains(index)) {
                        selected.value - index
                    } else {
                        selected.value + index
                    }
                },
                onClick = onOpenDetail,
            )
        }
        item {
            V2GlassCard(
                Modifier.fillMaxWidth(),
                padding = 11.dp,
                radius = 17.dp,
                glow = V2Amber,
                borderAlpha = .30f,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.WarningAmber, null, tint = V2Amber, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        KText("8 تطبيقات لم تُستخدم منذ 30 يومًا", size = 9.5.sp, weight = FontWeight.Bold)
                        KText("مراجعتها قد تحرر حتى 2.4 GB من المساحة.", size = 7.8.sp, color = V2Soft)
                    }
                }
            }
        }
        item {
            PreviewNote("أسماء الحزم والأحجام هنا بيانات معاينة وليست قراءة مباشرة من الجهاز.")
        }
    }
}

@Composable
private fun AppFilterChip(
    text: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier
            .height(34.dp)
            .clip(shape)
            .background(
                if (active) Brush.horizontalGradient(listOf(V2Cyan.withAlpha( .88f), V2Blue.withAlpha( .92f)))
                else Brush.horizontalGradient(listOf(Color.White.withAlpha( .055f), Color.White.withAlpha( .02f)))
            )
            .border(1.dp, if (active) Color.Transparent else V2Border.withAlpha( .26f), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        KText(
            text,
            size = 8.6.sp,
            weight = if (active) FontWeight.Bold else FontWeight.Medium,
            color = if (active) V2Deep else V2Soft,
            maxLines = 1,
        )
    }
}

@Composable
private fun AppManagerSummary() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .52f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = .67f,
                size = 92.dp,
                stroke = 7.dp,
                colors = listOf(V2Cyan, V2Blue, V2Violet),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText("46.8", size = 17.sp, weight = FontWeight.Black)
                    KText("GB مستخدمة", size = 7.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                KText("54 تطبيقًا مثبتًا", size = 11.5.sp, weight = FontWeight.Bold)
                KText("من أصل 72.4 GB مساحة متاحة", size = 8.3.sp, color = V2Soft)
                Spacer(Modifier.height(6.dp))
                V2ProgressBar(.67f, height = 5.dp)
                Spacer(Modifier.height(7.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SummaryPill("8 ثقيلة", V2Pink)
                    SummaryPill("12 نادرًا", V2Amber)
                }
            }
        }
    }
}

@Composable
internal fun SummaryPill(text: String, accent: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(accent.withAlpha( .12f))
            .border(1.dp, accent.withAlpha( .28f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        KText(text, size = 7.6.sp, weight = FontWeight.SemiBold, color = accent)
    }
}

@Composable
private fun ManagedAppRow(
    app: ManagedAppV2,
    checked: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(17.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    if (checked) listOf(app.accent.withAlpha( .13f), V2Panel2)
                    else listOf(Color.White.withAlpha( .04f), V2Panel2)
                )
            )
            .border(1.dp, if (checked) app.accent.withAlpha( .45f) else Color.White.withAlpha( .10f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppLogo(app.mark, size = 42.dp)
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            KText(app.name, size = 11.5.sp, weight = FontWeight.Bold, maxLines = 1)
            KText(app.packageName, size = 7.2.sp, color = V2Muted, maxLines = 1)
            Spacer(Modifier.height(2.dp))
            KText("آخر استخدام ${app.lastUsed}", size = 7.sp, color = V2Muted, maxLines = 1)
        }
        Column(horizontalAlignment = Alignment.End) {
            KText(app.size, size = 10.5.sp, weight = FontWeight.Bold, color = app.accent)
            KText("${app.permissions} أذونات", size = 7.sp, color = V2Muted)
        }
        Spacer(Modifier.width(8.dp))
        V2Check(checked = checked, accent = app.accent, onToggle = onToggle)
    }
}

/* ------------------------------------------------------------------ *
 * App detail
 * ------------------------------------------------------------------ */

@Composable
fun V2AppDetailScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val app = managedAppsV2.first()
    var autoStart by remember { mutableStateOf(false) }
    var backgroundRestrict by remember { mutableStateOf(true) }

    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(.92f)) {
                    V2GhostButton("مسح الكاش", onClick = {}, icon = Icons.Rounded.CleaningServices, height = 47.dp)
                }
                Box(Modifier.weight(1.08f)) {
                    V2GradientButton("فتح التطبيق", onClick = {}, icon = Icons.Rounded.PlayArrow, height = 47.dp)
                }
            }
        },
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                AppLogo(app.mark, size = 68.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        app.name,
                        style = TextStyle(
                            fontFamily = V2Font,
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp,
                            color = Color.White,
                        )
                    )
                    KText(app.packageName, size = 7.4.sp, color = V2Muted, maxLines = 1)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        SummaryPill("الإصدار 34.5.1", V2Cyan)
                        SummaryPill("محدّث", V2Green)
                    }
                }
            }
        }
        item {
            V2GlassCard(
                Modifier.fillMaxWidth(),
                padding = 13.dp,
                radius = 22.dp,
                glow = V2Cyan,
                borderAlpha = .52f,
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        V2NeonRing(
                            progress = .74f,
                            size = 96.dp,
                            stroke = 7.dp,
                            colors = listOf(V2Cyan, V2Blue, V2Violet),
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                KText("842 MB", size = 17.sp, weight = FontWeight.Black)
                                KText("حجم التطبيق", size = 6.8.sp, color = V2Soft)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            DetailLegend("التطبيق", "228 MB", V2Cyan)
                            DetailLegend("البيانات", "486 MB", V2Violet)
                            DetailLegend("الذاكرة المؤقتة", "128 MB", V2Pink)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    SegmentedBar(listOf(.27f to V2Cyan, .58f to V2Violet, .15f to V2Pink))
                    Spacer(Modifier.height(5.dp))
                    KText("إجمالي الاستخدام 842 MB من مساحة الجهاز", size = 7.6.sp, color = V2Muted)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                AppActionTile("تحديث", Icons.Rounded.Update, V2Green, Modifier.weight(1f))
                AppActionTile("إيقاف", Icons.Rounded.PowerSettingsNew, V2Amber, Modifier.weight(1f))
                AppActionTile("إلغاء التثبيت", Icons.Rounded.Delete, V2Red, Modifier.weight(1f))
            }
        }
        item { V2SectionLabel("إدارة الخلفية", trailing = "توفير الطاقة") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 17.dp, glow = V2Amber, borderAlpha = .28f) {
                Column {
                    SettingToggleRow(
                        icon = Icons.Rounded.Bolt,
                        title = "تشغيل تلقائي",
                        subtitle = "السماح بالتشغيل مع بدء الجهاز",
                        checked = autoStart,
                        accent = V2Amber,
                        onChange = { autoStart = it },
                    )
                    HairLine()
                    SettingToggleRow(
                        icon = Icons.Rounded.BatteryFull,
                        title = "تقييد الخلفية",
                        subtitle = "إيقاف النشاط عند عدم الاستخدام",
                        checked = backgroundRestrict,
                        accent = V2Green,
                        onChange = { backgroundRestrict = it },
                    )
                }
            }
        }
        item { V2SectionLabel("الأذونات", trailing = "12 إذنًا") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 18.dp, glow = V2Violet, borderAlpha = .30f) {
                Column {
                    PermissionRow("الكاميرا", "تُستخدم لتصوير الفيديو", Icons.Rounded.Visibility, "مسموح", V2Green)
                    HairLine()
                    PermissionRow("الميكروفون", "تُستخدم للتسجيل الصوتي", Icons.Rounded.Settings, "مسموح", V2Green)
                    HairLine()
                    PermissionRow("الموقع", "غير مستخدم حاليًا", Icons.Rounded.Security, "مرفوض", V2Red)
                    HairLine()
                    PermissionRow("الإشعارات", "تنبيهات التفاعل", Icons.Rounded.Notifications, "مسموح", V2Green)
                }
            }
        }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 17.dp, glow = V2Cyan, borderAlpha = .24f) {
                Column {
                    InfoLine("تاريخ التثبيت", "12 أكتوبر 2026", Color.White)
                    HairLine()
                    InfoLine("آخر تحديث", "منذ 4 أيام", Color.White)
                    HairLine()
                    InfoLine("المصدر", "متجر Play", Color.White)
                    HairLine()
                    InfoLine("الحد الأدنى للإصدار", "Android 9", Color.White)
                }
            }
        }
        item { PreviewNote("قيم الحجم والاستخدام معاينة تصميمية حتى تُربط بقراءة الجهاز الفعلية.") }
    }
}

@Composable
private fun DetailLegend(label: String, value: String, accent: Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(accent))
        Spacer(Modifier.width(5.dp))
        KText(label, size = 8.2.sp, color = V2Soft)
        Spacer(Modifier.weight(1f))
        KText(value, size = 8.4.sp, weight = FontWeight.SemiBold, color = Color.White)
    }
}

@Composable
private fun AppActionTile(label: String, icon: ImageVector, accent: Color, modifier: Modifier = Modifier) {
    V2GlassCard(modifier, padding = 8.dp, radius = 15.dp, glow = accent, borderAlpha = .32f) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(19.dp))
            Spacer(Modifier.height(4.dp))
            KText(label, size = 8.2.sp, weight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
        }
    }
}

@Composable
private fun PermissionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    status: String,
    accent: Color,
) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(accent.withAlpha( .10f))
                .border(1.dp, accent.withAlpha( .26f), RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            KText(title, size = 9.8.sp, weight = FontWeight.SemiBold)
            KText(subtitle, size = 7.4.sp, color = V2Muted, maxLines = 1)
        }
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(accent.withAlpha( .11f))
                .padding(horizontal = 7.dp, vertical = 2.dp)
        ) {
            KText(status, size = 7.2.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    accent: Color,
    onChange: (Boolean) -> Unit,
) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        V2IconTile(icon, accent, size = 32.dp, iconSize = 16.dp)
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            KText(title, size = 9.8.sp, weight = FontWeight.SemiBold)
            KText(subtitle, size = 7.4.sp, color = V2Muted, maxLines = 1)
        }
        V2Toggle(checked, onChange, accent)
    }
}

/* ------------------------------------------------------------------ *
 * Storage analyzer
 * ------------------------------------------------------------------ */

private data class StorageCategoryV2(
    val name: String,
    val size: String,
    val percent: String,
    val fraction: Float,
    val icon: ImageVector,
    val accent: Color,
)

private val storageCategories = listOf(
    StorageCategoryV2("التطبيقات", "46.8 GB", "65%", .65f, Icons.Rounded.Apps, V2Violet),
    StorageCategoryV2("الصور والفيديو", "18.2 GB", "25%", .25f, Icons.Rounded.PhotoLibrary, V2Pink),
    StorageCategoryV2("المستندات", "4.6 GB", "6%", .06f, Icons.Rounded.Article, V2Cyan),
    StorageCategoryV2("الصوتيات", "1.8 GB", "3%", .03f, Icons.Rounded.Audiotrack, V2Amber),
    StorageCategoryV2("أخرى", "1.0 GB", "1%", .01f, Icons.Rounded.Folder, V2Green),
)

@Composable
fun V2StorageScreen(onBack: () -> Unit, onOpenDetail: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton("عرض التفصيل الكامل", onOpenDetail, icon = Icons.Rounded.Storage, trailingChevron = true)
        },
    ) {
        item {
            PageHeading("تحليل التخزين", "اعرف أين تذهب كل مساحة على جهازك", Icons.Rounded.Storage, V2Cyan)
        }
        item { StorageHeroCard() }
        item { V2SectionLabel("حسب النوع", trailing = "بيانات معاينة") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .28f) {
                Column {
                    SegmentedBar(storageCategories.map { it.fraction to it.accent }, height = 11)
                    Spacer(Modifier.height(11.dp))
                    storageCategories.forEachIndexed { index, category ->
                        StorageCategoryRow(category)
                        if (index != storageCategories.lastIndex) {
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
        item { V2SectionLabel("أكبر الملفات", trailing = "عرض الكل") }
        item { BigFileRow("فيديو 4K — رحلة الجبال", "2.4 GB", Icons.Rounded.Videocam, V2Pink) }
        item { BigFileRow("أرشيف الصور — 2025", "1.8 GB", Icons.Rounded.PhotoLibrary, V2Violet) }
        item { BigFileRow("نسخة احتياطية قديمة", "1.2 GB", Icons.Rounded.Archive, V2Amber) }
        item { PreviewNote("أحجام الفئات هنا عرض تصميمي وليست قياسًا مباشرًا من الجهاز.") }
    }
}

@Composable
private fun StorageHeroCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .52f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = .67f,
                size = 112.dp,
                stroke = 8.dp,
                colors = listOf(V2Pink, V2Violet, V2Blue, V2Cyan),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText("67%", size = 26.sp, weight = FontWeight.Black)
                    KText("مستخدم", size = 8.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                KText("46.8 GB", size = 22.sp, weight = FontWeight.Black)
                KText("من إجمالي 72.4 GB", size = 8.4.sp, color = V2Soft)
                Spacer(Modifier.height(7.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Check, null, tint = V2Green, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    KText("25.6 GB متاحة", size = 9.sp, weight = FontWeight.SemiBold, color = V2Green)
                }
                Spacer(Modifier.height(5.dp))
                KText("يمكن تحرير حتى 6.2 GB بمراجعة الملفات الكبيرة.", size = 7.8.sp, color = V2Muted)
            }
        }
    }
}

@Composable
private fun StorageCategoryRow(category: StorageCategoryV2) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(category.accent.withAlpha( .12f))
                .border(1.dp, category.accent.withAlpha( .28f), RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(category.icon, null, tint = category.accent, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                KText(category.name, size = 9.6.sp, weight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                KText(category.size, size = 9.2.sp, weight = FontWeight.Bold, color = category.accent)
            }
            Spacer(Modifier.height(4.dp))
            V2ProgressBar(category.fraction, height = 4.dp, colors = listOf(category.accent, category.accent.withAlpha( .55f)))
        }
        Spacer(Modifier.width(8.dp))
        KText(category.percent, size = 7.6.sp, color = V2Muted, modifier = Modifier.width(26.dp), align = TextAlign.End)
    }
}

@Composable
private fun BigFileRow(title: String, size: String, icon: ImageVector, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 9.dp, radius = 15.dp, glow = accent, borderAlpha = .24f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.withAlpha( .12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = accent, modifier = Modifier.size(17.dp))
            }
            Spacer(Modifier.width(8.dp))
            KText(title, size = 9.4.sp, weight = FontWeight.SemiBold, modifier = Modifier.weight(1f), maxLines = 1)
            KText(size, size = 9.4.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Battery
 * ------------------------------------------------------------------ */

@Composable
fun V2BatteryScreen(onBack: () -> Unit, onOpenDetail: () -> Unit, onPro: () -> Unit) {
    var mode by remember { mutableStateOf(1) }
    val modes = listOf("اقتصادي", "متوازن", "أداء")

    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GhostButton("تشخيص البطارية", onOpenDetail, icon = Icons.Rounded.Thermostat, height = 47.dp)
        },
    ) {
        item { PageHeading("توفير البطارية", "تحكم في الاستهلاك وحرارة الجهاز", Icons.Rounded.BatteryFull, V2Green) }
        item { BatteryHeroCard() }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                modes.forEachIndexed { index, label ->
                    AppFilterChip(
                        text = label,
                        active = mode == index,
                        modifier = Modifier.weight(1f),
                        onClick = { mode = index },
                    )
                }
            }
        }
        item { V2SectionLabel("الأكثر استهلاكًا", trailing = "آخر 24 ساعة") }
        item { DrainRow("TikTok", "32%", .82f, V2Pink, AppMark.TikTok) }
        item { DrainRow("YouTube", "24%", .64f, V2Red, AppMark.YouTube) }
        item { DrainRow("WhatsApp", "11%", .32f, V2Green, AppMark.WhatsApp) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 17.dp, glow = V2Cyan, borderAlpha = .28f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Thermostat, null, tint = V2Cyan, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        KText("حرارة الجهاز مستقرة", size = 9.8.sp, weight = FontWeight.Bold)
                        KText("34° مئوية · لا يوجد ارتفاع غير طبيعي", size = 7.8.sp, color = V2Soft)
                    }
                    KText("جيد", size = 8.sp, weight = FontWeight.Bold, color = V2Green)
                }
            }
        }
        item { PreviewNote("نِسب الاستهلاك والوقت المتبقي قيم معاينة وليست قراءة من النظام.") }
    }
}

@Composable
private fun BatteryHeroCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 22.dp,
        glow = V2Green,
        borderAlpha = .52f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = .93f,
                size = 112.dp,
                stroke = 8.dp,
                colors = listOf(V2Green, V2Cyan, V2Blue),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText("93%", size = 28.sp, weight = FontWeight.Black)
                    KText("صحة البطارية", size = 7.6.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Bolt, null, tint = V2Green, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    KText("الوقت المتبقي", size = 9.sp, color = V2Soft)
                }
                KText("14 س 20 د", size = 20.sp, weight = FontWeight.Black)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Thermostat, null, tint = V2Amber, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    KText("34° مئوية", size = 9.sp, weight = FontWeight.SemiBold, color = V2Amber)
                }
                Spacer(Modifier.height(5.dp))
                KText("الشحن السريع غير متصل", size = 7.6.sp, color = V2Muted)
            }
        }
    }
}

@Composable
private fun DrainRow(name: String, percent: String, fraction: Float, accent: Color, mark: AppMark) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 9.dp, radius = 15.dp, glow = accent, borderAlpha = .22f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppLogo(mark, size = 34.dp)
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    KText(name, size = 9.6.sp, weight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    KText(percent, size = 9.2.sp, weight = FontWeight.Bold, color = accent)
                }
                Spacer(Modifier.height(4.dp))
                V2ProgressBar(fraction, height = 4.dp, colors = listOf(accent, accent.withAlpha( .52f)))
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * AI optimization
 * ------------------------------------------------------------------ */

private data class AiAction(
    val title: String,
    val detail: String,
    val gain: String,
    val icon: ImageVector,
    val accent: Color,
)

private val aiActions = listOf(
    AiAction("إغلاق التطبيقات الخلفية", "11 تطبيقًا يستهلك الذاكرة بدون استخدام", "1.2 GB", Icons.Rounded.Memory, V2Cyan),
    AiAction("تحسين بدء التشغيل", "تعطيل 4 تطبيقات تبدأ تلقائيًا", "أسرع 18%", Icons.Rounded.Speed, V2Violet),
    AiAction("تنظيف ذاكرة التخزين", "ملفات مؤقتة قابلة للحذف بأمان", "684 MB", Icons.Rounded.CleaningServices, V2Pink),
    AiAction("تبريد الجهاز", "إيقاف النشاط الثقيل مؤقتًا", "-4°", Icons.Rounded.Thermostat, V2Amber),
)

@Composable
fun V2AiScreen(onBack: () -> Unit, onOpenDetail: () -> Unit, onPro: () -> Unit) {
    val applied = remember { mutableStateOf(setOf<Int>()) }
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton(
                "تطبيق التحسينات المحددة (${applied.value.size})",
                onOpenDetail,
                icon = Icons.Rounded.AutoAwesome,
                trailingChevron = true,
            )
        },
    ) {
        item { PageHeading("تحسين الأداء", "توصيات ذكية مبنية على حالة الجهاز", Icons.Rounded.AutoAwesome, V2Pink) }
        item { AiScoreCard() }
        item { V2SectionLabel("التوصيات", trailing = "4 اقتراحات") }
        items(aiActions.size) { index ->
            val action = aiActions[index]
            val isApplied = applied.value.contains(index)
            AiActionRow(
                action = action,
                applied = isApplied,
                onToggle = {
                    applied.value = if (isApplied) applied.value - index else applied.value + index
                },
            )
        }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 17.dp, glow = V2GoldText, borderAlpha = .30f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = V2Amber, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        KText("تحسينات Pro الأعمق", size = 9.8.sp, weight = FontWeight.Bold, color = V2GoldText)
                        KText("تحليل مستمر وتوصيات مخصصة حسب استخدامك.", size = 7.8.sp, color = V2Soft)
                    }
                    Icon(Icons.Rounded.ChevronLeft, null, tint = V2Amber, modifier = Modifier.size(17.dp))
                }
            }
        }
        item { PreviewNote("الأرقام هنا معاينة تصميمية، لا يتم تعديل إعدادات النظام فعليًا في هذا البناء.") }
    }
}

@Composable
private fun AiScoreCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 22.dp,
        glow = V2Pink,
        borderAlpha = .52f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = .94f,
                size = 108.dp,
                stroke = 8.dp,
                colors = listOf(V2Cyan, V2Blue, V2Violet, V2Pink),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText("94", size = 30.sp, weight = FontWeight.Black)
                    KText("نقطة الأداء", size = 7.4.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Check, null, tint = V2Green, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    KText("أداء ممتاز", size = 11.sp, weight = FontWeight.Bold, color = V2Green)
                }
                Spacer(Modifier.height(4.dp))
                KText("هناك 4 تحسينات متاحة قد ترفع النتيجة إلى 98.", size = 8.6.sp, color = V2Soft)
                Spacer(Modifier.height(6.dp))
                V2ProgressBar(.94f, height = 5.dp, colors = listOf(V2Pink, V2Violet, V2Cyan))
            }
        }
    }
}

@Composable
private fun AiActionRow(action: AiAction, applied: Boolean, onToggle: () -> Unit) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 17.dp,
        glow = if (applied) V2Green else action.accent,
        borderAlpha = .34f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(action.icon, if (applied) V2Green else action.accent, size = 36.dp, iconSize = 18.dp)
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(action.title, size = 9.8.sp, weight = FontWeight.Bold)
                KText(action.detail, size = 7.4.sp, color = V2Muted, maxLines = 1)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                KText(action.gain, size = 8.6.sp, weight = FontWeight.Bold, color = V2Cyan)
                Spacer(Modifier.height(3.dp))
                AppFilterChip(
                    text = if (applied) "مطبق" else "تطبيق",
                    active = applied,
                    onClick = onToggle,
                )
            }
        }
    }
}

@Composable
private fun AiImpactStat(
    value: String,
    label: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    V2GlassCard(modifier, padding = 9.dp, radius = 15.dp, glow = accent, borderAlpha = .28f) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(3.dp))
            KText(value, size = 12.sp, weight = FontWeight.Bold)
            KText(label, size = 7.5.sp, color = V2Soft, maxLines = 1, align = TextAlign.Center)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Settings
 * ------------------------------------------------------------------ */

@Composable
fun V2SettingsScreenV2(onBack: () -> Unit) {
    var notifications by remember { mutableStateOf(true) }
    var weekly by remember { mutableStateOf(true) }
    var haptics by remember { mutableStateOf(true) }
    var localAnalytics by remember { mutableStateOf(false) }
    var appLock by remember { mutableStateOf(false) }
    var highQuality by remember { mutableStateOf(true) }

    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = {})
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item { PageHeading("الإعدادات", "تخصيص التطبيق والخصوصية", Icons.Rounded.Settings, V2Violet) }
                item {
                    V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .42f) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(V2Violet, V2Cyan)))
                                    .border(1.dp, Color.White.withAlpha( .30f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                KText("K", size = 19.sp, weight = FontWeight.Black)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                KText("KNOUX ID", size = 11.sp, weight = FontWeight.Bold)
                                KText("وضع الضيف · كل الأدوات محلية", size = 7.8.sp, color = V2Soft)
                            }
                            SummaryPill("ضيف", V2Cyan)
                        }
                    }
                }
                item { V2SectionLabel("عام") }
                item {
                    V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 18.dp, glow = V2Cyan, borderAlpha = .26f) {
                        Column {
                            SettingNavRow(Icons.Rounded.Language, "اللغة", "العربية", V2Cyan)
                            HairLine()
                            SettingNavRow(Icons.Rounded.DarkMode, "المظهر", "الوضع الداكن", V2Violet)
                            HairLine()
                            SettingNavRow(Icons.Rounded.Palette, "جودة العرض", if (highQuality) "عالية" else "موفرة", V2Pink)
                            HairLine()
                            SettingToggleRow(Icons.Rounded.Notifications, "الإشعارات", "تنبيهات الفحص والتقارير", notifications, V2Amber) { notifications = it }
                        }
                    }
                }
                item { V2SectionLabel("الخصوصية والأمان") }
                item {
                    V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 18.dp, glow = V2Green, borderAlpha = .26f) {
                        Column {
                            SettingToggleRow(Icons.Rounded.Report, "تحليلات محلية", "إحصاءات على الجهاز فقط", localAnalytics, V2Green) { localAnalytics = it }
                            HairLine()
                            SettingToggleRow(Icons.Rounded.Lock, "قفل التطبيق", "بصمة قبل فتح الأدوات الحساسة", appLock, V2Cyan) { appLock = it }
                            HairLine()
                            SettingToggleRow(Icons.Rounded.CloudOff, "منع الرفع السحابي", "لا تُرسل أي بيانات خارج الجهاز", true, V2Violet) { }
                        }
                    }
                }
                item { V2SectionLabel("التقارير") }
                item {
                    V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 18.dp, glow = V2Pink, borderAlpha = .26f) {
                        Column {
                            SettingToggleRow(Icons.Rounded.Timeline, "التقرير الأسبوعي", "ملخص كل يوم أحد", weekly, V2Pink) { weekly = it }
                            HairLine()
                            SettingToggleRow(Icons.Rounded.Settings, "الاهتزاز", "ردود فعل لمسية خفيفة", haptics, V2Amber) { haptics = it }
                            HairLine()
                            SettingNavRow(Icons.Rounded.Backup, "نسخة احتياطية للإعدادات", "غير مفعلة", V2Muted)
                        }
                    }
                }
                item { V2SectionLabel("حول") }
                item {
                    var versionTapCount by remember { mutableStateOf(0) }
                    var showDevPanel by remember { mutableStateOf(false) }
                    V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 18.dp, glow = V2Violet, borderAlpha = .26f) {
                        Column {
                            InfoLine(
                                label = "الإصدار",
                                value = "1.0.0 (build 2)",
                                accent = Color.White,
                                onClick = {
                                    versionTapCount++
                                    if (versionTapCount >= 5) {
                                        showDevPanel = true
                                        versionTapCount = 0
                                    }
                                }
                            )
                            HairLine()
                            InfoLine("البناء", "معاينة تصميم V2", Color.White)
                            HairLine()
                            SettingNavRow(Icons.Rounded.Description, "سياسة الخصوصية", "محلية بالكامل", V2Cyan)
                            HairLine()
                            SettingNavRow(Icons.Rounded.Info, "شروط الاستخدام", "عرض", V2Muted)
                        }
                    }
                    if (showDevPanel) {
                        DeveloperPanel(onDismiss = { showDevPanel = false })
                    }
                }
                item { PreviewNote("هذه الإعدادات واجهة معاينة؛ لا تغيّر سلوك النظام فعليًا بعد.") }
            }
        }
    }
}

@Composable
private fun SettingNavRow(icon: ImageVector, title: String, value: String, accent: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        V2IconTile(icon, accent, size = 30.dp, iconSize = 15.dp)
        Spacer(Modifier.width(9.dp))
        KText(title, size = 9.8.sp, weight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        KText(value, size = 8.4.sp, color = V2Muted, maxLines = 1)
        Spacer(Modifier.width(3.dp))
        Icon(Icons.Rounded.ChevronLeft, null, tint = V2Soft, modifier = Modifier.size(15.dp))
    }
}

/* ------------------------------------------------------------------ *
 * Deep scan summary
 * ------------------------------------------------------------------ */

@Composable
fun V2ScanSummaryScreen(onBack: () -> Unit, onOpenSmartClean: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton("بدء التنظيف الذكي", onOpenSmartClean, icon = Icons.Rounded.CleaningServices, trailingChevron = true)
        },
    ) {
        item { PageHeading("ملخص الفحص العميق", "5 وحدات · اكتمل الفحص بنسبة 72%", Icons.Rounded.Radar, V2Cyan) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 13.dp, radius = 22.dp, glow = V2Violet, borderAlpha = .52f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    V2NeonRing(.72f, size = 104.dp, stroke = 8.dp, colors = listOf(V2Pink, V2Violet, V2Blue, V2Cyan)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            KText("72%", size = 24.sp, weight = FontWeight.Black)
                            KText("مكتمل", size = 7.6.sp, color = V2Soft)
                        }
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        KText("3.42 GB", size = 21.sp, weight = FontWeight.Black)
                        KText("مساحة قابلة للاستعادة بعد المراجعة", size = 8.2.sp, color = V2Soft)
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            SummaryPill("11 ملفًا مؤقتًا", V2Cyan)
                            SummaryPill("3 مجموعات", V2Violet)
                        }
                    }
                }
            }
        }
        item { V2SectionLabel("الوحدات المفحوصة", trailing = "بيانات معاينة") }
        item { SummaryModuleRow(Icons.Rounded.Storage, "التخزين", "182 GB مستخدمة · 72%", "2.6 GB", V2Cyan) }
        item { SummaryModuleRow(Icons.Rounded.Apps, "التطبيقات", "54 تطبيقًا · 8 ثقيلة", "1.2 GB", V2Violet) }
        item { SummaryModuleRow(Icons.Rounded.Shield, "الخصوصية", "43 تطبيقًا · 3 مخاطر مرتفعة", "5 ملاحظات", V2Pink) }
        item { SummaryModuleRow(Icons.Rounded.Speed, "الأداء", "نقطة 94 · 4 توصيات", "+4 نقاط", V2Green) }
        item { SummaryModuleRow(Icons.Rounded.BatteryFull, "البطارية", "صحة 93% · 34°", "14 س 20 د", V2Amber) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 17.dp, glow = V2Green, borderAlpha = .26f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(7.dp))
                    KText("لن يتم حذف أي ملف قبل عرضه وتأكيدك.", size = 8.2.sp, color = V2Soft)
                }
            }
        }
    }
}

@Composable
private fun SummaryModuleRow(icon: ImageVector, title: String, subtitle: String, value: String, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = accent, borderAlpha = .26f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 35.dp, iconSize = 17.dp)
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 9.8.sp, weight = FontWeight.Bold)
                KText(subtitle, size = 7.5.sp, color = V2Muted)
            }
            KText(value, size = 9.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Residue review
 * ------------------------------------------------------------------ */

private data class ResidueFileV2(val path: String, val size: String, val files: Int, val accent: Color)

private val residueFiles = listOf(
    ResidueFileV2("Android/data/com.tiktok/cache", "1.42 GB", 214, V2Violet),
    ResidueFileV2("Android/data/com.whatsapp/temp", "684 MB", 86, V2Pink),
    ResidueFileV2("Download/apk_old", "356 MB", 12, V2Cyan),
    ResidueFileV2("Android/logs", "78 MB", 41, V2Amber),
    ResidueFileV2(".trash/old_backup", "310 MB", 9, V2Green),
)

@Composable
fun V2ResidueReviewScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val selected = remember { mutableStateOf(setOf(0, 1, 2)) }
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton(
                "حذف ${selected.value.size} مجلدات محددة (2.46 GB)",
                {},
                icon = Icons.Rounded.Delete,
                trailingChevron = true,
            )
        },
    ) {
        item { PageHeading("مراجعة البقايا", "افتح كل مجلد وراجع قبل الحذف", Icons.Rounded.Folder, V2Violet) }
        item { V2SectionLabel("المجلدات المكتشفة", trailing = "2.84 GB") }
        items(residueFiles.size) { index ->
            val item = residueFiles[index]
            val checked = selected.value.contains(index)
            V2GlassCard(
                Modifier.fillMaxWidth().clickable {
                    selected.value = if (checked) selected.value - index else selected.value + index
                },
                padding = 10.dp,
                radius = 16.dp,
                glow = item.accent,
                borderAlpha = if (checked) .50f else .18f,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    V2IconTile(Icons.Rounded.Folder, item.accent, size = 35.dp, iconSize = 17.dp)
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        KText(item.path, size = 8.6.sp, weight = FontWeight.SemiBold, maxLines = 1)
                        KText("${item.files} ملفًا · قابل للمراجعة", size = 7.2.sp, color = V2Muted)
                    }
                    KText(item.size, size = 9.4.sp, weight = FontWeight.Bold, color = item.accent)
                    Spacer(Modifier.width(8.dp))
                    V2Check(checked, item.accent)
                }
            }
        }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 17.dp, glow = V2Amber, borderAlpha = .30f) {
                KText(
                    "لا تحذف مجلدات التطبيقات النشطة. العناصر المحددة هنا بقايا من تطبيقات أُزيلت أو ملفات مؤقتة فقط.",
                    size = 8.sp,
                    lineHeight = 12.sp,
                    color = V2Soft,
                )
            }
        }
        item { PreviewNote("أحجام البقايا ومساراتها بيانات معاينة حتى تُربط بقراءة التخزين الفعلية.") }
    }
}

/* ------------------------------------------------------------------ *
 * Privacy risk inspector
 * ------------------------------------------------------------------ */

@Composable
fun V2PrivacyRiskScreen(onBack: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton("فتح إعدادات الأذونات", {}, icon = Icons.Rounded.Security, trailingChevron = true)
        },
    ) {
        item { PageHeading("تفاصيل مخاطر الخصوصية", "3 مخاطر مرتفعة تحتاج قرارك", Icons.Rounded.Shield, V2Pink) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Pink, borderAlpha = .44f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    V2NeonRing(.86f, size = 88.dp, stroke = 7.dp, colors = listOf(V2Pink, V2Violet, V2Cyan)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            KText("86", size = 22.sp, weight = FontWeight.Black)
                            KText("درجة الخصوصية", size = 6.8.sp, color = V2Soft)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        KText("تحسين بسيط يرفع الدرجة", size = 10.sp, weight = FontWeight.Bold)
                        KText("راجع الأذونات الثلاثة الأكثر خطورة أدناه.", size = 8.sp, color = V2Soft)
                        Spacer(Modifier.height(5.dp))
                        V2ProgressBar(.86f, height = 5.dp, colors = listOf(V2Amber, V2Pink))
                    }
                }
            }
        }
        item { V2SectionLabel("أعلى المخاطر", trailing = "مرتبة حسب الخطورة") }
        item { PrivacyRiskDetailRow(Icons.Rounded.Visibility, "الكاميرا", "3 تطبيقات تصل للكاميرا دائمًا", "مرتفع", V2Pink) }
        item { PrivacyRiskDetailRow(Icons.Rounded.Settings, "الميكروفون", "5 تطبيقات تسجل في الخلفية", "مرتفع", V2Red) }
        item { PrivacyRiskDetailRow(Icons.Rounded.Security, "الموقع الدقيق", "تطبيقان يتابعان الموقع", "متوسط", V2Amber) }
        item { PrivacyRiskDetailRow(Icons.Rounded.Notifications, "الإشعارات", "8 تطبيقات ترسل تنبيهات", "منخفض", V2Green) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 17.dp, glow = V2Green, borderAlpha = .26f) {
                Column {
                    KText("توصية KNOUX AI", size = 9.6.sp, weight = FontWeight.Bold, color = V2Green)
                    Spacer(Modifier.height(3.dp))
                    KText("اجعل إذن الكاميرا «أثناء الاستخدام فقط» للتطبيقات الثلاثة، وأوقف الميكروفون لتطبيقين لا يحتاجانه.", size = 8.sp, lineHeight = 12.sp, color = V2Soft)
                }
            }
        }
        item { PreviewNote("قائمة الأذونات هذه معاينة تصميمية حتى تُربط ببيانات النظام.") }
    }
}

@Composable
private fun PrivacyRiskDetailRow(icon: ImageVector, title: String, subtitle: String, risk: String, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = accent, borderAlpha = .28f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 35.dp, iconSize = 17.dp)
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 9.8.sp, weight = FontWeight.Bold)
                KText(subtitle, size = 7.4.sp, color = V2Muted, maxLines = 1)
            }
            SummaryPill(risk, accent)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Duplicate group review
 * ------------------------------------------------------------------ */

@Composable
fun V2DuplicateGroupScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val selected = remember { mutableStateOf(setOf(1, 2, 3)) }
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton(
                "حذف ${selected.value.size} نسخ مكررة (9.2 MB)",
                {},
                icon = Icons.Rounded.Delete,
                trailingChevron = true,
            )
        },
    ) {
        item { PageHeading("مراجعة مجموعة مكررة", "4 نسخ من الصورة نفسها", Icons.Rounded.PhotoLibrary, V2Violet) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 22.dp, glow = V2Violet, borderAlpha = .48f) {
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        repeat(4) { index ->
                            val checked = selected.value.contains(index)
                            Box(Modifier.weight(1f).height(104.dp)) {
                                MiniMountainThumb(
                                    Modifier.fillMaxSize(),
                                    if (index == 0) V2Amber else V2Violet,
                                    showCity = index % 2 == 0,
                                )
                                if (index == 0) {
                                    Box(
                                        Modifier
                                            .align(Alignment.TopStart)
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.Black.withAlpha( .55f))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CrownGold(size = 11.dp)
                                            Spacer(Modifier.width(2.dp))
                                            KText("الأفضل", size = 6.4.sp, weight = FontWeight.Bold, color = V2GoldText)
                                        }
                                    }
                                }
                                V2Check(
                                    checked = checked,
                                    accent = V2Pink,
                                    onToggle = {
                                        selected.value = if (checked) selected.value - index else selected.value + index
                                    },
                                    modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(9.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.PhotoLibrary, null, tint = V2Cyan, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(5.dp))
                        KText("IMG_2026_0451.jpg · 4.8 MB", size = 8.sp, color = V2Soft)
                        Spacer(Modifier.weight(1f))
                        KText("3 محددة", size = 8.sp, weight = FontWeight.Bold, color = V2Pink)
                    }
                }
            }
        }
        item { V2SectionLabel("تفاصيل المجموعة") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 17.dp, glow = V2Cyan, borderAlpha = .24f) {
                Column {
                    InfoLine("نوع الملف", "صورة JPEG", Color.White)
                    HairLine()
                    InfoLine("الأبعاد", "4000 × 3000", Color.White)
                    HairLine()
                    InfoLine("تاريخ الالتقاط", "12 مايو 2026", Color.White)
                    HairLine()
                    InfoLine("المجلد", "DCIM/Camera", Color.White)
                }
            }
        }
        item { V2SectionLabel("النسخ في المجموعة", trailing = "أصل واحد · 3 نسخ") }
        items(4) { index ->
            val checked = selected.value.contains(index)
            V2GlassCard(
                Modifier.fillMaxWidth().clickable {
                    selected.value = if (checked) selected.value - index else selected.value + index
                },
                padding = 8.dp,
                radius = 14.dp,
                glow = if (index == 0) V2Amber else V2Violet,
                borderAlpha = if (checked) .46f else .18f,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    V2IconTile(
                        Icons.Rounded.PhotoLibrary,
                        if (index == 0) V2Amber else V2Violet,
                        size = 32.dp,
                        iconSize = 15.dp,
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        KText(
                            if (index == 0) "IMG_2026_0451.jpg" else "IMG_2026_0451 ($index).jpg",
                            size = 8.4.sp,
                            weight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                        KText(
                            if (index == 0) "النسخة الأصلية · 4.8 MB" else "نسخة مطابقة · 4.4 MB",
                            size = 7.2.sp,
                            color = V2Muted,
                        )
                    }
                    if (index == 0) {
                        SummaryPill("الأصل", V2Amber)
                        Spacer(Modifier.width(6.dp))
                    }
                    V2Check(checked = checked, accent = V2Pink)
                }
            }
        }
        item { PreviewNote("اختيار النسخة الأفضل إرشادي فقط؛ راجع الصور قبل الحذف.") }
    }
}

/* ------------------------------------------------------------------ *
 * Storage breakdown
 * ------------------------------------------------------------------ */

@Composable
fun V2StorageBreakdownScreen(onBack: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(onBack = onBack, onPro = onPro) {
        item { PageHeading("تفصيل التخزين", "المجلدات الأكثر استهلاكًا", Icons.Rounded.Folder, V2Cyan) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .40f) {
                Column {
                    SegmentedBar(listOf(.34f to V2Violet, .22f to V2Pink, .16f to V2Cyan, .12f to V2Amber, .16f to V2Blue), height = 11)
                    Spacer(Modifier.height(9.dp))
                    KText("46.8 GB مستخدمة من 72.4 GB", size = 9.sp, color = V2Soft)
                }
            }
        }
        item { V2SectionLabel("المجلدات", trailing = "بيانات معاينة") }
        item { FolderBreakdownRow("Android", "18.4 GB", .61f, V2Violet) }
        item { FolderBreakdownRow("DCIM", "12.2 GB", .40f, V2Pink) }
        item { FolderBreakdownRow("Download", "8.6 GB", .28f, V2Cyan) }
        item { FolderBreakdownRow("WhatsApp", "6.1 GB", .20f, V2Green) }
        item { FolderBreakdownRow("Telegram", "4.2 GB", .14f, V2Blue) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 17.dp, glow = V2Amber, borderAlpha = .30f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Download, null, tint = V2Amber, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(7.dp))
                    Column(Modifier.weight(1f)) {
                        KText("مجلد التنزيلات يحتوي 42 ملفًا", size = 9.4.sp, weight = FontWeight.Bold)
                        KText("راجع الملفات القديمة لتحرير 3.1 GB.", size = 7.6.sp, color = V2Soft)
                    }
                    Icon(Icons.Rounded.ChevronLeft, null, tint = V2Soft, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun FolderBreakdownRow(title: String, size: String, fraction: Float, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 15.dp, glow = accent, borderAlpha = .24f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Folder, null, tint = accent, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    KText(title, size = 9.6.sp, weight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    KText(size, size = 9.2.sp, weight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.height(4.dp))
                V2ProgressBar(fraction, height = 4.dp, colors = listOf(accent, accent.withAlpha( .50f)))
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Battery diagnostics
 * ------------------------------------------------------------------ */

@Composable
fun V2BatteryDiagnosticsScreen(onBack: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(onBack = onBack, onPro = onPro) {
        item { PageHeading("تشخيص البطارية", "حالة الخلايا والاستهلاك", Icons.Rounded.Thermostat, V2Green) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 13.dp, radius = 22.dp, glow = V2Green, borderAlpha = .48f) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        V2NeonRing(.93f, size = 96.dp, stroke = 7.dp, colors = listOf(V2Green, V2Cyan, V2Blue)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                KText("93%", size = 23.sp, weight = FontWeight.Black)
                                KText("صحة", size = 7.4.sp, color = V2Soft)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            InfoLine("الحالة", "جيدة", V2Green)
                            InfoLine("دورات الشحن", "284 دورة", Color.White)
                            InfoLine("السعة التصميمية", "5000 mAh", Color.White)
                            InfoLine("الحرارة", "34° مئوية", V2Amber)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    MiniDrainChart()
                    Spacer(Modifier.height(4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("12ص", "3م", "6م", "9م", "12م").forEach {
                            KText(it, size = 6.8.sp, color = V2Muted)
                        }
                    }
                }
            }
        }
        item { V2SectionLabel("ملاحظات التشخيص", trailing = "آخر 24 ساعة") }
        item { DiagnosticNote(Icons.Rounded.Check, "لا يوجد تدهور غير طبيعي", "السعة الحالية 91% من التصميمية.", V2Green) }
        item { DiagnosticNote(Icons.Rounded.WarningAmber, "ارتفاع حرارة مؤقت", "حدث أثناء الشحن السريع لمدة 12 دقيقة.", V2Amber) }
        item { DiagnosticNote(Icons.Rounded.Bolt, "الشحن السريع يعمل", "من 20% إلى 80% خلال 42 دقيقة.", V2Cyan) }
        item { PreviewNote("بيانات البطارية معاينة تصميمية وليست قراءة من النظام.") }
    }
}

@Composable
private fun MiniDrainChart() {
    Canvas(Modifier.fillMaxWidth().height(72.dp)) {
        val points = listOf(.82f, .78f, .74f, .70f, .66f, .62f, .58f, .55f, .52f, .48f, .45f, .42f)
        val step = size.width / (points.size - 1)
        var previous = Offset(0f, size.height * (1f - points.first()))
        points.forEachIndexed { index, value ->
            val current = Offset(index * step, size.height * (1f - value))
            drawLine(
                brush = Brush.horizontalGradient(listOf(V2Green, V2Cyan, V2Pink)),
                start = previous,
                end = current,
                strokeWidth = 2.dp.toPx(),
            )
            drawCircle(V2Cyan, radius = 1.8.dp.toPx(), center = current)
            previous = current
        }
    }
}

@Composable
private fun DiagnosticNote(icon: ImageVector, title: String, subtitle: String, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = accent, borderAlpha = .26f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 9.6.sp, weight = FontWeight.Bold)
                KText(subtitle, size = 7.6.sp, color = V2Soft)
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * AI recommendations detail
 * ------------------------------------------------------------------ */

@Composable
fun V2AiRecommendationsScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val applied = remember { mutableStateOf(setOf<Int>()) }
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton("تطبيق الكل (${aiActions.size})", {}, icon = Icons.Rounded.AutoAwesome)
        },
    ) {
        item { PageHeading("كل التوصيات", "خطوات مرتبة حسب الأثر على الأداء", Icons.Rounded.AutoAwesome, V2Pink) }
        items(aiActions.size) { index ->
            val action = aiActions[index]
            AiActionRow(
                action = action,
                applied = applied.value.contains(index),
                onToggle = {
                    applied.value = if (applied.value.contains(index)) applied.value - index else applied.value + index
                },
            )
        }
        item { V2SectionLabel("الأثر المتوقع", trailing = "بعد التطبيق") }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                AiImpactStat("1.2 GB", "ذاكرة حرة", Icons.Rounded.Memory, V2Cyan, Modifier.weight(1f))
                AiImpactStat("+18%", "سرعة البدء", Icons.Rounded.Speed, V2Violet, Modifier.weight(1f))
                AiImpactStat("684 MB", "مساحة", Icons.Rounded.CleaningServices, V2Pink, Modifier.weight(1f))
            }
        }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 17.dp, glow = V2Violet, borderAlpha = .26f) {
                Column {
                    KText("كيف يحسب KNOUX النتيجة؟", size = 9.6.sp, weight = FontWeight.Bold)
                    Spacer(Modifier.height(3.dp))
                    KText("يجمع التطبيق بين الذاكرة الحرة، نشاط الخلفية، حرارة الجهاز، ومساحة التخزين المتاحة.", size = 8.sp, lineHeight = 12.sp, color = V2Soft)
                }
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Performance report detail
 * ------------------------------------------------------------------ */

@Composable
fun V2PerformanceReportScreen(onBack: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(onBack = onBack, onPro = onPro) {
        item { PageHeading("تقرير الأداء", "ملخص آخر 7 أيام", Icons.Rounded.Report, V2Cyan) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 13.dp, radius = 22.dp, glow = V2Cyan, borderAlpha = .50f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    V2NeonRing(.92f, size = 100.dp, stroke = 8.dp, colors = listOf(V2Pink, V2Violet, V2Blue, V2Cyan)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            KText("92", size = 26.sp, weight = FontWeight.Black)
                            KText("نقطة", size = 7.4.sp, color = V2Soft)
                        }
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        KText("+32% تحسن الأداء", size = 15.sp, weight = FontWeight.Black, color = V2Cyan)
                        KText("مقارنة بالأسبوع الماضي", size = 8.2.sp, color = V2Soft)
                        Spacer(Modifier.height(6.dp))
                        KText("31 عملية تنظيف · 7 إصلاحات خصوصية · 26.4 GB مستعادة", size = 7.8.sp, lineHeight = 11.sp, color = V2Muted)
                    }
                }
            }
        }
        item { V2SectionLabel("التطور اليومي", trailing = "بيانات معاينة") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .30f) {
                Column {
                    BarTrendChart()
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("الجمعة", "الخميس", "الأربعاء", "الثلاثاء", "الاثنين", "الأحد", "السبت").forEach {
                            KText(it, size = 6.8.sp, color = V2Muted)
                        }
                    }
                }
            }
        }
        item { ReportDetailRow("المساحة المستعادة", "26.4 GB", Icons.Rounded.Storage, V2Cyan) }
        item { ReportDetailRow("التطبيقات المحسنة", "12 تطبيقًا", Icons.Rounded.Apps, V2Violet) }
        item { ReportDetailRow("أذونات مُعدلة", "7 أذونات", Icons.Rounded.Shield, V2Pink) }
        item { ReportDetailRow("متوسط الحرارة", "34° مئوية", Icons.Rounded.Thermostat, V2Amber) }
        item { PreviewNote("القيم معاينة تصميمية، وستُستبدل ببيانات فعلية عند تفعيل جمع القياسات.") }
    }
}

@Composable
private fun BarTrendChart() {
    // RTL time axis: the newest day sits on the left, the oldest on the right.
    val values = listOf(.42f, .55f, .51f, .66f, .60f, .76f, .90f).reversed()
    val accents = listOf(V2Cyan, V2Blue, V2Violet, V2Pink, V2Violet, V2Blue, V2Cyan)
    Canvas(Modifier.fillMaxWidth().height(88.dp)) {
        val gap = size.width * .022f
        val barWidth = (size.width - gap * 6) / 7f
        values.forEachIndexed { index, value ->
            val h = size.height * .85f * value
            val accent = accents[index % accents.size]
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(accent.withAlpha( if (index == 0) 1f else .82f), accent.withAlpha( .18f))
                ),
                topLeft = Offset(index * (barWidth + gap) + barWidth * .14f, size.height - h),
                size = androidx.compose.ui.geometry.Size(barWidth * .72f, h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth * .36f),
            )
        }
    }
}

@Composable
private fun ReportDetailRow(title: String, value: String, icon: ImageVector, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 9.dp, radius = 15.dp, glow = accent, borderAlpha = .24f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 33.dp, iconSize = 16.dp)
            Spacer(Modifier.width(9.dp))
            KText(title, size = 9.4.sp, weight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            KText(value, size = 9.4.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Developer Panel (5-tap on version)
 * ------------------------------------------------------------------ */

@Composable
private fun DeveloperPanel(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val proActive = LocalProActive.current
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.withAlpha( .75f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        V2GlassCard(
            Modifier
                .width(340.dp)
                .padding(20.dp)
                .clickable(onClick = {}),
            padding = 20.dp,
            radius = 24.dp,
            glow = V2Amber,
            borderAlpha = .65f,
        ) {
            Column(Modifier.width(300.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    CrownGold(size = 28.dp, color = V2Amber)
                    Spacer(Modifier.width(8.dp))
                    KText("لوحة المطور", size = 18.sp, weight = FontWeight.Black, color = V2Amber)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Rounded.Close, null, tint = V2Soft, modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onDismiss)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.withAlpha( .3f))
                        .border(1.dp, V2Amber.withAlpha( .3f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                ) {
                    val proStatusText = if (proActive) "مفعّل ✓" else "معطل"
                    val proStatusColor = if (proActive) V2Green else V2Amber
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DevRow("حالة Pro", proStatusText, proStatusColor)
                        DevRow("إصدار التطبيق", "1.0.0 (build 2)", V2Cyan)
                        DevRow("نموذج التصميم", "V2 (Arabic-first)", V2Violet)
                        DevRow("الخط", "KnouxArabic", V2Pink)
                        DividerLine()
                        DevRow("تفعيل Pro محلي", "", V2Amber, action = {
                            KnouxProState.setActive(context, !proActive)
                            onDismiss()
                        })
                        DevRow("إعادة تعيين Onboarding", "", V2Pink, action = {
                            markKnouxOnboardingSeen(context, false)
                            onDismiss()
                        })
                        DevRow("إعادة تعيين جميع الإعدادات", "", V2Red, action = {
                            // Reset all SharedPreferences
                            context.applicationContext.getSharedPreferences("knoux_pro_v1", Context.MODE_PRIVATE).edit().clear().commit()
                            context.applicationContext.getSharedPreferences("knoux_prefs", Context.MODE_PRIVATE).edit().clear().commit()
                            onDismiss()
                        })
                    }
                }
                Spacer(Modifier.height(8.dp))
                KText(
                    "اضغط على الإصدار 5 مرات لفتح هذه اللوحة",
                    size = 7.5.sp,
                    color = V2Muted,
                    align = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun DevRow(label: String, value: String, accent: Color, action: (() -> Unit)? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .then(if (action != null) Modifier
                .fillMaxWidth()
                .clickable(onClick = action)
                .background(Color.White.withAlpha( .03f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            else Modifier.fillMaxWidth().padding(vertical = 6.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KText(label, size = 9.sp, color = V2Soft, weight = FontWeight.Medium)
        Spacer(Modifier.weight(1f))
        if (value.isNotBlank()) {
            KText(value, size = 9.sp, weight = FontWeight.Bold, color = accent)
        }
        if (action != null) {
            Icon(Icons.Rounded.ChevronLeft, null, tint = accent, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun DividerLine() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.withAlpha( .1f)))
}

/* ------------------------------------------------------------------ *
 * Large Files
 * ------------------------------------------------------------------ */

private data class LargeFileV2(val name: String, val size: String, val path: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val accent: Color)

private val largeFiles = listOf(
    LargeFileV2("فيديو 4K — رحلة الجبال.mp4", "2.4 GB", "DCIM/Camera", Icons.Rounded.Videocam, V2Pink),
    LargeFileV2("أرشيف الصور — 2025.zip", "1.8 GB", "Download", Icons.Rounded.Archive, V2Violet),
    LargeFileV2("نسخة احتياطية قديمة.ab", "1.2 GB", "Android/backup", Icons.Rounded.Backup, V2Amber),
    LargeFileV2("فيلم طويل.mkv", "980 MB", "DCIM/Movies", Icons.Rounded.Videocam, V2Pink),
    LargeFileV2("مستندات العمل.pdf", "450 MB", "Documents", Icons.Rounded.Description, V2Cyan),
    LargeFileV2("لعبة كبيرة.apk", "1.1 GB", "Download", Icons.Rounded.Folder, V2Green),
    LargeFileV2("تسجيل صوتي طويل.m4a", "320 MB", "Recordings", Icons.Rounded.Audiotrack, V2Blue),
    LargeFileV2("مجموعة صور RAW", "850 MB", "DCIM/Camera", Icons.Rounded.PhotoLibrary, V2Violet),
)

@Composable
fun V2LargeFilesScreen(onBack: () -> Unit, onPro: () -> Unit) {
    var selected by remember { mutableStateOf(setOf<Int>()) }
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton(
                "حذف ${selected.size} ملفات محددة",
                {},
                icon = Icons.Rounded.Delete,
                trailingChevron = true,
            )
        },
    ) {
        item { PageHeading("الملفات الكبيرة", "أكبر الملفات التي تستهلك مساحة", Icons.Rounded.Storage, V2Amber) }
        item { V2SectionLabel("قائمة الملفات", trailing = "${largeFiles.size} ملف") }
        items(largeFiles.size) { index ->
            val item = largeFiles[index]
            val checked = selected.contains(index)
            V2GlassCard(
                Modifier.fillMaxWidth().clickable {
                    selected = if (checked) selected - index else selected + index
                },
                padding = 10.dp,
                radius = 16.dp,
                glow = item.accent,
                borderAlpha = if (checked) .50f else .18f,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(item.accent.withAlpha(.12f))
                            .border(1.dp, item.accent.withAlpha(.32f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(item.icon, null, tint = item.accent, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        KText(item.name, size = 9.4.sp, weight = FontWeight.SemiBold, maxLines = 1)
                        KText(item.path, size = 7.2.sp, color = V2Muted, maxLines = 1)
                    }
                    KText(item.size, size = 9.4.sp, weight = FontWeight.Bold, color = item.accent)
                    Spacer(Modifier.width(8.dp))
                    V2Check(checked, item.accent, onToggle = { selected = if (checked) selected - index else selected + index })
                }
            }
        }
        item { PreviewNote("أحجام الملفات ومساراتها بيانات معاينة حتى تُربط بقراءة التخزين الفعلية.") }
    }
}

/* ------------------------------------------------------------------ *
 * Old Backups
 * ------------------------------------------------------------------ */

private data class OldBackupV2(val name: String, val date: String, val size: String, val path: String, val accent: Color)

private val oldBackups = listOf(
    OldBackupV2("نسخة نظام 2025", "12 مايو 2025", "1.2 GB", "Android/backup/full_2025", V2Amber),
    OldBackupV2("نسخة تطبيقات واتساب", "3 مارس 2025", "850 MB", "Android/backup/whatsapp", V2Violet),
    OldBackupV2("نسخة صور Google", "1 يناير 2025", "620 MB", "Android/backup/photos", V2Cyan),
    OldBackupV2("نسخة إعدادات النظام", "15 يونيو 2025", "150 MB", "Android/backup/settings", V2Green),
)

@Composable
fun V2OldBackupsScreen(onBack: () -> Unit, onPro: () -> Unit) {
    var selected by remember { mutableStateOf(setOf<Int>()) }
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton(
                "حذف ${selected.size} نسخ احتياطية",
                {},
                icon = Icons.Rounded.Delete,
                trailingChevron = true,
            )
        },
    ) {
        item { PageHeading("النسخ الاحتياطية القديمة", "مراجعة وحذف النسخ غير المستخدمة", Icons.Rounded.Backup, V2Amber) }
        item { V2SectionLabel("النسخ المتاحة", trailing = "${oldBackups.size} نسخة") }
        items(oldBackups.size) { index ->
            val item = oldBackups[index]
            val checked = selected.contains(index)
            V2GlassCard(
                Modifier.fillMaxWidth().clickable {
                    selected = if (checked) selected - index else selected + index
                },
                padding = 10.dp,
                radius = 16.dp,
                glow = item.accent,
                borderAlpha = if (checked) .50f else .18f,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(item.accent.withAlpha(.12f))
                            .border(1.dp, item.accent.withAlpha(.32f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Backup, null, tint = item.accent, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        KText(item.name, size = 9.4.sp, weight = FontWeight.SemiBold, maxLines = 1)
                        KText("${item.date} · ${item.path}", size = 7.2.sp, color = V2Muted, maxLines = 1)
                    }
                    KText(item.size, size = 9.4.sp, weight = FontWeight.Bold, color = item.accent)
                    Spacer(Modifier.width(8.dp))
                    V2Check(checked, item.accent, onToggle = { selected = if (checked) selected - index else selected + index })
                }
            }
        }
        item { PreviewNote("حذف النسخ الاحتياطية لا يسترجع المساحة فوراً حتى يتم فراغ سلة المهملات.") }
    }
}

/* ------------------------------------------------------------------ *
 * System Logs
 * ------------------------------------------------------------------ */

private data class SystemLogV2(val app: String, val level: String, val message: String, val time: String, val accent: Color)

private val systemLogs = listOf(
    SystemLogV2("النظام", "معلومات", "تم تشغيل الجهاز بنجاح", "منذ 4 ساعات", V2Cyan),
    SystemLogV2("واتساب", "تحذير", "فشل في مزامنة النسخة الاحتياطية", "منذ 3 ساعات", V2Amber),
    SystemLogV2("التيك توك", "خطأ", "توقف التطبيق بشكل غير متوقع", "منذ ساعتين", V2Red),
    SystemLogV2("المتصفح", "معلومات", "تم تحديث ملفات الكاش", "منذ ساعة", V2Green),
    SystemLogV2("النظام", "معلومات", "تم تثبيت تحديث أمني", "منذ 30 دقيقة", V2Cyan),
    SystemLogV2("اليوتيوب", "تحذير", "استهلاك بيانات مرتفع", "منذ 15 دقيقة", V2Amber),
    SystemLogV2("الصور", "معلومات", "تم نسخ الصور احتياطياً", "منذ 10 دقائق", V2Violet),
    SystemLogV2("النظام", "خطأ", "فشل في قراءة بطاقة الذاكرة", "منذ 5 دقائق", V2Red),
)

@Composable
fun V2SystemLogsScreen(onBack: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
    ) {
        item { PageHeading("سجلات النظام", "عرض سجلات التطبيقات والنظام", Icons.Rounded.Description, V2Cyan) }
        item { V2SectionLabel("آخر السجلات", trailing = "${systemLogs.size} سجل") }
        items(systemLogs.size) { index ->
            val log = systemLogs[index]
            V2GlassCard(
                Modifier.fillMaxWidth(),
                padding = 10.dp,
                radius = 16.dp,
                glow = log.accent,
                borderAlpha = .24f,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                            .background(log.accent.withAlpha(.12f))
                            .border(1.dp, log.accent.withAlpha(.32f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            when (log.level) {
                                "خطأ" -> Icons.Rounded.Report
                                "تحذير" -> Icons.Rounded.WarningAmber
                                else -> Icons.Rounded.Info
                            },
                            null,
                            tint = log.accent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        KText(log.app, size = 9.4.sp, weight = FontWeight.SemiBold, maxLines = 1)
                        KText(log.message, size = 7.6.sp, color = V2Soft, maxLines = 1)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        KText(log.time, size = 7.6.sp, color = V2Muted)
                        Box(
                            Modifier.clip(RoundedCornerShape(50))
                                .background(log.accent.withAlpha(.12f))
                                .border(1.dp, log.accent.withAlpha(.32f), RoundedCornerShape(50))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            KText(log.level, size = 7.sp, weight = FontWeight.Bold, color = log.accent)
                        }
                    }
                }
            }
        }
        item { PreviewNote("السجلات هنا بيانات معاينة حتى تُربط بقراءة النظام الفعلية.") }
    }
}

/* ------------------------------------------------------------------ *
 * Download Cleanup
 * ------------------------------------------------------------------ */

private data class DownloadFileV2(val name: String, val size: String, val type: String, val accent: Color)

private val downloadFiles = listOf(
    DownloadFileV2("مستند العمل.docx", "2.4 MB", "مستند", V2Cyan),
    DownloadFileV2("صورة journey.jpg", "3.2 MB", "صورة", V2Pink),
    DownloadFileV2("تطبيق الألعاب.apk", "45.6 MB", "تطبيق", V2Green),
    DownloadFileV2("مقطع الرحلة.mp4", "120 MB", "فيديو", V2Violet),
    DownloadFileV2("ملف Musik.mp3", "5.4 MB", "صوت", V2Amber),
    DownloadFileV2("أرشيف bestanden.zip", "15.2 MB", "أرشيف", V2Blue),
    DownloadFileV2("كتاب تعليمي.pdf", "8.9 MB", "مستند", V2Cyan),
    DownloadFileV2("تطبيق التوصيل.apk", "32.1 MB", "تطبيق", V2Green),
)

@Composable
fun V2DownloadCleanupScreen(onBack: () -> Unit, onPro: () -> Unit) {
    var selected by remember { mutableStateOf(setOf<Int>()) }
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton(
                "حذف ${selected.size} ملفات من التنزيلات",
                {},
                icon = Icons.Rounded.Delete,
                trailingChevron = true,
            )
        },
    ) {
        item { PageHeading("تنظيف التنزيلات", "مراجعة وحذف الملفات المحملة", Icons.Rounded.Download, V2Cyan) }
        item { V2SectionLabel("ملفات التنزيل", trailing = "${downloadFiles.size} ملف") }
        items(downloadFiles.size) { index ->
            val item = downloadFiles[index]
            val checked = selected.contains(index)
            V2GlassCard(
                Modifier.fillMaxWidth().clickable {
                    selected = if (checked) selected - index else selected + index
                },
                padding = 10.dp,
                radius = 16.dp,
                glow = item.accent,
                borderAlpha = if (checked) .50f else .18f,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(item.accent.withAlpha(.12f))
                            .border(1.dp, item.accent.withAlpha(.32f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Folder, null, tint = item.accent, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        KText(item.name, size = 9.4.sp, weight = FontWeight.SemiBold, maxLines = 1)
                        KText(item.type, size = 7.2.sp, color = V2Muted, maxLines = 1)
                    }
                    KText(item.size, size = 9.4.sp, weight = FontWeight.Bold, color = item.accent)
                    Spacer(Modifier.width(8.dp))
                    V2Check(checked, item.accent, onToggle = { selected = if (checked) selected - index else selected + index })
                }
            }
        }
        item { PreviewNote("يمكن استعادة الملفات المحذوفة من سلة المهملات خلال 30 يوماً.") }
    }
}

/* ------------------------------------------------------------------ *
 * File Explorer
 * ------------------------------------------------------------------ */

private data class FolderItemV2(val name: String, val items: String, val accent: Color)

private val folders = listOf(
    FolderItemV2("Android", "124 عنصراً", V2Violet),
    FolderItemV2("DCIM", "89 عنصراً", V2Pink),
    FolderItemV2("Download", "67 عنصراً", V2Cyan),
    FolderItemV2("WhatsApp", "45 عنصراً", V2Green),
    FolderItemV2("Telegram", "38 عنصراً", V2Blue),
    FolderItemV2("Documents", "23 عنصراً", V2Amber),
    FolderItemV2("Music", "15 عنصراً", V2Pink),
    FolderItemV2("Movies", "12 عنصراً", V2Violet),
)

@Composable
fun V2FileExplorerScreen(onBack: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
    ) {
        item { PageHeading("مستكشف الملفات", "تصفح الملفات والمجلدات على جهازك", Icons.Rounded.Folder, V2Cyan) }
        item { V2SectionLabel("المجلدات الرئيسية", trailing = "${folders.size} مجلد") }
        items(folders.size) { index ->
            val folder = folders[index]
            V2GlassCard(
                Modifier.fillMaxWidth().clickable { /* navigate into folder */ },
                padding = 10.dp,
                radius = 16.dp,
                glow = folder.accent,
                borderAlpha = .24f,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(folder.accent.withAlpha(.12f))
                            .border(1.dp, folder.accent.withAlpha(.32f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Folder, null, tint = folder.accent, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        KText(folder.name, size = 9.6.sp, weight = FontWeight.SemiBold)
                        KText(folder.items, size = 7.4.sp, color = V2Soft)
                    }
                    Icon(Icons.Rounded.ChevronLeft, null, tint = V2Soft, modifier = Modifier.size(15.dp))
                }
            }
        }
        item { PreviewNote("تصفح الملفات يتطلب إذن الوصول إلى التخزين من النظام.") }
    }
}

/* ------------------------------------------------------------------ *
 * Device Info
 * ------------------------------------------------------------------ */

@Composable
fun V2DeviceInfoScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val packageInfo = remember { context.packageManager.getPackageInfo(context.packageName, 0) }
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
    ) {
        item { PageHeading("معلومات الجهاز", "عرض مواصفات الجهاز وإصدار النظام", Icons.Rounded.Android, V2Violet) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .42f) {
                Column {
                    InfoLine("اسم التطبيق", "KNOUX AI Pro", Color.White)
                    HairLine()
                    InfoLine("الإصدار", "${packageInfo.versionName ?: "1.0.0"} (build ${packageInfo.versionCode})", Color.White)
                    HairLine()
                    InfoLine(label = "معرّف الحزمة", value = "com.knoux.ai.pro", accent = Color.White)
                    HairLine()
                    InfoLine("نظام التشغيل", "Android", Color.White)
                    HairLine()
                    InfoLine("واجهة المستخدم", "Compose Material 3", Color.White)
                    HairLine()
                    InfoLine("الاتجاه", "العربية (RTL)", Color.White)
                    HairLine()
                    InfoLine("النموذج الأولي", "V2 (Arabic-first)", Color.White)
                }
            }
        }
        item { PreviewNote("معلومات الجهاز 여기 بيانات معاينة حتى تُربط بقراءة النظام الفعلية.") }
    }
}

/* ------------------------------------------------------------------ *
 * Network Scan
 * ------------------------------------------------------------------ */

@Composable
fun V2NetworkScanScreen(onBack: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
    ) {
        item { PageHeading("فحص الشبكة", "فحص الاتصالات واستهلاك البيانات", Icons.Rounded.Wifi, V2Cyan) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .52f) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KText("الشبكة المتصلة", size = 9.6.sp, weight = FontWeight.SemiBold, color = V2Soft)
                        Spacer(Modifier.weight(1f))
                        KText("Wi-Fi · آمن", size = 9.2.sp, weight = FontWeight.Bold, color = V2Green)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KText("استهلاك البيانات", size = 9.6.sp, weight = FontWeight.SemiBold, color = V2Soft)
                        Spacer(Modifier.weight(1f))
                        KText("2.4 GB هذا الشهر", size = 9.2.sp, weight = FontWeight.Bold, color = V2Cyan)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KText("أكثر التطبيقات استهلاكاً", size = 9.6.sp, weight = FontWeight.SemiBold, color = V2Soft)
                        Spacer(Modifier.weight(1f))
                        KText("اليوتيوب · 45%", size = 9.2.sp, weight = FontWeight.Bold, color = V2Pink)
                    }
                }
            }
        }
        item { V2SectionLabel("تطبيقات استهلاك البيانات", trailing = "آخر 7 أيام") }
        item {
            NetworkRow("اليوتيوب", "45%", V2Pink)
            NetworkRow("التيك توك", "28%", V2Violet)
            NetworkRow("الفيسبوك", "15%", V2Blue)
            NetworkRow("إنستغرام", "12%", V2Pink)
        }
        item { PreviewNote("بيانات الشبكة هنا معاينة تصميمية حتى تُربط بقراءة النظام الفعلية.") }
    }
}

@Composable
private fun NetworkRow(name: String, percent: String, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 8.dp, radius = 12.dp, glow = accent, borderAlpha = .20f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            KText(name, size = 9.sp, weight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            KText(percent, size = 8.8.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Security Scan
 * ------------------------------------------------------------------ */

@Composable
fun V2SecurityScanScreen(onBack: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
    ) {
        item { PageHeading("فحص الأمان", "فحص الثغرات والمخاطر الأمنية", Icons.Rounded.Shield, V2Pink) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Pink, borderAlpha = .52f) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KText("مستوى الأمان", size = 9.6.sp, weight = FontWeight.SemiBold, color = V2Soft)
                        Spacer(Modifier.weight(1f))
                        KText("82%", size = 20.sp, weight = FontWeight.Black, color = V2Green)
                    }
                    Spacer(Modifier.height(8.dp))
                    V2ProgressBar(.82f, height = 5.dp, colors = listOf(V2Pink, V2Violet, V2Cyan))
                }
            }
        }
        item { V2SectionLabel("نتائج الفحص", trailing = "4 فحوصات مكتملة") }
        item {
            SecurityRow("الثغرات المعروفة", "لا توجد ثغرات حرجة", V2Green, Icons.Rounded.Check)
            SecurityRow("التطبيقات الضارة", "لا تطبيقات مشبوهة", V2Green, Icons.Rounded.Check)
            SecurityRow("شبكة Wi-Fi", "شبكة آمنة ومشفرة", V2Green, Icons.Rounded.Check)
            SecurityRow("إعدادات القفل", "قفل الشاشة مفعل", V2Green, Icons.Rounded.Check)
        }
        item { PreviewNote("فحص الأمان هنا بيانات معاينة حتى تُربط بقراءة النظام الفعلية.") }
    }
}

@Composable
private fun SecurityRow(title: String, status: String, accent: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 8.dp, radius = 12.dp, glow = accent, borderAlpha = .20f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            KText(title, size = 9.sp, weight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            KText(status, size = 8.8.sp, color = V2Soft)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Notification Manager
 * ------------------------------------------------------------------ */

@Composable
fun V2NotificationManagerScreen(onBack: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
    ) {
        item { PageHeading("إدارة الإشعارات", "التحكم في إشعارات التطبيقات", Icons.Rounded.Notifications, V2Amber) }
        item { V2SectionLabel("التطبيقات التي ترسل إشعارات", trailing = "12 تطبيق") }
        item {
            NotificationRow("واتساب", "4 إشعارات جديدة", V2Green, true)
            NotificationRow("التيك توك", "2 إشعارات جديدة", V2Violet, true)
            NotificationRow("اليوتيوب", "لا إشعارات", V2Pink, false)
            NotificationRow("الفيسبوك", "6 إشعارات جديدة", V2Blue, true)
            NotificationRow("إنستغرام", "لا إشعارات", V2Amber, false)
            NotificationRow("الرسائل", "لا إشعارات", V2Cyan, false)
        }
        item { PreviewNote("إدارة الإشعارات تفتح إعدادات النظام للتطبيقات المحددة.") }
    }
}

@Composable
private fun NotificationRow(name: String, status: String, accent: Color, enabled: Boolean) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 8.dp, radius = 12.dp, glow = accent, borderAlpha = .20f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                    .background(accent.withAlpha(.12f))
                    .border(1.dp, accent.withAlpha(.32f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Notifications, null, tint = accent, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(name, size = 9.4.sp, weight = FontWeight.SemiBold, maxLines = 1)
                KText(status, size = 7.4.sp, color = V2Soft, maxLines = 1)
            }
            Box(
                Modifier.clip(RoundedCornerShape(50))
                    .background(if (enabled) V2Green.withAlpha(.20f) else V2Red.withAlpha(.20f))
                    .border(1.dp, if (enabled) V2Green.withAlpha(.40f) else V2Red.withAlpha(.40f), RoundedCornerShape(50))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                KText(if (enabled) "مفعل" else "معطل", size = 7.sp, weight = FontWeight.Bold, color = if (enabled) V2Green else V2Red)
            }
        }
    }
}
