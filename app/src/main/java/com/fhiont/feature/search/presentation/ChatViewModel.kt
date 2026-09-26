package com.fhiont.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhiont.feature.search.data.session.UserSession
import com.fhiont.feature.search.domain.model.ChatMessage
import com.fhiont.feature.search.domain.model.Enquiry
import com.fhiont.feature.search.domain.usecase.GetChatMessagesUseCase
import com.fhiont.feature.search.domain.usecase.SendChatMessageUseCase
import com.fhiont.feature.search.domain.utils.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val POLL_INTERVAL_MS = 4_000L

/**
 * ViewModel for the per-enquiry chat thread between the enquirer and the
 * property owner. Polls the server for new messages while the screen is open.
 */
class ChatViewModel(
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val userSession: UserSession,
    private val enquiry: Enquiry
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatUiState(myUserId = userSession.getUserId().orEmpty())
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                loadMessages(showSpinner = _uiState.value.messages.isEmpty())
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch { loadMessages(showSpinner = true) }
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
        when (val result = getChatMessagesUseCase(enquiry.id)) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(
                    messages = result.data,
                    isLoading = false,
                    errorMessage = null
                )
            }
            is Result.Error -> {
                // Only surface poll failures when there is nothing to show yet;
                // a transient error should not wipe an existing conversation.
                if (_uiState.value.messages.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
}
