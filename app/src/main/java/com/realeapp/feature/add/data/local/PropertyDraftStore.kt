package com.realeapp.feature.add.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.search.domain.model.Age
import com.realeapp.feature.search.domain.model.Amenity
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Facing
import com.realeapp.feature.search.domain.model.Furnishing
import com.realeapp.feature.search.domain.model.ListingCategory
import com.realeapp.feature.search.domain.model.NearbyPlace
import com.realeapp.feature.search.domain.model.NearbyPlaceType
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.domain.model.ResidentialCommercial
import com.realeapp.util.Logger
import org.json.JSONArray
import org.json.JSONObject

/**
 * Restored in-progress "add property" draft: the form plus the step the user
 * was on. The step is kept as its enum name so this data-layer class does not
 * depend on the presentation-layer [com.realeapp.feature.add.presentation.AddPropertyStep].
 */
data class PropertyDraft(
    val form: PropertyForm,
    val stepName: String
)

/**
 * Persists the in-progress add-property draft in plain SharedPreferences as a
 * single JSON blob so the user can close the app mid-flow and resume on the
 * same step with every field prefilled.
 */
class PropertyDraftStore(context: Context) {

    private companion object {
        const val TAG = "PropertyDraftStore"
        const val PREFS_NAME = "add_property_draft"
        const val KEY_DRAFT = "draft"

        const val F_STEP = "step"
        const val F_RENT_BUY = "rentBuy"
        const val F_RESIDENTIAL_COMMERCIAL = "residentialCommercial"
        const val F_PROPERTY_TYPE = "propertyType"
        const val F_BEDROOM_TYPE = "bedroomType"
        const val F_TITLE = "title"
        const val F_DESCRIPTION = "description"
        const val F_PRICE = "price"
        const val F_CITY = "city"
        const val F_LOCALITY = "locality"
        const val F_PINCODE = "pincode"
        const val F_ADDRESS = "address"
        const val F_LATITUDE = "latitude"
        const val F_LONGITUDE = "longitude"
        const val F_FURNISHING = "furnishing"
        const val F_FACING = "facing"
        const val F_AGE = "age"
        const val F_AMENITIES = "amenities"
        const val F_CARPET_AREA = "carpetArea"
        const val F_BUILT_UP_AREA = "builtUpArea"
        const val F_SUPER_BUILT_UP_AREA = "superBuiltUpArea"
        const val F_BATHROOMS = "bathrooms"
        const val F_AGENT_PHONE = "agentPhone"
        const val F_LISTING_CATEGORY = "listingCategory"
        const val F_NEARBY_PLACES = "nearbyPlaces"
        const val F_IMAGES = "images"
        const val F_PLACE_NAME = "name"
        const val F_PLACE_DISTANCE = "distanceKm"
        const val F_PLACE_TYPE = "type"
    }

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveDraft(form: PropertyForm, stepName: String) {
        runCatching {
            preferences.edit { putString(KEY_DRAFT, toJson(form, stepName).toString()) }
        }.onFailure { Logger.e(TAG, "Failed to save draft", it) }
    }

    fun loadDraft(): PropertyDraft? {
        val raw = preferences.getString(KEY_DRAFT, null) ?: return null
        return runCatching { fromJson(JSONObject(raw)) }
            .onFailure { Logger.e(TAG, "Failed to read draft, clearing", it) }
            .getOrNull()
            ?: run {
                clearDraft()
                null
            }
    }

    fun hasDraft(): Boolean = preferences.contains(KEY_DRAFT)

    fun clearDraft() {
        preferences.edit { remove(KEY_DRAFT) }
    }

