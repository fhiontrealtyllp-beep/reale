package com.example.mytestapp.feature.add.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytestapp.feature.add.domain.model.PropertyForm
import com.example.mytestapp.feature.add.domain.usecase.AddPropertyUseCase
import com.example.mytestapp.feature.search.data.session.UserSession
import com.example.mytestapp.feature.search.domain.model.Age
import com.example.mytestapp.feature.search.domain.model.Amenity
import com.example.mytestapp.feature.search.domain.model.BedroomType
import com.example.mytestapp.feature.search.domain.model.Facing
import com.example.mytestapp.feature.search.domain.model.Furnishing
import com.example.mytestapp.feature.search.domain.model.PropertyType
import com.example.mytestapp.feature.search.domain.model.RentBuy
import com.example.mytestapp.feature.search.domain.model.ResidentialCommercial
import com.example.mytestapp.feature.search.domain.utils.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddViewModel(
    private val addPropertyUseCase: AddPropertyUseCase,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<String>()
    val sideEffect: SharedFlow<String> = _sideEffect.asSharedFlow()

    init {
        load()
    }

    fun load() {
        val userId = userSession.getUserId()
        _uiState.value = AddUiState(
            isLoading = false,
            isLoggedIn = !userId.isNullOrEmpty(),
            form = PropertyForm(),
            fieldErrors = emptyList(),
            isSubmitting = false,
            isSubmitSuccess = false,
            successMessage = null,
            errorMessage = null
        )
    }

    fun refresh() {
        load()
    }

    fun onRentBuyChanged(rentBuy: RentBuy) {
        updateForm { copy(rentBuy = rentBuy) }
    }

    fun onResidentialCommercialChanged(residentialCommercial: ResidentialCommercial) {
        updateForm {
            copy(
                residentialCommercial = residentialCommercial,
                propertyType = null
            )
        }
    }

    fun onPropertyTypeChanged(propertyType: PropertyType) {
        updateForm { copy(propertyType = propertyType) }
    }

    fun onBedroomTypeChanged(bedroomType: BedroomType?) {
        updateForm { copy(bedroomType = bedroomType) }
    }

    fun onTitleChanged(title: String) {
        updateForm { copy(title = title) }
    }

    fun onDescriptionChanged(description: String) {
        updateForm { copy(description = description) }
    }

    fun onPriceChanged(price: String) {
        updateForm { copy(price = price) }
    }

    fun onCityChanged(city: String) {
        updateForm { copy(city = city) }
    }

    fun onLocalityChanged(locality: String) {
        updateForm { copy(locality = locality) }
    }

    fun onPincodeChanged(pincode: String) {
        updateForm { copy(pincode = pincode) }
    }

    fun onAddressChanged(address: String) {
        updateForm { copy(address = address) }
    }

    fun onFurnishingChanged(furnishing: Furnishing?) {
        updateForm { copy(furnishing = furnishing) }
    }

    fun onFacingChanged(facing: Facing?) {
        updateForm { copy(facing = facing) }
    }

    fun onAgeChanged(age: Age?) {
        updateForm { copy(age = age) }
    }

    fun onAmenitiesChanged(amenities: List<Amenity>) {
        updateForm { copy(amenities = amenities) }
    }

    fun onCarpetAreaChanged(carpetArea: String) {
        updateForm { copy(carpetArea = carpetArea) }
    }

    fun onBuiltUpAreaChanged(builtUpArea: String) {
        updateForm { copy(builtUpArea = builtUpArea) }
    }

    fun onSuperBuiltUpAreaChanged(superBuiltUpArea: String) {
        updateForm { copy(superBuiltUpArea = superBuiltUpArea) }
    }

    fun onAgentPhoneChanged(agentPhone: String) {
        updateForm { copy(agentPhone = agentPhone) }
    }

    fun onImagesChanged(images: List<String>) {
        updateForm { copy(images = images) }
    }

    fun onLocationPicked(
        latitude: String,
        longitude: String,
        city: String? = null,
        locality: String? = null,
        address: String? = null
    ) {
        updateForm {
            copy(
                latitude = latitude,
                longitude = longitude,
                city = if (!city.isNullOrBlank()) city.trim() else this.city,
                locality = if (!locality.isNullOrBlank()) locality.trim() else this.locality,
                address = if (!address.isNullOrBlank()) address.trim() else this.address
            )
        }
    }

    fun submit() {
        val current = _uiState.value
        val userId = userSession.getUserId().orEmpty()

        if (userId.isEmpty()) {
            _uiState.value = current.copy(
                isLoggedIn = false,
                errorMessage = "Please log in to add a property"
            )
            return
        }

        val form = current.form
        val validationErrors = form.validate()
        if (validationErrors.isNotEmpty()) {
            _uiState.value = current.copy(fieldErrors = validationErrors)
            return
        }

        _uiState.value = current.copy(
            isSubmitting = true,
            fieldErrors = emptyList(),
            errorMessage = null,
            successMessage = null
        )

        viewModelScope.launch {
            when (val result = addPropertyUseCase(userId, form)) {
                is Result.Success -> {
                    _uiState.value = AddUiState(
                        isLoading = false,
                        isLoggedIn = true,
                        isSubmitting = false,
                        isSubmitSuccess = true,
                        successMessage = "Property added successfully",
                        form = PropertyForm()
                    )
                    _sideEffect.emit("Property added successfully")
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        errorMessage = result.message
                    )
                    _sideEffect.emit(result.message)
                }
            }
        }
    }

    fun onMessageShown() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    fun onDismissSuccess() {
        _uiState.value = _uiState.value.copy(isSubmitSuccess = false)
    }

    private inline fun updateForm(transform: PropertyForm.() -> PropertyForm) {
        val current = _uiState.value
        _uiState.value = current.copy(
            form = current.form.transform(),
            fieldErrors = emptyList(),
            errorMessage = null
        )
    }
}
