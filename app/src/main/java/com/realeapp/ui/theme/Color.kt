package com.realeapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color


// Brand palette (fixed in both themes).
val BrandBlue = Color(0xFF2563EB)
val BrandRed = Color(0xFFEF4444)
val BrandCoral = Color(0xFFFF6B6B)
val Accent = Color(0xFFFDD60D)
val VerifiedGreen = Color(0xFF22C55E)
val Error = Color(0xFFFF6B6B)
val MapMarker = Color(0xFFE91E63)

// Fixed content colors: text/icons drawn on brand-colored containers or on
// photos/media. These must NOT flip with the theme.
val OnBrandContent = Color.White
val OnAccentText = Color.Black
val OnMediaContent = Color.White
val MediaScrim = Color.Black

// Fixed navy for text/icons drawn on the always-light welcome illustration.
val OnLightArtwork = Color(0xFF1B2A4A)

// Light theme palette. Private: screens must use the theme-aware getters.
private val LightTextSecondary = Color(0xFF6B7280)
private val LightBorder = Color(0xFFE5E7EB)
private val LightFill = Color(0xFFF3F4F6)
private val LightSuccessBadge = Color(0xFFDCFCE7)

// Dark theme palette. Private: screens must use the theme-aware getters.
private val DarkBackground = Color(0xFF141C3D)
private val DarkSurface = Color(0xFF1C2755)
private val DarkOnSurface = Color(0xFFFBFBFB)
private val DarkTextSecondary = Color(0xFF8F9FDC)
private val DarkBorder = Color(0xFF2B3C83)
private val DarkFill = Color(0xFF263266)

// Theme-aware palette. Screens read these roles through the shared color
// getters below so the UI repaints when the theme mode changes.
@Immutable
class AppColors(
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val textSecondary: Color,
    val hint: Color,
    val border: Color,
    val fillUnselected: Color,
    val navyText: Color,
    val chipSelectedContainer: Color,
    val chipSelectedLabel: Color,
    val chipContainer: Color,
    val chipLabel: Color,
    val successBadge: Color
)

val LightAppColors = AppColors(
    background = Color.White,
    surface = Color.White,
    onSurface = Color.Black,
    textSecondary = LightTextSecondary,
    hint = Color.Black.copy(alpha = 0.38f),
    border = LightBorder,
    fillUnselected = LightFill,
    navyText = OnLightArtwork,
    chipSelectedContainer = BrandBlue,
    chipSelectedLabel = OnBrandContent,
    chipContainer = LightFill,
    chipLabel = Color.Black,
    successBadge = LightSuccessBadge
)

val DarkAppColors = AppColors(
    background = DarkBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    textSecondary = DarkTextSecondary,
    hint = DarkOnSurface.copy(alpha = 0.38f),
    border = DarkBorder,
    fillUnselected = DarkFill,
    navyText = DarkOnSurface,
    chipSelectedContainer = Accent,
    chipSelectedLabel = OnAccentText,
    chipContainer = DarkBorder,
    chipLabel = OnBrandContent,
    successBadge = VerifiedGreen.copy(alpha = 0.2f)
)

val LocalAppColors = compositionLocalOf { LightAppColors }

// Theme-aware color getters. These names are kept for compatibility with the
// existing screens: `White` now means "neutral surface" and `Black` means
// "content on a neutral surface". Use OnBrandContent/OnMediaContent/MediaScrim
// for content that must stay fixed regardless of theme.
val AppBackground: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.background

val White: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.surface

val Black: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.onSurface

val Gray: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.textSecondary

val HomeTextSecondary: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.textSecondary

val TextHint: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.hint

val HomeSearchBarBorder: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.border

val HomeCategoryUnselected: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.fillUnselected

val SurfaceLight: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.fillUnselected

// True when the dark app palette is active (respects the manual theme override).
val IsDarkAppTheme: Boolean
    @Composable @ReadOnlyComposable get() = LocalAppColors.current == DarkAppColors

// Navy heading text: keeps the brand navy in light mode, flips to the dark
// theme's primary text color in dark mode.
val NavyText: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.navyText

// Filter-chip colors. Selected: brand blue in light mode (matches the capsule
// toggles), yellow accent in dark mode. Unselected: neutral theme fill in
// light mode, navy chip color in dark mode.
val FilterChipSelectedContainer: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.chipSelectedContainer

val FilterChipSelectedLabel: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.chipSelectedLabel

val FilterChipContainer: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.chipContainer

val FilterChipLabel: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.chipLabel

// Soft badge background behind success/verified icons.
val SuccessBadge: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.successBadge