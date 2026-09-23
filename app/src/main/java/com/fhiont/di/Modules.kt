package com.fhiont.di

import com.fhiont.core.like.LikeStateManager
import com.fhiont.core.theme.ThemePreferences
import com.fhiont.feature.onboarding.data.OnboardingPreferences
import com.fhiont.feature.onboarding.data.repository.OnboardingRepositoryImpl
import com.fhiont.feature.onboarding.domain.repository.OnboardingRepository
import com.fhiont.feature.onboarding.domain.usecase.GetOnboardingAddressUseCase
import com.fhiont.feature.onboarding.domain.usecase.GetOnboardingAddressUseCaseImpl
import com.fhiont.feature.onboarding.domain.usecase.GetOnboardingCityUseCase
import com.fhiont.feature.onboarding.domain.usecase.GetOnboardingCityUseCaseImpl
import com.fhiont.feature.onboarding.domain.usecase.GetOnboardingCompletedUseCase
import com.fhiont.feature.onboarding.domain.usecase.GetOnboardingCompletedUseCaseImpl
import com.fhiont.feature.onboarding.domain.usecase.GetOnboardingLocationUseCase
import com.fhiont.feature.onboarding.domain.usecase.GetOnboardingLocationUseCaseImpl
import com.fhiont.feature.onboarding.domain.usecase.SetOnboardingAddressUseCase
import com.fhiont.feature.onboarding.domain.usecase.SetOnboardingAddressUseCaseImpl
import com.fhiont.feature.onboarding.domain.usecase.SetOnboardingCityUseCase
import com.fhiont.feature.onboarding.domain.usecase.SetOnboardingCityUseCaseImpl
import com.fhiont.feature.onboarding.domain.usecase.SetOnboardingCompletedUseCase
import com.fhiont.feature.onboarding.domain.usecase.SetOnboardingCompletedUseCaseImpl
import com.fhiont.feature.add.data.local.PropertyDraftStore
import com.fhiont.feature.add.data.remote.AddPropertyRemoteDataSource
import com.fhiont.feature.add.data.remote.AddPropertyRemoteDataSourceImpl
import com.fhiont.feature.add.data.repository.AddPropertyRepositoryImpl
import com.fhiont.feature.add.domain.repository.AddPropertyRepository
import com.fhiont.feature.add.domain.usecase.AddPropertyUseCase
import com.fhiont.feature.add.domain.usecase.AddPropertyUseCaseImpl
import com.fhiont.feature.add.domain.usecase.GetMyPropertiesUseCase
import com.fhiont.feature.add.domain.usecase.GetMyPropertiesUseCaseImpl
import com.fhiont.feature.add.domain.usecase.UploadImageUseCase as AddUploadImageUseCase
import com.fhiont.feature.add.domain.usecase.UploadImageUseCaseImpl as AddUploadImageUseCaseImpl
import com.fhiont.feature.add.presentation.AddViewModel
import com.fhiont.feature.auth.data.remote.AuthRemoteDataSource
import com.fhiont.feature.auth.data.remote.AuthRemoteDataSourceImpl
import com.fhiont.feature.auth.data.repository.AuthRepositoryImpl
import com.fhiont.feature.auth.domain.repository.AuthRepository
import com.fhiont.feature.auth.domain.usecase.LoginUseCase
import com.fhiont.feature.auth.domain.usecase.LoginUseCaseImpl
import com.fhiont.feature.auth.domain.usecase.RegisterUseCase
import com.fhiont.feature.auth.domain.usecase.RegisterUseCaseImpl
import com.fhiont.feature.auth.domain.usecase.SendPhoneOtpUseCase
import com.fhiont.feature.auth.domain.usecase.SendPhoneOtpUseCaseImpl
import com.fhiont.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.fhiont.feature.auth.domain.usecase.SignInWithGoogleUseCaseImpl
import com.fhiont.feature.auth.domain.usecase.VerifyPhoneOtpUseCase
import com.fhiont.feature.auth.domain.usecase.VerifyPhoneOtpUseCaseImpl
import com.fhiont.feature.auth.presentation.LoginViewModel
import com.fhiont.feature.auth.presentation.PhoneAuthViewModel
import com.fhiont.feature.auth.presentation.RegisterViewModel
import com.fhiont.feature.profile.data.remote.ProfileRemoteDataSource
import com.fhiont.feature.profile.data.remote.ProfileRemoteDataSourceImpl
import com.fhiont.feature.profile.data.repository.ProfileRepositoryImpl
import com.fhiont.feature.profile.domain.repository.ProfileRepository
import com.fhiont.feature.profile.domain.usecase.GetUserDetailsUseCase
import com.fhiont.feature.profile.domain.usecase.GetUserDetailsUseCaseImpl
import com.fhiont.feature.profile.domain.usecase.LogoutUseCase
import com.fhiont.feature.profile.domain.usecase.LogoutUseCaseImpl
import com.fhiont.feature.profile.domain.usecase.UpdateProfileUseCase
import com.fhiont.feature.profile.domain.usecase.UpdateProfileUseCaseImpl
import com.fhiont.feature.profile.domain.usecase.UploadImageUseCase as ProfileUploadImageUseCase
import com.fhiont.feature.profile.domain.usecase.UploadImageUseCaseImpl as ProfileUploadImageUseCaseImpl
import com.fhiont.feature.profile.presentation.MyEnquiriesViewModel
import com.fhiont.feature.profile.presentation.MyListingsViewModel
import com.fhiont.feature.profile.presentation.ProfileViewModel
import com.fhiont.feature.saved.data.remote.SavedRemoteDataSource
import com.fhiont.feature.saved.data.remote.SavedRemoteDataSourceImpl
import com.fhiont.feature.saved.data.repository.SavedRepositoryImpl
import com.fhiont.feature.saved.domain.repository.SavedRepository
import com.fhiont.feature.saved.domain.usecase.GetLikedPropertiesUseCase
import com.fhiont.feature.saved.domain.usecase.GetLikedPropertiesUseCaseImpl
import com.fhiont.feature.saved.presentation.SavedViewModel
import com.fhiont.core.firebase.FirebaseProvider
import com.fhiont.core.network.PhpAuthApi
import com.fhiont.feature.search.data.local.PropertyLocationSuggestionRepository
import com.fhiont.feature.search.data.remote.EnquiryRemoteDataSource
import com.fhiont.feature.search.data.remote.EnquiryRemoteDataSourceImpl
import com.fhiont.feature.search.data.remote.PropertyRemoteDataSource
import com.fhiont.feature.search.data.remote.PropertyRemoteDataSourceImpl
import com.fhiont.feature.search.data.repository.EnquiryRepositoryImpl
import com.fhiont.feature.search.data.repository.PropertyRepositoryImpl
import com.fhiont.feature.search.data.session.UserSession
import com.fhiont.feature.search.data.session.UserSessionImpl
import com.fhiont.feature.search.domain.repository.EnquiryRepository
import com.fhiont.feature.search.domain.repository.LocationSuggestionRepository
import com.fhiont.feature.search.domain.repository.PropertyRepository
import com.fhiont.feature.search.domain.usecase.GetAllPropertiesUseCase
import com.fhiont.feature.search.domain.usecase.GetAllPropertiesUseCaseImpl
import com.fhiont.feature.search.domain.usecase.GetFeaturedPropertiesUseCase
import com.fhiont.feature.search.domain.usecase.GetFeaturedPropertiesUseCaseImpl
import com.fhiont.feature.search.domain.usecase.GetLocationSuggestionsUseCase
import com.fhiont.feature.search.domain.usecase.GetLocationSuggestionsUseCaseImpl
import com.fhiont.feature.search.domain.usecase.GetEnquiriesByPropertyUseCase
import com.fhiont.feature.search.domain.usecase.GetEnquiriesByPropertyUseCaseImpl
import com.fhiont.feature.search.domain.usecase.GetEnquiryCountsForPropertiesUseCase
import com.fhiont.feature.search.domain.usecase.GetEnquiryCountsForPropertiesUseCaseImpl
import com.fhiont.feature.search.domain.usecase.GetMyEnquiriesUseCase
import com.fhiont.feature.search.domain.usecase.GetMyEnquiriesUseCaseImpl
import com.fhiont.feature.search.domain.usecase.GetPromotionalPropertiesUseCase
import com.fhiont.feature.search.domain.usecase.GetPromotionalPropertiesUseCaseImpl
import com.fhiont.feature.search.domain.usecase.SendEnquiryUseCase
import com.fhiont.feature.search.domain.usecase.SendEnquiryUseCaseImpl
import com.fhiont.feature.search.domain.usecase.UpdatePropertyLikeUseCase
import com.fhiont.feature.search.domain.usecase.UpdatePropertyLikeUseCaseImpl
import com.fhiont.feature.search.presentation.EnquireViewModel
import com.fhiont.feature.search.presentation.SearchViewModel
import com.fhiont.ui.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseProvider() }
    single { PhpAuthApi() }
    single<UserSession> { UserSessionImpl(androidContext()) }
    single { LikeStateManager }
    single { ThemePreferences(androidContext()) }
    single { OnboardingPreferences(androidContext()) }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    single<GetOnboardingCityUseCase> { GetOnboardingCityUseCaseImpl(get()) }
    single<GetOnboardingLocationUseCase> { GetOnboardingLocationUseCaseImpl(get()) }
    single<GetOnboardingAddressUseCase> { GetOnboardingAddressUseCaseImpl(get()) }
    single<GetOnboardingCompletedUseCase> { GetOnboardingCompletedUseCaseImpl(get()) }
    single<SetOnboardingCityUseCase> { SetOnboardingCityUseCaseImpl(get()) }
    single<SetOnboardingAddressUseCase> { SetOnboardingAddressUseCaseImpl(get()) }
    single<SetOnboardingCompletedUseCase> { SetOnboardingCompletedUseCaseImpl(get()) }
    viewModel { MainViewModel(get(), get(), get(), get()) }
}

