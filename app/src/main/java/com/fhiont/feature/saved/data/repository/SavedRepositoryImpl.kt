package com.fhiont.feature.saved.data.repository

import com.fhiont.feature.saved.data.remote.SavedRemoteDataSource
import com.fhiont.feature.saved.domain.repository.SavedRepository
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.utils.Result

class SavedRepositoryImpl(
    private val remoteDataSource: SavedRemoteDataSource
) : SavedRepository {
    override suspend fun getLikedProperties(userId: String): Result<List<Property>> {
        return remoteDataSource.getLikedProperties(userId)
    }
}
