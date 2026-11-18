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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Compact proof strip under a service banner.
 *
 * Every number here comes from the Room journal written after a real scan or
 * operation on this device - it is never a placeholder or a marketing figure.
 * When nothing has been logged yet the strip stays hidden instead of showing
 * zeros.
 */
@Composable
fun ServiceAuditStrip(
    state: SectionUiState,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    V2GlassCard(
        modifier.fillMaxWidth(),
        padding = 9.dp,
        radius = 17.dp,
        glow = accent,
        borderAlpha = .26f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (state.isScanning) {
                CircularProgressIndicator(
                    Modifier.width(30.dp).height(30.dp),
                    color = accent,
                    strokeWidth = 2.dp,
                )
            } else {
                V2IconTile(Icons.Rounded.FactCheck, accent, size = 31.dp, iconSize = 16.dp)
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KText(
                        "سجل التحقق على هذا الهاتف",
                        size = 9.sp,
                        weight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(accent.withAlpha(.12f))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        KText(
                            if (state.isScanning) "جارٍ التوثيق" else "مكتوب في Room",
                            size = 6.4.sp,
                            weight = FontWeight.Bold,
                            color = accent,
                        )
                    }
                }
                Spacer(Modifier.height(5.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AuditStat("جلسات", state.sessionsLogged, V2Cyan)
                    AuditStat("عناصر", state.itemsLogged, V2Green)
                    AuditStat("عمليات", state.operationsLogged, V2Pink)
                }
            }
        }
    }
}

@Composable
private fun AuditStat(label: String, value: Int, accent: Color) {
    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(accent.withAlpha(.10f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KText(formatCount(value), size = 8.sp, weight = FontWeight.Bold, color = accent)
        Spacer(Modifier.width(3.dp))
        KText(label, size = 6.6.sp, color = V2Soft)
    }
}
