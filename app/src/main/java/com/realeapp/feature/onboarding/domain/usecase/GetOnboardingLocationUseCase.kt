package com.realeapp.feature.onboarding.domain.usecase

import com.realeapp.feature.onboarding.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.StateFlow

interface GetOnboardingLocationUseCase {
    operator fun invoke(): StateFlow<String>
}

class GetOnboardingLocationUseCaseImpl(
    private val repository: OnboardingRepository
) : GetOnboardingLocationUseCase {
    override fun invoke(): StateFlow<String> = repository.location
}
