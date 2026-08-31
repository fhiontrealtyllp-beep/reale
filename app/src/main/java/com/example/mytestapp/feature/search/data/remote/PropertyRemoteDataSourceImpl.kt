package com.example.mytestapp.feature.search.data.remote

import com.example.mytestapp.feature.search.data.mapper.PropertyMapper
import com.example.mytestapp.feature.search.data.mapper.jsonName
import com.example.mytestapp.feature.search.data.session.UserSession
import com.example.mytestapp.feature.search.domain.model.Property
import com.example.mytestapp.feature.search.domain.model.PropertyFilter
import com.example.mytestapp.feature.search.domain.model.PropertyType
import com.example.mytestapp.feature.search.domain.utils.Result
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.exceptions.AppwriteException

class PropertyRemoteDataSourceImpl(
    private val userSession: UserSession
) : PropertyRemoteDataSource {

    private val databases = AppWriteProvider.databases

    override suspend fun getAllProperties(
        filter: PropertyFilter?,
        page: Int,
        limit: Int
    ): Result<List<Property>> {
        return try {
            val queries = buildQueries(filter, page, limit)

            val response = databases.listDocuments(
                databaseId = AppWriteConstants.DATABASE_ID,
                collectionId = AppWriteConstants.PROPERTY_COLLECTION_ID,
                queries = queries
            )

            val documents = response.documents
            val properties = documents.map { doc ->
                @Suppress("UNCHECKED_CAST")
                val data = doc.data as? Map<String, Any?> ?: emptyMap()
                PropertyMapper.fromMap(data, doc.id)
            }

            val userId = userSession.getUserId()
            if (userId.isNullOrEmpty()) {
                Result.Success(properties)
            } else {
                Result.Success(mergeLikes(properties, userId))
            }
        } catch (e: AppwriteException) {
            Result.Error(e.message ?: "Appwrite error")
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun updateLikeStatus(
        propertyId: String,
        isLiked: Boolean
    ): Result<Unit> {
        val userId = userSession.getUserId()
        if (userId.isNullOrEmpty()) {
            return Result.Error("User not logged in")
        }

        return try {
            if (isLiked) {
                databases.createDocument(
                    databaseId = AppWriteConstants.DATABASE_ID,
                    collectionId = AppWriteConstants.LIKES_COLLECTION_ID,
                    documentId = ID.unique(),
                    data = mapOf(
                        "userId" to userId,
                        "propertyId" to propertyId
                    )
                )
            } else {
                val response = databases.listDocuments(
                    databaseId = AppWriteConstants.DATABASE_ID,
                    collectionId = AppWriteConstants.LIKES_COLLECTION_ID,
                    queries = listOf(
                        Query.equal("userId", listOf(userId)),
                        Query.equal("propertyId", listOf(propertyId))
                    )
                )
                val docId = response.documents.firstOrNull()?.id
                if (docId != null) {
                    databases.deleteDocument(
                        databaseId = AppWriteConstants.DATABASE_ID,
                        collectionId = AppWriteConstants.LIKES_COLLECTION_ID,
                        documentId = docId
                    )
                } else {
                    return Result.Error("No like found to remove")
                }
            }
            Result.Success(Unit)
        } catch (e: AppwriteException) {
            Result.Error(e.message ?: "Appwrite error")
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    private suspend fun mergeLikes(
        properties: List<Property>,
        userId: String
    ): List<Property> {
        return try {
            val likedResponse = databases.listDocuments(
                databaseId = AppWriteConstants.DATABASE_ID,
                collectionId = AppWriteConstants.LIKES_COLLECTION_ID,
                queries = listOf(
                    Query.equal("userId", listOf(userId)),
                    Query.limit(100)
                )
            )
            val likedIds = likedResponse.documents
                .mapNotNull { it.data["propertyId"] as? String }
                .toSet()
            properties.map { it.copy(isLiked = likedIds.contains(it.documentId ?: it.id)) }
        } catch (e: Exception) {
            properties
        }
    }

    private fun buildQueries(filter: PropertyFilter?, page: Int, limit: Int): MutableList<String> {
        val queries = mutableListOf<String>()
        queries.add(Query.limit(limit))
        queries.add(Query.offset(page * limit))
        queries.add(Query.equal("status", listOf("live")))

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
        filter.city?.takeIf { it.isNotBlank() }?.let {
            conditions.add(Query.equal("city", listOf(it.lowercase())))
        }
        filter.localities.takeIf { it.isNotEmpty() }?.let {
            conditions.add(Query.contains("locality", it))
        }
        filter.pincode?.takeIf { it.isNotBlank() }?.let {
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
            range.min.let { queries.add(Query.greaterThanEqual("price", it)) }
            range.max.let { queries.add(Query.lessThanEqual("price", it)) }
        }

        filter.carpetAreaRange?.let { range ->
            range.min.let { queries.add(Query.greaterThanEqual("carpetArea", it)) }
            range.max.let { queries.add(Query.lessThanEqual("carpetArea", it)) }
        }

        filter.builtUpAreaRange?.let { range ->
            range.min.let { queries.add(Query.greaterThanEqual("builtUpArea", it)) }
            range.max.let { queries.add(Query.lessThanEqual("builtUpArea", it)) }
        }

        filter.superBuiltUpAreaRange?.let { range ->
            range.min.let { queries.add(Query.greaterThanEqual("superBuiltUpArea", it)) }
            range.max.let { queries.add(Query.lessThanEqual("superBuiltUpArea", it)) }
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
            PropertyType.FARM_HOUSE,
            PropertyType.BUILDER_FLOOR,
            PropertyType.STUDIO_APARTMENT,
            PropertyType.SERVICE_APARTMENT,
            PropertyType.INDEPENDENT_HOUSE,
            PropertyType.PENTHOUSE,
            PropertyType.DUPLEX
        )
    }
}
