package com.example.mytestapp.feature.search.data.mapper

import com.example.mytestapp.feature.search.domain.model.Age
import com.example.mytestapp.feature.search.domain.model.Amenity
import com.example.mytestapp.feature.search.domain.model.BedroomType
import com.example.mytestapp.feature.search.domain.model.Facing
import com.example.mytestapp.feature.search.domain.model.Furnishing
import com.example.mytestapp.feature.search.domain.model.Property
import com.example.mytestapp.feature.search.domain.model.PropertyType
import com.example.mytestapp.feature.search.domain.model.RentBuy
import com.example.mytestapp.feature.search.domain.model.ResidentialCommercial
import org.json.JSONArray

object PropertyMapper {

    fun fromMap(map: Map<String, Any?>, documentId: String? = null): Property {
        return Property(
            id = map["id"]?.toString() ?: documentId.orEmpty(),
            documentId = documentId ?: map["\$id"]?.toString(),
            userId = map["userId"]?.toString().orEmpty(),
            title = map["title"]?.toString().orEmpty(),
            description = map["description"]?.toString().orEmpty(),
            price = (map["price"] as? Number)?.toDouble() ?: 0.0,
            city = map["city"]?.toString().orEmpty(),
            locality = map["locality"]?.toString().orEmpty(),
            pincode = map["pincode"]?.toString(),
            address = map["address"]?.toString(),
            latitude = (map["latitude"] as? Number)?.toDouble(),
            longitude = (map["longitude"] as? Number)?.toDouble(),
            images = parseImages(map["images"]),
            isLiked = null,
            rating = (map["rating"] as? Number)?.toDouble(),
            agentPhone = map["agentPhone"]?.toString().orEmpty(),
            status = map["status"]?.toString(),
            createdAt = map["createdAt"]?.toString() ?: map["\$createdAt"]?.toString(),
            rentBuy = parseEnumFromJsonName<RentBuy>(map["rentBuy"] as? String),
            residentialCommercial = parseEnumFromJsonName<ResidentialCommercial>(map["residentialCommercial"] as? String),
            propertyType = parseEnumFromJsonName<PropertyType>(map["propertyType"] as? String),
            bedroomType = parseEnumFromJsonName<BedroomType>(map["bedroomType"] as? String),
            furnishing = parseEnumFromJsonName<Furnishing>(map["furnishing"] as? String),
            facing = parseEnumFromJsonName<Facing>(map["facing"] as? String),
            age = parseEnumFromJsonName<Age>(map["age"] as? String),
            amenities = parseAmenities(map["amenities"]),
            carpetArea = (map["carpetArea"] as? Number)?.toDouble(),
            builtUpArea = (map["builtUpArea"] as? Number)?.toDouble(),
            superBuiltUpArea = (map["superBuiltUpArea"] as? Number)?.toDouble()
        )
    }

    private fun parseImages(value: Any?): List<String> {
        return when (value) {
            is String -> {
                try {
                    val json = JSONArray(value)
                    List(json.length()) { json.getString(it) }
                } catch (e: Exception) {
                    if (value.isNotBlank()) listOf(value) else emptyList()
                }
            }
            is List<*> -> value.filterIsInstance<String>()
            else -> emptyList()
        }
    }

    private fun parseAmenities(value: Any?): List<Amenity> {
        val raw = when (value) {
            is String -> {
                try {
                    val json = JSONArray(value)
                    List(json.length()) { json.getString(it) }
                } catch (e: Exception) {
                    emptyList()
                }
            }
            is List<*> -> value.filterIsInstance<String>()
            else -> emptyList()
        }
        return raw.mapNotNull { parseEnumFromJsonName<Amenity>(it) }
    }
}
