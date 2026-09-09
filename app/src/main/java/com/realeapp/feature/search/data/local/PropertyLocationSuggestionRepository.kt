package com.realeapp.feature.search.data.local

import com.realeapp.core.firebase.FirebaseConstants
import com.realeapp.core.firebase.FirebaseProvider
import com.realeapp.feature.search.domain.model.LocationSuggestion
import com.realeapp.feature.search.domain.repository.LocationSuggestionRepository
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

private const val TAG = "PropertyLocationSuggestionRepository"
private const val SUGGESTION_LIMIT = 20
private const val FETCH_LIMIT = 1000L

class PropertyLocationSuggestionRepository(
    private val firebaseProvider: FirebaseProvider
) : LocationSuggestionRepository {

    override suspend fun getSuggestions(query: String): Result<List<LocationSuggestion>> {
        Logger.d(TAG, "getSuggestions: query=$query")
        if (query.isBlank()) {
            return Result.Success(emptyList())
        }

        return try {
            val snapshot = firebaseProvider.firestore
                .collection(FirebaseConstants.PROPERTIES_COLLECTION)
                .whereEqualTo("status", "live")
                .limit(FETCH_LIMIT)
                .get()
                .await()

            val normalizedQuery = query.trim().lowercase()

            val suggestions = snapshot.documents
                .asSequence()
                .mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val city = data["city"]?.toString()?.trim().orEmpty()
                    val locality = data["locality"]?.toString()?.trim().orEmpty()
                    if (city.isBlank()) return@mapNotNull null

                    LocationSuggestion(
                        placeId = doc.id,
                        primaryText = city,
                        secondaryText = locality.takeIf {
                            it.isNotBlank() && !it.equals(city, ignoreCase = true)
                        }.orEmpty(),
                        fullText = buildFullText(city, locality)
                    )
                }
                .filter {
                    it.primaryText.contains(normalizedQuery, ignoreCase = true) ||
                        it.secondaryText.contains(normalizedQuery, ignoreCase = true)
                }
                .distinctBy { it.fullText.lowercase() }
                .sortedBy { it.fullText.lowercase() }
                .take(SUGGESTION_LIMIT)
                .toList()

            Logger.d(TAG, "getSuggestions: found=${suggestions.size}")
            Result.Success(suggestions)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Logger.e(TAG, "getSuggestions: error=${e.message}", e)
            Result.Error(e.message ?: "Failed to load suggestions")
        }
    }

    private fun buildFullText(city: String, locality: String): String {
        return if (locality.isNotBlank() && !locality.equals(city, ignoreCase = true)) {
            "$city, $locality"
        } else {
            city
        }
    }
}
