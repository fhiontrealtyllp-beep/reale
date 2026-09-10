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

    const val ENTER_NUMBER_TITLE = "Enter Your Mobile Number"
    const val ENTER_NUMBER_SUBTITLE = "We'll send you a verification code to continue."
    const val COUNTRY_CODE_IN = "+91"
    const val PHONE_HINT = "Enter mobile number"
    const val BUTTON_SEND_OTP = "Send OTP"
    const val DIVIDER_OR = "or"

    const val BADGE_SECURE = "Secure\nand safe"
    const val BADGE_PROTECTED = "Your data\nis protected"
    const val BADGE_QUICK = "Quick\nand easy"

    const val VERIFY_TITLE = "Verify Your Number"
    const val VERIFY_SUBTITLE = "We've sent a 6-digit OTP to"
    const val EDIT = "Edit"
    const val RESEND_PREFIX = "Didn't receive the code? "
    const val RESEND_OTP = "Resend OTP"
    const val RESEND_IN = " in "
    const val VERIFY_ILLUSTRATION_TEXT = "Good\nHomes\nBrighter\nTomorrows"

    const val PROFILE_TITLE = "Tell us a bit about yourself"
    const val PROFILE_SUBTITLE = "This helps us personalize your experience."
    const val LABEL_FULL_NAME = "Full Name"
    const val NAME_HINT = "Enter your full name"
    const val NAME_HELPER = "Enter your full name as per official documents."
    const val LABEL_DOB = "Date of Birth"
    const val DOB_HINT = "DD MMM YYYY"
    const val DOB_HELPER = "You must be 18 years or older to continue."
    const val BUTTON_CONTINUE = "Continue"
    const val BUTTON_OK = "OK"
    const val BUTTON_CANCEL = "Cancel"
    const val INFO_SECURE = "Your information is safe and secure with us."

    const val ERROR_INVALID_PHONE = "Please enter a valid 10-digit mobile number"
    const val ERROR_INVALID_OTP = "Please enter the 6-digit OTP"
    const val ERROR_NAME_REQUIRED = "Full name is required to continue"
    const val ERROR_SESSION_EXPIRED = "Session expired. Please request a new OTP"
    const val ERROR_OTP_ACTIVITY_NOT_FOUND = "Could not start verification. Please try again."
    const val ERROR_OTP_SEND_FAILED = "Failed to send OTP. Please try again."
    const val ERROR_OTP_VERIFY_FAILED = "Failed to verify OTP. Please try again."
    const val ERROR_OTP_INVALID_CODE = "Invalid OTP. Please try again."
    const val ERROR_GOOGLE_SIGN_IN_FAILED = "Google sign-in failed. Please try again."
    const val ERROR_GOOGLE_NO_ACCOUNT = "No Google account found. Please add one to your device."
    const val ERROR_LOGIN_FAILED = "Login failed. Please check your credentials and try again."
    const val ERROR_REGISTRATION_FAILED = "Registration failed. Please try again."

    const val LOGIN_PROMPT_TITLE = "Login to save"
    const val LOGIN_PROMPT_MESSAGE = "Sign in to save your favorite properties and access them anytime."
    const val LOGIN_PROMPT_LOGIN = "Login"
    const val LOGIN_PROMPT_CANCEL = "Maybe later"

    const val CD_WELCOME_ILLUSTRATION = "House illustration"
    const val CD_GOOGLE_LOGO = "Google logo"
    const val CD_OPTION_ARROW = "Continue"
    const val CD_BACK = "Back"
    const val CD_COUNTRY_PICKER = "Select country code"
    const val CD_BADGE_SECURE = "Secure and safe"
    const val CD_BADGE_PROTECTED = "Data protected"
    const val CD_BADGE_QUICK = "Quick and easy"
    const val CD_CALENDAR = "Pick date of birth"
    const val CD_SHIELD = "Information secure"
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

    val SPACE_12 = 12.dp
    val SPACE_40 = 40.dp

    val SMALL_LOGO_FONT_SIZE = 32.sp
    val ENTER_TITLE_FONT_SIZE = 24.sp
    val ENTER_SUBTITLE_FONT_SIZE = 14.sp
    val ENTER_ILLUSTRATION_WIDTH = 150.dp

    val FIELD_CORNER_RADIUS = 14.dp
    val COUNTRY_CODE_FONT_SIZE = 15.sp
    val FLAG_WIDTH = 26.dp
    val FLAG_HEIGHT = 18.dp
    val FLAG_CORNER_RADIUS = 3.dp

    val SEND_OTP_HEIGHT = 52.dp
    val SEND_OTP_CORNER_RADIUS = 14.dp
    val SEND_OTP_FONT_SIZE = 16.sp
    val SEND_OTP_PROGRESS_SIZE = 24.dp
    val BUTTON_DISABLED_ALPHA = 0.5f

    val BADGE_CIRCLE_SIZE = 56.dp
    val BADGE_ICON_SIZE = 22.dp
    val BADGE_LABEL_FONT_SIZE = 12.sp
    val BADGE_LABEL_LINE_HEIGHT = 16.sp

    val BACK_ICON_SIZE = 24.dp

    val SPACE_4 = 4.dp

    val VERIFY_ILLUSTRATION_WIDTH = 160.dp
    val VERIFY_SCRIPT_FONT_SIZE = 11.sp
    val VERIFY_SCRIPT_LINE_HEIGHT = 14.sp
    val VERIFY_PHONE_FONT_SIZE = 15.sp
    val PROFILE_TITLE_FONT_SIZE = 20.sp

    val OTP_BOX_SIZE = 48.dp
    val OTP_BOX_CORNER_RADIUS = 12.dp
    val OTP_BOX_BORDER_WIDTH = 1.dp
    val OTP_BOX_FOCUSED_BORDER_WIDTH = 2.dp
    val OTP_FONT_SIZE = 20.sp

    val FIELD_LABEL_FONT_SIZE = 14.sp
    val FIELD_HELPER_FONT_SIZE = 12.sp
    val INFO_FONT_SIZE = 13.sp
    val INFO_ICON_SIZE = 18.dp

    val LOGIN_PROMPT_HORIZONTAL_MARGIN = 32.dp
    val LOGIN_PROMPT_PADDING = 24.dp
    val LOGIN_PROMPT_CORNER_RADIUS = 24.dp
    val LOGIN_PROMPT_ELEVATION = 8.dp
    val LOGIN_PROMPT_ICON_CONTAINER_SIZE = 64.dp
    val LOGIN_PROMPT_ICON_SIZE = 32.dp
    val LOGIN_PROMPT_TITLE_FONT_SIZE = 20.sp
    val LOGIN_PROMPT_MESSAGE_FONT_SIZE = 14.sp
    val LOGIN_PROMPT_TITLE_SPACING = 16.dp
    val LOGIN_PROMPT_MESSAGE_SPACING = 8.dp
    val LOGIN_PROMPT_BUTTONS_SPACING = 24.dp
    val LOGIN_PROMPT_BUTTON_SPACING = 12.dp
    val LOGIN_PROMPT_BUTTON_HEIGHT = 48.dp
    val LOGIN_PROMPT_BUTTON_CORNER_RADIUS = 12.dp
    val LOGIN_PROMPT_BUTTON_FONT_SIZE = 16.sp
}
