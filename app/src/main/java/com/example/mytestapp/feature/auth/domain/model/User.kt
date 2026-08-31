package com.example.mytestapp.feature.auth.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val status: String,
    val city: String,
    val location: String,
    val address: String,
    val password: String,
    val sessionId: String,
    val image: String? = null
)
