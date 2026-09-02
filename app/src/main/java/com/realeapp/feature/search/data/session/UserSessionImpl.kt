package com.realeapp.feature.search.data.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "UserSession"

object UserSessionImpl : UserSession {

    private const val PREFS_NAME = "user_session"
    private const val KEY_ID = "id"
    private const val KEY_NAME = "name"
    private const val KEY_EMAIL = "email"
    private const val KEY_PHONE = "phone"
    private const val KEY_STATUS = "status"
    private const val KEY_CITY = "city"
    private const val KEY_LOCATION = "location"
    private const val KEY_ADDRESS = "address"
    private const val KEY_PASSWORD = "password"
    private const val KEY_SESSION_ID = "session_id"
    private const val KEY_IMAGE = "image"

    private var appContext: Context? = null
    private var preferences: SharedPreferences? = null
    private var currentUser: User? = null

    private val _user = MutableStateFlow<User?>(null)
    override val user: StateFlow<User?> = _user.asStateFlow()

    fun init(context: Context) {
        appContext = context.applicationContext
        preferences = createPrefs()
        restore()
        _user.value = currentUser
    }

    private fun createPrefs(): SharedPreferences? {
        val context = appContext ?: return null
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Logger.e(TAG, "Failed to open encrypted prefs, resetting", e)
            deletePrefsFile(context)
            try {
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (retry: Exception) {
                Logger.e(TAG, "Failed to recreate encrypted prefs", retry)
                null
            }
        }
    }

    private fun deletePrefsFile(context: Context) {
        try {
            context.deleteSharedPreferences(PREFS_NAME)
        } catch (e: Exception) {
            Logger.e(TAG, "Failed to delete corrupted prefs file", e)
        }
    }

    override fun getUserId(): String? = getUser()?.id

    override fun getUser(): User? {
        if (currentUser == null) {
            restore()
        }
        return currentUser
    }

    override fun setUser(user: User?) {
        Logger.d(TAG, "setUser: userId=${user?.id}")
        currentUser = user
        _user.value = user
        if (user != null) {
            save(user)
        } else {
            clearPrefs()
        }
    }

    override fun clear() {
        Logger.d(TAG, "clear")
        currentUser = null
        _user.value = null
        clearPrefs()
    }

    private fun save(user: User) {
        val prefs = preferences ?: return
        prefs.edit {
            putString(KEY_ID, user.id)
            putString(KEY_NAME, user.name)
            putString(KEY_EMAIL, user.email)
            putString(KEY_PHONE, user.phone)
            putString(KEY_STATUS, user.status)
            putString(KEY_CITY, user.city)
            putString(KEY_LOCATION, user.location)
            putString(KEY_ADDRESS, user.address)
            putString(KEY_PASSWORD, user.password)
            putString(KEY_SESSION_ID, user.sessionId)
            putString(KEY_IMAGE, user.image)
        }
    }

    private fun restore() {
        val prefs = preferences ?: return
        val id = prefs.getString(KEY_ID, null) ?: return

        currentUser = User(
            id = id,
            name = prefs.getString(KEY_NAME, "").orEmpty(),
            email = prefs.getString(KEY_EMAIL, "").orEmpty(),
            phone = prefs.getString(KEY_PHONE, "").orEmpty(),
            status = prefs.getString(KEY_STATUS, "").orEmpty(),
            city = prefs.getString(KEY_CITY, "").orEmpty(),
            location = prefs.getString(KEY_LOCATION, "").orEmpty(),
            address = prefs.getString(KEY_ADDRESS, "").orEmpty(),
            password = prefs.getString(KEY_PASSWORD, "").orEmpty(),
            sessionId = prefs.getString(KEY_SESSION_ID, "").orEmpty(),
            image = prefs.getString(KEY_IMAGE, null)
        )
        _user.value = currentUser
    }

    private fun clearPrefs() {
        val prefs = preferences ?: return
        try {
            prefs.edit {
                clear()
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Failed to clear encrypted prefs, resetting", e)
            appContext?.let { deletePrefsFile(it) }
            preferences = createPrefs()
        }
    }
}
