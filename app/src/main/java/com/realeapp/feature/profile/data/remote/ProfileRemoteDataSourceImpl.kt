package com.realeapp.feature.profile.data.remote

import com.realeapp.feature.auth.data.mapper.UserMapper
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.search.data.remote.AppWriteConstants
import com.realeapp.feature.search.data.remote.AppWriteProvider
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.exceptions.AppwriteException

private const val TAG = "ProfileRemoteDataSource"
private const val ARROW = "\u279C"
private const val TICK = "\u2705"
private const val CROSS = "\u274C"

class ProfileRemoteDataSourceImpl(
    private val userSession: UserSession
) : ProfileRemoteDataSource {

    private val account = AppWriteProvider.account
    private val storage = AppWriteProvider.storage

    override suspend fun getUserDetails(): Result<User> {
        return try {
            val user = account.get()
            val currentSessionId = userSession.getUser()?.sessionId.orEmpty()
            val mapped = UserMapper.fromAppwrite(user, currentSessionId)
            userSession.setUser(mapped)
            Result.Success(mapped)
        } catch (e: AppwriteException) {
            Result.Error(e.message ?: "Failed to load profile")
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun updateProfileField(
        userId: String,
        field: String,
        value: String
    ): Result<String> {
        return try {
            when (field) {
                "name" -> {
                    account.updateName(name = value)
                }
                "email" -> {
                    val password = userSession.getUser()?.password.orEmpty()
                    if (password.isEmpty()) {
                        return Result.Error("Password is required to update email")
                    }
                    account.updateEmail(email = value, password = password)
                }
                "image", "phone", "address" -> {
                    val currentUser = account.get()
                    @Suppress("UNCHECKED_CAST")
                    val prefs = (currentUser.prefs.data as? Map<String, Any>)?.toMutableMap() ?: mutableMapOf()
                    prefs[field] = value
                    account.updatePrefs(prefs = prefs)
                }
                else -> return Result.Error("Unsupported field: $field")
            }

            val updatedUser = account.get()
            val currentSessionId = userSession.getUser()?.sessionId.orEmpty()
            val mapped = UserMapper.fromAppwrite(updatedUser, currentSessionId)
            userSession.setUser(mapped)
            Result.Success("$field updated successfully")
        } catch (e: AppwriteException) {
            Result.Error(e.message ?: "Failed to update $field")
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun logout(sessionId: String): Result<Unit> {
        Logger.d(TAG, "$ARROW logout() called for sessionId: $sessionId")
        return try {
            account.deleteSession(sessionId = sessionId)
            userSession.clear()
            Logger.d(TAG, "$TICK logout() succeeded")
            Result.Success(Unit)
        } catch (e: AppwriteException) {
            Logger.e(TAG, "$CROSS logout() failed: ${e.message}")
            userSession.clear()
            Result.Error(e.message ?: "Logout failed")
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS logout() unexpected error: ${e.message}")
            userSession.clear()
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String> {
        return try {
            val mimeType = when (filename.substringAfterLast('.', "").lowercase()) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                "gif" -> "image/gif"
                "webp" -> "image/webp"
                "bmp" -> "image/bmp"
                else -> "image/jpeg"
            }
            val inputFile = InputFile.fromBytes(bytes, filename, mimeType)
            val result = storage.createFile(
                bucketId = AppWriteConstants.STORAGE_BUCKET_ID,
                fileId = ID.unique(),
                file = inputFile
            )

            val imageUrl = buildString {
                append(AppWriteConstants.ENDPOINT)
                append("/storage/buckets/")
                append(AppWriteConstants.STORAGE_BUCKET_ID)
                append("/files/")
                append(result.id)
                append("/view?project=")
                append(AppWriteConstants.PROJECT_ID)
            }

            Result.Success(imageUrl)
        } catch (e: AppwriteException) {
            Result.Error(e.message ?: "Image upload failed")
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}")
        }
    }
}
