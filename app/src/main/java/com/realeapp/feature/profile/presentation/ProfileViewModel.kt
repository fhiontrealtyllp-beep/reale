package com.realeapp.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.profile.domain.usecase.GetUserDetailsUseCase
import com.realeapp.feature.profile.domain.usecase.LogoutUseCase
import com.realeapp.feature.profile.domain.usecase.UpdateProfileUseCase
import com.realeapp.feature.profile.domain.usecase.UploadImageUseCase
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.utils.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<String>()
    val sideEffect: SharedFlow<String> = _sideEffect.asSharedFlow()

    init {
        load()
        viewModelScope.launch {
            var wasLoggedIn = userSession.user.value != null
            userSession.user.collect { user ->
                val isLoggedIn = user != null
                if (isLoggedIn != wasLoggedIn) {
                    wasLoggedIn = isLoggedIn
                    if (isLoggedIn) {
                        load()
                    } else {
                        _uiState.value = ProfileUiState(isLoading = false, isLoggedIn = false)
                    }
                }
            }
        }
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
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, updateSuccessMessage = null)

        viewModelScope.launch {
            when (val result = updateProfileUseCase(currentUser.id, field, value)) {
                is Result.Success -> {
                    val refreshed = userSession.getUser()
                    _uiState.value = _uiState.value.copy(
                        user = refreshed,
                        isLoading = false,
                        updateSuccessMessage = result.data
                    )
                    _sideEffect.emit(result.data)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                    _sideEffect.emit(result.message)
                }
            }
        }
    }

    fun uploadImage(bytes: ByteArray, filename: String) {
        val currentUser = _uiState.value.user ?: return
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            when (val uploadResult = uploadImageUseCase(bytes, filename)) {
                is Result.Success -> {
                    when (val updateResult = updateProfileUseCase(currentUser.id, "image", uploadResult.data)) {
                        is Result.Success -> {
                            val refreshed = userSession.getUser()
                            _uiState.value = _uiState.value.copy(
                                user = refreshed,
                                isLoading = false,
                                updateSuccessMessage = "Profile image updated"
                            )
                            _sideEffect.emit("Profile image updated")
                        }
                        is Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = updateResult.message
                            )
                            _sideEffect.emit(updateResult.message)
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = uploadResult.message
                    )
                    _sideEffect.emit(uploadResult.message)
                }
            }
        }
    }

    fun logout() {
        val currentUser = userSession.getUser() ?: return
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = logoutUseCase(currentUser.sessionId)) {
                is Result.Success -> {
                    _uiState.value = ProfileUiState(isLoading = false, isLoggedIn = false)
                    _sideEffect.emit("Logged out successfully")
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _sideEffect.emit(result.message)
                }
            }
        }
    }

    fun getCurrentUser(): User? = _uiState.value.user
}
