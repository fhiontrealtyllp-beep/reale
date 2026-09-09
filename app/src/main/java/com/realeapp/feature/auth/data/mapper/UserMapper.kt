package com.realeapp.feature.auth.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.realeapp.feature.auth.domain.model.User

object UserMapper {

    fun fromFirebaseUser(
        user: FirebaseUser,
        extra: Map<String, Any?> = emptyMap(),
        fallbackPassword: String = ""
    ): User {
        return User(
            id = user.uid,
            name = user.displayName ?: extra["name"].toString().orEmpty(),
            email = user.email ?: extra["email"].toString().orEmpty(),
            phone = user.phoneNumber ?: extra["phone"].toString().orEmpty(),
            status = extra["status"].toString().orEmpty(),
            city = extra["city"].toString().orEmpty(),
            location = extra["location"].toString().orEmpty(),
            address = extra["address"].toString().orEmpty(),
            password = fallbackPassword,
            sessionId = user.uid,
            image = user.photoUrl?.toString() ?: extra["image"].toString().takeIf { it.isNotBlank() }
        )
    }

    fun fromMap(map: Map<String, Any?>, userId: String, fallbackPassword: String = ""): User {
        return User(
            id = userId,
            name = map["name"].toString().orEmpty(),
            email = map["email"].toString().orEmpty(),
            phone = map["phone"].toString().orEmpty(),
            status = map["status"].toString().orEmpty(),
            city = map["city"].toString().orEmpty(),
            location = map["location"].toString().orEmpty(),
            address = map["address"].toString().orEmpty(),
            password = fallbackPassword,
            sessionId = userId,
            image = map["image"].toString().takeIf { it.isNotBlank() }
        )
    }

    fun toMap(user: User): Map<String, Any?> {
        return mapOf(
            "name" to user.name,
            "email" to user.email,
            "phone" to user.phone,
            "status" to user.status,
            "city" to user.city,
            "location" to user.location,
            "address" to user.address,
            "image" to user.image
        )
    }
}
