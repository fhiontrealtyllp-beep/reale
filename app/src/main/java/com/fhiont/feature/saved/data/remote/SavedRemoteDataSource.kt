package com.fhiont.feature.saved.data.remote

import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.utils.Result

interface SavedRemoteDataSource {
    suspend fun getLikedProperties(userId: String): Result<List<Property>>
}
