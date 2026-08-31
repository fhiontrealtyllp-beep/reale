package com.example.mytestapp.feature.search.data.repository

import com.example.mytestapp.feature.search.data.remote.PropertyRemoteDataSource
import com.example.mytestapp.feature.search.domain.model.Property
import com.example.mytestapp.feature.search.domain.model.PropertyFilter
import com.example.mytestapp.feature.search.domain.repository.PropertyRepository
import com.example.mytestapp.feature.search.domain.utils.Result

class PropertyRepositoryImpl(
    private val remoteDataSource: PropertyRemoteDataSource
) : PropertyRepository {
    override suspend fun getAllProperties(filter: PropertyFilter?, page: Int, limit: Int): Result<List<Property>> {
        return remoteDataSource.getAllProperties(filter, page, limit)
    }

    override suspend fun updateLikeStatus(propertyId: String, isLiked: Boolean): Result<Unit> {
        return remoteDataSource.updateLikeStatus(propertyId, isLiked)
    }
}
