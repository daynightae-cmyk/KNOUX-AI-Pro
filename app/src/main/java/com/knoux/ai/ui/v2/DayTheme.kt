package com.knoux.ai.ui.v2

import androidx.compose.ui.graphics.Color
import com.knoux.ai.ui.theme.KnouxText

/* ------------------------------------------------------------------ *
 * KNOUX day world — porcelain light, professional contrast.
 *
 * Owner direction (20 Nov): a real day mode, not an inverted night.
 * Chrome turns warm porcelain; the cosmic service stage, the K orb jewel
 * and gradient CTAs stay night-lit islands (LocalKnouxOnDark) so the
 * approved neon identity keeps its punch on a light canvas. Accents are
 * deepened here for legibility on light surfaces (WCAG-minded).
 * ------------------------------------------------------------------ */

val DayPaperTop = Color(0xFFF7F4FF)
val DayPaperMid = Color(0xFFEFEAFB)
val DayPaperBase = Color(0xFFE7E0F6)
val DayHorizon = Color(0xFFF6E8D8)

val DayCardTop = Color(0xFFFFFFFF)
val DayCardBottom = Color(0xFFF4F0FC)
val DayCardSeam = Color(0xFFC9B8E8)
val DayBorderSoft = Color(0xFFB9A6DE)

val DayInk = Color(0xFF241740)
val DayInkSoft = Color(0xFF4A3B6B)
val DayInkMuted = Color(0xFF6F6390)

val DayViolet = Color(0xFF6D28D9)
val DayPink = Color(0xFFBE185D)
val DayCyan = Color(0xFF0E7490)
val DayBlue = Color(0xFF2743C7)
val DayGreen = Color(0xFF047857)
val DayRed = Color(0xFFBE123C)
val DayAmber = Color(0xFFB45309)
val DayGold = Color(0xFF8A5A00)
val DayGoldBright = Color(0xFFB45309)

/** Day accent for any standard/Pro neon token; unknown colors pass through. */
fun dayAccent(color: Color): Color = when (color) {
    V2Cyan -> DayCyan
    V2Pink -> DayPink
    V2Violet -> DayViolet
    V2Blue -> DayBlue
    V2Green, ProGreen -> DayGreen
    V2Red, ProRed -> DayRed
    V2Amber, ProAmber -> DayAmber
    V2GoldText, ProGold, ProGoldBright -> DayGold
    // The Pro world remaps the neon slots to gold/amber; on porcelain they
    // deepen to the same warm ink family, never to pale lavender.
    ProCyan -> DayGold
    ProViolet, ProBlue -> DayAmber
    ProPink -> DayPink
    V2Soft, ProSoft -> DayInkSoft
    V2Muted, ProMuted -> DayInkMuted
    KnouxText -> DayInk
    else -> color
}
