package com.fhiont.feature.search.presentation

import com.fhiont.feature.search.domain.model.ChatMessage

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    val errorMessage: String? = null,
    val sendError: String? = null,
    val myUserId: String = ""
)
