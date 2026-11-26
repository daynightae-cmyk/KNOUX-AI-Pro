package com.knoux.ai.ui.v2

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.DeveloperMode
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.HealthAndSafety
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.BuildConfig
import com.knoux.ai.cloud.model.CloudAccount
import com.knoux.ai.cloud.model.displayName
import com.knoux.ai.developer.vault.CredentialVault
import com.knoux.ai.google.auth.GoogleAccountManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/*
 * The four owner-requested extra section hubs. They reuse the exact same
 * luxury language as the ten original hubs (identity header, health panel,
 * centerpiece K orb, four-column service grid, live stat strip) and every
 * number is read from this device through DeviceFacts / android.os.Build.
 */

/* ------------------------------------------------------------------ *
 * Shared helpers
 * ------------------------------------------------------------------ */

/**
 * The royal stage every extra hub opens on: the interior top bar, the fixed
 * cosmic service hero with its measured ribbon, and the pinned run control
 * directly beneath the stage. Results scroll below — exactly the structure
 * the approved service references describe.
 */
@Composable
private fun ExtraHubShell(
    spec: KnouxServiceSpec,
    status: String,
    statusColor: Color,
    metric: String?,
    ribbonSlots: List<HeroRibbonSlot>,
    onBack: () -> Unit,
    onPro: () -> Unit,
    action: @Composable () -> Unit,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            FixedServiceScreen(
                spec = spec,
                status = status,
                statusColor = statusColor,
                metric = metric,
                scene = { sceneModifier -> ServiceArtScene(spec, sceneModifier) },
                ribbonSlots = ribbonSlots,
                modifier = Modifier.weight(1f),
                action = {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) { action() }
                },
                showIdentityLine = false,
                content = content,
            )
        }
    }
}

