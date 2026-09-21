package com.fhiont.feature.saved.domain.repository

import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.utils.Result

interface SavedRepository {
    suspend fun getLikedProperties(userId: String): Result<List<Property>>
}
