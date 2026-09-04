package com.realeapp.feature.search.data.repository

import com.realeapp.feature.search.data.remote.PropertyRemoteDataSource
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.repository.PropertyRepository
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger

class PropertyRepositoryImpl(
    private val remoteDataSource: PropertyRemoteDataSource
) : PropertyRepository {
    override suspend fun getAllProperties(filter: PropertyFilter?, page: Int, limit: Int): Result<List<Property>> {
        return remoteDataSource.getAllProperties(filter, page, limit)
    }

    override suspend fun updateLikeStatus(propertyId: String, isLiked: Boolean): Result<Unit> {
        Logger.d("PropertyRepository", "updateLikeStatus: propertyId=$propertyId, isLiked=$isLiked")
        return remoteDataSource.updateLikeStatus(propertyId, isLiked)
    }
}
