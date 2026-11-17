package com.knoux.ai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.knoux.ai.R

@Composable
internal fun AuthStag(modifier: Modifier = Modifier, alpha: Float = 1f) {
    Image(
        painter = painterResource(R.drawable.knoux_stag_official),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        alpha = alpha,
        modifier = modifier,
    )
}
