package com.realeapp.feature.search.data.remote

import com.realeapp.feature.search.data.mapper.jsonName
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyFilter

object PropertyQueryBuilder {

    fun isClientSideMatch(property: Property, filter: PropertyFilter?): Boolean =
        rejectionReason(property, filter) == null

    /**
     * Returns a short description of the first filter criterion that rejects
     * [property], or null when the property matches. Used for diagnostics when
     * a filter yields zero results.
     */
    fun rejectionReason(property: Property, filter: PropertyFilter?): String? {
        filter ?: return null

        filter.normalizedCity?.let { city ->
            if (property.city.lowercase() != city) {
                return "city('${property.city}' != '$city')"
            }
        }

        if (filter.normalizedLocalities.isNotEmpty()) {
            if (property.locality !in filter.normalizedLocalities) {
                return "locality('${property.locality}' not in ${filter.normalizedLocalities})"
            }
        }

        filter.normalizedPincode?.let { pincode ->
            if (property.pincode != pincode) {
                return "pincode('${property.pincode}' != '$pincode')"
            }
        }

        if (filter.rentBuy != null && property.rentBuy != filter.rentBuy) {
            return "rentBuy(${property.rentBuy} != ${filter.rentBuy})"
        }
        if (filter.residentialCommercial != null && property.residentialCommercial != filter.residentialCommercial) {
            return "residentialCommercial(${property.residentialCommercial} != ${filter.residentialCommercial})"
        }
        if (filter.propertyType != null && property.propertyType != filter.propertyType) {
            return "propertyType(${property.propertyType} != ${filter.propertyType})"
        }
        if (filter.bedroomType != null && property.bedroomType != filter.bedroomType) {
            return "bedroomType(${property.bedroomType} != ${filter.bedroomType})"
        }
        if (filter.bathrooms != null && property.bathrooms != filter.bathrooms) {
            return "bathrooms(${property.bathrooms} != ${filter.bathrooms})"
        }
        if (filter.furnishing != null && property.furnishing != filter.furnishing) {
            return "furnishing(${property.furnishing} != ${filter.furnishing})"
        }
        if (filter.facing != null && property.facing != filter.facing) {
            return "facing(${property.facing} != ${filter.facing})"
        }
        if (filter.age != null && property.age != filter.age) {
            return "age(${property.age} != ${filter.age})"
        }

        filter.priceRange?.let { range ->
            if (property.price < range.min || property.price > range.max) {
                return "price(${property.price} outside ${range.min}..${range.max})"
            }
        }
        filter.carpetAreaRange?.let { range ->
            val value = property.carpetArea ?: return "carpetArea(null)"
            if (value < range.min || value > range.max) {
                return "carpetArea($value outside ${range.min}..${range.max})"
            }
        }
        filter.builtUpAreaRange?.let { range ->
            val value = property.builtUpArea ?: return "builtUpArea(null)"
            if (value < range.min || value > range.max) {
                return "builtUpArea($value outside ${range.min}..${range.max})"
            }
        }
        filter.superBuiltUpAreaRange?.let { range ->
            val value = property.superBuiltUpArea ?: return "superBuiltUpArea(null)"
            if (value < range.min || value > range.max) {
                return "superBuiltUpArea($value outside ${range.min}..${range.max})"
            }
        }

        if (filter.amenities.isNotEmpty()) {
            val propertyAmenities = property.amenities.map { it.jsonName() }.toSet()
            if (!filter.amenities.all { it.jsonName() in propertyAmenities }) {
                return "amenities(missing ${filter.amenities.map { it.jsonName() } - propertyAmenities})"
            }
        }

        return null
    }
}
