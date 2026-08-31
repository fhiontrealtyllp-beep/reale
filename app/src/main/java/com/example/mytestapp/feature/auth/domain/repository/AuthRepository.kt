package com.example.mytestapp.feature.auth.domain.repository

import com.example.mytestapp.feature.auth.domain.model.User
import com.example.mytestapp.feature.search.domain.utils.Result

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
}
