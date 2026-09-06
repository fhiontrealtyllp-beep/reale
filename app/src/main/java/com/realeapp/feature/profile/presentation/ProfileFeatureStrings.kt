package com.realeapp.feature.profile.presentation

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centralized UI strings and dimensions for the Profile feature.
 */
internal object ProfileStrings {
    const val APP_NAME_BLUE = "Real"
    const val APP_NAME_ACCENT = "e"

    const val CD_NOTIFICATIONS = "Notifications"

    const val EDIT_PROFILE = "Edit Profile"
    const val PHONE_VERIFIED = "Phone Verified"
    const val EMAIL_VERIFIED = "Email Verified"
    const val CD_PHONE_VERIFIED = "Phone verified"
    const val CD_EMAIL_VERIFIED = "Email verified"
    const val CD_AVATAR = "Profile picture"
    const val CD_EDIT_AVATAR = "Change profile picture"

    const val LIST_PROPERTY_TITLE = "List Your Property"
    const val LIST_PROPERTY_SUBTITLE = "Sell or rent your property in just a few steps."
    const val LIST_PROPERTY_BUTTON = "List Property"
    const val CD_LIST_PROPERTY_IMAGE = "List property"
    const val CD_LIST_PROPERTY_ARROW = "List property"

    const val SECTION_MY_ACTIVITY = "My Activity"
    const val MY_LISTINGS = "My Listings"
    const val MY_LISTINGS_SUBTITLE = "Manage your properties"
    const val MY_ENQUIRIES = "My Enquiries"
    const val MY_ENQUIRIES_SUBTITLE = "Track your enquiries"
    const val SECTION_ACCOUNT = "Account"
    const val PERSONAL_INFORMATION = "Personal Information"
    const val PERSONAL_INFORMATION_SUBTITLE = "Name, phone, email"
    const val NOTIFICATIONS = "Notifications"
    const val NOTIFICATIONS_SUBTITLE = "Manage your preferences"
    const val SETTINGS = "Settings"
    const val SETTINGS_SUBTITLE = "App settings and privacy"
    const val APPEARANCE = "Appearance"
    const val APPEARANCE_SUBTITLE = "Theme: %s"
    const val THEME_SYSTEM = "System default"
    const val THEME_LIGHT = "Light"
    const val THEME_DARK = "Dark"
    const val THEME_DIALOG_TITLE = "Choose theme"
    const val HELP_SUPPORT = "Help & Support"
    const val HELP_SUPPORT_SUBTITLE = "FAQs, support and contact us"
    const val LOGOUT = "Logout"

    const val CD_PERSONAL_INFO = "Personal information"
    const val CD_NOTIFICATIONS_ITEM = "Notifications"
    const val CD_SETTINGS = "Settings"
    const val CD_APPEARANCE = "Appearance"
    const val CD_HELP_SUPPORT = "Help and support"
    const val CD_LOGOUT = "Logout"
    const val CD_ARROW = "Open"

    const val LABEL_NAME = "Name"
    const val LABEL_PHONE = "Phone"
    const val LABEL_EMAIL = "Email"
    const val LABEL_ADDRESS = "Address"
    const val ACTION_SAVE = "Save"
    const val ACTION_CANCEL = "Cancel"

    const val LOGIN_PROMPT_TITLE = "Please Login to View your profile"
    const val LOGIN_BUTTON = "Login"
    const val FALLBACK_INITIALS = "?"

    const val SUPPORT_EMAIL = "fhiontrealtyllp@gmail.com"
    const val EMAIL_SUBJECT = "Reale App - Support Request"
    const val EMAIL_BODY = "Hi Team,\n\nI am writing to you regarding the Reale app.\n\n"
    const val EMAIL_CHOOSER_TITLE = "Write to us"
    const val ERROR_NO_EMAIL_APP = "No email app found"

    const val MSG_IMAGE_UPDATED = "Profile image updated"
    const val MSG_LOGGED_OUT = "Logged out successfully"

    // Profile field keys sent to the update API.
    const val FIELD_NAME = "name"
    const val FIELD_EMAIL = "email"
    const val FIELD_PHONE = "phone"
    const val FIELD_ADDRESS = "address"
    const val FIELD_IMAGE = "image"
}

internal object ProfileDims {
    val SCREEN_PADDING = 20.dp
    val SECTION_SPACING = 24.dp
    val SECTION_TITLE_TO_CONTENT = 12.dp
    val SECTION_TITLE_FONT_SIZE = 17.sp
    val CARD_CORNER_RADIUS = 20.dp
    val CARD_ELEVATION = 2.dp
    val CARD_INNER_PADDING = 16.dp
    val CARD_INNER_ICON_CORNER_RADIUS = 12.dp

    val APP_NAME_FONT_SIZE = 30.sp
    val TOP_BAR_ICON_SIZE = 26.dp
    val NOTIFICATION_BADGE_SIZE = 8.dp

