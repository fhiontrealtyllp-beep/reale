package com.realeapp.feature.search.presentation

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centralized UI strings and dimensions for the Search feature landing screen.
 */
internal object SearchStrings {
    const val TITLE = "Find a place you'll love"

    const val SEARCH_HINT = "Search by location, project or keyword"
    const val CD_SEARCH_ICON = "Search"
    const val CD_CLEAR = "Clear search"
    const val CD_FILTER_ICON = "Filter"
    const val CD_SUGGESTION_LOCATION = "Location"
    const val CD_NOTIFICATIONS = "Notifications"
    const val CD_BACK = "Back"
    const val CD_LOCATION_IMAGE = "Location image"
    const val CD_SEARCH_ARROW = "Search properties"
    const val CD_LIST_VIEW = "List view"
    const val CD_MAP_VIEW = "Map view"

    const val TAB_BUY = "Buy"
    const val TAB_RENT = "Rent"
    const val TAB_PROJECTS = "Projects"
    const val TAB_COMMERCIAL = "Commercial"

    const val SECTION_LOCATIONS = "Explore Popular Locations"
    const val ACTION_SEE_ALL = "See All"
    const val PROPERTIES_SUFFIX = "properties"

    const val LOCATION_NORTH_GOA = "North Goa"
    const val LOCATION_SOUTH_GOA = "South Goa"
    const val LOCATION_PANAJI = "Panaji"

    const val SECTION_LOOKING_FOR = "What are you looking for?"
    const val SECTION_BUDGET = "What's your budget?"
    const val SECTION_FILTERS = "Refine your search"
    const val FILTER_ESSENTIAL = "Essential filters"
    const val FILTER_MORE_OPTIONS = "More filters"
    const val FILTER_LOCALITY = "Locality"
    const val FILTER_PINCODE = "Pincode"
    const val FILTER_LISTING_INTENT = "Looking to"
    const val FILTER_CATEGORY = "Property category"
    const val FILTER_PROPERTY_TYPE = "Property type"
    const val FILTER_BEDROOMS = "Bedrooms"
    const val FILTER_BATHROOMS = "Bathrooms"
    const val FILTER_FURNISHING = "Furnishing"
    const val FILTER_FACING = "Facing"
    const val FILTER_AGE = "Property age"
    const val FILTER_AMENITIES = "Amenities"
    const val FILTER_CARPET_AREA = "Carpet area (sq ft)"
    const val FILTER_BUILT_UP_AREA = "Built-up area (sq ft)"
    const val FILTER_SUPER_BUILT_UP_AREA = "Super built-up area (sq ft)"
    const val FILTER_PRICE_UNDER_25_LAKH = "Under ₹25 L"
    const val FILTER_PRICE_25_TO_50_LAKH = "₹25–50 L"
    const val FILTER_PRICE_50_LAKH_TO_1_CRORE = "₹50 L–1 Cr"
    const val FILTER_PRICE_1_TO_5_CRORE = "₹1–5 Cr"
    const val FILTER_PRICE_5_CRORE_PLUS = "₹5 Cr+"
    const val FILTER_RENT_UNDER_10_THOUSAND = "Under ₹10 K/mo"
    const val FILTER_RENT_10_TO_25_THOUSAND = "₹10–25 K/mo"
    const val FILTER_RENT_25_TO_50_THOUSAND = "₹25–50 K/mo"
    const val FILTER_RENT_50_THOUSAND_PLUS = "₹50 K+/mo"
    const val FILTER_AREA_UNDER_500 = "Under 500"
    const val FILTER_AREA_500_TO_1000 = "500–1,000"
    const val FILTER_AREA_1000_TO_2000 = "1,000–2,000"
    const val FILTER_AREA_2000_PLUS = "2,000+"
    const val FILTER_RESET = "Clear all"

    const val BTN_SEARCH_PROPERTIES = "Search Properties"
    const val RETRY = "Retry"

    const val RUPEE = "₹"
    const val LAKH_SUFFIX = " L"
    const val CRORE_SUFFIX = " Cr"
    const val RANGE_SEPARATOR = " – "
    const val PLUS_SUFFIX = "+"
}

