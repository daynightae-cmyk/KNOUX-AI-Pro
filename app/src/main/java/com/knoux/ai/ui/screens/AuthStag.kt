package com.knoux.ai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.knoux.ai.R

@Composable
internal fun AuthStag(modifier: Modifier = Modifier, alpha: Float = 1f) {
    val stag = painterResource(R.drawable.knoux_deer_mascot)
    Image(
        painter = stag,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .graphicsLayer {
                this.alpha = alpha
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithCache {
                // Trace the mascot in source pixels so the scenery stays out of the auth hero.
                val scale = minOf(size.width / 484f, size.height / 760f)
                val left = (size.width - 484f * scale) / 2f
                val top = (size.height - 760f * scale) / 2f
                val transform = android.graphics.Matrix().apply {
                    setScale(scale, scale)
                    postTranslate(left, top)
                }
                val silhouette = PathParser().parsePathString(StagSilhouette).toPath()
                    .asAndroidPath().apply { transform(transform) }.asComposePath()
                // Soften only the edges that touch the original art boundary.
                val sideFade = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, Color.White),
                    startX = left,
                    endX = left + 68f * scale,
                )
                val endFade = Brush.linearGradient(
                    0f to Color.Transparent, 0.065f to Color.White,
                    0.76f to Color.White, 1f to Color.Transparent,
                    start = Offset(0f, top), end = Offset(0f, top + 760f * scale),
                )
                onDrawWithContent {
                    clipPath(silhouette) { this@onDrawWithContent.drawContent() }
                    drawRect(sideFade, blendMode = BlendMode.DstIn)
                    drawRect(endFade, blendMode = BlendMode.DstIn)
                }
            },
    )
}

private val StagSilhouette = """
    M 0 459 C 48 434 85 414 117 404 C 128 390 142 368 146 345
    C 153 331 152 310 162 296 C 164 278 174 259 187 248
    C 159 239 137 215 130 184 C 154 177 188 187 210 206
    C 225 189 264 188 291 208 L 318 198 L 311 229
    C 329 235 348 241 357 248 C 369 263 354 285 344 289
    C 326 293 317 294 305 299 C 295 309 292 325 294 342
    L 303 390 L 294 384 L 307 420 L 299 412 L 307 454 L 298 445
    C 304 474 295 525 278 554 C 278 594 264 624 255 656
    L 242 760 L 216 760 C 208 726 216 700 215 665
    C 216 647 220 635 216 625 C 212 614 204 608 194 609
    C 184 611 172 648 161 685 L 151 736 L 128 730 L 128 666
    C 117 637 115 611 103 589 C 71 572 36 554 0 554 Z

    M 244 212 C 207 182 168 158 129 143 C 57 119 17 78 1 8
    L 1 0 L 12 0 C 20 56 49 87 66 92 C 53 69 48 39 46 0
    L 54 0 C 61 43 69 66 82 74 C 83 47 91 18 106 0 L 116 0
    C 95 35 95 61 99 76 C 126 54 142 22 151 0 L 160 0
    C 147 39 124 71 104 94 C 93 121 126 129 154 137
    C 148 113 151 78 169 54 C 168 75 157 109 166 133
    C 170 150 190 155 202 158 C 210 145 212 117 215 99
    C 225 120 218 144 230 157 C 239 174 251 181 263 197 Z

    M 267 201 C 297 169 307 149 299 124 C 287 104 265 83 254 45
    C 267 60 279 91 296 99 C 307 94 300 64 292 48
    C 311 67 320 93 319 111 C 324 122 331 129 337 126
    C 348 117 355 91 354 76 C 343 44 327 22 323 0 L 334 0
    C 349 25 363 48 369 72 C 378 62 378 35 381 25
    C 388 46 386 68 378 89 C 376 105 372 122 367 133
    C 382 126 393 102 397 76 L 402 60
    C 410 80 402 119 393 137 C 404 130 414 113 421 103
    C 417 130 404 146 384 158 C 344 179 303 174 283 209 Z
""".trimIndent()
