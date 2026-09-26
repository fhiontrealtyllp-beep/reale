package com.fhiont.feature.search.presentation

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centralized UI strings and dimensions for the enquiry chat screen.
 */
internal object ChatStrings {
    const val CD_BACK = "Back"
    const val CD_SEND = "Send message"
    const val CD_PROPERTY_IMAGE = "Property image"

    const val HINT_MESSAGE = "Type a message"
    const val EMPTY_TITLE = "No messages yet"
    const val EMPTY_SUBTITLE = "Start the conversation about this property."
    const val RETRY = "Retry"
    const val FALLBACK_SENDER_NAME = "User"

    const val TIME_FORMAT = "h:mm a"
    const val DATE_TIME_FORMAT = "d MMM, h:mm a"
    const val SERVER_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val UTC_ZONE = "UTC"
}

internal object ChatDims {
    val TOP_BAR_AVATAR_SIZE = 40.dp
    val TOP_BAR_AVATAR_CORNER_RADIUS = 8.dp
    val TOP_BAR_AVATAR_TEXT_SPACING = 12.dp
    val TOP_BAR_TITLE_FONT_SIZE = 15.sp
    val TOP_BAR_SUBTITLE_FONT_SIZE = 12.sp
    val BACK_ICON_SIZE = 24.dp

    val LIST_HORIZONTAL_PADDING = 12.dp
    val LIST_TOP_PADDING = 8.dp
    val LIST_BOTTOM_PADDING = 12.dp
    val MESSAGE_SPACING = 6.dp

    val BUBBLE_CORNER_RADIUS = 16.dp
    val BUBBLE_FLAT_CORNER_RADIUS = 4.dp
    val BUBBLE_HORIZONTAL_PADDING = 12.dp
    val BUBBLE_VERTICAL_PADDING = 8.dp
    val BUBBLE_TEXT_FONT_SIZE = 14.sp
    val BUBBLE_TIME_FONT_SIZE = 10.sp
    val BUBBLE_SENDER_FONT_SIZE = 11.sp
    val BUBBLE_TIME_SPACING = 2.dp
    const val BUBBLE_MAX_WIDTH_FRACTION = 0.78f
    const val TIME_TEXT_ALPHA = 0.7f

    val COMPOSER_PADDING = 12.dp
    val COMPOSER_SPACING = 8.dp
    val COMPOSER_FIELD_CORNER_RADIUS = 24.dp
    val COMPOSER_FIELD_MAX_LINES = 4
    val COMPOSER_SEND_BUTTON_SIZE = 48.dp
    val COMPOSER_SEND_ICON_SIZE = 20.dp
    val COMPOSER_ERROR_BOTTOM_PADDING = 8.dp

    val LOADING_STROKE = 3.dp
    val EMPTY_ICON_SIZE = 64.dp
    val EMPTY_TITLE_SPACING = 16.dp
    val EMPTY_SUBTITLE_SPACING = 8.dp
    val ERROR_PADDING = 24.dp
}
