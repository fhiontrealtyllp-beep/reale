package com.example.mytestapp.feature.saved.domain.repository

import com.example.mytestapp.feature.search.domain.model.Property
import com.example.mytestapp.feature.search.domain.utils.Result

interface SavedRepository {
    suspend fun getLikedProperties(userId: String): Result<List<Property>>
}
