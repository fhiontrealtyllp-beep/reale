package com.realeapp.feature.add.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.add.domain.usecase.AddPropertyUseCase
import com.realeapp.feature.add.domain.usecase.GetMyPropertiesUseCase
import com.realeapp.feature.add.domain.usecase.UploadImageUseCase
import com.realeapp.feature.search.data.session.SessionObserver
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.model.Age
import com.realeapp.feature.search.domain.model.Amenity
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Facing
import com.realeapp.feature.search.domain.model.Furnishing
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.domain.model.ResidentialCommercial
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
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
                    if (form.title.isBlank()) add("Property title is required")
                    if (form.propertyType == null) add("Property type is required")
                    if (form.rentBuy == null) add("Listing type is required")
                    if (form.city.isBlank()) add("City is required")
                    if (form.locality.isBlank()) add("Locality is required")
                }
            }
            AddPropertyStep.PROPERTY_DETAILS -> emptyList()
            AddPropertyStep.PHOTOS_MEDIA -> emptyList()
            AddPropertyStep.PRICING -> {
                buildList {
                    if (form.price.isBlank()) add("Price is required")
                    else if (form.price.toDoubleOrNull() == null) add("Price must be a valid number")
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

    fun onBedroomTypeChanged(bedroomType: BedroomType?) {
        updateForm { copy(bedroomType = bedroomType) }
    }

    // Stepper count -> BedroomType mapping used by the Step 2 bedrooms stepper.
    fun onBedroomCountChanged(count: Int) {
        val bedroomType = when (count) {
            1 -> BedroomType.ONE_BHK
            2 -> BedroomType.TWO_BHK
            3 -> BedroomType.THREE_BHK
            4 -> BedroomType.FOUR_BHK
            5 -> BedroomType.FIVE_BHK
            6 -> BedroomType.SIX_BHK
            7 -> BedroomType.SIX_PLUS_BHK
            else -> null
        }
        updateForm { copy(bedroomType = bedroomType) }
    }

    fun onBathroomsChanged(bathrooms: Int) {
        updateForm { copy(bathrooms = bathrooms.coerceIn(0, 10)) }
    }

    fun onFloorNoChanged(floorNo: String) {
        updateForm { copy(floorNo = floorNo) }
    }

    fun onTotalFloorsChanged(totalFloors: String) {
        updateForm { copy(totalFloors = totalFloors) }
    }

    fun onPlotAreaChanged(plotArea: String) {
        updateForm { copy(plotArea = plotArea) }
    }

    fun onVideoChanged(videoUrl: String) {
        updateForm { copy(videoUrl = videoUrl) }
    }

    fun onPriceModeChanged(pricePerSqFt: Boolean) {
        updateForm { copy(pricePerSqFt = pricePerSqFt) }
    }

    fun onNegotiableChanged(negotiable: Boolean) {
        updateForm { copy(negotiable = negotiable) }
    }

    fun onAdditionalCostsChanged(additionalCosts: String) {
        updateForm { copy(additionalCosts = additionalCosts) }
    }

    fun onPropertyStatusChanged(propertyStatus: String) {
        updateForm { copy(propertyStatus = propertyStatus) }
    }

    fun onPossessionDateChanged(possessionDate: String) {
        updateForm { copy(possessionDate = possessionDate) }
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
                    val newProperty = form.toProperty(result.data, userId)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        isSubmitting = false,
                        isSubmitSuccess = true,
                        submittedProperty = newProperty,
                        isShowingAddForm = false,
                        successMessage = "Property added successfully",
                        form = PropertyForm(),
                        errorMessage = null,
                        fieldErrors = emptyList(),
                        myProperties = listOf(newProperty) + _uiState.value.myProperties
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
        _uiState.value = _uiState.value.copy(
            isSubmitSuccess = false,
            submittedProperty = null
        )
    }

    private fun PropertyForm.toProperty(documentId: String, userId: String): Property {
        return Property(
            id = documentId,
            documentId = documentId,
            userId = userId,
            title = title.trim(),
            description = description.trim(),
            price = price.toDoubleOrNull() ?: 0.0,
            city = city.trim(),
            locality = locality.trim(),
            pincode = pincode.trim().ifBlank { null },
            address = address.trim().ifBlank { null },
            latitude = latitude.toDoubleOrNull(),
            longitude = longitude.toDoubleOrNull(),
            images = images.map { it.trim() }.filter { it.isNotBlank() },
            agentPhone = agentPhone.trim(),
            status = "live",
            createdAt = currentTimestamp(),
            rentBuy = rentBuy,
            residentialCommercial = residentialCommercial,
            propertyType = propertyType,
            bedroomType = bedroomType,
            furnishing = furnishing,
            facing = facing,
            age = age,
            amenities = amenities,
            carpetArea = carpetArea.toDoubleOrNull(),
            builtUpArea = builtUpArea.toDoubleOrNull(),
            superBuiltUpArea = superBuiltUpArea.toDoubleOrNull()
        )
    }

    private fun currentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
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
