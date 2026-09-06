package com.realeapp.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.core.like.LikeStateManager
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.usecase.GetAllPropertiesUseCase
import com.realeapp.feature.search.domain.usecase.GetFeaturedPropertiesUseCase
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
private const val FEATURED_PROPERTIES_LIMIT = 10

class SearchViewModel(
    private val getAllPropertiesUseCase: GetAllPropertiesUseCase,
    private val getFeaturedPropertiesUseCase: GetFeaturedPropertiesUseCase,
    private val updatePropertyLikeUseCase: UpdatePropertyLikeUseCase,
    private val likeStateManager: LikeStateManager = LikeStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _featuredProperties = MutableStateFlow<List<Property>>(emptyList())
    val featuredProperties: StateFlow<List<Property>> = _featuredProperties.asStateFlow()

    private val _sideEffect = MutableSharedFlow<String>()
    val sideEffect: SharedFlow<String> = _sideEffect.asSharedFlow()

    private var currentPage = 0
    private val limit = 10

    private val currentFilter: PropertyFilter?
        get() = _uiState.value.currentFilter

    init {
        refresh()
        loadFeaturedProperties()
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

                val updatedFeatured = _featuredProperties.value.map {
                    it.copy(isLiked = likedIds.contains(it.documentId ?: it.id))
                }
                _featuredProperties.value = updatedFeatured
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
        val allIndex = _uiState.value.properties.indexOfFirst {
            it.documentId == propertyId || it.id == propertyId
        }
        val featuredIndex = _featuredProperties.value.indexOfFirst {
            it.documentId == propertyId || it.id == propertyId
        }
        if (allIndex == -1 && featuredIndex == -1) {
            Logger.w(TAG, "onLikeClicked: property not found in list id=$propertyId")
            return
        }

        val oldProperty = when {
            allIndex != -1 -> _uiState.value.properties[allIndex]
            else -> _featuredProperties.value[featuredIndex]
        }
        val oldIsLiked = oldProperty.isLiked ?: false
        val newIsLiked = !oldIsLiked
        Logger.d(TAG, "onLikeClicked: toggling id=$propertyId from isLiked=$oldIsLiked to isLiked=$newIsLiked")

        val updatedProperty = oldProperty.copy(isLiked = newIsLiked)
        likeStateManager.setLiked(oldProperty, newIsLiked)
        if (allIndex != -1) updatePropertyInList(allIndex, updatedProperty)
        if (featuredIndex != -1) updateFeaturedPropertyInList(featuredIndex, updatedProperty)

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
                    if (allIndex != -1) updatePropertyInList(allIndex, oldProperty)
                    if (featuredIndex != -1) updateFeaturedPropertyInList(featuredIndex, oldProperty)
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

    private fun updateFeaturedPropertyInList(index: Int, property: Property) {
        val updated = _featuredProperties.value.toMutableList()
        updated[index] = property
        _featuredProperties.value = updated
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

    private fun loadFeaturedProperties() {
        viewModelScope.launch {
            when (val result = getFeaturedPropertiesUseCase(FEATURED_PROPERTIES_LIMIT)) {
                is Result.Success -> {
                    Logger.d(TAG, "loadFeaturedProperties: received=${result.data.size}")
                    _featuredProperties.value = result.data
                }
                is Result.Error -> {
                    Logger.e(TAG, "loadFeaturedProperties: failed ${result.message}")
                }
            }
        }
    }
}
