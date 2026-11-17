package com.knoux.ai.ui.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Schedule
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

/* ------------------------------------------------------------------ *
 * Deep Scan — the vertical Batch 01 route.
 *
 * The owner layout is now the real page structure: the service cover with
 * the stag, wordmark and sonar is fixed under the app top bar; the measured
 * stages, results and history scroll under it; the primary action is pinned
 * above the shell bottom bar.
 * ------------------------------------------------------------------ */

@Composable
fun V2DeepScanScreen(onBack: () -> Unit, onOpenSummary: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val controller: DeepScanViewModel = viewModel()
    val state = controller.state
    val listState = rememberLazyListState()
    var history by remember { mutableStateOf(readScanHistory(context)) }

    LaunchedEffect(state) {
        if (state is DeepScanUiState.Completed) {
            history = readScanHistory(context)
        }
    }
    // Starting a scan brings the reader back to the header so the live stage
    // and the moving radar are both on screen; nothing is hidden by the fold.
    LaunchedEffect(state.isRunning()) {
        if (state.isRunning()) {
            listState.animateScrollToItem(0)
        }
    }

    val status = when (state) {
        is DeepScanUiState.Idle -> "جاهز للفحص"
        is DeepScanUiState.Running -> "جاري الفحص"
        is DeepScanUiState.Completed -> "اكتمل الفحص"
        is DeepScanUiState.Cancelled -> "تم الإلغاء"
        is DeepScanUiState.Failed -> "تعذّر إكمال الفحص"
    }
    val statusColor = when (state) {
        is DeepScanUiState.Completed -> V2Green
        is DeepScanUiState.Cancelled -> V2Amber
        is DeepScanUiState.Failed -> V2Red
        is DeepScanUiState.Running -> V2Cyan
        else -> V2Violet
    }
    val metric = when (state) {
        is DeepScanUiState.Idle -> "6 وحدات · قراءة محلية"
        is DeepScanUiState.Running ->
            "${state.completed} من 6 · ${state.stage.label}"
        is DeepScanUiState.Completed ->
            "6 من 6 · ${formatScanTime(state.completedAt)}"
        is DeepScanUiState.Cancelled ->
            "توقف عند ${state.completed} من 6"
        is DeepScanUiState.Failed ->
            "توقف عند ${state.completed} من 6"
    }

    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            FixedServiceScreen(
                spec = ServiceSpecs.deepScan,
                status = status,
                statusColor = statusColor,
                metric = metric,
                scene = { modifier -> DeepScanSonar(state, modifier) },
                listState = listState,
                action = {
                    DeepScanPrimaryAction(
                        state = state,
                        onStart = { controller.start(context) },
                        onCancel = { controller.cancel() },
                    )
                },
            ) {
                when (val current = state) {
                    is DeepScanUiState.Idle -> {
                        ScanReadyCard()
                        ScanModulesCard(completed = 0, active = null)
                    }
                    is DeepScanUiState.Running -> {
                        ScanModulesCard(
                            completed = current.completed,
                            active = current.stage,
                        )
                        ScanLiveCard(
                            completed = current.completed,
                            stage = current.stage,
                        )
                    }
                    is DeepScanUiState.Completed -> {
                        ScanModulesCard(completed = 6, active = null)
                        ScanResultsCard(current.facts, complete = true, measured = 6)
                        V2GhostButton(
                            text = "فتح ملخص الجلسة",
                            onClick = onOpenSummary,
                            icon = Icons.Rounded.Description,
                            height = 44.dp,
                        )
                        ScanHistoryCard(history)
                    }
                    is DeepScanUiState.Cancelled -> {
                        ScanCancelledCard(current.completed)
                        ScanModulesCard(
                            completed = current.completed,
                            active = null,
                        )
                        if (current.completed > 0) {
                            ScanResultsCard(
                                current.facts,
                                complete = false,
                                measured = current.completed,
                            )
                        }
                    }
                    is DeepScanUiState.Failed -> {
                        ScanFailedCard(current.reason)
                        ScanModulesCard(
                            completed = current.completed,
                            active = null,
                        )
                    }
                }
                ServiceSubGrid(ServiceSpecs.deepScan)
                PreviewNote(
                    "كل القيم أعلاه مقروءة من هذا الجهاز لحظة الفحص وحُفظت في سجل محلي. " +
                        "الفحص لا يقرأ بيانات خارج الأذونات المتاحة ولا يدّعي فحص برمجيات خبيثة " +
                        "بدون محرك توقيعات.",
                )
            }
        }
    }
}

