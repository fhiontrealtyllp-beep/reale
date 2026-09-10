package com.realeapp.feature.onboarding.domain.usecase

import com.realeapp.feature.onboarding.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.StateFlow

interface GetOnboardingCompletedUseCase {
    operator fun invoke(): StateFlow<Boolean>
}

class GetOnboardingCompletedUseCaseImpl(
    private val repository: OnboardingRepository
) : GetOnboardingCompletedUseCase {
    override fun invoke(): StateFlow<Boolean> = repository.isOnboardingCompleted
}
