package com.fhiont.feature.city.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhiont.feature.search.domain.repository.LocationSuggestionRepository
import com.fhiont.feature.search.domain.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CityUiState(
    val cities: List<City> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CityViewModel(
    private val locationSuggestionRepository: LocationSuggestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CityUiState(isLoading = true))
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = locationSuggestionRepository.getCities("")) {
                is Result.Success -> {
                    _uiState.value = CityUiState(
                        cities = result.data.map { name -> City(name = name, region = "") },
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
}
