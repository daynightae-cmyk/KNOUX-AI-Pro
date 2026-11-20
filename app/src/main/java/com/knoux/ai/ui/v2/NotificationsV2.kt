package com.knoux.ai.ui.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.Bolt
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.v2.V2GoldText
import com.knoux.ai.ui.v2.LocalProTheme
import com.knoux.ai.ui.v2.LocalProActive

private data class KnouxNotice(
    val title: String,
    val body: String,
    val time: String,
    val icon: ImageVector,
    val accent: Color,
    val group: String,
    val route: String,
)

// 50 notifications distributed across 10 service sections (5 each)
private val knouxNotices = listOf(
    // ====== SMART CLEAN (5) ======
    KnouxNotice(
        "التنظيف الذكي جاهز",
        "2.41 GB محددة من أصل 2.92 GB قابلة للمراجعة",
        "منذ ساعة",
        Icons.Rounded.CleaningServices, V2Cyan, "التنظيف", "smartclean",
    ),
    KnouxNotice(
        "تنظيف مجدول مكتمل",
        "تم تنظيف 1.2 GB تلقائيًا فجر اليوم",
        "منذ 6 ساعات",
        Icons.Rounded.CleaningServices, V2Violet, "التنظيف", "smartclean",
    ),
    KnouxNotice(
        "ملفات كبيرة مكتشفة",
        "14 ملفًا يزيد حجمها عن 500 MB في التنزيلات",
        "منذ 12 ساعة",
        Icons.Rounded.Folder, V2Pink, "التنظيف", "storage-breakdown",
    ),
    KnouxNotice(
        "تنظيف عميق مقترح",
        "فحص شامل قد يستعيد 3.8 GB إضافية",
        "أمس",
        Icons.Rounded.Radar, V2Amber, "التنظيف", "deepscan",
    ),
    KnouxNotice(
        "سجل التنظيف متاح",
        "آخر 5 عمليات استعادت 12.4 GB إجمالًا",
        "منذ يومين",
        Icons.Rounded.Timeline, V2Green, "التنظيف", "reports",
    ),

    // ====== DUPLICATE FINDER (5) ======
    KnouxNotice(
        "ملفات مكررة تحتاج قرارك",
        "48 مجموعة · 2.8 GB قابلة للاستعادة",
        "منذ 30 دقيقة",
        Icons.Rounded.Layers, V2Violet, "التنظيف", "duplicates",
    ),
    KnouxNotice(
        "صور متطابقة جديدة",
        "12 مجموعة صور · 450 MB مكررة",
        "منذ 3 ساعات",
        Icons.Rounded.Image, V2Pink, "التنظيف", "duplicates",
    ),
    KnouxNotice(
        "فيديوهات مكررة",
        "6 فيديوهات مكررة · 1.2 GB",
        "منذ 5 ساعات",
        Icons.Rounded.Videocam, V2Amber, "التنظيف", "duplicates",
    ),
    KnouxNotice(
        "مستندات مكررة",
        "23 ملف PDF/Office مكرر · 180 MB",
        "أمس",
        Icons.Rounded.Description, V2Cyan, "التنظيف", "duplicates",
    ),
    KnouxNotice(
        "مراجعة المكررات متاحة",
        "استخدم 'حماية النسخة الأصلية' للحفظ الآمن",
        "منذ يومين",
        Icons.Rounded.Shield, V2Green, "التنظيف", "duplicates",
    ),

    // ====== APP RESIDUE (5) ======
    KnouxNotice(
        "بقايا تطبيقات مكتشفة",
        "2.84 GB قابلة للتنظيف من 4 فئات",
        "منذ 45 دقيقة",
        Icons.Rounded.Layers, V2Violet, "التنظيف", "residue",
    ),
    KnouxNotice(
        "ذاكرة تخزين مؤقت كبيرة",
        "TikTok: 1.4 GB · WhatsApp: 684 MB",
        "منذ 3 ساعات",
        Icons.Rounded.Memory, V2Pink, "التنظيف", "residue",
    ),
    KnouxNotice(
        "ملفات تثبيت قديمة",
        "APKs مهجورة في Download · 356 MB",
        "منذ 6 ساعات",
        Icons.Rounded.Folder, V2Amber, "التنظيف", "residue",
    ),
    KnouxNotice(
        "سجلات تطبيقات",
        "41 ملف سجل · 78 MB قابلة للإزالة",
        "أمس",
        Icons.Rounded.Description, V2Cyan, "التنظيف", "residue",
    ),
    KnouxNotice(
        "مراجعة البقايا جاهزة",
        "3 مجلدات محددة · 2.46 GB للمراجعة",
        "منذ يومين",
        Icons.Rounded.Verified, V2Green, "التنظيف", "residue",
    ),

    // ====== PRIVACY SWEEP (5) ======
    KnouxNotice(
        "تنبيه خصوصية مرتفع",
        "الكاميرا متاحة لـ 3 تطبيقات وتحتاج مراجعة",
        "منذ 12 دقيقة",
        Icons.Rounded.Shield, V2Pink, "الخصوصية", "privacy",
    ),
    KnouxNotice(
        "وصول للموقع في الخلفية",
        "4 تطبيقات تطلب الموقع باستمرار",
        "منذ ساعة",
        Icons.Rounded.LocationOn, V2Cyan, "الخصوصية", "privacy",
    ),
    KnouxNotice(
        "أذونات ميكروفون",
        "5 تطبيقات تسجل في الخلفية",
        "منذ 3 ساعات",
        Icons.Rounded.Mic, V2Violet, "الخصوصية", "privacy",
    ),
    KnouxNotice(
        "أدوات تتبع مكتشفة",
        "12 تطبيقًا بأذونات إعلانية مشبوهة",
        "منذ 5 ساعات",
        Icons.Rounded.Visibility, V2Amber, "الخصوصية", "privacy",
    ),
    KnouxNotice(
        "تقرير الخصوصية الأسبوعي",
        "مستوى الحماية 76% · 5 مخاطر مرتفعة",
        "أمس",
        Icons.Rounded.BarChart, V2Green, "الخصوصية", "privacy",
    ),

    // ====== APP MANAGER (5) ======
    KnouxNotice(
        "تطبيق جديد مثبت",
        "تم تثبيت 'Spotify' · 180 MB",
        "منذ 20 دقيقة",
        Icons.Rounded.Apps, V2Cyan, "الأدوات", "appmanager",
    ),
    KnouxNotice(
        "تطبيقات غير مستخدمة",
        "7 تطبيقات لم تُفتح منذ 30+ يوم",
        "منذ 4 ساعات",
        Icons.Rounded.Apps, V2Violet, "الأدوات", "appmanager",
    ),
    KnouxNotice(
        "تطبيقات ثقيلة",
        "3 تطبيقات تستهلك >500 MB للتخزين",
        "منذ 8 ساعات",
        Icons.Rounded.Storage, V2Pink, "الأدوات", "storage-breakdown",
    ),
    KnouxNotice(
        "تحديثات متاحة",
        "5 تطبيقات لديها تحديثات على Play Store",
        "أمس",
        Icons.Rounded.Download, V2Amber, "الأدوات", "appmanager",
    ),
    KnouxNotice(
        "إحصائيات الاستخدام",
        "أعلى 3: WhatsApp، Chrome، YouTube",
        "منذ يومين",
        Icons.Rounded.BarChart, V2Green, "الأدوات", "appmanager",
    ),

    // ====== BATTERY SAVER (5) ======
    KnouxNotice(
        "بطارية الجهاز مستقرة",
        "93% · 34° مئوية ولا يوجد ارتفاع غير طبيعي",
        "أمس",
        Icons.Rounded.BatteryFull, V2Green, "الأداء", "battery-diagnostics",
    ),
    KnouxNotice(
        "تطبيق يستهلك بطارية",
        "Google Maps: 18% في آخر 24 ساعة",
        "منذ 3 ساعات",
        Icons.Rounded.Bolt, V2Amber, "الأداء", "battery-diagnostics",
    ),
    KnouxNotice(
        "شحن محسن مقترح",
        "تفعيل 'الشحن التكيفي' يطيل عمر البطارية",
        "منذ 6 ساعات",
        Icons.Rounded.BatteryFull, V2Cyan, "الأداء", "battery-diagnostics",
    ),
    KnouxNotice(
        "حرارة الجهاز طبيعية",
        "أقصى درجة 37° أثناء الألعاب · آمن",
        "منذ 10 ساعات",
        Icons.Rounded.Thermostat, V2Violet, "الأداء", "battery-diagnostics",
    ),
    KnouxNotice(
        "تقرير البطارية الأسبوعي",
        "متوسط الشحن اليومي: 1.2 دورة · صحة 93%",
        "أمس",
        Icons.Rounded.Timeline, V2Green, "الأداء", "battery-diagnostics",
    ),

    // ====== DEEP SCAN (5) ======
    KnouxNotice(
        "اكتمل الفحص العميق",
        "تم فحص 5 وحدات · النتيجة الإجمالية 72%",
        "الآن",
        Icons.Rounded.Radar, V2Cyan, "الأداء", "deepscan",
    ),
    KnouxNotice(
        "وحدة التخزين مكتملة",
        "182 GB مستخدمة · 72% · 2.6 GB قابلة للاستعادة",
        "منذ 10 دقائق",
        Icons.Rounded.Storage, V2Violet, "الأداء", "deepscan",
    ),
    KnouxNotice(
        "وحدة التطبيقات مكتملة",
        "54 تطبيقًا · 8 ثقيلة · 1.2 GB قابلة للتحسين",
        "منذ 15 دقيقة",
        Icons.Rounded.Apps, V2Pink, "الأداء", "deepscan",
    ),
    KnouxNotice(
        "وحدة الخصوصية مكتملة",
        "43 تطبيقًا · 3 مخاطر مرتفعة · 5 ملاحظات",
        "منذ 20 دقيقة",
        Icons.Rounded.Shield, V2Amber, "الأداء", "deepscan",
    ),
    KnouxNotice(
        "وحدة الأداء مكتملة",
        "نقطة 94 · 4 توصيات · +4 نقاط ممكنة",
        "منذ 25 دقيقة",
        Icons.Rounded.RocketLaunch, V2Green, "الأداء", "deepscan",
    ),

    // ====== REPORTS (5) ======
    KnouxNotice(
        "التقرير الأسبوعي متاح",
        "+32% تحسن في الأداء مقارنة بالأسبوع الماضي",
        "منذ 3 ساعات",
        Icons.Rounded.BarChart, V2Amber, "الأداء", "performance-report",
    ),
    KnouxNotice(
        "تقرير شهري جاهز",
        "استعيد 26.4 GB · 12 تطبيقًا محسنًا",
        "أمس",
        Icons.Rounded.BarChart, V2Green, "الأداء", "performance-report",
    ),
    KnouxNotice(
        "سجل الفحوصات",
        "آخر 7 فحوصات · متوسط 78% إكمال",
        "منذ يومين",
        Icons.Rounded.Timeline, V2Cyan, "الأداء", "performance-report",
    ),
    KnouxNotice(
        "تصدير التقرير متاح",
        "يمكنك مشاركة تقرير PDF مع الدعم",
        "منذ 3 أيام",
        Icons.Rounded.Download, V2Violet, "الأداء", "performance-report",
    ),
    KnouxNotice(
        "اتجاه الأداء إيجابي",
        "6 أسابيع متتالية من التحسن المستمر",
        "منذ 4 أيام",
        Icons.Rounded.TrendingUp, V2Green, "الأداء", "performance-report",
    ),

    // ====== AI OPTIMIZATION (5) ======
    KnouxNotice(
        "توصيات ذكاء اصطناعي جديدة",
        "4 اقتراحات لتحسين الأداء وتقليل الحرارة",
        "أمس",
        Icons.Rounded.AutoAwesome, V2Violet, "الأداء", "ai-recommendations",
    ),
    KnouxNotice(
        "تنظيف تنبئي مقترح",
        "الذكاء الاصطناعي يتنبأ بـ 3.2 GB قابلة للتنظيف",
        "منذ يومين",
        Icons.Rounded.AutoAwesome, V2Amber, "الأداء", "ai-recommendations",
    ),
    KnouxNotice(
        "تحسين بدء التشغيل",
        "تعطيل 3 تطبيقات بداية يوفر 12 ث",
        "منذ 3 أيام",
        Icons.Rounded.RocketLaunch, V2Cyan, "الأداء", "ai-recommendations",
    ),
    KnouxNotice(
        "إدارة حرارة ذكية",
        "تقليل تردد CPU عند 40° يوفر 15% بطارية",
        "منذ 4 أيام",
        Icons.Rounded.Thermostat, V2Pink, "الأداء", "ai-recommendations",
    ),
    KnouxNotice(
        "نموذج AI محدث",
        "إصدار جديد بتحسينات دقة 18% متاح",
        "منذ 5 أيام",
        Icons.Rounded.Verified, V2Green, "الأداء", "ai-recommendations",
    ),

    // ====== PRO (5) ======
    KnouxNotice(
        "هوية Pro متاحة للمعاينة",
        "جرّب تفعيل Pro وراجع الهوية الذهبية على جهازك",
        "منذ يومين",
        Icons.Rounded.Star, V2Amber, "Pro", "pro",
    ),
    KnouxNotice(
        "ميزة Pro: تنظيف بلا حدود",
        "قوائم تنظيف أوسع وأدوات احترافية متاحة",
        "منذ 3 أيام",
        Icons.Rounded.CleaningServices, V2GoldText, "Pro", "pro",
    ),
    KnouxNotice(
        "ميزة Pro: حماية فورية",
        "مراقبة مستمرة للأذونات والتهديدات",
        "منذ 4 أيام",
        Icons.Rounded.Shield, V2GoldText, "Pro", "pro",
    ),
    KnouxNotice(
        "ميزة Pro: تقارير كاملة",
        "تتبع كل التحسينات على مدى الأشهر",
        "منذ 5 أيام",
        Icons.Rounded.BarChart, V2GoldText, "Pro", "pro",
    ),
    KnouxNotice(
        "ميزة Pro: ذكاء اصطناعي متقدم",
        "تحليل أعمق وتقارير مخصصة حسب استخدامك",
        "منذ 6 أيام",
        Icons.Rounded.AutoAwesome, V2GoldText, "Pro", "pro",
    ),
)

