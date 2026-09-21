package com.fhiont.feature.search.data.session

import com.fhiont.feature.auth.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserSession {
    val user: StateFlow<User?>
    fun getUserId(): String?
    fun getUser(): User?
    fun setUser(user: User?)
    fun clear()
}
