package com.realeapp.feature.add.data.remote

import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.search.data.mapper.PropertyMapper
import com.realeapp.feature.search.data.mapper.jsonName
import com.realeapp.feature.search.data.remote.AppWriteConstants
import com.realeapp.feature.search.data.remote.AppWriteProvider
import com.realeapp.feature.search.domain.model.LocationNormalizer
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
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

// Document field keys
private const val FIELD_ID = "id"
private const val FIELD_USER_ID = "userId"
private const val FIELD_TITLE = "title"
private const val FIELD_DESCRIPTION = "description"
private const val FIELD_PRICE = "price"
private const val FIELD_CITY = "city"
private const val FIELD_LOCALITY = "locality"
private const val FIELD_PINCODE = "pincode"
private const val FIELD_ADDRESS = "address"
private const val FIELD_LATITUDE = "latitude"
private const val FIELD_LONGITUDE = "longitude"
private const val FIELD_IMAGES = "images"
private const val FIELD_CREATED_AT = "createdAt"
private const val FIELD_STATUS = "status"
private const val FIELD_RENT_BUY = "rentBuy"
private const val FIELD_RESIDENTIAL_COMMERCIAL = "residentialCommercial"
private const val FIELD_PROPERTY_TYPE = "propertyType"
private const val FIELD_BEDROOM_TYPE = "bedroomType"
private const val FIELD_BATHROOMS = "bathrooms"
private const val FIELD_FURNISHING = "furnishing"
private const val FIELD_FACING = "facing"
private const val FIELD_AGE = "age"
private const val FIELD_AMENITIES = "amenities"
private const val FIELD_CARPET_AREA = "carpetArea"
private const val FIELD_BUILT_UP_AREA = "builtUpArea"
private const val FIELD_SUPER_BUILT_UP_AREA = "superBuiltUpArea"
private const val FIELD_AGENT_PHONE = "agentPhone"

// Values
private const val STATUS_LIVE = "live"
private const val TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
private const val TIMEZONE_UTC = "UTC"
private const val ORDER_CREATED_AT_DESC = "\$createdAt"

// Image mime types
private const val EXT_JPG = "jpg"
private const val EXT_JPEG = "jpeg"
private const val EXT_PNG = "png"
private const val EXT_GIF = "gif"
private const val EXT_WEBP = "webp"
private const val EXT_BMP = "bmp"
private const val MIME_JPEG = "image/jpeg"
private const val MIME_PNG = "image/png"
private const val MIME_GIF = "image/gif"
private const val MIME_WEBP = "image/webp"
private const val MIME_BMP = "image/bmp"

// Error messages
private const val ERROR_APPWRITE = "Appwrite error"
private const val ERROR_UNEXPECTED_PREFIX = "Unexpected error: "
private const val ERROR_IMAGE_UPLOAD = "Image upload failed"

// Storage view URL path segments
private const val URL_STORAGE_BUCKETS = "/storage/buckets/"
private const val URL_FILES = "/files/"
private const val URL_VIEW_PROJECT = "/view?project="

