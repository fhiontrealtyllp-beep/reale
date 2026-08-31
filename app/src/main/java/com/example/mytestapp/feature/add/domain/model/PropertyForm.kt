package com.example.mytestapp.feature.add.domain.model

import com.example.mytestapp.feature.search.domain.model.Age
import com.example.mytestapp.feature.search.domain.model.Amenity
import com.example.mytestapp.feature.search.domain.model.BedroomType
import com.example.mytestapp.feature.search.domain.model.Facing
import com.example.mytestapp.feature.search.domain.model.Furnishing
import com.example.mytestapp.feature.search.domain.model.PropertyType
import com.example.mytestapp.feature.search.domain.model.RentBuy
import com.example.mytestapp.feature.search.domain.model.ResidentialCommercial

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
    val agentPhone: String = "",
    val images: List<String> = emptyList()
) {
    fun isValid(): Boolean {
        return validate().isEmpty()
    }

    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        if (rentBuy == null) errors.add("Rent/Buy is required")
        if (residentialCommercial == null) errors.add("Residential/Commercial is required")
        if (propertyType == null) errors.add("Property type is required")
        if (title.isBlank()) errors.add("Title is required")
        if (price.isBlank()) {
            errors.add("Price is required")
        } else if (price.toDoubleOrNull() == null) {
            errors.add("Price must be a valid number")
        }
        if (city.isBlank()) errors.add("City is required")
        if (locality.isBlank()) errors.add("Locality is required")
        if (latitude.isNotBlank() && latitude.toDoubleOrNull() == null) {
            errors.add("Latitude must be a valid number")
        }
        if (longitude.isNotBlank() && longitude.toDoubleOrNull() == null) {
            errors.add("Longitude must be a valid number")
        }
        return errors
    }
}
