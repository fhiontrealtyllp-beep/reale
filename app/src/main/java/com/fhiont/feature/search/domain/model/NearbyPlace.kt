package com.fhiont.feature.search.domain.model

data class NearbyPlace(
    val name: String,
    val distanceKm: Double,
    val type: NearbyPlaceType = NearbyPlaceType.OTHER
)