@Composable
private fun DeepScanPrimaryAction(
    state: DeepScanUiState,
    onStart: () -> Unit,
    onCancel: () -> Unit,
) {
    when (state) {
        is DeepScanUiState.Running -> V2GhostButton(
            text = "إلغاء الفحص",
            onClick = onCancel,
            icon = Icons.Rounded.Close,
            height = 48.dp,
            modifier = Modifier.testTag("deepscan_cta"),
        )
        is DeepScanUiState.Completed -> V2GradientButton(
            text = "إعادة الفحص العميق",
            onClick = onStart,
            icon = Icons.Rounded.Radar,
            modifier = Modifier.testTag("deepscan_cta"),
        )
        else -> V2GradientButton(
            text = if (state is DeepScanUiState.Cancelled || state is DeepScanUiState.Failed) {
                "بدء فحص جديد"
            } else {
                "بدء الفحص العميق"
            },
            onClick = onStart,
            icon = Icons.Rounded.Radar,
            trailingChevron = true,
            modifier = Modifier.testTag("deepscan_cta"),
        )
    }
}

@Composable
private fun ScanReadyCard() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 20.dp,
        glow = V2Violet,
        borderAlpha = .42f,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Radar, null, tint = V2Cyan, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(7.dp))
                KText("الفحص العميق جاهز", size = 14.5.sp, weight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                KText("لم يبدأ بعد", size = 8.6.sp, color = V2Muted)
            }
            Spacer(Modifier.height(5.dp))
            KText(
                "ستة قراءات متتابعة من هذا الهاتف: البطارية، التخزين، التطبيقات، " +
                    "الخصوصية، الكاش، ثم الوسائط. يبدأ كل شيء بعد ضغطك على زر البدء، " +
                    "ويمكنك إلغاء الفحص في أي لحظة.",
                size = 10.5.sp,
                lineHeight = 15.sp,
                color = V2Soft,
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ReadyChip("قراءة لحظية", V2Cyan)
                ReadyChip("إلغاء فوري", V2Amber)
                ReadyChip("سجل محلي", V2Green)
            }
        }
    }
}

@Composable
private fun ReadyChip(label: String, accent: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(accent.copy(alpha = .12f))
            .border(1.dp, accent.copy(alpha = .30f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        KText(label, size = 8.4.sp, weight = FontWeight.SemiBold, color = accent)
    }
}

@Composable
private fun ScanLiveCard(completed: Int, stage: DeepScanStage) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 18.dp,
        glow = V2Cyan,
        borderAlpha = .34f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Radar, null, tint = V2Cyan, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText("يقرأ الآن: ${stage.label}", size = 12.sp, weight = FontWeight.Bold)
                KText(
                    "أكملت $completed من 6 وحدات. الرادار يتحرك فقط أثناء القراءة الفعلية.",
                    size = 8.8.sp,
                    lineHeight = 12.sp,
                    color = V2Soft,
                )
            }
        }
    }
}

@Composable
private fun ScanModulesCard(completed: Int, active: DeepScanStage?) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 11.dp,
        radius = 18.dp,
        glow = V2Cyan,
        borderAlpha = .28f,
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Verified, null, tint = V2Amber, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(6.dp))
                KText("وحدات الفحص", size = 12.sp, weight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                KText("$completed من 6", size = 8.6.sp, color = V2Cyan)
            }
            Spacer(Modifier.height(8.dp))
            DeepScanStage.values().toList().chunked(3).forEachIndexed { rowIndex, row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { stage ->
                        val index = DeepScanStage.values().indexOf(stage)
                        val done = index < completed
                        val isActive = active == stage && !done
                        Column(
                            Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(
                                    when {
                                        done -> V2Cyan.copy(alpha = .14f)
                                        isActive -> V2Cyan.copy(alpha = .09f)
                                        else -> Color.White.copy(alpha = .04f)
                                    }
                                )
                                .border(
                                    1.dp,
                                    when {
                                        done -> V2Cyan.copy(alpha = .40f)
                                        isActive -> V2Cyan.copy(alpha = .55f)
                                        else -> Color.White.copy(alpha = .10f)
                                    },
                                    RoundedCornerShape(13.dp),
                                )
                                .padding(5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                if (done) Icons.Rounded.Check else stageIcon(stage),
                                null,
                                tint = when {
                                    done -> V2Cyan
                                    isActive -> V2Cyan
                                    else -> V2Soft
                                },
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.height(2.dp))
                            KText(
                                stage.label,
                                size = 8.2.sp,
                                color = if (done || isActive) Color.White else V2Soft,
                                maxLines = 1,
                            )
                            KText(
                                when {
                                    done -> "تم"
                                    isActive -> "يقرأ"
                                    else -> "بانتظار"
                                },
                                size = 7.sp,
                                color = when {
                                    done -> V2Cyan
                                    isActive -> V2Cyan
                                    else -> V2Muted
                                },
                            )
                        }
                    }
                }
                if (rowIndex == 0) Spacer(Modifier.height(6.dp))
            }
        }
    }
}