@Composable
private fun ExtraHubStatStrip(
    facts: DeviceFacts,
    first: Pair<String, String>,
    second: Pair<String, String>,
    accent: Color,
) {
    val live = facts.computedAt > 0L
    V2LuxuryStatStrip(
        stats = listOf(
            SectionStat(Icons.Rounded.Android, Build.VERSION.RELEASE, "إصدار النظام", V2Cyan),
            SectionStat(
                Icons.Rounded.Memory,
                if (live) formatBytes(facts.ownCacheBytes) else "—",
                "كاش KNOUX",
                V2Pink,
            ),
            SectionStat(
                Icons.Rounded.Storage,
                if (live) formatBytes(facts.storage.freeBytes) else "—",
                "مساحة متاحة",
                V2Green,
            ),
            SectionStat(
                Icons.Rounded.Apps,
                if (live) formatCount(facts.installedCount) else "—",
                "تطبيق مثبت",
                accent,
            ),
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
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val navigate = LocalServiceNavigate.current

    // Royal stage: fixed header with live instrument + measured ribbon
    ExtraHubShell(
        spec = ServiceSpecs.developer,
        status = if (facts.computedAt > 0L) "قراءة مباشرة من الجهاز" else "جاري القراءة…",
        statusColor = if (facts.computedAt > 0L) V2Green else V2Amber,
        metric = "Android ${Build.VERSION.RELEASE} · API ${Build.VERSION.SDK_INT}",
        ribbonSlots = listOf(
            HeroRibbonSlot(Icons.Rounded.Android, Build.VERSION.RELEASE, "إصدار النظام", V2Cyan),
            HeroRibbonSlot(
                Icons.Rounded.Memory,
                if (facts.computedAt > 0L) formatBytes(facts.ownCacheBytes) else "—",
                "كاش KNOUX",
                V2Pink,
            ),
            HeroRibbonSlot(
                Icons.Rounded.Storage,
                if (facts.computedAt > 0L) formatBytes(facts.storage.freeBytes) else "—",
                "مساحة متاحة",
                V2Green,
            ),
            HeroRibbonSlot(
                Icons.Rounded.Apps,
                if (facts.computedAt > 0L) formatCount(facts.installedCount) else "—",
                "تطبيق مثبت",
                V2Violet,
            ),
        ),
        onBack = onBack,
        onPro = onPro,
        action = {
            KnouxRunOrbBlock(
                progress = if (facts.computedAt > 0L) 1f else 0f,
                running = false,
                enabled = facts.computedAt > 0L,
                label = if (facts.computedAt > 0L) "اضغط لإعادة قراءة الجهاز" else "جاري القراءة…",
                accent = V2Cyan,
                accent2 = V2Blue,
                size = 118.dp,
                onTap = { bump() },
            )
        },
    ) {
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
        V2LuxurySurface(Modifier.fillMaxWidth(), radius = 18.dp, accent = V2Cyan, borderAlpha = .38f) {
            Column(Modifier.fillMaxWidth().padding(13.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Info, null, tint = if (LocalDayMode.current) DayCyan else V2Cyan, modifier = Modifier.size(15.dp))
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
        ExtraHubAction(
            icon = Icons.Rounded.DeveloperMode,
            title = "إعدادات المطور في النظام",
            subtitle = "افتح خيارات المطوّر الرسمية في إعدادات Android",
            accent = V2Cyan,
            onClick = { openSystemSettings(context, Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS) },
        )
        ExtraHubStatStrip(
            facts = facts,
            first = Build.VERSION.RELEASE to "إصدار النظام",
            second = BuildConfig.VERSION_NAME to "إصدار التطبيق",
            accent = V2Cyan,
        )
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
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val navigate = LocalServiceNavigate.current
    val googleCount = googleAppCount(facts)

    // Real connected Google accounts live in the encrypted account manager.
    // This screen reports exactly what is stored there — it never claims a
    // connection that was not made, and never asks for a credential itself.
    val vault = remember { CredentialVault(context) }
    val accountManager = remember {
        GoogleAccountManager(context, vault, BuildConfig.GOOGLE_ANDROID_CLIENT_ID)
    }
    val accounts: List<CloudAccount> = remember(refresh) {
        accountManager.getConnectedAccounts()
    }
    val oauthConfigured = accountManager.isConfigured

    ExtraHubShell(
        spec = ServiceSpecs.googleServices,
        status = when {
            facts.computedAt <= 0L -> "جاري القراءة…"
            accounts.isNotEmpty() -> "حساب متصل · PackageManager"
            else -> "حزم Google · بلا حساب"
        },
        statusColor = when {
            facts.computedAt <= 0L -> V2Amber
            accounts.isNotEmpty() -> V2Green
            else -> V2Violet
        },
        metric = "$googleCount تطبيق Google · ${accounts.size} حساب",
        ribbonSlots = listOf(
            HeroRibbonSlot(
                Icons.Rounded.AccountCircle,
                if (facts.computedAt > 0L) formatCount(googleCount) else "—",
                "تطبيق Google",
                V2Cyan,
            ),
            HeroRibbonSlot(
                Icons.Rounded.Apps,
                if (facts.computedAt > 0L) formatCount(facts.installedCount) else "—",
                "تطبيق مثبت",
                V2Violet,
            ),
            HeroRibbonSlot(Icons.Rounded.CloudSync, formatCount(accounts.size), "حساب سحابي", V2Green),
            HeroRibbonSlot(
                Icons.Rounded.Storage,
                if (facts.computedAt > 0L) formatBytes(facts.storage.freeBytes) else "—",
                "مساحة متاحة",
                V2Pink,
            ),
        ),
        onBack = onBack,
        onPro = onPro,
        action = {
            KnouxRunOrbBlock(
                progress = if (facts.computedAt > 0L) 1f else 0f,
                running = false,
                enabled = facts.computedAt > 0L,
                label = if (facts.computedAt > 0L) "اضغط لإعادة قراءة حزم Google" else "جاري القراءة…",
                accent = V2Violet,
                accent2 = V2Cyan,
                size = 118.dp,
                onTap = { bump() },
            )
        },
    ) {
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
                    title = "ربط حساب Google",
                    subtitle = "Drive وGmail بحساب حقيقي",
                    route = "google-storage",
                    icon = Icons.Rounded.CloudSync,
                    accent = V2Green,
                ),
            ),
            onNavigate = navigate,
        )
        V2LuxurySurface(Modifier.fillMaxWidth(), radius = 18.dp, accent = V2Violet, borderAlpha = .38f) {
            Column(Modifier.fillMaxWidth().padding(13.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AccountCircle, null, tint = V2Violet, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(7.dp))
                    KText("حسابات Google داخل KNOUX", size = 12.5.sp, weight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.height(7.dp))
                ExtraInfoLine("الحسابات المتصلة", if (accounts.isEmpty()) "لا يوجد" else formatCount(accounts.size))
                accounts.take(3).forEach { account ->
                    ExtraInfoLine(account.provider.displayName(), account.email)
                }
                if (accounts.isEmpty()) {
                    ExtraInfoLine("الربط", "من شاشة Google Storage عبر OAuth")
                }
                ExtraInfoLine("عميل OAuth", if (oauthConfigured) "مُكوّن في هذا البناء" else "غير مُكوّن في هذا البناء")
                ExtraInfoLine(
                    "تطبيقات Google",
                    if (facts.computedAt > 0L) formatCount(googleCount) else "—",
                )
                ExtraInfoLine(
                    "كل التطبيقات",
                    if (facts.computedAt > 0L) formatCount(facts.installedCount) else "—",
                )
                ExtraInfoLine("Google Play Store", if (googleCount > 0) "موجود" else "غير ظاهر")
            }
        }
        ExtraHubAction(
            icon = Icons.Rounded.Settings,
            title = "إعدادات Google Play",
            subtitle = "افتح إعدادات Google Play في نظام Android",
            accent = V2Violet,
            onClick = { openSystemSettings(context, "com.android.vending.SETTINGS") },
        )
        ExtraHubStatStrip(
            facts = facts,
            first = formatCount(googleCount) to "تطبيق Google",
            second = formatCount(accounts.size) to "حساب سحابي",
            accent = V2Violet,
        )
    }
}

/* ------------------------------------------------------------------ *
 * 13 - Advanced security
 * ------------------------------------------------------------------ */

/**
 * One measured signal of the device-security state. [strength] is null when
 * the reading itself was unavailable — a missing signal is drawn as missing,
 * never as a zero that would read like a perfect score.
 */
private data class RadarSignal(val label: String, val strength: Float?)

private fun biometricLabel(facts: DeviceFacts): String {
    if (!facts.isLive) return "—"
    return when (facts.biometricStrength) {
        2 -> "قوية"
        1 -> "ضعيفة"
        0 -> "غير متاحة"
        else -> "غير معروفة"
    }
}

/**
 * The protection radar: six real device-security readings placed on six
 * spokes. Ring radius encodes the measured strength of the signal, so the
 * geometry changes only when the device state changes. With no live reading
 * the radar stays empty and says so.
 */
@Composable
private fun SecurityRadar(facts: DeviceFacts, accent: Color) {
    val live = facts.isLive
    val signals = listOf(
        RadarSignal("قفل الشاشة", if (!live) null else if (facts.lockSecure) 1f else .12f),
        RadarSignal(
            "البيومترية",
            if (!live) null else when (facts.biometricStrength) {
                2 -> 1f
                1 -> .55f
                0 -> .12f
                else -> .12f
            },
        ),
        RadarSignal("VPN", if (!live) null else if (facts.vpnActive) 1f else .12f),
        RadarSignal(
            "مشرفو الجهاز",
            if (!live) null else if (facts.deviceAdminCount > 0) {
                (facts.deviceAdminCount / 3f).coerceIn(.3f, 1f)
            } else {
                .12f
            },
        ),
        RadarSignal(
            "نوافذ عائمة",
            if (!live) null else if (facts.overlayAppCount > 0) {
                (facts.overlayAppCount / 5f).coerceIn(.3f, 1f)
            } else {
                .12f
            },
        ),
        RadarSignal(
            "وصول الاستخدام",
            if (!live) null else if (facts.usageAccessGranted) 1f else .12f,
        ),
    )

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(172.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxR = size.minDimension / 2f - 8.dp.toPx()
                listOf(.14f, .20f, .28f).forEachIndexed { index, alpha ->
                    drawCircle(
                        color = accent.copy(alpha = alpha),
                        radius = maxR * (index + 1) / 3f,
                        center = center,
                        style = Stroke(width = 1.2.dp.toPx()),
                    )
                }
                for (spoke in 0 until 6) {
                    val rad = Math.toRadians((-90 + spoke * 60).toDouble())
                    drawLine(
                        color = accent.copy(alpha = .10f),
                        start = center,
                        end = Offset(
                            center.x + maxR * cos(rad).toFloat(),
                            center.y + maxR * sin(rad).toFloat(),
                        ),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
                signals.forEachIndexed { index, signal ->
                    val strength = signal.strength ?: return@forEachIndexed
                    val rad = Math.toRadians((-90 + index * 60).toDouble())
                    val radius = maxR * strength.coerceIn(0f, 1f)
                    val position = Offset(
                        center.x + radius * cos(rad).toFloat(),
                        center.y + radius * sin(rad).toFloat(),
                    )
                    val color = when {
                        strength >= .8f -> V2Green
                        strength >= .45f -> V2Amber
                        else -> V2Pink
                    }
                    drawCircle(color.copy(alpha = .26f), radius = 9.dp.toPx(), center = position)
                    drawCircle(color, radius = 4.5.dp.toPx(), center = position)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                KText(
                    if (live) "${facts.healthEstimate}%" else "—",
                    size = 21.sp,
                    weight = FontWeight.Black,
                    color = Color.White,
                )
                KText(if (live) "تقدير محلي" else "غير مقروء", size = 8.5.sp, color = V2Soft)
            }
        }
        Spacer(Modifier.height(6.dp))
        KText(
            "قفل · بيومترية · VPN · مشرف · نوافذ · استخدام",
            size = 8.5.sp,
            color = V2Muted,
            align = TextAlign.Center,
        )
    }
}

@Composable
fun V2AdvancedSecurityScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val navigate = LocalServiceNavigate.current

    ExtraHubShell(
        spec = ServiceSpecs.advancedSecurity,
        status = if (facts.isLive) "قراءات حقيقية من الجهاز" else "جاري القراءة…",
        statusColor = if (facts.isLive) V2Green else V2Amber,
        metric = "${formatCount(facts.sensitiveAppCount)} تطبيق حساس",
        ribbonSlots = listOf(
            HeroRibbonSlot(
                Icons.Rounded.Lock,
                if (facts.isLive) (if (facts.lockSecure) "مؤمّن" else "غير مؤمّن") else "—",
                "قفل الشاشة",
                V2Cyan,
            ),
            HeroRibbonSlot(Icons.Rounded.Fingerprint, biometricLabel(facts), "البيومترية", V2Violet),
            HeroRibbonSlot(
                Icons.Rounded.Shield,
                if (facts.computedAt > 0L) formatCount(facts.sensitiveAppCount) else "—",
                "تطبيق حساس",
                V2Pink,
            ),
            HeroRibbonSlot(
                Icons.Rounded.HealthAndSafety,
                if (facts.isLive) "${facts.healthEstimate}%" else "—",
                "تقدير محلي",
                V2Green,
            ),
        ),
        onBack = onBack,
        onPro = onPro,
        action = {
            KnouxRunOrbBlock(
                progress = if (facts.computedAt > 0L) 1f else 0f,
                running = false,
                enabled = facts.computedAt > 0L,
                label = if (facts.computedAt > 0L) "اضغط لإعادة قراءة حالة الأمان" else "جاري القراءة…",
                accent = V2Green,
                accent2 = V2Cyan,
                size = 118.dp,
                onTap = { bump() },
            )
        },
    ) {
        V2LuxurySurface(Modifier.fillMaxWidth(), radius = 20.dp, accent = V2Green, borderAlpha = .45f) {
            Column(Modifier.fillMaxWidth().padding(13.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.HealthAndSafety, null, tint = V2Green, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(7.dp))
                    KText("رادار الحماية", size = 12.5.sp, weight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.height(4.dp))
                SecurityRadar(facts, V2Green)
            }
        }
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
        V2LuxurySurface(Modifier.fillMaxWidth(), radius = 18.dp, accent = V2Green, borderAlpha = .38f) {
            Column(Modifier.fillMaxWidth().padding(13.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Shield, null, tint = V2Green, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(7.dp))
                    KText("ملخص الأمان", size = 12.5.sp, weight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.height(7.dp))
                ExtraInfoLine(
                    "قفل الشاشة",
                    when {
                        !facts.isLive -> "—"
                        facts.lockSecure -> "مؤمّن"
                        else -> "غير مؤمّن"
                    },
                )
                ExtraInfoLine("المصادقة البيومترية", biometricLabel(facts))
                ExtraInfoLine(
                    "شبكة VPN",
                    when {
                        !facts.isLive -> "—"
                        facts.vpnActive -> "نشط"
                        else -> "غير نشط"
                    },
                )
                ExtraInfoLine(
                    "مشرفو الجهاز",
                    if (facts.isLive) formatCount(facts.deviceAdminCount) else "—",
                )
                ExtraInfoLine(
                    "نوافذ عائمة",
                    if (facts.isLive) formatCount(facts.overlayAppCount) else "—",
                )
                ExtraInfoLine(
                    "تطبيقات حساسة",
                    if (facts.computedAt > 0L) formatCount(facts.sensitiveAppCount) else "—",
                )
                ExtraInfoLine(
                    "مجموعات أذونات",
                    if (facts.computedAt > 0L) "${facts.privacyGroups.size} مجموعة" else "—",
                )
                ExtraInfoLine(
                    "وصول الاستخدام",
                    if (facts.usageAccessGranted) "ممنوح" else "غير ممنوح",
                )
                ExtraInfoLine(
                    "تقدير الجهاز",
                    if (facts.isLive) "${facts.healthEstimate}% (محلي)" else "—",
                )
            }
        }
        ExtraHubAction(
            icon = Icons.Rounded.Fingerprint,
            title = "إعدادات الأمان",
            subtitle = "قفل الشاشة والمصادقة البيومترية في نظام Android",
            accent = V2Green,
            onClick = { openSystemSettings(context, Settings.ACTION_SECURITY_SETTINGS) },
        )
        ExtraHubStatStrip(
            facts = facts,
            first = formatCount(facts.sensitiveAppCount) to "تطبيق حساس",
            second = if (facts.usageAccessGranted) "ممنوح" to "وصول الاستخدام" else "غير ممنوح" to "وصول الاستخدام",
            accent = V2Green,
        )
    }
}

/* ------------------------------------------------------------------ *
 * 14 - Backup & restore
 * ------------------------------------------------------------------ */

/**
 * The display name of the SAF tree the user authorised, when the provider
 * exposes one. Falls back to null when the provider does not publish a name —
 * the vault then shows the tree identifier instead of a made-up label.
 */
private fun treeDisplayName(context: Context, tree: Uri): String? = runCatching {
    val documentId = DocumentsContract.getTreeDocumentId(tree)
    val document = DocumentsContract.buildDocumentUriUsingTree(tree, documentId)
    context.contentResolver.query(
        document,
        arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME),
        null,
        null,
        null,
    )?.use { cursor ->
        if (cursor.moveToFirst()) cursor.getString(0) else null
    }
}.getOrNull()

@Composable
fun V2BackupRestoreScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val navigate = LocalServiceNavigate.current

    // The chosen destination survives a restart: the SAF grant is persistable
    // and the tree URI is kept in preferences, then revalidated against the
    // live persisted-permission list every time the screen opens.
    val prefs = remember { context.getSharedPreferences("knoux_backup_v1", Context.MODE_PRIVATE) }
    var pickedTree by remember {
        mutableStateOf(
            prefs.getString("tree_uri", null)
                ?.let { runCatching { Uri.parse(it) }.getOrNull() }
                ?.takeIf { saved ->
                    context.contentResolver.persistedUriPermissions.any { permission -> permission.uri == saved }
                },
        )
    }
    var folderEntries by remember { mutableStateOf(emptyList<ScopedDocument>()) }
    var folderMessage by remember { mutableStateOf<String?>(null) }

    val folderId = remember(pickedTree) {
        pickedTree?.let { runCatching { DocumentsContract.getTreeDocumentId(it) }.getOrNull() }
    }
    val destinationName = remember(pickedTree) {
        pickedTree?.let { tree -> treeDisplayName(context, tree) ?: folderId }
    }

    // The folder picker is the system SAF tree browser. KNOUX only remembers
    // the permission it was granted; it never copies anything on its own.
    val treePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree(),
    ) { uri ->
        if (uri == null) {
            folderMessage = "أُلغي الاختيار؛ لم يتغير شيء"
        } else {
            pickedTree = uri
            folderMessage = null
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                )
            }
            prefs.edit().putString("tree_uri", uri.toString()).apply()
        }
    }

    LaunchedEffect(pickedTree, refresh) {
        val root = pickedTree ?: return@LaunchedEffect
        val id = folderId ?: run {
            folderMessage = "تعذّر قراءة معرّف المجلد المختار"
            return@LaunchedEffect
        }
        folderMessage = null
        try {
            folderEntries = withContext(Dispatchers.IO) { readScopedFolder(context, root, id) }
        } catch (error: Exception) {
            folderEntries = emptyList()
            folderMessage = "تعذّرت قراءة المجلد المصرح به: ${error.message ?: error.javaClass.simpleName}"
        }
    }

    val folderBytes = folderEntries.sumOf { entry -> entry.bytes ?: 0L }

    ExtraHubShell(
        spec = ServiceSpecs.backupRestore,
        status = when {
            pickedTree != null -> "الوجهة مضبوطة عبر SAF"
            facts.computedAt > 0L -> "جاهز · اختر الوجهة"
            else -> "جاري القراءة…"
        },
        statusColor = when {
            pickedTree != null -> V2Green
            facts.computedAt > 0L -> V2Amber
            else -> V2Muted
        },
        metric = "${formatBytes(facts.storage.freeBytes)} متاحة للنسخ",
        ribbonSlots = listOf(
            HeroRibbonSlot(
                Icons.Rounded.FolderOpen,
                if (pickedTree != null) "مختارة" else "—",
                "الوجهة",
                V2Amber,
            ),
            HeroRibbonSlot(Icons.Rounded.Inventory, formatCount(folderEntries.size), "ملف في الوجهة", V2Cyan),
            HeroRibbonSlot(
                Icons.Rounded.Storage,
                if (facts.computedAt > 0L) formatBytes(facts.storage.freeBytes) else "—",
                "مساحة متاحة",
                V2Green,
            ),
            HeroRibbonSlot(Icons.Rounded.Backup, formatBytes(folderBytes), "حجم الوجهة", V2Violet),
        ),
        onBack = onBack,
        onPro = onPro,
        action = {
            KnouxRunOrbBlock(
                progress = if (facts.computedAt > 0L) 1f else 0f,
                running = false,
                enabled = facts.computedAt > 0L,
                label = if (facts.computedAt > 0L) "اضغط لإعادة قراءة المساحة المتاحة" else "جاري القراءة…",
                accent = V2Amber,
                accent2 = V2Violet,
                size = 118.dp,
                onTap = { bump() },
            )
        },
    ) {
        V2LuxurySurface(Modifier.fillMaxWidth(), radius = 20.dp, accent = V2Amber, borderAlpha = .5f) {
            Column(Modifier.fillMaxWidth().padding(13.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Lock, null, tint = V2Amber, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(7.dp))
                    KText("خزنة النسخ الاحتياطي", size = 12.5.sp, weight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.height(7.dp))
                ExtraInfoLine("الوجهة", destinationName ?: "لم تُختر بعد")
                ExtraInfoLine(
                    "محتوى الوجهة",
                    "${formatCount(folderEntries.size)} عنصر · ${formatBytes(folderBytes)}",
                )
                if (pickedTree != null && folderEntries.isEmpty() && folderMessage == null) {
                    ExtraInfoLine("المجلد", "فارغ أو بلا إدخالات مقروءة")
                }
                folderMessage?.let { message -> ExtraInfoLine("القراءة", message) }
                folderEntries
                    .filterNot { entry -> entry.isDirectory }
                    .sortedByDescending { entry -> entry.bytes ?: 0L }
                    .take(5)
                    .forEach { entry ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Rounded.Description, null, tint = V2Cyan, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            KText(
                                entry.name,
                                Modifier.weight(1f),
                                size = 9.5.sp,
                                color = Color.White,
                                maxLines = 1,
                            )
                            KText(
                                entry.bytes?.let(::formatBytes) ?: "حجم غير معلن",
                                size = 9.5.sp,
                                weight = FontWeight.SemiBold,
                                color = V2Amber,
                            )
                        }
                    }
                ExtraInfoLine(
                    "مساحة متاحة",
                    if (facts.computedAt > 0L) formatBytes(facts.storage.freeBytes) else "—",
                )
                ExtraInfoLine(
                    "كاش KNOUX",
                    if (facts.computedAt > 0L) {
                        "${formatBytes(facts.ownCacheBytes)} · ${formatCount(facts.ownCacheFiles)} ملف"
                    } else {
                        "—"
                    },
                )
                ExtraInfoLine("السحابة", "غير مستخدمة — كل شيء محلي")
            }
        }
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
        ExtraHubAction(
            icon = Icons.Rounded.FolderOpen,
            title = "اختيار مجلد الوجهة",
            subtitle = "يفتح منتقي مجلدات Android. لا يُكتب أي ملف قبل موافقتك.",
            accent = V2Amber,
            onClick = { treePicker.launch(null) },
        )
        ExtraHubStatStrip(
            facts = facts,
            first = formatBytes(facts.storage.freeBytes) to "مساحة متاحة",
            second = formatBytes(facts.storage.totalBytes) to "سعة التخزين",
            accent = V2Amber,
        )
    }
}
