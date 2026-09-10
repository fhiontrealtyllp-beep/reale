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
private const val KEY_NAGPUR_SEEDED = "nagpur_seeded"
private const val KEY_ESSENTIAL_FILTER_SEEDED = "essential_filter_coverage_seeded"

private const val PROPERTIES_PER_CITY = 10
private const val IMAGES_PER_CITY = 5
private const val PANAJI_FEATURED_COUNT = 5
private const val PANAJI_PROMOTIONAL_COUNT = 5
private const val ESSENTIAL_FILTER_SEED_COPIES = 2
private const val ESSENTIAL_FILTER_MAX_BATHROOMS = 5
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

private data class SeedBudget(val label: String, val price: Double)

private val SEED_BUY_BUDGETS = listOf(
    SeedBudget("Under \u20B925 L", 1_500_000.0),
    SeedBudget("\u20B925-50 L", 3_500_000.0),
    SeedBudget("\u20B950 L-1 Cr", 7_500_000.0),
    SeedBudget("\u20B91-5 Cr", 25_000_000.0),
    SeedBudget("\u20B95 Cr+", 60_000_000.0)
)

private val SEED_RENT_BUDGETS = listOf(
    SeedBudget("Under \u20B910 K/mo", 5_000.0),
    SeedBudget("\u20B910-25 K/mo", 17_000.0),
    SeedBudget("\u20B925-50 K/mo", 37_000.0),
    SeedBudget("\u20B950 K+/mo", 60_000.0)
)

private val SEED_LOCALITIES = listOf(
    "Civil Lines", "Sadar", "Dharampeth", "Ramdaspeth", "Laxminagar",
    "Wardha Road", "Manish Nagar", "Hingna", "Parsodi", "Futala"
)

