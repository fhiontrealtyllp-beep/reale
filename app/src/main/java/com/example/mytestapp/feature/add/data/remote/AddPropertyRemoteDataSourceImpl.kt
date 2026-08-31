package com.example.mytestapp.feature.add.data.remote

import com.example.mytestapp.feature.add.domain.model.PropertyForm
import com.example.mytestapp.feature.search.data.mapper.jsonName
import com.example.mytestapp.feature.search.data.remote.AppWriteConstants
import com.example.mytestapp.feature.search.data.remote.AppWriteProvider
import com.example.mytestapp.feature.search.domain.utils.Result
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException

class AddPropertyRemoteDataSourceImpl : AddPropertyRemoteDataSource {

    private val databases = AppWriteProvider.databases

    override suspend fun addProperty(userId: String, form: PropertyForm): Result<String> {
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
                "status" to "live",
                "rentBuy" to form.rentBuy?.jsonName(),
                "residentialCommercial" to form.residentialCommercial?.jsonName(),
                "propertyType" to form.propertyType?.jsonName(),
                "bedroomType" to form.bedroomType?.jsonName(),
                "furnishing" to form.furnishing?.jsonName(),
                "facing" to form.facing?.jsonName(),
                "age" to form.age?.jsonName(),
                "amenities" to form.amenities.map { it.jsonName() },
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

            Result.Success(documentId)
        } catch (e: AppwriteException) {
            Result.Error(e.message ?: "Appwrite error")
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}")
        }
    }
}
