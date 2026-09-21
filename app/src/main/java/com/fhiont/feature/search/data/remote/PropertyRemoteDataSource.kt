package com.fhiont.feature.search.data.remote

import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.model.PropertyFilter
import com.fhiont.feature.search.domain.utils.Result

interface PropertyRemoteDataSource {
    suspend fun getAllProperties(filter: PropertyFilter?, page: Int, limit: Int): Result<List<Property>>
    suspend fun getFeaturedProperties(limit: Int): Result<List<Property>>
    suspend fun getPromotionalProperties(limit: Int): Result<List<Property>>
    suspend fun updateLikeStatus(propertyId: String, isLiked: Boolean): Result<Unit>
}
