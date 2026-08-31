package com.example.mytestapp.feature.add.data.repository

import com.example.mytestapp.feature.add.data.remote.AddPropertyRemoteDataSource
import com.example.mytestapp.feature.add.domain.model.PropertyForm
import com.example.mytestapp.feature.add.domain.repository.AddPropertyRepository
import com.example.mytestapp.feature.search.domain.utils.Result

class AddPropertyRepositoryImpl(
    private val remoteDataSource: AddPropertyRemoteDataSource
) : AddPropertyRepository {

    override suspend fun addProperty(userId: String, form: PropertyForm): Result<String> {
        return remoteDataSource.addProperty(userId, form)
    }

    override suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String> {
        return remoteDataSource.uploadImage(bytes, filename)
    }
}
