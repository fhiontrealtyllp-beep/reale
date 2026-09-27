package com.fhiont.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
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
    private val enquiryNotificationUpdates: EnquiryNotificationUpdates by inject()
    private val notificationPreferences: NotificationPreferences by inject()

    override fun onNewToken(token: String) {
        Logger.d(TAG, "FCM token refreshed")
        pushTokenManager.register(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Logger.d(TAG, "Message received from: ${message.from}")
        val notificationSettings = notificationPreferences.settings.value
        if (!notificationSettings.enabled) {
            Logger.d(TAG, "Notifications disabled by user preference; skipping notification")
            return
        }
        if (!message.data[PushNotificationConstants.EXTRA_ENQUIRY_ID].isNullOrBlank()) {
            enquiryNotificationUpdates.notifyReceived()
        }
        val title = message.notification?.title
            ?: message.data["title"]
            ?: PushNotificationConstants.DEFAULT_TITLE
        val body = message.notification?.body
            ?: message.data["body"]
            ?: PushNotificationConstants.DEFAULT_MESSAGE
        createChannel(
            soundEnabled = notificationSettings.soundEnabled,
            vibrationEnabled = notificationSettings.vibrationEnabled
        )
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            message.data.forEach { (key, value) ->
                putExtra(key, value)
            }
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
            .setSound(if (notificationSettings.soundEnabled) RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) else null)
            .setVibrate(if (notificationSettings.vibrationEnabled) longArrayOf(0, 250, 100, 250) else longArrayOf(0))
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

    private fun createChannel(soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.deleteNotificationChannel(PushNotificationConstants.OLD_CHANNEL_ID)
        notificationManager.deleteNotificationChannel(PushNotificationConstants.CHANNEL_ID)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channel = NotificationChannel(
            PushNotificationConstants.CHANNEL_ID,
            PushNotificationConstants.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = PushNotificationConstants.CHANNEL_DESCRIPTION
            setSound(
                if (soundEnabled) soundUri else null,
                if (soundEnabled) {
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                } else {
                    null
                }
            )
            enableVibration(vibrationEnabled)
            vibrationPattern = if (vibrationEnabled) longArrayOf(0, 250, 100, 250) else longArrayOf(0)
        }
        notificationManager.createNotificationChannel(channel)
    }
}
