package com.realeapp.feature.profile.data.remote

import com.realeapp.core.firebase.FirebaseConstants
import com.realeapp.core.firebase.FirebaseProvider
import com.realeapp.feature.auth.data.mapper.UserMapper
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import java.util.UUID
import kotlinx.coroutines.tasks.await

private const val TAG = "ProfileRemoteDataSource"
private const val ARROW = "\u279C"
private const val TICK = "\u2705"
private const val CROSS = "\u274C"

class ProfileRemoteDataSourceImpl(
    private val firebaseProvider: FirebaseProvider
) : ProfileRemoteDataSource {

    private val auth = firebaseProvider.auth
    private val firestore = firebaseProvider.firestore
    private val storage = firebaseProvider.storage

    override suspend fun getUserDetails(): Result<User> {
        return try {
            val firebaseUser = auth.currentUser
                ?: return Result.Error("User not logged in")
            val snapshot = firestore.collection(FirebaseConstants.USERS_COLLECTION)
                .document(firebaseUser.uid)
                .get()
                .await()
            val user = UserMapper.fromFirebaseUser(
                firebaseUser,
                snapshot.data ?: emptyMap()
            )
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error("Failed to load profile: ${e.message}")
        }
    }

    override suspend fun updateProfileField(
        userId: String,
        field: String,
        value: String
    ): Result<String> {
        return try {
            val firebaseUser = auth.currentUser
                ?: return Result.Error("User not logged in")

            val userRef = firestore.collection(FirebaseConstants.USERS_COLLECTION).document(userId)

            when (field) {
                "name" -> {
                    auth.currentUser?.updateProfile(
                        com.google.firebase.auth.UserProfileChangeRequest.Builder()
                            .setDisplayName(value)
                            .build()
                    )?.await()
                    userRef.update("name", value).await()
                }
                "email" -> {
                    firebaseUser.updateEmail(value).await()
                    userRef.update("email", value).await()
                }
                "phone", "image", "address", "status", "city", "location" -> {
                    userRef.update(field, value).await()
                }
                else -> return Result.Error("Unsupported field: $field")
            }

            Result.Success("$field updated successfully")
        } catch (e: Exception) {
            Result.Error("Failed to update $field: ${e.message}")
        }
    }

    override suspend fun logout(sessionId: String): Result<Unit> {
        Logger.d(TAG, "$ARROW logout() called")
        return try {
            auth.signOut()
            Logger.d(TAG, "$TICK logout() succeeded")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS logout() failed: ${e.message}")
            Result.Error("Logout failed: ${e.message}")
        }
    }

    override suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.Error("User not logged in")
            val ext = filename.substringAfterLast('.', "jpg").lowercase()
            val safeName = "${System.currentTimeMillis()}_${UUID.randomUUID()}.${ext}"
            val ref = storage.reference.child("${FirebaseConstants.PROFILE_IMAGES_PATH}/$userId/$safeName")

            ref.putBytes(bytes).await()
            val url = ref.downloadUrl.await().toString()

            Result.Success(url)
        } catch (e: Exception) {
            Result.Error("Image upload failed: ${e.message}")
        }
    }
}
