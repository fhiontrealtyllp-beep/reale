package com.fhiont.feature.auth.presentation

sealed interface PhoneAuthEffect {
    data object NavigateToVerify : PhoneAuthEffect
    data object NavigateToMain : PhoneAuthEffect
}
