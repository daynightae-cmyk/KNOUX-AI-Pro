package com.knoux.ai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
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
 * KNOUX AI design tokens (sampled from the approved 10 reference PNGs)
 * ------------------------------------------------------------------ */

val KnouxBackdrop = Color(0xFF07051C)
val KnouxBackdropDeep = Color(0xFF0B0725)
val KnouxWave = Color(0xFF2A1163)
val KnouxPanel = Color(0xFF191040)
val KnouxPanelAlt = Color(0xFF231757)
val KnouxPanelDeep = Color(0xFF120B33)
val KnouxBorder = Color(0xFF5A33C8)
val KnouxBorderSoft = Color(0xFF3A2188)

val KnouxViolet = Color(0xFF8F30FF)
val KnouxMagenta = Color(0xFFE040FB)
val KnouxPink = Color(0xFFFF6BD6)
val KnouxCyan = Color(0xFF03DAFF)
val KnouxBlue = Color(0xFF3D7BFF)
val KnouxAmber = Color(0xFFFFC24B)

val KnouxText = Color(0xFFF6F3FF)
val KnouxSoftText = Color(0xFFC7BDE5)
val KnouxMutedText = Color(0xFF8E85B8)

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

private val KnouxType = Typography(
    displayLarge = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic,
        fontSize = 40.sp, lineHeight = 46.sp, color = KnouxText
    ),
    displayMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic,
        fontSize = 32.sp, lineHeight = 38.sp, color = KnouxText
    ),
    headlineMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic,
        fontSize = 27.sp, lineHeight = 33.sp, color = KnouxText
    ),
    titleLarge = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 17.sp,
        lineHeight = 22.sp, color = KnouxText
    ),
    titleMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
        lineHeight = 20.sp, color = KnouxText
    ),
    bodyLarge = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 13.sp,
        lineHeight = 18.sp, color = KnouxSoftText
    ),
    bodyMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 11.5.sp,
        lineHeight = 16.sp, color = KnouxSoftText
    ),
    labelLarge = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 14.sp,
        lineHeight = 18.sp, color = KnouxText
    ),
    labelMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 11.sp,
        lineHeight = 14.sp, color = KnouxSoftText
    ),
    labelSmall = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 9.5.sp,
        lineHeight = 12.sp, color = KnouxMutedText
    ),
)

@Composable
fun KnouxTheme(
    referenceCapture: Boolean = false,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalReferenceCapture provides referenceCapture,
        // The current UI is English-only and the 20 references are LTR.
        // Force LTR so an Arabic system locale cannot mirror the whole layout.
        LocalLayoutDirection provides LayoutDirection.Ltr,
    ) {
        MaterialTheme(
            colorScheme = KnouxColors,
            typography = KnouxType,
            content = content,
        )
    }
}