private fun stageIcon(stage: DeepScanStage) = when (stage) {
    DeepScanStage.BATTERY -> androidx.compose.material.icons.Icons.Rounded.BatteryFull
    DeepScanStage.STORAGE -> androidx.compose.material.icons.Icons.Rounded.Storage
    DeepScanStage.APPS -> androidx.compose.material.icons.Icons.Rounded.Layers
    DeepScanStage.PRIVACY -> androidx.compose.material.icons.Icons.Rounded.Shield
    DeepScanStage.CACHE -> androidx.compose.material.icons.Icons.Rounded.CleaningServices
    DeepScanStage.MEDIA -> androidx.compose.material.icons.Icons.Rounded.Image
}

@Composable
private fun ScanResultsCard(facts: DeviceFacts, complete: Boolean, measured: Int) {
    // Stage index in DeepScanStage; a row is shown as "not read" when the scan
    // stopped before reaching its stage. Never print a zero that was not read.
    val storageRead = measured >= DeepScanStage.STORAGE.ordinal + 1
    val appsRead = measured >= DeepScanStage.APPS.ordinal + 1
    val privacyRead = measured >= DeepScanStage.PRIVACY.ordinal + 1
    val cacheRead = measured >= DeepScanStage.CACHE.ordinal + 1
    val mediaRead = measured >= DeepScanStage.MEDIA.ordinal + 1
    val media = facts.media
    val reclaimable = media.duplicateGroups.sumOf { it.reclaimableBytes }
    val storagePercent = (facts.storage.usedFraction * 100).toInt()
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
                    Box(
                        Modifier
                            .size(width = 3.dp, height = 17.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (complete) V2Green else V2Amber),
                    )
                    Spacer(Modifier.width(6.dp))
                    KText(
                        if (complete) "نتائج الفحص" else "قياسات مكتملة قبل الإلغاء",
                        size = 16.sp,
                        weight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.weight(1f))
                Icon(
                    if (complete) Icons.Rounded.Verified else Icons.Rounded.Schedule,
                    null,
                    tint = if (complete) V2Green else V2Amber,
                    modifier = Modifier.size(15.dp),
                )
            }
            Spacer(Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                ResultRow(
                    Icons.Rounded.Storage,
                    "التخزين",
                    if (storageRead) "${formatBytes(facts.storage.freeBytes)} متاحة" else "لم تُقرأ بعد",
                    if (storageRead) "$storagePercent%" else "—",
                    V2Cyan,
                    measured = storageRead,
                )
                ResultRow(
                    Icons.Rounded.Layers,
                    "التطبيقات",
                    if (appsRead) "${formatCount(facts.installedCount)} مثبتًا" else "لم تُقرأ بعد",
                    if (appsRead) formatCount(facts.userAppCount) else "—",
                    V2Violet,
                    measured = appsRead,
                )
                ResultRow(
                    Icons.Rounded.Shield,
                    "الخصوصية",
                    if (privacyRead) "${formatCount(facts.sensitiveAppCount)} تطبيقًا حساسًا" else "لم تُقرأ بعد",
                    if (privacyRead) formatCount(facts.privacyGroups.sumOf { it.grantedCount }) else "—",
                    V2Amber,
                    measured = privacyRead,
                )
                ResultRow(
                    Icons.Rounded.Description,
                    "ملفات مكررة",
                    when {
                        !mediaRead -> "لم تُقرأ بعد"
                        !media.permissionGranted -> "صلاحية غير ممنوحة"
                        else -> formatBytes(reclaimable)
                    },
                    when {
                        !mediaRead -> "—"
                        media.permissionGranted -> "${media.duplicateGroups.size}"
                        else -> "!"
                    },
                    V2Pink,
                    measured = mediaRead,
                )
                ResultRow(
                    Icons.Rounded.CleaningServices,
                    "كاش KNOUX",
                    if (cacheRead) "${formatCount(facts.ownCacheFiles)} ملفًا" else "لم تُقرأ بعد",
                    if (cacheRead) formatBytes(facts.ownCacheBytes) else "—",
                    V2Blue,
                    measured = cacheRead,
                )
            }
            if (!complete) {
                Spacer(Modifier.height(8.dp))
                KText(
                    "لم يكتمل الفحص، لذلك تعرض الوحدات المقروءة فقط قيمها، والباقي بلا أرقام. " +
                        "لا يُحفظ هذا الفحص في السجل.",
                    size = 7.8.sp,
                    lineHeight = 12.sp,
                    color = V2Amber,
                )
            }
        }
    }
}

