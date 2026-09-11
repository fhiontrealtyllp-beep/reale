package com.realeapp.feature.add.data.mapper

import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.add.presentation.AddStrings
import com.realeapp.feature.search.domain.model.LocationNormalizer
import com.realeapp.feature.search.domain.model.Property
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun PropertyForm.toProperty(documentId: String, userId: String): Property {
    return Property(
        id = documentId,
        documentId = documentId,
        userId = userId,
        title = title.trim(),
        description = description.trim(),
        price = price.toDoubleOrNull() ?: 0.0,
        city = LocationNormalizer.normalizeCity(city).orEmpty(),
        locality = LocationNormalizer.normalizeLocality(locality).orEmpty(),
        pincode = LocationNormalizer.normalizePincode(pincode),
        address = address.trim().ifBlank { null },
        latitude = latitude.toDoubleOrNull(),
        longitude = longitude.toDoubleOrNull(),
        images = images.map { it.trim() }.filter { it.isNotBlank() },
        agentPhone = agentPhone.trim(),
        status = AddStrings.STATUS_LIVE,
        listingCategory = listingCategory,
        createdAt = currentTimestamp(),
        rentBuy = rentBuy,
        residentialCommercial = residentialCommercial,
        propertyType = propertyType,
        bedroomType = bedroomType,
        bathrooms = bathrooms,
        furnishing = furnishing,
        facing = facing,
        age = age,
        amenities = amenities,
        nearbyPlaces = nearbyPlaces,
        carpetArea = carpetArea.toDoubleOrNull(),
        builtUpArea = builtUpArea.toDoubleOrNull(),
        superBuiltUpArea = superBuiltUpArea.toDoubleOrNull()
    )
}

private fun currentTimestamp(): String {
    val sdf = SimpleDateFormat(AddStrings.TIMESTAMP_FORMAT, Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone(AddStrings.TIMEZONE_UTC)
    return sdf.format(Date())
}
