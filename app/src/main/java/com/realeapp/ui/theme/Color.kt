package com.realeapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color


// Brand palette used across the app.
val MainBackground = Color(0xFF141C3D)
val CardBackground = Color(0xFF1C2755)
val TextPrimary = Color(0xFFFBFBFB)
val TextSecondary = Color(0xFF8F9FDC)
val Accent = Color(0xFFFDD60D)
val OnAccentText = Color.Black
val Error = Color(0xFFFF6B6B)
val FilterChipUnselectedContainer = Color(0xFF2B3C83)
val ChipBackground = Color(0xFF263266)
val MapMarker = Color(0xFFE91E63)
val Outline = Color(0xFF2B3C83)

// Brand palette (fixed in both themes).
val BrandBlue = Color(0xFF2563EB)
val BrandRed = Color(0xFFEF4444)
val BrandCoral = Color(0xFFFF6B6B)
val VerifiedGreen = Color(0xFF22C55E)

// Fixed content colors: text/icons drawn on brand-colored containers or on
// photos/media. These must NOT flip with the theme.
val OnBrandContent = Color.White
val OnMediaContent = Color.White
val MediaScrim = Color.Black

// Fixed navy for text/icons drawn on the always-light welcome illustration.
val OnLightArtwork = Color(0xFF1B2A4A)

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
    textSecondary = Color(0xFF6B7280),
    hint = Color.Black.copy(alpha = 0.38f),
    border = Color(0xFFE5E7EB),
    fillUnselected = Color(0xFFF3F4F6),
    navyText = OnLightArtwork,
    chipSelectedContainer = BrandBlue,
    chipSelectedLabel = OnBrandContent,
    chipContainer = Color(0xFFF3F4F6),
    chipLabel = Color.Black,
    successBadge = Color(0xFFDCFCE7)
)

val DarkAppColors = AppColors(
    background = MainBackground,
    surface = CardBackground,
    onSurface = TextPrimary,
    textSecondary = TextSecondary,
    hint = TextPrimary.copy(alpha = 0.38f),
    border = Outline,
    fillUnselected = ChipBackground,
    navyText = TextPrimary,
    chipSelectedContainer = Accent,
    chipSelectedLabel = OnAccentText,
    chipContainer = FilterChipUnselectedContainer,
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