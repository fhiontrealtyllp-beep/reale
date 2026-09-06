package com.realeapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrandBlue,
    onPrimary = OnBrandContent,
    secondary = Accent,
    onSecondary = OnAccentText,
    background = DarkAppColors.background,
    onBackground = DarkAppColors.onSurface,
    surface = DarkAppColors.surface,
    onSurface = DarkAppColors.onSurface,
    error = Error
)

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = OnBrandContent,
    secondary = Accent,
    onSecondary = OnAccentText,
    background = LightAppColors.background,
    onBackground = LightAppColors.onSurface,
    surface = LightAppColors.surface,
    onSurface = LightAppColors.onSurface,
    error = Error
)

@Composable
fun RealeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+. Disabled by default so the
    // brand palette is used consistently.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography
    ) {
        // Default text style carries AppFontFamily so plain Text() calls inherit the app font.
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current.copy(fontFamily = AppFontFamily),
            LocalAppColors provides if (darkTheme) DarkAppColors else LightAppColors,
            content = content
        )
    }
}