internal object SearchDims {
    val SCREEN_PADDING = 20.dp
    val SECTION_SPACING = 24.dp
    val CONTENT_VERTICAL_PADDING = 16.dp

    val HEADER_ICON_SIZE = 26.dp
    val NOTIFICATION_BADGE_SIZE = 8.dp

    val SEARCH_HEIGHT = 52.dp
    val SEARCH_CORNER_RADIUS = 26.dp
    val SEARCH_ICON_SIZE = 20.dp
    val SEARCH_HORIZONTAL_PADDING = 16.dp
    val SEARCH_CONTENT_SPACING = 12.dp
    val SEARCH_DIVIDER_WIDTH = 1.dp
    val SEARCH_DIVIDER_HEIGHT = 24.dp

    val SUGGESTION_LIST_MAX_HEIGHT = 220.dp
    val SUGGESTION_LIST_CORNER_RADIUS = 16.dp
    val SUGGESTION_LIST_ELEVATION = 4.dp
    val SUGGESTION_LIST_PADDING = 4.dp
    val SUGGESTION_ITEM_HORIZONTAL_PADDING = 16.dp
    val SUGGESTION_ITEM_VERTICAL_PADDING = 12.dp
    val SUGGESTION_ITEM_ICON_SIZE = 20.dp
    val SUGGESTION_ITEM_TEXT_SPACING = 12.dp
    val SUGGESTION_ITEM_DIVIDER_HEIGHT = 1.dp

    val SECTION_HEADER_SPACING = 12.dp
    val FILTER_SECTION_SPACING = 16.dp
    val FILTER_ITEM_SPACING = 8.dp
    val FILTER_FIELD_HEIGHT = 56.dp
    val FILTER_FIELD_CORNER_RADIUS = 12.dp
    val FILTER_PANEL_CORNER_RADIUS = 20.dp
    val FILTER_PANEL_PADDING = 16.dp

    val LOCATION_CARD_WIDTH = 150.dp
    val LOCATION_IMAGE_HEIGHT = 100.dp
    val LOCATION_CARD_CORNER_RADIUS = 14.dp
    val LOCATION_CARD_SPACING = 12.dp
    val LOCATION_TEXT_PADDING = 10.dp
    val LOCATION_TEXT_SPACING = 2.dp

    val TYPE_CARD_SPACING = 10.dp
    val TYPE_CARD_CORNER_RADIUS = 14.dp
    val TYPE_CARD_VERTICAL_PADDING = 14.dp
    val TYPE_ICON_CIRCLE_SIZE = 48.dp
    val TYPE_ICON_SIZE = 24.dp
    val TYPE_LABEL_SPACING = 8.dp

    val BUTTON_HEIGHT = 54.dp
    val BUTTON_CORNER_RADIUS = 27.dp
    val BUTTON_ICON_SIZE = 20.dp
    val BUTTON_CONTENT_SPACING = 8.dp
    val STICKY_ACTION_HORIZONTAL_PADDING = 20.dp
    val STICKY_ACTION_VERTICAL_PADDING = 12.dp
    val FILTER_LIST_BOTTOM_PADDING = 24.dp

    val BORDER_WIDTH = 1.dp
    val CARD_ELEVATION = 2.dp
    val ERROR_PADDING = 24.dp

    val RESULTS_LIST_HORIZONTAL_PADDING = 12.dp
    val RESULTS_LIST_TOP_PADDING = 8.dp
    val RESULTS_LIST_BOTTOM_PADDING = 80.dp

    const val SELECTED_ICON_OVERLAY_ALPHA = 0.2f
}

/**
 * Centralized UI strings for the property detail screen.
 */
internal object DetailStrings {
    const val BADGE_FOR_SALE = "For Sale"
    const val BADGE_FOR_RENT = "For Rent"
    const val PER_SQ_FT_SUFFIX = " per sq ft"
    const val ACTION_GET_HOME_LOAN = "Get Home Loan"

