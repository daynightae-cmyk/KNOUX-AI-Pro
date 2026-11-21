package com.knoux.ai.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.knoux.ai.R
import com.knoux.ai.ui.v2.DayViolet
import com.knoux.ai.ui.v2.LocalDayMode
import com.knoux.ai.ui.v2.LocalKnouxOnDark
import com.knoux.ai.ui.v2.V2Violet

/**
 * The official white circular KNOUX seal — the owner's company mark.
 *
 * The pearl disc is self-lit (white face, violet orb mark), so it reads
 * professionally on both the night sky and the porcelain day chrome without
 * re-inking. Around it a breathing halo carries two counter-orbiting arcs
 * and one white satellite, reusing the same halo language as the header.
 * Under Reduce Motion every orbit snaps to its static frame.
 */
@Composable
fun BrandSeal(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    haloColor: Color = V2Violet,
    animated: Boolean = true,
    contentDescription: String? = "ختم KNOUX الرسمي",
) {
    val motion = rememberMotionEnabled() && animated
    val transition = rememberInfiniteTransition(label = "brandSeal")
    val orbit by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) 360f else 0f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
        label = "sealOrbit",
    )
    val counter by transition.animateFloat(
        initialValue = 360f,
        targetValue = if (motion) 0f else 360f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Restart),
        label = "sealCounter",
    )
    val breath by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (motion) 1.045f else 1f,
        animationSpec = infiniteRepeatable(
            tween(2600, easing = FastOutSlowInEasing),
            RepeatMode.Reverse,
        ),
        label = "sealBreath",
    )
    Box(modifier.size(size).scale(breath), contentAlignment = Alignment.Center) {
        HeaderHalo(Modifier.matchParentSize().rotate(orbit), haloColor)
        HeaderHalo(Modifier.matchParentSize().rotate(counter), haloColor.copy(alpha = .6f))
        // On porcelain the pearl disc gets a fine violet rim so its white face
        // never dissolves into the day chrome; on night it keeps a light seam.
        val day = LocalDayMode.current && !LocalKnouxOnDark.current
        Image(
            painter = painterResource(R.drawable.knoux_seal_official),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(size * .82f)
                .clip(CircleShape)
                .border(
                    1.dp,
                    if (day) DayViolet.copy(alpha = .30f) else Color.White.copy(alpha = .14f),
                    CircleShape,
                ),
        )
    }
}
