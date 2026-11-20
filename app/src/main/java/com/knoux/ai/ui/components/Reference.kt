package com.knoux.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
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
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProTextPrimary

/**
 * Small metric tile used by the inner screens to reach the density of the
 * reference designs without turning every number into a giant card.
 */
data class SignalTile(
    val icon: ImageVector,
    val value: String,
    val label: String,
    val tint: Color = ProArcticCyan,
    val trailing: String? = null,
)

@Composable
fun SignalTileCard(tile: SignalTile, modifier: Modifier = Modifier) {
    PremiumGlassCard(
        modifier = modifier,
        padding = 9.dp,
        radius = 14.dp,
        accent = tile.tint,
        borderAlpha = 0.34f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                tile.icon,
                contentDescription = null,
                tint = tile.tint,
                modifier = Modifier.size(15.dp),
            )
            Spacer(Modifier.width(6.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        tile.value,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ProTextPrimary,
                        ),
                        maxLines = 1,
                    )
                    if (tile.trailing != null) {
                        Spacer(Modifier.width(3.dp))
                        Text(
                            tile.trailing,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = tile.tint,
                            ),
                            maxLines = 1,
                        )
                    }
                }
                Text(
                    tile.label,
                    style = TextStyle(fontSize = 9.sp, color = ProTextSecondary),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun SignalTileGrid(
    tiles: List<SignalTile>,
    modifier: Modifier = Modifier,
    columns: Int = 2,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tiles.chunked(columns).forEach { row ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { tile ->
                    SignalTileCard(tile, Modifier.weight(1f))
                }
                repeat(columns - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Insight banner used for the "what the reference calls an AI insight" block.
 * The wording stays honest: it describes what the screen would report.
 */
@Composable
fun InsightBanner(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.AutoAwesome,
    tint: Color = ProArcticCyan,
    badge: String? = null,
) {
    PremiumGlassCard(
        modifier = modifier.fillMaxWidth(),
        padding = 11.dp,
        radius = 18.dp,
        accent = tint,
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        title,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ProTextPrimary,
                        ),
                        maxLines = 1,
                    )
                    if (badge != null) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(tint.copy(alpha = 0.14f))
                                .border(1.dp, tint.copy(alpha = 0.5f), RoundedCornerShape(50))
                                .padding(horizontal = 6.dp, vertical = 1.dp),
                        ) {
                            Text(
                                badge,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.4.sp,
                                    color = tint,
                                ),
                            )
                        }
                    }
                }
                Spacer(Modifier.size(3.dp))
                Text(
                    message,
                    style = TextStyle(
                        fontSize = 9.sp,
                        lineHeight = 12.sp,
                        color = ProTextSecondary,
                    ),
                )
            }
        }
    }
}

/** Small uppercase caption used above a group of rows. */
@Composable
fun ReferenceSectionLabel(text: String, trailing: String? = null, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text.uppercase(),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 1.sp,
                color = ProTextSecondary,
            ),
            maxLines = 1,
        )
        Spacer(Modifier.weight(1f))
        if (trailing != null) {
            Text(
                trailing,
                style = TextStyle(fontSize = 9.sp, color = ProTextSecondary),
                maxLines = 1,
            )
        }
    }
}

/** Filled gradient chip used for severity / status labels. */
@Composable
fun StatusChip(
    text: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(
                    listOf(tint.copy(alpha = 0.30f), tint.copy(alpha = 0.12f))
                )
            )
            .border(1.dp, tint.copy(alpha = 0.55f), RoundedCornerShape(50))
            .padding(horizontal = 7.dp, vertical = 2.dp),
    ) {
        Text(
            text,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = tint,
            ),
            maxLines = 1,
        )
    }
}