private val SEED_PINCODES = listOf("440001", "440010", "440022")

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

    suspend fun seedNagpurPropertiesIfNeeded() = withContext(Dispatchers.IO) {
        if (prefs.getBoolean(KEY_NAGPUR_SEEDED, false)) {
            Logger.d(TAG, "Nagpur properties already seeded, skipping.")
            return@withContext
        }

        Logger.d(TAG, "Starting Nagpur property seeding...")
        val propertiesCollection = firestore.collection(FirebaseConstants.PROPERTIES_COLLECTION)
        val city = "Nagpur"
        val normalizedCity = LocationNormalizer.normalizeCity(city) ?: city.lowercase()
        val images = buildImageUrls(normalizedCity)
        val pincode = "440001"
        val baseLatitude = 21.1458
        val baseLongitude = 79.0882
        val localities = listOf(
            "Civil Lines", "Sadar", "Dharampeth", "Ramdaspeth", "Laxminagar",
            "Wardha Road", "Manish Nagar", "Hingna", "Parsodi", "Futala"
        )
        val rentBuyTypes = listOf(
            RentBuy.BUY, RentBuy.BUY, RentBuy.BUY, RentBuy.BUY, RentBuy.BUY,
            RentBuy.RENT, RentBuy.RENT, RentBuy.RENT, RentBuy.RENT, RentBuy.RENT
        )
        val categories = listOf(
            ListingCategory.FEATURED,
            ListingCategory.FEATURED,
            ListingCategory.PROMOTIONAL,
            ListingCategory.NORMAL,
            ListingCategory.NORMAL,
            ListingCategory.NORMAL,
            ListingCategory.NORMAL,
            ListingCategory.NORMAL,
            ListingCategory.NORMAL,
            ListingCategory.NORMAL
        )

        var successCount = 0
        for (propertyIndex in 1..10) {
            val globalIndex = 1_000 + propertyIndex
            val latitude = baseLatitude + (propertyIndex * 0.001)
            val longitude = baseLongitude + (propertyIndex * 0.001)
            val data = buildPropertyData(
                city = normalizedCity,
                locality = localities[(propertyIndex - 1) % localities.size],
                pincode = pincode,
                propertyIndex = propertyIndex,
                globalIndex = globalIndex,
                images = images,
                listingCategory = categories[propertyIndex - 1],
                latitude = latitude,
                longitude = longitude,
                rentBuyOverride = rentBuyTypes[propertyIndex - 1]
            )

            try {
                val docRef = propertiesCollection.document()
                val dataWithId = data.toMutableMap().apply { this["id"] = docRef.id }
                docRef.set(dataWithId).await()
                successCount++
                Logger.d(TAG, "Seeded Nagpur property $propertyIndex -> ${docRef.id}")
            } catch (e: Exception) {
                Logger.e(TAG, "Failed to seed Nagpur property $propertyIndex: ${e.message}", e)
            }
        }

        if (successCount == 10) {
            prefs.edit().putBoolean(KEY_NAGPUR_SEEDED, true).apply()
            Logger.d(TAG, "Nagpur property seeding complete. Total: $successCount")
        } else {
            Logger.w(TAG, "Nagpur property seeding incomplete. Success: $successCount / 10")
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

    /**
     * Builds the essential-filter coverage seed data without writing to
     * Firestore. Two properties are generated for every combination of the
     * essential chip filters (rentBuy x category x propertyType x bedroomType).
     * Bathrooms, budget, locality and pincode are distributed across the groups
     * so every value is represented, and each title lists the exact filter
     * values it matches.
     */
    fun buildEssentialFilterCoverageData(): List<Map<String, Any?>> {
        val city = "Nagpur"
        val normalizedCity = LocationNormalizer.normalizeCity(city) ?: city.lowercase()
        val images = buildImageUrls("nagpur_filter_seed")
        val baseLatitude = 21.1458
        val baseLongitude = 79.0882

        val result = mutableListOf<Map<String, Any?>>()
        var groupIndex = 0
        var globalIndex = 20_000

        for (rentBuy in RentBuy.entries) {
            val budgets = if (rentBuy == RentBuy.RENT) SEED_RENT_BUDGETS else SEED_BUY_BUDGETS
            for (resComm in ResidentialCommercial.entries) {
                for (propertyType in PropertyType.entries) {
                    for (bedroomType in BedroomType.entries) {
                        val bathrooms = (groupIndex % ESSENTIAL_FILTER_MAX_BATHROOMS) + 1
                        val budget = budgets[groupIndex % budgets.size]
                        val locality = SEED_LOCALITIES[groupIndex % SEED_LOCALITIES.size]
                        val pincode = SEED_PINCODES[groupIndex % SEED_PINCODES.size]
                        val furnishing = Furnishing.entries[groupIndex % Furnishing.entries.size]
                        val facing = Facing.entries[groupIndex % Facing.entries.size]
                        val age = Age.entries[groupIndex % Age.entries.size]

                        val title = "${rentBuy.label} | ${resComm.label} | ${propertyType.label} | " +
                            "${bedroomType.label} | $bathrooms Bath | ${budget.label} | $locality"
                        val description = "Filter test property matching: ${rentBuy.label}, " +
                            "${resComm.label}, ${propertyType.label}, ${bedroomType.label}, " +
                            "$bathrooms bathrooms, ${budget.label} in $locality, $city."

                        for (copy in 1..ESSENTIAL_FILTER_SEED_COPIES) {
                            result += buildPropertyData(
                                city = normalizedCity,
                                locality = locality,
                                pincode = pincode,
                                propertyIndex = globalIndex,
                                globalIndex = globalIndex,
                                images = images,
                                latitude = baseLatitude + (globalIndex * 0.0001),
                                longitude = baseLongitude + (globalIndex * 0.0001),
                                rentBuyOverride = rentBuy,
                                residentialCommercialOverride = resComm,
                                propertyTypeOverride = propertyType,
                                bedroomTypeOverride = bedroomType,
                                bathroomsOverride = bathrooms,
                                furnishingOverride = furnishing,
                                facingOverride = facing,
                                ageOverride = age,
                                priceOverride = budget.price,
                                titleOverride = "$title #$copy",
                                descriptionOverride = description
                            )
                            globalIndex++
                        }
                        groupIndex++
                    }
                }
            }
        }

        return result
    }

    /** Returns the titles of the essential-filter coverage seed data for preview. */
    fun previewEssentialFilterCoverageTitles(): List<String> =
        buildEssentialFilterCoverageData().mapNotNull { it["title"] as? String }

    suspend fun seedEssentialFilterCoverageIfNeeded() = withContext(Dispatchers.IO) {
        if (prefs.getBoolean(KEY_ESSENTIAL_FILTER_SEEDED, false)) {
            Logger.d(TAG, "Essential filter coverage already seeded, skipping.")
            return@withContext
        }

        Logger.d(TAG, "Starting essential filter coverage seeding...")
        val propertiesCollection = firestore.collection(FirebaseConstants.PROPERTIES_COLLECTION)
        val seedData = buildEssentialFilterCoverageData()
        var successCount = 0

        for (data in seedData) {
            try {
                val docRef = propertiesCollection.document()
                val dataWithId = data.toMutableMap().apply { this["id"] = docRef.id }
                docRef.set(dataWithId).await()
                successCount++
            } catch (e: Exception) {
                Logger.e(TAG, "Failed to seed filter coverage property: ${e.message}", e)
            }
        }

        if (successCount == seedData.size) {
            prefs.edit().putBoolean(KEY_ESSENTIAL_FILTER_SEEDED, true).apply()
            Logger.d(TAG, "Essential filter coverage seeding complete. Total: $successCount")
        } else {
            Logger.w(TAG, "Essential filter coverage seeding incomplete. Success: $successCount / ${seedData.size}")
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
        listingCategory: ListingCategory = ListingCategory.NORMAL,
        latitude: Double = (12.0 + globalIndex * 0.01),
        longitude: Double = (77.0 + globalIndex * 0.01),
        rentBuyOverride: RentBuy? = null,
        residentialCommercialOverride: ResidentialCommercial? = null,
        propertyTypeOverride: PropertyType? = null,
        bedroomTypeOverride: BedroomType? = null,
        bathroomsOverride: Int? = null,
        furnishingOverride: Furnishing? = null,
        facingOverride: Facing? = null,
        ageOverride: Age? = null,
        priceOverride: Double? = null,
        titleOverride: String? = null,
        descriptionOverride: String? = null
    ): Map<String, Any?> {
        val propertyType = propertyTypeOverride ?: PropertyType.entries[globalIndex % PropertyType.entries.size]
        val rentBuy = rentBuyOverride ?: if (globalIndex % 2 == 0) RentBuy.BUY else RentBuy.RENT
        val residentialCommercial = residentialCommercialOverride ?: propertyType.category
        val bedroomType = bedroomTypeOverride ?: BedroomType.entries[propertyIndex % BedroomType.entries.size]
        val furnishing = furnishingOverride ?: Furnishing.entries[propertyIndex % Furnishing.entries.size]
        val facing = facingOverride ?: Facing.entries[propertyIndex % Facing.entries.size]
        val age = ageOverride ?: Age.entries[propertyIndex % Age.entries.size]
        val bathrooms = bathroomsOverride ?: (propertyIndex % 4) + 1

        val price = priceOverride ?: if (rentBuy == RentBuy.RENT) {
            (10_000 + (propertyIndex * 1_500)).toDouble()
        } else {
            (2_000_000 + (globalIndex * 100_000)).toDouble()
        }

        val amenities = Amenity.entries.map { it.jsonName() }

        val categoryPrefix = if (listingCategory == ListingCategory.NORMAL) "" else "${listingCategory.name} "
        val defaultTitle = "$categoryPrefix$propertyIndex ${propertyType.label} in " +
            city.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val defaultDescription = "A ${propertyType.label} for ${rentBuy.label.lowercase()} in " +
            "$locality, $city. This is a seed property created for testing."

        return mapOf(
            "userId" to SEED_USER_ID,
            "title" to (titleOverride ?: defaultTitle),
            "description" to (descriptionOverride ?: defaultDescription),
            "price" to price,
            "city" to city,
            "locality" to LocationNormalizer.normalizeLocality(locality),
            "pincode" to pincode,
            "address" to "$locality, $city",
            "latitude" to latitude,
            "longitude" to longitude,
            "images" to images,
            "createdAt" to currentTimestamp(),
            "status" to STATUS_LIVE,
            "listingCategory" to listingCategory.name,
            "rentBuy" to rentBuy.jsonName(),
            "residentialCommercial" to residentialCommercial.jsonName(),
            "propertyType" to propertyType.jsonName(),
            "bedroomType" to bedroomType.jsonName(),
            "bathrooms" to bathrooms,
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