    const val SECTION_OVERVIEW = "Overview"
    const val ACTION_READ_MORE = "Read More"
    const val ACTION_READ_LESS = "Read Less"
    const val SECTION_HIGHLIGHTS = "Key Highlights"
    const val SECTION_LOCATION = "Location"
    const val SECTION_NEARBY_PLACES = "Nearby Places"
    const val ACTION_VIEW_ON_MAP = "View on Map"
    const val NO_NEARBY_PLACES = "No nearby places"
    const val NEARBY_DISTANCE_FORMAT = "%.1f km"

    const val ACTION_CALL = "Call"
    const val ACTION_ENQUIRE = "Enquire Now"

    const val MEDIA_PHOTOS = "Photos"
    const val MEDIA_MAP = "Map"
    const val MEDIA_360 = "360°"
    const val PHOTOS_COUNT_SUFFIX = " Photos"
    const val MORE_PHOTOS_PREFIX = "+"

    // Key-highlights display labels
    const val HL_PRIVATE_POOL = "Private Pool"
    const val HL_MODULAR_KITCHEN = "Modular Kitchen"
    const val HL_COVERED_PARKING = "Covered Parking"
    const val HL_LANDSCAPED_GARDEN = "Landscaped Garden"
    const val HL_24X7_SECURITY = "24x7 Security"
    const val HL_CLOSE_TO_SCHOOLS_HOSPITALS = "Close to Schools & Hospitals"

    const val LABEL_BEDS = "Beds"
    const val LABEL_BATHS = "Baths"
    const val LABEL_SQ_FT = "sq ft"
    const val LABEL_TYPE = "Type"
    const val LABEL_FACING = "Facing"
    const val LABEL_FURNISHING = "Furnishing"

    const val CD_BACK = "Back"
    const val CD_LIKE = "Like"
    const val CD_SHARE = "Share"
    const val CD_CLOSE = "Close"
    const val CD_PHOTOS = "View photos"
    const val CD_VIEW_MAP = "View location on map"
    const val CD_VIRTUAL_TOUR = "360 virtual tour"

    const val FALLBACK_IMAGE_PREFIX = "https://picsum.photos/seed/"
    const val FALLBACK_IMAGE_SUFFIX = "/600/400"

    const val SHARE_MIME_TYPE = "text/plain"
    const val TEL_URI_PREFIX = "tel:"
    const val SMS_URI_PREFIX = "smsto:"
    const val GEO_URI_PREFIX = "geo:"
    const val GEO_QUERY_SEPARATOR = "?q="
    const val GEO_DEFAULT_COORDINATE = "0,0"
    const val MAPS_API_KEY_METADATA = "com.google.android.geo.API_KEY"
    const val MAPS_KEY_PLACEHOLDER = "YOUR_API_KEY"
    const val INDIA_LOCALE_TAG = "en-IN"
}

/**
 * Centralized dimensions for the property detail screen.
 */
internal object DetailDims {
    val HERO_HEIGHT = 320.dp
    val HERO_OVERLAY_PADDING = 16.dp
    val HERO_BUTTON_SIZE = 40.dp
    val HERO_BUTTON_ICON_SIZE = 20.dp
    val HERO_BUTTON_SPACING = 10.dp

    val BADGE_CORNER_RADIUS = 20.dp
    val BADGE_ICON_SIZE = 16.dp
    val BADGE_CONTENT_SPACING = 6.dp
    val BADGE_HORIZONTAL_PADDING = 12.dp
    val BADGE_VERTICAL_PADDING = 6.dp

    val MEDIA_PILL_CORNER_RADIUS = 18.dp
    val MEDIA_PILL_ICON_SIZE = 16.dp
    val MEDIA_PILL_CONTENT_SPACING = 4.dp
    val MEDIA_PILL_SPACING = 8.dp
    val MEDIA_PILL_HORIZONTAL_PADDING = 12.dp
    val MEDIA_PILL_VERTICAL_PADDING = 8.dp

