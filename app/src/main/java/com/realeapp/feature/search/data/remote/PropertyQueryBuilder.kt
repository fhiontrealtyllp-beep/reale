package com.realeapp.feature.search.data.remote

import com.realeapp.feature.search.data.mapper.jsonName
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyFilter

object PropertyQueryBuilder {

    fun isClientSideMatch(property: Property, filter: PropertyFilter?): Boolean {
        filter ?: return true

        filter.normalizedCity?.let { city ->
            if (property.city != city) return false
        }

        if (filter.normalizedLocalities.isNotEmpty()) {
            if (property.locality !in filter.normalizedLocalities) return false
        }

        filter.normalizedPincode?.let { pincode ->
            if (property.pincode != pincode) return false
        }

        if (filter.rentBuy != null && property.rentBuy != filter.rentBuy) return false
        if (filter.residentialCommercial != null && property.residentialCommercial != filter.residentialCommercial) return false
        if (filter.propertyType != null && property.propertyType != filter.propertyType) return false
        if (filter.bedroomType != null && property.bedroomType != filter.bedroomType) return false
        if (filter.furnishing != null && property.furnishing != filter.furnishing) return false
        if (filter.facing != null && property.facing != filter.facing) return false
        if (filter.age != null && property.age != filter.age) return false

        filter.priceRange?.let { range ->
            if (property.price < range.min || property.price > range.max) return false
        }
        filter.carpetAreaRange?.let { range ->
            val value = property.carpetArea ?: return false
            if (value < range.min || value > range.max) return false
        }
        filter.builtUpAreaRange?.let { range ->
            val value = property.builtUpArea ?: return false
            if (value < range.min || value > range.max) return false
        }
        filter.superBuiltUpAreaRange?.let { range ->
            val value = property.superBuiltUpArea ?: return false
            if (value < range.min || value > range.max) return false
        }

        if (filter.amenities.isNotEmpty()) {
            val propertyAmenities = property.amenities.map { it.jsonName() }.toSet()
            if (!filter.amenities.all { it.jsonName() in propertyAmenities }) return false
        }

        return true
    }
}
