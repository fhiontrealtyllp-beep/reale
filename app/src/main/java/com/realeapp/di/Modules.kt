package com.realeapp.di

import com.realeapp.core.like.LikeStateManager
import com.realeapp.core.theme.ThemePreferences
import com.realeapp.feature.onboarding.data.OnboardingPreferences
import com.realeapp.feature.onboarding.data.repository.OnboardingRepositoryImpl
import com.realeapp.feature.onboarding.domain.repository.OnboardingRepository
import com.realeapp.feature.add.data.remote.AddPropertyRemoteDataSource
import com.realeapp.feature.add.data.remote.AddPropertyRemoteDataSourceImpl
import com.realeapp.feature.add.data.repository.AddPropertyRepositoryImpl
import com.realeapp.feature.add.domain.repository.AddPropertyRepository
import com.realeapp.feature.add.domain.usecase.AddPropertyUseCase
import com.realeapp.feature.add.domain.usecase.AddPropertyUseCaseImpl
import com.realeapp.feature.add.domain.usecase.GetMyPropertiesUseCase
import com.realeapp.feature.add.domain.usecase.GetMyPropertiesUseCaseImpl
import com.realeapp.feature.add.domain.usecase.UploadImageUseCase as AddUploadImageUseCase
import com.realeapp.feature.add.domain.usecase.UploadImageUseCaseImpl as AddUploadImageUseCaseImpl
import com.realeapp.feature.add.presentation.AddViewModel
import com.realeapp.feature.auth.data.remote.AuthRemoteDataSource
import com.realeapp.feature.auth.data.remote.AuthRemoteDataSourceImpl
import com.realeapp.feature.auth.data.repository.AuthRepositoryImpl
import com.realeapp.feature.auth.domain.repository.AuthRepository
import com.realeapp.feature.auth.domain.usecase.LoginUseCase
import com.realeapp.feature.auth.domain.usecase.LoginUseCaseImpl
import com.realeapp.feature.auth.domain.usecase.RegisterUseCase
import com.realeapp.feature.auth.domain.usecase.RegisterUseCaseImpl
import com.realeapp.feature.auth.domain.usecase.SendPhoneOtpUseCase
import com.realeapp.feature.auth.domain.usecase.SendPhoneOtpUseCaseImpl
import com.realeapp.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.realeapp.feature.auth.domain.usecase.SignInWithGoogleUseCaseImpl
import com.realeapp.feature.auth.domain.usecase.VerifyPhoneOtpUseCase
import com.realeapp.feature.auth.domain.usecase.VerifyPhoneOtpUseCaseImpl
import com.realeapp.feature.auth.presentation.LoginViewModel
import com.realeapp.feature.auth.presentation.PhoneAuthViewModel
import com.realeapp.feature.auth.presentation.RegisterViewModel
import com.realeapp.feature.profile.data.remote.ProfileRemoteDataSource
import com.realeapp.feature.profile.data.remote.ProfileRemoteDataSourceImpl
import com.realeapp.feature.profile.data.repository.ProfileRepositoryImpl
import com.realeapp.feature.profile.domain.repository.ProfileRepository
import com.realeapp.feature.profile.domain.usecase.GetUserDetailsUseCase
import com.realeapp.feature.profile.domain.usecase.GetUserDetailsUseCaseImpl
import com.realeapp.feature.profile.domain.usecase.LogoutUseCase
import com.realeapp.feature.profile.domain.usecase.LogoutUseCaseImpl
import com.realeapp.feature.profile.domain.usecase.UpdateProfileUseCase
import com.realeapp.feature.profile.domain.usecase.UpdateProfileUseCaseImpl
import com.realeapp.feature.profile.domain.usecase.UploadImageUseCase as ProfileUploadImageUseCase
import com.realeapp.feature.profile.domain.usecase.UploadImageUseCaseImpl as ProfileUploadImageUseCaseImpl
import com.realeapp.feature.profile.presentation.ProfileViewModel
import com.realeapp.feature.saved.data.remote.SavedRemoteDataSource
import com.realeapp.feature.saved.data.remote.SavedRemoteDataSourceImpl
import com.realeapp.feature.saved.data.repository.SavedRepositoryImpl
import com.realeapp.feature.saved.domain.repository.SavedRepository
import com.realeapp.feature.saved.domain.usecase.GetLikedPropertiesUseCase
import com.realeapp.feature.saved.domain.usecase.GetLikedPropertiesUseCaseImpl
import com.realeapp.feature.saved.presentation.SavedViewModel
import com.realeapp.core.firebase.FirebaseProvider
import com.realeapp.feature.search.data.local.PropertyLocationSuggestionRepository
import com.realeapp.feature.search.data.remote.PropertyRemoteDataSource
import com.realeapp.feature.search.data.remote.PropertyRemoteDataSourceImpl
import com.realeapp.feature.search.data.repository.PropertyRepositoryImpl
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.data.session.UserSessionImpl
import com.realeapp.feature.search.domain.repository.LocationSuggestionRepository
import com.realeapp.feature.search.domain.repository.PropertyRepository
import com.realeapp.feature.search.domain.usecase.GetAllPropertiesUseCase
import com.realeapp.feature.search.domain.usecase.GetAllPropertiesUseCaseImpl
import com.realeapp.feature.search.domain.usecase.GetFeaturedPropertiesUseCase
import com.realeapp.feature.search.domain.usecase.GetFeaturedPropertiesUseCaseImpl
import com.realeapp.feature.search.domain.usecase.GetLocationSuggestionsUseCase
import com.realeapp.feature.search.domain.usecase.GetLocationSuggestionsUseCaseImpl
import com.realeapp.feature.search.domain.usecase.GetPromotionalPropertiesUseCase
import com.realeapp.feature.search.domain.usecase.GetPromotionalPropertiesUseCaseImpl
import com.realeapp.feature.search.domain.usecase.UpdatePropertyLikeUseCase
import com.realeapp.feature.search.domain.usecase.UpdatePropertyLikeUseCaseImpl
import com.realeapp.feature.search.presentation.SearchViewModel
import com.realeapp.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseProvider() }
    single<UserSession> { UserSessionImpl(androidContext()) }
    single { LikeStateManager }
    single { ThemePreferences(androidContext()) }
    single { OnboardingPreferences(androidContext()) }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    viewModel { MainViewModel(get(), get()) }
}

