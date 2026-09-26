package com.fhiont.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhiont.feature.search.data.session.UserSession
import com.fhiont.feature.search.domain.model.Enquiry
import com.fhiont.feature.search.domain.usecase.GetEnquiriesByPropertyUseCase
import com.fhiont.feature.search.domain.usecase.GetMyEnquiriesUseCase
import com.fhiont.feature.search.domain.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyEnquiriesViewModel(
    private val getMyEnquiriesUseCase: GetMyEnquiriesUseCase,
    private val getEnquiriesByPropertyUseCase: GetEnquiriesByPropertyUseCase,
    private val userSession: UserSession,
    private val filterPropertyId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyEnquiriesUiState())
    val uiState: StateFlow<MyEnquiriesUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = if (filterPropertyId != null) {
                getEnquiriesByPropertyUseCase(filterPropertyId)
            } else {
                val userId = userSession.getUserId()
                if (userId.isNullOrBlank()) {
                    _uiState.value = MyEnquiriesUiState(
                        isLoading = false,
                        errorMessage = MyEnquiriesStrings.ERROR_NOT_LOGGED_IN
                    )
                    return@launch
                }
                getMyEnquiriesUseCase(userId)
            }

            when (result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        enquiries = result.data.groupByUserPropertyThread(),
                        isLoading = false,
                        errorMessage = null
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

/**
 * Groups enquiries by the same user + property pair so that multiple messages
 * from one user do not appear as duplicate cards. Keeps only the latest message
 * per thread, which is what the owner sees and taps to continue the chat.
 */
private fun List<Enquiry>.groupByUserPropertyThread(): List<Enquiry> {
    return groupBy { it.propertyId to it.userId }
        .mapNotNull { (_, thread) ->
            thread.maxByOrNull { it.createdAt.toEnquiryTimestamp() }
        }
}

private fun String?.toEnquiryTimestamp(): Long {
    if (isNullOrBlank()) return 0L
    return try {
        // Accept Unix timestamps in milliseconds or seconds.
        val numeric = trim().toDouble().toLong()
        // Seconds-since-epoch values are before year 2286; promote to ms.
        if (numeric < 1_000_000_000_000L) numeric * 1000L else numeric
    } catch (_: NumberFormatException) {
        try {
            // Fall back to ISO-8601 style strings; lexicographic compare works
            // for the standard "yyyy-MM-ddTHH:mm:ss" format.
            java.time.Instant.parse(this).toEpochMilli()
        } catch (_: Exception) {
            0L
        }
    }
}
