package com.realeapp.feature.onboarding.domain.usecase

import com.realeapp.feature.onboarding.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.StateFlow

interface GetOnboardingCityUseCase {
    operator fun invoke(): StateFlow<String>
}

class GetOnboardingCityUseCaseImpl(
    private val repository: OnboardingRepository
) : GetOnboardingCityUseCase {
    override fun invoke(): StateFlow<String> = repository.city
}
