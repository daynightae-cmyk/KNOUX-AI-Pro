package com.knoux.ai.ui.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.database.AppDatabase
import com.knoux.ai.database.KnouxRepository
import com.knoux.ai.database.OperationRequestEntity

/**
 * Reports surface backed by the Room operation journal.
 *
 * Every row is an operation the user actually confirmed on this device.
 * Predicted bytes and measured bytes are shown as two separate numbers, and
 * cancelled / failed / partial runs stay visible instead of being hidden.
 */
@Composable
fun ReportsRoomAuditCard(
    modifier: Modifier = Modifier,
    limit: Int = 6,
) {
    val context = LocalContext.current
    val database = remember(context) { AppDatabase.getDatabase(context.applicationContext) }
    val repository = remember(database) { KnouxRepository(database) }

    val operations by repository.recentOperations(limit).collectAsState(initial = emptyList())
    val summaries by repository.outcomeSummaries().collectAsState(initial = emptyList())
    val sessions by repository.recentSessions(limit).collectAsState(initial = emptyList())

    val summaryById = remember(summaries) { summaries.associateBy { it.operationId } }
    val measuredTotal = remember(summaries) { summaries.sumOf { it.measuredBytes } }
    val verifiedCount = remember(operations) {
        operations.count { it.status == "VERIFIED" || it.status == "PARTIAL" }
    }

    V2GlassCard(modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .30f) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.History, null, tint = V2Cyan, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(5.dp))
                KText("سجل العمليات من Room", size = 10.5.sp, weight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(V2Cyan.withAlpha(.12f))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    KText("قياس فعلي", size = 6.6.sp, weight = FontWeight.Bold, color = V2Cyan)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ReportAuditStat("عمليات", formatCount(operations.size), V2Pink, Modifier.weight(1f))
                ReportAuditStat("منفذة", formatCount(verifiedCount), V2Green, Modifier.weight(1f))
                ReportAuditStat("مساحة مقيسة", formatBytes(measuredTotal), V2Cyan, Modifier.weight(1f))
                ReportAuditStat("جلسات Room", formatCount(sessions.size), V2Violet, Modifier.weight(1f))
            }
            Spacer(Modifier.height(9.dp))
            if (operations.isEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Inbox, null, tint = V2Muted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    KText(
                        "مفيش عمليات مسجلة لسه. نفّذ خدمة من الأقسام وبعدها هيظهر هنا سجلها الحقيقي.",
                        size = 7.6.sp,
                        lineHeight = 11.sp,
                        color = V2Soft,
                    )
                }
            } else {
                operations.take(limit).forEach { operation ->
                    OperationAuditRow(operation, summaryById[operation.id])
                    Spacer(Modifier.height(5.dp))
                }
            }
        }
    }
}

@Composable
private fun ReportAuditStat(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(accent.withAlpha(.08f))
            .padding(horizontal = 6.dp, vertical = 5.dp)
    ) {
        KText(value, size = 9.5.sp, weight = FontWeight.Bold, color = accent)
        Spacer(Modifier.height(1.dp))
        KText(label, size = 6.4.sp, color = V2Muted)
    }
}

@Composable
private fun OperationAuditRow(
    operation: OperationRequestEntity,
    summary: com.knoux.ai.database.OperationOutcomeSummary?,
) {
    val statusColor = when (operation.status) {
        "VERIFIED" -> V2Green
        "PARTIAL" -> V2Amber
        "ERROR" -> V2Pink
        "CANCELLED" -> V2Muted
        else -> V2Cyan
    }
    val statusText = when (operation.status) {
        "VERIFIED" -> "متحقق"
        "PARTIAL" -> "جزئي"
        "ERROR" -> "خطأ"
        "CANCELLED" -> "ملغي"
        "PENDING" -> "بانتظار الموافقة"
        "OS_CONFIRMATION" -> "بانتظار النظام"
        else -> operation.status
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = .03f))
            .padding(horizontal = 7.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(50))
                .background(statusColor)
        )
        Spacer(Modifier.width(6.dp))
        Column(Modifier.weight(1f)) {
            KText(
                serviceLabel(operation.serviceId),
                size = 8.2.sp,
                weight = FontWeight.Bold,
            )
            Spacer(Modifier.height(1.dp))
            KText(
                "${operation.requestedAction} · ${formatScanTime(operation.createdAt)}",
                size = 6.4.sp,
                color = V2Muted,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            KText(statusText, size = 7.sp, weight = FontWeight.Bold, color = statusColor)
            Spacer(Modifier.height(1.dp))
            KText(
                buildString {
                    append(formatBytes(operation.predictedEligibleBytes ?: 0L))
                    summary?.let { append(" → ${formatBytes(it.measuredBytes)}") }
                },
                size = 6.4.sp,
                color = V2Soft,
            )
        }
    }
}

internal fun serviceLabel(serviceId: String): String = when (serviceId) {
    "storage_analyzer" -> "محلل التخزين"
    "duplicate_finder" -> "كاشف التكرار"
    "app_residue" -> "بقايا التطبيقات"
    "privacy_sweep" -> "فحص الخصوصية"
    "app_manager" -> "مدير التطبيقات"
    "battery_saver" -> "موفر البطارية"
    "deep_scan" -> "الفحص العميق"
    "smart_clean" -> "التنظيف الذكي"
    "reports" -> "التقارير"
    "ai_optimization" -> "تحسين الذكاء"
    else -> serviceId
}

/**
 * Honest coverage of the 150-item catalog: how many items are bound to a real
 * screen and which ones Android (or missing work) still blocks, with reason.
 */
@Composable
fun CatalogCoverageCard(modifier: Modifier = Modifier) {
    val routed = ServiceCatalog150.routedCount
    val total = ServiceCatalog150.total
    val fraction = routed.toFloat() / total.toFloat()
    val blocked = remember { ServiceCatalog150.items.filter { !it.isRouted } }
    val routedCore = remember { ServiceCatalog150.items.count { it.kind == CatalogKind.CORE && it.isRouted } }
    val routedPro = remember { ServiceCatalog150.items.count { it.kind == CatalogKind.PRO && it.isRouted } }

    V2GlassCard(modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .30f) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.FactCheck, null, tint = V2Violet, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(5.dp))
                KText("تغطية كتالوج 150 خدمة", size = 10.5.sp, weight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                KText("$routed/$total", size = 9.sp, weight = FontWeight.Bold, color = V2Violet)
            }
            Spacer(Modifier.height(7.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = .07f))
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(fraction)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(V2Violet)
                )
            }
            Spacer(Modifier.height(7.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ReportAuditStat("CORE مربوط", "$routedCore", V2Cyan, Modifier.weight(1f))
                ReportAuditStat("PRO مربوط", "$routedPro", V2Pink, Modifier.weight(1f))
                ReportAuditStat("غير متاح", "${blocked.size}", V2Amber, Modifier.weight(1f))
            }
            if (blocked.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                blocked.forEach { item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Box(
                            Modifier
                                .padding(top = 3.dp)
                                .size(5.dp)
                                .clip(RoundedCornerShape(50))
                                .background(V2Amber)
                        )
                        Spacer(Modifier.width(6.dp))
                        Column(Modifier.weight(1f)) {
                            KText(
                                "${item.familyLabelAr} · ${item.titleAr}",
                                size = 7.4.sp,
                                weight = FontWeight.Bold,
                            )
                            KText(
                                item.blockReason ?: "غير مربوط بشاشة",
                                size = 6.4.sp,
                                lineHeight = 9.sp,
                                color = V2Muted,
                            )
                        }
                    }
                }
            }
        }
    }
}
