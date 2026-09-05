package com.realeapp.feature.search.presentation.components

import com.realeapp.feature.search.domain.model.CarpetAreaRange
import com.realeapp.feature.search.domain.model.PriceRange
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy

data class FilterDialogState(
    val city: String = "",
    val locality: String = "",
    val pincode: String = "",
    val rentBuy: RentBuy? = null,
    val propertyType: PropertyType? = null,
    val priceMin: String = "",
    val priceMax: String = "",
    val carpetMin: String = "",
    val carpetMax: String = "",
    val builtUpMin: String = "",
    val builtUpMax: String = "",
    val superBuiltUpMin: String = "",
    val superBuiltUpMax: String = ""
)

fun PropertyFilter?.toFilterDialogState(): FilterDialogState = FilterDialogState(
    city = this?.city ?: "",
    locality = this?.localities?.joinToString(", ") ?: "",
    pincode = this?.pincode ?: "",
    rentBuy = this?.rentBuy,
    propertyType = this?.propertyType,
    priceMin = this?.priceRange?.min?.toInt()?.toString() ?: "",
    priceMax = this?.priceRange?.max?.toInt()?.toString() ?: "",
    carpetMin = this?.carpetAreaRange?.min?.toInt()?.toString() ?: "",
    carpetMax = this?.carpetAreaRange?.max?.toInt()?.toString() ?: "",
    builtUpMin = this?.builtUpAreaRange?.min?.toInt()?.toString() ?: "",
    builtUpMax = this?.builtUpAreaRange?.max?.toInt()?.toString() ?: "",
    superBuiltUpMin = this?.superBuiltUpAreaRange?.min?.toInt()?.toString() ?: "",
    superBuiltUpMax = this?.superBuiltUpAreaRange?.max?.toInt()?.toString() ?: ""
)

fun FilterDialogState.toPropertyFilter(): PropertyFilter {
    val localitiesList = locality.split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }

    return PropertyFilter(
        city = city.takeIf { it.isNotBlank() },
        localities = localitiesList,
        pincode = pincode.takeIf { it.isNotBlank() },
        rentBuy = rentBuy,
        propertyType = propertyType,
        priceRange = parseDoubleRange(priceMin, priceMax)?.let { PriceRange(it.first, it.second) },
        carpetAreaRange = parseDoubleRange(carpetMin, carpetMax)?.let { CarpetAreaRange(it.first, it.second) },
        builtUpAreaRange = parseDoubleRange(builtUpMin, builtUpMax)?.let { CarpetAreaRange(it.first, it.second) },
        superBuiltUpAreaRange = parseDoubleRange(superBuiltUpMin, superBuiltUpMax)?.let { CarpetAreaRange(it.first, it.second) }
    )
}

private fun parseDoubleRange(minText: String, maxText: String): Pair<Double, Double>? {
    val min = minText.toDoubleOrNull()
    val max = maxText.toDoubleOrNull()
    return if (min != null && max != null && min <= max) min to max else null
}
