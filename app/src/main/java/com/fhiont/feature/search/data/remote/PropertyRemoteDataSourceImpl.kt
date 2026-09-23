package com.fhiont.feature.search.data.remote

import com.fhiont.core.firebase.FirebaseConstants
import com.fhiont.core.firebase.FirebaseProvider
import com.fhiont.core.network.PhpPropertyApi
import com.fhiont.feature.search.data.mapper.PropertyMapper
import com.fhiont.feature.search.data.session.UserSession
import com.fhiont.feature.search.domain.model.ListingCategory
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.model.PropertyFilter
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.util.Logger
import kotlinx.coroutines.tasks.await

private const val TAG = "PropertyRemoteDataSource"
private const val BATCH_LIMIT = 1000L

class PropertyRemoteDataSourceImpl(
    private val userSession: UserSession,
    private val firebaseProvider: FirebaseProvider,
    private val phpPropertyApi: PhpPropertyApi
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
            val city = filter?.normalizedCity?.takeIf { it.isNotBlank() }
            var query: com.google.firebase.firestore.Query = properties
                .whereEqualTo("status", "live")
            if (city != null) {
                query = query.whereEqualTo("city", city)
            }
            val snapshot = query
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
        Logger.d(TAG, "getFeaturedProperties: limit=$limit")
        val token = userSession.getUser()?.sessionId
        return if (!token.isNullOrBlank()) {
            phpPropertyApi.getFeaturedProperties(token, limit)
        } else {
            getByCategoryFromFirebase(ListingCategory.FEATURED, limit)
        }
    }

    override suspend fun getPromotionalProperties(limit: Int): Result<List<Property>> {
        Logger.d(TAG, "getPromotionalProperties: limit=$limit")
        val token = userSession.getUser()?.sessionId
        return if (!token.isNullOrBlank()) {
            phpPropertyApi.getPromotionalProperties(token, limit)
        } else {
            getByCategoryFromFirebase(ListingCategory.PROMOTIONAL, limit)
        }
    }

    private suspend fun getByCategoryFromFirebase(category: ListingCategory, limit: Int): Result<List<Property>> {
        Logger.d(TAG, "getByCategoryFromFirebase: start category=$category, limit=$limit")
        return try {
            val snapshot = properties
                .whereEqualTo("status", "live")
                .whereEqualTo("listingCategory", category.name)
                .limit(limit.toLong())
                .get()
                .await()

            val all = snapshot.documents.map { doc ->
                val data = doc.data ?: emptyMap()
                PropertyMapper.fromMap(data, doc.id)
            }.sortedByDescending { it.createdAt }

            val filtered = all.take(limit)

            Logger.d(TAG, "getByCategoryFromFirebase: category=$category received=${all.size}, filtered=${filtered.size}")
            val userId = userSession.getUserId()
            Result.Success(if (userId.isNullOrEmpty()) filtered else mergeLikes(filtered, userId))
        } catch (e: Exception) {
            Logger.e(TAG, "getByCategoryFromFirebase: category=$category error=${e.message}", e)
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun updateLikeStatus(
        propertyId: String,
        isLiked: Boolean
    ): Result<Unit> {
        Logger.d("PropertyRemoteDataSource", "updateLikeStatus: propertyId=$propertyId, isLiked=$isLiked")
        val token = userSession.getUser()?.sessionId
        if (!token.isNullOrBlank()) {
            return phpPropertyApi.updateLike(token, propertyId, isLiked)
        }

        val userId = userSession.getUserId()
        if (userId.isNullOrEmpty()) {
            Logger.w("PropertyRemoteDataSource", "updateLikeStatus: user not logged in")
            return Result.Error("User not logged in")
        }

        return updateLikeStatusInFirebase(userId, propertyId, isLiked)
    }

    private suspend fun mergeLikes(
        properties: List<Property>,
        userId: String
    ): List<Property> {
        val token = userSession.getUser()?.sessionId
        return if (!token.isNullOrBlank()) {
            mergeLikesFromPhp(properties, token)
        } else {
            mergeLikesFromFirebase(properties, userId)
        }
    }

    private suspend fun updateLikeStatusInFirebase(
        userId: String,
        propertyId: String,
        isLiked: Boolean
    ): Result<Unit> {
        return try {
            if (isLiked) {
                firestore.collection(FirebaseConstants.LIKES_COLLECTION)
                    .add(mapOf("userId" to userId, "propertyId" to propertyId))
                    .await()
            } else {
                val existing = firestore.collection(FirebaseConstants.LIKES_COLLECTION)
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("propertyId", propertyId)
                    .limit(1L)
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

    private suspend fun mergeLikesFromPhp(
        properties: List<Property>,
        token: String
    ): List<Property> {
        return when (val result = phpPropertyApi.getLikedProperties(token)) {
            is Result.Success -> {
                val likedIds = result.data.map { it.documentId ?: it.id }.toSet()
                properties.map { it.copy(isLiked = likedIds.contains(it.documentId ?: it.id)) }
            }
            is Result.Error -> {
                Logger.w("PropertyRemoteDataSource", "mergeLikesFromPhp failed: ${result.message}")
                properties
            }
        }
    }

    private suspend fun mergeLikesFromFirebase(
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
