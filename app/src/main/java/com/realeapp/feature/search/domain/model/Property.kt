package com.realeapp.feature.search.domain.model

data class Property(
    val id: String,
    val documentId: String? = null,
    val userId: String,
    val title: String,
    val description: String,
    val price: Double,
    val city: String,
    val locality: String,
    val pincode: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val images: List<String> = emptyList(),
    val isLiked: Boolean? = null,
    val rating: Double? = null,
    val agentPhone: String = "",
    val status: String? = null,
    val listingCategory: ListingCategory = ListingCategory.NORMAL,
    val createdAt: String? = null,
    val rentBuy: RentBuy? = null,
    val residentialCommercial: ResidentialCommercial? = null,
    val propertyType: PropertyType? = null,
    val bedroomType: BedroomType? = null,
    val bathrooms: Int? = null,
    val furnishing: Furnishing? = null,
    val facing: Facing? = null,
    val age: Age? = null,
    val amenities: List<Amenity> = emptyList(),
    val nearbyPlaces: List<NearbyPlace> = emptyList(),
    val carpetArea: Double? = null,
    val builtUpArea: Double? = null,
    val superBuiltUpArea: Double? = null
) {
    fun isRentProperty(): Boolean = rentBuy == RentBuy.RENT
}

enum class ListingCategory {
    FEATURED,
    PROMOTIONAL,
    NORMAL
}
