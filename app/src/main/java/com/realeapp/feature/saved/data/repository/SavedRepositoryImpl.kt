package com.realeapp.feature.saved.data.repository

import com.realeapp.feature.saved.data.remote.SavedRemoteDataSource
import com.realeapp.feature.saved.domain.repository.SavedRepository
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result

class SavedRepositoryImpl(
    private val remoteDataSource: SavedRemoteDataSource
) : SavedRepository {
    override suspend fun getLikedProperties(userId: String): Result<List<Property>> {
        return remoteDataSource.getLikedProperties(userId)
    }
}
