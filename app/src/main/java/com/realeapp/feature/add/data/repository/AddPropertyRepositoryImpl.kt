package com.realeapp.feature.add.data.repository

import com.realeapp.feature.add.data.remote.AddPropertyRemoteDataSource
import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.add.domain.repository.AddPropertyRepository
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result

class AddPropertyRepositoryImpl(
    private val remoteDataSource: AddPropertyRemoteDataSource
) : AddPropertyRepository {

    override suspend fun addProperty(userId: String, form: PropertyForm): Result<String> {
        return remoteDataSource.addProperty(userId, form)
    }

    override suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String> {
        return remoteDataSource.uploadImage(bytes, filename)
    }

    override suspend fun getMyProperties(userId: String): Result<List<Property>> {
        return remoteDataSource.getMyProperties(userId)
    }
}
