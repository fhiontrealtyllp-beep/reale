package com.realeapp.feature.search.data.remote

import com.realeapp.feature.search.data.mapper.PropertyMapper
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.exceptions.AppwriteException

class PropertyRemoteDataSourceImpl(
    private val userSession: UserSession,
    private val appWriteProvider: AppWriteProvider
) : PropertyRemoteDataSource {

    private val databases = appWriteProvider.databases

    override suspend fun getAllProperties(
        filter: PropertyFilter?,
        page: Int,
        limit: Int
    ): Result<List<Property>> {
        Logger.d(TAG, "getAllProperties: start page=$page, limit=$limit, filter=$filter")
        return try {
            val queries = PropertyQueryBuilder.build(filter, page, limit)
            Logger.d(TAG, "getAllProperties: queries=$queries")

            val response = databases.listDocuments(
                databaseId = AppWriteConstants.DATABASE_ID,
                collectionId = AppWriteConstants.PROPERTY_COLLECTION_ID,
                queries = queries
            )

            val documents = response.documents
            Logger.d(TAG, "getAllProperties: received=${documents.size}, total=${response.total}")
            val properties = documents.map { doc ->
                @Suppress("UNCHECKED_CAST")
                val data = doc.data as? Map<String, Any?> ?: emptyMap()
                Logger.d(
                    TAG,
                    "getAllProperties: documentId=${doc.id}, title=${data[FIELD_TITLE]}, rawListingCategory=${data[FIELD_LISTING_CATEGORY]}"
                )
                PropertyMapper.fromMap(data, doc.id)
            }
            Logger.d(TAG, "getAllProperties: mappedCategories=${properties.groupingBy { it.listingCategory }.eachCount()}")

            val userId = userSession.getUserId()
            if (userId.isNullOrEmpty()) {
                Logger.d(TAG, "getAllProperties: returning without like merge; user not logged in")
                Result.Success(properties)
            } else {
                Logger.d(TAG, "getAllProperties: merging likes for userId=$userId")
                Result.Success(mergeLikes(properties, userId))
            }
        } catch (e: AppwriteException) {
            Logger.e(TAG, "getAllProperties: AppwriteException code=${e.code}, message=${e.message}", e)
            Result.Error(e.message ?: "Appwrite error")
        } catch (e: Exception) {
            Logger.e(TAG, "getAllProperties: unexpected error=${e.message}", e)
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun getFeaturedProperties(limit: Int): Result<List<Property>> {
        Logger.d(TAG, "getFeaturedProperties: start limit=$limit")
        return try {
            val queries = listOf(
                Query.equal(FIELD_STATUS, listOf(STATUS_LIVE)),
                Query.equal(FIELD_LISTING_CATEGORY, FEATURED_CATEGORY_VALUES),
                Query.limit(limit)
            )
            Logger.d(TAG, "getFeaturedProperties: queries=$queries")
            val response = databases.listDocuments(
                databaseId = AppWriteConstants.DATABASE_ID,
                collectionId = AppWriteConstants.PROPERTY_COLLECTION_ID,
                queries = queries
            )
            val properties = response.documents.map { document ->
                @Suppress("UNCHECKED_CAST")
                val data = document.data as? Map<String, Any?> ?: emptyMap()
                Logger.d(
                    TAG,
                    "getFeaturedProperties: documentId=${document.id}, title=${data[FIELD_TITLE]}, rawListingCategory=${data[FIELD_LISTING_CATEGORY]}"
                )
                PropertyMapper.fromMap(data, document.id)
            }
            Logger.d(TAG, "getFeaturedProperties: received=${properties.size}, total=${response.total}")
            val userId = userSession.getUserId()
            Result.Success(if (userId.isNullOrEmpty()) properties else mergeLikes(properties, userId))
        } catch (e: AppwriteException) {
            Logger.e(TAG, "getFeaturedProperties: AppwriteException code=${e.code}, message=${e.message}", e)
            Result.Error(e.message ?: "Appwrite error")
        } catch (e: Exception) {
            Logger.e(TAG, "getFeaturedProperties: unexpected error=${e.message}", e)
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun updateLikeStatus(
        propertyId: String,
        isLiked: Boolean
    ): Result<Unit> {
        Logger.d("PropertyRemoteDataSource", "updateLikeStatus: propertyId=$propertyId, isLiked=$isLiked")
        val userId = userSession.getUserId()
        if (userId.isNullOrEmpty()) {
            Logger.w("PropertyRemoteDataSource", "updateLikeStatus: user not logged in")
            return Result.Error("User not logged in")
        }
        Logger.d("PropertyRemoteDataSource", "updateLikeStatus: userId=$userId")

        return try {
            if (isLiked) {
                Logger.d("PropertyRemoteDataSource", "updateLikeStatus: creating like document")
                databases.createDocument(
                    databaseId = AppWriteConstants.DATABASE_ID,
                    collectionId = AppWriteConstants.LIKES_COLLECTION_ID,
                    documentId = ID.unique(),
                    data = mapOf(
                        "userId" to userId,
                        "propertyId" to propertyId
                    )
                )
                Logger.d("PropertyRemoteDataSource", "updateLikeStatus: like document created")
            } else {
                Logger.d("PropertyRemoteDataSource", "updateLikeStatus: querying like document to delete")
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
                    Logger.d("PropertyRemoteDataSource", "updateLikeStatus: deleting like document docId=$docId")
                    databases.deleteDocument(
                        databaseId = AppWriteConstants.DATABASE_ID,
                        collectionId = AppWriteConstants.LIKES_COLLECTION_ID,
                        documentId = docId
                    )
                    Logger.d("PropertyRemoteDataSource", "updateLikeStatus: like document deleted")
                } else {
                    Logger.w("PropertyRemoteDataSource", "updateLikeStatus: no like found to remove")
                    return Result.Error("No like found to remove")
                }
            }
            Result.Success(Unit)
        } catch (e: AppwriteException) {
            Logger.e("PropertyRemoteDataSource", "updateLikeStatus: AppwriteException ${e.message}", e)
            Result.Error(e.message ?: "Appwrite error")
        } catch (e: Exception) {
            Logger.e("PropertyRemoteDataSource", "updateLikeStatus: unexpected error ${e.message}", e)
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

    private companion object {
        const val TAG = "PropertyRemoteDataSource"
        const val FIELD_TITLE = "title"
        const val FIELD_STATUS = "status"
        const val FIELD_LISTING_CATEGORY = "listingCategory"
        const val STATUS_LIVE = "live"
        val FEATURED_CATEGORY_VALUES = listOf("featured", "FEATURED", "Featured")
    }

}
