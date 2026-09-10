package com.realeapp.feature.onboarding.domain.usecase

import com.realeapp.feature.onboarding.domain.repository.OnboardingRepository

interface SetOnboardingCompletedUseCase {
    operator fun invoke(completed: Boolean)
}

class SetOnboardingCompletedUseCaseImpl(
    private val repository: OnboardingRepository
) : SetOnboardingCompletedUseCase {
    override fun invoke(completed: Boolean) {
        repository.setOnboardingCompleted(completed)
    }
}
