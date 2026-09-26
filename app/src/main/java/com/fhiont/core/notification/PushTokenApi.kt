package com.fhiont.core.notification

import com.fhiont.BuildConfig
import com.fhiont.util.Logger
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val TAG = "PushTokenApi"
private const val CONNECT_TIMEOUT_MS = 15_000
private const val READ_TIMEOUT_MS = 20_000

class PushTokenApi {
    suspend fun register(sessionToken: String, deviceToken: String) = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val endpoint = BuildConfig.API_BASE_URL.trimEnd('/') + "/device-token.php"
            connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Authorization", "Bearer $sessionToken")
            }
            val body = JSONObject().apply {
                put("token", deviceToken)
                put("platform", PushNotificationConstants.PLATFORM_ANDROID)
            }
            connection.outputStream.use { output ->
                output.write(body.toString().toByteArray(Charsets.UTF_8))
            }
            val status = connection.responseCode
            if (status !in 200..299) {
                val error = connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                Logger.e(TAG, "Token registration failed: HTTP $status $error")
            }
        } catch (exception: Exception) {
            Logger.e(TAG, "Token registration failed", exception)
        } finally {
            connection?.disconnect()
        }
    }
}
