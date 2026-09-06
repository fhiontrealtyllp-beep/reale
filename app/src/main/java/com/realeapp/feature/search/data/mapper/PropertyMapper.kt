package com.realeapp.feature.search.data.mapper

import com.realeapp.feature.search.domain.model.Age
import com.realeapp.feature.search.domain.model.Amenity
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Facing
import com.realeapp.feature.search.domain.model.Furnishing
import com.realeapp.feature.search.domain.model.NearbyPlace
import com.realeapp.feature.search.domain.model.NearbyPlaceType
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.domain.model.ResidentialCommercial
import org.json.JSONArray

object PropertyMapper {

    fun fromMap(map: Map<String, Any?>, documentId: String? = null): Property {
        return Property(
            id = getString(map, "id") ?: documentId.orEmpty(),
            documentId = documentId ?: getString(map, "\$id"),
            userId = getString(map, "userId").orEmpty(),
            title = getString(map, "title").orEmpty(),
            description = getString(map, "description").orEmpty(),
            price = getDouble(map, "price") ?: 0.0,
            city = getString(map, "city").orEmpty(),
            locality = getString(map, "locality").orEmpty(),
            pincode = getString(map, "pincode"),
            address = getString(map, "address"),
            latitude = getDouble(map, "latitude"),
            longitude = getDouble(map, "longitude"),
            images = parseImages(map["images"]),
            isLiked = null,
            rating = getDouble(map, "rating"),
            agentPhone = getString(map, "agentPhone").orEmpty(),
            status = getString(map, "status"),
            createdAt = getString(map, "createdAt") ?: getString(map, "\$createdAt"),
            rentBuy = parseEnumFromJsonName<RentBuy>(getString(map, "rentBuy")),
            residentialCommercial = parseEnumFromJsonName<ResidentialCommercial>(getString(map, "residentialCommercial")),
            propertyType = parseEnumFromJsonName<PropertyType>(getString(map, "propertyType")),
            bedroomType = parseEnumFromJsonName<BedroomType>(getString(map, "bedroomType")),
            bathrooms = getInt(map, "bathrooms"),
            furnishing = parseEnumFromJsonName<Furnishing>(getString(map, "furnishing")),
            facing = parseEnumFromJsonName<Facing>(getString(map, "facing")),
            age = parseEnumFromJsonName<Age>(getString(map, "age")),
            amenities = parseAmenities(map["amenities"]),
            nearbyPlaces = parseNearbyPlaces(map["nearbyPlaces"]),
            carpetArea = getDouble(map, "carpetArea"),
            builtUpArea = getDouble(map, "builtUpArea"),
            superBuiltUpArea = getDouble(map, "superBuiltUpArea")
        )
    }

    private fun getString(map: Map<String, Any?>, key: String): String? {
        return map[key]?.toString()?.takeIf { it.isNotBlank() }
    }

    private fun getDouble(map: Map<String, Any?>, key: String): Double? {
        return when (val value = map[key]) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }
    }

    private fun getInt(map: Map<String, Any?>, key: String): Int? {
        return when (val value = map[key]) {
            is Number -> value.toInt()
            is String -> value.toIntOrNull()
            else -> null
        }
    }

    private fun getStringList(value: Any?): List<String> {
        return when (value) {
            is String -> {
                try {
                    val json = JSONArray(value)
                    List(json.length()) { json.optString(it, "") }
                } catch (e: Exception) {
                    if (value.isNotBlank()) listOf(value) else emptyList()
                }
            }
            is List<*> -> value.filterIsInstance<String>()
            else -> emptyList()
        }
    }

    private fun parseImages(value: Any?): List<String> {
        return getStringList(value).filter { it.isNotBlank() }
    }

    private fun parseAmenities(value: Any?): List<Amenity> {
        return getStringList(value).mapNotNull { parseEnumFromJsonName<Amenity>(it) }
    }

    private fun parseNearbyPlaces(value: Any?): List<NearbyPlace> {
        return when (value) {
            is List<*> -> value.mapNotNull { item ->
                (item as? Map<*, *>)?.let { map ->
                    val name = map["name"]?.toString()?.takeIf { it.isNotBlank() } ?: return@let null
                    val distance = map["distanceKm"]?.let { getDouble(mapOf("v" to it), "v") } ?: 0.0
                    val type = parseNearbyPlaceType(map["type"]?.toString())
                    NearbyPlace(name = name, distanceKm = distance, type = type)
                }
            }
            is String -> {
                try {
                    val json = JSONArray(value)
                    List(json.length()) { index ->
                        val obj = json.optJSONObject(index)
                        val name = obj?.optString("name").orEmpty().takeIf { it.isNotBlank() }
                        if (name == null) {
                            null
                        } else {
                            NearbyPlace(
                                name = name,
                                distanceKm = obj?.optDouble("distanceKm", 0.0) ?: 0.0,
                                type = parseNearbyPlaceType(obj?.optString("type"))
                            )
                        }
                    }.filterNotNull()
                } catch (e: Exception) {
                    emptyList()
                }
            }
            else -> emptyList()
        }
    }

    private fun parseNearbyPlaceType(name: String?): NearbyPlaceType {
        return try {
            NearbyPlaceType.valueOf(name.orEmpty().trim().uppercase())
        } catch (_: IllegalArgumentException) {
            NearbyPlaceType.OTHER
        }
    }
}
