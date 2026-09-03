package com.realeapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.realeapp.R

/**
 * Single source of truth for the app font.
 * Change this one value to switch the typeface everywhere in the app.
 * Font files live in res/font.
 */
val AppFontFamily: FontFamily = FontFamily(
    Font(R.font.valley_sans_thin, FontWeight.Thin),
    Font(R.font.valley_sans_extralight, FontWeight.ExtraLight),
    Font(R.font.valley_sans_light, FontWeight.Light),
    Font(R.font.valley_sans_regular, FontWeight.Normal),
    Font(R.font.valley_sans_medium, FontWeight.Medium),
    Font(R.font.valley_sans_semibold, FontWeight.SemiBold),
    Font(R.font.valley_sans_bold, FontWeight.Bold),
    Font(R.font.valley_sans_extrabold, FontWeight.ExtraBold),
    Font(R.font.valley_sans_black, FontWeight.Black),
    Font(R.font.valley_sans_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.valley_sans_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.valley_sans_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.valley_sans_bold_italic, FontWeight.Bold, FontStyle.Italic)
)

private val DefaultTypography = Typography()

// Material typography with every style routed through AppFontFamily.
val AppTypography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = AppFontFamily),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = AppFontFamily),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = AppFontFamily),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = AppFontFamily),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = AppFontFamily),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = AppFontFamily),
    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = AppFontFamily),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = AppFontFamily),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = AppFontFamily),
    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = AppFontFamily),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = AppFontFamily),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = AppFontFamily),
    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = AppFontFamily),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = AppFontFamily),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = AppFontFamily)
)