package com.example.mytestapp.feature.search.data.remote

import com.example.mytestapp.feature.search.domain.model.Property
import com.example.mytestapp.feature.search.domain.model.PropertyFilter
import com.example.mytestapp.feature.search.domain.utils.Result

interface PropertyRemoteDataSource {
    suspend fun getAllProperties(filter: PropertyFilter?, page: Int, limit: Int): Result<List<Property>>
    suspend fun updateLikeStatus(propertyId: String, isLiked: Boolean): Result<Unit>
}
