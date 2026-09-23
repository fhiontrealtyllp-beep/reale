package com.fhiont.feature.profile.data.remote

import com.fhiont.core.firebase.FirebaseConstants
import com.fhiont.core.firebase.FirebaseProvider
import com.fhiont.core.network.PhpAuthApi
import com.fhiont.feature.auth.data.mapper.UserMapper
import com.fhiont.feature.auth.domain.model.User
import com.fhiont.feature.search.data.session.UserSession
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.util.Logger
import java.util.UUID
import kotlinx.coroutines.tasks.await

private const val TAG = "ProfileRemoteDataSource"

class ProfileRemoteDataSourceImpl(
    private val firebaseProvider: FirebaseProvider,
    private val phpAuthApi: PhpAuthApi,
    private val userSession: UserSession
) : ProfileRemoteDataSource {

    private val auth = firebaseProvider.auth
    private val firestore = firebaseProvider.firestore
    private val storage = firebaseProvider.storage

    override suspend fun getUserDetails(): Result<User> {
        val sessionUser = userSession.getUser()
        val token = sessionUser?.sessionId
        return if (!token.isNullOrBlank()) {
            Logger.d(TAG, "getUserDetails: loading from PHP session")
            when (val result = phpAuthApi.me(token)) {
                is Result.Success -> result
                is Result.Error -> {
                    Logger.w(TAG, "getUserDetails: PHP me failed, falling back to session user: ${result.message}")
                    sessionUser?.let { Result.Success(it) } ?: result
                }
            }
        } else {
            getUserDetailsFromFirebase()
        }
    }

    private suspend fun getUserDetailsFromFirebase(): Result<User> {
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
        Logger.d(TAG, "logout() called")
        return try {
            val result = if (sessionId.isNotBlank()) {
                phpAuthApi.logout(sessionId)
            } else {
                Result.Success(Unit)
            }
            auth.signOut()
            result
        } catch (e: Exception) {
            Logger.e(TAG, "logout() failed: ${e.message}")
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
