package com.realeapp.feature.add.data.remote

import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result

interface AddPropertyRemoteDataSource {
    suspend fun addProperty(userId: String, form: PropertyForm): Result<String>
    suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String>
    suspend fun getMyProperties(userId: String): Result<List<Property>>
}
