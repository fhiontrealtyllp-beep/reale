package com.realeapp.feature.search.data.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SessionObserver(
    private val userSession: UserSession,
    private val scope: CoroutineScope,
    private val onLogin: () -> Unit,
    private val onLogout: () -> Unit
) {
    private var wasLoggedIn = userSession.user.value != null

    init {
        scope.launch {
            userSession.user.collect { user ->
                val isLoggedIn = user != null
                if (isLoggedIn != wasLoggedIn) {
                    wasLoggedIn = isLoggedIn
                    if (isLoggedIn) onLogin() else onLogout()
                }
            }
        }
    }
}
