package com.example.mytestapp.feature.search.data.session

import com.example.mytestapp.feature.auth.domain.model.User

interface UserSession {
    fun getUserId(): String?
    fun getUser(): User?
    fun setUser(user: User?)
    fun clear()
}
