package com.realeapp.feature.saved.domain.repository

import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result

interface SavedRepository {
    suspend fun getLikedProperties(userId: String): Result<List<Property>>
}
