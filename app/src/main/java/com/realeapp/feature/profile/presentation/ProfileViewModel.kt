package com.realeapp.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.core.theme.ThemeMode
import com.realeapp.core.theme.ThemePreferences
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.onboarding.domain.repository.OnboardingRepository
import com.realeapp.feature.profile.domain.usecase.GetUserDetailsUseCase
import com.realeapp.feature.profile.domain.usecase.LogoutUseCase
import com.realeapp.feature.profile.domain.usecase.UpdateProfileUseCase
import com.realeapp.feature.profile.domain.usecase.UploadImageUseCase
import com.realeapp.feature.search.data.session.SessionObserver
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ProfileViewModel"

class ProfileViewModel(
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val userSession: UserSession,
    private val onboardingRepository: OnboardingRepository,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<String>()
    val sideEffect: SharedFlow<String> = _sideEffect.asSharedFlow()

    val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode

    // Locally cached address used when there is no logged-in user.
    val savedAddress: StateFlow<String> = onboardingRepository.address

    // City and location cached from the onboarding city picker.
    val savedCity: StateFlow<String> = onboardingRepository.city
    val savedLocation: StateFlow<String> = onboardingRepository.location

    init {
        load()
        Logger.d(
            TAG,
            "init: savedAddress='${savedAddress.value}', " +
                "savedCity='${savedCity.value}', savedLocation='${savedLocation.value}'"
        )
        SessionObserver(
            userSession = userSession,
            scope = viewModelScope,
            onLogin = {
                Logger.d(TAG, "SessionObserver.onLogin: reloading profile")
                load()
            },
            onLogout = {
                Logger.d(TAG, "SessionObserver.onLogout: clearing profile state")
                _uiState.value = ProfileUiState(isLoading = false, isLoggedIn = false)
            }
        )
    }

    fun load() {
        val currentUser = userSession.getUser()
        if (currentUser == null) {
            _uiState.value = ProfileUiState(isLoading = false, isLoggedIn = false)
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = getUserDetailsUseCase()) {
                is Result.Success -> {
                    userSession.setUser(result.data)
                    _uiState.value = ProfileUiState(
                        user = result.data,
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun refresh() {
        load()
    }

    fun updateProfileField(field: String, value: String) {
        val currentUser = _uiState.value.user ?: return
        _uiState.value = _uiState.value.copy(
            updatingField = field,
            errorMessage = null,
            updateSuccessMessage = null
        )

        viewModelScope.launch {
            when (val result = updateProfileUseCase(currentUser.id, field, value)) {
                is Result.Success -> {
                    val updated = currentUser.withUpdatedField(field, value)
                    userSession.setUser(updated)
                    _uiState.value = _uiState.value.copy(
                        user = updated,
                        isLoading = false,
                        updatingField = null,
                        updateSuccessMessage = result.data
                    )
                    _sideEffect.emit(result.data)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        updatingField = null,
                        errorMessage = result.message
                    )
                    _sideEffect.emit(result.message)
                }
            }
        }
    }

    fun uploadImage(bytes: ByteArray, filename: String) {
        val currentUser = _uiState.value.user ?: return
        _uiState.value = _uiState.value.copy(isImageUploading = true, errorMessage = null)

        viewModelScope.launch {
            when (val uploadResult = uploadImageUseCase(bytes, filename)) {
                is Result.Success -> {
                    when (val updateResult = updateProfileUseCase(currentUser.id, ProfileStrings.FIELD_IMAGE, uploadResult.data)) {
                        is Result.Success -> {
                            val updated = currentUser.copy(image = uploadResult.data)
                            userSession.setUser(updated)
                            _uiState.value = _uiState.value.copy(
                                user = updated,
                                isImageUploading = false,
                                updateSuccessMessage = ProfileStrings.MSG_IMAGE_UPDATED
                            )
                            _sideEffect.emit(ProfileStrings.MSG_IMAGE_UPDATED)
                        }
                        is Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isImageUploading = false,
                                errorMessage = updateResult.message
                            )
                            _sideEffect.emit(updateResult.message)
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isImageUploading = false,
                        errorMessage = uploadResult.message
                    )
                    _sideEffect.emit(uploadResult.message)
                }
            }
        }
    }

    fun logout() {
        val currentUser = userSession.getUser()
        if (currentUser == null) {
            Logger.w(TAG, "logout() ignored: no current user")
            return
        }
        Logger.d(TAG, "logout() invoked from UI: userId=${currentUser.id}")
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = logoutUseCase(currentUser.sessionId)) {
                is Result.Success -> {
                    Logger.d(TAG, "logout() success: userId=${currentUser.id}")
                    _uiState.value = ProfileUiState(isLoading = false, isLoggedIn = false)
                    _sideEffect.emit(ProfileStrings.MSG_LOGGED_OUT)
                }
                is Result.Error -> {
                    Logger.e(TAG, "logout() error: ${result.message}")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _sideEffect.emit(result.message)
                }
            }
        }
    }

    fun saveAddress(address: String) {
        if (_uiState.value.isLoggedIn && _uiState.value.user != null) {
            updateProfileField(ProfileStrings.FIELD_ADDRESS, address)
        } else {
            onboardingRepository.setAddress(address)
            viewModelScope.launch {
                _sideEffect.emit(ProfileStrings.MSG_ADDRESS_SAVED)
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        themePreferences.setThemeMode(mode)
    }

    fun getCurrentUser(): User? = _uiState.value.user
}

private fun User.withUpdatedField(field: String, value: String): User = when (field) {
    ProfileStrings.FIELD_NAME -> copy(name = value)
    ProfileStrings.FIELD_EMAIL -> copy(email = value)
    ProfileStrings.FIELD_PHONE -> copy(phone = value)
    ProfileStrings.FIELD_ADDRESS -> copy(address = value)
    ProfileStrings.FIELD_IMAGE -> copy(image = value)
    else -> this
}