val authModule = module {
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get(), get()) }
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
    single<EnquiryRemoteDataSource> { EnquiryRemoteDataSourceImpl(get()) }
    single<PropertyRepository> { PropertyRepositoryImpl(get()) }
    single<EnquiryRepository> { EnquiryRepositoryImpl(get()) }
    single<GetAllPropertiesUseCase> { GetAllPropertiesUseCaseImpl(get()) }
    single<GetFeaturedPropertiesUseCase> { GetFeaturedPropertiesUseCaseImpl(get()) }
    single<GetPromotionalPropertiesUseCase> { GetPromotionalPropertiesUseCaseImpl(get()) }
    single<LocationSuggestionRepository> { PropertyLocationSuggestionRepository(get()) }
    single<GetLocationSuggestionsUseCase> { GetLocationSuggestionsUseCaseImpl(get()) }
    single<UpdatePropertyLikeUseCase> { UpdatePropertyLikeUseCaseImpl(get()) }
    single<SendEnquiryUseCase> { SendEnquiryUseCaseImpl(get(), get()) }
    single<GetMyEnquiriesUseCase> { GetMyEnquiriesUseCaseImpl(get()) }
    single<GetEnquiriesByPropertyUseCase> { GetEnquiriesByPropertyUseCaseImpl(get()) }
    single<GetEnquiryCountsForPropertiesUseCase> { GetEnquiryCountsForPropertiesUseCaseImpl(get()) }
    viewModel { SearchViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { EnquireViewModel(get()) }
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
    single { PropertyDraftStore(androidContext()) }
    viewModel { AddViewModel(get(), get(), get(), get(), get()) }
}

val profileModule = module {
    single<ProfileRemoteDataSource> { ProfileRemoteDataSourceImpl(get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    single<GetUserDetailsUseCase> { GetUserDetailsUseCaseImpl(get()) }
    single<UpdateProfileUseCase> { UpdateProfileUseCaseImpl(get()) }
    single<LogoutUseCase> { LogoutUseCaseImpl(get()) }
    single<ProfileUploadImageUseCase> { ProfileUploadImageUseCaseImpl(get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { (filterPropertyId: String?) -> MyEnquiriesViewModel(get(), get(), get(), filterPropertyId) }
    viewModel { MyListingsViewModel(get(), get(), get()) }
}
