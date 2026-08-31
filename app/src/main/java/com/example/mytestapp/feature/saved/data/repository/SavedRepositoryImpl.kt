package com.example.mytestapp.feature.saved.data.repository

import com.example.mytestapp.feature.saved.data.remote.SavedRemoteDataSource
import com.example.mytestapp.feature.saved.domain.repository.SavedRepository
import com.example.mytestapp.feature.search.domain.model.Property
import com.example.mytestapp.feature.search.domain.utils.Result

class SavedRepositoryImpl(
    private val remoteDataSource: SavedRemoteDataSource
) : SavedRepository {
    override suspend fun getLikedProperties(userId: String): Result<List<Property>> {
        return remoteDataSource.getLikedProperties(userId)
    }
}
