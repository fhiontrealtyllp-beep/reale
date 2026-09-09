package com.realeapp.feature.auth.presentation

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.feature.auth.domain.usecase.SendPhoneOtpUseCase
import com.realeapp.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.realeapp.feature.auth.domain.usecase.VerifyPhoneOtpUseCase
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PhoneAuthViewModel"
private const val PHONE_LENGTH = 10
private const val OTP_LENGTH = 6

class PhoneAuthViewModel(
    private val sendPhoneOtpUseCase: SendPhoneOtpUseCase,
    private val verifyPhoneOtpUseCase: VerifyPhoneOtpUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneAuthUiState())
    val uiState: StateFlow<PhoneAuthUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<PhoneAuthEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<PhoneAuthEffect> = _effect.asSharedFlow()

    fun sendOtp(activity: Activity?, phone: String) {
        if (phone.length != PHONE_LENGTH) {
            _uiState.update {
                it.copy(errorMessage = AuthStrings.ERROR_INVALID_PHONE)
            }
            return
        }

        Logger.d(TAG, "sendOtp() called: phone=$phone")
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val fullPhone = "${AuthStrings.COUNTRY_CODE_IN}$phone"
            when (val result = sendPhoneOtpUseCase(activity, fullPhone)) {
                is Result.Success -> {
                    Logger.d(TAG, "sendOtp() success: verificationId=${result.data}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pendingVerificationId = result.data,
                            pendingPhone = phone
                        )
                    }
                    _effect.emit(PhoneAuthEffect.NavigateToVerify)
                }

                is Result.Error -> {
                    Logger.e(TAG, "sendOtp() error: ${result.message}")
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun resendOtp(activity: Activity?) {
        val phone = _uiState.value.pendingPhone
        if (phone.isBlank()) {
            _uiState.update { it.copy(errorMessage = AuthStrings.ERROR_INVALID_PHONE) }
            return
        }
        sendOtp(activity, phone)
    }

    fun verifyOtpAndSaveProfile(otp: String, fullName: String, dob: String) {
        val current = _uiState.value

        when {
            otp.length != OTP_LENGTH -> {
                _uiState.update { it.copy(errorMessage = AuthStrings.ERROR_INVALID_OTP) }
                return
            }

            fullName.isBlank() -> {
                _uiState.update { it.copy(errorMessage = AuthStrings.ERROR_NAME_REQUIRED) }
                return
            }

            current.pendingVerificationId.isBlank() || current.pendingPhone.isBlank() -> {
                _uiState.update { it.copy(errorMessage = AuthStrings.ERROR_SESSION_EXPIRED) }
                return
            }
        }

        Logger.d(TAG, "verifyOtpAndSaveProfile() called: otp=$otp, name=$fullName")
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val fullPhone = "${AuthStrings.COUNTRY_CODE_IN}${current.pendingPhone}"
            when (val result = verifyPhoneOtpUseCase(
                verificationId = current.pendingVerificationId,
                secret = otp,
                phone = fullPhone,
                name = fullName,
                dob = dob
            )) {
                is Result.Success -> {
                    Logger.d(TAG, "verifyOtpAndSaveProfile() success: userId=${result.data.id}")
                    userSession.setUser(result.data)
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.emit(PhoneAuthEffect.NavigateToMain)
                }

                is Result.Error -> {
                    Logger.e(TAG, "verifyOtpAndSaveProfile() error: ${result.message}")
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun signInWithGoogle(activity: Activity?) {
        val currentActivity = activity ?: run {
            _uiState.update { it.copy(errorMessage = AuthStrings.ERROR_GOOGLE_SIGN_IN_FAILED) }
            return
        }

        Logger.d(TAG, "signInWithGoogle() called")
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = signInWithGoogleUseCase(currentActivity)) {
                is Result.Success -> {
                    Logger.d(TAG, "signInWithGoogle() success: userId=${result.data.id}")
                    userSession.setUser(result.data)
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.emit(PhoneAuthEffect.NavigateToMain)
                }

                is Result.Error -> {
                    Logger.e(TAG, "signInWithGoogle() error: ${result.message}")
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
