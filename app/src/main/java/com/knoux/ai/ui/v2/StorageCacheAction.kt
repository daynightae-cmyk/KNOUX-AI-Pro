package com.knoux.ai.ui.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The only destructive action in the storage service: clearing KNOUX's own
 * cache. It never runs straight from a tap - the user sees the measured
 * amount, confirms, and then the result is re-measured and journaled.
 */
@Composable
fun StorageCacheActionCard(
    state: StorageUiState,
    onPrepare: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onRescan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = when (state.gate) {
        OperationGate.VERIFIED -> V2Green
        OperationGate.PARTIAL -> V2Amber
        OperationGate.ERROR -> V2Pink
        OperationGate.AWAITING_CONSENT -> V2Amber
        else -> V2Cyan
    }

    V2GlassCard(modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = accent, borderAlpha = .34f) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.CleaningServices, null, tint = accent, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(5.dp))
                KText("كاش KNOUX — إجراء بتأكيد", size = 10.5.sp, weight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                KText(formatBytes(state.measuredBefore), size = 9.sp, weight = FontWeight.Bold, color = accent)
            }
            Spacer(Modifier.height(6.dp))
            KText(
                "المساحة المقاسة لكاش التطبيق نفسه فقط. مفيش ملفات شخصية بتتلمس، ومفيش حذف قبل موافقتك.",
                size = 7.6.sp,
                lineHeight = 11.sp,
                color = V2Soft,
            )
            Spacer(Modifier.height(9.dp))

            when (state.gate) {
                OperationGate.IDLE, OperationGate.CANCELLED -> {
                    ActionPill("مراجعة ومسح كاش KNOUX", accent, onPrepare)
                }

                OperationGate.AWAITING_CONSENT -> {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(V2Amber.withAlpha(.10f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.WarningAmber, null, tint = V2Amber, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        KText(
                            "هتمسح ${formatBytes(state.measuredBefore)} من كاش KNOUX. تأكد إنك موافق.",
                            size = 7.6.sp,
                            lineHeight = 10.5.sp,
                            color = V2Soft,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionPill("تأكيد المسح", V2Amber, onConfirm, Modifier.weight(1f))
                        OutlinePill("إلغاء", V2Muted, onCancel, Modifier.weight(1f))
                    }
                }

                OperationGate.RUNNING -> {
                    KText("جاري التنفيذ وإعادة القياس…", size = 8.sp, color = accent)
                }

                OperationGate.VERIFIED, OperationGate.PARTIAL, OperationGate.ERROR -> {
                    KText(
                        state.operationMessage ?: "تم التنفيذ",
                        size = 8.sp,
                        weight = FontWeight.Bold,
                        color = accent,
                    )
                    Spacer(Modifier.height(3.dp))
                    KText(
                        "قبل: ${formatBytes(state.measuredBefore)} · بعد: ${formatBytes(state.measuredAfter ?: 0L)}",
                        size = 7.sp,
                        color = V2Muted,
                    )
                    Spacer(Modifier.height(8.dp))
                    ActionPill("إعادة قراءة التخزين", accent, onRescan)
                }
            }
        }
    }
}

@Composable
private fun ActionPill(
    label: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(accent.withAlpha(.16f))
            .border(1.dp, accent.withAlpha(.45f), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        KText(label, size = 8.sp, weight = FontWeight.Bold, color = accent)
    }
}

@Composable
private fun OutlinePill(
    label: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = .03f))
            .border(1.dp, accent.withAlpha(.35f), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        KText(label, size = 8.sp, weight = FontWeight.Bold, color = accent)
    }
}
