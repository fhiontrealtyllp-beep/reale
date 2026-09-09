package com.realeapp.feature.search.domain.model

/**
 * A city/locality suggestion derived from the app's property database.
 */
data class LocationSuggestion(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String,
    val fullText: String
)
