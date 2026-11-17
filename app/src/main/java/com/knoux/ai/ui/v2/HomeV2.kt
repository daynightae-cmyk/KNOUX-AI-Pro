package com.knoux.ai.ui.v2

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.theme.KnouxText

private data class HomeServiceMini(
    val title: String,
    val route: String,
    @DrawableRes val art: Int,
    val accent: Color,
    val glyph: ImageVector? = null,
)

private val firstRow = listOf(
    HomeServiceMini("الملفات\nالمكررة", "hub-duplicates", R.drawable.knoux_home_icon_duplicate, V2Cyan),
    HomeServiceMini("بقيا\nالتطبيقات", "hub-residue", R.drawable.knoux_svc_residue, V2Pink),
    HomeServiceMini("فحص\nالخصوصية", "hub-privacy", R.drawable.knoux_home_icon_privacy, V2Violet),
    HomeServiceMini("مدير\nالتطبيقات", "hub-appmanager", R.drawable.knoux_svc_apps, V2Cyan),
    HomeServiceMini("توفير\nالبطارية", "hub-battery", R.drawable.knoux_home_icon_battery, V2Amber),
)

private val secondRow = listOf(
    HomeServiceMini("التنظيف\nالذكي", "hub-smartclean", R.drawable.knoux_home_icon_clean, V2Violet),
    HomeServiceMini("تحليل\nالتخزين", "hub-storage", R.drawable.knoux_home_icon_storage, V2Cyan),
    HomeServiceMini("الفحص\nالعميق", "hub-deepscan", R.drawable.knoux_svc_deepscan, V2Pink),
    HomeServiceMini("التقارير\nالذكية", "hub-reports", 0, V2Violet, Icons.Rounded.BarChart),
    HomeServiceMini("تحسين\nالأداء", "hub-ai", R.drawable.knoux_home_icon_ai, V2Cyan),
)

