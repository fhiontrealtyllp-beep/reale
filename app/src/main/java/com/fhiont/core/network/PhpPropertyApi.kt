package com.fhiont.core.network

import android.util.Base64
import com.fhiont.BuildConfig
import com.fhiont.feature.add.domain.model.PropertyForm
import com.fhiont.feature.add.presentation.AddStrings
import com.fhiont.feature.search.data.mapper.PropertyMapper
import com.fhiont.feature.search.data.mapper.jsonName
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.util.Logger
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

private const val CONNECT_TIMEOUT_MS = 15_000
private const val READ_TIMEOUT_MS = 20_000
private const val CONTENT_TYPE_JSON = "application/json; charset=utf-8"
private const val HTTP_METHOD_GET = "GET"
private const val HTTP_METHOD_POST = "POST"
private const val TAG = "PhpPropertyApi"

class PhpPropertyApi {

    suspend fun addProperty(token: String, form: PropertyForm): Result<String> = apiCall(
        method = HTTP_METHOD_POST,
        endpoint = "properties.php",
        token = token,
        body = form.toJson(),
        parse = { it.getJSONObject("data").getString("propertyId") }
    )

    suspend fun getMyProperties(token: String): Result<List<Property>> = apiCall(
        method = HTTP_METHOD_GET,
        endpoint = "properties.php",
        token = token,
        parse = { response ->
            val array = response.getJSONObject("data").getJSONArray("properties")
            List(array.length()) { index ->
                PropertyMapper.fromMap(array.getJSONObject(index).toMap())
            }
        }
    )

    suspend fun uploadImage(token: String, bytes: ByteArray, filename: String): Result<String> = apiCall(
        method = HTTP_METHOD_POST,
        endpoint = "upload-image.php",
        token = token,
        body = JSONObject()
            .put("filename", filename)
            .put("mimeType", imageMimeType(filename))
            .put("data", Base64.encodeToString(bytes, Base64.DEFAULT)),
        parse = { it.getJSONObject("data").getString("url") }
    )

    suspend fun updateLike(token: String, propertyId: String, isLiked: Boolean): Result<Unit> = apiCall(
        method = HTTP_METHOD_POST,
        endpoint = "like.php",
        token = token,
        body = JSONObject()
            .put("propertyId", propertyId)
            .put("isLiked", isLiked),
        parse = { }
    )

    suspend fun getLikedProperties(token: String): Result<List<Property>> = apiCall(
        method = HTTP_METHOD_GET,
        endpoint = "likes.php",
        token = token,
        parse = { response ->
            val array = response.getJSONObject("data").getJSONArray("properties")
            List(array.length()) { index ->
                PropertyMapper.fromMap(array.getJSONObject(index).toMap())
            }
        }
    )

    suspend fun getFeaturedProperties(token: String, limit: Int): Result<List<Property>> = apiCall(
        method = HTTP_METHOD_GET,
        endpoint = "properties-featured.php?limit=$limit",
        token = token,
        parse = { response ->
            val array = response.getJSONObject("data").getJSONArray("properties")
            List(array.length()) { index ->
                PropertyMapper.fromMap(array.getJSONObject(index).toMap())
            }
        }
    )

    suspend fun getPromotionalProperties(token: String, limit: Int): Result<List<Property>> = apiCall(
        method = HTTP_METHOD_GET,
        endpoint = "properties-promotional.php?limit=$limit",
        token = token,
        parse = { response ->
            val array = response.getJSONObject("data").getJSONArray("properties")
            List(array.length()) { index ->
                PropertyMapper.fromMap(array.getJSONObject(index).toMap())
            }
        }
    )

    private fun imageMimeType(filename: String): String {
        return when (filename.substringAfterLast('.', "").lowercase()) {
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            else -> "image/jpeg"
        }
    }

    private fun PropertyForm.toJson(): JSONObject = JSONObject().apply {
        put("title", title.trim())
        put("description", description.trim())
        putOpt("price", price.toDoubleOrNull())
        put("city", city.trim())
        put("locality", locality.trim())
        put("pincode", pincode.trim())
        put("address", address.trim())
        putOpt("latitude", latitude.toDoubleOrNull())
        putOpt("longitude", longitude.toDoubleOrNull())
        putOpt("rentBuy", rentBuy?.jsonName())
        putOpt("residentialCommercial", residentialCommercial?.jsonName())
        putOpt("propertyType", propertyType?.jsonName())
        putOpt("bedroomType", bedroomType?.jsonName())
        put("bathrooms", bathrooms)
        putOpt("furnishing", furnishing?.jsonName())
        putOpt("facing", facing?.jsonName())
        putOpt("age", age?.jsonName())
        putOpt("carpetArea", carpetArea.toDoubleOrNull())
        putOpt("builtUpArea", builtUpArea.toDoubleOrNull())
        putOpt("superBuiltUpArea", superBuiltUpArea.toDoubleOrNull())
        put("agentPhone", agentPhone.trim())
        put("status", AddStrings.STATUS_LIVE)
        put("listingCategory", listingCategory.name)
        putOpt("amenities", JSONArray(amenities.map { it.jsonName() }))
        putOpt("images", JSONArray(images.map { it.trim() }.filter { it.isNotBlank() }))
        putOpt("nearbyPlaces", JSONArray(nearbyPlaces.map {
            JSONObject()
                .put("name", it.name)
                .put("distanceKm", it.distanceKm)
                .put("type", it.type.name)
        }))
    }

    private fun JSONObject.toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        val keys = keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = when (val value = opt(key)) {
                is JSONObject -> value.toMap()
                is JSONArray -> value.toList()
                JSONObject.NULL -> null
                else -> value
            }
        }
        return map
    }

    private fun JSONArray.toList(): List<Any?> {
        return List(length()) { index ->
            when (val value = opt(index)) {
                is JSONObject -> value.toMap()
                is JSONArray -> value.toList()
                JSONObject.NULL -> null
                else -> value
            }
        }
    }

    private suspend fun <T> apiCall(
        method: String,
        endpoint: String,
        token: String,
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
                setRequestProperty("Authorization", "Bearer $token")
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
