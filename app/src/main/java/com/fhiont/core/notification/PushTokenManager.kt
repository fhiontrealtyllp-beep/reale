package com.fhiont.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import com.fhiont.feature.search.data.session.UserSession
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PushTokenManager(
    context: Context,
    private val userSession: UserSession,
    private val pushTokenApi: PushTokenApi
) {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start() {
        createNotificationChannel()
        scope.launch {
            userSession.user.collectLatest { user ->
                if (user != null && user.sessionId.isNotBlank()) {
                    runCatching { FirebaseMessaging.getInstance().token.await() }
                        .getOrNull()
                        ?.takeIf { it.isNotBlank() }
                        ?.let { pushTokenApi.register(user.sessionId, it) }
                }
            }
        }
    }

    fun register(token: String) {
        val sessionToken = userSession.getUser()?.sessionId?.takeIf { it.isNotBlank() } ?: return
        scope.launch {
            pushTokenApi.register(sessionToken, token)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val notificationManager = appContext.getSystemService(NotificationManager::class.java)
        notificationManager.deleteNotificationChannel(PushNotificationConstants.OLD_CHANNEL_ID)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channel = NotificationChannel(
            PushNotificationConstants.CHANNEL_ID,
            PushNotificationConstants.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = PushNotificationConstants.CHANNEL_DESCRIPTION
            setSound(
                soundUri,
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 250, 100, 250)
        }
        notificationManager.createNotificationChannel(channel)
    }
}
