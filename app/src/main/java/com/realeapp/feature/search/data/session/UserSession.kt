package com.realeapp.feature.search.data.session

import com.realeapp.feature.auth.domain.model.User

interface UserSession {
    fun getUserId(): String?
    fun getUser(): User?
    fun setUser(user: User?)
    fun clear()
}
