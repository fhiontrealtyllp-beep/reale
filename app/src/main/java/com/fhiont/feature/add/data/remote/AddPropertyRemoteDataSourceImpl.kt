package com.fhiont.feature.add.data.remote

import com.fhiont.core.network.PhpPropertyApi
import com.fhiont.feature.add.domain.model.PropertyForm
import com.fhiont.feature.search.data.session.UserSession
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.util.Logger

private const val TAG = "AddPropertyRemoteDataSource"

class AddPropertyRemoteDataSourceImpl(
    private val phpPropertyApi: PhpPropertyApi,
    private val userSession: UserSession
) : AddPropertyRemoteDataSource {

    override suspend fun addProperty(userId: String, form: PropertyForm): Result<String> {
        Logger.d(TAG, "addProperty() called for user: $userId, title: ${form.title}")
        val token = userSession.getUser()?.sessionId
        if (token.isNullOrBlank()) {
            Logger.e(TAG, "addProperty() failed: no session token")
            return Result.Error("User not logged in")
        }
        return phpPropertyApi.addProperty(token, form)
    }

    override suspend fun uploadImage(
        bytes: ByteArray,
        filename: String,
        uploadGroupId: String
    ): Result<String> {
        Logger.d(TAG, "uploadImage() called: filename=$filename, size=${bytes.size}")
        val token = userSession.getUser()?.sessionId
        if (token.isNullOrBlank()) {
            Logger.e(TAG, "uploadImage() failed: no session token")
            return Result.Error("User not logged in")
        }
        return phpPropertyApi.uploadImage(token, bytes, filename, uploadGroupId)
    }

    override suspend fun getMyProperties(userId: String): Result<List<Property>> {
        Logger.d(TAG, "getMyProperties() called for user: $userId")
        val token = userSession.getUser()?.sessionId
        if (token.isNullOrBlank()) {
            Logger.e(TAG, "getMyProperties() failed: no session token")
            return Result.Error("User not logged in")
        }
        return phpPropertyApi.getMyProperties(token)
    }

    override suspend fun deleteProperty(userId: String, propertyId: String): Result<Unit> {
        Logger.d(TAG, "deleteProperty() called for user: $userId, property: $propertyId")
        val token = userSession.getUser()?.sessionId
        if (token.isNullOrBlank()) {
            Logger.e(TAG, "deleteProperty() failed: no session token")
            return Result.Error("User not logged in")
        }
        return phpPropertyApi.deleteProperty(token, propertyId)
    }
}
