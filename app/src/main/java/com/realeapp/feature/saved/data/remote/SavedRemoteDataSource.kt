package com.realeapp.feature.saved.data.remote

import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result

interface SavedRemoteDataSource {
    suspend fun getLikedProperties(userId: String): Result<List<Property>>
}
