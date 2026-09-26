package com.fhiont.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fhiont.MainActivity
import com.fhiont.R
import com.fhiont.util.Logger
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject

private const val TAG = "FhiontMessagingService"

class FhiontMessagingService : FirebaseMessagingService() {
    private val pushTokenManager: PushTokenManager by inject()

    override fun onNewToken(token: String) {
        Logger.d(TAG, "FCM token refreshed")
        pushTokenManager.register(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Logger.d(TAG, "Message received from: ${message.from}")
        val title = message.notification?.title
            ?: message.data["title"]
            ?: PushNotificationConstants.DEFAULT_TITLE
        val body = message.notification?.body
            ?: message.data["body"]
            ?: PushNotificationConstants.DEFAULT_MESSAGE
        createChannel()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(PushNotificationConstants.EXTRA_PROPERTY_ID, message.data[PushNotificationConstants.EXTRA_PROPERTY_ID])
            putExtra(PushNotificationConstants.EXTRA_ENQUIRY_ID, message.data[PushNotificationConstants.EXTRA_ENQUIRY_ID])
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            message.messageId?.hashCode() ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationManager = NotificationManagerCompat.from(this)
        if (!notificationManager.areNotificationsEnabled()) {
            Logger.d(TAG, "Notifications are disabled; skipping notification post")
            return
        }
        val notification = NotificationCompat.Builder(this, PushNotificationConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        runCatching {
            notificationManager.notify(
                PushNotificationConstants.NOTIFICATION_ID_BASE + (message.messageId?.hashCode() ?: 0),
                notification
            )
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            PushNotificationConstants.CHANNEL_ID,
            PushNotificationConstants.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = PushNotificationConstants.CHANNEL_DESCRIPTION
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}
