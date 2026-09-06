package com.realeapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Brand palette used across the app.
val MainBackground = Color(0xFF141C3D)
val CardBackground = Color(0xFF1C2755)
val ItemCardBackground = Color(0xFF1C1C24)
val TextPrimary = Color(0xFFFBFBFB)
val TextSecondary = Color(0xFF8F9FDC)
val Accent = Color(0xFFFDD60D)
val OnAccent = MainBackground
val OnAccentText = Color.Black
val BottomNavBackground = MainBackground
val Error = Color(0xFFFF6B6B)
val FilterChipUnselectedContainer = Color(0xFF2B3C83)
val ChipBackground = Color(0xFF263266)
val MapMarker = Color(0xFFE91E63)
val Outline = Color(0xFF2B3C83)
val DialogSectionBackground = Color(0xFF243265)
val SliderTrackInactive = Color(0xFF53649D)
val TextFieldUnfocusedBorder = Color(0xFF7180B2)
val TextFieldUnfocusedLabel = Color(0xFFB9C1DF)

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

// Theme-aware palette. Screens read these roles through the shared color
// getters below so the UI repaints when the theme mode changes.
@Immutable
class AppColors(
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val hint: Color,
    val border: Color,
    val fillUnselected: Color,
    val iconUnselected: Color
)

val LightAppColors = AppColors(
    background = Color.White,
    surface = Color.White,
    onSurface = Color.Black,
    textSecondary = Color(0xFF6B7280),
    textTertiary = Color(0xFF71737E),
    hint = Color.Black.copy(alpha = 0.38f),
    border = Color(0xFFE5E7EB),
    fillUnselected = Color(0xFFF3F4F6),
    iconUnselected = Color(0xFF9CA3AF)
)

val DarkAppColors = AppColors(
    background = MainBackground,
    surface = CardBackground,
    onSurface = TextPrimary,
    textSecondary = TextSecondary,
    textTertiary = TextSecondary,
    hint = TextPrimary.copy(alpha = 0.38f),
    border = Outline,
    fillUnselected = ChipBackground,
    iconUnselected = TextFieldUnfocusedLabel
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

val TextTertiary: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.textTertiary

val TextHint: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.hint

val HomeSearchBarBorder: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.border

val HomeCategoryUnselected: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.fillUnselected

val SurfaceLight: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.fillUnselected

val HomeCategoryIconUnselected: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.iconUnselected

val OnSurfaceLight: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.onSurface