package com.realeapp.feature.add.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.feature.add.data.mapper.toProperty
import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.add.domain.usecase.AddPropertyUseCase
import com.realeapp.feature.add.domain.usecase.GetMyPropertiesUseCase
import com.realeapp.feature.add.domain.usecase.UploadImageUseCase
import com.realeapp.feature.search.data.session.SessionObserver
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.model.Age
import com.realeapp.feature.search.domain.model.Amenity
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.ListingCategory
import com.realeapp.feature.search.domain.model.NearbyPlace
import com.realeapp.feature.search.domain.model.Facing
import com.realeapp.feature.search.domain.model.Furnishing
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.domain.model.ResidentialCommercial
import com.realeapp.feature.search.domain.utils.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddViewModel(
    private val addPropertyUseCase: AddPropertyUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val getMyPropertiesUseCase: GetMyPropertiesUseCase,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<String>()
    val sideEffect: SharedFlow<String> = _sideEffect.asSharedFlow()

    init {
        load()
        SessionObserver(
            userSession = userSession,
            scope = viewModelScope,
            onLogin = { load() },
            onLogout = {
                _uiState.value = AddUiState(isLoading = false, isLoggedIn = false)
            }
        )
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
        if (!userId.isNullOrEmpty()) {
            loadMyProperties(userId)
        }
    }

    fun refresh() {
        load()
    }

    private fun loadMyProperties(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingMyProperties = true,
                myPropertiesError = null
            )
            when (val result = getMyPropertiesUseCase(userId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        myProperties = result.data,
                        isLoadingMyProperties = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        myPropertiesError = result.message,
                        isLoadingMyProperties = false
                    )
                }
            }
        }
    }

    fun onShowAddForm() {
        _uiState.value = _uiState.value.copy(
            isShowingAddForm = true,
            currentStep = AddPropertyStep.BASIC_DETAILS,
            form = PropertyForm(),
            fieldErrors = emptyList(),
            errorMessage = null,
            successMessage = null,
            isSubmitSuccess = false,
            submittedProperty = null
        )
    }

    fun onHideAddForm() {
        _uiState.value = _uiState.value.copy(
            isShowingAddForm = false,
            currentStep = AddPropertyStep.BASIC_DETAILS,
            form = PropertyForm(),
            fieldErrors = emptyList(),
            errorMessage = null,
            successMessage = null
        )
    }

    fun goToStep(step: AddPropertyStep) {
        _uiState.value = _uiState.value.copy(
            currentStep = step,
            fieldErrors = emptyList(),
            errorMessage = null
        )
    }

    /**
     * Step-indicator tap: backward navigation is free; forward navigation
     * validates each intermediate step and stops at the first failing one.
     */
    fun onStepClicked(target: AddPropertyStep) {
        val current = _uiState.value
        if (target == current.currentStep) return
        if (target.index < current.currentStep.index) {
            goToStep(target)
            return
        }
        for (i in current.currentStep.index until target.index) {
            val step = AddPropertyStep.fromIndex(i)
            val errors = validateStep(step)
            if (errors.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(
                    currentStep = step,
                    fieldErrors = errors,
                    errorMessage = null
                )
                return
            }
        }
        goToStep(target)
    }

    fun nextStep() {
        val current = _uiState.value
        val step = current.currentStep
        val errors = validateStep(step)
        if (errors.isNotEmpty()) {
            _uiState.value = current.copy(fieldErrors = errors)
            return
        }
        val next = AddPropertyStep.fromIndex(step.index + 1)
        _uiState.value = current.copy(
            currentStep = next,
            fieldErrors = emptyList(),
            errorMessage = null
        )
    }

    fun previousStep() {
        val current = _uiState.value
        val step = current.currentStep
        val previous = if (step.isFirst) step else AddPropertyStep.fromIndex(step.index - 1)
        _uiState.value = current.copy(
            currentStep = previous,
            fieldErrors = emptyList(),
            errorMessage = null
        )
    }

    private fun validateStep(step: AddPropertyStep): List<String> {
        val form = _uiState.value.form
        return when (step) {
            AddPropertyStep.BASIC_DETAILS -> {
                buildList {
                    if (form.title.isBlank()) add(AddStrings.ERR_TITLE_REQUIRED)
                    if (form.propertyType == null) add(AddStrings.ERR_PROPERTY_TYPE_REQUIRED)
                    if (form.rentBuy == null) add(AddStrings.ERR_LISTING_TYPE_REQUIRED)
                    if (form.city.isBlank()) add(AddStrings.ERR_CITY_REQUIRED)
                    if (form.locality.isBlank()) add(AddStrings.ERR_LOCALITY_REQUIRED)
                }
            }
            AddPropertyStep.PROPERTY_DETAILS -> emptyList()
            AddPropertyStep.PHOTOS_MEDIA -> emptyList()
            AddPropertyStep.PRICING -> {
                buildList {
                    if (form.price.isBlank()) add(AddStrings.ERR_PRICE_REQUIRED)
                    else if (form.price.toDoubleOrNull() == null) add(AddStrings.ERR_PRICE_INVALID)
                }
            }
            AddPropertyStep.REVIEW_PUBLISH -> form.validate()
        }
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
        updateForm {
            copy(
                propertyType = propertyType,
                residentialCommercial = propertyType.category
            )
        }
    }

    fun onBathroomsChanged(bathrooms: Int) {
        updateForm { copy(bathrooms = bathrooms.coerceIn(0, 10)) }
    }

    fun onBedroomCountChanged(count: Int) {
        val coerced = count.coerceIn(0, 7)
        val bedroomType = when (coerced) {
            1 -> BedroomType.ONE_BHK
            2 -> BedroomType.TWO_BHK
            3 -> BedroomType.THREE_BHK
            4 -> BedroomType.FOUR_BHK
            5 -> BedroomType.FIVE_BHK
            6 -> BedroomType.SIX_BHK
            7 -> BedroomType.SIX_PLUS_BHK
            else -> null
        }
        onBedroomTypeChanged(bedroomType)
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

    fun onListingCategoryChanged(listingCategory: ListingCategory) {
        updateForm { copy(listingCategory = listingCategory) }
    }

    fun onNearbyPlacesChanged(nearbyPlaces: List<NearbyPlace>) {
        updateForm { copy(nearbyPlaces = nearbyPlaces) }
    }

    fun onImagesChanged(images: List<String>) {
        updateForm { copy(images = images) }
    }

    fun uploadImage(bytes: ByteArray, filename: String) {
        uploadImages(listOf(bytes to filename))
    }

    fun uploadImages(imagesToUpload: List<Pair<ByteArray, String>>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploadingImage = true,
                imageUploadError = null
            )

            for ((bytes, filename) in imagesToUpload) {
                when (val result = uploadImageUseCase(bytes, filename)) {
                    is Result.Success -> addImageUrl(result.data)
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(imageUploadError = result.message)
                        break
                    }
                }
            }

            _uiState.value = _uiState.value.copy(isUploadingImage = false)
        }
    }

    fun addImageUrl(url: String) {
        val trimmed = url.trim()
        if (trimmed.isNotBlank()) {
            updateForm {
                val current = images.toMutableSet()
                if (current.add(trimmed)) {
                    copy(images = current.toList())
                } else this
            }
        }
    }

    fun removeImageUrl(url: String) {
        updateForm {
            copy(images = images.filterNot { it == url })
        }
    }

    fun onLocationPicked(
        latitude: String,
        longitude: String,
        city: String? = null,
        locality: String? = null,
        pincode: String? = null,
        address: String? = null
    ) {
        updateForm {
            copy(
                latitude = latitude,
                longitude = longitude,
                city = if (!city.isNullOrBlank()) city.trim() else this.city,
                locality = if (!locality.isNullOrBlank()) locality.trim() else this.locality,
                pincode = if (!pincode.isNullOrBlank()) pincode.trim() else this.pincode,
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
                errorMessage = AddStrings.ERR_LOGIN_REQUIRED
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
                    val newProperty = form.toProperty(result.data, userId)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        isSubmitting = false,
                        isSubmitSuccess = true,
                        submittedProperty = newProperty,
                        isShowingAddForm = false,
                        successMessage = AddStrings.MSG_PROPERTY_ADDED,
                        form = PropertyForm(),
                        errorMessage = null,
                        fieldErrors = emptyList(),
                        myProperties = listOf(newProperty) + _uiState.value.myProperties
                    )
                    _sideEffect.emit(AddStrings.MSG_PROPERTY_ADDED)
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
        _uiState.value = _uiState.value.copy(
            isSubmitSuccess = false,
            submittedProperty = null
        )
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
