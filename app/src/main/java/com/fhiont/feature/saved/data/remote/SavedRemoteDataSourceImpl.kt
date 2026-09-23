package com.fhiont.feature.saved.data.remote

import com.fhiont.core.network.PhpPropertyApi
import com.fhiont.feature.search.data.session.UserSession
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.util.Logger

private const val TAG = "SavedRemoteDataSource"

class SavedRemoteDataSourceImpl(
    private val phpPropertyApi: PhpPropertyApi,
    private val userSession: UserSession
) : SavedRemoteDataSource {

    override suspend fun getLikedProperties(userId: String): Result<List<Property>> {
        Logger.d(TAG, "getLikedProperties() called for user: $userId")
        val token = userSession.getUser()?.sessionId
        if (token.isNullOrBlank()) {
            Logger.e(TAG, "getLikedProperties() failed: no session token")
            return Result.Error("User not logged in")
        }
        return when (val result = phpPropertyApi.getLikedProperties(token)) {
            is Result.Success -> Result.Success(result.data.map { it.copy(isLiked = true) })
            is Result.Error -> result
        }
    }
}
