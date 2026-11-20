package com.knoux.ai.ui.v2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R

@Composable
fun V2HomeScreen(
    onNavigate: (String) -> Unit,
    onMenu: () -> Unit,
    onPro: () -> Unit,
    onProfile: () -> Unit,
    onNotifications: () -> Unit,
) {
    val facts = rememberDeviceFacts()
    val identity = remember(facts.computedAt) { sectionIdentity("deepscan", facts) }
    val services = remember {
        listOf(
            ServiceSubItem("تحليل التخزين", "", "hub-storage", Icons.Rounded.Storage, V2Cyan, art = R.drawable.knoux_home_icon_storage),
            ServiceSubItem("الملفات المكررة", "", "hub-duplicates", Icons.Rounded.Layers, V2Violet, art = R.drawable.knoux_home_icon_duplicate),
            ServiceSubItem("بقايا التطبيقات", "", "hub-residue", Icons.Rounded.Folder, V2Pink, art = R.drawable.knoux_svc_residue),
            ServiceSubItem("فحص الخصوصية", "", "hub-privacy", Icons.Rounded.Shield, V2Violet, art = R.drawable.knoux_home_icon_privacy),
            ServiceSubItem("مدير التطبيقات", "", "hub-appmanager", Icons.Rounded.Apps, V2Cyan, art = R.drawable.knoux_svc_apps),
            ServiceSubItem("توفير البطارية", "", "hub-battery", Icons.Rounded.Bolt, V2Green, art = R.drawable.knoux_home_icon_battery),
            ServiceSubItem("التنظيف الذكي", "", "hub-smartclean", Icons.Rounded.CleaningServices, V2Pink, art = R.drawable.knoux_home_icon_clean),
            ServiceSubItem("الفحص العميق", "", "hub-deepscan", Icons.Rounded.Radar, V2Cyan, art = R.drawable.knoux_svc_deepscan),
            ServiceSubItem("التقارير الذكية", "", "hub-reports", Icons.Rounded.BarChart, V2Violet),
            ServiceSubItem("تحسين الأداء", "", "hub-ai", Icons.Rounded.AutoAwesome, V2Pink, art = R.drawable.knoux_home_icon_ai),
        )
    }
    Box(Modifier.fillMaxSize().windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.statusBars)) {
        V2LuxuryHeaderScenery(V2Violet, Modifier.align(Alignment.TopStart))
        Column(Modifier.fillMaxSize()) {
            V2TopBar(
                mode = V2TopMode.HOME,
                onMenu = onMenu,
                onPro = onPro,
                onProfile = onProfile,
                onNotifications = onNotifications,
            )
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                item {
                    V2LuxuryIdentity(
                        title = "KNOUX AI",
                        english = "DEVICE INTELLIGENCE",
                        subtitle = "ذكاء لجهازك · أداء أعلى · حماية أعمق",
                        accent = V2Cyan,
                        home = true,
                    )
                }
                item {
                    V2LuxuryHealthPanel(
                        gaugeLabel = identity.gaugeLabel,
                        gaugePercent = identity.gaugePercent,
                        lines = identity.lines,
                        accent = V2Violet,
                        accent2 = V2Cyan,
                    )
                }
                item {
                    V2LuxuryStage(
                        spec = ServiceSpecs.deepScan,
                        actionLabel = "فحص ذكي",
                        left = SectionSide("توصيات ذكية", "AI INSIGHTS", Icons.Rounded.AutoAwesome, V2Cyan, "hub-ai"),
                        right = SectionSide("حماية فورية", "PRIVACY", Icons.Rounded.Shield, V2Violet, "hub-privacy"),
                        onPrimary = { onNavigate("hub-deepscan") },
                        onLeft = { onNavigate("hub-ai") },
                        onRight = { onNavigate("hub-privacy") },
                    )
                }
                item { V2LuxurySectionTitle("الخدمات الرئيسية", "١٠ أقسام تشغيل", V2Cyan) }
                item { V2LuxuryServiceGrid(services, onNavigate) }
                item { V2LuxuryProBanner(onClick = onPro) }
                item {
                    V2LuxuryStatStrip(
                        listOf(
                            SectionStat(Icons.Rounded.Storage, formatBytes(facts.storage.freeBytes), "مساحة متاحة", V2Cyan),
                            SectionStat(Icons.Rounded.CleaningServices, formatBytes(facts.ownCacheBytes), "كاش KNOUX", V2Pink),
                            SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق ظاهر", V2Violet),
                            SectionStat(Icons.Rounded.Bolt, facts.battery.levelPercent?.let { "$it%" } ?: "—", "مستوى البطارية", V2Green),
                        ),
                    )
                }
                item { LiveReadNotice() }
            }
        }
    }
}

@Composable
internal fun LiveReadNotice() {
    V2LuxurySurface(
        Modifier.fillMaxWidth(),
        radius = 16.dp,
        accent = V2Green,
        borderAlpha = .30f,
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText("قراءة حقيقية من هذا الجهاز", size = 9.8.sp, weight = FontWeight.Bold, color = V2Green)
                Spacer(Modifier.height(1.dp))
                KText(
                    "كل الأرقام أعلاه من مصادر Android المتاحة على هاتفك، ولا يتم رفع أي بيانات.",
                    size = 8.4.sp,
                    lineHeight = 11.5.sp,
                    color = V2Soft,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/* Shared with the ten hubs; the layouts live once in LuxuryV2. */
@Composable
internal fun HealthLine(
    icon: ImageVector,
    label: String,
    value: String,
    progress: Float,
    accent: androidx.compose.ui.graphics.Color,
) {
    V2LuxuryHealthLine(SectionLine(icon, label, value, progress, accent))
}

@Composable
internal fun ProBanner(onClick: () -> Unit) {
    V2LuxuryProBanner(onClick)
}

@Composable
internal fun HomeStat(
    icon: ImageVector,
    value: String,
    label: String,
    accent: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    V2LuxuryStat(icon, value, label, accent, modifier)
}

@Composable
internal fun StatDivider() {
    V2LuxuryStatDivider()
}