    private fun toJson(form: PropertyForm, stepName: String): JSONObject {
        return JSONObject().apply {
            put(F_STEP, stepName)
            put(F_RENT_BUY, form.rentBuy?.name)
            put(F_RESIDENTIAL_COMMERCIAL, form.residentialCommercial?.name)
            put(F_PROPERTY_TYPE, form.propertyType?.name)
            put(F_BEDROOM_TYPE, form.bedroomType?.name)
            put(F_TITLE, form.title)
            put(F_DESCRIPTION, form.description)
            put(F_PRICE, form.price)
            put(F_CITY, form.city)
            put(F_LOCALITY, form.locality)
            put(F_PINCODE, form.pincode)
            put(F_ADDRESS, form.address)
            put(F_LATITUDE, form.latitude)
            put(F_LONGITUDE, form.longitude)
            put(F_FURNISHING, form.furnishing?.name)
            put(F_FACING, form.facing?.name)
            put(F_AGE, form.age?.name)
            put(F_AMENITIES, JSONArray().apply { form.amenities.forEach { put(it.name) } })
            put(F_CARPET_AREA, form.carpetArea)
            put(F_BUILT_UP_AREA, form.builtUpArea)
            put(F_SUPER_BUILT_UP_AREA, form.superBuiltUpArea)
            put(F_BATHROOMS, form.bathrooms)
            put(F_AGENT_PHONE, form.agentPhone)
            put(F_LISTING_CATEGORY, form.listingCategory.name)
            put(F_NEARBY_PLACES, JSONArray().apply {
                form.nearbyPlaces.forEach { place ->
                    put(JSONObject().apply {
                        put(F_PLACE_NAME, place.name)
                        put(F_PLACE_DISTANCE, place.distanceKm)
                        put(F_PLACE_TYPE, place.type.name)
                    })
                }
            })
            put(F_IMAGES, JSONArray().apply { form.images.forEach { put(it) } })
        }
    }

    private fun fromJson(json: JSONObject): PropertyDraft {
        val form = PropertyForm(
            rentBuy = enumOrNull<RentBuy>(json.optStringOrNull(F_RENT_BUY)),
            residentialCommercial = enumOrNull<ResidentialCommercial>(json.optStringOrNull(F_RESIDENTIAL_COMMERCIAL)),
            propertyType = enumOrNull<PropertyType>(json.optStringOrNull(F_PROPERTY_TYPE)),
            bedroomType = enumOrNull<BedroomType>(json.optStringOrNull(F_BEDROOM_TYPE)),
            title = json.optString(F_TITLE),
            description = json.optString(F_DESCRIPTION),
            price = json.optString(F_PRICE),
            city = json.optString(F_CITY),
            locality = json.optString(F_LOCALITY),
            pincode = json.optString(F_PINCODE),
            address = json.optString(F_ADDRESS),
            latitude = json.optString(F_LATITUDE),
            longitude = json.optString(F_LONGITUDE),
            furnishing = enumOrNull<Furnishing>(json.optStringOrNull(F_FURNISHING)),
            facing = enumOrNull<Facing>(json.optStringOrNull(F_FACING)),
            age = enumOrNull<Age>(json.optStringOrNull(F_AGE)),
            amenities = json.optJSONArray(F_AMENITIES).toStringList()
                .mapNotNull { enumOrNull<Amenity>(it) },
            carpetArea = json.optString(F_CARPET_AREA),
            builtUpArea = json.optString(F_BUILT_UP_AREA),
            superBuiltUpArea = json.optString(F_SUPER_BUILT_UP_AREA),
            bathrooms = json.optInt(F_BATHROOMS),
            agentPhone = json.optString(F_AGENT_PHONE),
            listingCategory = enumOrNull<ListingCategory>(json.optStringOrNull(F_LISTING_CATEGORY))
                ?: ListingCategory.NORMAL,
            nearbyPlaces = json.optJSONArray(F_NEARBY_PLACES).toNearbyPlaces(),
            images = json.optJSONArray(F_IMAGES).toStringList()
        )
        return PropertyDraft(form = form, stepName = json.optString(F_STEP))
    }

    private fun JSONObject.optStringOrNull(key: String): String? {
        return if (has(key) && !isNull(key)) optString(key) else null
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return (0 until length()).mapNotNull { optString(it, null) }
    }

    private fun JSONArray?.toNearbyPlaces(): List<NearbyPlace> {
        if (this == null) return emptyList()
        return (0 until length()).mapNotNull { index ->
            optJSONObject(index)?.let { place ->
                NearbyPlace(
                    name = place.optString(F_PLACE_NAME),
                    distanceKm = place.optDouble(F_PLACE_DISTANCE),
                    type = enumOrNull<NearbyPlaceType>(place.optStringOrNull(F_PLACE_TYPE))
                        ?: NearbyPlaceType.OTHER
                )
            }
        }
    }

    private inline fun <reified T : Enum<T>> enumOrNull(name: String?): T? {
        return name?.let { runCatching { enumValueOf<T>(it) }.getOrNull() }
    }
}
