package com.realeapp.feature.auth.presentation

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * String constants for the auth feature.
 */
internal object AuthStrings {
    const val WELCOME_TITLE_PREFIX = "Welcome to "
    const val WELCOME_SUBTITLE = "Create an account to save properties, get updates and more."
    const val WELCOME_ILLUSTRATION_TEXT = "A Better\nPlace\nAwaits"

    const val OPTION_GOOGLE = "Continue with Google"
    const val OPTION_MOBILE = "Continue with Mobile Number"
    const val OPTION_GUEST = "Continue as Guest"

    const val TERMS_PREFIX = "By continuing, you agree to our "
    const val TERMS_OF_SERVICE = "Terms of Service"
    const val TERMS_AND = " and "
    const val PRIVACY_POLICY = "Privacy Policy"
    const val TERMS_SUFFIX = "."

    const val CD_WELCOME_ILLUSTRATION = "House illustration"
    const val CD_GOOGLE_LOGO = "Google logo"
    const val CD_OPTION_ARROW = "Continue"
}

/**
 * Dimension constants for the auth feature.
 */
internal object AuthDims {
    val SCREEN_PADDING = 24.dp

    val SPACE_8 = 8.dp
    val SPACE_16 = 16.dp
    val SPACE_24 = 24.dp
    val SPACE_32 = 32.dp

    val LOGO_FONT_SIZE = 44.sp
    val TITLE_FONT_SIZE = 22.sp
    val SUBTITLE_FONT_SIZE = 15.sp
    val SCRIPT_FONT_SIZE = 18.sp
    val SCRIPT_LINE_HEIGHT = 22.sp
    val OPTION_FONT_SIZE = 16.sp
    val TERMS_FONT_SIZE = 13.sp

    val OPTION_CORNER_RADIUS = 16.dp
    val OPTION_BORDER_WIDTH = 1.dp
    val OPTION_HORIZONTAL_PADDING = 20.dp
    val OPTION_VERTICAL_PADDING = 18.dp
    val OPTION_ICON_SIZE = 24.dp

    val SCRIPT_TOP_PADDING = 40.dp
    val SCRIPT_END_PADDING = 28.dp
    val SWOOSH_WIDTH = 72.dp
    val SWOOSH_HEIGHT = 10.dp
    val SWOOSH_STROKE_WIDTH = 2.dp
}
