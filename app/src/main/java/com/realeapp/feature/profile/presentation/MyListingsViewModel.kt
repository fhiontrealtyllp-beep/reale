package com.realeapp.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.feature.add.domain.usecase.GetMyPropertiesUseCase
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.usecase.GetEnquiryCountsForPropertiesUseCase
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class MyListingsViewModel(
    private val getMyPropertiesUseCase: GetMyPropertiesUseCase,
    private val getEnquiryCountsForPropertiesUseCase: GetEnquiryCountsForPropertiesUseCase,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyListingsUiState())
    val uiState: StateFlow<MyListingsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        val userId = userSession.getUserId()
        if (userId.isNullOrBlank()) {
            _uiState.value = MyListingsUiState(
                isLoading = false,
                errorMessage = MyListingsStrings.ERROR_NOT_LOGGED_IN
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val propertiesResult = getMyPropertiesUseCase(userId)
            when (propertiesResult) {
                is Result.Success -> {
                    val properties = propertiesResult.data
                    val counts = if (properties.isNotEmpty()) {
                        when (val countsResult = getEnquiryCountsForPropertiesUseCase(properties.map { it.id })) {
                            is Result.Success -> countsResult.data
                            is Result.Error -> emptyMap()
                        }
                    } else {
                        emptyMap()
                    }
                    _uiState.value = _uiState.value.copy(
                        listings = properties.map { it.toMyListing(counts[it.id] ?: 0) },
                        properties = properties,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = propertiesResult.message ?: MyListingsStrings.ERROR_LOADING
                    )
                }
            }
        }
    }

    private fun Property.toMyListing(enquiryCount: Int): MyListing {
        return MyListing(
            id = id,
            imageUrl = images.firstOrNull().orEmpty(),
            title = title,
            location = buildShortLocation(),
            type = propertyType?.label ?: residentialCommercial?.label ?: "",
            price = price,
            beds = bedroomType?.toBeds(),
            baths = bathrooms,
            sqft = carpetArea?.toInt() ?: builtUpArea?.toInt() ?: superBuiltUpArea?.toInt() ?: 0,
            views = 0,
            enquiries = enquiryCount,
            shortlisted = 0,
            photoCount = images.size,
            status = status.toListingStatus()
        )
    }

    private fun Property.buildShortLocation(): String {
        return listOfNotNull(locality, city)
            .filter { it.isNotBlank() }
            .joinToString(", ")
    }

    private fun BedroomType.toBeds(): Int = when (this) {
        BedroomType.ONE_RK -> 1
        BedroomType.ONE_BHK -> 1
        BedroomType.TWO_BHK -> 2
        BedroomType.THREE_BHK -> 3
        BedroomType.FOUR_BHK -> 4
        BedroomType.FIVE_BHK -> 5
        BedroomType.SIX_BHK -> 6
        BedroomType.SIX_PLUS_BHK -> 6
        BedroomType.STUDIO_APARTMENT -> 1
    }

    private fun String?.toListingStatus(): ListingStatus = when (this) {
        "live" -> ListingStatus.ACTIVE
        else -> ListingStatus.INACTIVE
    }
}
