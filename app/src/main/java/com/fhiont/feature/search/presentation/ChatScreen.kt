package com.fhiont.feature.search.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.fhiont.feature.auth.domain.model.User
import com.fhiont.feature.search.data.session.UserSession
import com.fhiont.feature.search.domain.model.ChatMessage
import com.fhiont.feature.search.domain.model.Enquiry
import com.fhiont.feature.search.domain.usecase.GetChatMessagesUseCase
import com.fhiont.feature.search.domain.usecase.SendChatMessageUseCase
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.ui.theme.AppBackground
import com.fhiont.ui.theme.Black
import com.fhiont.ui.theme.ControlAccent
import com.fhiont.ui.theme.Error
import com.fhiont.ui.theme.FhiontTheme
import com.fhiont.ui.theme.Gray
import com.fhiont.ui.theme.HomeSearchBarBorder
import com.fhiont.ui.theme.OnControlAccent
import com.fhiont.ui.theme.SurfaceLight
import com.fhiont.ui.theme.TextHint
import com.fhiont.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Two-way chat thread attached to a property enquiry. The enquirer and the
 * property owner see the same screen; own messages sit on the right.
 */
@Composable
fun ChatScreen(
    enquiry: Enquiry,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = koinViewModel { parametersOf(enquiry) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Keep the latest message visible when the thread grows.
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
    ) {
        ChatHeader(enquiry = enquiry, onBack = onBack)

        Box(modifier = Modifier.weight(1f)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ControlAccent,
                    strokeWidth = ChatDims.LOADING_STROKE
                )

                uiState.errorMessage != null -> ChatError(
                    message = uiState.errorMessage.orEmpty(),
                    onRetry = viewModel::refresh,
                    modifier = Modifier.align(Alignment.Center)
                )

                uiState.messages.isEmpty() -> ChatEmpty(
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = ChatDims.LIST_HORIZONTAL_PADDING,
                        end = ChatDims.LIST_HORIZONTAL_PADDING,
                        top = ChatDims.LIST_TOP_PADDING,
                        bottom = ChatDims.LIST_BOTTOM_PADDING
                    ),
                    verticalArrangement = Arrangement.spacedBy(ChatDims.MESSAGE_SPACING)
                ) {
                    items(
                        items = uiState.messages,
                        key = { it.id }
                    ) { message ->
                        ChatBubble(
                            message = message,
                            isMine = message.senderId == uiState.myUserId
                        )
                    }
                }
            }
        }

        uiState.sendError?.let { error ->
            Text(
                text = error,
                color = Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ChatDims.COMPOSER_PADDING)
                    .padding(bottom = ChatDims.COMPOSER_ERROR_BOTTOM_PADDING)
            )
        }

        ChatComposer(
            value = uiState.inputText,
            isSending = uiState.isSending,
            onValueChange = viewModel::onInputChanged,
            onSend = viewModel::sendMessage
        )
    }
}

