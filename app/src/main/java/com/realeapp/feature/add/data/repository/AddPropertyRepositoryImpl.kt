package com.realeapp.feature.add.data.repository

import com.realeapp.feature.add.data.mapper.toProperty
import com.realeapp.feature.add.data.remote.AddPropertyRemoteDataSource
import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.add.domain.repository.AddPropertyRepository
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result

class AddPropertyRepositoryImpl(
    private val remoteDataSource: AddPropertyRemoteDataSource
) : AddPropertyRepository {

    private val myPropertiesCache = mutableMapOf<String, List<Property>>()

    override suspend fun addProperty(userId: String, form: PropertyForm): Result<String> {
        return when (val result = remoteDataSource.addProperty(userId, form)) {
            is Result.Success -> {
                val newProperty = form.toProperty(result.data, userId)
                myPropertiesCache[userId] = listOf(newProperty) + (myPropertiesCache[userId] ?: emptyList())
                result
            }
            is Result.Error -> result
        }
    }

    override suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String> {
        return remoteDataSource.uploadImage(bytes, filename)
    }

    override suspend fun getMyProperties(userId: String): Result<List<Property>> {
        myPropertiesCache[userId]?.let { return Result.Success(it) }

        return when (val result = remoteDataSource.getMyProperties(userId)) {
            is Result.Success -> {
                myPropertiesCache[userId] = result.data
                result
            }
            is Result.Error -> result
        }
    }
}
