package com.example.mytestapp.feature.add.data.remote

import com.example.mytestapp.feature.add.domain.model.PropertyForm
import com.example.mytestapp.feature.search.data.mapper.PropertyMapper
import com.example.mytestapp.feature.search.data.mapper.jsonName
import com.example.mytestapp.feature.search.data.remote.AppWriteConstants
import com.example.mytestapp.feature.search.data.remote.AppWriteProvider
import com.example.mytestapp.feature.search.domain.model.Property
import com.example.mytestapp.feature.search.domain.utils.Result
import com.example.mytestapp.util.Logger
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.models.InputFile
import io.appwrite.exceptions.AppwriteException
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val TAG = "AddPropertyRemoteDataSource"

private const val ARROW = "\u279C"
private const val TICK = "\u2705"
private const val CROSS = "\u274C"

class AddPropertyRemoteDataSourceImpl : AddPropertyRemoteDataSource {

    private val databases = AppWriteProvider.databases
    private val storage = AppWriteProvider.storage

    override suspend fun addProperty(userId: String, form: PropertyForm): Result<String> {
        Logger.d(TAG, "$ARROW addProperty() called for user: $userId, title: ${form.title}")
        return try {
            val documentId = ID.unique()
            val data = mutableMapOf<String, Any?>(
                "id" to documentId,
                "userId" to userId,
                "title" to form.title.trim(),
                "description" to form.description.trim(),
                "price" to form.price.toDoubleOrNull(),
                "city" to form.city.trim().lowercase(),
                "locality" to form.locality.trim().lowercase(),
                "pincode" to form.pincode.trim().ifBlank { null },
                "address" to form.address.trim().ifBlank { null },
                "latitude" to form.latitude.toDoubleOrNull(),
                "longitude" to form.longitude.toDoubleOrNull(),
                "images" to form.images.map { it.trim() }.filter { it.isNotBlank() },
                "createdAt" to currentTimestamp(),
                "status" to "live",
                "rentBuy" to form.rentBuy?.jsonName(),
                "residentialCommercial" to form.residentialCommercial?.jsonName(),
                "propertyType" to form.propertyType?.jsonName(),
                "bedroomType" to form.bedroomType?.jsonName(),
                "furnishing" to form.furnishing?.jsonName(),
                "facing" to form.facing?.jsonName(),
                "age" to form.age?.jsonName(),
                "amenities" to JSONArray(form.amenities.map { it.jsonName() }).toString(),
                "carpetArea" to form.carpetArea.toDoubleOrNull(),
                "builtUpArea" to form.builtUpArea.toDoubleOrNull(),
                "superBuiltUpArea" to form.superBuiltUpArea.toDoubleOrNull(),
                "agentPhone" to form.agentPhone.trim()
            )

            databases.createDocument(
                databaseId = AppWriteConstants.DATABASE_ID,
                collectionId = AppWriteConstants.PROPERTY_COLLECTION_ID,
                documentId = documentId,
                data = data
            )

            Logger.d(TAG, "$TICK addProperty() succeeded: documentId=$documentId for user: $userId")
            Result.Success(documentId)
        } catch (e: AppwriteException) {
            Logger.e(TAG, "$CROSS addProperty() failed: ${e.message}")
            Result.Error(e.message ?: "Appwrite error")
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS addProperty() unexpected error: ${e.message}")
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String> {
        Logger.d(TAG, "$ARROW uploadImage() called: filename=$filename, size=${bytes.size}")
        return try {
            val mimeType = when (filename.substringAfterLast('.', "").lowercase()) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                "gif" -> "image/gif"
                "webp" -> "image/webp"
                "bmp" -> "image/bmp"
                else -> "image/jpeg"
            }
            val inputFile = InputFile.fromBytes(bytes, filename, mimeType)
            val result = storage.createFile(
                bucketId = AppWriteConstants.STORAGE_BUCKET_ID,
                fileId = ID.unique(),
                file = inputFile
            )

            val imageUrl = buildString {
                append(AppWriteConstants.ENDPOINT)
                append("/storage/buckets/")
                append(AppWriteConstants.STORAGE_BUCKET_ID)
                append("/files/")
                append(result.id)
                append("/view?project=")
                append(AppWriteConstants.PROJECT_ID)
            }

            Logger.d(TAG, "$TICK uploadImage() succeeded: $imageUrl")
            Result.Success(imageUrl)
        } catch (e: AppwriteException) {
            Logger.e(TAG, "$CROSS uploadImage() failed for $filename: ${e.message}")
            Result.Error(e.message ?: "Image upload failed")
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS uploadImage() unexpected error for $filename: ${e.message}")
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun getMyProperties(userId: String): Result<List<Property>> {
        Logger.d(TAG, "$ARROW getMyProperties() called for user: $userId")
        return try {
            val response = databases.listDocuments(
                databaseId = AppWriteConstants.DATABASE_ID,
                collectionId = AppWriteConstants.PROPERTY_COLLECTION_ID,
                queries = listOf(
                    Query.equal("userId", listOf(userId)),
                    Query.orderDesc("\$createdAt")
                )
            )

            val properties = response.documents.map { doc ->
                @Suppress("UNCHECKED_CAST")
                val data = doc.data as? Map<String, Any?> ?: emptyMap()
                PropertyMapper.fromMap(data, doc.id)
            }

            Logger.d(TAG, "$TICK getMyProperties() succeeded: found ${properties.size} properties for user: $userId")
            Result.Success(properties)
        } catch (e: AppwriteException) {
            Logger.e(TAG, "$CROSS getMyProperties() failed: ${e.message}")
            Result.Error(e.message ?: "Appwrite error")
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS getMyProperties() unexpected error: ${e.message}")
            Result.Error("Unexpected error: ${e.message}")
        }
    }
}

private fun currentTimestamp(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date())
}
