package com.realeapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color


// Brand palette (fixed in both themes). Private: screens must use the
// theme-aware getters below so every color read goes through LocalAppColors.
private val FixedBrandCoral = Color(0xFFFF6B6B)
private val FixedAccent = Color(0xFFFDD60D)
private val FixedVerifiedGreen = Color(0xFF22C55E)
private val FixedError = Color(0xFFFF6B6B)
private val FixedMapMarker = Color(0xFFE91E63)

// Fixed content colors: text/icons drawn on brand-colored containers or on
// photos/media. Private: screens must use the theme-aware getters below.
private val FixedOnBrandContent = Color.White
private val FixedOnAccentText = Color.Black
private val FixedOnMediaContent = Color.White
private val FixedMediaScrim = Color.Black

// Fixed navy for text/icons drawn on the always-light welcome illustration.
private val FixedOnLightArtwork = Color(0xFF1B2A4A)

// Light theme palette. Private: screens must use the theme-aware getters.
private val LightAccent = Color(0xFF2563EB)
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
// Elevated pill fill: dark with a faint warm/yellow tint so unselected
// chips and toggle tracks read against the navy background.
private val DarkPillFill = Color(0xFF33322C)

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
    val controlAccent: Color,
    val onControlAccent: Color,
    val navyText: Color,
    val chipSelectedContainer: Color,
    val chipSelectedLabel: Color,
    val chipContainer: Color,
    val chipLabel: Color,
    val successBadge: Color,
    val brandCoral: Color,
    val accent: Color,
    val verifiedGreen: Color,
    val error: Color,
    val mapMarker: Color,
    val onBrandContent: Color,
    val onAccentText: Color,
    val onMediaContent: Color,
    val mediaScrim: Color,
    val onLightArtwork: Color
)

val LightAppColors = AppColors(
    background = Color.White,
    surface = Color.White,
    onSurface = Color.Black,
    textSecondary = LightTextSecondary,
    hint = Color.Black.copy(alpha = 0.38f),
    border = LightBorder,
    fillUnselected = LightFill,
    controlAccent = LightAccent,
    onControlAccent = FixedOnBrandContent,
    navyText = FixedOnLightArtwork,
    chipSelectedContainer = LightAccent,
    chipSelectedLabel = FixedOnBrandContent,
    chipContainer = Color.White,
    chipLabel = LightTextSecondary,
    successBadge = LightSuccessBadge,
    brandCoral = FixedBrandCoral,
    accent = FixedAccent,
    verifiedGreen = FixedVerifiedGreen,
    error = FixedError,
    mapMarker = FixedMapMarker,
    onBrandContent = FixedOnBrandContent,
    onAccentText = FixedOnAccentText,
    onMediaContent = FixedOnMediaContent,
    mediaScrim = FixedMediaScrim,
    onLightArtwork = FixedOnLightArtwork
)

val DarkAppColors = AppColors(
    background = DarkBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    textSecondary = DarkTextSecondary,
    hint = DarkOnSurface.copy(alpha = 0.38f),
    border = DarkBorder,
    fillUnselected = DarkFill,
    controlAccent = FixedAccent,
    onControlAccent = FixedOnAccentText,
    navyText = DarkOnSurface,
    chipSelectedContainer = FixedAccent,
    chipSelectedLabel = FixedOnAccentText,
    chipContainer = DarkPillFill,
    chipLabel = DarkTextSecondary,
    successBadge = FixedVerifiedGreen.copy(alpha = 0.2f),
    brandCoral = FixedBrandCoral,
    accent = FixedAccent,
    verifiedGreen = FixedVerifiedGreen,
    error = FixedError,
    mapMarker = FixedMapMarker,
    onBrandContent = FixedOnBrandContent,
    onAccentText = FixedOnAccentText,
    onMediaContent = FixedOnMediaContent,
    mediaScrim = FixedMediaScrim,
    onLightArtwork = FixedOnLightArtwork
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

// Accent for interactive controls (slider thumb/track, focused field
// borders, cursors): brand blue in light mode, yellow accent in dark mode.
val ControlAccent: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.controlAccent

// Content drawn on ControlAccent containers (white on blue in light mode,
// black on yellow in dark mode).
val OnControlAccent: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.onControlAccent

// True when the dark app palette is active (respects the manual theme override).
val IsDarkAppTheme: Boolean
    @Composable @ReadOnlyComposable get() = LocalAppColors.current == DarkAppColors

// Navy heading text: keeps the brand navy in light mode, flips to the dark
// theme's primary text color in dark mode.
val NavyText: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.navyText

// Filter-chip colors. Selected: brand blue in light mode (matches the capsule
// toggles), yellow accent in dark mode. Unselected: theme surface with
// secondary label, matching the floating white-pill toggle theme.
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

// Brand palette getters. Values are identical in both themes (brand identity
// stays fixed) but reads go through LocalAppColors like every other color.
val BrandCoral: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.brandCoral

val Accent: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.accent

val VerifiedGreen: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.verifiedGreen

val Error: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.error

val MapMarker: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.mapMarker

// Content colors drawn on brand-colored containers or on photos/media. Values
// stay fixed in both themes but reads go through LocalAppColors.
val OnBrandContent: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.onBrandContent

val OnAccentText: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.onAccentText

val OnMediaContent: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.onMediaContent

val MediaScrim: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.mediaScrim

val OnLightArtwork: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.onLightArtwork