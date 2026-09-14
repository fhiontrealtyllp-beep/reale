package com.realeapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.realeapp.R

/**
 * Primary app font (Figtree) — used for title, body and label styles.
 * Change this one value to switch the typeface everywhere in the app.
 * Font files live in res/font.
 */
val AppFontFamily: FontFamily = FontFamily(
    Font(R.font.figtree_light, FontWeight.Light),
    Font(R.font.figtree_regular, FontWeight.Normal),
    Font(R.font.figtree_medium, FontWeight.Medium),
    Font(R.font.figtree_semibold, FontWeight.SemiBold),
    Font(R.font.figtree_bold, FontWeight.Bold),
    Font(R.font.figtree_extrabold, FontWeight.ExtraBold),
    Font(R.font.figtree_black, FontWeight.Black),
    Font(R.font.figtree_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.figtree_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.figtree_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.figtree_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.figtree_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.figtree_extrabold_italic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.figtree_black_italic, FontWeight.Black, FontStyle.Italic)
)

/**
 * Display font (Valley Sans) — used for display and headline styles.
 */
val DisplayFontFamily: FontFamily = FontFamily(
    Font(R.font.valley_sans_thin, FontWeight.Thin),
    Font(R.font.valley_sans_extralight, FontWeight.ExtraLight),
    Font(R.font.valley_sans_light, FontWeight.Light),
    Font(R.font.valley_sans_regular, FontWeight.Normal),
    Font(R.font.valley_sans_medium, FontWeight.Medium),
    Font(R.font.valley_sans_semibold, FontWeight.SemiBold),
    Font(R.font.valley_sans_bold, FontWeight.Bold),
    Font(R.font.valley_sans_extrabold, FontWeight.ExtraBold),
    Font(R.font.valley_sans_black, FontWeight.Black),
    Font(R.font.valley_sans_thin_italic, FontWeight.Thin, FontStyle.Italic),
    Font(R.font.valley_sans_extralight_italic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.valley_sans_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.valley_sans_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.valley_sans_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.valley_sans_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.valley_sans_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.valley_sans_extrabold_italic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.valley_sans_black_italic, FontWeight.Black, FontStyle.Italic)
)

private val DefaultTypography = Typography()

// Material typography: display/headline styles use Valley Sans, everything
// else uses Figtree.
val AppTypography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = DisplayFontFamily),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = DisplayFontFamily),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = DisplayFontFamily),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = DisplayFontFamily),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = DisplayFontFamily),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = DisplayFontFamily),
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