package com.example.mytestapp.feature.search.data.session

import com.example.mytestapp.feature.auth.domain.model.User

object UserSessionImpl : UserSession {
    private var currentUser: User? = null

    override fun getUserId(): String? = currentUser?.id
    override fun getUser(): User? = currentUser
    override fun setUser(user: User?) {
        currentUser = user
    }
    override fun clear() {
        currentUser = null
    }
}
