package com.realeapp.feature.search.data.remote

import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.utils.Result

interface PropertyRemoteDataSource {
    suspend fun getAllProperties(filter: PropertyFilter?, page: Int, limit: Int): Result<List<Property>>
    suspend fun updateLikeStatus(propertyId: String, isLiked: Boolean): Result<Unit>
}
