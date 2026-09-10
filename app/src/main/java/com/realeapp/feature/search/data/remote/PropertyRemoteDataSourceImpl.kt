package com.realeapp.feature.search.data.remote

import com.realeapp.core.firebase.FirebaseConstants
import com.realeapp.core.firebase.FirebaseProvider
import com.realeapp.feature.search.data.mapper.PropertyMapper
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.model.ListingCategory
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import kotlinx.coroutines.tasks.await

private const val TAG = "PropertyRemoteDataSource"
private const val BATCH_LIMIT = 1000L

class PropertyRemoteDataSourceImpl(
    private val userSession: UserSession,
    private val firebaseProvider: FirebaseProvider
) : PropertyRemoteDataSource {

    private val firestore = firebaseProvider.firestore
    private val properties = firestore.collection(FirebaseConstants.PROPERTIES_COLLECTION)

    override suspend fun getAllProperties(
        filter: PropertyFilter?,
        page: Int,
        limit: Int
    ): Result<List<Property>> {
        Logger.d(TAG, "getAllProperties: start page=$page, limit=$limit, filter=$filter")
        return try {
            val snapshot = properties
                .whereEqualTo("status", "live")
                .limit(BATCH_LIMIT)
                .get()
                .await()

            val all = snapshot.documents.map { doc ->
                val data = doc.data ?: emptyMap()
                PropertyMapper.fromMap(data, doc.id)
            }.sortedByDescending { it.createdAt }

            val filtered = all.filter { PropertyQueryBuilder.isClientSideMatch(it, filter) }
            Logger.d(TAG, "getAllProperties: received=${all.size}, matched=${filtered.size}")

            if (filter != null && filtered.size < all.size) {
                val rejections = all.asSequence()
                    .mapNotNull { PropertyQueryBuilder.rejectionReason(it, filter) }
                    .groupingBy { it }
                    .eachCount()
                Logger.d(TAG, "getAllProperties: rejection reasons=$rejections")
            }

            val start = page * limit
            if (start >= filtered.size) {
                return Result.Success(emptyList())
            }
            val end = (start + limit).coerceAtMost(filtered.size)
            val properties = filtered.subList(start, end)

            Logger.d(TAG, "getAllProperties: page=$page pageSize=${properties.size}")

            val userId = userSession.getUserId()
            Result.Success(if (userId.isNullOrEmpty()) properties else mergeLikes(properties, userId))
        } catch (e: Exception) {
            Logger.e(TAG, "getAllProperties: error=${e.message}", e)
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun getFeaturedProperties(limit: Int): Result<List<Property>> {
        return getByCategory(ListingCategory.FEATURED, limit)
    }

    override suspend fun getPromotionalProperties(limit: Int): Result<List<Property>> {
        return getByCategory(ListingCategory.PROMOTIONAL, limit)
    }

    private suspend fun getByCategory(category: ListingCategory, limit: Int): Result<List<Property>> {
        Logger.d(TAG, "getByCategory: start category=$category, limit=$limit")
        return try {
            val snapshot = properties
                .whereEqualTo("status", "live")
                .limit(BATCH_LIMIT)
                .get()
                .await()

            val all = snapshot.documents.map { doc ->
                val data = doc.data ?: emptyMap()
                PropertyMapper.fromMap(data, doc.id)
            }.sortedByDescending { it.createdAt }

            val filtered = all
                .filter { it.listingCategory == category }
                .take(limit)

            Logger.d(TAG, "getByCategory: category=$category received=${all.size}, filtered=${filtered.size}")
            val userId = userSession.getUserId()
            Result.Success(if (userId.isNullOrEmpty()) filtered else mergeLikes(filtered, userId))
        } catch (e: Exception) {
            Logger.e(TAG, "getByCategory: category=$category error=${e.message}", e)
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

        return try {
            if (isLiked) {
                firestore.collection(FirebaseConstants.LIKES_COLLECTION)
                    .add(mapOf("userId" to userId, "propertyId" to propertyId))
                    .await()
            } else {
                val existing = firestore.collection(FirebaseConstants.LIKES_COLLECTION)
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("propertyId", propertyId)
                    .limit(1)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()

                if (existing != null) {
                    existing.reference.delete().await()
                } else {
                    Logger.w("PropertyRemoteDataSource", "updateLikeStatus: no like found to remove")
                    return Result.Error("No like found to remove")
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e("PropertyRemoteDataSource", "updateLikeStatus: error ${e.message}", e)
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    private suspend fun mergeLikes(
        properties: List<Property>,
        userId: String
    ): List<Property> {
        return try {
            val likedSnapshot = firestore.collection(FirebaseConstants.LIKES_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val likedIds = likedSnapshot.documents
                .mapNotNull { it.getString("propertyId") }
                .toSet()

            properties.map { it.copy(isLiked = likedIds.contains(it.documentId ?: it.id)) }
        } catch (e: Exception) {
            properties
        }
    }
}
