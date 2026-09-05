package com.realeapp.feature.search.data.remote

import com.realeapp.feature.search.data.mapper.jsonName
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.model.PropertyType
import io.appwrite.Query

object PropertyQueryBuilder {

    fun build(
        filter: PropertyFilter?,
        page: Int,
        limit: Int,
        status: String = "live"
    ): List<String> {
        val queries = mutableListOf<String>()
        queries.add(Query.limit(limit))
        queries.add(Query.offset(page * limit))
        queries.add(Query.equal("status", listOf(status)))

        filter ?: return queries

        val locationQueries = buildLocationQueries(filter)
        if (locationQueries.size == 1) {
            queries.add(locationQueries.first())
        } else if (locationQueries.size > 1) {
            queries.add(Query.or(locationQueries))
        }

        queries.addAll(buildOtherQueries(filter))

        return queries
    }

    private fun buildLocationQueries(filter: PropertyFilter): List<String> {
        val conditions = mutableListOf<String>()
        filter.normalizedCity?.let {
            conditions.add(Query.equal("city", listOf(it)))
        }
        if (filter.normalizedLocalities.isNotEmpty()) {
            conditions.add(Query.contains("locality", filter.normalizedLocalities))
        }
        filter.normalizedPincode?.let {
            conditions.add(Query.equal("pincode", listOf(it)))
        }
        return conditions
    }

    private fun buildOtherQueries(filter: PropertyFilter): List<String> {
        val queries = mutableListOf<String>()

        filter.rentBuy?.let {
            queries.add(Query.equal("rentBuy", listOf(it.jsonName())))
        }
        filter.residentialCommercial?.let {
            queries.add(Query.equal("residentialCommercial", listOf(it.jsonName())))
        }
        filter.propertyType?.let { propertyType ->
            queries.add(Query.equal("propertyType", listOf(propertyType.jsonName())))
            if (supportsBedrooms(propertyType)) {
                filter.bedroomType?.let {
                    queries.add(Query.equal("bedroomType", listOf(it.jsonName())))
                }
            }
        }
        filter.furnishing?.let {
            queries.add(Query.equal("furnishing", listOf(it.jsonName())))
        }
        filter.facing?.let {
            queries.add(Query.equal("facing", listOf(it.jsonName())))
        }
        filter.age?.let {
            queries.add(Query.equal("age", listOf(it.jsonName())))
        }

        filter.priceRange?.let { range ->
            queries.add(Query.greaterThanEqual("price", range.min))
            queries.add(Query.lessThanEqual("price", range.max))
        }

        filter.carpetAreaRange?.let { range ->
            queries.add(Query.greaterThanEqual("carpetArea", range.min))
            queries.add(Query.lessThanEqual("carpetArea", range.max))
        }

        filter.builtUpAreaRange?.let { range ->
            queries.add(Query.greaterThanEqual("builtUpArea", range.min))
            queries.add(Query.lessThanEqual("builtUpArea", range.max))
        }

        filter.superBuiltUpAreaRange?.let { range ->
            queries.add(Query.greaterThanEqual("superBuiltUpArea", range.min))
            queries.add(Query.lessThanEqual("superBuiltUpArea", range.max))
        }

        filter.amenities.forEach { amenity ->
            queries.add(Query.search("amenities", amenity.jsonName()))
        }

        return queries
    }

    private fun supportsBedrooms(propertyType: PropertyType): Boolean {
        return propertyType in setOf(
            PropertyType.APARTMENT,
            PropertyType.VILLA,
           /* PropertyType.FARM_HOUSE,
            PropertyType.BUILDER_FLOOR,
            PropertyType.STUDIO_APARTMENT,
            PropertyType.SERVICE_APARTMENT,
            PropertyType.INDEPENDENT_HOUSE,
            PropertyType.PENTHOUSE,
            PropertyType.DUPLEX*/
        )
    }
}
