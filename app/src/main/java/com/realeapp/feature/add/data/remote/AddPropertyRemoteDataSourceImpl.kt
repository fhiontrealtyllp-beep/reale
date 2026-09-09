package com.realeapp.feature.add.data.remote

import com.realeapp.core.firebase.FirebaseConstants
import com.realeapp.core.firebase.FirebaseProvider
import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.search.data.mapper.PropertyMapper
import com.realeapp.feature.search.data.mapper.jsonName
import com.realeapp.feature.search.domain.model.LocationNormalizer
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import kotlinx.coroutines.tasks.await
import org.json.JSONArray

private const val TAG = "AddPropertyRemoteDataSource"
private const val ARROW = "\u279C"
private const val TICK = "\u2705"
private const val CROSS = "\u274C"
private const val TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
private const val TIMEZONE_UTC = "UTC"
private const val STATUS_LIVE = "live"

class AddPropertyRemoteDataSourceImpl(
    private val firebaseProvider: FirebaseProvider
) : AddPropertyRemoteDataSource {

    private val firestore = firebaseProvider.firestore
    private val storage = firebaseProvider.storage
    private val auth = firebaseProvider.auth
    private val properties = firestore.collection(FirebaseConstants.PROPERTIES_COLLECTION)

    override suspend fun addProperty(userId: String, form: PropertyForm): Result<String> {
        Logger.d(TAG, "$ARROW addProperty() called for user: $userId, title: ${form.title}")
        return try {
            val docRef = properties.document()
            val documentId = docRef.id

            val data = hashMapOf<String, Any?>(
                "id" to documentId,
                "userId" to userId,
                "title" to form.title.trim(),
                "description" to form.description.trim(),
                "price" to form.price.toDoubleOrNull(),
                "city" to LocationNormalizer.normalizeCity(form.city),
                "locality" to LocationNormalizer.normalizeLocality(form.locality),
                "pincode" to LocationNormalizer.normalizePincode(form.pincode),
                "address" to form.address.trim().ifBlank { null },
                "latitude" to form.latitude.toDoubleOrNull(),
                "longitude" to form.longitude.toDoubleOrNull(),
                "images" to form.images.map { it.trim() }.filter { it.isNotBlank() },
                "createdAt" to currentTimestamp(),
                "status" to STATUS_LIVE,
                "listingCategory" to "NORMAL",
                "rentBuy" to form.rentBuy?.jsonName(),
                "residentialCommercial" to form.residentialCommercial?.jsonName(),
                "propertyType" to form.propertyType?.jsonName(),
                "bedroomType" to form.bedroomType?.jsonName(),
                "bathrooms" to form.bathrooms,
                "furnishing" to form.furnishing?.jsonName(),
                "facing" to form.facing?.jsonName(),
                "age" to form.age?.jsonName(),
                "amenities" to JSONArray(form.amenities.map { it.jsonName() }).toString(),
                "carpetArea" to form.carpetArea.toDoubleOrNull(),
                "builtUpArea" to form.builtUpArea.toDoubleOrNull(),
                "superBuiltUpArea" to form.superBuiltUpArea.toDoubleOrNull(),
                "agentPhone" to form.agentPhone.trim()
            )

            docRef.set(data).await()

            Logger.d(TAG, "$TICK addProperty() succeeded: documentId=$documentId for user: $userId")
            Result.Success(documentId)
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS addProperty() failed: ${e.message}")
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String> {
        Logger.d(TAG, "$ARROW uploadImage() called: filename=$filename, size=${bytes.size}")
        return try {
            val userId = auth.currentUser?.uid
            if (userId.isNullOrEmpty()) {
                Logger.e(TAG, "$CROSS uploadImage() failed: user is not signed in")
                return Result.Error("User must be signed in to upload images")
            }

            val ext = filename.substringAfterLast('.', "jpg").lowercase()
            val safeName = "${System.currentTimeMillis()}_${UUID.randomUUID()}.${ext}"
            val ref = storage.reference.child("${FirebaseConstants.PROPERTY_IMAGES_PATH}/$userId/$safeName")
            Logger.d(TAG, "$ARROW uploadImage() uploading to: ${ref.path}, bucket: ${ref.bucket}")

            val metadata = com.google.firebase.storage.StorageMetadata.Builder()
                .setContentType("image/${if (ext == "png") "png" else "jpeg"}")
                .build()

            val uploadTask = ref.putBytes(bytes, metadata)
            val taskSnapshot = uploadTask.await()
            Logger.d(TAG, "$TICK uploadImage() upload complete. bytesTransferred=${taskSnapshot.bytesTransferred}, total=${taskSnapshot.totalByteCount}")

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                ref.downloadUrl
            }.await()

            val url = urlTask.toString()
            Logger.d(TAG, "$TICK uploadImage() succeeded: $url")
            Result.Success(url)
        } catch (e: com.google.firebase.storage.StorageException) {
            val code = e.errorCode
            val http = (e.cause as? java.io.IOException)?.message ?: e.cause?.toString()
            Logger.e(TAG, "$CROSS uploadImage() failed for $filename: errorCode=$code, http=$http, message=${e.message}", e)
            Result.Error("Storage error ${e.message}")
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS uploadImage() failed for $filename: ${e.message}", e)
            Result.Error("Image upload failed: ${e.message}")
        }
    }

    override suspend fun getMyProperties(userId: String): Result<List<Property>> {
        Logger.d(TAG, "$ARROW getMyProperties() called for user: $userId")
        return try {
            val snapshot = properties
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val properties = snapshot.documents.map { doc ->
                val data = doc.data ?: emptyMap()
                PropertyMapper.fromMap(data, doc.id)
            }

            Logger.d(TAG, "$TICK getMyProperties() succeeded: found ${properties.size} properties for user: $userId")
            Result.Success(properties)
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS getMyProperties() failed: ${e.message}")
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    private fun currentTimestamp(): String {
        val sdf = SimpleDateFormat(TIMESTAMP_FORMAT, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(TIMEZONE_UTC)
        return sdf.format(Date())
    }
}
