package com.fhiont.feature.add.domain.repository

import com.fhiont.feature.add.domain.model.PropertyForm
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.utils.Result

interface AddPropertyRepository {
    suspend fun addProperty(userId: String, form: PropertyForm): Result<String>
    suspend fun uploadImage(bytes: ByteArray, filename: String, uploadGroupId: String): Result<String>
    suspend fun getMyProperties(userId: String): Result<List<Property>>
}
