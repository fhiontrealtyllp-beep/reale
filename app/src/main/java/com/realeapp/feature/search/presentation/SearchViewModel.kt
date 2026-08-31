package com.realeapp.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.usecase.GetAllPropertiesUseCase
import com.realeapp.feature.search.domain.usecase.UpdatePropertyLikeUseCase
import com.realeapp.feature.search.domain.utils.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getAllPropertiesUseCase: GetAllPropertiesUseCase,
    private val updatePropertyLikeUseCase: UpdatePropertyLikeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<String>()
    val sideEffect: SharedFlow<String> = _sideEffect.asSharedFlow()

    private var currentPage = 0
    private val limit = 10

    private val currentFilter: PropertyFilter?
        get() = _uiState.value.currentFilter

    init {
        refresh()
    }

    fun refresh() {
        currentPage = 0
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            isLoadingMore = false,
            hasReachedEnd = false,
            errorMessage = null
        )
        loadPage(0)
    }

    fun onLoadMore() {
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore || _uiState.value.hasReachedEnd) return
        _uiState.value = _uiState.value.copy(isLoadingMore = true)
        loadPage(currentPage)
    }

    fun onFilterChanged(filter: PropertyFilter?) {
        _uiState.value = _uiState.value.copy(currentFilter = filter)
        refresh()
    }

    fun onResetFilter() {
        _uiState.value = _uiState.value.copy(currentFilter = null)
        refresh()
    }

    fun onToggleView() {
        _uiState.value = _uiState.value.copy(isMapView = !_uiState.value.isMapView)
    }

    fun onLikeClicked(propertyId: String) {
        val index = _uiState.value.properties.indexOfFirst {
            it.documentId == propertyId || it.id == propertyId
        }
        if (index == -1) return

        val oldProperty = _uiState.value.properties[index]
        val newIsLiked = !(oldProperty.isLiked ?: false)
        updatePropertyInList(index, oldProperty.copy(isLiked = newIsLiked))

        viewModelScope.launch {
            val result = updatePropertyLikeUseCase(oldProperty.documentId ?: propertyId, newIsLiked)
            when (result) {
                is Result.Success -> Unit
                is Result.Error -> {
                    updatePropertyInList(index, oldProperty)
                    _sideEffect.emit("Failed to update like: ${result.message}")
                }
            }
        }
    }

    fun onErrorShown() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun updatePropertyInList(index: Int, property: Property) {
        val updated = _uiState.value.properties.toMutableList()
        updated[index] = property
        _uiState.value = _uiState.value.copy(properties = updated)
    }

    private fun loadPage(page: Int) {
        viewModelScope.launch {
            val result = getAllPropertiesUseCase(currentFilter, page, limit)
            when (result) {
                is Result.Success -> {
                    val newProperties = result.data
                    val updatedList = if (page == 0) newProperties else _uiState.value.properties + newProperties
                    val reachedEnd = newProperties.size < limit
                    if (newProperties.isNotEmpty()) {
                        currentPage = page + 1
                    }
                    _uiState.value = _uiState.value.copy(
                        properties = updatedList,
                        isLoading = false,
                        isLoadingMore = false,
                        hasReachedEnd = reachedEnd,
                        errorMessage = null
                    )
                }
                is Result.Error -> {
                    if (page == 0) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessage = result.message
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoadingMore = false
                        )
                        _sideEffect.emit(result.message)
                    }
                }
            }
        }
    }
}
