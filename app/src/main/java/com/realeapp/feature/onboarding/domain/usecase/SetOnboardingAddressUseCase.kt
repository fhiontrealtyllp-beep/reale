package com.realeapp.feature.onboarding.domain.usecase

import com.realeapp.feature.onboarding.domain.repository.OnboardingRepository

interface SetOnboardingAddressUseCase {
    operator fun invoke(address: String)
}

class SetOnboardingAddressUseCaseImpl(
    private val repository: OnboardingRepository
) : SetOnboardingAddressUseCase {
    override fun invoke(address: String) {
        repository.setAddress(address)
    }
}