val authModule = module {
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<LoginUseCase> { LoginUseCaseImpl(get()) }
    single<RegisterUseCase> { RegisterUseCaseImpl(get()) }
    single<SendPhoneOtpUseCase> { SendPhoneOtpUseCaseImpl(get()) }
    single<VerifyPhoneOtpUseCase> { VerifyPhoneOtpUseCaseImpl(get()) }
    single<SignInWithGoogleUseCase> { SignInWithGoogleUseCaseImpl(get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { PhoneAuthViewModel(get(), get(), get(), get()) }
}

val searchModule = module {
    single<PropertyRemoteDataSource> { PropertyRemoteDataSourceImpl(get(), get()) }
    single<PropertyRepository> { PropertyRepositoryImpl(get()) }
    single<GetAllPropertiesUseCase> { GetAllPropertiesUseCaseImpl(get()) }
    single<GetFeaturedPropertiesUseCase> { GetFeaturedPropertiesUseCaseImpl(get()) }
    single<GetPromotionalPropertiesUseCase> { GetPromotionalPropertiesUseCaseImpl(get()) }
    single<LocationSuggestionRepository> { PropertyLocationSuggestionRepository(get()) }
    single<GetLocationSuggestionsUseCase> { GetLocationSuggestionsUseCaseImpl(get()) }
    single<UpdatePropertyLikeUseCase> { UpdatePropertyLikeUseCaseImpl(get()) }
    viewModel { SearchViewModel(get(), get(), get(), get(), get()) }
}

val savedModule = module {
    single<SavedRemoteDataSource> { SavedRemoteDataSourceImpl(get()) }
    single<SavedRepository> { SavedRepositoryImpl(get()) }
    single<GetLikedPropertiesUseCase> { GetLikedPropertiesUseCaseImpl(get()) }
    viewModel { SavedViewModel(get(), get(), get(), get()) }
}

val addModule = module {
    single<AddPropertyRemoteDataSource> { AddPropertyRemoteDataSourceImpl(get()) }
    single<AddPropertyRepository> { AddPropertyRepositoryImpl(get()) }
    single<AddPropertyUseCase> { AddPropertyUseCaseImpl(get()) }
    single<AddUploadImageUseCase> { AddUploadImageUseCaseImpl(get()) }
    single<GetMyPropertiesUseCase> { GetMyPropertiesUseCaseImpl(get()) }
    viewModel { AddViewModel(get(), get(), get(), get()) }
}

val profileModule = module {
    single<ProfileRemoteDataSource> { ProfileRemoteDataSourceImpl(get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    single<GetUserDetailsUseCase> { GetUserDetailsUseCaseImpl(get()) }
    single<UpdateProfileUseCase> { UpdateProfileUseCaseImpl(get()) }
    single<LogoutUseCase> { LogoutUseCaseImpl(get()) }
    single<ProfileUploadImageUseCase> { ProfileUploadImageUseCaseImpl(get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get(), get(), get(), get()) }
}
