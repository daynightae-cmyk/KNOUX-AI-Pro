package com.knoux.ai.ui.v2

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.components.HeaderHalo
import com.knoux.ai.ui.components.rememberMotionEnabled
import com.knoux.ai.ui.theme.KnouxAmber
import com.knoux.ai.ui.theme.KnouxArabic
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxText
import com.knoux.ai.ui.theme.KnouxViolet
import kotlin.math.cos
import kotlin.math.sin

/* ------------------------------------------------------------------ *
 * KNOUX AI V2 — Arabic reference palette
 * ------------------------------------------------------------------ */

inline fun Color.withAlpha(alpha: Float): Color = Color(this.red, this.green, this.blue, alpha)

val V2Deep = Color(0xFF050317)
val V2Deep2 = Color(0xFF0C0627)
val V2Panel = Color(0xFF160C36)
val V2Panel2 = Color(0xFF0D0728)
val V2GlassTop = Color(0xFF2A195A)
val V2GlassBottom = Color(0xFF0B0626)
val V2Border = Color(0xFF7B4BFF)
val V2Violet = Color(0xFF9D4DFF)
val V2Pink = Color(0xFFFF3FC8)
val V2Cyan = Color(0xFF17E6FF)
val V2Blue = Color(0xFF3F8CFF)
val V2Green = Color(0xFF37F3B4)
val V2Red = Color(0xFFFF5677)
val V2Amber = Color(0xFFFFC857)
val V2Soft = Color(0xFFBDB0E4)
val V2Muted = Color(0xFF8E82B4)
val V2GoldText = Color(0xFFFFD37B)
val V2ProRed = Color(0xFFFF1744)
val V2ProRedDeep = Color(0xFF8B0018)

/* ------------------------------------------------------------------ *
 * Cosmic scenery — layered sky/hero/ridge tones used by the shared
 * shells. Standard values reproduce the approved neon art exactly;
 * Pro values warm the same layers toward the golden horizon instead
 * of bypassing the theme with raw literals.
 * ------------------------------------------------------------------ */

val V2SkyTop = Color(0xFF050210)
val V2SkyMid = Color(0xFF0D0424)
val V2SkyGlow = Color(0xFF1A0638)
val V2SkyBase = Color(0xFF0C0322)
val V2HeroTop = Color(0xFF120633)
val V2HeroMid = Color(0xFF1C0A46)
val V2HeroBase = Color(0xFF0A0424)
val V2RidgeTop = Color(0xFF1B0A3C)
val V2RidgeBase = Color(0xFF080214)
val V2ArtCoreTop = Color(0xFF1A0A3A)
val V2ArtCoreMid = Color(0xFF0D0318)
val V2ArtCoreBase = Color(0xFF050108)
val V2SonarGlass = Color(0xFF261152)
val V2OrbCore = Color(0xFF321374)
val V2OrbMid = Color(0xFF110735)
val V2SurfaceTop = Color(0xFF211353)
val V2SurfaceMid = Color(0xFF130A34)
val V2SurfaceBase = Color(0xFF09051F)
val V2SurfaceSeam = Color(0xFF3F3A92)
val V2MountainTint = Color(0xFF351079)
val V2GaugeCore = Color(0xFF10072F)
val V2GaugeRim = Color(0xFF060315)
val V2BronzeEdge = Color(0xFF5A3108)
val V2BronzeMid = Color(0xFF1C103C)
val V2BarTop = Color(0xFF291453)

/* ------------------------------------------------------------------ *
 * Pro Identity — complete brand change when Pro activates
 * ------------------------------------------------------------------ */

// Pro identity: the original violet-neon world stays the operational surface;
// gold/bronze is reserved for Pro accents (crown, entitlement, subscription card)
// and a warm horizon in the background — never a flat brown wash over every card.
val ProDeep = Color(0xFF0A0522)
val ProDeep2 = Color(0xFF150A34)
val ProPanel = Color(0xFF241238)
val ProPanel2 = Color(0xFF1A0C30)
val ProGlassTop = Color(0xFF3A2258)
val ProGlassBottom = Color(0xFF140A2C)
val ProBorder = Color(0xFFA86BFF)
val ProViolet = Color(0xFFFFB043)
val ProPink = Color(0xFFFF8A6B)
val ProCyan = Color(0xFFFFD37B)
val ProBlue = Color(0xFFFF9F3A)
val ProGreen = Color(0xFF37F3B4)
val ProRed = Color(0xFFFF5677)
val ProAmber = Color(0xFFFFC857)
val ProSoft = Color(0xFFCBB8E8)
val ProMuted = Color(0xFF9A8CC0)
val ProGold = Color(0xFFFFD37B)
val ProGoldBright = Color(0xFFFFE9B0)

/**
 * Pro identity configuration - provides gold theme when Pro is active.
 * Use LocalProTheme.current to access Pro-aware colors throughout the app.
 */
data class ProTheme(
    val isPro: Boolean,
    val deep: Color,
    val deep2: Color,
    val panel: Color,
    val panel2: Color,
    val glassTop: Color,
    val glassBottom: Color,
    val border: Color,
    val violet: Color,
    val pink: Color,
    val cyan: Color,
    val blue: Color,
    val green: Color,
    val red: Color,
    val amber: Color,
    val soft: Color,
    val muted: Color,
    val gold: Color,
    val goldBright: Color,
    val skyTop: Color,
    val skyMid: Color,
    val skyGlow: Color,
    val skyBase: Color,
    val heroTop: Color,
    val heroMid: Color,
    val heroBase: Color,
    val ridgeTop: Color,
    val ridgeBase: Color,
    val artCoreTop: Color,
    val artCoreMid: Color,
    val artCoreBase: Color,
    val sonarGlass: Color,
    val orbCore: Color,
    val orbMid: Color,
    val surfaceTop: Color,
    val surfaceMid: Color,
    val surfaceBase: Color,
    val surfaceSeam: Color,
    val mountainTint: Color,
    val gaugeCore: Color,
    val gaugeRim: Color,
    val bronzeEdge: Color,
    val bronzeMid: Color,
    val barTop: Color,
) {
    companion object {
        val Standard = ProTheme(
            isPro = false,
            deep = V2Deep, deep2 = V2Deep2, panel = V2Panel, panel2 = V2Panel2,
            glassTop = V2GlassTop, glassBottom = V2GlassBottom, border = V2Border,
            violet = V2Violet, pink = V2Pink, cyan = V2Cyan, blue = V2Blue,
            green = V2Green, red = V2Red, amber = V2Amber, soft = V2Soft, muted = V2Muted,
            gold = V2GoldText, goldBright = Color(0xFFFFE9B0),
            skyTop = V2SkyTop, skyMid = V2SkyMid, skyGlow = V2SkyGlow, skyBase = V2SkyBase,
            heroTop = V2HeroTop, heroMid = V2HeroMid, heroBase = V2HeroBase,
            ridgeTop = V2RidgeTop, ridgeBase = V2RidgeBase,
            artCoreTop = V2ArtCoreTop, artCoreMid = V2ArtCoreMid, artCoreBase = V2ArtCoreBase,
            sonarGlass = V2SonarGlass, orbCore = V2OrbCore, orbMid = V2OrbMid,
            surfaceTop = V2SurfaceTop, surfaceMid = V2SurfaceMid, surfaceBase = V2SurfaceBase,
            surfaceSeam = V2SurfaceSeam, mountainTint = V2MountainTint,
            gaugeCore = V2GaugeCore, gaugeRim = V2GaugeRim,
            bronzeEdge = V2BronzeEdge, bronzeMid = V2BronzeMid, barTop = V2BarTop,
        )
        val Pro = ProTheme(
            isPro = true,
            deep = ProDeep, deep2 = ProDeep2, panel = ProPanel, panel2 = ProPanel2,
            glassTop = ProGlassTop, glassBottom = ProGlassBottom, border = ProBorder,
            // Operational language stays Electric Blue / Cyan even in Pro; the
            // golden-brown world lives in the surfaces and the gold accent slots,
            // never in CTAs, progress, selection or focus (design DNA).
            violet = V2Violet, pink = V2Pink, cyan = V2Cyan, blue = V2Blue,
            green = ProGreen, red = ProRed, amber = ProAmber, soft = ProSoft, muted = ProMuted,
            gold = ProGold, goldBright = ProGoldBright,
            // Warm-horizon variants of the same scenery layers; gold stays an
            // accent over a deep plum sky, never a flat brown wash.
            skyTop = Color(0xFF0A0416), skyMid = Color(0xFF160A28),
            skyGlow = Color(0xFF2E1638), skyBase = Color(0xFF140826),
            heroTop = Color(0xFF1A0C30), heroMid = Color(0xFF2A1440),
            heroBase = Color(0xFF120626),
            ridgeTop = Color(0xFF2E1638), ridgeBase = Color(0xFF0C0416),
            artCoreTop = Color(0xFF2E1638), artCoreMid = Color(0xFF160820),
            artCoreBase = Color(0xFF080208),
            sonarGlass = Color(0xFF341A40), orbCore = Color(0xFF3A1E44),
            orbMid = Color(0xFF1A0C30),
            surfaceTop = Color(0xFF2E1840), surfaceMid = Color(0xFF1D0E32),
            surfaceBase = Color(0xFF100620), surfaceSeam = Color(0xFF7A5230),
            mountainTint = Color(0xFF5A3108),
            gaugeCore = Color(0xFF1A0C30), gaugeRim = Color(0xFF0A0416),
            bronzeEdge = V2BronzeEdge, bronzeMid = V2BronzeMid,
            barTop = ProGlassTop,
        )
    }
}

