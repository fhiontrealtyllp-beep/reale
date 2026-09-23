package com.fhiont.core.network

import com.fhiont.BuildConfig
import com.fhiont.feature.search.domain.model.Enquiry
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.util.Logger
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val CONNECT_TIMEOUT_MS = 15_000
private const val READ_TIMEOUT_MS = 20_000
private const val CONTENT_TYPE_JSON = "application/json; charset=utf-8"
private const val HTTP_METHOD_GET = "GET"
private const val HTTP_METHOD_POST = "POST"
private const val TAG = "PhpEnquiryApi"

class PhpEnquiryApi {

    val isConfigured: Boolean
        get() = BuildConfig.API_BASE_URL.isNotBlank()

    suspend fun sendEnquiry(
        property: Property,
        message: String,
        userId: String?
    ): Result<Unit> = apiCall(
        method = HTTP_METHOD_POST,
        endpoint = "enquiry.php",
        token = null,
        body = JSONObject().apply {
            put("propertyId", property.id)
            put("propertyTitle", property.title)
            put("propertyLocation", buildShortLocation(property))
            put("propertyImage", property.images.firstOrNull().orEmpty())
            put("agentPhone", property.agentPhone)
            put("message", message.trim())
            putOpt("userId", userId?.toLongOrNull())
        },
        parse = { }
    )

    suspend fun getEnquiriesByUser(token: String): Result<List<Enquiry>> = apiCall(
        method = HTTP_METHOD_GET,
        endpoint = "enquiries.php?type=user",
        token = token,
        parse = { response ->
            val array = response.getJSONObject("data").getJSONArray("enquiries")
            List(array.length()) { index ->
                array.getJSONObject(index).toEnquiry()
            }
        }
    )

    suspend fun getEnquiriesByProperty(token: String, propertyId: String): Result<List<Enquiry>> = apiCall(
        method = HTTP_METHOD_GET,
        endpoint = "enquiries.php?type=property&propertyId=${URLEncoder.encode(propertyId, "UTF-8")}",
        token = token,
        parse = { response ->
            val array = response.getJSONObject("data").getJSONArray("enquiries")
            List(array.length()) { index ->
                array.getJSONObject(index).toEnquiry()
            }
        }
    )

    suspend fun getEnquiryCountsForPropertyIds(
        token: String,
        propertyIds: List<String>
    ): Result<Map<String, Int>> = apiCall(
        method = HTTP_METHOD_GET,
        endpoint = "enquiry-counts.php?ids=${URLEncoder.encode(propertyIds.joinToString(","), "UTF-8")}",
        token = token,
        parse = { response ->
            val counts = response.getJSONObject("data").getJSONObject("counts")
            val map = mutableMapOf<String, Int>()
            val keys = counts.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = counts.optInt(key, 0)
            }
            map
        }
    )

    private fun JSONObject.toEnquiry(): Enquiry = Enquiry(
        id = optString("id"),
        propertyId = optString("propertyId"),
        propertyTitle = optString("propertyTitle"),
        propertyLocation = optString("propertyLocation"),
        propertyImage = optString("propertyImage"),
        agentPhone = optString("agentPhone"),
        message = optString("message"),
        userId = if (has("userId") && !isNull("userId")) optString("userId") else null,
        status = optString("status"),
        createdAt = optString("createdAt")
    )

    private fun buildShortLocation(property: Property): String {
        return listOf(property.locality, property.city)
            .filter { it.isNotBlank() }
            .joinToString(", ")
    }

    private suspend fun <T> apiCall(
        method: String,
        endpoint: String,
        token: String?,
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
}
