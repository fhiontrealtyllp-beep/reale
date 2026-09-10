package com.realeapp.feature.search.presentation

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centralized UI strings and dimensions for the Properties listing screen.
 */
internal object PropertiesStrings {
    const val SCREEN_TITLE = "Properties"

    const val SEARCH_HINT = "Search by location, project or keyword"
    const val CD_SEARCH = "Search"
    const val CD_CLEAR = "Clear search"
    const val CD_FILTER = "Open filter"
    const val CD_SORT = "Sort by"
    const val CD_LIKE = "Like"
    const val CD_PHOTOS = "Photo count"
    const val CD_PROPERTY_IMAGE = "Property image"

    const val TAB_BUY = "Buy"
    const val TAB_RENT = "Rent"
    const val TAB_NEW_PROJECTS = "New Projects"
    const val TAB_COMMERCIAL = "Commercial"

    const val FILTER_PROPERTY_TYPE = "Property Type"
    const val FILTER_PRICE_RANGE = "Price Range"
    const val FILTER_BHK = "BHK"

    const val BADGE_FOR_SALE = "For Sale"
    const val BADGE_FOR_RENT = "For Rent"

    const val BEDS_LABEL = "Beds"
    const val BATHS_LABEL = "Baths"
    const val SQFT_LABEL = "sq ft"
    const val PHOTOS_LABEL = "Photos"

    const val SORT_BY = "Sort by:"
    const val SORT_RELEVANCE = "Relevance"
    const val SORT_PRICE_LOW_HIGH = "Price: Low to High"
    const val SORT_PRICE_HIGH_LOW = "Price: High to Low"
    const val SORT_NEWEST = "Newest"

    const val PROPERTIES_FOUND_FORMAT = "%d Properties found"
    const val END_OF_RESULTS = "End of results"

    const val EMPTY_TITLE = "No Properties Found"
    const val EMPTY_SUBTITLE = "Please look for other Filter"
    const val EMPTY_CITY_TITLE_FORMAT = "No properties in %s"
    const val EMPTY_CHANGE_CITY = "Change City"
}

internal object PropertiesDims {
    val SCREEN_PADDING = 16.dp
    val SECTION_SPACING = 12.dp
    val BORDER_WIDTH = 1.dp

    val SEARCH_BAR_HEIGHT = 52.dp
    val SEARCH_BAR_CORNER_RADIUS = 26.dp
    val SEARCH_BAR_HORIZONTAL_PADDING = 16.dp
    val SEARCH_BAR_INNER_VERTICAL_PADDING = 14.dp
    val SEARCH_BAR_ICON_SIZE = 20.dp
    val SEARCH_BAR_CONTENT_SPACING = 12.dp
    val SEARCH_BAR_DIVIDER_HEIGHT = 24.dp
    val SEARCH_BAR_FONT_SIZE = 15.sp

    val FILTER_CHIP_CORNER_RADIUS = 20.dp
    val FILTER_CHIP_HORIZONTAL_PADDING = 12.dp
    val FILTER_CHIP_VERTICAL_PADDING = 8.dp
    val FILTER_CHIP_ICON_SIZE = 18.dp
    val FILTER_CHIP_ICON_TEXT_SPACING = 4.dp
    val FILTER_CHIPS_SPACING = 8.dp
    val FILTER_CHIPS_TOP_PADDING = 12.dp
    val CHIP_FONT_SIZE = 12.sp

    val TABS_ROW_TOP_PADDING = 12.dp

    val RESULTS_HEADER_VERTICAL_PADDING = 12.dp
    val RESULTS_COUNT_FONT_SIZE = 14.sp
    val SORT_FONT_SIZE = 14.sp
    val RESULTS_LIST_SPACING = 12.dp
    val RESULTS_LIST_VERTICAL_PADDING = 4.dp

    val CARD_CORNER_RADIUS = 12.dp
    val CARD_ELEVATION = 2.dp
    val CARD_PADDING = 12.dp
    val CARD_IMAGE_WIDTH = 130.dp
    val CARD_IMAGE_HEIGHT = 110.dp
    val CARD_IMAGE_CORNER_RADIUS = 10.dp
    val CARD_CONTENT_SPACING = 10.dp

    val BADGE_CORNER_RADIUS = 6.dp
    val BADGE_HORIZONTAL_PADDING = 8.dp
    val BADGE_VERTICAL_PADDING = 4.dp
    val BADGE_FONT_SIZE = 10.sp

    val PHOTO_COUNT_CORNER_RADIUS = 6.dp
    val PHOTO_COUNT_ICON_SIZE = 14.dp
    val PHOTO_COUNT_FONT_SIZE = 10.sp
    val PHOTO_COUNT_HORIZONTAL_PADDING = 6.dp
    val PHOTO_COUNT_VERTICAL_PADDING = 3.dp

    val LIKE_ICON_SIZE = 20.dp

    val TITLE_FONT_SIZE = 15.sp
    val LOCATION_FONT_SIZE = 13.sp
    val PRICE_FONT_SIZE = 17.sp
    val SPEC_FONT_SIZE = 12.sp
    val SPEC_ICON_SIZE = 14.dp
    val SPEC_ICON_TEXT_SPACING = 2.dp
    val SPEC_ITEM_SPACING = 10.dp

    val EMPTY_ICON_HEIGHT = 80.dp
    val EMPTY_TITLE_FONT_SIZE = 18.sp
    val EMPTY_SUBTITLE_FONT_SIZE = 14.sp
    val EMPTY_TITLE_SPACING = 16.dp
    val EMPTY_SUBTITLE_SPACING = 8.dp
    val EMPTY_BUTTON_SPACING = 16.dp

    val FOOTER_PADDING = 24.dp
    val FOOTER_FONT_SIZE = 14.sp
}
