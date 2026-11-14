package com.knoux.ai.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.theme.KnouxAmber
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxText
import com.knoux.ai.ui.theme.KnouxViolet

/**
 * The single application header.
 *
 * There is exactly one of these per screen, owned by the navigation level —
 * never duplicated inside a hero or a page body. It renders the official
 * KNOUX AI lockup, an optional back affordance with a 48 dp touch target,
 * the Pro badge and the settings action, inside a single glass capsule.
 */
@Composable
fun KnouxAppHeader(
    modifier: Modifier = Modifier,
    leading: TopLeading = TopLeading.Menu,
    onLeadingClick: () -> Unit = {},
    accent: Color = KnouxViolet,
    showPro: Boolean = true,
    right: @Composable RowScope.() -> Unit = {},
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.88f else 1f,
        animationSpec = tween(140),
        label = "header-leading-press",
    )
    val edgeColor by animateColorAsState(
        targetValue = if (pressed) KnouxCyan else accent,
        animationSpec = tween(260),
        label = "header-edge",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF1A0F3D).copy(alpha = 0.92f),
                        Color(0xFF120B33).copy(alpha = 0.88f),
                        Color(0xFF0D0A2E).copy(alpha = 0.92f),
                    )
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        edgeColor.copy(alpha = 0.55f),
                        Color.White.copy(alpha = 0.10f),
                        KnouxCyan.copy(alpha = 0.22f),
                    )
                ),
                RoundedCornerShape(26.dp),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (leading) {
            TopLeading.Back -> HeaderIconButton(
                icon = Icons.Rounded.ArrowBack,
                contentDescription = "Back",
                onClick = onLeadingClick,
                interaction = interaction,
                pressScale = pressScale,
                accent = edgeColor,
            )
            TopLeading.Menu -> HeaderIconButton(
                icon = Icons.Rounded.Menu,
                contentDescription = "Menu",
                onClick = onLeadingClick,
                interaction = interaction,
                pressScale = pressScale,
                accent = edgeColor,
            )
            TopLeading.None -> Spacer(Modifier.width(44.dp))
        }

        Spacer(Modifier.width(4.dp))

        BrandLockup(symbolSize = 34.dp, wordmarkHeight = 17.dp)

        Spacer(Modifier.weight(1f))

        if (showPro) {
            HeaderProBadge()
            Spacer(Modifier.width(6.dp))
        }
        right()
    }
}

/** 48 dp accessible target with a springy press response. */
@Composable
private fun HeaderIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    interaction: MutableInteractionSource,
    pressScale: Float,
    accent: Color,
) {
    Box(
        Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        listOf(accent.copy(alpha = 0.18f), Color.White.copy(alpha = 0.04f))
                    )
                )
                .border(1.dp, accent.copy(alpha = 0.38f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = contentDescription,
                tint = KnouxText,
                modifier = Modifier.size(19.dp),
            )
        }
    }
}

/** Compact Pro badge sized for the single global header. */
@Composable
private fun HeaderProBadge(modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(
                    listOf(KnouxAmber.copy(alpha = 0.20f), Color(0x14FFC24B))
                )
            )
            .border(1.dp, KnouxAmber.copy(alpha = 0.72f), RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            "PRO",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 0.8.sp,
                color = KnouxAmber,
            ),
        )
    }
}

/**
 * Convenience wrapper that keeps the legacy call sites working while routing
 * every screen through the single canonical header.
 */
@Composable
fun KnouxTopBar(
    modifier: Modifier = Modifier,
    leading: TopLeading = TopLeading.Back,
    onLeadingClick: () -> Unit = {},
    accent: Color = KnouxViolet,
    right: @Composable RowScope.() -> Unit = {},
) {
    KnouxAppHeader(
        modifier = modifier,
        leading = leading,
        onLeadingClick = onLeadingClick,
        accent = accent,
        right = right,
    )
}
