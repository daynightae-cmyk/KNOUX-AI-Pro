package com.knoux.ai.ui.v2

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.DeveloperMode
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.BuildConfig
import kotlin.math.roundToInt

/*
 * The four owner-requested extra section hubs. They reuse the exact same
 * luxury language as the ten original hubs (identity header, health panel,
 * centerpiece K orb, four-column service grid, live stat strip) and every
 * number is read from this device through DeviceFacts / android.os.Build.
 */

/* ------------------------------------------------------------------ *
 * Shared helpers
 * ------------------------------------------------------------------ */

@Composable
private fun ExtraHubHeader(
    title: String,
    english: String,
    subtitle: String,
    accent: Color,
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    // Interior top bar gives these hubs the same back/crown affordances as
    // every other routed screen; previously they relied on system back only.
    V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
    V2LuxuryIdentity(
        title = title,
        english = english,
        subtitle = subtitle,
        accent = accent,
    )
}

@Composable
private fun ExtraHubStatStrip(
    facts: DeviceFacts,
    first: Pair<String, String>,
    second: Pair<String, String>,
    accent: Color,
) {
    V2LuxuryStatStrip(
        stats = listOf(
            SectionStat(Icons.Rounded.Android, Build.VERSION.RELEASE, "إصدار النظام", V2Cyan),
            SectionStat(Icons.Rounded.Memory, formatBytes(facts.ownCacheBytes), "كاش KNOUX", V2Pink),
            SectionStat(Icons.Rounded.Storage, formatBytes(facts.storage.freeBytes), "مساحة متاحة", V2Green),
            SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", accent),
        ),
    )
}

@Composable
private fun ExtraHubAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit,
) {
    V2LuxurySurface(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        radius = 18.dp,
        accent = accent,
        borderAlpha = .42f,
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = .16f))
                    .border(1.dp, accent.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = accent, modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 12.5.sp, weight = FontWeight.Bold, color = Color.White)
                KText(subtitle, size = 9.sp, color = V2Soft, maxLines = 2)
            }
            Icon(Icons.Rounded.Verified, null, tint = accent.copy(alpha = .75f), modifier = Modifier.size(16.dp))
        }
    }
}

private fun openSystemSettings(context: android.content.Context, action: String) {
    val intent = Intent(action).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (action == Settings.ACTION_SETTINGS) {
            data = Uri.parse("package:${context.packageName}")
        }
    }
    runCatching { context.startActivity(intent) }
}

/* ------------------------------------------------------------------ *
 * 11 - Developer tools
 * ------------------------------------------------------------------ */

