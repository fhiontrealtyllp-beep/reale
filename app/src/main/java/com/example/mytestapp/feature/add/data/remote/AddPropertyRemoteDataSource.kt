package com.example.mytestapp.feature.add.data.remote

import com.example.mytestapp.feature.add.domain.model.PropertyForm
import com.example.mytestapp.feature.search.domain.utils.Result

interface AddPropertyRemoteDataSource {
    suspend fun addProperty(userId: String, form: PropertyForm): Result<String>
}
