package com.realeapp.feature.auth.data.remote

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.realeapp.R
import com.realeapp.core.firebase.FirebaseConstants
import com.realeapp.core.firebase.FirebaseProvider
import com.realeapp.feature.auth.data.mapper.UserMapper
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.auth.presentation.AuthStrings
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthRemoteDataSource"
private const val ARROW = "\u279C"
private const val TICK = "\u2705"
private const val CROSS = "\u274C"
private const val VERIFICATION_TIMEOUT_SECONDS = 60L

class AuthRemoteDataSourceImpl(
    private val firebaseProvider: FirebaseProvider
) : AuthRemoteDataSource {

    private val auth = firebaseProvider.auth
    private val firestore = firebaseProvider.firestore

    override suspend fun login(email: String, password: String): Result<User> {
        Logger.d(TAG, "$ARROW login() called for email: $email")
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult?.user
                ?: return Result.Error(AuthStrings.ERROR_LOGIN_FAILED)
            val user = fetchUser(firebaseUser)
            Logger.d(TAG, "$TICK login() succeeded: userId=${user.id}")
            Result.Success(user)
        } catch (e: FirebaseAuthInvalidUserException) {
            Logger.e(TAG, "$CROSS login() invalid user: ${e.message}")
            Result.Error(e.message ?: AuthStrings.ERROR_LOGIN_FAILED)
        } catch (e: FirebaseAuthException) {
            Logger.e(TAG, "$CROSS login() failed: ${e.message}")
            Result.Error(e.message ?: AuthStrings.ERROR_LOGIN_FAILED)
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS login() unexpected error: ${e.message}")
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        Logger.d(TAG, "$ARROW register() called for name: $name, email: $email")
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult?.user
                ?: return Result.Error(AuthStrings.ERROR_REGISTRATION_FAILED)

            firebaseUser.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            ).await()

            firestore.collection(FirebaseConstants.USERS_COLLECTION)
                .document(firebaseUser.uid)
                .set(
                    mapOf(
                        "name" to name,
                        "email" to email,
                        "createdAt" to System.currentTimeMillis()
                    )
                )
                .await()

            val user = UserMapper.fromFirebaseUser(
                firebaseUser,
                mapOf("name" to name, "email" to email)
            )
            Logger.d(TAG, "$TICK register() succeeded: userId=${user.id}")
            Result.Success(user)
        } catch (e: FirebaseAuthException) {
            Logger.e(TAG, "$CROSS register() failed: ${e.message}")
            Result.Error(e.message ?: AuthStrings.ERROR_REGISTRATION_FAILED)
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS register() unexpected error: ${e.message}")
            Result.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun sendPhoneOtp(activity: Activity?, phone: String): Result<String> {
        Logger.d(TAG, "$ARROW sendPhoneOtp() called for phone: $phone")

        val currentActivity = activity ?: run {
            Logger.e(TAG, "$CROSS sendPhoneOtp() failed: no Activity provided")
            return Result.Error(AuthStrings.ERROR_OTP_ACTIVITY_NOT_FOUND)
        }

        return try {
            val verificationId = suspendCancellableCoroutine { continuation ->
                val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        Logger.d(TAG, "$TICK onVerificationCompleted")
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        Logger.e(TAG, "$CROSS onVerificationFailed: ${e.message}")
                        if (continuation.isActive) {
                            continuation.resumeWithException(e)
                        }
                    }

                    override fun onCodeSent(
                        id: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        Logger.d(TAG, "$TICK onCodeSent")
                        if (continuation.isActive) {
                            continuation.resume(id)
                        }
                    }
                }

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phone)
                    .setTimeout(VERIFICATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .setActivity(currentActivity)
                    .setCallbacks(callbacks)
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)
            }

            Logger.d(TAG, "$TICK sendPhoneOtp() succeeded")
            Result.Success(verificationId)
        } catch (e: FirebaseAuthInvalidUserException) {
            Logger.e(TAG, "$CROSS sendPhoneOtp() invalid user: ${e.message}")
            Result.Error(e.message ?: AuthStrings.ERROR_OTP_SEND_FAILED)
        } catch (e: FirebaseAuthException) {
            Logger.e(TAG, "$CROSS sendPhoneOtp() failed: ${e.message}")
            Result.Error(e.message ?: AuthStrings.ERROR_OTP_SEND_FAILED)
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS sendPhoneOtp() unexpected error: ${e.message}")
            Result.Error(AuthStrings.ERROR_OTP_SEND_FAILED)
        }
    }

    override suspend fun verifyPhoneOtp(
        verificationId: String,
        secret: String,
        phone: String,
        name: String,
        dob: String
    ): Result<User> {
        Logger.d(TAG, "$ARROW verifyPhoneOtp() called")
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, secret)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult?.user
                ?: return Result.Error(AuthStrings.ERROR_OTP_VERIFY_FAILED)

            if (name.isNotBlank()) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()
            }

            ensureUserDocument(firebaseUser, name, phone)

            val user = fetchUser(firebaseUser)
            Logger.d(TAG, "$TICK verifyPhoneOtp() succeeded: userId=${user.id}")
            Result.Success(user)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Logger.e(TAG, "$CROSS verifyPhoneOtp() invalid code: ${e.message}")
            Result.Error(AuthStrings.ERROR_OTP_INVALID_CODE)
        } catch (e: FirebaseAuthException) {
            Logger.e(TAG, "$CROSS verifyPhoneOtp() failed: ${e.message}")
            Result.Error(e.message ?: AuthStrings.ERROR_OTP_VERIFY_FAILED)
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS verifyPhoneOtp() unexpected error: ${e.message}")
            Result.Error(AuthStrings.ERROR_OTP_VERIFY_FAILED)
        }
    }

    override suspend fun signInWithGoogle(activity: Activity): Result<User> {
        Logger.d(TAG, "$ARROW signInWithGoogle() called")
        return try {
            val credentialManager = CredentialManager.create(activity)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(activity.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val result = credentialManager.getCredential(activity, request)
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
            val idToken = googleIdTokenCredential.idToken
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(firebaseCredential).await()
            val firebaseUser = authResult?.user
                ?: return Result.Error(AuthStrings.ERROR_GOOGLE_SIGN_IN_FAILED)

            ensureUserDocument(firebaseUser)

            val user = fetchUser(firebaseUser)
            Logger.d(TAG, "$TICK signInWithGoogle() succeeded: userId=${user.id}")
            Result.Success(user)
        } catch (e: NoCredentialException) {
            Logger.e(TAG, "$CROSS signInWithGoogle() no credential: ${e.message}")
            Result.Error(AuthStrings.ERROR_GOOGLE_NO_ACCOUNT)
        } catch (e: GoogleIdTokenParsingException) {
            Logger.e(TAG, "$CROSS signInWithGoogle() token parsing error: ${e.message}")
            Result.Error(AuthStrings.ERROR_GOOGLE_SIGN_IN_FAILED)
        } catch (e: FirebaseAuthException) {
            Logger.e(TAG, "$CROSS signInWithGoogle() firebase error: ${e.message}")
            Result.Error(e.message ?: AuthStrings.ERROR_GOOGLE_SIGN_IN_FAILED)
        } catch (e: GetCredentialException) {
            Logger.e(TAG, "$CROSS signInWithGoogle() credential error: ${e.message}")
            Result.Error(e.message ?: AuthStrings.ERROR_GOOGLE_SIGN_IN_FAILED)
        } catch (e: Exception) {
            Logger.e(TAG, "$CROSS signInWithGoogle() unexpected error: ${e.message}")
            Result.Error(AuthStrings.ERROR_GOOGLE_SIGN_IN_FAILED)
        }
    }

    private suspend fun fetchUser(firebaseUser: FirebaseUser): User {
        val snapshot = firestore.collection(FirebaseConstants.USERS_COLLECTION)
            .document(firebaseUser.uid)
            .get()
            .await()
        return UserMapper.fromFirebaseUser(
            firebaseUser,
            snapshot.data ?: emptyMap()
        )
    }

    private suspend fun ensureUserDocument(
        firebaseUser: FirebaseUser,
        name: String = firebaseUser.displayName.orEmpty(),
        phone: String = firebaseUser.phoneNumber.orEmpty()
    ) {
        val docRef = firestore.collection(FirebaseConstants.USERS_COLLECTION).document(firebaseUser.uid)
        val snapshot = docRef.get().await()
        if (!snapshot.exists()) {
            docRef.set(
                mapOf(
                    "name" to (firebaseUser.displayName ?: name),
                    "email" to firebaseUser.email.orEmpty(),
                    "phone" to (firebaseUser.phoneNumber ?: phone),
                    "image" to firebaseUser.photoUrl?.toString().orEmpty(),
                    "createdAt" to System.currentTimeMillis()
                )
            ).await()
        }
    }
}
