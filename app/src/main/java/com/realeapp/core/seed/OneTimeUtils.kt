package com.realeapp.core.seed

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.realeapp.core.firebase.FirebaseConstants
import com.realeapp.core.firebase.FirebaseProvider
import com.realeapp.feature.search.data.mapper.jsonName
import com.realeapp.feature.search.domain.model.Age
import com.realeapp.feature.search.domain.model.Amenity
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Facing
import com.realeapp.feature.search.domain.model.Furnishing
import com.realeapp.feature.search.domain.model.ListingCategory
import com.realeapp.feature.search.domain.model.LocationNormalizer
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.domain.model.ResidentialCommercial
import com.realeapp.util.Logger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "OneTimeUtils"
private const val PREFS_NAME = "OneTimeUtilsPrefs"
private const val KEY_SEEDED = "properties_seeded"
private const val KEY_PANAJI_PROMO_SEEDED = "panaji_promo_seeded"

private const val PROPERTIES_PER_CITY = 10
private const val IMAGES_PER_CITY = 5
private const val PANAJI_FEATURED_COUNT = 5
private const val PANAJI_PROMOTIONAL_COUNT = 5
private const val SEED_USER_ID = "seed_user"
private const val STATUS_LIVE = "live"
private const val AGENT_PHONE = "9876543210"
private const val TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
private const val TIMEZONE_UTC = "UTC"
private const val IMAGE_URL_TEMPLATE = "https://picsum.photos/seed/%s-%d/800/600"

private val CITIES = listOf("Bengaluru", "Pune", "Panaji", "Nagpur")

private val LOCALITY_NAMES = listOf(
    "Koramangala", "MG Road", "Indiranagar", "Whitefield", "HSR Layout",
    "Kalyani Nagar", "Viman Nagar", "Baner", "Koregaon Park", "Camp",
    "Miramar", "Panjim Market", "Caranzalem", "Dona Paula", "Altinho",
    "Civil Lines", "Sadar", "Dharampeth", "Ramdaspeth", "Laxminagar"
)

private val PINCODES = listOf(
    "560001", "411001", "403001", "440001"
)

