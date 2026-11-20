package com.knoux.ai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.knoux.ai.R

/* ------------------------------------------------------------------ *
 * KNOUX AI design tokens
 *
 * Two palettes intentionally live side by side:
 *  - the V2 neon palette used by the Arabic product shell (ui/v2)
 *  - the Arctic Depth tokens used by the auth/splash surfaces
 * ------------------------------------------------------------------ */

// ===== V2 NEON (product shell) =====
val KnouxBackdrop = Color(0xFF07051C)
val KnouxBackdropDeep = Color(0xFF0B0725)
val KnouxWave = Color(0xFF2A1163)
val KnouxPanel = Color(0xFF191040)
val KnouxPanelAlt = Color(0xFF231757)
val KnouxPanelDeep = Color(0xFF120B33)
val KnouxBorder = Color(0xFF5A33C8)
val KnouxBorderSoft = Color(0xFF3A2188)

val KnouxViolet = Color(0xFF9D4DFF)
val KnouxMagenta = Color(0xFFE040FB)
val KnouxPink = Color(0xFFFF6BD6)
val KnouxCyan = Color(0xFF17E6FF)
val KnouxBlue = Color(0xFF3D7BFF)
val KnouxAmber = Color(0xFFFFC857)

val KnouxText = Color(0xFFF6F3FF)
val KnouxSoftText = Color(0xFFC7BDE5)
val KnouxMutedText = Color(0xFF8E85B8)

// ===== ARCTIC DEPTH (splash / auth / legacy summaries) =====
val ProDeepInk = Color(0xFF071321)
val ProNavy = Color(0xFF10243A)
val ProElevated = Color(0xFF18334E)
val ProDivider = Color(0xFF2B4963)
val ProTextPrimary = Color(0xFFF0F6FC)
val ProTextSecondary = Color(0xFFA9BCD0)
val ProBlue = Color(0xFF398BEA)
val ProArcticCyan = Color(0xFF83D4F1)
val ProFrozenWhite = Color(0xFFDDEBF6)
val ProSafe = Color(0xFF49B5A9)
val ProWarning = Color(0xFFE5AD5D)
val ProError = Color(0xFFE77E87)

// ===== LIGHT MODE — Pearl White =====
val ProLightBg = Color(0xFFF3F7FB)
val ProLightSurface = Color(0xFFFFFFFF)
val ProLightRaised = Color(0xFFE8F1F8)
val ProLightDivider = Color(0xFFD7E3EF)
val ProLightText = Color(0xFF152D43)
val ProLightTextSec = Color(0xFF607A90)
val ProLightAction = Color(0xFF205EAC)
val ProLightAccent = Color(0xFF158DC5)
val ProLightSafe = Color(0xFF087F73)
val ProLightWarning = Color(0xFFA16C1E)
val ProLightError = Color(0xFFB54552)

val KnouxBrandCyan = Color(0xFF06B6D4)

/** True while the app is capturing reference-parity screenshots (debug only). */
val LocalReferenceCapture = staticCompositionLocalOf { false }

private val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.poppins_bold_italic, FontWeight.Bold, FontStyle.Italic),
)

/**
 * Arabic-first UI face. Cairo keeps the same geometric, modern voice as the
 * Latin brand while covering the Arabic glyph set used by the V2 screens.
 */
val KnouxArabic = FontFamily(
    Font(R.font.cairo_variable, FontWeight.Light),
    Font(R.font.cairo_variable, FontWeight.Normal),
    Font(R.font.cairo_variable, FontWeight.Medium),
    Font(R.font.cairo_variable, FontWeight.SemiBold),
    Font(R.font.cairo_variable, FontWeight.Bold),
    Font(R.font.cairo_variable, FontWeight.Black),
)

/** Latin lockup / numerals used where the reference keeps a Latin treatment. */
val KnouxLatin = Poppins

private val KnouxColors = darkColorScheme(
    primary = KnouxCyan,
    onPrimary = KnouxBackdrop,
    secondary = KnouxViolet,
    onSecondary = KnouxText,
    tertiary = KnouxMagenta,
    background = KnouxBackdrop,
    onBackground = KnouxText,
    surface = KnouxPanel,
    onSurface = KnouxText,
    surfaceVariant = KnouxPanelAlt,
    onSurfaceVariant = KnouxSoftText,
    outline = KnouxBorder,
    error = Color(0xFFFF6B6B),
)

