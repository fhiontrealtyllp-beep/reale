package com.fhiont.feature.add.domain.model

import com.fhiont.feature.add.presentation.AddStrings
import com.fhiont.feature.search.domain.model.Age
import com.fhiont.feature.search.domain.model.Amenity
import com.fhiont.feature.search.domain.model.BedroomType
import com.fhiont.feature.search.domain.model.Facing
import com.fhiont.feature.search.domain.model.Furnishing
import com.fhiont.feature.search.domain.model.ListingCategory
import com.fhiont.feature.search.domain.model.NearbyPlace
import com.fhiont.feature.search.domain.model.PropertyType
import com.fhiont.feature.search.domain.model.RentBuy
import com.fhiont.feature.search.domain.model.ResidentialCommercial

data class PropertyForm(
    val rentBuy: RentBuy? = RentBuy.RENT,
    val residentialCommercial: ResidentialCommercial? = ResidentialCommercial.RESIDENTIAL,
    val propertyType: PropertyType? = PropertyType.APARTMENT,
    val bedroomType: BedroomType? = null,
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val city: String = "",
    val locality: String = "",
    val pincode: String = "",
    val address: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val furnishing: Furnishing? = null,
    val facing: Facing? = null,
    val age: Age? = null,
    val amenities: List<Amenity> = emptyList(),
    val carpetArea: String = "",
    val builtUpArea: String = "",
    val superBuiltUpArea: String = "",
    val bathrooms: Int = 0,
    val agentPhone: String = "",
    val listingCategory: ListingCategory = ListingCategory.NORMAL,
    val nearbyPlaces: List<NearbyPlace> = emptyList(),
    val images: List<String> = emptyList()
) {
    fun isValid(): Boolean {
        return validate().isEmpty()
    }

    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        if (rentBuy == null) errors.add(AddStrings.ERR_RENT_BUY_REQUIRED)
        if (residentialCommercial == null) errors.add(AddStrings.ERR_RESIDENTIAL_COMMERCIAL_REQUIRED)
        if (propertyType == null) errors.add(AddStrings.ERR_PROPERTY_TYPE_REQUIRED)
        if (title.isBlank()) errors.add(AddStrings.ERR_TITLE_REQUIRED)
        if (description.isBlank()) errors.add(AddStrings.ERR_DESCRIPTION_REQUIRED)
        if (price.isBlank()) {
            errors.add(AddStrings.ERR_PRICE_REQUIRED)
        } else if (price.toDoubleOrNull() == null) {
            errors.add(AddStrings.ERR_PRICE_INVALID)
        }
        if (city.isBlank()) errors.add(AddStrings.ERR_CITY_REQUIRED)
        if (locality.isBlank()) errors.add(AddStrings.ERR_LOCALITY_REQUIRED)
        if (pincode.isBlank()) errors.add(AddStrings.ERR_PINCODE_REQUIRED)
        if (address.isBlank()) errors.add(AddStrings.ERR_ADDRESS_REQUIRED)
        if (latitude.isBlank()) {
            errors.add(AddStrings.ERR_LATITUDE_REQUIRED)
        } else if (latitude.toDoubleOrNull() == null) {
            errors.add(AddStrings.ERR_LATITUDE_INVALID)
        }
        if (longitude.isBlank()) {
            errors.add(AddStrings.ERR_LONGITUDE_REQUIRED)
        } else if (longitude.toDoubleOrNull() == null) {
            errors.add(AddStrings.ERR_LONGITUDE_INVALID)
        }
        if (bedroomType == null) errors.add(AddStrings.ERR_BEDROOM_TYPE_REQUIRED)
        if (bathrooms < 1) errors.add(AddStrings.ERR_BATHROOMS_REQUIRED)
        if (furnishing == null) errors.add(AddStrings.ERR_FURNISHING_REQUIRED)
        if (facing == null) errors.add(AddStrings.ERR_FACING_REQUIRED)
        if (age == null) errors.add(AddStrings.ERR_AGE_REQUIRED)
        if (carpetArea.isBlank() && builtUpArea.isBlank() && superBuiltUpArea.isBlank()) {
            errors.add(AddStrings.ERR_AREA_REQUIRED)
        }
        if (agentPhone.isBlank()) errors.add(AddStrings.ERR_AGENT_PHONE_REQUIRED)
        if (images.size < 2) {
            errors.add(AddStrings.ERR_MIN_PHOTOS)
        }
        if (images.size > AddStrings.MAX_PROPERTY_PHOTOS) {
            errors.add(AddStrings.ERR_MAX_PHOTOS)
        }
        return errors
    }
}
