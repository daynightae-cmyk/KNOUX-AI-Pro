package com.knoux.ai.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.data.Key
import com.knoux.ai.ui.data.MiniStat
import com.knoux.ai.ui.data.PreviewRow
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxPanelAlt
import com.knoux.ai.ui.theme.KnouxPanelDeep
import com.knoux.ai.ui.theme.KnouxSoftText
import com.knoux.ai.ui.theme.KnouxText
import com.knoux.ai.ui.theme.KnouxViolet

fun iconFor(key: Key): ImageVector = when (key) {
    Key.Image -> Icons.Rounded.Image
    Key.Video -> Icons.Rounded.Movie
    Key.Document -> Icons.Rounded.Description
    Key.Audio -> Icons.Rounded.MusicNote
    Key.Share -> Icons.Rounded.Share
    Key.Folder -> Icons.Rounded.Folder
    Key.Storage -> Icons.Rounded.Storage
    Key.Chip -> Icons.Rounded.Memory
    Key.Shield -> Icons.Rounded.Security
    Key.Bolt -> Icons.Rounded.Bolt
    Key.Clock -> Icons.Rounded.Schedule
}

@Composable
fun MiniStatStrip(
    stats: List<MiniStat>,
    modifier: Modifier = Modifier,
    valueColor: Color = KnouxText,
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        stats.forEach { stat ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    iconFor(stat.icon), null,
                    tint = KnouxCyan,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    stat.value,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = valueColor)
                )
                Text(
                    stat.label,
                    style = TextStyle(fontSize = 9.sp, color = KnouxSoftText),
                    maxLines = 2,
                )
            }
        }
    }
}

/** Row used by screens that show a selectable finding (Phase 1 = preview only). */
@Composable
fun SelectableFindingRow(
    row: PreviewRow,
    checked: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        KnouxPanelAlt.copy(alpha = 0.85f),
                        KnouxPanelDeep.copy(alpha = 0.80f)
                    )
                )
            )
            .border(1.dp, KnouxViolet.copy(alpha = 0.30f), RoundedCornerShape(16.dp))
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NeonIconTile(iconFor(row.icon), size = 34.dp, iconSize = 17.dp)
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(
                row.title,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KnouxText)
            )
            Text(
                row.subtitle,
                style = TextStyle(fontSize = 10.sp, lineHeight = 14.sp, color = KnouxSoftText),
                maxLines = 2,
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            row.badge,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = KnouxMagenta)
        )
        Spacer(Modifier.width(8.dp))
        Box(
            Modifier
                .size(21.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (checked)
                        Brush.linearGradient(listOf(KnouxCyan, Color(0xFF48A8FF)))
                    else Brush.linearGradient(
                        listOf(Color(0x33000000), Color(0x22000000))
                    )
                )
                .border(
                    1.dp,
                    if (checked) KnouxCyan else KnouxViolet.copy(alpha = 0.6f),
                    RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Icon(Icons.Rounded.Check, null, tint = Color(0xFF032033), modifier = Modifier.size(14.dp))
            }
        }
    }
}

/** Row used by screens with a metric + inline action pill (no selection state). */
@Composable
fun FeatureRow(
    row: PreviewRow,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        KnouxPanelAlt.copy(alpha = 0.85f),
                        KnouxPanelDeep.copy(alpha = 0.80f)
                    )
                )
            )
            .border(1.dp, KnouxViolet.copy(alpha = 0.30f), RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NeonIconTile(iconFor(row.icon), size = 34.dp, iconSize = 17.dp)
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(
                row.title,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KnouxText)
            )
            Text(
                row.subtitle,
                style = TextStyle(fontSize = 10.sp, lineHeight = 14.sp, color = KnouxSoftText),
                maxLines = 2,
            )
        }
        Spacer(Modifier.width(8.dp))
        if (actionLabel != null && onAction != null) {
            MiniPill(actionLabel, onAction)
        } else {
            Text(
                row.badge,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 11.sp, color = KnouxMagenta)
            )
        }
        if (onClick != null) {
            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Rounded.ChevronRight, null,
                tint = KnouxSoftText.copy(alpha = 0.7f), modifier = Modifier.size(16.dp)
            )
        }
    }
}

/** One line of the "status summary" column (icon + label + value). */
@Composable
fun SummaryLine(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color = KnouxCyan,
    valueColor: Color = KnouxText,
    modifier: Modifier = Modifier,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = iconColor, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            label,
            style = TextStyle(fontSize = 11.sp, color = KnouxSoftText),
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 11.sp, color = valueColor)
        )
    }
}

/** "Select All (n)" pill used above lists of selectable findings. */
@Composable
fun SelectAllPill(label: String, allSelected: Boolean, modifier: Modifier = Modifier, onToggle: () -> Unit) {
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (allSelected)
                    Brush.horizontalGradient(listOf(KnouxCyan, Color(0xFF48A8FF)))
                else Brush.horizontalGradient(listOf(Color(0x1FFFFFFF), Color(0x11FFFFFF)))
            )
            .border(1.dp, KnouxCyan.copy(alpha = if (allSelected) 0.9f else 0.35f), RoundedCornerShape(50))
            .clickable { onToggle() }
            .padding(horizontal = 11.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(
            Modifier
                .size(15.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (allSelected) Color(0xFF032033) else Color.Transparent
                )
                .border(1.dp, if (allSelected) Color(0xFF032033) else KnouxCyan.copy(alpha = 0.6f), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (allSelected) {
                Icon(Icons.Rounded.Check, null, tint = KnouxCyan, modifier = Modifier.size(11.dp))
            }
        }
        Text(
            label,
            style = TextStyle(
                fontWeight = FontWeight.Bold, fontSize = 10.sp,
                color = if (allSelected) Color(0xFF032033) else KnouxText
            )
        )
    }
}
