package com.example.mytestapp.feature.search.domain.model

data class PropertyFilter(
    val city: String? = null,
    val cityLatLng: CityLatLng? = null,
    val localities: List<String> = emptyList(),
    val pincode: String? = null,
    val rentBuy: RentBuy? = null,
    val residentialCommercial: ResidentialCommercial? = null,
    val propertyType: PropertyType? = null,
    val bedroomType: BedroomType? = null,
    val furnishing: Furnishing? = null,
    val facing: Facing? = null,
    val age: Age? = null,
    val amenities: List<Amenity> = emptyList(),
    val priceRange: PriceRange? = null,
    val carpetAreaRange: CarpetAreaRange? = null,
    val builtUpAreaRange: CarpetAreaRange? = null,
    val superBuiltUpAreaRange: CarpetAreaRange? = null
)