val LocalProTheme = staticCompositionLocalOf { ProTheme.Standard }

/** The Arabic UI face used by every V2 text node. */
val V2Font = KnouxArabic

private val ArabicLetters =
    Regex("[\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF\\uFB50-\\uFDFF\\uFE70-\\uFEFF]")

/**
 * Keeps latin units glued to their number inside Arabic sentences, so "2.8 GB"
 * never flips to "GB 2.8" when the paragraph direction is right-to-left.
 */
private val LatinUnitToken =
    Regex("(\\d+(?:[.,]\\d+)?)\\s?(%|°C?|GB|MB|KB|TB|mAh|MHz|GHz|fps)(?![\\p{L}\\p{N}])")

fun String.v2Bidi(): String {
    if (!ArabicLetters.containsMatchIn(this)) return this
    return LatinUnitToken.replace(this) { "\u2066${it.value}\u2069" }
}

fun kStyle(
    size: TextUnit = 12.sp,
    weight: FontWeight = FontWeight.Normal,
    color: Color = KnouxText,
    lineHeight: TextUnit = (size.value * 1.35f).sp,
    letterSpacing: TextUnit = 0.sp,
): TextStyle = TextStyle(
    fontFamily = V2Font,
    fontSize = size,
    fontWeight = weight,
    color = color,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
    textDirection = TextDirection.Content,
)

@Composable
fun KText(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 12.sp,
    weight: FontWeight = FontWeight.Normal,
    color: Color = KnouxText,
    lineHeight: TextUnit = (size.value * 1.35f).sp,
    align: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    letterSpacing: TextUnit = 0.sp,
    textDirection: TextDirection? = null,
) {
    val resolved = text.v2Bidi()
    // A Latin-only label inside an RTL row would infer an LTR paragraph and
    // then align to the left of its slot, colliding with the value. Callers
    // that know the row direction can pin it explicitly.
    val direction = textDirection
        ?: if (ArabicLetters.containsMatchIn(text)) TextDirection.Rtl else TextDirection.Ltr
    // One palette switch for every text node: when Pro is active the standard
    // neon accents resolve to their gold/amber counterparts without touching
    // each screen. In standard mode every mapping is the identity.
    val proTheme = LocalProTheme.current
    val themedColor = when (color) {
        KnouxText -> proTheme.soft
        V2Cyan -> proTheme.cyan
        V2Pink -> proTheme.pink
        V2Violet -> proTheme.violet
        V2Blue -> proTheme.blue
        V2Amber -> proTheme.amber
        V2Green -> proTheme.green
        V2Red -> proTheme.red
        V2Soft -> proTheme.soft
        V2Muted -> proTheme.muted
        V2GoldText -> proTheme.gold
        else -> color
    }
    // Day world: chrome text turns deep ink and neon accents deepen for
    // contrast on porcelain. Dark islands (cosmic stage, orb jewel, gradient
    // CTAs) keep their night text untouched via LocalKnouxOnDark.
    val dayMode = LocalDayMode.current
    val onDark = LocalKnouxOnDark.current
    val finalColor = when {
        !dayMode -> themedColor
        onDark -> if (color == Color.White) Color.White else themedColor
        color == Color.White -> DayInk
        else -> dayAccent(themedColor)
    }
    // Single-line labels must ellipsize instead of hard-clipping mid-glyph;
    // callers can still pin Clip explicitly where a precise cut is intended.
    val resolvedOverflow =
        if (overflow == TextOverflow.Clip && maxLines == 1) TextOverflow.Ellipsis else overflow
    Text(
        text = resolved,
        modifier = modifier,
        style = kStyle(size, weight, finalColor, lineHeight, letterSpacing)
            .copy(textDirection = direction),
        textAlign = align,
        maxLines = maxLines,
        overflow = resolvedOverflow,
    )
}

/* ------------------------------------------------------------------ *
 * Background
 * ------------------------------------------------------------------ */

@Composable
fun V2Background(modifier: Modifier = Modifier) {
    val proTheme = LocalProTheme.current
    val isPro = proTheme.isPro
    val day = LocalDayMode.current
    Canvas(modifier.fillMaxSize()) {
        if (day) {
            // Porcelain morning: warm paper sky, a soft gold horizon and the
            // same mountain silhouette in lavender mist. Stars become faint
            // dust; the energy ribbon keeps its neon accent, deepened.
            drawRect(
                Brush.verticalGradient(
                    0f to DayPaperTop,
                    0.34f to DayPaperMid,
                    0.70f to DayPaperBase,
                    1f to DayHorizon,
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(DayCyan.withAlpha(.10f), Color.Transparent),
                    center = Offset(size.width * 0.82f, size.height * 0.12f),
                    radius = size.width * 0.68f,
                ),
                radius = size.width * 0.68f,
                center = Offset(size.width * 0.82f, size.height * 0.12f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(DayPink.withAlpha(.08f), Color.Transparent),
                    center = Offset(size.width * 0.08f, size.height * 0.35f),
                    radius = size.width * 0.70f,
                ),
                radius = size.width * 0.70f,
                center = Offset(size.width * 0.08f, size.height * 0.35f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(DayAmber.withAlpha(if (isPro) .20f else .14f), Color.Transparent),
                    center = Offset(size.width * 0.50f, size.height * 0.97f),
                    radius = size.width * 0.92f,
                ),
                radius = size.width * 0.92f,
                center = Offset(size.width * 0.50f, size.height * 0.97f),
            )
            drawDayMountains()
            drawDayDust()
            drawEnergyRibbon(isPro, proTheme)
            return@Canvas
        }
        drawRect(
            Brush.verticalGradient(
                0f to (if (isPro) Color(0xFF0A0522) else Color(0xFF060318)),
                0.30f to (if (isPro) Color(0xFF150A34) else V2Deep2),
                0.67f to (if (isPro) Color(0xFF241238) else Color(0xFF10062F)),
                1f to (if (isPro) Color(0xFF3A2208) else Color(0xFF170940)),
            )
        )

        if (isPro) {
            // Pro keeps the original violet night world (as the reference) and
            // layers the brown-gold warmth on the horizon, the stag glow and the
            // glass — "البني واللون الأصلي" together, not a flat brown wash.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF7A2DFF).withAlpha(0.20f), Color.Transparent),
                    center = Offset(size.width * 0.82f, size.height * 0.12f),
                    radius = size.width * 0.68f,
                ),
                radius = size.width * 0.68f,
                center = Offset(size.width * 0.82f, size.height * 0.12f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(proTheme.pink.withAlpha(0.14f), Color.Transparent),
                    center = Offset(size.width * 0.08f, size.height * 0.35f),
                    radius = size.width * 0.70f,
                ),
                radius = size.width * 0.70f,
                center = Offset(size.width * 0.08f, size.height * 0.35f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(proTheme.amber.withAlpha(0.26f), Color.Transparent),
                    center = Offset(size.width * 0.50f, size.height * 0.97f),
                    radius = size.width * 0.92f,
                ),
                radius = size.width * 0.92f,
                center = Offset(size.width * 0.50f, size.height * 0.97f),
            )
        } else {
            // Standard background
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(V2Cyan.withAlpha( 0.12f), Color.Transparent),
                    center = Offset(size.width * 0.82f, size.height * 0.12f),
                    radius = size.width * 0.68f,
                ),
                radius = size.width * 0.68f,
                center = Offset(size.width * 0.82f, size.height * 0.12f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(V2Pink.withAlpha( 0.12f), Color.Transparent),
                    center = Offset(size.width * 0.08f, size.height * 0.35f),
                    radius = size.width * 0.70f,
                ),
                radius = size.width * 0.70f,
                center = Offset(size.width * 0.08f, size.height * 0.35f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF7A2DFF).withAlpha( 0.24f), Color.Transparent),
                    center = Offset(size.width * 0.50f, size.height * 0.95f),
                    radius = size.width * 0.90f,
                ),
                radius = size.width * 0.90f,
                center = Offset(size.width * 0.50f, size.height * 0.95f),
            )
        }

        drawMountains(isPro)
        drawStars(isPro, proTheme)
        drawEnergyRibbon(isPro, proTheme)
    }
}

