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
            val message = e.message ?: ""
            if (message.contains("prohibited", ignoreCase = true) ||
                message.contains("session is active", ignoreCase = true)
            ) {
                try {
                    val user = account.get()
                    val sessions = account.listSessions()
                    val sessionId = sessions.sessions.firstOrNull()?.id.orEmpty()
                    Result.Success(UserMapper.fromAppwrite(user, sessionId))
                } catch (fallback: Exception) {
                    Result.Error(fallback.message ?: "Could not recover existing session")
                }
            } else {
                Result.Error(message.ifEmpty { "Login failed" })
            }
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}")
        }
    }
}