@Composable
private fun ChatHeader(
    enquiry: Enquiry,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = ChatDims.COMPOSER_ERROR_BOTTOM_PADDING,
                end = ChatDims.COMPOSER_PADDING,
                top = ChatDims.COMPOSER_ERROR_BOTTOM_PADDING,
                bottom = ChatDims.COMPOSER_ERROR_BOTTOM_PADDING
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = ChatStrings.CD_BACK,
                tint = Black,
                modifier = Modifier.size(ChatDims.BACK_ICON_SIZE)
            )
        }

        Box(
            modifier = Modifier
                .size(ChatDims.TOP_BAR_AVATAR_SIZE)
                .clip(RoundedCornerShape(ChatDims.TOP_BAR_AVATAR_CORNER_RADIUS))
                .background(HomeSearchBarBorder),
            contentAlignment = Alignment.Center
        ) {
            if (enquiry.propertyImage.isNotBlank()) {
                AsyncImage(
                    model = enquiry.propertyImage,
                    contentDescription = ChatStrings.CD_PROPERTY_IMAGE,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = ChatStrings.CD_PROPERTY_IMAGE,
                    tint = Gray,
                    modifier = Modifier.size(ChatDims.BACK_ICON_SIZE)
                )
            }
        }

        Spacer(modifier = Modifier.width(ChatDims.TOP_BAR_AVATAR_TEXT_SPACING))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = enquiry.propertyTitle,
                color = Black,
                fontSize = ChatDims.TOP_BAR_TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (enquiry.propertyLocation.isNotBlank()) {
                Text(
                    text = enquiry.propertyLocation,
                    color = Gray,
                    fontSize = ChatDims.TOP_BAR_SUBTITLE_FONT_SIZE,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    isMine: Boolean
) {
    val bubbleShape = if (isMine) {
        RoundedCornerShape(
            topStart = ChatDims.BUBBLE_CORNER_RADIUS,
            topEnd = ChatDims.BUBBLE_CORNER_RADIUS,
            bottomStart = ChatDims.BUBBLE_CORNER_RADIUS,
            bottomEnd = ChatDims.BUBBLE_FLAT_CORNER_RADIUS
        )
    } else {
        RoundedCornerShape(
            topStart = ChatDims.BUBBLE_CORNER_RADIUS,
            topEnd = ChatDims.BUBBLE_CORNER_RADIUS,
            bottomStart = ChatDims.BUBBLE_FLAT_CORNER_RADIUS,
            bottomEnd = ChatDims.BUBBLE_CORNER_RADIUS
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        if (!isMine && message.senderName.isNotBlank()) {
            Text(
                text = message.senderName,
                color = Gray,
                fontSize = ChatDims.BUBBLE_SENDER_FONT_SIZE,
                modifier = Modifier.padding(start = ChatDims.BUBBLE_FLAT_CORNER_RADIUS)
            )
        }

        // Row capped at a fraction of the screen width; the weight spacer
        // pushes the bubble to the correct edge so it wraps its content.
        Row(modifier = Modifier.fillMaxWidth(ChatDims.BUBBLE_MAX_WIDTH_FRACTION)) {
            if (isMine) {
                Spacer(modifier = Modifier.weight(1f))
            }

            Column(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(if (isMine) ControlAccent else SurfaceLight)
                    .padding(
                        horizontal = ChatDims.BUBBLE_HORIZONTAL_PADDING,
                        vertical = ChatDims.BUBBLE_VERTICAL_PADDING
                    )
            ) {
                Text(
                    text = message.message,
                    color = if (isMine) OnControlAccent else Black,
                    fontSize = ChatDims.BUBBLE_TEXT_FONT_SIZE
                )

                Spacer(modifier = Modifier.height(ChatDims.BUBBLE_TIME_SPACING))

                Text(
                    text = formatChatTimestamp(message.createdAt),
                    color = if (isMine) OnControlAccent.copy(alpha = ChatDims.TIME_TEXT_ALPHA)
                    else Gray.copy(alpha = ChatDims.TIME_TEXT_ALPHA),
                    fontSize = ChatDims.BUBBLE_TIME_FONT_SIZE,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            if (!isMine) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ChatComposer(
    value: String,
    isSending: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(ChatDims.COMPOSER_PADDING),
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = ChatStrings.HINT_MESSAGE,
                    color = TextHint
                )
            },
            maxLines = ChatDims.COMPOSER_FIELD_MAX_LINES,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default
            ),
            shape = RoundedCornerShape(ChatDims.COMPOSER_FIELD_CORNER_RADIUS),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Black,
                unfocusedTextColor = Black,
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedBorderColor = ControlAccent,
                unfocusedBorderColor = HomeSearchBarBorder
            )
        )

        Spacer(modifier = Modifier.width(ChatDims.COMPOSER_SPACING))

        IconButton(
            onClick = onSend,
            enabled = value.isNotBlank() && !isSending,
            modifier = Modifier
                .size(ChatDims.COMPOSER_SEND_BUTTON_SIZE)
                .background(ControlAccent, CircleShape)
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    color = OnControlAccent,
                    strokeWidth = ChatDims.LOADING_STROKE,
                    modifier = Modifier.size(ChatDims.COMPOSER_SEND_ICON_SIZE)
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = ChatStrings.CD_SEND,
                    tint = OnControlAccent,
                    modifier = Modifier.size(ChatDims.COMPOSER_SEND_ICON_SIZE)
                )
            }
        }
    }
}

