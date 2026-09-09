package com.realeapp.feature.saved.data.remote

import com.realeapp.core.firebase.FirebaseConstants
import com.realeapp.core.firebase.FirebaseProvider
import com.realeapp.feature.search.data.mapper.PropertyMapper
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result
import kotlinx.coroutines.tasks.await

class SavedRemoteDataSourceImpl(
    private val firebaseProvider: FirebaseProvider
) : SavedRemoteDataSource {

    private val firestore = firebaseProvider.firestore
    private val likes = firestore.collection(FirebaseConstants.LIKES_COLLECTION)
    private val properties = firestore.collection(FirebaseConstants.PROPERTIES_COLLECTION)

    override suspend fun getLikedProperties(userId: String): Result<List<Property>> {
        return try {
            val likesSnapshot = likes
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val likedIds = likesSnapshot.documents.mapNotNull { it.getString("propertyId") }.distinct()

            if (likedIds.isEmpty()) {
                return Result.Success(emptyList())
            }

            val propertyChunks = likedIds.chunked(10)
            val allProperties = mutableListOf<Property>()

            propertyChunks.forEach { chunk ->
                val snapshot = properties
                    .whereIn("id", chunk)
                    .get()
                    .await()

                val chunkProperties = snapshot.documents.map { doc ->
                    val data = doc.data ?: emptyMap()
                    PropertyMapper.fromMap(data, doc.id)
                }
                allProperties.addAll(chunkProperties)
            }

            Result.Success(allProperties.map { it.copy(isLiked = true) })
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}")
        }
    }
}