private val ProLightColors = lightColorScheme(
    primary = ProLightAction,
    onPrimary = ProLightSurface,
    secondary = ProLightAccent,
    onSecondary = ProLightSurface,
    tertiary = ProLightSafe,
    background = ProLightBg,
    onBackground = ProLightText,
    surface = ProLightSurface,
    onSurface = ProLightText,
    surfaceVariant = ProLightRaised,
    onSurfaceVariant = ProLightTextSec,
    outline = ProLightDivider,
    error = ProLightError,
)

/**
 * Porcelain day scheme for Material surfaces (dialogs, sheets, text fields).
 * The V2 chrome paints its own day world; this keeps every Material component
 * legible on porcelain instead of falling back to the night panel colors.
 */
val KnouxDayColorScheme = lightColorScheme(
    primary = Color(0xFF6D28D9),
    onPrimary = Color.White,
    secondary = Color(0xFF0E7490),
    onSecondary = Color.White,
    tertiary = Color(0xFFD6246E),
    background = Color(0xFFF7F4FF),
    onBackground = Color(0xFF241740),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF241740),
    surfaceVariant = Color(0xFFF4F0FC),
    onSurfaceVariant = Color(0xFF4A3B6B),
    outline = Color(0xFFB9A6DE),
    error = Color(0xFFC81E4A),
)

private val KnouxType = Typography(
    displayLarge = TextStyle(
        fontFamily = KnouxArabic, fontWeight = FontWeight.Black,
        fontSize = 40.sp, lineHeight = 48.sp, color = KnouxText
    ),
    displayMedium = TextStyle(
        fontFamily = KnouxArabic, fontWeight = FontWeight.Black,
        fontSize = 32.sp, lineHeight = 40.sp, color = KnouxText
    ),
    headlineMedium = TextStyle(
        fontFamily = KnouxArabic, fontWeight = FontWeight.Bold,
        fontSize = 27.sp, lineHeight = 34.sp, color = KnouxText
    ),
    titleLarge = TextStyle(
        fontFamily = KnouxArabic, fontWeight = FontWeight.Bold, fontSize = 18.sp,
        lineHeight = 24.sp, color = KnouxText
    ),
    titleMedium = TextStyle(
        fontFamily = KnouxArabic, fontWeight = FontWeight.Bold, fontSize = 15.sp,
        lineHeight = 21.sp, color = KnouxText
    ),
    bodyLarge = TextStyle(
        fontFamily = KnouxArabic, fontWeight = FontWeight.Normal, fontSize = 13.sp,
        lineHeight = 19.sp, color = KnouxSoftText
    ),
    bodyMedium = TextStyle(
        fontFamily = KnouxArabic, fontWeight = FontWeight.Normal, fontSize = 11.5.sp,
        lineHeight = 17.sp, color = KnouxSoftText
    ),
    labelLarge = TextStyle(
        fontFamily = KnouxArabic, fontWeight = FontWeight.Bold, fontSize = 14.sp,
        lineHeight = 19.sp, color = KnouxText
    ),
    labelMedium = TextStyle(
        fontFamily = KnouxArabic, fontWeight = FontWeight.SemiBold, fontSize = 11.sp,
        lineHeight = 15.sp, color = KnouxSoftText
    ),
    labelSmall = TextStyle(
        fontFamily = KnouxArabic, fontWeight = FontWeight.Medium, fontSize = 9.5.sp,
        lineHeight = 13.sp, color = KnouxMutedText
    ),
)

@Composable
fun KnouxTheme(
    referenceCapture: Boolean = false,
    darkMode: Boolean = true, // Default to dark; system pref can override later
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalReferenceCapture provides referenceCapture,
        // V2 is an Arabic-first product. Compose mirrors every start/end
        // alignment automatically while numbers and package names stay LTR.
        LocalLayoutDirection provides LayoutDirection.Rtl,
    ) {
        MaterialTheme(
            colorScheme = if (darkMode) KnouxColors else ProLightColors,
            typography = KnouxType,
            content = content,
        )
    }
}