@Composable
private fun ChatEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(ChatDims.ERROR_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = null,
            tint = Gray,
            modifier = Modifier.size(ChatDims.EMPTY_ICON_SIZE)
        )
        Spacer(modifier = Modifier.height(ChatDims.EMPTY_TITLE_SPACING))
        Text(
            text = ChatStrings.EMPTY_TITLE,
            color = Black,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(ChatDims.EMPTY_SUBTITLE_SPACING))
        Text(
            text = ChatStrings.EMPTY_SUBTITLE,
            color = Gray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ChatError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(ChatDims.ERROR_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = Black,
            style = MaterialTheme.typography.bodyMedium
        )
        TextButton(onClick = onRetry) {
            Text(
                text = ChatStrings.RETRY,
                color = ControlAccent,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Server timestamps are UTC (`yyyy-MM-dd HH:mm:ss`). Show just the time for
 * messages sent today; older ones include the day and month.
 */
private fun formatChatTimestamp(createdAt: String): String {
    return try {
        val parser = SimpleDateFormat(ChatStrings.SERVER_TIMESTAMP_FORMAT, Locale.US).apply {
            timeZone = TimeZone.getTimeZone(ChatStrings.UTC_ZONE)
        }
        val date: Date = parser.parse(createdAt) ?: return ""
        val messageDay = Calendar.getInstance().apply { time = date }
        val today = Calendar.getInstance()
        val sameDay = messageDay.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                messageDay.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        val pattern = if (sameDay) ChatStrings.TIME_FORMAT else ChatStrings.DATE_TIME_FORMAT
        SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    } catch (_: Exception) {
        ""
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenPreview() {
    FhiontTheme {
        ChatScreen(
            enquiry = Enquiry(
                id = "1",
                propertyId = "p1",
                propertyTitle = "2 BHK Apartment",
                propertyLocation = "Porvorim, Goa",
                propertyImage = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=400&q=80",
                agentPhone = "1234567890",
                message = "I am interested in this property.",
                userId = "u1",
                status = "new",
                createdAt = "2026-09-10 10:00:00"
            ),
            onBack = {},
            viewModel = ChatViewModel(
                getChatMessagesUseCase = object : GetChatMessagesUseCase {
                    override suspend fun invoke(enquiryId: String): Result<List<ChatMessage>> {
                        return Result.Success(
                            listOf(
                                ChatMessage(
                                    id = "m1",
                                    enquiryId = "1",
                                    senderId = "u1",
                                    senderName = "Rahul",
                                    message = "Hi, is this property still available?",
                                    createdAt = "2026-09-26 10:00:00"
                                ),
                                ChatMessage(
                                    id = "m2",
                                    enquiryId = "1",
                                    senderId = "u2",
                                    senderName = "Priya",
                                    message = "Yes, it is. Would you like to schedule a visit?",
                                    createdAt = "2026-09-26 10:02:00"
                                )
                            )
                        )
                    }
                },
                sendChatMessageUseCase = object : SendChatMessageUseCase {
                    override suspend fun invoke(
                        enquiryId: String,
                        message: String
                    ): Result<ChatMessage> {
                        return Result.Success(
                            ChatMessage(
                                id = "m3",
                                enquiryId = enquiryId,
                                senderId = "u1",
                                senderName = "Me",
                                message = message,
                                createdAt = "2026-09-26 10:05:00"
                            )
                        )
                    }
                },
                userSession = object : UserSession {
                    override val user: StateFlow<User?> = MutableStateFlow(null)
                    override fun getUserId(): String? = "u1"
                    override fun getUser(): User? = null
                    override fun setUser(user: User?) {}
                    override fun clear() {}
                },
                enquiry = Enquiry(
                    id = "1",
                    propertyId = "p1",
                    propertyTitle = "2 BHK Apartment",
                    propertyLocation = "Porvorim, Goa",
                    propertyImage = "",
                    agentPhone = "1234567890",
                    message = "I am interested in this property.",
                    userId = "u1",
                    status = "new",
                    createdAt = "2026-09-10 10:00:00"
                )
            )
        )
    }
}
