package com.fhiont.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhiont.core.notification.EnquiryNotificationUpdates
import com.fhiont.feature.search.data.local.ChatReadStore
import com.fhiont.feature.search.data.session.UserSession
import com.fhiont.feature.search.domain.model.ChatMessage
import com.fhiont.feature.search.domain.model.Enquiry
import com.fhiont.feature.search.domain.usecase.GetChatMessagesUseCase
import com.fhiont.feature.search.domain.usecase.GetEnquiriesByPropertyUseCase
import com.fhiont.feature.search.domain.usecase.SendChatMessageUseCase
import com.fhiont.feature.search.domain.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * ViewModel for the per-enquiry chat thread between the enquirer and the
 * property owner. Loads messages once on open and refreshes when an FCM push
 * notification for any enquiry arrives. Manual refresh is also available.
 */
class ChatViewModel(
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val getEnquiriesByPropertyUseCase: GetEnquiriesByPropertyUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val userSession: UserSession,
    private val chatReadStore: ChatReadStore,
    private val enquiryNotificationUpdates: EnquiryNotificationUpdates,
    private val enquiry: Enquiry
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatUiState(myUserId = userSession.getUserId().orEmpty())
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Cache the set of related enquiry ids so we don't hit enquiries.php on
    // every poll. They only change when a brand-new enquiry row is created.
    private var relatedEnquiryIds: List<String>? = null

    init {
        // Initial load.
        viewModelScope.launch {
            loadMessages(showSpinner = _uiState.value.messages.isEmpty())
        }
        // Refresh when an enquiry-related push notification arrives while
        // this chat is open.
        viewModelScope.launch {
            enquiryNotificationUpdates.updates.collect {
                loadMessages(showSpinner = false)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            relatedEnquiryIds = null
            loadMessages(showSpinner = true)
        }
    }

    fun onInputChanged(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text, sendError = null)
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty() || _uiState.value.isSending) return

        _uiState.value = _uiState.value.copy(isSending = true, sendError = null)

        viewModelScope.launch {
            when (val result = sendChatMessageUseCase(enquiry.id, text)) {
                is Result.Success -> {
                    val appended = result.data
                    val current = _uiState.value
                    _uiState.value = current.copy(
                        inputText = "",
                        isSending = false,
                        messages = if (current.messages.any { it.id == appended.id }) {
                            current.messages
                        } else {
                            current.messages + appended
                        }
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSending = false,
                        sendError = result.message
                    )
                }
            }
        }
    }

    private suspend fun loadMessages(showSpinner: Boolean) {
        if (showSpinner) {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        }

        // The backend can create multiple enquiry rows for the same user-property
        // pair, so collect message IDs across the whole thread.
        val relatedIds = ensureRelatedEnquiryIds()

        val allMessages = mutableListOf<ChatMessage>()
        var firstError: String? = null
        for (id in relatedIds) {
            when (val result = getChatMessagesUseCase(id)) {
                is Result.Success -> allMessages.addAll(result.data)
                is Result.Error -> if (firstError == null) firstError = result.message
            }
        }

        if (allMessages.isNotEmpty() || firstError == null) {
            _uiState.value = _uiState.value.copy(
                messages = allMessages.sortedBy { it.createdAt.toChatTimestamp() },
                isLoading = false,
                errorMessage = null
            )
            allMessages.maxByOrNull { it.createdAt.toChatTimestamp() }?.createdAt?.let { latest ->
                chatReadStore.markRead(enquiry.id, latest)
            }
        } else if (_uiState.value.messages.isEmpty()) {
            // Only surface poll failures when there is nothing to show yet.
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = firstError
            )
        }
    }

    private suspend fun ensureRelatedEnquiryIds(): List<String> {
        relatedEnquiryIds?.let { return it }
        return when (val result = getEnquiriesByPropertyUseCase(enquiry.propertyId)) {
            is Result.Success -> {
                result.data
                    .filter { it.userId == enquiry.userId && it.userId != null }
                    .map { it.id }
                    .ifEmpty { listOf(enquiry.id) }
                    .also { relatedEnquiryIds = it }
            }
            is Result.Error -> listOf(enquiry.id).also { relatedEnquiryIds = it }
        }
    }
}

private fun String?.toChatTimestamp(): Long {
    if (isNullOrBlank()) return 0L
    return try {
        val parser = SimpleDateFormat(ChatStrings.SERVER_TIMESTAMP_FORMAT, Locale.US).apply {
            timeZone = TimeZone.getTimeZone(ChatStrings.UTC_ZONE)
        }
        parser.parse(this)?.time ?: 0L
    } catch (_: Exception) {
        try {
            val numeric = trim().toDouble().toLong()
            if (numeric < 1_000_000_000_000L) numeric * 1000L else numeric
        } catch (_: NumberFormatException) {
            try {
                java.time.Instant.parse(this).toEpochMilli()
            } catch (_: Exception) {
                0L
            }
        }
    }
}