@Composable
fun V2DeveloperToolsScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val facts = rememberDeviceFacts()
    val navigate = LocalServiceNavigate.current

    Column(Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars)) {
        ExtraHubHeader(
            title = "أدوات المطورين",
            english = "DEVELOPER TOOLS",
            subtitle = "معلومات البناء والنظام وسجل التشغيل على هذا الهاتف",
            accent = V2Cyan,
            onBack = onBack,
            onPro = onPro,
        )
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            item {
                KnouxRunOrbBlock(
                    progress = if (facts.computedAt > 0L) 1f else 0f,
                    running = false,
                    enabled = false,
                    label = if (facts.computedAt > 0L) "قراءة مباشرة من الجهاز" else "جاري القراءة…",
                    accent = V2Cyan,
                    accent2 = V2Blue,
                    size = 132.dp,
                    onTap = {},
                )
            }
            item {
                V2LuxuryServiceGrid(
                    items = listOf(
                        ServiceSubItem(
                            title = "تفاصيل البناء",
                            subtitle = "Build.VERSION وإصدار النظام",
                            route = "dev-build",
                            icon = Icons.Rounded.Build,
                            accent = V2Cyan,
                        ),
                        ServiceSubItem(
                            title = "سجل العمليات",
                            subtitle = "آخر الفحوص في Room",
                            route = "dev-log",
                            icon = Icons.Rounded.History,
                            accent = V2Violet,
                        ),
                        ServiceSubItem(
                            title = "حزم النظام",
                            subtitle = "PackageManager على هذا الجهاز",
                            route = "dev-packages",
                            icon = Icons.Rounded.Android,
                            accent = V2Pink,
                        ),
                        ServiceSubItem(
                            title = "قراءة الذاكرة",
                            subtitle = "StatFs ومساحة KNOUX",
                            route = "dev-memory",
                            icon = Icons.Rounded.Memory,
                            accent = V2Green,
                        ),
                    ),
                    onNavigate = navigate,
                )
            }
            item {
                V2LuxurySurface(Modifier.fillMaxWidth(), radius = 18.dp, accent = V2Cyan, borderAlpha = .38f) {
                    Column(Modifier.fillMaxWidth().padding(13.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Info, null, tint = V2Cyan, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(7.dp))
                            KText("معلومات البناء", size = 12.5.sp, weight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(Modifier.height(7.dp))
                        ExtraInfoLine("الحزمة", BuildConfig.APPLICATION_ID)
                        ExtraInfoLine("الإصدار", BuildConfig.VERSION_NAME)
                        ExtraInfoLine("كود البناء", BuildConfig.VERSION_CODE.toString())
                        ExtraInfoLine("نظام التشغيل", "Android ${Build.VERSION.RELEASE}")
                        ExtraInfoLine("مستوى API", Build.VERSION.SDK_INT.toString())
                        ExtraInfoLine("المعمارية", Build.SUPPORTED_ABIS.firstOrNull() ?: "—")
                    }
                }
            }
            item {
                ExtraHubAction(
                    icon = Icons.Rounded.DeveloperMode,
                    title = "إعدادات المطور في النظام",
                    subtitle = "افتح خيارات المطوّر الرسمية في إعدادات Android",
                    accent = V2Cyan,
                    onClick = { openSystemSettings(context, Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS) },
                )
            }
            item {
                ExtraHubStatStrip(
                    facts = facts,
                    first = Build.VERSION.RELEASE to "إصدار النظام",
                    second = BuildConfig.VERSION_NAME to "إصدار التطبيق",
                    accent = V2Cyan,
                )
            }
        }
    }
}

@Composable
private fun ExtraInfoLine(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KText(label, size = 9.6.sp, color = V2Soft)
        Spacer(Modifier.weight(1f))
        KText(value, size = 9.6.sp, weight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
    }
}

/* ------------------------------------------------------------------ *
 * 12 - Google services
 * ------------------------------------------------------------------ */

