package com.fhiont.feature.city.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhiont.feature.onboarding.domain.usecase.GetOnboardingCityUseCase
import com.fhiont.feature.search.domain.repository.LocationSuggestionRepository
import com.fhiont.feature.search.domain.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CityUiState(
    val cities: List<City> = emptyList(),
    val selectedCity: City? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CityViewModel(
    private val locationSuggestionRepository: LocationSuggestionRepository,
    private val getOnboardingCityUseCase: GetOnboardingCityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CityUiState(isLoading = true))
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()

    init {
        load()
        observeSavedCity()
    }

    private fun observeSavedCity() {
        viewModelScope.launch {
            getOnboardingCityUseCase().collect { savedCity -> syncSelection(savedCity) }
        }
    }

    fun load() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = locationSuggestionRepository.getCities("")) {
                is Result.Success -> {
                    val cities = result.data.map { name -> City(name = name, region = "") }
                    val savedCity = getOnboardingCityUseCase().value
                    _uiState.value = CityUiState(
                        cities = cities,
                        selectedCity = cities.matchingCity(savedCity),
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun selectCity(city: City) {
        _uiState.value = _uiState.value.copy(selectedCity = city)
    }

    /**
     * Re-derives the selection from the persisted city. Called when the screen
     * opens so a previously unconfirmed pick does not linger.
     */
    fun syncSelection() {
        syncSelection(getOnboardingCityUseCase().value)
    }

    private fun syncSelection(savedCity: String) {
        _uiState.value = _uiState.value.copy(
            selectedCity = _uiState.value.cities.matchingCity(savedCity)
        )
    }

    private fun List<City>.matchingCity(name: String): City? =
        firstOrNull { it.name.equals(name, ignoreCase = true) }
}
