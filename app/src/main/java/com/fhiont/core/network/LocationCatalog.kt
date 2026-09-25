package com.fhiont.core.network

/** Cities and localities catalog returned by locations.php. */
data class LocationCatalog(
    val cities: List<String>,
    val localitiesByCity: Map<String, List<String>>
)
