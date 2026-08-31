package com.example.mytestapp.feature.auth.data.remote

import com.example.mytestapp.feature.auth.domain.model.User
import com.example.mytestapp.feature.search.domain.utils.Result

interface AuthRemoteDataSource {
    suspend fun login(email: String, password: String): Result<User>
}
