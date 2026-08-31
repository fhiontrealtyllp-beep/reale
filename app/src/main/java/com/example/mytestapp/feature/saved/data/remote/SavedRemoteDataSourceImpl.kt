package com.example.mytestapp.feature.saved.data.remote

import com.example.mytestapp.feature.search.data.mapper.PropertyMapper
import com.example.mytestapp.feature.search.data.remote.AppWriteConstants
import com.example.mytestapp.feature.search.data.remote.AppWriteProvider
import com.example.mytestapp.feature.search.data.session.UserSession
import com.example.mytestapp.feature.search.domain.model.Property
import com.example.mytestapp.feature.search.domain.utils.Result
import io.appwrite.Query
import io.appwrite.exceptions.AppwriteException

class SavedRemoteDataSourceImpl(
    private val userSession: UserSession
) : SavedRemoteDataSource {

    private val databases = AppWriteProvider.databases

    override suspend fun getLikedProperties(userId: String): Result<List<Property>> {
        return try {
            val likesResponse = databases.listDocuments(
                databaseId = AppWriteConstants.DATABASE_ID,
                collectionId = AppWriteConstants.LIKES_COLLECTION_ID,
                queries = listOf(Query.equal("userId", listOf(userId)))
            )

            val likedIds = likesResponse.documents.mapNotNull {
                it.data["propertyId"] as? String
            }

            if (likedIds.isEmpty()) {
                return Result.Success(emptyList())
            }

            var propertiesResponse = databases.listDocuments(
                databaseId = AppWriteConstants.DATABASE_ID,
                collectionId = AppWriteConstants.PROPERTY_COLLECTION_ID,
                queries = listOf(Query.equal("\$id", likedIds))
            )

            if (propertiesResponse.documents.isEmpty()) {
                propertiesResponse = databases.listDocuments(
                    databaseId = AppWriteConstants.DATABASE_ID,
                    collectionId = AppWriteConstants.PROPERTY_COLLECTION_ID,
                    queries = listOf(Query.equal("id", likedIds))
                )
            }

            val properties = propertiesResponse.documents.map { doc ->
                @Suppress("UNCHECKED_CAST")
                val data = doc.data as? Map<String, Any?> ?: emptyMap()
                PropertyMapper.fromMap(data, doc.id)
            }

            val currentUserId = userSession.getUserId()
            if (currentUserId == userId) {
                Result.Success(properties.map { it.copy(isLiked = true) })
            } else {
                Result.Success(properties)
            }
        } catch (e: AppwriteException) {
            Result.Error(e.message ?: "Appwrite error")
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}")
        }
    }
}
