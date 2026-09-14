package com.realeapp.feature.home.presentation

import com.realeapp.AppStrings
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centralized UI strings and dimensions for the Home feature.
 */
internal object HomeStrings {
    const val HOME_TITLE_LINE1 = "Find a home"
    const val HOME_TITLE_LINE2 = "for your next chapter"

    const val SEARCH_HINT = "Search by location, project or keyword"
    const val SEARCH_START = "Start your search"
    const val CD_SEARCH_ICON = "Search"
    const val CD_FILTER_ICON = "Filter"

    const val CD_NOTIFICATIONS = "Notifications"
    const val CD_LOGO = AppStrings.APP_NAME + " logo"

    const val LOOKING_FOR_TITLE = "Looking for"

    const val CATEGORY_ALL = "All"
    const val CATEGORY_HOMES = "Homes"
    const val CATEGORY_BUY = "Buy"
    const val CATEGORY_RENT = "Rent"
    const val CATEGORY_NEW_PROJECTS = "New Projects"
    const val CATEGORY_COMMERCIAL = "Commercial"

    const val SECTION_FEATURED = "Featured Properties"
    const val SECTION_POPULAR_IN_CITY_FORMAT = "Popular homes in %s"
    const val ACTION_SEE_ALL = "See All"
    const val SEPARATOR_DOT = " · "
    const val BADGE_FEATURED = "Featured"

    const val BANNER_TITLE = "Invest in a brighter tomorrow"
    const val BANNER_SUBTITLE = "Premium homes. Greater possibilities."
    const val CD_BANNER_ARROW = "Learn more"
    const val BANNER_FALLBACK_IMAGE = "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?w=800&q=80"

    const val BADGE_PROMOTIONAL = "Ad"
    const val CD_PROMOTIONAL_PROPERTY_IMAGE = "Promotional property image"

    const val CD_FAVORITE = "Favorite"
    const val CD_PROPERTY_IMAGE = "Property image"
    const val LOCATION_SEPARATOR = ", "

    const val SPEC_BEDS_LABEL = "Beds"
    const val SPEC_BATHS_LABEL = "Baths"
    const val SPEC_SQFT_LABEL = "sq.ft"

    const val NO_PROPERTIES_TITLE = "No properties found"
    const val NO_PROPERTIES_SUBTITLE = "Try a different city or check back later"
}

internal object HomeDims {
    val SCREEN_PADDING = 20.dp
    val SCREEN_TOP_PADDING = 12.dp
    val SECTION_SPACING = 8.dp
    val TITLE_TOP_SPACING = 24.dp
    val TITLE_LINE_SPACING = 4.dp

    val LOGO_ICON_SIZE = 32.dp
    val LOGO_ICON_TEXT_SPACING = 8.dp
    val NOTIFICATION_BADGE_SIZE = 8.dp

    val SEARCH_HEIGHT = 72.dp
    val SEARCH_CORNER_RADIUS = 36.dp
    val SEARCH_ICON_SIZE = 22.dp
    val SEARCH_HORIZONTAL_PADDING = 24.dp
    val SEARCH_VERTICAL_PADDING = 12.dp
    val SEARCH_CONTENT_SPACING = 1.dp
    val SEARCH_ELEVATION = 6.dp
    // Gap between the search bar and the buy/rent toggle; tighter than
    // SECTION_SPACING since the two belong together.
    val SEARCH_TO_TOGGLE_SPACING = 16.dp

    val CATEGORY_CHIP_HEIGHT = 52.dp
    val CATEGORY_CHIP_RADIUS = 26.dp
    val CATEGORY_CHIP_HORIZONTAL_PADDING = 18.dp
    val CATEGORY_CHIP_SPACING = 10.dp
    val CATEGORY_EMOJI_SPACING = 8.dp
    val CATEGORY_FONT_SIZE = 16.sp

    val CATEGORY_CIRCLE_SIZE = 56.dp
    val CATEGORY_ICON_SIZE = 28.dp
    val CATEGORY_ITEM_WIDTH = 72.dp
    val CATEGORY_LABEL_SPACING = 8.dp
    val CATEGORY_SPACING = 16.dp

    val TOGGLE_TITLE_SPACING = 8.dp
    val TOGGLE_ROW_HEIGHT = 48.dp
    val TOGGLE_INNER_PADDING = 4.dp
    val TOGGLE_FONT_SIZE = 14.sp

    val FEATURED_CARD_WIDTH = 196.dp
    val FEATURED_CARD_SPACING = 14.dp
    val FEATURED_CARD_IMAGE_HEIGHT = 184.dp
    val FEATURED_CARD_CORNER_RADIUS = 20.dp
    val FEATURED_CARD_CONTENT_PADDING = 12.dp
    val FEATURED_CARD_ELEVATION = 4.dp

    val FEATURED_TEXT_TOP_SPACING = 10.dp
    val FEATURED_TEXT_LINE_SPACING = 3.dp
    val FEATURED_TITLE_FONT_SIZE = 16.sp
    val FEATURED_META_FONT_SIZE = 14.sp
    val SEE_ALL_CIRCLE_SIZE = 38.dp
    val SEE_ALL_ICON_SIZE = 19.dp
    val FEATURED_DETAILS_SPACING = 4.dp
    val FEATURED_SPEC_SPACING = 12.dp
    val FEATURED_SPEC_LABEL_SPACING = 4.dp
    val FEATURED_BADGE_CORNER_RADIUS = 6.dp
    val FEATURED_BADGE_HORIZONTAL_PADDING = 8.dp
    val FEATURED_BADGE_VERTICAL_PADDING = 4.dp

    val HEART_BUTTON_SIZE = 36.dp
    val HEART_ICON_SIZE = 27.dp
    val HEART_PADDING = 8.dp
    val HEART_STROKE_WIDTH = 1.dp

    val BANNER_HEIGHT = 120.dp
    val BANNER_CORNER_RADIUS = 16.dp
    val BANNER_PADDING = 16.dp
    val BANNER_ARROW_BUTTON_SIZE = 44.dp
    val BANNER_ARROW_ICON_SIZE = 20.dp
    val BANNER_TEXT_OVERLAY_START = 0.55f

    val CATEGORY_BORDER_WIDTH = 1.dp
    val SMALL_ICON_SIZE = 14.dp
    val TINY_ICON_SIZE = 12.dp

    val CARD_ELEVATION = 4.dp
}