@Composable
fun V2HomeScreen(
    onNavigate: (String) -> Unit,
    onMenu: () -> Unit,
    onPro: () -> Unit,
    onProfile: () -> Unit,
    onNotifications: () -> Unit,
) {
    val facts = rememberDeviceFacts()
    Box(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        V2DeerHeader(
            Modifier
                .align(Alignment.TopStart)
                .absoluteOffset(x = 2.dp, y = 33.dp)
                .size(width = 170.dp, height = 267.dp)
                .graphicsLayer {
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 0f)
                    scaleX = 1.16f
                    scaleY = 1.16f
                    alpha = .92f
                }
        )

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
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp),
            ) {
                item {
                    Spacer(Modifier.height(2.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        androidx.compose.material3.Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = Color.White)) { append("KNOUX AI ") }
                                withStyle(
                                    SpanStyle(
                                        brush = Brush.horizontalGradient(listOf(Color(0xFFFFE7AA), V2Amber, Color(0xFFFFB043)))
                                    )
                                ) { append("Pro") }
                            },
                            style = TextStyle(
                                fontFamily = V2Font,
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp,
                                lineHeight = 34.sp,
                            ),
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    KText(
                        "ذكاء كامل لجهازك .. أداء أعلى .. حماية أعمق",
                        size = 11.5.sp,
                        color = V2Soft,
                        align = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 1.dp),
                    )
                    Spacer(Modifier.height(9.dp))
                }

                item { DeviceHealthCard(facts) }
                item { Spacer(Modifier.height(10.dp)) }
                item {
                    HomeScanCenterpiece(
                        onScan = { onNavigate("hub-deepscan") },
                        onInsights = { onNavigate("hub-ai") },
                        onProtection = { onNavigate("hub-privacy") },
                    )
                }
                item { Spacer(Modifier.height(11.dp)) }
                item { HomeServiceGridRow(firstRow, onNavigate) }
                item { Spacer(Modifier.height(7.dp)) }
                item { HomeServiceGridRow(secondRow, onNavigate) }
                item { Spacer(Modifier.height(11.dp)) }
                item { ProBanner(onClick = onPro) }
                item { Spacer(Modifier.height(9.dp)) }
                item { V2GlassCard(Modifier.fillMaxWidth(), padding = 9.dp, radius = 16.dp, glow = V2Cyan, borderAlpha = .22f) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        HomeStat(
                            Icons.Rounded.Bolt,
                            facts.battery.levelPercent?.let { "$it%" } ?: "—",
                            "مستوى البطارية",
                            V2Green,
                            Modifier.weight(.9f),
                        )
                        StatDivider()
                        HomeStat(Icons.Rounded.Apps, "${formatCount(facts.installedCount)}", "تطبيق ظاهر", V2Pink, Modifier.weight(.85f))
                        StatDivider()
                        HomeStat(Icons.Rounded.CleaningServices, formatBytes(facts.ownCacheBytes), "كاش KNOUX", V2Cyan, Modifier.weight(.85f))
                        StatDivider()
                        HomeStat(Icons.Rounded.Storage, formatBytes(facts.storage.freeBytes), "مساحة متاحة", V2Violet, Modifier.weight(1f))
                    }
                } }
                item {
                    Spacer(Modifier.height(6.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(11.dp))
                        Spacer(Modifier.width(4.dp))
                        KText(
                            "هذه الأرقام مقروءة من الجهاز الآن، وليست بيانات معاينة.",
                            size = 7.2.sp,
                            color = V2Muted,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceHealthCard(facts: DeviceFacts) {
    val score = facts.healthEstimate
    val storage = facts.storage
    val battery = facts.battery
    V2GlassCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 11.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .52f,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HealthLine(
                    Icons.Rounded.Storage,
                    "التخزين",
                    "${formatBytes(storage.usedBytes)} / ${formatBytes(storage.totalBytes)}",
                    storage.usedFraction,
                    V2Cyan,
                )
                HealthLine(
                    Icons.Rounded.Bolt,
                    "البطارية",
                    battery.levelPercent?.let { "$it% · ${battery.statusLabel}" } ?: "غير متاح",
                    (battery.levelPercent ?: 0) / 100f,
                    V2Green,
                )
                HealthLine(
                    Icons.Rounded.Shield,
                    "أذونات حساسة",
                    "${formatCount(facts.sensitiveAppCount)} تطبيق",
                    if (facts.installedCount > 0) facts.sensitiveAppCount.toFloat() / facts.installedCount else 0f,
                    V2Amber,
                )
            }
            Spacer(Modifier.width(10.dp))
            V2NeonRing(
                progress = score / 100f,
                size = 88.dp,
                stroke = 7.dp,
                colors = listOf(V2Pink, V2Violet, V2Blue, V2Cyan),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText(if (score > 0) "$score%" else "—", size = 26.sp, weight = FontWeight.Black)
                    KText("تقدير الجهاز", size = 8.sp, color = V2Soft)
                    Spacer(Modifier.height(2.dp))
                    Icon(Icons.Rounded.Verified, null, tint = V2Amber, modifier = Modifier.size(11.dp))
                }
            }
        }
    }
}

@Composable
internal fun HealthLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    progress: Float,
    accent: Color,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(accent.copy(alpha = .12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(13.dp))
        }
        Spacer(Modifier.width(7.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                KText(label, size = 9.5.sp, weight = FontWeight.Medium, color = Color.White)
                Spacer(Modifier.weight(1f))
                KText(value, size = 8.sp, color = V2Soft, maxLines = 1)
            }
            Spacer(Modifier.height(3.dp))
            V2ProgressBar(
                progress = progress,
                height = 4.dp,
                colors = listOf(accent, accent.copy(alpha = .62f)),
            )
        }
    }
}