@Composable
private fun ResultRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    detail: String,
    value: String,
    accent: Color,
    measured: Boolean = true,
) {
    val tint = if (measured) accent else V2Muted
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(tint.copy(alpha = .16f))
                .border(1.dp, tint.copy(alpha = .34f), RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(13.dp))
        }
        Spacer(Modifier.width(6.dp))
        Column(Modifier.weight(1f)) {
            KText(
                label,
                size = 9.4.sp,
                weight = FontWeight.Bold,
                maxLines = 1,
                color = if (measured) Color.White else V2Soft,
            )
            KText(detail, size = 7.4.sp, color = V2Muted, maxLines = 1)
        }
        Spacer(Modifier.width(4.dp))
        KText(value, size = 10.sp, weight = FontWeight.Bold, color = tint, maxLines = 1)
    }
}

@Composable
private fun ScanCancelledCard(completed: Int) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 18.dp,
        glow = V2Amber,
        borderAlpha = .40f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Close, null, tint = V2Amber, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(7.dp))
            Column(Modifier.weight(1f)) {
                KText("أُلغي الفحص بعد $completed من 6 وحدات", size = 11.5.sp, weight = FontWeight.Bold)
                KText(
                    "الرادار توقف فور الإلغاء. لا يُحفظ هذا الفحص في السجل؛ " +
                        "يمكنك بدء فحص جديد من الزر الثابت أسفل الشاشة.",
                    size = 8.8.sp,
                    lineHeight = 13.sp,
                    color = V2Soft,
                )
            }
        }
    }
}

@Composable
private fun ScanFailedCard(reason: String) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 18.dp,
        glow = V2Red,
        borderAlpha = .40f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Close, null, tint = V2Red, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(7.dp))
            Column(Modifier.weight(1f)) {
                KText("تعذّر إكمال الفحص", size = 11.5.sp, weight = FontWeight.Bold)
                KText(
                    "$reason. القياسات المكتملة فقط هي المعروضة، ولم يُحفظ سجل جديد.",
                    size = 8.8.sp,
                    lineHeight = 13.sp,
                    color = V2Soft,
                )
            }
        }
    }
}

@Composable
private fun ScanHistoryCard(history: List<ScanHistoryEntry>) {
    val latest = history.firstOrNull()
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 17.dp,
        glow = V2Green,
        borderAlpha = .28f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Schedule, null, tint = V2Green, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(7.dp))
            Column(Modifier.weight(1f)) {
                if (latest == null) {
                    KText("لا يوجد فحص محفوظ بعد", size = 11.sp, weight = FontWeight.Bold)
                    KText("سيُحفظ الفحص التالي في السجل المحلي تلقائيًا.", size = 8.2.sp, color = V2Soft)
                } else {
                    KText("آخر فحص محفوظ", size = 11.sp, weight = FontWeight.Bold, color = V2Green)
                    KText(
                        "${formatScanTime(latest.timestamp)} · ${formatCount(latest.appsScanned)} تطبيقًا · " +
                            "${formatBytes(latest.cacheBytes)} كاش",
                        size = 8.2.sp,
                        color = V2Soft,
                    )
                }
            }
            KText("${history.size}", size = 13.sp, weight = FontWeight.Black, color = V2Green)
        }
    }
}
