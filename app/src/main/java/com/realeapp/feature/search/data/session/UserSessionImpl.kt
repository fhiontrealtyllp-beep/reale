package com.realeapp.feature.search.data.session

import android.content.Context
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.util.Logger

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

    private var context: Context? = null
    private var currentUser: User? = null

    fun init(context: Context) {
        this.context = context.applicationContext
        restore()
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
        if (user != null) {
            save(user)
        } else {
            clearPrefs()
        }
    }

    override fun clear() {
        Logger.d(TAG, "clear")
        currentUser = null
        clearPrefs()
    }

    private fun save(user: User) {
        val ctx = context ?: return
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
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
            apply()
        }
    }

    private fun restore() {
        val ctx = context ?: return
        val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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
    }

    private fun clearPrefs() {
        val ctx = context ?: return
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