@Composable
private fun HomeScanCenterpiece(
    onScan: () -> Unit,
    onInsights: () -> Unit,
    onProtection: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(186.dp)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(V2Violet.copy(alpha = .30f), V2Cyan.copy(alpha = .08f), Color.Transparent),
                    center = Offset(size.width * .5f, size.height * .52f),
                    radius = size.width * .44f,
                ),
                radius = size.width * .44f,
                center = Offset(size.width * .5f, size.height * .52f),
            )
            drawOval(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, V2Pink.copy(alpha = .48f), V2Cyan.copy(alpha = .52f), Color.Transparent)
                ),
                topLeft = Offset(size.width * .22f, size.height * .80f),
                size = Size(size.width * .56f, 1.5.dp.toPx()),
            )
        }

        SideActionCard(
            title = "توصيات ذكية",
            subtitle = "AI Insights",
            icon = Icons.Rounded.AutoAwesome,
            accent = V2Cyan,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 2.dp, y = 21.dp)
                .graphicsLayer { rotationZ = -3f },
            onClick = onInsights,
        )
        SideActionCard(
            title = "فحص الخصوصية",
            subtitle = "Privacy Sweep",
            icon = Icons.Rounded.Shield,
            accent = V2Blue,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-2).dp, y = 21.dp)
                .graphicsLayer { rotationZ = 3f },
            onClick = onProtection,
        )

        HomeScanOrb(
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 1.dp)
                .clickable(onClick = onScan),
        )
    }
}

@Composable
private fun HomeScanOrb(modifier: Modifier = Modifier) {
    Box(modifier.size(162.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(V2Cyan.copy(alpha = .20f), Color.Transparent),
                    radius = size.width * .52f,
                ),
                radius = size.width * .52f,
            )
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(V2Blue, V2Violet, V2Cyan, V2Pink, V2Blue),
                    center,
                ),
                startAngle = -70f,
                sweepAngle = 305f,
                useCenter = false,
                style = Stroke(4.dp.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
                color = V2Blue.copy(alpha = .28f),
                startAngle = 8f,
                sweepAngle = 336f,
                useCenter = false,
                style = Stroke(9.dp.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
                color = Color.White.copy(alpha = .20f),
                startAngle = 245f,
                sweepAngle = 70f,
                useCenter = false,
                style = Stroke(1.dp.toPx()),
            )
            drawCircle(color = V2Cyan.copy(alpha = .9f), radius = 3.dp.toPx(), center = Offset(size.width * .81f, size.height * .20f))
            drawCircle(color = V2Blue.copy(alpha = .8f), radius = 2.3.dp.toPx(), center = Offset(size.width * .16f, size.height * .33f))
        }
        Box(
            Modifier
                .size(136.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF25115C), Color(0xFF0F082B)),
                    )
                )
                .border(1.dp, V2Blue.copy(alpha = .56f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.knoux_symbol_alpha),
                    contentDescription = "KNOUX AI",
                    modifier = Modifier.size(45.dp),
                )
                Spacer(Modifier.height(3.dp))
                KText("فحص ذكي شامل", size = 12.5.sp, weight = FontWeight.Bold)
                KText("Deep Scan", size = 8.5.sp, color = V2Soft)
                Spacer(Modifier.height(3.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(listOf(V2Pink.copy(alpha = .20f), V2Cyan.copy(alpha = .20f))))
                        .border(1.dp, V2Cyan.copy(alpha = .28f), RoundedCornerShape(50))
                        .padding(horizontal = 9.dp, vertical = 2.dp)
                ) {
                    KText("فتح قسم الفحص", size = 7.5.sp, color = V2Cyan)
                }
            }
        }
    }
}

@Composable
internal fun SideActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    V2GlassCard(
        modifier = modifier
            .width(108.dp)
            .height(77.dp)
            .clickable(onClick = onClick),
        padding = 8.dp,
        radius = 16.dp,
        glow = accent,
        borderAlpha = .58f,
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(3.dp))
            KText(title, size = 10.5.sp, weight = FontWeight.Bold, maxLines = 1)
            KText(subtitle, size = 7.2.sp, color = V2Soft, maxLines = 1)
        }
    }
}

