package com.realeapp.feature.search.data.remote

import com.realeapp.core.firebase.FirebaseConstants
import com.realeapp.core.firebase.FirebaseProvider
import com.realeapp.feature.search.domain.model.Enquiry
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.tasks.await

private const val TAG = "EnquiryRemoteDataSource"
private const val TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
private const val TIMEZONE_UTC = "UTC"
private const val STATUS_NEW = "new"
private const val WHERE_IN_CHUNK_SIZE = 10

class EnquiryRemoteDataSourceImpl(
    private val firebaseProvider: FirebaseProvider
) : EnquiryRemoteDataSource {

    private val firestore = firebaseProvider.firestore
    private val enquiries = firestore.collection(FirebaseConstants.ENQUIRIES_COLLECTION)

    override suspend fun sendEnquiry(
        property: Property,
        message: String,
        userId: String?
    ): Result<Unit> {
        Logger.d(TAG, "sendEnquiry: propertyId=${property.id}, userId=$userId")
        return try {
            val docRef = enquiries.document()
            val data = hashMapOf<String, Any?>(
                "id" to docRef.id,
                "propertyId" to property.id,
                "propertyTitle" to property.title,
                "propertyLocation" to buildShortLocation(property),
                "propertyImage" to property.images.firstOrNull().orEmpty(),
                "agentPhone" to property.agentPhone,
                "message" to message.trim(),
                "userId" to userId,
                "status" to STATUS_NEW,
                "createdAt" to currentTimestamp()
            )

            docRef.set(data).await()
            Logger.d(TAG, "sendEnquiry succeeded: enquiryId=${docRef.id}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "sendEnquiry failed: ${e.message}", e)
            Result.Error("Unable to send enquiry: ${e.message}")
        }
    }

    override suspend fun getEnquiriesByUser(userId: String): Result<List<Enquiry>> {
        Logger.d(TAG, "getEnquiriesByUser: userId=$userId")
        return try {
            val snapshot = enquiries
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val list = snapshot.documents
                .mapNotNull { it.toEnquiry() }
                .sortedByDescending { it.createdAt }
            Logger.d(TAG, "getEnquiriesByUser: found ${list.size} enquiries")
            Result.Success(list)
        } catch (e: Exception) {
            Logger.e(TAG, "getEnquiriesByUser failed: ${e.message}", e)
            Result.Error("Unable to load enquiries: ${e.message}")
        }
    }

    override suspend fun getEnquiriesByProperty(propertyId: String): Result<List<Enquiry>> {
        Logger.d(TAG, "getEnquiriesByProperty: propertyId=$propertyId")
        return try {
            val snapshot = enquiries
                .whereEqualTo("propertyId", propertyId)
                .get()
                .await()

            val list = snapshot.documents
                .mapNotNull { it.toEnquiry() }
                .sortedByDescending { it.createdAt }
            Logger.d(TAG, "getEnquiriesByProperty: found ${list.size} enquiries")
            Result.Success(list)
        } catch (e: Exception) {
            Logger.e(TAG, "getEnquiriesByProperty failed: ${e.message}", e)
            Result.Error("Unable to load enquiries: ${e.message}")
        }
    }

    override suspend fun getEnquiryCountsForPropertyIds(propertyIds: List<String>): Result<Map<String, Int>> {
        Logger.d(TAG, "getEnquiryCountsForPropertyIds: propertyIds=${propertyIds.size}")
        if (propertyIds.isEmpty()) {
            return Result.Success(emptyMap())
        }
        return try {
            val counts = mutableMapOf<String, Int>()
            val query = enquiries
            propertyIds.chunked(WHERE_IN_CHUNK_SIZE)
                .forEach { chunk ->
                    val snapshot = query
                        .whereIn("propertyId", chunk)
                        .get()
                        .await()
                    snapshot.documents.forEach { doc ->
                        val id = doc.getString("propertyId") ?: return@forEach
                        counts[id] = counts.getOrDefault(id, 0) + 1
                    }
                }
            Logger.d(TAG, "getEnquiryCountsForPropertyIds: counts=$counts")
            Result.Success(counts)
        } catch (e: Exception) {
            Logger.e(TAG, "getEnquiryCountsForPropertyIds failed: ${e.message}", e)
            Result.Error("Unable to load enquiry counts: ${e.message}")
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toEnquiry(): Enquiry? {
        val data = data ?: return null
        return Enquiry(
            id = getString("id") ?: return null,
            propertyId = getString("propertyId") ?: return null,
            propertyTitle = getString("propertyTitle") ?: "",
            propertyLocation = getString("propertyLocation") ?: "",
            propertyImage = getString("propertyImage") ?: "",
            agentPhone = getString("agentPhone") ?: "",
            message = getString("message") ?: "",
            userId = getString("userId"),
            status = getString("status") ?: "",
            createdAt = getString("createdAt") ?: ""
        )
    }

    private fun buildShortLocation(property: Property): String {
        return listOf(property.locality, property.city)
            .filter { it.isNotBlank() }
            .joinToString(", ")
    }

    private fun currentTimestamp(): String {
        val sdf = SimpleDateFormat(TIMESTAMP_FORMAT, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(TIMEZONE_UTC)
        return sdf.format(Date())
    }
}
