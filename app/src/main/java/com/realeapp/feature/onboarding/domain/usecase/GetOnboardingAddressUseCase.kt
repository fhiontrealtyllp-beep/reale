package com.realeapp.feature.onboarding.domain.usecase

import com.realeapp.feature.onboarding.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.StateFlow

interface GetOnboardingAddressUseCase {
    operator fun invoke(): StateFlow<String>
}

class GetOnboardingAddressUseCaseImpl(
    private val repository: OnboardingRepository
) : GetOnboardingAddressUseCase {
    override fun invoke(): StateFlow<String> = repository.address
}
