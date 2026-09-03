package com.realeapp.feature.search.data.session

import com.realeapp.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "SessionObserver"

class SessionObserver(
    private val userSession: UserSession,
    private val scope: CoroutineScope,
    private val onLogin: () -> Unit,
    private val onLogout: () -> Unit
) {
    private var wasLoggedIn = userSession.user.value != null

    init {
        Logger.d(TAG, "init: wasLoggedIn=$wasLoggedIn")
        scope.launch {
            userSession.user.collect { user ->
                val isLoggedIn = user != null
                if (isLoggedIn != wasLoggedIn) {
                    wasLoggedIn = isLoggedIn
                    if (isLoggedIn) {
                        Logger.d(TAG, "session transition -> login: userId=${user?.id}")
                        onLogin()
                    } else {
                        Logger.d(TAG, "session transition -> logout")
                        onLogout()
                    }
                }
            }
        }
    }
}