private fun DrawScope.drawDayMountains() {
    val back = Path().apply {
        moveTo(0f, size.height * 0.20f)
        lineTo(size.width * 0.11f, size.height * 0.13f)
        lineTo(size.width * 0.19f, size.height * 0.19f)
        lineTo(size.width * 0.31f, size.height * 0.09f)
        lineTo(size.width * 0.43f, size.height * 0.18f)
        lineTo(size.width * 0.55f, size.height * 0.12f)
        lineTo(size.width * 0.69f, size.height * 0.21f)
        lineTo(size.width * 0.84f, size.height * 0.11f)
        lineTo(size.width, size.height * 0.17f)
        lineTo(size.width, 0f)
        lineTo(0f, 0f)
        close()
    }
    drawPath(
        back,
        Brush.verticalGradient(
            listOf(Color(0xFFB9A6DE).withAlpha(.30f), Color(0xFFB9A6DE).withAlpha(0f)),
            startY = 0f,
            endY = size.height * 0.25f,
        )
    )
    val front = Path().apply {
        moveTo(0f, size.height * 0.26f)
        lineTo(size.width * 0.09f, size.height * 0.21f)
        lineTo(size.width * 0.18f, size.height * 0.25f)
        lineTo(size.width * 0.30f, size.height * 0.16f)
        lineTo(size.width * 0.45f, size.height * 0.26f)
        lineTo(size.width * 0.60f, size.height * 0.19f)
        lineTo(size.width * 0.77f, size.height * 0.28f)
        lineTo(size.width * 0.89f, size.height * 0.20f)
        lineTo(size.width, size.height * 0.25f)
        lineTo(size.width, 0f)
        lineTo(0f, 0f)
        close()
    }
    drawPath(
        front,
        Brush.verticalGradient(
            listOf(Color(0xFF8F76C9).withAlpha(.22f), Color(0xFF8F76C9).withAlpha(0f)),
            startY = 0f,
            endY = size.height * 0.30f,
        )
    )
}

private fun DrawScope.drawDayDust() {
    val motes = listOf(
        .09f to .08f, .23f to .16f, .38f to .07f, .55f to .13f, .71f to .05f,
        .87f to .15f, .14f to .34f, .44f to .40f, .78f to .33f, .30f to .58f,
        .62f to .62f, .90f to .55f,
    )
    motes.forEachIndexed { index, (x, y) ->
        drawCircle(
            color = (if (index % 4 == 0) DayGold else DayViolet).withAlpha(.16f),
            radius = if (index % 5 == 0) 1.4.dp.toPx() else .8.dp.toPx(),
            center = Offset(size.width * x, size.height * y),
        )
    }
}

private fun DrawScope.drawMountains(isPro: Boolean) {
    val back = Path().apply {
        moveTo(0f, size.height * 0.20f)
        lineTo(size.width * 0.11f, size.height * 0.13f)
        lineTo(size.width * 0.19f, size.height * 0.19f)
        lineTo(size.width * 0.31f, size.height * 0.09f)
        lineTo(size.width * 0.43f, size.height * 0.18f)
        lineTo(size.width * 0.55f, size.height * 0.12f)
        lineTo(size.width * 0.69f, size.height * 0.21f)
        lineTo(size.width * 0.84f, size.height * 0.11f)
        lineTo(size.width, size.height * 0.17f)
        lineTo(size.width, 0f)
        lineTo(0f, 0f)
        close()
    }
    drawPath(
        back,
        Brush.verticalGradient(
            listOf(
                (if (isPro) Color(0xFF3A1E6E) else Color(0xFF2A1661)).withAlpha(0.62f),
                (if (isPro) Color(0xFF0A0524) else Color(0xFF0A0524)).withAlpha(0f),
            ),
            startY = 0f,
            endY = size.height * 0.25f,
        )
    )
    val front = Path().apply {
        moveTo(0f, size.height * 0.26f)
        lineTo(size.width * 0.09f, size.height * 0.21f)
        lineTo(size.width * 0.18f, size.height * 0.25f)
        lineTo(size.width * 0.30f, size.height * 0.16f)
        lineTo(size.width * 0.45f, size.height * 0.26f)
        lineTo(size.width * 0.60f, size.height * 0.19f)
        lineTo(size.width * 0.77f, size.height * 0.28f)
        lineTo(size.width * 0.89f, size.height * 0.20f)
        lineTo(size.width, size.height * 0.25f)
        lineTo(size.width, 0f)
        lineTo(0f, 0f)
        close()
    }
    drawPath(
        front,
        Brush.verticalGradient(
            listOf(
                (if (isPro) Color(0xFF1A0C3C) else Color(0xFF10072E)).withAlpha(0.92f),
                (if (isPro) Color(0xFF07031A) else Color(0xFF07031A)).withAlpha(0f),
            ),
            startY = 0f,
            endY = size.height * 0.30f,
        )
    )
}

private fun DrawScope.drawStars(isPro: Boolean, proTheme: ProTheme) {
    val stars = listOf(
        .05f to .06f, .13f to .15f, .21f to .05f, .29f to .18f, .38f to .08f,
        .49f to .14f, .58f to .04f, .66f to .20f, .74f to .09f, .84f to .16f,
        .92f to .06f, .97f to .22f, .08f to .30f, .17f to .39f, .28f to .34f,
        .36f to .46f, .47f to .33f, .59f to .44f, .71f to .36f, .82f to .47f,
        .94f to .35f, .09f to .57f, .23f to .63f, .41f to .58f, .57f to .68f,
        .75f to .61f, .88f to .70f, .12f to .81f, .33f to .88f, .63f to .84f,
        .86f to .91f,
    )
    stars.forEachIndexed { index, (x, y) ->
        drawCircle(
            color = if (index % 5 == 0) {
                (if (isPro) proTheme.gold else V2Cyan).withAlpha(.40f)
            } else Color.White.withAlpha(.18f),
            radius = if (index % 7 == 0) 1.2.dp.toPx() else .7.dp.toPx(),
            center = Offset(size.width * x, size.height * y),
        )
    }
}

