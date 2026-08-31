package com.example.mytestapp.feature.saved.data.remote

import com.example.mytestapp.feature.search.domain.model.Property
import com.example.mytestapp.feature.search.domain.utils.Result

interface SavedRemoteDataSource {
    suspend fun getLikedProperties(userId: String): Result<List<Property>>
}
