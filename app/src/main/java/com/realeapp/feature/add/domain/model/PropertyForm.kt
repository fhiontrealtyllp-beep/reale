package com.realeapp.feature.add.domain.model

import com.realeapp.feature.add.presentation.AddStrings
import com.realeapp.feature.search.domain.model.Age
import com.realeapp.feature.search.domain.model.Amenity
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Facing
import com.realeapp.feature.search.domain.model.Furnishing
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.domain.model.ResidentialCommercial

data class PropertyForm(
    val rentBuy: RentBuy? = null,
    val residentialCommercial: ResidentialCommercial? = null,
    val propertyType: PropertyType? = null,
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
    val plotArea: String = "",
    val bathrooms: Int = 0,
    val floorNo: String = "",
    val totalFloors: String = "",
    val videoUrl: String = "",
    val pricePerSqFt: Boolean = false,
    val negotiable: Boolean = false,
    val additionalCosts: String = "",
    val propertyStatus: String = "",
    val possessionDate: String = "",
    val agentPhone: String = "",
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
        if (price.isBlank()) {
            errors.add(AddStrings.ERR_PRICE_REQUIRED)
        } else if (price.toDoubleOrNull() == null) {
            errors.add(AddStrings.ERR_PRICE_INVALID)
        }
        if (city.isBlank()) errors.add(AddStrings.ERR_CITY_REQUIRED)
        if (locality.isBlank()) errors.add(AddStrings.ERR_LOCALITY_REQUIRED)
        if (latitude.isNotBlank() && latitude.toDoubleOrNull() == null) {
            errors.add(AddStrings.ERR_LATITUDE_INVALID)
        }
        if (longitude.isNotBlank() && longitude.toDoubleOrNull() == null) {
            errors.add(AddStrings.ERR_LONGITUDE_INVALID)
        }
        return errors
    }
}