private fun DrawScope.drawEnergyRibbon(isPro: Boolean, proTheme: ProTheme) {
    val path = Path().apply {
        moveTo(-size.width * .1f, size.height * .57f)
        cubicTo(
            size.width * .18f, size.height * .44f,
            size.width * .38f, size.height * .64f,
            size.width * .62f, size.height * .51f,
        )
        cubicTo(
            size.width * .82f, size.height * .41f,
            size.width * .94f, size.height * .47f,
            size.width * 1.1f, size.height * .32f,
        )
    }
    drawPath(
        path = path,
        brush = Brush.linearGradient(
            listOf(
                Color.Transparent,
                (if (isPro) proTheme.pink else V2Pink).withAlpha(.20f),
                (if (isPro) proTheme.cyan else V2Cyan).withAlpha(.15f),
                Color.Transparent,
            ),
            start = Offset(0f, size.height * .60f),
            end = Offset(size.width, size.height * .38f),
        ),
        style = Stroke(width = 1.2.dp.toPx()),
    )
}

/**
 * A stylised neon stag. The supplied reference uses a rendered deer; this
 * native vector keeps the same silhouette and lighting language without
 * adding a 2 MB bitmap or claiming to reproduce artwork we do not own.
 */
@Composable
fun StagCrest(
    modifier: Modifier = Modifier,
    tint: Color = V2Cyan,
    accent: Color = V2Violet,
    glowAlpha: Float = .20f,
) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val neck = Path().apply {
            moveTo(w * .56f, h)
            cubicTo(w * .55f, h * .76f, w * .58f, h * .57f, w * .51f, h * .43f)
            cubicTo(w * .45f, h * .30f, w * .39f, h * .24f, w * .32f, h * .25f)
            cubicTo(w * .24f, h * .26f, w * .20f, h * .33f, w * .25f, h * .38f)
            cubicTo(w * .29f, h * .42f, w * .34f, h * .39f, w * .35f, h * .34f)
            cubicTo(w * .43f, h * .44f, w * .43f, h * .65f, w * .32f, h)
            close()
        }
        drawPath(neck, Brush.verticalGradient(listOf(accent.withAlpha( .88f), Color(0xFF1A0C3B))))
        drawPath(neck, Color.White.withAlpha( .35f), style = Stroke(1.2.dp.toPx()))

        // ears
        val ear = Path().apply {
            moveTo(w * .33f, h * .28f)
            quadraticTo(w * .25f, h * .17f, w * .20f, h * .22f)
            quadraticTo(w * .24f, h * .30f, w * .32f, h * .32f)
            close()
        }
        drawPath(ear, tint.withAlpha( .75f))

        // antlers
        fun antler(points: List<Pair<Float, Float>>) {
            val p = Path().apply {
                moveTo(w * .33f, h * .26f)
                points.forEach { (x, y) -> lineTo(w * x, h * y) }
            }
            drawPath(
                p,
                brush = Brush.linearGradient(listOf(tint, accent)),
                style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            drawPath(
                p,
                color = tint.withAlpha( glowAlpha),
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
        antler(listOf(.34f to .15f, .24f to .10f, .22f to .03f))
        antler(listOf(.34f to .16f, .42f to .10f, .46f to .02f))
        antler(listOf(.31f to .19f, .18f to .16f, .12f to .08f))
        antler(listOf(.36f to .18f, .48f to .15f, .56f to .07f))
        antler(listOf(.28f to .22f, .16f to .24f, .10f to .19f))
        antler(listOf(.39f to .21f, .52f to .24f, .61f to .20f))
        antler(listOf(.24f to .10f, .15f to .04f))
        antler(listOf(.46f to .02f, .53f to .00f))

        // eye / nose lights
        drawCircle(Color.White.withAlpha( .92f), radius = w * .012f, center = Offset(w * .27f, h * .32f))
        drawCircle(tint, radius = 2.dp.toPx(), center = Offset(w * .23f, h * .35f))
        drawCircle(tint.withAlpha( .55f), radius = 18.dp.toPx(), center = Offset(w * .33f, h * .28f))
    }
}

/* ------------------------------------------------------------------ *
 * Header
 * ------------------------------------------------------------------ */

enum class V2TopMode { HOME, INTERIOR }

@Composable
fun V2TopBar(
    mode: V2TopMode,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onProfile: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onMenu: () -> Unit = {},
    onPro: () -> Unit = {},
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 16.dp),
    ) {
        Row(
            Modifier.align(Alignment.CenterStart),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CrownHexButton(onClick = onPro)
            HeaderIcon(Icons.Rounded.Menu, "القائمة", onMenu)
        }

        Row(
            Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            BrandLockupV2()
        }

        if (mode == V2TopMode.HOME) {
            Row(
                Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    HeaderIcon(Icons.Rounded.Person, "الحساب", onProfile)
                    Spacer(Modifier.width(7.dp))
                    Box {
                        HeaderIcon(Icons.Rounded.NotificationsNone, "التنبيهات", onNotifications)
                        Box(
                            Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(V2Red)
                                .border(1.dp, V2Deep2, CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = (-6).dp, y = 6.dp)
                        )
                    }
                }
            }
        } else {
            Box(Modifier.align(Alignment.CenterEnd)) {
                HeaderIcon(Icons.Rounded.ArrowBack, "رجوع", onBack)
            }
        }
    }
}

@Composable
fun HeaderIcon(icon: ImageVector, description: String, onClick: () -> Unit) {
    // 40dp touch target (visual disc stays compact); keeps a11y minimum while
    // fitting the 54dp header row.
    val day = LocalDayMode.current
    Box(
        Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (day) Color.White.withAlpha(.72f) else Color.White.withAlpha( .035f))
            .border(1.dp, if (day) DayBorderSoft.withAlpha(.55f) else Color.White.withAlpha( .14f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        HeaderHalo(Modifier.matchParentSize(), color = if (day) DayCyan else V2Cyan)
        Icon(icon, description, tint = if (day) DayInk else Color.White.withAlpha( .92f), modifier = Modifier.size(20.dp))
    }
}

@Composable
fun BrandLockupV2() {
    val day = LocalDayMode.current
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // The official white company seal leads the lockup on every V2
            // screen; its halo breathes and orbits, the pearl disc stays lit.
            com.knoux.ai.ui.components.BrandSeal(
                size = 30.dp,
                haloColor = if (day) DayViolet else V2Violet,
            )
            Spacer(Modifier.width(6.dp))
            Image(
                painter = painterResource(R.drawable.knoux_wordmark_alpha),
                contentDescription = "knoux ai",
                // The wordmark is white-on-transparent night art; on porcelain
                // it is re-inked to the day violet so the brand stays legible.
                colorFilter = if (day) androidx.compose.ui.graphics.ColorFilter.tint(DayViolet) else null,
                modifier = Modifier.height(14.5.dp),
            )
            Spacer(Modifier.width(7.dp))
            ProPill()
        }
    }
}

@Composable
fun ProPill(modifier: Modifier = Modifier) {
    val proTheme = LocalProTheme.current
    val active = LocalProActive.current
    val shape = RoundedCornerShape(50)
    Box(
        modifier
            .clip(shape)
            .background(
                if (active) {
                    Brush.horizontalGradient(listOf(proTheme.goldBright, proTheme.amber, proTheme.gold))
                } else {
                    Brush.horizontalGradient(listOf(Color(0x33FFC857), Color(0x0EFFC857)))
                }
            )
            .border(1.dp, if (active) proTheme.goldBright.withAlpha( .9f) else proTheme.amber.withAlpha( .72f), shape)
            .padding(horizontal = 9.dp, vertical = 3.dp),
    ) {
        // The active pill is a gold island; its dark text must never be
        // re-inked by day mode. The inactive pill reads as amber text on
        // porcelain, deepened for contrast.
        CompositionLocalProvider(LocalKnouxOnDark provides active) {
            KText(
                if (active) "PRO ✓" else "PRO",
                size = 9.sp,
                weight = FontWeight.Bold,
                color = if (active) proTheme.deep else proTheme.amber,
                letterSpacing = .6.sp,
            )
        }
    }
}

/**
 * Strong red Pro marker used on Pro-only services and their section header.
 * It is intentionally louder than the gold Pro pill: the owner asked for a
 * clearly different, stronger signal that appears only while Pro is active.
 */
