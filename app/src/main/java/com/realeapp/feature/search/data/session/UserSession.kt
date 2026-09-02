package com.realeapp.feature.search.data.session

import com.realeapp.feature.auth.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserSession {
    val user: StateFlow<User?>
    fun getUserId(): String?
    fun getUser(): User?
    fun setUser(user: User?)
    fun clear()
}
