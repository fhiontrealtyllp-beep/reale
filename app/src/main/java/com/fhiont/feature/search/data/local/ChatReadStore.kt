package com.fhiont.feature.search.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class ChatReadStore(context: Context) {

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun markRead(enquiryId: String, timestamp: String) {
        preferences.edit { putString(keyFor(enquiryId), timestamp) }
    }

    fun getLastReadTimestamp(enquiryId: String): String? {
        return preferences.getString(keyFor(enquiryId), null)
    }

    fun isUnread(enquiryId: String, latestTimestamp: String): Boolean {
        val lastRead = getLastReadTimestamp(enquiryId) ?: return true
        return latestTimestamp > lastRead
    }

    fun getUnreadCount(enquiryId: String, timestamps: List<String>): Int {
        if (timestamps.isEmpty()) return 0
        val lastRead = getLastReadTimestamp(enquiryId) ?: return timestamps.size
        return timestamps.count { it > lastRead }
    }

    fun clear(enquiryId: String) {
        preferences.edit { remove(keyFor(enquiryId)) }
    }

    private fun keyFor(enquiryId: String) = "${KEY_PREFIX}$enquiryId"

    private companion object {
        const val PREFS_NAME = "chat_read_state"
        const val KEY_PREFIX = "chat_read_"
    }
}
