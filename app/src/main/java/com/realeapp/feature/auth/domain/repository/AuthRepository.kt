package com.realeapp.feature.auth.domain.repository

import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.search.domain.utils.Result

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
}
