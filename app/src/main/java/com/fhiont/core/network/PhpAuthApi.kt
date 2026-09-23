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
private const val HTTP_METHOD_GET = "GET"
private const val HTTP_METHOD_POST = "POST"
private const val TAG = "PhpAuthApi"

class PhpAuthApi {

    val isConfigured: Boolean
        get() = BuildConfig.API_BASE_URL.isNotBlank()

    suspend fun login(email: String, password: String): Result<User> = apiCall(
        method = HTTP_METHOD_POST,
        endpoint = "login.php",
        body = JSONObject()
            .put("email", email)
            .put("password", password),
        parse = { it.userJson().toUser() }
    )

    suspend fun register(name: String, email: String, password: String): Result<User> = apiCall(
        method = HTTP_METHOD_POST,
        endpoint = "register.php",
        body = JSONObject()
            .put("name", name)
            .put("email", email)
            .put("password", password),
        parse = { it.userJson().toUser() }
    )

    suspend fun me(token: String): Result<User> = apiCall(
        method = HTTP_METHOD_GET,
        endpoint = "me.php",
        token = token,
        parse = { it.userJson().toUser() }
    )

    suspend fun logout(token: String): Result<Unit> = apiCall(
        method = HTTP_METHOD_POST,
        endpoint = "logout.php",
        token = token,
        body = JSONObject(),
        parse = { }
    )

    private fun JSONObject.userJson(): JSONObject = getJSONObject("data").getJSONObject("user")

    private suspend fun <T> apiCall(
        method: String,
        endpoint: String,
        token: String? = null,
        body: JSONObject? = null,
        parse: (JSONObject) -> T
    ): Result<T> = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val baseUrl = BuildConfig.API_BASE_URL.trimEnd('/') + "/"
            val requestUrl = baseUrl + endpoint
            Logger.d(TAG, "$method $requestUrl")
            connection = (URL(requestUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = method
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("Content-Type", CONTENT_TYPE_JSON)
                setRequestProperty("Accept", "application/json")
                if (!token.isNullOrBlank()) {
                    setRequestProperty("Authorization", "Bearer $token")
                }
                if (body != null) {
                    doOutput = true
                }
            }
            body?.let {
                connection.outputStream.use { output ->
                    output.write(it.toString().toByteArray(Charsets.UTF_8))
                }
            }

            val status = connection.responseCode
            Logger.d(TAG, "$method $requestUrl -> HTTP $status")
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val responseText = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            val response = if (responseText.isBlank()) JSONObject() else JSONObject(responseText)
            val message = response.optString("message").ifBlank { "Request failed ($status)" }
            if (status !in 200..299 || !response.optBoolean("success", false)) {
                return@withContext Result.Error(message)
            }

            Result.Success(parse(response))
        } catch (exception: Exception) {
            Logger.e(
                TAG,
                "$method ${BuildConfig.API_BASE_URL.trimEnd('/')}/$endpoint failed: ${exception.javaClass.simpleName}: ${exception.message}",
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
