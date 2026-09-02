package com.realeapp.feature.auth.presentation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.feature.auth.domain.usecase.RegisterUseCase
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

private const val TAG = "RegisterViewModel"

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sideEffect: SharedFlow<Unit> = _sideEffect.asSharedFlow()

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(name = name, errorMessage = null)
    }

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun onTogglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun register() {
        val current = _uiState.value
        val name = current.name.trim()
        val email = current.email.trim()
        val password = current.password
        val confirmPassword = current.confirmPassword

        when {
            name.isEmpty() -> {
                Logger.w(TAG, "register() validation failed: name is empty")
                _uiState.value = current.copy(errorMessage = "Name is required")
                return
            }
            email.isEmpty() -> {
                Logger.w(TAG, "register() validation failed: email is empty")
                _uiState.value = current.copy(errorMessage = "Email is required")
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Logger.w(TAG, "register() validation failed: invalid email")
                _uiState.value = current.copy(errorMessage = "Enter a valid email")
                return
            }
            password.length < 8 -> {
                Logger.w(TAG, "register() validation failed: password too short")
                _uiState.value = current.copy(errorMessage = "Password must be at least 8 characters")
                return
            }
            password != confirmPassword -> {
                Logger.w(TAG, "register() validation failed: passwords do not match")
                _uiState.value = current.copy(errorMessage = "Passwords do not match")
                return
            }
        }

        Logger.d(TAG, "register() called for name: $name, email: $email")
        _uiState.value = current.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            when (val result = registerUseCase(name, email, password)) {
                is Result.Success -> {
                    Logger.d(TAG, "register() success: userId=${result.data.id}")
                    userSession.setUser(result.data)
                    _uiState.value = current.copy(isLoading = false, errorMessage = null)
                    _sideEffect.emit(Unit)
                }
                is Result.Error -> {
                    Logger.e(TAG, "register() error: ${result.message}")
                    _uiState.value = current.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
}