@Composable
fun ProBadge(modifier: Modifier = Modifier, label: String = "PRO") {
    // Strong red Pro marker: hot red core, deep red edge, bright rim and a
    // white pulse dot so it reads instantly on both gold and violet surfaces.
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFFF2D55), V2ProRed, Color(0xFFC40027), V2ProRedDeep)
                )
            )
            .border(1.dp, Color(0xFFFF9AAE), RoundedCornerShape(50))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(4.5.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(.6.dp, Color(0xFFFFC2CE), CircleShape)
        )
        Spacer(Modifier.width(3.dp))
        CompositionLocalProvider(LocalKnouxOnDark provides true) {
            KText(
                label,
                size = 9.sp,
                weight = FontWeight.Black,
                color = Color.White,
                letterSpacing = .5.sp,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun CrownHexButton(onClick: () -> Unit = {}) {
    val proTheme = LocalProTheme.current
    val isPro = proTheme.isPro
    Box(Modifier.size(40.dp).clickable(onClickLabel = "Pro", onClick = onClick), contentAlignment = Alignment.Center) {
        HeaderHalo(Modifier.size(34.dp), color = proTheme.gold)
        Canvas(Modifier.size(34.dp)) {
            val path = hexagonPath(size.width * .5f, size.height * .5f, size.width * .47f)
            val gradient = if (isPro)
                Brush.linearGradient(listOf(proTheme.goldBright.withAlpha( .4f), proTheme.amber.withAlpha( .1f)))
            else
                Brush.linearGradient(listOf(Color(0x44FFC857), Color(0x0CFF9D00)))
            val borderColor = if (isPro) proTheme.amber else V2Amber
            val crownColor = if (isPro) proTheme.gold else V2Amber

            drawPath(path, gradient)
            drawPath(path, borderColor.withAlpha( .85f), style = Stroke(1.dp.toPx()))
            drawCrown(
                center = Offset(size.width * .5f, size.height * .50f),
                width = size.width * .50f,
                height = size.height * .38f,
                color = crownColor,
            )
        }
    }
}

@Composable
fun CrownGold(size: Dp = 30.dp, color: Color = V2Amber) {
    Canvas(Modifier.size(size)) {
        drawCrown(
            Offset(this.size.width / 2f, this.size.height * .48f),
            this.size.width * .92f,
            this.size.height * .56f,
            color,
        )
    }
}

private fun hexagonPath(cx: Float, cy: Float, radius: Float): Path = Path().apply {
    for (i in 0 until 6) {
        val a = Math.toRadians((60.0 * i - 30.0))
        val x = cx + (radius * cos(a)).toFloat()
        val y = cy + (radius * sin(a)).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

private fun DrawScope.drawCrown(center: Offset, width: Float, height: Float, color: Color) {
    val left = center.x - width / 2f
    val top = center.y - height * .35f
    val p = Path().apply {
        moveTo(left, top + height * .64f)
        lineTo(left + width * .07f, top + height * .16f)
        lineTo(left + width * .34f, top + height * .40f)
        lineTo(left + width * .50f, top)
        lineTo(left + width * .66f, top + height * .40f)
        lineTo(left + width * .93f, top + height * .16f)
        lineTo(left + width, top + height * .64f)
        close()
    }
    drawPath(p, color)
    drawRoundRect(
        color = color,
        topLeft = Offset(left + width * .06f, top + height * .76f),
        size = Size(width * .88f, height * .20f),
        cornerRadius = CornerRadius(width * .04f),
    )
}

/* ------------------------------------------------------------------ *
 * Bottom bar — five destinations, elevated Pro crown.
 * ------------------------------------------------------------------ */

data class V2Tab(
    val route: String,
    val label: String,
    val icon: ImageVector?,
)

val V2Tabs = listOf(
    V2Tab("home", "الرئيسية", Icons.Rounded.Home),
    V2Tab("clean", "تنظيف", Icons.Rounded.CleaningServices),
    V2Tab("pro", "Pro", null),
    V2Tab("tools", "الأدوات", Icons.Rounded.GridView),
    V2Tab("reports", "التقارير", Icons.Rounded.BarChart),
)

/** Approved bottom-bar home glyph: outline house, no fill. */
@Composable
fun V2HomeOutlineGlyph(modifier: Modifier = Modifier, color: Color = V2Pink) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.9.dp.toPx()
        val pink = color
        val roof = Path().apply {
            moveTo(w * .06f, h * .46f)
            lineTo(w * .5f, h * .06f)
            lineTo(w * .94f, h * .46f)
        }
        drawPath(roof, pink, style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val body = Path().apply {
            moveTo(w * .16f, h * .40f)
            lineTo(w * .16f, h * .92f)
            lineTo(w * .84f, h * .92f)
            lineTo(w * .84f, h * .40f)
        }
        drawPath(body, pink, style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val door = Path().apply {
            moveTo(w * .40f, h * .92f)
            lineTo(w * .40f, h * .62f)
            lineTo(w * .60f, h * .62f)
            lineTo(w * .60f, h * .92f)
        }
        drawPath(door, pink, style = Stroke(stroke * .85f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/**
 * Catalog-driven bottom-bar lane for routes without an explicit mapping.
 * Section hubs, catalog tools and the dev/cloud dashboards all highlight the
 * lane they belong to instead of falling through to Home.
 */
internal fun bottomBarLaneForRoute(route: String): String {
    SectionHub.byRoute(route)?.let { entry ->
        return when (entry.key) {
            "smartclean", "duplicates", "residue", "deepscan" -> "clean"
            "reports" -> "reports"
            else -> "tools"
        }
    }
    if (route.startsWith("clean-")) return "clean"
    SectionTools.defs.firstOrNull { it.route == route }?.let { def ->
        return when (def.section) {
            "smartclean", "duplicates", "residue", "deepscan" -> "clean"
            "reports" -> "reports"
            else -> "tools"
        }
    }
    if (route.startsWith("dev-") ||
        route == "developer-studio" || route == "cloud-cleaner" ||
        route == "google-storage" || route == "microsoft-storage"
    ) return "tools"
    return "home"
}

@Composable
fun V2BottomBar(
    selectedRoute: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val proTheme = LocalProTheme.current
    val isPro = proTheme.isPro
    val day = LocalDayMode.current
    val selected = when (selectedRoute) {
        "home" -> "home"
        "clean", "smartclean", "deepscan", "duplicates", "residue", "duplicate-group",
        "deep-scan-summary", "residue-item" -> "clean"
        "tools", "storage", "appmanager", "app-detail", "privacy", "battery", "ai",
        "storage-breakdown", "installed-apps", "battery-diagnostics",
        "ai-recommendations", "privacy-risk" -> "tools"
        "reports", "performance-report" -> "reports"
        "pro" -> "pro"
        // Everything else resolves through the catalog so new services never
        // silently fall back to the Home highlight while browsing them.
        else -> bottomBarLaneForRoute(selectedRoute)
    }

    Box(modifier.fillMaxWidth().height(88.dp).padding(horizontal = 12.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val top = 15.dp.toPx()
            val shape = Path().apply {
                addRoundRect(
                    androidx.compose.ui.geometry.RoundRect(
                        0f, top, size.width, size.height,
                        CornerRadius(30.dp.toPx()),
                    )
                )
            }
            val bgColor1 = if (day) DayCardTop else if (isPro) proTheme.deep else proTheme.barTop
            val bgColor2 = if (day) DayCardBottom else if (isPro) proTheme.deep2 else proTheme.panel2
            val borderColor = if (day) DayBorderSoft else if (isPro) proTheme.border else V2Border
            val accentColor = if (day) DayCyan else if (isPro) proTheme.cyan else V2Cyan
            
            drawPath(shape, Brush.verticalGradient(listOf(bgColor1, bgColor2)))
            // Approved bar edge: violet→cyan→gold luminous sweep (night only).
            if (!day) {
                drawPath(
                    shape,
                    Brush.horizontalGradient(
                        listOf(V2Violet.withAlpha(.75f), V2Cyan.withAlpha(.6f), V2Amber.withAlpha(.55f), V2Violet.withAlpha(.75f))
                    ),
                    style = Stroke(1.2.dp.toPx()),
                )
            } else {
                drawPath(shape, borderColor.withAlpha(.48f), style = Stroke(1.dp.toPx()))
            }
            drawPath(
                shape,
                Brush.verticalGradient(
                    listOf(Color.Transparent, accentColor.withAlpha( .10f), Color.Transparent),
                    0f,
                    size.height,
                )
            )
            // Aurora crest above the bar — soft violet/cyan ribbons like the
            // approved art, drawn in the gap above the capsule.
            if (!day) {
                val crest = Path().apply {
                    moveTo(0f, top * .55f)
                    cubicTo(size.width * .22f, top * .05f, size.width * .40f, top * .95f, size.width * .62f, top * .35f)
                    cubicTo(size.width * .80f, top * .02f, size.width * .92f, top * .7f, size.width, top * .4f)
                }
                drawPath(crest, Brush.horizontalGradient(listOf(Color.Transparent, V2Violet.withAlpha(.40f), V2Cyan.withAlpha(.30f), Color.Transparent)), style = Stroke(2.2.dp.toPx(), cap = StrokeCap.Round))
                drawPath(crest, Brush.horizontalGradient(listOf(Color.Transparent, V2Cyan.withAlpha(.16f), V2Pink.withAlpha(.14f), Color.Transparent)), style = Stroke(5.dp.toPx(), cap = StrokeCap.Round))
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Approved bar reads RTL: الرئيسية RIGHT → التقارير LEFT.
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            V2Tabs.forEach { tab ->
                if (tab.route == "pro") {
                    Spacer(Modifier.weight(1f))
                } else {
                    BottomItem(
                        tab = tab,
                        active = selected == tab.route,
                        onClick = { onSelect(tab.route) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            }
        }

        Box(
            Modifier
                .align(Alignment.TopCenter)
                .size(74.dp)
                .clickable(
                    interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() },
                    indication = null,
                ) { onSelect("pro") },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.fillMaxSize()) {
                // Approved art: hot gold bloom spilling well past the ring.
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(proTheme.amber.withAlpha(.55f), proTheme.amber.withAlpha(.22f), Color.Transparent),
                        radius = size.minDimension * .5f,
                    ),
                    radius = size.minDimension * .5f,
                )
            }
            Box(
                Modifier
                    .size(66.dp)
                    .clip(CircleShape)
                    .background(
                        if (day)
                            Brush.verticalGradient(listOf(Color(0xFF2A1A4E), Color(0xFF171034)))
                        else if (isPro)
                            Brush.verticalGradient(listOf(proTheme.deep2, proTheme.deep))
                        else
                            // Violet world in standard mode too — the crown stays
                            // gold-accented, but the orb no longer clashes brown.
                            Brush.verticalGradient(listOf(proTheme.panel, proTheme.panel2))
                    )
                    .border(
                        2.4.dp,
                        Brush.sweepGradient(
                            listOf(
                                if (day) DayGold else Color(0xFFFFE9A8),
                                if (day) DayGold.withAlpha(.7f) else V2Amber,
                                if (day) DayGold else Color(0xFFB87A1E),
                                if (day) DayGold else Color(0xFFFFE9A8),
                            )
                        ),
                        CircleShape,
                    )
                    .border(1.dp, if (day) Color.White.withAlpha(.30f) else Color.White.withAlpha(.28f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CrownGold(size = 26.dp, color = if (day) V2GoldText else V2Amber)
                    CompositionLocalProvider(LocalKnouxOnDark provides true) {
                        KText("Pro", size = 11.sp, weight = FontWeight.Black, color = if (day) V2GoldText else proTheme.gold)
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.BottomItem(
    tab: V2Tab,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val proTheme = LocalProTheme.current
    val isPro = proTheme.isPro
    val day = LocalDayMode.current
    val activeTop = if (day) DayViolet else if (isPro) proTheme.amber else V2Violet
    val activeBottom = if (day) DayBlue else if (isPro) proTheme.deep2 else Color(0xFF3A1670)
    val activeBorder = if (day) DayGold else if (isPro) proTheme.gold else V2Pink
    Box(
        modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (active) {
                    Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(activeTop.withAlpha(.82f), activeBottom.withAlpha(.66f))
                            )
                        )
                        .border(1.dp, activeBorder.withAlpha(.52f), RoundedCornerShape(20.dp))
                } else Modifier
            )
            .clickable(onClick = onClick)
            .semantics { contentDescription = tab.label },
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalKnouxOnDark provides active) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val iconTint = if (active) Color.White else if (day) DayInkSoft else proTheme.soft.withAlpha(.82f)
            if (tab.route == "home") {
                // Approved art: outline house, not the filled Material glyph.
                Box(Modifier.size(22.dp), contentAlignment = Alignment.Center) {
                    V2HomeOutlineGlyph(Modifier.size(21.dp), color = iconTint)
                }
            } else {
                Icon(
                    tab.icon ?: return@Column,
                    contentDescription = tab.label,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.height(2.dp))
            KText(
                tab.label,
                size = 10.5.sp,
                weight = if (active) FontWeight.Bold else FontWeight.Medium,
                color = if (active) Color.White else if (day) DayInkSoft else proTheme.soft.withAlpha(.85f),
                maxLines = 1,
            )
        }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Surfaces and controls
 * ------------------------------------------------------------------ */

@Composable
fun V2GlassCard(
    modifier: Modifier = Modifier,
    padding: Dp = 14.dp,
    radius: Dp = 20.dp,
    glow: Color = V2Violet,
    borderAlpha: Float = .48f,
    content: @Composable BoxScope.() -> Unit,
) {
    val proTheme = LocalProTheme.current
    val isPro = proTheme.isPro
    val day = LocalDayMode.current
    val shape = RoundedCornerShape(radius)
    // Use Pro-aware colors when Pro is active
    val glassTop = if (day) DayCardTop else if (isPro) proTheme.glassTop else V2GlassTop
    val glassBottom = if (day) DayCardBottom else if (isPro) proTheme.glassBottom else V2GlassBottom
    val borderColor = if (day) dayAccent(glow) else if (isPro) proTheme.border else glow
    val glowColor = if (day) dayAccent(glow) else if (isPro) proTheme.amber else glow
    
    Box(
        modifier
            .clip(shape)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(glowColor.withAlpha( if (day) .10f else .16f), Color.Transparent),
                        center = Offset(size.width * .12f, 0f),
                        radius = size.width * .85f,
                    ),
                    radius = size.width * .85f,
                    center = Offset(size.width * .12f, 0f),
                )
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, glowColor.withAlpha( if (day) .38f else .52f), Color.Transparent)
                    ),
                    start = Offset(size.width * .10f, 1.dp.toPx()),
                    end = Offset(size.width * .90f, 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .background(Brush.verticalGradient(listOf(glassTop.withAlpha( .93f), glassBottom.withAlpha( .98f))))
            .border(1.dp, borderColor.withAlpha( if (day) (borderAlpha * .8f).coerceAtLeast(.30f) else borderAlpha), shape)
            .padding(padding),
        content = content,
    )
}

/**
 * Pro-aware GlassCard that automatically uses gold theme when Pro is active.
 * Use this for screens that should reflect Pro identity.
 */
@Composable
fun V2ProGlassCard(
    modifier: Modifier = Modifier,
    padding: Dp = 14.dp,
    radius: Dp = 20.dp,
    borderAlpha: Float = .48f,
    content: @Composable BoxScope.() -> Unit,
) {
    val proTheme = LocalProTheme.current
    val isPro = proTheme.isPro
    val day = LocalDayMode.current
    val shape = RoundedCornerShape(radius)
    val glassTop = if (day) DayCardTop else if (isPro) proTheme.glassTop else V2GlassTop
    val glassBottom = if (day) DayCardBottom else if (isPro) proTheme.glassBottom else V2GlassBottom
    val borderColor = if (day) DayBorderSoft else if (isPro) proTheme.border else V2Border
    val glowColor = if (day) DayViolet else if (isPro) proTheme.amber else V2Violet
    
    Box(
        modifier
            .clip(shape)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(glowColor.withAlpha( .18f), Color.Transparent),
                        center = Offset(size.width * .12f, 0f),
                        radius = size.width * .85f,
                    ),
                    radius = size.width * .85f,
                    center = Offset(size.width * .12f, 0f),
                )
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, glowColor.withAlpha( .55f), Color.Transparent)
                    ),
                    start = Offset(size.width * .10f, 1.dp.toPx()),
                    end = Offset(size.width * .90f, 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .background(Brush.verticalGradient(listOf(glassTop.withAlpha( .95f), glassBottom.withAlpha( .98f))))
            .border(1.dp, borderColor.withAlpha( borderAlpha), shape)
            .padding(padding),
        content = content,
    )
}

@Composable
fun V2SectionLabel(title: String, modifier: Modifier = Modifier, trailing: String? = null) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        KText(title, size = 15.sp, weight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        if (trailing != null) KText(trailing, size = 10.5.sp, color = V2Muted)
    }
}

@Composable
fun V2GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    height: Dp = 48.dp,
    contentColor: Color = Color.White,
    trailingChevron: Boolean = false,
    chevronRight: Boolean = false,
) {
    val proTheme = LocalProTheme.current
    val isPro = proTheme.isPro
    val shape = RoundedCornerShape(50)
    val gradient = if (isPro) 
        Brush.horizontalGradient(listOf(proTheme.pink, proTheme.violet, proTheme.blue, proTheme.cyan))
    else 
        Brush.horizontalGradient(listOf(V2Pink, V2Violet, V2Blue, V2Cyan))
    
    Row(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(gradient)
            .border(1.dp, Color.White.withAlpha( .28f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        // The gradient CTA stays a night-lit island in day mode too.
        CompositionLocalProvider(LocalKnouxOnDark provides true) {
        if (icon != null) {
            Icon(icon, null, tint = contentColor, modifier = Modifier.size(19.dp))
            Spacer(Modifier.width(8.dp))
        }
        KText(text, size = 15.sp, weight = FontWeight.Bold, color = contentColor)
        if (trailingChevron) {
            Spacer(Modifier.width(8.dp))
            // RTL shell: "forward" reads to the left; use the same chevron the
            // luxury surfaces use instead of a back-arrow glyph. The approved
            // onboarding reference shows a literal right chevron, so callers
            // can pin that direction explicitly.
            Icon(
                if (chevronRight) Icons.Rounded.ChevronRight else Icons.Rounded.ChevronLeft,
                null, tint = contentColor, modifier = Modifier.size(17.dp),
            )
        }
        }
    }
}

@Composable
fun V2GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val proTheme = LocalProTheme.current
    val isPro = proTheme.isPro
    val shape = RoundedCornerShape(50)
    Row(
        modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(proTheme.deep2, proTheme.deep, proTheme.panel)))
            .border(1.dp, proTheme.amber.withAlpha( .78f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        CompositionLocalProvider(LocalKnouxOnDark provides true) {
        if (icon != null) {
            Icon(icon, null, tint = proTheme.gold, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(7.dp))
        }
        KText(text, size = 14.sp, weight = FontWeight.Bold, color = proTheme.gold)
        }
    }
}

/** Bright champagne CTA used by the privacy / Pro reference screens. */
@Composable
fun V2BrightGoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val proTheme = LocalProTheme.current
    val isPro = proTheme.isPro
    val shape = RoundedCornerShape(50)
    Row(
        modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(
                if (isPro)
                    Brush.horizontalGradient(listOf(proTheme.goldBright, proTheme.amber, proTheme.gold, proTheme.violet))
                else
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFFE9B0), Color(0xFFD79A34), Color(0xFFFFD37B), Color(0xFFB8741A))
                    )
            )
            .border(1.dp, if (isPro) proTheme.goldBright.withAlpha( .85f) else Color(0xFFFFE9B0).withAlpha( .85f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = if (isPro) proTheme.deep else Color(0xFF2A1705), modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(7.dp))
        }
        KText(text, size = 15.sp, weight = FontWeight.Black, color = if (isPro) proTheme.deep else Color(0xFF2A1705))
    }
}

@Composable
fun V2GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    height: Dp = 44.dp,
) {
    val proTheme = LocalProTheme.current
    val day = LocalDayMode.current
    val shape = RoundedCornerShape(50)
    Row(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(
                if (day) Color.White.withAlpha(.80f)
                else Color.White.withAlpha(if (proTheme.isPro) .06f else .035f)
            )
            .border(1.dp, (if (day) DayBorderSoft else proTheme.border).withAlpha(if (day) .70f else .50f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = proTheme.soft, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
        }
        KText(text, size = 12.5.sp, weight = FontWeight.Bold, color = proTheme.soft, maxLines = 1)
    }
}

@Composable
fun V2NeonRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 92.dp,
    stroke: Dp = 8.dp,
    colors: List<Color> = listOf(V2Cyan, V2Blue, V2Violet, V2Pink),
    content: @Composable BoxScope.() -> Unit,
) {
    val proTheme = LocalProTheme.current
    val isPro = proTheme.isPro
    val day = LocalDayMode.current
    val proColors = if (isPro) listOf(proTheme.cyan, proTheme.blue, proTheme.violet, proTheme.pink) else colors
    val glowColor = if (isPro) proTheme.amber else colors.last()
    
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = stroke.toPx()
            val inset = sw / 2f
            // Day world: a porcelain face inside the ring keeps the center
            // text legible; night keeps the transparent cosmic center.
            if (day) {
                drawCircle(
                    brush = Brush.radialGradient(listOf(DayCardTop, DayCardBottom)),
                    radius = (this.size.minDimension - sw * 2f) / 2f,
                )
            }
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(glowColor.withAlpha( .20f), Color.Transparent),
                    radius = this.size.minDimension * .53f,
                ),
                radius = this.size.minDimension * .53f,
            )
            drawArc(
                color = (if (day) DayBorderSoft else Color.White).withAlpha(if (day) .50f else .08f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(this.size.width - sw, this.size.height - sw),
                style = Stroke(sw, cap = StrokeCap.Round),
            )
            val sweep = 360f * progress.coerceIn(0f, 1f)
            val brush = Brush.sweepGradient(proColors, center)
            drawArc(
                brush = brush,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(this.size.width - sw, this.size.height - sw),
                style = Stroke(sw * 3.3f, cap = StrokeCap.Round),
                alpha = .18f,
            )
            drawArc(
                brush = brush,
                startAngle = -90f,
                sweepAngle = (sweep - 4f).coerceAtLeast(0f),
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(this.size.width - sw, this.size.height - sw),
                style = Stroke(sw, cap = StrokeCap.Round),
            )
            if (progress > .02f) {
                val angle = Math.toRadians((-90f + sweep).toDouble())
                val r = (this.size.width - sw) / 2f
                drawCircle(
                    (if (day) DayViolet else Color.White).withAlpha( .95f),
                    radius = sw * .34f,
                    center = Offset(center.x + (r * cos(angle)).toFloat(), center.y + (r * sin(angle)).toFloat()),
                )
            }
        }
        content()
    }
}

