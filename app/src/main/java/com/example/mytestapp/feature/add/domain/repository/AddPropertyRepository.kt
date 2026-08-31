package com.example.mytestapp.feature.add.domain.repository

import com.example.mytestapp.feature.add.domain.model.PropertyForm
import com.example.mytestapp.feature.search.domain.utils.Result

interface AddPropertyRepository {
    suspend fun addProperty(userId: String, form: PropertyForm): Result<String>
}
