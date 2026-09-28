package com.fhiont.feature.search.domain.model

/**
 * Sentinel [PropertyFilter.bathrooms] value meaning "5 or more bathrooms".
 * Values 1-5 are exact counts; this value matches any property with
 * bathrooms >= [BATHROOMS_FIVE_PLUS_MIN].
 */
const val BATHROOMS_FIVE_PLUS = 6
const val BATHROOMS_FIVE_PLUS_MIN = 5

data class PropertyFilter(
    val city: String? = null,
    val cityLatLng: CityLatLng? = null,
    val localities: List<String> = emptyList(),
    val pincode: String? = null,
    val rentBuy: RentBuy? = null,
    val residentialCommercial: ResidentialCommercial? = null,
    val propertyType: PropertyType? = null,
    val bedroomType: BedroomType? = null,
    val bathrooms: Int? = null,
    val furnishing: Furnishing? = null,
    val facing: Facing? = null,
    val age: Age? = null,
    val amenities: List<Amenity> = emptyList(),
    val priceRange: PriceRange? = null,
    val carpetAreaRange: CarpetAreaRange? = null,
    val builtUpAreaRange: CarpetAreaRange? = null,
    val superBuiltUpAreaRange: CarpetAreaRange? = null
) {
    val normalizedCity: String? = LocationNormalizer.normalizeCity(city)
    val normalizedLocalities: List<String> = LocationNormalizer.normalizeLocalities(localities)
    val normalizedPincode: String? = LocationNormalizer.normalizePincode(pincode)
}