@Composable
fun V2ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    colors: List<Color> = listOf(V2Cyan, V2Blue),
) {
    val day = LocalDayMode.current
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(if (day) DayBorderSoft.withAlpha(.30f) else Color.White.withAlpha( .09f))
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxSize()
                .clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(colors))
        )
    }
}

@Composable
fun V2MetricRow(
    icon: ImageVector,
    label: String,
    value: String,
    progress: Float,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val day = LocalDayMode.current
    val tint = if (day) dayAccent(accent) else accent
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(tint.withAlpha( if (day) .14f else .10f))
                .border(1.dp, tint.withAlpha( if (day) .42f else .28f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(7.dp))
        KText(label, size = 10.sp, weight = FontWeight.Medium, color = V2Soft, maxLines = 1, modifier = Modifier.width(58.dp))
        Column(Modifier.weight(1f)) {
            V2ProgressBar(progress, height = 5.dp, colors = listOf(accent, V2Cyan))
        }
        Spacer(Modifier.width(7.dp))
        KText(value, size = 9.sp, color = Color.White, weight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
fun V2SearchField(
    placeholder: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val day = LocalDayMode.current
    Row(
        modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (day) Color.White.withAlpha(.85f) else Color.White.withAlpha( .045f))
            .border(1.dp, (if (day) DayBorderSoft else V2Border).withAlpha( if (day) .60f else .32f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.Search, null, tint = V2Soft, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(7.dp))
        KText(placeholder, size = 10.5.sp, color = V2Muted)
    }
}

@Composable
fun V2IconTile(
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    iconSize: Dp = 18.dp,
) {
    val day = LocalDayMode.current
    val dayTint = dayAccent(accent)
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(size * .31f))
            .background(
                if (day) Brush.linearGradient(listOf(dayTint.withAlpha(.14f), DayCardBottom))
                else Brush.linearGradient(listOf(accent.withAlpha( .28f), Color(0xFF1B1040)))
            )
            .border(1.dp, (if (day) dayTint else accent).withAlpha( if (day) .45f else .50f), RoundedCornerShape(size * .31f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, tint = if (day) dayTint else accent, modifier = Modifier.size(iconSize))
    }
}

@Composable
fun V2RasterTile(
    @DrawableRes image: Int,
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    accent: Color = V2Violet,
) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(accent.withAlpha( .34f), Color.Transparent),
                    radius = this.size.minDimension * .54f,
                ),
                radius = this.size.minDimension * .54f,
            )
            drawOval(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, accent.withAlpha( .58f), Color.Transparent)
                ),
                topLeft = Offset(this.size.width * .13f, this.size.height * .92f),
                size = Size(this.size.width * .74f, 2.dp.toPx()),
            )
        }
        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(.94f),
            contentScale = ContentScale.Fit,
            colorFilter = null,
            alpha = 1f,
        )
    }
}

