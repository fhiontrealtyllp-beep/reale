package com.realeapp.feature.search.data.mapper

import com.realeapp.feature.search.domain.model.Age
import com.realeapp.feature.search.domain.model.Amenity
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Facing
import com.realeapp.feature.search.domain.model.Furnishing
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.domain.model.ResidentialCommercial

fun Enum<*>.jsonName(): String = when (this) {
    is RentBuy -> when (this) {
        RentBuy.RENT -> "RENT"
        RentBuy.BUY -> "BUY"
    }
    is Furnishing -> when (this) {
        Furnishing.FULLY_FURNISHED -> "fullyFurnished"
        Furnishing.SEMI_FURNISHED -> "semiFurnished"
        Furnishing.UNFURNISHED -> "Unfurnished"
    }
    is Age -> when (this) {
        Age.ONE_TO_FIVE -> "oneToFive"
        Age.FIVE_TO_TEN -> "fiveToTen"
        Age.TEN_PLUS -> "TenPlus"
    }
    is BedroomType -> when (this) {
        BedroomType.SIX_PLUS_BHK -> "sixPLusBhk"
        else -> name.toCamelCaseFromUnderscore()
    }
    is PropertyType -> when (this) {
        PropertyType.PLOT -> "plot"
        PropertyType.LAND -> "land"
        /*PropertyType.COMMERCIAL_PLOT -> "commercialPlot"
        PropertyType.COMMERCIAL_LAND -> "commercialLand"
        PropertyType.PENTHOUSE -> "penthouse"
        PropertyType.HOSTEL -> "hostel"
        PropertyType.SHOP -> "shop"
        PropertyType.WAREHOUSE -> "warehouse"
        PropertyType.SHOWROOM -> "showroom"
        PropertyType.GODOWN -> "godown"
        PropertyType.KIOSK -> "kiosk"
        PropertyType.CLINIC -> "clinic"
        PropertyType.SCHOOL -> "school"
        PropertyType.HOTEL -> "hotel"*/
        else -> name.toCamelCaseFromUnderscore()
    }
    is Amenity -> when (this) {
        Amenity.PARKING -> "parking"
        Amenity.LIFT -> "lift"
        Amenity.POWER_BACKUP -> "powerBackup"
        Amenity.SWIMMING_POOL -> "swimmingPool"
        Amenity.CLUB_HOUSE -> "clubHouse"
        Amenity.GAS_PIPELINE -> "gasPipeline"
        Amenity.GYMNASIUM -> "gymnasium"
        Amenity.PLAYGROUND -> "playground"
        Amenity.EV_CHARGING_POINTS -> "evChargingPoints"
        Amenity.CCTV -> "cctv"
        Amenity.GATED_COMMUNITY -> "gatedCommunity"
        Amenity.AUTOMATED_PARKING_SYSTEMS -> "automatedParkingSystems"
        Amenity.GARDEN -> "garden"
        Amenity.SECURITY -> "security"
        Amenity.PET_FRIENDLY -> "petFriendly"
        Amenity.MODULAR_KITCHEN -> "modularKitchen"
        Amenity.SCHOOLS_HOSPITALS -> "schoolsHospitals"
    }
    is Facing -> name.lowercase()
    is ResidentialCommercial -> name.lowercase()
    else -> name.toCamelCaseFromUnderscore()
}

inline fun <reified T : Enum<T>> parseEnumFromJsonName(name: String?): T? {
    if (name == null) return null
    return enumValues<T>().find { it.jsonName() == name }
}

fun String.toCamelCaseFromUnderscore(): String {
    val parts = split("_").filter { it.isNotEmpty() }
    if (parts.isEmpty()) return this
    return buildString {
        parts.forEachIndexed { index, part ->
            if (index == 0) {
                append(part.lowercase())
            } else {
                append(part[0].uppercase())
                append(part.drop(1).lowercase())
            }
        }
    }
}
