package com.fhiont.core.network

import com.fhiont.BuildConfig
import com.fhiont.feature.auth.domain.model.User
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.util.Logger
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val CONNECT_TIMEOUT_MS = 15_000
private const val READ_TIMEOUT_MS = 20_000
private const val CONTENT_TYPE_JSON = "application/json; charset=utf-8"
private const val HTTP_METHOD_POST = "POST"
private const val TAG = "PhpAuthApi"

class PhpAuthApi {

    val isConfigured: Boolean
        get() = BuildConfig.API_BASE_URL.isNotBlank()

    suspend fun login(email: String, password: String): Result<User> {
        return post(
            endpoint = "login.php",
            body = JSONObject()
                .put("email", email)
                .put("password", password)
        )
    }

    suspend fun register(name: String, email: String, password: String): Result<User> {
        return post(
            endpoint = "register.php",
            body = JSONObject()
                .put("name", name)
                .put("email", email)
                .put("password", password)
        )
    }

    private suspend fun post(endpoint: String, body: JSONObject): Result<User> = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val baseUrl = BuildConfig.API_BASE_URL.trimEnd('/') + "/"
            val requestUrl = baseUrl + endpoint
            Logger.d(TAG, "POST $requestUrl")
            connection = (URL(requestUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = HTTP_METHOD_POST
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                doOutput = true
                setRequestProperty("Content-Type", CONTENT_TYPE_JSON)
                setRequestProperty("Accept", "application/json")
            }
            connection.outputStream.use { output ->
                output.write(body.toString().toByteArray(Charsets.UTF_8))
            }

            val status = connection.responseCode
            Logger.d(TAG, "POST $requestUrl -> HTTP $status")
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val responseText = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            val response = if (responseText.isBlank()) JSONObject() else JSONObject(responseText)
            val message = response.optString("message").ifBlank { "Request failed ($status)" }
            if (status !in 200..299 || !response.optBoolean("success", false)) {
                return@withContext Result.Error(message)
            }

            val userJson = response.getJSONObject("data").getJSONObject("user")
            Result.Success(userJson.toUser())
        } catch (exception: Exception) {
            Logger.e(
                TAG,
                "POST ${BuildConfig.API_BASE_URL.trimEnd('/')}/$endpoint failed: ${exception.javaClass.simpleName}: ${exception.message}",
                exception
            )
            val message = when (exception) {
                is IOException -> "Unable to connect to the server"
                else -> exception.message ?: "Unexpected server response"
            }
            Result.Error(message)
        } finally {
            connection?.disconnect()
        }
    }

    private fun JSONObject.toUser(): User = User(
        id = getString("id"),
        name = optString("name"),
        email = optString("email"),
        phone = optString("phone"),
        status = optString("status"),
        city = optString("city"),
        location = optString("location"),
        address = optString("address"),
        password = "",
        sessionId = optString("sessionId"),
        image = optString("image").takeIf { it.isNotBlank() && it != "null" }
    )
}