private val noticeFilters = listOf("الكل", "التنظيف", "الخصوصية", "الأداء", "الأدوات", "Pro")

@Composable
fun V2NotificationsScreen(
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    onPro: () -> Unit,
) {
    var filter by remember { mutableStateOf("الكل") }
    val read = remember { mutableStateListOf(4, 5, 6, 7, 8) }
    val unread = knouxNotices.indices.count { it !in read }
    val proTheme = LocalProTheme.current
    val isPro = LocalProActive.current

    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            androidx.compose.material3.Text(
                                "الإشعارات",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = V2Font,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 27.sp,
                                    color = if (LocalDayMode.current) DayInk else Color.White,
                                ),
                            )
                            KText("ملخص ما حدث على جهازك خلال آخر يومين.", size = 10.sp, color = if (isPro) ProSoft else V2Soft)
                        }
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isPro) proTheme.amber.copy(alpha = .16f) else V2Amber.copy(alpha = .16f))
                                .border(1.dp, if (isPro) proTheme.amber.copy(alpha = .48f) else V2Amber.copy(alpha = .48f), RoundedCornerShape(50))
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            KText("$unread جديدة", size = 9.sp, weight = FontWeight.Bold, color = if (isPro) proTheme.amber else V2Amber)
                        }
                    }
                }

                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        noticeFilters.forEach { option ->
                            val selected = option == filter
                            val filterAccent = if (isPro) proTheme.amber else V2Cyan
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(if (selected) filterAccent.copy(alpha = .20f) else Color.White.copy(alpha = .04f))
                                    .border(
                                        1.dp,
                                        if (selected) filterAccent.copy(alpha = .62f) else Color.White.copy(alpha = .12f),
                                        RoundedCornerShape(50),
                                    )
                                    .clickable { filter = option }
                                    .padding(horizontal = 11.dp, vertical = 6.dp)
                            ) {
                                KText(
                                    option,
                                    size = 9.sp,
                                    weight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) if (isPro) proTheme.deep else filterAccent else if (isPro) ProMuted else V2Soft,
                                )
                            }
                        }
                    }
                }

                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        KText("الأحدث أولًا", size = 9.sp, color = if (isPro) ProMuted else V2Muted)
                        Spacer(Modifier.weight(1f))
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { read.addAll(knouxNotices.indices.filter { it !in read }) }
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Rounded.Check, null, tint = if (isPro) proTheme.amber else V2Cyan, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            KText("تحديد الكل كمقروء", size = 9.sp, color = if (isPro) proTheme.amber else V2Cyan)
                        }
                    }
                }

                val visible = knouxNotices.withIndex().filter {
                    filter == "الكل" || it.value.group == filter
                }
                items(visible.size) { position ->
                    val entry = visible[position]
                    val notice = entry.value
                    val isUnread = entry.index !in read
                    NoticeCard(
                        notice = notice,
                        unread = isUnread,
                        onClick = {
                            if (entry.index !in read) read.add(entry.index)
                            onOpen(notice.route)
                        },
                    )
                }

                item { PreviewNote("إشعارات محلية تجريبية؛ لا يوجد Push سحابي أو حساب في هذا البناء.") }
                item {
                    V2GhostButton(
                        "إعدادات الإشعارات",
                        onClick = { onOpen("settings") },
                        icon = Icons.Rounded.Settings,
                    )
                }
            }
        }
    }
}

