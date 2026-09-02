package com.realeapp.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.feature.auth.domain.usecase.LoginUseCase
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

private const val TAG = "LoginViewModel"

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sideEffect: SharedFlow<Unit> = _sideEffect.asSharedFlow()

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun onTogglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun login() {
        val current = _uiState.value
        val email = current.email.trim()
        val password = current.password.trim()

        Logger.d(TAG, "login() invoked from UI: email=$email")

        if (email.isEmpty()) {
            Logger.w(TAG, "login() validation failed: email is empty")
            _uiState.value = current.copy(errorMessage = "Email is required")
            return
        }
        if (password.isEmpty()) {
            Logger.w(TAG, "login() validation failed: password is empty")
            _uiState.value = current.copy(errorMessage = "Password is required")
            return
        }

        Logger.d(TAG, "login() called for email: $email")
        _uiState.value = current.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> {
                    Logger.d(TAG, "login() success: userId=${result.data.id}")
                    userSession.setUser(result.data)
                    _uiState.value = current.copy(isLoading = false, errorMessage = null)
                    _sideEffect.emit(Unit)
                }
                is Result.Error -> {
                    Logger.e(TAG, "login() error: ${result.message}")
                    _uiState.value = current.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
}
