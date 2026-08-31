package com.realeapp.feature.auth.data.remote

import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.search.domain.utils.Result

interface AuthRemoteDataSource {
    suspend fun login(email: String, password: String): Result<User>
}
