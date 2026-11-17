package com.knoux.ai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.knoux.ai.R
import com.knoux.ai.ui.components.rememberMotionEnabled
import androidx.compose.ui.unit.dp

@Composable
internal fun AuthStag(modifier: Modifier = Modifier, alpha: Float = 1f) {
    val float = if (rememberMotionEnabled()) {
        rememberInfiniteTransition(label = "entry stag").animateFloat(
            initialValue = 0f,
            targetValue = -3f,
            animationSpec = infiniteRepeatable(tween(4200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "entry stag float",
        )
    } else null
    Image(
        painter = painterResource(R.drawable.knoux_stag_official),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        alpha = alpha,
        modifier = modifier.graphicsLayer { translationY = (float?.value ?: 0f).dp.toPx() },
    )
}
