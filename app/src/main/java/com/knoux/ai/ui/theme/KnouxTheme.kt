package com.knoux.ai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.knoux.ai.R

/* ------------------------------------------------------------------ *
 * KNOUX AI Pro — Arctic Depth / Frostline Design System
 * Exact tokens from Figma reference-locked contract (2026-11-14)
 * ------------------------------------------------------------------ */

// ===== DARK MODE (Default) — Arctic Depth =====
val ProDeepInk      = Color(0xFF071321) // Background
val ProNavy         = Color(0xFF10243A) // Main Surface
val ProElevated     = Color(0xFF18334E) // Elevated Surface
val ProDivider      = Color(0xFF2B4963) // Fine Divider
val ProTextPrimary  = Color(0xFFF0F6FC) // Primary Text
val ProTextSecondary= Color(0xFFA9BCD0) // Secondary Text
val ProBlue         = Color(0xFF398BEA) // Primary Action / Selected
val ProArcticCyan   = Color(0xFF83D4F1) // Measurement / Gauge accent
val ProFrozenWhite  = Color(0xFFDDEBF6) // Tiny highlights
val ProSafe         = Color(0xFF49B5A9) // Confirmed / Safe
val ProWarning      = Color(0xFFE5AD5D) // Warning
val ProError        = Color(0xFFE77E87) // Error

// ===== LIGHT MODE — Pearl White =====
val ProLightBg      = Color(0xFFF3F7FB) // Background
val ProLightSurface = Color(0xFFFFFFFF) // Main Surface
val ProLightRaised  = Color(0xFFE8F1F8) // Raised Surface
val ProLightDivider = Color(0xFFD7E3EF) // Fine Divider
val ProLightText    = Color(0xFF152D43) // Primary Text
val ProLightTextSec = Color(0xFF607A90) // Secondary Text
val ProLightAction  = Color(0xFF205EAC) // Primary Action
val ProLightAccent  = Color(0xFF158DC5) // Instrument Accent
val ProLightSafe    = Color(0xFF087F73) // Confirmed / Safe
val ProLightWarning = Color(0xFFA16C1E) // Warning
val ProLightError   = Color(0xFFB54552) // Error

// ===== BRAND ACCENT (restricted slots only) =====
val KnouxViolet = Color(0xFF7C3AED)
val KnouxMagenta = Color(0xFF9333EA)
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

private val ProDarkColors = darkColorScheme(
    primary = ProBlue,
    onPrimary = ProDeepInk,
    secondary = ProArcticCyan,
    onSecondary = ProDeepInk,
    tertiary = ProSafe,
    background = ProDeepInk,
    onBackground = ProTextPrimary,
    surface = ProNavy,
    onSurface = ProTextPrimary,
    surfaceVariant = ProElevated,
    onSurfaceVariant = ProTextSecondary,
    outline = ProDivider,
    error = ProError,
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

private val ProTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic,
        fontSize = 40.sp, lineHeight = 46.sp, color = ProTextPrimary
    ),
    displayMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic,
        fontSize = 32.sp, lineHeight = 38.sp, color = ProTextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic,
        fontSize = 27.sp, lineHeight = 33.sp, color = ProTextPrimary
    ),
    titleLarge = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 17.sp,
        lineHeight = 22.sp, color = ProTextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
        lineHeight = 20.sp, color = ProTextPrimary
    ),
    bodyLarge = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 13.sp,
        lineHeight = 18.sp, color = ProTextSecondary
    ),
    bodyMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 11.5.sp,
        lineHeight = 16.sp, color = ProTextSecondary
    ),
    labelLarge = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 14.sp,
        lineHeight = 18.sp, color = ProTextPrimary
    ),
    labelMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 11.sp,
        lineHeight = 14.sp, color = ProTextSecondary
    ),
    labelSmall = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 9.5.sp,
        lineHeight = 12.sp, color = ProTextSecondary
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
        LocalLayoutDirection provides LayoutDirection.Ltr, // References are LTR
    ) {
        MaterialTheme(
            colorScheme = if (darkMode) ProDarkColors else ProLightColors,
            typography = ProTypography,
            content = content,
        )
    }
}