    val THUMB_OVERLAP = 28.dp
    val THUMB_CARD_CORNER_RADIUS = 16.dp
    val THUMB_CARD_PADDING = 10.dp
    val THUMB_CARD_ELEVATION = 4.dp
    val THUMB_SIZE = 62.dp
    val THUMB_CORNER_RADIUS = 10.dp
    val THUMB_SPACING = 8.dp
    val THUMB_SELECTED_BORDER = 2.dp

    // Bottom hero content (photo badge, media pills) must clear the part of the
    // thumbnail card that overlaps the hero: card height minus the overlap below it.
    val HERO_BOTTOM_CONTENT_GAP = 12.dp
    val HERO_BOTTOM_CONTENT_PADDING =
        THUMB_SIZE + (THUMB_CARD_PADDING * 2) - THUMB_OVERLAP + HERO_BOTTOM_CONTENT_GAP

    val SCREEN_PADDING = 16.dp
    val SECTION_SPACING = 20.dp
    val CONTENT_SPACING = 8.dp
    val CONTENT_SPACING_SMALL = 4.dp

    val SALE_BADGE_CORNER_RADIUS = 8.dp
    val SALE_BADGE_HORIZONTAL_PADDING = 10.dp
    val SALE_BADGE_VERTICAL_PADDING = 4.dp
    val LOCATION_ICON_SIZE = 16.dp

    val LOAN_PILL_CORNER_RADIUS = 10.dp
    val LOAN_ICON_SIZE = 18.dp
    val LOAN_CHEVRON_SIZE = 16.dp
    val LOAN_PILL_CONTENT_SPACING = 6.dp
    val LOAN_PILL_HORIZONTAL_PADDING = 12.dp
    val LOAN_PILL_VERTICAL_PADDING = 8.dp

    val STATS_CARD_CORNER_RADIUS = 16.dp
    val STATS_CARD_VERTICAL_PADDING = 14.dp
    val STAT_ICON_SIZE = 24.dp
    val STAT_ITEM_SPACING = 6.dp

    val SECTION_TITLE_SPACING = 10.dp
    val READ_MORE_ICON_SIZE = 18.dp
    val HIGHLIGHT_ICON_SIZE = 20.dp
    val HIGHLIGHT_ITEM_SPACING = 10.dp
    val HIGHLIGHT_ROW_SPACING = 14.dp
    val HIGHLIGHT_LABEL_FONT_SIZE = 14.sp

    val MAP_HEIGHT = 180.dp
    val MAP_CORNER_RADIUS = 12.dp
    val MAP_PLACEHOLDER_ICON_SIZE = 28.dp
    val MAP_PLACEHOLDER_SPACING = 12.dp

    val LOCATION_CARD_CORNER_RADIUS = 16.dp
    val LOCATION_CONTENT_SPACING = 12.dp
    val NEARBY_CARD_WIDTH = 180.dp
    val NEARBY_CARD_PADDING = 12.dp
    val NEARBY_ITEM_SPACING = 12.dp
    val NEARBY_ITEM_ICON_SIZE = 20.dp
    val NEARBY_ITEM_NAME_FONT_SIZE = 13.sp
    val NEARBY_ITEM_DISTANCE_FONT_SIZE = 12.sp

    val BOTTOM_BAR_PADDING = 16.dp
    val BOTTOM_BAR_ELEVATION = 8.dp
    val BOTTOM_BUTTON_HEIGHT = 52.dp
    val BOTTOM_BUTTON_CORNER_RADIUS = 12.dp
    val BOTTOM_BUTTON_SPACING = 12.dp
    val BOTTOM_BUTTON_ICON_SIZE = 20.dp
    val BOTTOM_BUTTON_ICON_SPACING = 8.dp

    val BORDER_WIDTH = 1.dp
    val LIST_BOTTOM_PADDING = 16.dp
    val VIEWER_CLOSE_PADDING = 12.dp
    val VIEWER_COUNTER_PADDING = 24.dp

    const val OVERLAY_SCRIM_ALPHA = 0.55f
    const val ACCENT_BACKGROUND_ALPHA = 0.1f
}
