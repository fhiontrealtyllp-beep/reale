package com.realeapp.feature.search.data.remote

import com.realeapp.core.firebase.FirebaseConstants
import com.realeapp.core.firebase.FirebaseProvider
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
