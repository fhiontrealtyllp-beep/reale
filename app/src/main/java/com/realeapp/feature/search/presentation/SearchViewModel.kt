package com.realeapp.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.core.like.LikeStateManager
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.usecase.GetAllPropertiesUseCase
import com.realeapp.feature.search.domain.usecase.UpdatePropertyLikeUseCase
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "SearchViewModel"

class SearchViewModel(
    private val getAllPropertiesUseCase: GetAllPropertiesUseCase,
    private val updatePropertyLikeUseCase: UpdatePropertyLikeUseCase,
    private val likeStateManager: LikeStateManager = LikeStateManager
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
        observeLikeState()
    }

    private fun observeLikeState() {
        viewModelScope.launch {
            likeStateManager.likedIds.collect { likedIds ->
                Logger.d(TAG, "observeLikeState: likedIds=${likedIds.size}")
                val updated = _uiState.value.properties.map {
                    it.copy(isLiked = likedIds.contains(it.documentId ?: it.id))
                }
                _uiState.value = _uiState.value.copy(properties = updated)
            }
        }
    }

    fun refresh() {
        Logger.d(TAG, "refresh: resetting and loading page 0")
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
        Logger.d(TAG, "onLikeClicked: propertyId=$propertyId")
        val index = _uiState.value.properties.indexOfFirst {
            it.documentId == propertyId || it.id == propertyId
        }
        if (index == -1) {
            Logger.w(TAG, "onLikeClicked: property not found in list id=$propertyId")
            return
        }

        val oldProperty = _uiState.value.properties[index]
        val oldIsLiked = oldProperty.isLiked ?: false
        val newIsLiked = !oldIsLiked
        Logger.d(TAG, "onLikeClicked: toggling id=$propertyId from isLiked=$oldIsLiked to isLiked=$newIsLiked")

        likeStateManager.setLiked(oldProperty, newIsLiked)
        updatePropertyInList(index, oldProperty.copy(isLiked = newIsLiked))

        viewModelScope.launch {
            val targetId = oldProperty.documentId ?: propertyId
            Logger.d(TAG, "onLikeClicked: invoking updatePropertyLikeUseCase id=$targetId, isLiked=$newIsLiked")
            val result = updatePropertyLikeUseCase(targetId, newIsLiked)
            when (result) {
                is Result.Success -> {
                    Logger.d(TAG, "onLikeClicked: updatePropertyLikeUseCase success id=$targetId")
                }
                is Result.Error -> {
                    Logger.e(TAG, "onLikeClicked: updatePropertyLikeUseCase failed id=$targetId, reverting UI")
                    likeStateManager.setLiked(oldProperty, oldIsLiked)
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
        Logger.d(TAG, "loadPage: loading page=$page")
        viewModelScope.launch {
            val result = getAllPropertiesUseCase(currentFilter, page, limit)
            when (result) {
                is Result.Success -> {
                    val newProperties = result.data
                    Logger.d(TAG, "loadPage: received ${newProperties.size} properties for page=$page")
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
                    val pageIds = newProperties.map { it.documentId ?: it.id }.toSet()
                    val pageLikedIds = newProperties.mapNotNull {
                        if (it.isLiked == true) it.documentId ?: it.id else null
                    }.toSet()
                    val otherLikedIds = likeStateManager.likedIds.value - pageIds
                    val mergedIds = pageLikedIds + otherLikedIds
                    Logger.d(TAG, "loadPage: syncing ${mergedIds.size} liked IDs to LikeStateManager (page=$page, pageLiked=${pageLikedIds.size})")
                    likeStateManager.syncLikedIds(mergedIds)

                    val pageLikedCache = newProperties.filter { it.isLiked == true }.associateBy { it.documentId ?: it.id }
                    val otherCache = likeStateManager.likedPropertyCache.value.filterKeys { it !in pageIds }
                    likeStateManager.syncLikedProperties((otherCache + pageLikedCache).values.toList())
                }
                is Result.Error -> {
                    Logger.e(TAG, "loadPage: failed page=$page, ${result.message}")
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
