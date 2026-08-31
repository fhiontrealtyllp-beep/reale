package com.realeapp.feature.auth.data.mapper

import com.realeapp.feature.auth.domain.model.User
import io.appwrite.models.Session
import io.appwrite.models.User as AppwriteUser

object UserMapper {

    @Suppress("UNCHECKED_CAST")
    fun fromAppwrite(
        user: AppwriteUser<Map<String, Any>>,
        sessionId: String
    ): User {
        val prefs = user.prefs.data ?: emptyMap()
        return User(
            id = user.id,
            name = user.name,
            email = user.email,
            phone = prefs["phone"] as? String ?: "",
            status = prefs["status"] as? String ?: "",
            city = prefs["city"] as? String ?: "",
            location = prefs["location"] as? String ?: "",
            address = prefs["address"] as? String ?: "",
            password = prefs["password"] as? String ?: "",
            sessionId = sessionId,
            image = prefs["image"] as? String
        )
    }

    fun fromAppwrite(
        user: AppwriteUser<Map<String, Any>>,
        session: Session
    ): User {
        return fromAppwrite(user, session.id)
    }
}