class OneTimeUtils(
    context: Context,
    private val firebaseProvider: FirebaseProvider = FirebaseProvider()
) {

    private val firestore: FirebaseFirestore = firebaseProvider.firestore
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun seedPropertiesIfNeeded() = withContext(Dispatchers.IO) {
        if (prefs.getBoolean(KEY_SEEDED, false)) {
            Logger.d(TAG, "Properties already seeded, skipping.")
            return@withContext
        }

        Logger.d(TAG, "Starting one-time property seeding...")
        val propertiesCollection = firestore.collection(FirebaseConstants.PROPERTIES_COLLECTION)
        var successCount = 0

        for ((cityIndex, city) in CITIES.withIndex()) {
            val normalizedCity = LocationNormalizer.normalizeCity(city) ?: city.lowercase()
            val images = buildImageUrls(normalizedCity)
            val pincode = PINCODES.getOrElse(cityIndex) { "000000" }

            for (propertyIndex in 1..PROPERTIES_PER_CITY) {
                val globalIndex = cityIndex * PROPERTIES_PER_CITY + propertyIndex
                val locality = LOCALITY_NAMES[globalIndex % LOCALITY_NAMES.size]
                val data = buildPropertyData(
                    city = normalizedCity,
                    locality = locality,
                    pincode = pincode,
                    propertyIndex = propertyIndex,
                    globalIndex = globalIndex,
                    images = images
                )

                try {
                    val docRef = propertiesCollection.document()
                    val dataWithId = data.toMutableMap().apply { this["id"] = docRef.id }
                    docRef.set(dataWithId).await()
                    successCount++
                    Logger.d(TAG, "Seeded property $globalIndex in $city -> ${docRef.id}")
                } catch (e: Exception) {
                    Logger.e(TAG, "Failed to seed property $globalIndex in $city: ${e.message}", e)
                }
            }
        }

        if (successCount == CITIES.size * PROPERTIES_PER_CITY) {
            prefs.edit().putBoolean(KEY_SEEDED, true).apply()
            Logger.d(TAG, "Property seeding complete. Total: $successCount")
        } else {
            Logger.w(TAG, "Property seeding incomplete. Success: $successCount / ${CITIES.size * PROPERTIES_PER_CITY}")
        }
    }

    suspend fun seedFeaturedAndPromotionalPanajiIfNeeded() = withContext(Dispatchers.IO) {
        if (prefs.getBoolean(KEY_PANAJI_PROMO_SEEDED, false)) {
            Logger.d(TAG, "Panaji featured/promotional properties already seeded, skipping.")
            return@withContext
        }

        Logger.d(TAG, "Starting Panaji featured/promotional property seeding...")
        val propertiesCollection = firestore.collection(FirebaseConstants.PROPERTIES_COLLECTION)
        val city = "Panaji"
        val normalizedCity = LocationNormalizer.normalizeCity(city) ?: city.lowercase()
        val images = buildImageUrls(normalizedCity)
        val pincode = "403001"
        var successCount = 0
        val totalCount = PANAJI_FEATURED_COUNT + PANAJI_PROMOTIONAL_COUNT

        val categories = listOf(
            ListingCategory.FEATURED to PANAJI_FEATURED_COUNT,
            ListingCategory.PROMOTIONAL to PANAJI_PROMOTIONAL_COUNT
        )

        var categoryOffset = 0
        for ((listingCategory, count) in categories) {
            for (propertyIndex in 1..count) {
                val globalIndex = CITIES.size * PROPERTIES_PER_CITY + categoryOffset + propertyIndex
                val locality = LOCALITY_NAMES[globalIndex % LOCALITY_NAMES.size]
                val data = buildPropertyData(
                    city = normalizedCity,
                    locality = locality,
                    pincode = pincode,
                    propertyIndex = propertyIndex,
                    globalIndex = globalIndex,
                    images = images,
                    listingCategory = listingCategory
                )

                try {
                    val docRef = propertiesCollection.document()
                    val dataWithId = data.toMutableMap().apply { this["id"] = docRef.id }
                    docRef.set(dataWithId).await()
                    successCount++
                    Logger.d(TAG, "Seeded $listingCategory Panaji property $propertyIndex -> ${docRef.id}")
                } catch (e: Exception) {
                    Logger.e(TAG, "Failed to seed $listingCategory Panaji property $propertyIndex: ${e.message}", e)
                }
            }
            categoryOffset += count
        }

        if (successCount == totalCount) {
            prefs.edit().putBoolean(KEY_PANAJI_PROMO_SEEDED, true).apply()
            Logger.d(TAG, "Panaji featured/promotional seeding complete. Total: $successCount")
        } else {
            Logger.w(TAG, "Panaji featured/promotional seeding incomplete. Success: $successCount / $totalCount")
        }
    }

    private fun buildImageUrls(city: String): List<String> {
        return (1..IMAGES_PER_CITY).map { index ->
            IMAGE_URL_TEMPLATE.format(city.lowercase().replace(" ", "_"), index)
        }
    }

    private fun buildPropertyData(
        city: String,
        locality: String,
        pincode: String,
        propertyIndex: Int,
        globalIndex: Int,
        images: List<String>,
        listingCategory: ListingCategory = ListingCategory.NORMAL
    ): Map<String, Any?> {
        val propertyType = PropertyType.entries[globalIndex % PropertyType.entries.size]
        val rentBuy = if (globalIndex % 2 == 0) RentBuy.BUY else RentBuy.RENT
        val residentialCommercial = if (rentBuy == RentBuy.BUY) ResidentialCommercial.RESIDENTIAL else ResidentialCommercial.RESIDENTIAL
        val bedroomType = BedroomType.entries[propertyIndex % BedroomType.entries.size]
        val furnishing = Furnishing.entries[propertyIndex % Furnishing.entries.size]
        val facing = Facing.entries[propertyIndex % Facing.entries.size]
        val age = Age.entries[propertyIndex % Age.entries.size]

        val price = if (rentBuy == RentBuy.RENT) {
            (10_000 + (propertyIndex * 1_500)).toDouble()
        } else {
            (2_000_000 + (globalIndex * 100_000)).toDouble()
        }

        val amenities = listOf(
            Amenity.PARKING,
            Amenity.LIFT,
            Amenity.POWER_BACKUP,
            Amenity.CCTV,
            Amenity.GATED_COMMUNITY
        ).map { it.jsonName() }

        val categoryPrefix = if (listingCategory == ListingCategory.NORMAL) "" else "${listingCategory.name} "

        return mapOf(
            "userId" to SEED_USER_ID,
            "title" to "$categoryPrefix$propertyIndex ${propertyType.label} in ${city.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}",
            "description" to "A ${propertyType.label} for ${rentBuy.label.lowercase()} in $locality, $city. This is a seed property created for testing.",
            "price" to price,
            "city" to city,
            "locality" to LocationNormalizer.normalizeLocality(locality),
            "pincode" to pincode,
            "address" to "$locality, $city",
            "latitude" to (12.0 + globalIndex * 0.01),
            "longitude" to (77.0 + globalIndex * 0.01),
            "images" to images,
            "createdAt" to currentTimestamp(),
            "status" to STATUS_LIVE,
            "listingCategory" to listingCategory.name,
            "rentBuy" to rentBuy.jsonName(),
            "residentialCommercial" to residentialCommercial.jsonName(),
            "propertyType" to propertyType.jsonName(),
            "bedroomType" to bedroomType.jsonName(),
            "bathrooms" to (propertyIndex % 4) + 1,
            "furnishing" to furnishing.jsonName(),
            "facing" to facing.jsonName(),
            "age" to age.jsonName(),
            "amenities" to amenities,
            "carpetArea" to (500 + globalIndex * 50).toDouble(),
            "builtUpArea" to (600 + globalIndex * 60).toDouble(),
            "superBuiltUpArea" to (700 + globalIndex * 70).toDouble(),
            "agentPhone" to AGENT_PHONE
        )
    }

    private fun currentTimestamp(): String {
        val sdf = SimpleDateFormat(TIMESTAMP_FORMAT, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(TIMEZONE_UTC)
        return sdf.format(Date())
    }
}