    val AVATAR_SIZE = 84.dp
    val AVATAR_EDIT_BADGE_SIZE = 30.dp
    val AVATAR_EDIT_BADGE_BORDER_WIDTH = 2.dp
    val AVATAR_EDIT_ICON_SIZE = 16.dp
    val AVATAR_PROGRESS_SIZE = 50.dp
    val AVATAR_PROGRESS_STROKE = 3.dp
    val AVATAR_TO_DETAILS_SPACING = 14.dp
    val INITIALS_FONT_SIZE = 30.sp
    val PROFILE_NAME_FONT_SIZE = 18.sp
    val PROFILE_DETAIL_FONT_SIZE = 13.sp
    val PROFILE_DETAIL_SPACING = 2.dp

    val EDIT_PROFILE_BUTTON_HEIGHT = 36.dp
    val EDIT_PROFILE_BUTTON_CORNER_RADIUS = 20.dp
    val EDIT_PROFILE_BUTTON_BORDER = 1.dp
    val EDIT_PROFILE_BUTTON_HORIZONTAL_PADDING = 16.dp
    val EDIT_PROFILE_BUTTON_FONT_SIZE = 13.sp

    val VERIFIED_BADGE_TOP_SPACING = 12.dp
    val VERIFIED_BADGE_SPACING = 8.dp
    val VERIFIED_BADGE_ICON_SIZE = 14.dp
    val VERIFIED_BADGE_ICON_TEXT_SPACING = 4.dp
    val VERIFIED_BADGE_FONT_SIZE = 12.sp
    val VERIFIED_BADGE_HORIZONTAL_PADDING = 10.dp
    val VERIFIED_BADGE_VERTICAL_PADDING = 6.dp
    val VERIFIED_BADGE_CORNER_RADIUS = 20.dp

    val LIST_PROPERTY_BANNER_HEIGHT = 130.dp
    val LIST_PROPERTY_BANNER_CORNER_RADIUS = 20.dp
    val LIST_PROPERTY_BANNER_ELEVATION = 0.dp
    val LIST_PROPERTY_BANNER_IMAGE_CORNER_RADIUS = 16.dp
    val LIST_PROPERTY_IMAGE_WIDTH = 72.dp
    val LIST_PROPERTY_IMAGE_ICON_SIZE = 40.dp
    val LIST_PROPERTY_BUTTON_HEIGHT = 40.dp
    val LIST_PROPERTY_BUTTON_CORNER_RADIUS = 20.dp
    val LIST_PROPERTY_BUTTON_HORIZONTAL_PADDING = 10.dp
    val BANNER_TITLE_FONT_SIZE = 15.sp
    val BANNER_SUBTITLE_FONT_SIZE = 12.sp
    val BANNER_TITLE_SUBTITLE_SPACING = 4.dp
    val BANNER_CONTENT_SPACING = 10.dp
    val BANNER_BUTTON_FONT_SIZE = 12.sp
    val BANNER_BUTTON_ICON_SPACING = 4.dp
    val BANNER_ARROW_ICON_SIZE = 18.dp

    val MENU_ITEM_VERTICAL_PADDING = 16.dp
    val MENU_ITEM_ICON_SIZE = 44.dp
    val MENU_ITEM_ICON_INNER_SIZE = 24.dp
    val MENU_ITEM_ICON_TEXT_SPACING = 12.dp
    val MENU_ITEM_TITLE_FONT_SIZE = 15.sp
    val MENU_ITEM_SUBTITLE_FONT_SIZE = 12.sp
    val MENU_ITEM_TEXT_SPACING = 2.dp
    val MENU_ITEM_ARROW_SIZE = 16.dp
    val DIVIDER_THICKNESS = 1.dp

    val DIALOG_CORNER_RADIUS = 20.dp
    val DIALOG_TITLE_FONT_SIZE = 18.sp
    val THEME_OPTION_VERTICAL_PADDING = 12.dp
    val THEME_OPTION_RADIO_TEXT_SPACING = 12.dp
    val THEME_OPTION_FONT_SIZE = 15.sp
    val DIALOG_BUTTON_CORNER_RADIUS = 12.dp
    val DIALOG_BUTTON_HEIGHT = 48.dp
    val DIALOG_FIELD_CORNER_RADIUS = 12.dp
    val DIALOG_FIELD_ICON_SIZE = 20.dp
    val FIELD_SPACING = 16.dp

    val LOGIN_PROMPT_PADDING = 24.dp
    val LOGIN_TITLE_TOP_SPACING = 16.dp
    val LOGIN_BUTTON_TOP_SPACING = 24.dp
    val LOGIN_BUTTON_HEIGHT = 48.dp
    val LOGIN_BUTTON_CORNER_RADIUS = 12.dp
    val LOGIN_BUTTON_FONT_SIZE = 18.sp

    val EMPTY_ICON_SIZE = 80.dp
    val SNACKBAR_CORNER_RADIUS = 8.dp
}
