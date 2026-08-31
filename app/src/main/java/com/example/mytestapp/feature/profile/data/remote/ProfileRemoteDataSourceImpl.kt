package com.example.mytestapp.feature.profile.data.remote

import com.example.mytestapp.feature.auth.data.mapper.UserMapper
import com.example.mytestapp.feature.auth.domain.model.User
import com.example.mytestapp.feature.search.data.remote.AppWriteConstants
import com.example.mytestapp.feature.search.data.remote.AppWriteProvider
import com.example.mytestapp.feature.search.data.session.UserSession
import com.example.mytestapp.feature.search.domain.utils.Result
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.exceptions.AppwriteException

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
        return try {
            account.deleteSession(sessionId = sessionId)
            userSession.clear()
            Result.Success(Unit)
        } catch (e: AppwriteException) {
            userSession.clear()
            Result.Error(e.message ?: "Logout failed")
        } catch (e: Exception) {
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