@Composable
fun V2GoogleServicesScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val facts = rememberDeviceFacts()
    val navigate = LocalServiceNavigate.current
    val googleCount = googleAppCount(facts)

    Column(Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars)) {
        ExtraHubHeader(
            title = "خدمات جوجل",
            english = "GOOGLE SERVICES",
            subtitle = "Google Play Services والإشعارات وتسجيل الدخول على الجهاز",
            accent = V2Violet,
            onBack = onBack,
            onPro = onPro,
        )
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            item {
                KnouxRunOrbBlock(
                    progress = if (facts.computedAt > 0L) 1f else 0f,
                    running = false,
                    enabled = false,
                    label = "$googleCount تطبيق Google مرئي",
                    accent = V2Violet,
                    accent2 = V2Cyan,
                    size = 132.dp,
                    onTap = {},
                )
            }
            item {
                V2LuxuryServiceGrid(
                    items = listOf(
                        ServiceSubItem(
                            title = "تطبيقات Google",
                            subtitle = "حزم com.google. المرئية",
                            route = "gsc-apps",
                            icon = Icons.Rounded.AccountCircle,
                            accent = V2Cyan,
                        ),
                        ServiceSubItem(
                            title = "إعدادات Play",
                            subtitle = "إعدادات Google Play الرسمية",
                            route = "gsc-settings",
                            icon = Icons.Rounded.Settings,
                            accent = V2Violet,
                        ),
                        ServiceSubItem(
                            title = "قنوات الإشعارات",
                            subtitle = "ما يعرضه النظام",
                            route = "gsc-notifications",
                            icon = Icons.Rounded.Notifications,
                            accent = V2Pink,
                        ),
                        ServiceSubItem(
                            title = "دعم Google",
                            subtitle = "روابط المساعدة الرسمية",
                            route = "gsc-account",
                            icon = Icons.Rounded.Security,
                            accent = V2Green,
                        ),
                    ),
                    onNavigate = navigate,
                )
            }
            item {
                V2LuxurySurface(Modifier.fillMaxWidth(), radius = 18.dp, accent = V2Violet, borderAlpha = .38f) {
                    Column(Modifier.fillMaxWidth().padding(13.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.AccountCircle, null, tint = V2Violet, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(7.dp))
                            KText("حزم Google على هذا الجهاز", size = 12.5.sp, weight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(Modifier.height(7.dp))
                        ExtraInfoLine("تطبيقات Google", formatCount(googleCount))
                        ExtraInfoLine("كل التطبيقات", formatCount(facts.installedCount))
                        ExtraInfoLine("Google Play Store", if (googleCount > 0) "موجود" else "غير ظاهر")
                        ExtraInfoLine("تسجيل الدخول", "بوابة KNOUX · Keystore")
                    }
                }
            }
            item {
                ExtraHubAction(
                    icon = Icons.Rounded.Settings,
                    title = "إعدادات Google Play",
                    subtitle = "افتح إعدادات Google Play في نظام Android",
                    accent = V2Violet,
                    onClick = { openSystemSettings(context, "com.android.vending.SETTINGS") },
                )
            }
            item {
                ExtraHubStatStrip(
                    facts = facts,
                    first = formatCount(googleCount) to "تطبيق Google",
                    second = formatCount(facts.installedCount) to "تطبيق مثبت",
                    accent = V2Violet,
                )
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * 13 - Advanced security
 * ------------------------------------------------------------------ */

@Composable
fun V2AdvancedSecurityScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val facts = rememberDeviceFacts()
    val navigate = LocalServiceNavigate.current

    Column(Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars)) {
        ExtraHubHeader(
            title = "الأمان المتقدم",
            english = "ADVANCED SECURITY",
            subtitle = "قفل الجهاز والمصادقة البيومترية وحسابات الجهاز",
            accent = V2Green,
            onBack = onBack,
            onPro = onPro,
        )
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            item {
                KnouxRunOrbBlock(
                    progress = if (facts.computedAt > 0L) 1f else 0f,
                    running = false,
                    enabled = false,
                    label = "${formatCount(facts.sensitiveAppCount)} تطبيق حساس",
                    accent = V2Green,
                    accent2 = V2Cyan,
                    size = 132.dp,
                    onTap = {},
                )
            }
            item {
                V2LuxuryServiceGrid(
                    items = listOf(
                        ServiceSubItem(
                            title = "قفل الشاشة",
                            subtitle = "إعدادات الأمان الرسمية",
                            route = "sec-lock",
                            icon = Icons.Rounded.Lock,
                            accent = V2Cyan,
                        ),
                        ServiceSubItem(
                            title = "حسابات الجهاز",
                            subtitle = "حسابات Android المرئية",
                            route = "sec-accounts",
                            icon = Icons.Rounded.AccountCircle,
                            accent = V2Violet,
                        ),
                        ServiceSubItem(
                            title = "مراجعة الأذونات",
                            subtitle = "كل الأذونات الحساسة",
                            route = "sec-permissions",
                            icon = Icons.Rounded.Security,
                            accent = V2Pink,
                        ),
                        ServiceSubItem(
                            title = "ملخص الأمان",
                            subtitle = "من قراءات الجهاز فقط",
                            route = "advanced-security",
                            icon = Icons.Rounded.Shield,
                            accent = V2Green,
                        ),
                    ),
                    onNavigate = navigate,
                )
            }
            item {
                V2LuxurySurface(Modifier.fillMaxWidth(), radius = 18.dp, accent = V2Green, borderAlpha = .38f) {
                    Column(Modifier.fillMaxWidth().padding(13.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Shield, null, tint = V2Green, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(7.dp))
                            KText("ملخص الأمان", size = 12.5.sp, weight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(Modifier.height(7.dp))
                        ExtraInfoLine("تطبيقات حساسة", formatCount(facts.sensitiveAppCount))
                        ExtraInfoLine("مجموعات أذونات", "${facts.privacyGroups.size} مجموعة")
                        ExtraInfoLine(
                            "وصول الاستخدام",
                            if (facts.usageAccessGranted) "ممنوح" else "غير ممنوح",
                        )
                        ExtraInfoLine(
                            "تقدير الجهاز",
                            if (facts.healthEstimate > 0) "${facts.healthEstimate}%" else "—",
                        )
                    }
                }
            }
            item {
                ExtraHubAction(
                    icon = Icons.Rounded.Fingerprint,
                    title = "إعدادات الأمان",
                    subtitle = "قفل الشاشة والمصادقة البيومترية في نظام Android",
                    accent = V2Green,
                    onClick = { openSystemSettings(context, Settings.ACTION_SECURITY_SETTINGS) },
                )
            }
            item {
                ExtraHubStatStrip(
                    facts = facts,
                    first = formatCount(facts.sensitiveAppCount) to "تطبيق حساس",
                    second = if (facts.usageAccessGranted) "ممنوح" to "وصول الاستخدام" else "غير ممنوح" to "وصول الاستخدام",
                    accent = V2Green,
                )
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * 14 - Backup & restore
 * ------------------------------------------------------------------ */

@Composable
fun V2BackupRestoreScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val facts = rememberDeviceFacts()
    val navigate = LocalServiceNavigate.current
    var pickedTree by remember { mutableStateOf<Uri?>(null) }

    // The folder picker is the system SAF tree browser. KNOUX only remembers
    // the permission it was granted; it never copies anything on its own.
    val treePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree(),
    ) { uri ->
        pickedTree = uri
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                )
            }
        }
    }

    Column(Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars)) {
        ExtraHubHeader(
            title = "النسخ والاستعادة",
            english = "BACKUP & RESTORE",
            subtitle = "نسخ احتياطي صريح إلى مجلد تختاره أنت عبر SAF",
            accent = V2Amber,
            onBack = onBack,
            onPro = onPro,
        )
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            item {
                KnouxRunOrbBlock(
                    progress = if (facts.computedAt > 0L) 1f else 0f,
                    running = false,
                    enabled = false,
                    label = "${formatBytes(facts.storage.freeBytes)} متاحة للنسخ",
                    accent = V2Amber,
                    accent2 = V2Violet,
                    size = 132.dp,
                    onTap = {},
                )
            }
            item {
                V2LuxuryServiceGrid(
                    items = listOf(
                        ServiceSubItem(
                            title = "اختيار الوجهة",
                            subtitle = "منتقي SAF النظامي",
                            route = "bkp-pick",
                            icon = Icons.Rounded.FolderOpen,
                            accent = V2Cyan,
                        ),
                        ServiceSubItem(
                            title = "جرد الملفات",
                            subtitle = "قائمة صريحة قبل النسخ",
                            route = "bkp-inventory",
                            icon = Icons.Rounded.Inventory,
                            accent = V2Violet,
                        ),
                        ServiceSubItem(
                            title = "سجل النسخ",
                            subtitle = "محلي في Room",
                            route = "bkp-log",
                            icon = Icons.Rounded.History,
                            accent = V2Pink,
                        ),
                        ServiceSubItem(
                            title = "الاستعادة",
                            subtitle = "من مجلد تختاره",
                            route = "bkp-restore",
                            icon = Icons.Rounded.Restore,
                            accent = V2Green,
                        ),
                    ),
                    onNavigate = navigate,
                )
            }
            item {
                V2LuxurySurface(Modifier.fillMaxWidth(), radius = 18.dp, accent = V2Amber, borderAlpha = .38f) {
                    Column(Modifier.fillMaxWidth().padding(13.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Backup, null, tint = V2Amber, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(7.dp))
                            KText("حالة النسخ", size = 12.5.sp, weight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(Modifier.height(7.dp))
                        ExtraInfoLine("الوجهة", pickedTree?.let { "مجلد مختار" } ?: "لم تُختر بعد")
                        ExtraInfoLine("كاش KNOUX", formatBytes(facts.ownCacheBytes))
                        ExtraInfoLine("ملفات KNOUX", formatCount(facts.ownCacheFiles))
                        ExtraInfoLine("السحابة", "غير مستخدمة")
                    }
                }
            }
            item {
                ExtraHubAction(
                    icon = Icons.Rounded.FolderOpen,
                    title = "اختيار مجلد الوجهة",
                    subtitle = "يفتح منتقي مجلدات Android. لا يُكتب أي ملف قبل موافقتك.",
                    accent = V2Amber,
                    onClick = { treePicker.launch(null) },
                )
            }
            item {
                ExtraHubStatStrip(
                    facts = facts,
                    first = formatBytes(facts.storage.freeBytes) to "مساحة متاحة",
                    second = formatBytes(facts.storage.totalBytes) to "سعة التخزين",
                    accent = V2Amber,
                )
            }
        }
    }
}