@Composable
fun V2Chip(
    text: String,
    active: Boolean = false,
    accent: Color = V2Cyan,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(50)
    val day = LocalDayMode.current
    Row(
        modifier
            .clip(shape)
            .background(
                if (active) Brush.horizontalGradient(listOf((if (day) DayCyan else V2Cyan).withAlpha( .85f), (if (day) DayBlue else V2Blue).withAlpha( .90f)))
                else if (day) Brush.horizontalGradient(listOf(Color.White.withAlpha(.85f), Color.White.withAlpha(.65f)))
                else Brush.horizontalGradient(listOf(Color.White.withAlpha( .05f), Color.White.withAlpha( .02f)))
            )
            .border(if (active) 0.dp else 1.dp, if (active) Color.Transparent else (if (day) dayAccent(accent) else accent).withAlpha( if (day) .40f else .28f), shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .then(if (onClick != null) Modifier.heightIn(min = 36.dp) else Modifier)
            .padding(horizontal = 13.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalKnouxOnDark provides active) {
            KText(
                text,
                size = 10.5.sp,
                weight = if (active) FontWeight.Bold else FontWeight.Medium,
                color = if (active) V2Deep else V2Soft,
            )
        }
    }
}

@Composable
fun V2StatCell(value: String, label: String, accent: Color, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        KText(value, size = 12.5.sp, weight = FontWeight.Bold, color = accent)
        Spacer(Modifier.height(1.dp))
        KText(label, size = 9.sp, color = V2Soft, maxLines = 1)
    }
}

