package com.realeapp.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.core.like.LikeStateManager
import com.realeapp.feature.onboarding.domain.usecase.GetOnboardingCityUseCase
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.model.LocationSuggestion
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.domain.model.ResidentialCommercial
import com.realeapp.feature.search.domain.usecase.GetAllPropertiesUseCase
import com.realeapp.feature.search.domain.usecase.GetFeaturedPropertiesUseCase
import com.realeapp.feature.search.domain.usecase.GetLocationSuggestionsUseCase
import com.realeapp.feature.search.domain.usecase.GetPromotionalPropertiesUseCase
import com.realeapp.feature.search.domain.usecase.UpdatePropertyLikeUseCase
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

private const val TAG = "SearchViewModel"
private const val FEATURED_PROPERTIES_LIMIT = 10
private const val PROMOTIONAL_PROPERTIES_LIMIT = 1

class SearchViewModel(
    private val getAllPropertiesUseCase: GetAllPropertiesUseCase,
    private val getFeaturedPropertiesUseCase: GetFeaturedPropertiesUseCase,
    private val getPromotionalPropertiesUseCase: GetPromotionalPropertiesUseCase,
    private val getLocationSuggestionsUseCase: GetLocationSuggestionsUseCase,
    private val updatePropertyLikeUseCase: UpdatePropertyLikeUseCase,
    private val getOnboardingCityUseCase: GetOnboardingCityUseCase,
    private val userSession: UserSession,
    private val likeStateManager: LikeStateManager = LikeStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val selectedCity: StateFlow<String?> = _uiState
        .map { it.currentFilter?.city }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _rawFeaturedProperties = MutableStateFlow<List<Property>>(emptyList())
    private val _featuredProperties = MutableStateFlow<List<Property>>(emptyList())
    val featuredProperties: StateFlow<List<Property>> = _featuredProperties.asStateFlow()

    private val _rawPromotionalProperty = MutableStateFlow<Property?>(null)
    private val _promotionalProperty = MutableStateFlow<Property?>(null)
    val promotionalProperty: StateFlow<Property?> = _promotionalProperty.asStateFlow()

    private val _selectedHomeCategory = MutableStateFlow(HomeCategory.BUY)
    val selectedHomeCategory: StateFlow<HomeCategory> = _selectedHomeCategory.asStateFlow()

    private val _sideEffect = MutableSharedFlow<String>()
    val sideEffect: SharedFlow<String> = _sideEffect.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _suggestions = MutableStateFlow<List<LocationSuggestion>>(emptyList())
    val suggestions: StateFlow<List<LocationSuggestion>> = _suggestions.asStateFlow()

    private var suggestionsJob: Job? = null

    private var currentPage = 0
    private val limit = 10

    private val currentFilter: PropertyFilter?
        get() = _uiState.value.currentFilter

    /**
     * Filter actually sent to the data layer: the explicit filter combined with
     * the selected home category, so pagination runs over the categorized set.
     */
    private val effectiveFilter: PropertyFilter
        get() = filterWithCategory(_uiState.value.currentFilter, _selectedHomeCategory.value)

    init {
        val savedCity = getOnboardingCityUseCase().value
        Logger.d(TAG, "init: savedCity='$savedCity'")
        if (savedCity.isNotBlank()) {
            _uiState.value = _uiState.value.copy(
                currentFilter = PropertyFilter(city = savedCity)
            )
            _searchQuery.value = savedCity
        }
        _selectedHomeCategory.value = homeCategoryFor(currentFilter)
        refresh()
        loadFeaturedProperties()
        loadPromotionalProperty()
        applyCategoryFilter()
        observeLikeState()
        observeSavedCity()
    }

    private fun observeSavedCity() {
        viewModelScope.launch {
            getOnboardingCityUseCase()
                .collect { savedCity ->
                    Logger.d(TAG, "observeSavedCity: savedCity='$savedCity'")
                    if (savedCity.isNotBlank() && _uiState.value.currentFilter?.city != savedCity) {
                        _uiState.value = _uiState.value.copy(
                            currentFilter = PropertyFilter(city = savedCity)
                        )
                        _searchQuery.value = savedCity
                        _selectedHomeCategory.value = homeCategoryFor(currentFilter)
                        refresh(clearList = true)
                        loadFeaturedProperties()
                        loadPromotionalProperty()
                    }
                }
        }
    }

    private fun observeLikeState() {
        viewModelScope.launch {
            likeStateManager.likedIds.collect { likedIds ->
                Logger.d(TAG, "observeLikeState: likedIds=${likedIds.size}")
                val updated = _uiState.value.properties.map {
                    it.copy(isLiked = likedIds.contains(it.documentId ?: it.id))
                }
                _uiState.value = _uiState.value.copy(properties = updated)

                val updatedFeatured = _rawFeaturedProperties.value.map {
                    it.copy(isLiked = likedIds.contains(it.documentId ?: it.id))
                }
                _rawFeaturedProperties.value = updatedFeatured

                _rawPromotionalProperty.value?.let { ad ->
                    _rawPromotionalProperty.value = ad.copy(
                        isLiked = likedIds.contains(ad.documentId ?: ad.id)
                    )
                }

                applyCategoryFilter()
            }
        }
    }

    fun refresh(clearList: Boolean = false) {
        Logger.d(TAG, "refresh: resetting and loading page 0, clearList=$clearList")
        currentPage = 0
        _uiState.value = _uiState.value.copy(
            properties = if (clearList) emptyList() else _uiState.value.properties,
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
        _searchQuery.value = filter?.city.orEmpty()
        _selectedHomeCategory.value = homeCategoryFor(filter)
        applyCategoryFilter()
        refresh(clearList = true)
    }

    fun onResetFilter() {
        _uiState.value = _uiState.value.copy(currentFilter = null)
        _searchQuery.value = ""
        _selectedHomeCategory.value = homeCategoryFor(null)
        applyCategoryFilter()
        refresh(clearList = true)
    }

    fun onToggleView() {
        _uiState.value = _uiState.value.copy(isMapView = !_uiState.value.isMapView)
    }

    fun onLikeClicked(propertyId: String) {
        Logger.d(TAG, "onLikeClicked: propertyId=$propertyId")
        if (userSession.getUserId().isNullOrEmpty()) {
            Logger.d(TAG, "onLikeClicked: user not logged in, showing login prompt")
            _uiState.value = _uiState.value.copy(showLoginPrompt = true)
            return
        }

        val allIndex = _uiState.value.properties.indexOfFirst {
            it.documentId == propertyId || it.id == propertyId
        }
        val featuredIndex = _featuredProperties.value.indexOfFirst {
            it.documentId == propertyId || it.id == propertyId
        }
        val promotionalId = _promotionalProperty.value?.documentId ?: _promotionalProperty.value?.id
        if (allIndex == -1 && featuredIndex == -1 && promotionalId != propertyId) {
            Logger.w(TAG, "onLikeClicked: property not found in list id=$propertyId")
            return
        }

        val oldProperty = when {
            allIndex != -1 -> _uiState.value.properties[allIndex]
            featuredIndex != -1 -> _featuredProperties.value[featuredIndex]
            else -> _promotionalProperty.value!!
        }
        val oldIsLiked = oldProperty.isLiked ?: false
        val newIsLiked = !oldIsLiked
        Logger.d(TAG, "onLikeClicked: toggling id=$propertyId from isLiked=$oldIsLiked to isLiked=$newIsLiked")

        val updatedProperty = oldProperty.copy(isLiked = newIsLiked)
        likeStateManager.setLiked(oldProperty, newIsLiked)
        if (allIndex != -1) updatePropertyInList(allIndex, updatedProperty)
        if (featuredIndex != -1) updateFeaturedPropertyInList(featuredIndex, updatedProperty)
        if (promotionalId == propertyId) {
            _rawPromotionalProperty.value = updatedProperty
            applyCategoryFilter()
        }

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
                    if (promotionalId == propertyId) {
                        _rawPromotionalProperty.value = oldProperty
                        applyCategoryFilter()
                    }
                    _sideEffect.emit("Failed to update like: ${result.message}")
                }
            }
        }
    }

    suspend fun getSuggestions(query: String): List<LocationSuggestion> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        when (val result = getLocationSuggestionsUseCase(query)) {
            is Result.Success -> result.data
            is Result.Error -> {
                Logger.e(TAG, "getSuggestions: ${result.message}")
                emptyList()
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        suggestionsJob?.cancel()
        if (query.isBlank()) {
            _suggestions.value = emptyList()
            return
        }
        suggestionsJob = viewModelScope.launch {
            delay(SearchStrings.SUGGESTIONS_DEBOUNCE_MS)
            when (val result = getLocationSuggestionsUseCase(query)) {
                is Result.Success -> {
                    Logger.d(TAG, "onSearchQueryChanged: suggestions=${result.data.size}")
                    _suggestions.value = result.data
                }
                is Result.Error -> {
                    Logger.e(TAG, "onSearchQueryChanged: ${result.message}")
                    _suggestions.value = emptyList()
                }
            }
        }
    }

    fun onSuggestionSelected(suggestion: LocationSuggestion) {
        suggestionsJob?.cancel()
        _suggestions.value = emptyList()
        val current = _uiState.value.currentFilter ?: PropertyFilter()
        onFilterChanged(
            current.copy(
                city = suggestion.primaryText,
                localities = suggestion.secondaryText
                    .takeIf(String::isNotBlank)
                    ?.let(::listOf)
                    ?: emptyList()
            )
        )
    }

    fun onCategorySelected(category: HomeCategory) {
        Logger.d(TAG, "onCategorySelected: category=$category")
        _selectedHomeCategory.value = category
        val base = _uiState.value.currentFilter ?: PropertyFilter()
        val newFilter = when (category) {
            HomeCategory.RENT -> base.copy(
                rentBuy = RentBuy.RENT,
                residentialCommercial = ResidentialCommercial.RESIDENTIAL
            )
            HomeCategory.COMMERCIAL -> base.copy(
                rentBuy = null,
                residentialCommercial = ResidentialCommercial.COMMERCIAL
            )
            HomeCategory.BUY, HomeCategory.NEW_PROJECTS -> base.copy(
                rentBuy = RentBuy.BUY,
                residentialCommercial = ResidentialCommercial.RESIDENTIAL
            )
        }
        _uiState.value = _uiState.value.copy(currentFilter = newFilter)
        applyCategoryFilter()
        refresh(clearList = true)
    }

    private fun homeCategoryFor(filter: PropertyFilter?): HomeCategory = when {
        filter?.residentialCommercial == ResidentialCommercial.COMMERCIAL -> HomeCategory.COMMERCIAL
        filter?.rentBuy == RentBuy.RENT -> HomeCategory.RENT
        else -> HomeCategory.BUY
    }

    private fun filterWithCategory(filter: PropertyFilter?, category: HomeCategory): PropertyFilter {
        val base = filter ?: PropertyFilter()
        return when (category) {
            HomeCategory.RENT -> base.copy(
                rentBuy = RentBuy.RENT,
                residentialCommercial = ResidentialCommercial.RESIDENTIAL
            )
            HomeCategory.COMMERCIAL -> base.copy(
                residentialCommercial = ResidentialCommercial.COMMERCIAL
            )
            HomeCategory.BUY, HomeCategory.NEW_PROJECTS -> base.copy(
                rentBuy = RentBuy.BUY,
                residentialCommercial = ResidentialCommercial.RESIDENTIAL
            )
        }
    }

    fun onErrorShown() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun onLoginPromptDismissed() {
        _uiState.value = _uiState.value.copy(showLoginPrompt = false)
    }

    private fun updatePropertyInList(index: Int, property: Property) {
        val updated = _uiState.value.properties.toMutableList()
        updated[index] = property
        _uiState.value = _uiState.value.copy(properties = updated)
    }

    private fun updateFeaturedPropertyInList(index: Int, property: Property) {
        val updated = _rawFeaturedProperties.value.toMutableList()
        val rawIndex = updated.indexOfFirst {
            it.documentId == property.documentId || it.id == property.id
        }
        if (rawIndex != -1) {
            updated[rawIndex] = property
            _rawFeaturedProperties.value = updated
        }
        applyCategoryFilter()
    }

    private fun loadPromotionalProperty() {
        viewModelScope.launch {
            when (val result = getPromotionalPropertiesUseCase(PROMOTIONAL_PROPERTIES_LIMIT)) {
                is Result.Success -> {
                    Logger.d(TAG, "loadPromotionalProperty: received=${result.data.size}")
                    _rawPromotionalProperty.value = result.data.firstOrNull()
                    applyCategoryFilter()
                }
                is Result.Error -> {
                    Logger.e(TAG, "loadPromotionalProperty: failed ${result.message}")
                }
            }
        }
    }

    private fun loadPage(page: Int) {
        Logger.d(TAG, "loadPage: loading page=$page")
        viewModelScope.launch {
            val result = getAllPropertiesUseCase(effectiveFilter, page, limit)
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
                    _rawFeaturedProperties.value = result.data
                    applyCategoryFilter()
                }
                is Result.Error -> {
                    Logger.e(TAG, "loadFeaturedProperties: failed ${result.message}")
                }
            }
        }
    }

    private fun applyCategoryFilter() {
        val category = _selectedHomeCategory.value
        val normalizedCity = _uiState.value.currentFilter?.normalizedCity
        Logger.d(TAG, "applyCategoryFilter: category=$category, city=$normalizedCity")

        val filtered = _rawFeaturedProperties.value.filter { property ->
            category.matches(property) && cityMatches(property.city, normalizedCity)
        }
        _featuredProperties.value = filtered

        _promotionalProperty.value = _rawPromotionalProperty.value?.takeIf { property ->
            category.matches(property) && cityMatches(property.city, normalizedCity)
        }

        Logger.d(TAG, "applyCategoryFilter: featured=${filtered.size}, promo=${_promotionalProperty.value != null}")
    }

    private fun cityMatches(propertyCity: String, normalizedCity: String?): Boolean {
        if (normalizedCity == null) return true
        return propertyCity.lowercase() == normalizedCity
    }

    fun buildHeaderTitle(filter: PropertyFilter?, count: Int): String {
        val city = filter?.city
        val locality = filter?.localities?.firstOrNull()
        return when {
            !locality.isNullOrBlank() && !city.isNullOrBlank() -> {
                String.format(Locale.getDefault(), PropertiesStrings.PROPERTIES_FOUND_IN_LOCATION_FORMAT, count, locality, city)
            }
            !city.isNullOrBlank() -> {
                String.format(Locale.getDefault(), PropertiesStrings.PROPERTIES_FOUND_IN_CITY_FORMAT, count, city)
            }
            !locality.isNullOrBlank() -> {
                String.format(Locale.getDefault(), PropertiesStrings.PROPERTIES_FOUND_IN_CITY_FORMAT, count, locality)
            }
            else -> String.format(Locale.getDefault(), PropertiesStrings.PROPERTIES_FOUND_FORMAT, count)
        }
    }
}
