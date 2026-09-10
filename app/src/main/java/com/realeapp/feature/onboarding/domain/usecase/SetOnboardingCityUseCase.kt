package com.realeapp.feature.onboarding.domain.usecase

import com.realeapp.feature.onboarding.domain.repository.OnboardingRepository

interface SetOnboardingCityUseCase {
    operator fun invoke(city: String, location: String)
}

class SetOnboardingCityUseCaseImpl(
    private val repository: OnboardingRepository
) : SetOnboardingCityUseCase {
    override fun invoke(city: String, location: String) {
        repository.setCitySelection(city, location)
    }
}