class AddPropertyRemoteDataSourceImpl(
    private val appWriteProvider: AppWriteProvider
) : AddPropertyRemoteDataSource {

    private val databases = appWriteProvider.databases
    private val storage = appWriteProvider.storage

    override suspend fun addProperty(userId: String, form: PropertyForm): Result<String> {
        Logger.d(TAG, "$ARROW addProperty() called for user: $userId, title: ${form.title}")
        return try {
            val documentId = ID.unique()
            val data = mutableMapOf<String, Any?>(
                FIELD_ID to documentId,
                FIELD_USER_ID to userId,
                FIELD_TITLE to form.title.trim(),
                FIELD_DESCRIPTION to form.description.trim(),
                FIELD_PRICE to form.price.toDoubleOrNull(),
                FIELD_CITY to LocationNormalizer.normalizeCity(form.city),
                FIELD_LOCALITY to LocationNormalizer.normalizeLocality(form.locality),
                FIELD_PINCODE to LocationNormalizer.normalizePincode(form.pincode),
                FIELD_ADDRESS to form.address.trim().ifBlank { null },
                FIELD_LATITUDE to form.latitude.toDoubleOrNull(),
                FIELD_LONGITUDE to form.longitude.toDoubleOrNull(),
                FIELD_IMAGES to form.images.map { it.trim() }.filter { it.isNotBlank() },
                FIELD_CREATED_AT to currentTimestamp(),
                FIELD_STATUS to STATUS_LIVE,
                FIELD_RENT_BUY to form.rentBuy?.jsonName(),
                FIELD_RESIDENTIAL_COMMERCIAL to form.residentialCommercial?.jsonName(),
                FIELD_PROPERTY_TYPE to form.propertyType?.jsonName(),
                FIELD_BEDROOM_TYPE to form.bedroomType?.jsonName(),
                FIELD_BATHROOMS to form.bathrooms,
                FIELD_FURNISHING to form.furnishing?.jsonName(),
                FIELD_FACING to form.facing?.jsonName(),
                FIELD_AGE to form.age?.jsonName(),
                FIELD_AMENITIES to JSONArray(form.amenities.map { it.jsonName() }).toString(),
                FIELD_CARPET_AREA to form.carpetArea.toDoubleOrNull(),
                FIELD_BUILT_UP_AREA to form.builtUpArea.toDoubleOrNull(),
                FIELD_SUPER_BUILT_UP_AREA to form.superBuiltUpArea.toDoubleOrNull(),
                FIELD_AGENT_PHONE to form.agentPhone.trim()
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
            Result.Error(e.message ?: ERROR_APPWRITE)
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS addProperty() unexpected error: ${e.message}")
            Result.Error(ERROR_UNEXPECTED_PREFIX + e.message)
        }
    }

    override suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String> {
        Logger.d(TAG, "$ARROW uploadImage() called: filename=$filename, size=${bytes.size}")
        return try {
            val mimeType = when (filename.substringAfterLast('.', "").lowercase()) {
                EXT_JPG, EXT_JPEG -> MIME_JPEG
                EXT_PNG -> MIME_PNG
                EXT_GIF -> MIME_GIF
                EXT_WEBP -> MIME_WEBP
                EXT_BMP -> MIME_BMP
                else -> MIME_JPEG
            }
            val inputFile = InputFile.fromBytes(bytes, filename, mimeType)
            val result = storage.createFile(
                bucketId = AppWriteConstants.STORAGE_BUCKET_ID,
                fileId = ID.unique(),
                file = inputFile
            )

            val imageUrl = buildString {
                append(AppWriteConstants.ENDPOINT)
                append(URL_STORAGE_BUCKETS)
                append(AppWriteConstants.STORAGE_BUCKET_ID)
                append(URL_FILES)
                append(result.id)
                append(URL_VIEW_PROJECT)
                append(AppWriteConstants.PROJECT_ID)
            }

            Logger.d(TAG, "$TICK uploadImage() succeeded: $imageUrl")
            Result.Success(imageUrl)
        } catch (e: AppwriteException) {
            Logger.e(TAG, "$CROSS uploadImage() failed for $filename: ${e.message}")
            Result.Error(e.message ?: ERROR_IMAGE_UPLOAD)
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS uploadImage() unexpected error for $filename: ${e.message}")
            Result.Error(ERROR_UNEXPECTED_PREFIX + e.message)
        }
    }

    override suspend fun getMyProperties(userId: String): Result<List<Property>> {
        Logger.d(TAG, "$ARROW getMyProperties() called for user: $userId")
        return try {
            val response = databases.listDocuments(
                databaseId = AppWriteConstants.DATABASE_ID,
                collectionId = AppWriteConstants.PROPERTY_COLLECTION_ID,
                queries = listOf(
                    Query.equal(FIELD_USER_ID, listOf(userId)),
                    Query.orderDesc(ORDER_CREATED_AT_DESC)
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
            Result.Error(e.message ?: ERROR_APPWRITE)
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS getMyProperties() unexpected error: ${e.message}")
            Result.Error(ERROR_UNEXPECTED_PREFIX + e.message)
        }
    }
}

private fun currentTimestamp(): String {
    val sdf = SimpleDateFormat(TIMESTAMP_FORMAT, Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone(TIMEZONE_UTC)
    return sdf.format(Date())
}
