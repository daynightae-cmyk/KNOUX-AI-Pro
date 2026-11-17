package com.knoux.ai.ui.v2

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * A single, bounded artwork footprint for Home and every section hub.
 * This is a background child of the screen's Box: it never measures or
 * moves the toolbar, cards, navigation bar or the app window.
 */
@Composable
internal fun V2HeaderMascot(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.fillMaxWidth().height(286.dp)) {
        val width = minOf(232.dp, maxWidth * .60f)
        V2DeerHeader(
            Modifier.align(Alignment.TopStart)
                .offset(x = (-18).dp, y = 22.dp)
                .size(width = width, height = width * 1.12f)
                .graphicsLayer { alpha = .78f },
        )
    }
}