@Composable
private fun NoticeCard(notice: KnouxNotice, unread: Boolean, onClick: () -> Unit) {
    val proTheme = LocalProTheme.current
    val isPro = LocalProActive.current
    val cardAccent = if (isPro) proTheme.amber else notice.accent
    val cardGlow = if (isPro) proTheme.amber else notice.accent
    V2GlassCard(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        padding = 10.dp,
        radius = 17.dp,
        glow = cardGlow,
        borderAlpha = if (unread) .42f else .20f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(cardAccent.copy(alpha = .24f), cardAccent.copy(alpha = .06f))
                        )
                    )
                    .border(1.dp, cardAccent.copy(alpha = .36f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(notice.icon, null, tint = cardAccent, modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KText(notice.title, size = 10.sp, weight = FontWeight.Bold, maxLines = 1)
                    if (unread) {
                        Spacer(Modifier.width(5.dp))
                        Box(Modifier.size(6.dp).clip(CircleShape).background(cardAccent))
                    }
                }
                Spacer(Modifier.height(2.dp))
                KText(notice.body, size = 9.sp, lineHeight = 11.5.sp, color = if (isPro) ProMuted else V2Soft, maxLines = 2)
                Spacer(Modifier.height(3.dp))
                KText(notice.time, size = 9.sp, color = if (isPro) ProMuted else V2Muted)
            }
            Spacer(Modifier.width(4.dp))
            Icon(
                androidx.compose.material.icons.Icons.Rounded.ChevronLeft,
                null,
                tint = if (isPro) ProMuted else V2Soft,
                modifier = Modifier.size(15.dp),
            )
        }
    }
}
