package com.realeapp.feature.auth.data.remote

import com.realeapp.feature.auth.data.mapper.UserMapper
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.search.data.remote.AppWriteProvider
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException

private const val TAG = "AuthRemoteDataSource"
private const val ARROW = "\u279C"
private const val TICK = "\u2705"
private const val CROSS = "\u274C"

class AuthRemoteDataSourceImpl : AuthRemoteDataSource {

    private val account = AppWriteProvider.account

    override suspend fun login(email: String, password: String): Result<User> {
        Logger.d(TAG, "$ARROW login() called for email: $email")
        return try {
            val session = account.createEmailPasswordSession(
                email = email,
                password = password
            )
            val user = account.get()
            Logger.d(TAG, "$TICK login() succeeded: userId=${user.id}")
            Result.Success(UserMapper.fromAppwrite(user, session))
        } catch (e: AppwriteException) {
            val message = e.message ?: ""
            Logger.e(TAG, "$CROSS login() AppwriteException: $message")
            if (message.contains("prohibited", ignoreCase = true) ||
                message.contains("session is active", ignoreCase = true)
            ) {
                try {
                    val user = account.get()
                    val sessions = account.listSessions()
                    val sessionId = sessions.sessions.firstOrNull()?.id.orEmpty()
                    Logger.d(TAG, "$TICK login() recovered existing session: userId=${user.id}")
                    Result.Success(UserMapper.fromAppwrite(user, sessionId))
                } catch (fallback: Exception) {
                    Logger.e(TAG, "$CROSS login() failed to recover session: ${fallback.message}")
                    Result.Error(fallback.message ?: "Could not recover existing session")
                }
            } else {
                Result.Error(message.ifEmpty { "Login failed" })
            }
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS login() unexpected error: ${e.message}")
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        Logger.d(TAG, "$ARROW register() called for name: $name, email: $email")
        return try {
            account.create(
                userId = ID.unique(),
                email = email,
                password = password,
                name = name
            )
            val session = account.createEmailPasswordSession(
                email = email,
                password = password
            )
            val user = account.get()
            Logger.d(TAG, "$TICK register() succeeded: userId=${user.id}")
            Result.Success(UserMapper.fromAppwrite(user, session))
        } catch (e: AppwriteException) {
            Logger.e(TAG, "$CROSS register() failed: ${e.message}")
            Result.Error(e.message ?: "Registration failed")
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS register() unexpected error: ${e.message}")
            Result.Error("Unexpected error: ${e.message}")
        }
    }
}