/**
 * The KNOUX stag is the product mascot. The reference artwork is kept as a
 * raster asset so the antler filaments and holographic fur survive at phone
 * density; a very slow float/breath loop gives it life without distracting.
 * When Pro is active, a golden crown appears above the stag.
 */
@Composable
fun V2DeerHeader(
    modifier: Modifier = Modifier,
    mirrored: Boolean = false,
    animated: Boolean = true,
) {
    val proTheme = LocalProTheme.current
    val motion = animated && rememberMotionEnabled()
    val transition = rememberInfiniteTransition(label = "knouxDeer")
    val float by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) -5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "deerFloat",
    )
    Box(modifier, contentAlignment = Alignment.TopCenter) {
        // Golden crown when Pro is active
        if (proTheme.isPro) {
            CrownGold(
                size = 28.dp,
                color = proTheme.gold,
            )
        }
        Image(
            painter = painterResource(R.drawable.knoux_stag_official),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = float.dp.toPx()
                    scaleX = if (mirrored) -1f else 1f
                },
            contentScale = ContentScale.Fit,
        )
    }
}

/* ------------------------------------------------------------------ *
 * Custom raster-free app marks
 * ------------------------------------------------------------------ */

enum class AppMark { TikTok, WhatsApp, YouTube, Telegram, Photos, Generic }

@Composable
fun AppLogo(mark: AppMark, size: Dp = 42.dp, modifier: Modifier = Modifier) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        when (mark) {
            AppMark.TikTok -> {
                Box(
                    Modifier.fillMaxSize().clip(CircleShape).background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        val w = this.size.width
                        val h = this.size.height
                        val base = Offset(w * .50f, h * .52f)
                        drawCircle(V2Cyan.withAlpha( .9f), radius = w * .14f, center = base + Offset(-w * .075f, 0f))
                        drawCircle(V2Pink.withAlpha( .85f), radius = w * .14f, center = base + Offset(w * .075f, 0f))
                        drawCircle(Color.White, radius = w * .10f, center = base)
                    }
                }
            }
            AppMark.WhatsApp -> {
                Box(
                    Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFF25D366)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Call,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(size * .52f),
                    )
                }
            }
            AppMark.YouTube -> {
                Box(
                    Modifier.fillMaxSize().clip(RoundedCornerShape(size * .28f)).background(Color(0xFFFF0033)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(size * .56f),
                    )
                }
            }
            AppMark.Telegram, AppMark.Photos, AppMark.Generic -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            when (mark) {
                                AppMark.Telegram -> Brush.linearGradient(listOf(Color(0xFF2AABEE), Color(0xFF229ED9)))
                                AppMark.Photos -> Brush.linearGradient(listOf(V2Pink, V2Amber, V2Cyan, Color(0xFF33D17A)))
                                else -> Brush.linearGradient(listOf(Color(0xFF6C7AFF), V2Cyan))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (mark == AppMark.Telegram) Icons.AutoMirrored.Rounded.Send else Icons.Rounded.GridView,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(size * .47f),
                    )
                }
            }
        }
    }
}

@Composable
fun MiniMountainThumb(
    modifier: Modifier = Modifier,
    accent: Color = V2Violet,
    showCity: Boolean = false,
) {
    Canvas(modifier.clip(RoundedCornerShape(12.dp))) {
        drawRect(
            Brush.verticalGradient(
                listOf(Color(0xFF2A155C), Color(0xFF131036), accent.withAlpha( .34f))
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFFD882).withAlpha( .55f), Color.Transparent),
                radius = size.width * .53f,
            ),
            radius = size.width * .53f,
            center = Offset(size.width * .55f, size.height * .60f),
        )
        val ridge1 = Path().apply {
            moveTo(0f, size.height * .64f)
            lineTo(size.width * .26f, size.height * .44f)
            lineTo(size.width * .43f, size.height * .62f)
            lineTo(size.width * .61f, size.height * .32f)
            lineTo(size.width * .82f, size.height * .60f)
            lineTo(size.width, size.height * .48f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(ridge1, Color(0xFF23104A))
        if (showCity) {
            for (i in 0..5) {
                drawRect(
                    color = Color(0xFF0D0824),
                    topLeft = Offset(size.width * (.48f + i * .07f), size.height * (.50f + (i % 2) * .08f)),
                    size = Size(size.width * .055f, size.height * .30f),
                )
            }
        }
        val ridge2 = Path().apply {
            moveTo(0f, size.height * .83f)
            lineTo(size.width * .30f, size.height * .65f)
            lineTo(size.width * .50f, size.height * .82f)
            lineTo(size.width * .70f, size.height * .63f)
            lineTo(size.width, size.height * .84f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(
            ridge2,
            Brush.verticalGradient(listOf(Color(0xFF130A31), Color(0xFF09051C)))
        )
    }
}
