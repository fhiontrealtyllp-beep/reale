package com.realeapp.feature.saved.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.core.like.LikeStateManager
import com.realeapp.feature.saved.domain.usecase.GetLikedPropertiesUseCase
import com.realeapp.feature.search.data.session.SessionObserver
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.usecase.UpdatePropertyLikeUseCase
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

private const val TAG = "SavedViewModel"

class SavedViewModel(
    private val getLikedPropertiesUseCase: GetLikedPropertiesUseCase,
    private val updatePropertyLikeUseCase: UpdatePropertyLikeUseCase,
    private val userSession: UserSession,
    private val likeStateManager: LikeStateManager = LikeStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedUiState())
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<String>()
    val sideEffect: SharedFlow<String> = _sideEffect.asSharedFlow()

    init {
        Logger.d(TAG, "init")
        load()
        observeLikeState()
        SessionObserver(
            userSession = userSession,
            scope = viewModelScope,
            onLogin = {
                Logger.d(TAG, "onLogin")
                load()
            },
            onLogout = {
                Logger.d(TAG, "onLogout")
                _uiState.value = SavedUiState(isLoading = false, isLoggedIn = false)
            }
        )
    }

    private fun observeLikeState() {
        viewModelScope.launch {
            combine(
                likeStateManager.likedIds,
                likeStateManager.likedPropertyCache
            ) { _, _ ->
                rebuildSavedList()
            }.collect { properties ->
                Logger.d(TAG, "observeLikeState: emitting ${properties.size} properties")
                if (_uiState.value.isLoggedIn) {
                    _uiState.value = _uiState.value.copy(properties = properties)
                }
            }
        }
    }

    private fun rebuildSavedList(): List<Property> {
        val current = _uiState.value
        if (!current.isLoggedIn) {
            Logger.d(TAG, "rebuildSavedList: not logged in, returning current list")
            return current.properties
        }

        val remoteIds = current.properties.map { it.documentId ?: it.id }.toSet()
        val merged = current.properties.toMutableList()
        likeStateManager.likedPropertyCache.value.values.forEach { prop ->
            val id = prop.documentId ?: prop.id
            if (id !in remoteIds && likeStateManager.isLiked(id)) {
                Logger.d(TAG, "rebuildSavedList: adding search-liked property id=$id")
                merged.add(prop.copy(isLiked = true))
            }
        }
        val filtered = merged.filter { likeStateManager.isLiked(it.documentId ?: it.id) }
        Logger.d(TAG, "rebuildSavedList: merged=${merged.size}, filtered=${filtered.size}")
        return filtered
    }

    fun load() {
        Logger.d(TAG, "load: start")
        val userId = userSession.getUserId()
        if (userId.isNullOrEmpty()) {
            Logger.d(TAG, "load: user not logged in")
            _uiState.value = SavedUiState(
                isLoading = false,
                isLoggedIn = false
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            Logger.d(TAG, "load: calling getLikedPropertiesUseCase userId=$userId")
            when (val result = getLikedPropertiesUseCase(userId)) {
                is Result.Success -> {
                    Logger.d(TAG, "load: got ${result.data.size} liked properties")
                    val properties = result.data.map { it.copy(isLiked = true) }
                    val likedIds = properties.map { it.documentId ?: it.id }.toSet()
                    likeStateManager.syncLikedIds(likedIds)
                    likeStateManager.syncLikedProperties(properties)
                    _uiState.value = SavedUiState(
                        isLoading = false,
                        properties = properties,
                        errorMessage = null,
                        isLoggedIn = true
                    )
                }
                is Result.Error -> {
                    Logger.e(TAG, "load: failed ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun onLikeClicked(propertyId: String) {
        Logger.d(TAG, "onLikeClicked: propertyId=$propertyId")
        val property = _uiState.value.properties.find {
            it.documentId == propertyId || it.id == propertyId
        }
        if (property == null) {
            Logger.w(TAG, "onLikeClicked: property not found id=$propertyId")
            return
        }

        val oldIsLiked = property.isLiked ?: true
        val newIsLiked = !oldIsLiked
        Logger.d(TAG, "onLikeClicked: toggling id=$propertyId from isLiked=$oldIsLiked to isLiked=$newIsLiked")

        likeStateManager.setLiked(property, newIsLiked)

        viewModelScope.launch {
            val targetId = property.documentId ?: propertyId
            Logger.d(TAG, "onLikeClicked: invoking updatePropertyLikeUseCase id=$targetId, isLiked=$newIsLiked")
            val result = updatePropertyLikeUseCase(targetId, newIsLiked)
            when (result) {
                is Result.Success -> {
                    Logger.d(TAG, "onLikeClicked: updatePropertyLikeUseCase success id=$targetId")
                }
                is Result.Error -> {
                    Logger.e(TAG, "onLikeClicked: updatePropertyLikeUseCase failed id=$targetId, reverting")
                    likeStateManager.setLiked(property, oldIsLiked)
                    _sideEffect.emit("Failed to update like: ${result.message}")
                }
            }
        }
    }

    fun refresh() {
        Logger.d(TAG, "refresh")
        load()
    }
}
