package com.fhiont.feature.add.domain.model

import com.fhiont.feature.search.domain.model.PropertyType

data class PropertyFormConfig(
    val showBedrooms: Boolean = true,
    val showBathrooms: Boolean = true,
    val showFurnishing: Boolean = true,
    val showFacing: Boolean = true,
    val showAge: Boolean = true,
    val showAmenities: Boolean = true,
    val showCarpetArea: Boolean = true,
    val showBuiltUpArea: Boolean = true,
    val showSuperBuiltUpArea: Boolean = true,
    val showTotalRooms: Boolean = false,
    val showSharingType: Boolean = false,
    val showPreferredTenant: Boolean = false,
    val showFoodAvailable: Boolean = false,
    val bedroomsRequired: Boolean = true,
    val bathroomsRequired: Boolean = true,
    val furnishingRequired: Boolean = true,
    val facingRequired: Boolean = true,
    val ageRequired: Boolean = true,
    val areaRequired: Boolean = true,
    val totalRoomsRequired: Boolean = false,
    val sharingTypeRequired: Boolean = false,
    val preferredTenantRequired: Boolean = false,
    val foodAvailableRequired: Boolean = false
)

fun PropertyType.formConfig(): PropertyFormConfig = when (this) {
    PropertyType.PLOT,
    PropertyType.LAND -> PropertyFormConfig(
        showBedrooms = false,
        showBathrooms = false,
        showFurnishing = false,
        showFacing = false,
        showAge = false,
        showAmenities = false,
        showCarpetArea = false,
        showBuiltUpArea = true,
        showSuperBuiltUpArea = false,
        bedroomsRequired = false,
        bathroomsRequired = false,
        furnishingRequired = false,
        facingRequired = false,
        ageRequired = false,
        areaRequired = true
    )
    PropertyType.PAYING_GUEST,
    PropertyType.HOSTEL -> PropertyFormConfig(
        showBedrooms = false,
        showBathrooms = true,
        showFurnishing = true,
        showFacing = false,
        showAge = true,
        showAmenities = true,
        showCarpetArea = true,
        showBuiltUpArea = true,
        showSuperBuiltUpArea = true,
        showTotalRooms = true,
        showSharingType = true,
        showPreferredTenant = true,
        showFoodAvailable = true,
        bedroomsRequired = false,
        bathroomsRequired = true,
        furnishingRequired = false,
        facingRequired = false,
        ageRequired = true,
        areaRequired = true,
        totalRoomsRequired = true,
        sharingTypeRequired = true,
        preferredTenantRequired = true,
        foodAvailableRequired = false
    )
    PropertyType.GUEST_HOUSE -> PropertyFormConfig(
        showBedrooms = false,
        showBathrooms = true,
        showFurnishing = true,
        showFacing = false,
        showAge = true,
        showAmenities = true,
        showCarpetArea = true,
        showBuiltUpArea = true,
        showSuperBuiltUpArea = true,
        showTotalRooms = true,
        showSharingType = false,
        showPreferredTenant = true,
        showFoodAvailable = true,
        bedroomsRequired = false,
        bathroomsRequired = true,
        furnishingRequired = false,
        facingRequired = false,
        ageRequired = true,
        areaRequired = true,
        totalRoomsRequired = true,
        sharingTypeRequired = false,
        preferredTenantRequired = true,
        foodAvailableRequired = false
    )
    else -> PropertyFormConfig()
}
