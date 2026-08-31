package com.example.mytestapp.feature.auth.data.remote

import com.example.mytestapp.feature.auth.data.mapper.UserMapper
import com.example.mytestapp.feature.auth.domain.model.User
import com.example.mytestapp.feature.search.data.remote.AppWriteProvider
import com.example.mytestapp.feature.search.domain.utils.Result
import io.appwrite.exceptions.AppwriteException

class AuthRemoteDataSourceImpl : AuthRemoteDataSource {

    private val account = AppWriteProvider.account

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val session = account.createEmailPasswordSession(
                email = email,
                password = password
            )
            val user = account.get()
            Result.Success(UserMapper.fromAppwrite(user, session))
        } catch (e: AppwriteException) {
            Result.Error(e.message ?: "Login failed")
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}")
        }
    }
}