@Composable
private fun HomeServiceGridRow(services: List<HomeServiceMini>, onNavigate: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        services.forEach { service ->
            HomeServiceTile(
                service = service,
                onClick = { onNavigate(service.route) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HomeServiceTile(service: HomeServiceMini, onClick: () -> Unit, modifier: Modifier = Modifier) {
    // The Home tile is the rhythm anchor: every section tile reuses this exact
    // 91.dp height, 15.dp radius and glass surface.
    V2GlassCard(
        modifier = modifier
            .height(91.dp)
            .clickable(onClick = onClick),
        padding = 5.dp,
        radius = 15.dp,
        glow = service.accent,
        borderAlpha = .36f,
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (service.glyph != null) {
                V2NeonGlyph(service.glyph, service.accent, size = 50.dp)
            } else {
                V2RasterTile(image = service.art, size = 52.dp, accent = service.accent)
            }
            Spacer(Modifier.height(2.dp))
            KText(
                service.title,
                size = 8.2.sp,
                weight = FontWeight.SemiBold,
                color = Color.White,
                lineHeight = 10.5.sp,
                align = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

@Composable
internal fun V2NeonGlyph(icon: ImageVector, accent: Color, size: Dp = 46.dp) {
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(accent.copy(alpha = .32f), Color.Transparent),
                    radius = this.size.minDimension * .54f,
                ),
                radius = this.size.minDimension * .54f,
            )
            drawOval(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, accent.copy(alpha = .58f), Color.Transparent)
                ),
                topLeft = Offset(this.size.width * .13f, this.size.height * .92f),
                size = Size(this.size.width * .74f, 2.dp.toPx()),
            )
        }
        Icon(icon, null, tint = accent, modifier = Modifier.size(size * .54f))
    }
}

@Composable
internal fun ProBanner(onClick: () -> Unit) {
    val proTheme = LocalProTheme.current
    val shape = RoundedCornerShape(18.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    if (proTheme.isPro) {
                        listOf(proTheme.deep2, proTheme.panel, proTheme.deep2, proTheme.deep)
                    } else {
                        listOf(
                            Color(0xFF22104D),
                            Color(0xFF3C1C6B),
                            Color(0xFF24104D),
                            Color(0xFF170A38),
                        )
                    }
                )
            )
            .border(1.dp, V2Amber.copy(alpha = .45f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(V2Amber.copy(alpha = .26f), Color(0xFF321C0A))))
                .border(1.dp, V2Amber.copy(alpha = .62f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.ChevronRight, null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            androidx.compose.material3.Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White)) { append("KNOUX AI ") }
                    withStyle(SpanStyle(color = V2GoldText)) { append("Pro") }
                },
                style = TextStyle(
                    fontFamily = V2Font,
                    fontWeight = FontWeight.Black,
                    fontSize = 19.sp,
                ),
            )
            KText("خصائص متقدمة .. أدوات أعمق .. بدون قيود", size = 9.2.sp, color = V2Soft)
        }
        Spacer(Modifier.width(6.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CrownGold(size = 38.dp)
            KText("PRO", size = 6.4.sp, weight = FontWeight.Black, color = V2Amber, letterSpacing = .8.sp)
        }
    }
}

@Composable
internal fun HomeStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(5.dp))
        Column {
            KText(value, size = 10.5.sp, weight = FontWeight.Bold, color = Color.White, maxLines = 1)
            KText(label, size = 7.2.sp, color = V2Soft, maxLines = 1)
        }
    }
}

@Composable
internal fun StatDivider() {
    Box(
        Modifier
            .width(1.dp)
            .height(26.dp)
            .background(Color.White.copy(alpha = .12f))
    )
}
