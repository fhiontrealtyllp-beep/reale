package com.realeapp.feature.search.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.usecase.SendEnquiryUseCase
import com.realeapp.feature.search.presentation.components.formatIndianPrice
import com.realeapp.ui.preview.PreviewData
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.Gray
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.TextHint
import com.realeapp.ui.theme.VerifiedGreen
import com.realeapp.ui.theme.White
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnquireBottomSheet(
    property: Property,
    onDismiss: () -> Unit,
    onViewEnquiries: () -> Unit = onDismiss,
    viewModel: EnquireViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.reset()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = White,
        contentColor = Black,
        shape = RoundedCornerShape(
            topStart = EnquiryDims.SHEET_CORNER_RADIUS,
            topEnd = EnquiryDims.SHEET_CORNER_RADIUS
        ),
        tonalElevation = 0.dp,
        contentWindowInsets = { androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0) }
    ) {
        EnquireSheetContent(
            property = property,
            onClose = onDismiss,
            onViewEnquiries = onViewEnquiries,
            uiState = uiState,
            onMessageChange = viewModel::onMessageChanged,
            onSend = { viewModel.sendEnquiry(property) }
        )
    }
}

@Composable
private fun EnquireSheetContent(
    property: Property,
    onClose: () -> Unit,
    onViewEnquiries: () -> Unit,
    uiState: EnquireUiState,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .imePadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(
                start = EnquiryDims.SHEET_PADDING,
                end = EnquiryDims.SHEET_PADDING,
                top = EnquiryDims.SHEET_TOP_PADDING,
                bottom = EnquiryDims.SHEET_BOTTOM_PADDING
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = DetailStrings.CD_CLOSE,
                    tint = Gray,
                    modifier = Modifier.size(EnquiryDims.FORM_FIELD_ICON_SIZE)
                )
            }
        }

        if (uiState.isSuccess) {
            EnquirySuccessContent(
                property = property,
                onBack = onClose,
                onViewEnquiries = onViewEnquiries
            )
        } else {
            EnquiryFormContent(
                message = uiState.message,
                messageError = uiState.messageError,
                submitError = uiState.submitError,
                isLoading = uiState.isLoading,
                onMessageChange = onMessageChange,
                onSend = onSend
            )
        }
    }
}

@Composable
private fun EnquiryFormContent(
    message: String,
    messageError: String?,
    submitError: String?,
    isLoading: Boolean,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Text(
        text = EnquiryStrings.TITLE,
        modifier = Modifier.fillMaxWidth(),
        color = Black,
        fontSize = EnquiryDims.TITLE_FONT_SIZE,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(EnquiryDims.HEADER_SPACING))

    Text(
        text = EnquiryStrings.SUBTITLE,
        modifier = Modifier.fillMaxWidth(),
        color = Gray,
        fontSize = EnquiryDims.SUBTITLE_FONT_SIZE
    )

    Spacer(modifier = Modifier.height(EnquiryDims.FORM_FIELD_SPACING))

    EnquiryTextField(
        value = message,
        onValueChange = onMessageChange,
        label = EnquiryStrings.LABEL_MESSAGE,
        placeholder = EnquiryStrings.HINT_MESSAGE,
        leadingIcon = Icons.AutoMirrored.Filled.Chat,
        isError = messageError != null,
        supportingText = messageError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        enabled = !isLoading
    )

    Spacer(modifier = Modifier.height(EnquiryDims.BUTTON_TOP_SPACING))

    Button(
        onClick = onSend,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(EnquiryDims.BUTTON_HEIGHT),
        shape = RoundedCornerShape(EnquiryDims.BUTTON_CORNER_RADIUS),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandCoral,
            contentColor = OnBrandContent
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = OnBrandContent,
                strokeWidth = EnquiryDims.LOADING_INDICATOR_STROKE,
                modifier = Modifier.size(EnquiryDims.FORM_FIELD_ICON_SIZE)
            )
        } else {
            Text(
                text = EnquiryStrings.BUTTON_SEND,
                fontWeight = FontWeight.Bold
            )
        }
    }

    submitError?.let { error ->
        Spacer(modifier = Modifier.height(EnquiryDims.HEADER_SPACING))

        Text(
            text = error,
            modifier = Modifier.fillMaxWidth(),
            color = BrandCoral,
            fontSize = EnquiryDims.SUBTITLE_FONT_SIZE
        )
    }
}

@Composable
private fun EnquirySuccessContent(
    property: Property,
    onBack: () -> Unit,
    onViewEnquiries: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(EnquiryDims.SUCCESS_ICON_BACKGROUND_SIZE)
                .background(BrandBlue.copy(alpha = 0.1f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(EnquiryDims.SUCCESS_ICON_SIZE)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(EnquiryDims.SUCCESS_CHECK_SIZE)
                    .background(VerifiedGreen, shape = CircleShape)
                    .padding(EnquiryDims.HEADER_SPACING),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = OnBrandContent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(EnquiryDims.SUCCESS_TITLE_TOP_SPACING))

        Text(
            text = EnquiryStrings.SUCCESS_TITLE,
            color = Black,
            fontSize = EnquiryDims.TITLE_FONT_SIZE,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(EnquiryDims.SUCCESS_SUBTITLE_TOP_SPACING))

        Text(
            text = EnquiryStrings.SUCCESS_SUBTITLE,
            color = Gray,
            fontSize = EnquiryDims.SUBTITLE_FONT_SIZE,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(EnquiryDims.SUCCESS_PROPERTY_CARD_TOP_SPACING))

        EnquirySuccessPropertyCard(property = property)

        Spacer(modifier = Modifier.height(EnquiryDims.SUCCESS_BUTTON_TOP_SPACING))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(EnquiryDims.BUTTON_HEIGHT),
            shape = RoundedCornerShape(EnquiryDims.BUTTON_CORNER_RADIUS),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandCoral,
                contentColor = OnBrandContent
            )
        ) {
            Text(
                text = EnquiryStrings.SUCCESS_BUTTON_BACK,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(EnquiryDims.SUCCESS_LINK_TOP_SPACING))

        TextButton(onClick = onViewEnquiries) {
            Text(
                text = EnquiryStrings.SUCCESS_LINK_ENQUIRIES,
                color = BrandBlue,
                fontSize = EnquiryDims.SUCCESS_LINK_FONT_SIZE,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EnquirySuccessPropertyCard(
    property: Property,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(EnquiryDims.FORM_FIELD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = EnquiryDims.PROPERTY_CARD_ELEVATION)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EnquiryDims.PROPERTY_CARD_SPACING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = property.images.firstOrNull { it.isNotBlank() }
                ?: (DetailStrings.FALLBACK_IMAGE_PREFIX + property.id + DetailStrings.FALLBACK_IMAGE_SUFFIX)

            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(EnquiryDims.PROPERTY_CARD_IMAGE_SIZE)
                    .clip(RoundedCornerShape(EnquiryDims.PROPERTY_CARD_IMAGE_CORNER_RADIUS))
            )

            Spacer(modifier = Modifier.width(EnquiryDims.PROPERTY_CARD_SPACING))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = property.title,
                    color = Black,
                    fontSize = EnquiryDims.SUBTITLE_FONT_SIZE,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(EnquiryDims.HEADER_SPACING))

                Text(
                    text = buildShortLocation(property),
                    color = Gray,
                    fontSize = EnquiryDims.SUBTITLE_FONT_SIZE,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(EnquiryDims.HEADER_SPACING))

                Text(
                    text = formatIndianPrice(property.price, property.isRentProperty()),
                    color = BrandBlue,
                    fontSize = EnquiryDims.SUBTITLE_FONT_SIZE,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EnquiryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    prefix: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = Gray,
                modifier = Modifier.size(EnquiryDims.FORM_FIELD_ICON_SIZE)
            )
        },
        prefix = prefix,
        isError = isError,
        supportingText = if (supportingText != null) { { Text(supportingText) } } else null,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(EnquiryDims.FORM_FIELD_CORNER_RADIUS),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Black,
            unfocusedTextColor = Black,
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            focusedBorderColor = BrandBlue,
            unfocusedBorderColor = HomeSearchBarBorder,
            focusedLabelColor = Gray,
            unfocusedLabelColor = Gray,
            focusedLeadingIconColor = Gray,
            unfocusedLeadingIconColor = Gray,
            focusedPlaceholderColor = TextHint,
            unfocusedPlaceholderColor = TextHint,
            errorBorderColor = BrandCoral,
            errorLabelColor = BrandCoral,
            errorSupportingTextColor = BrandCoral,
            focusedPrefixColor = Black,
            unfocusedPrefixColor = Black
        )
    )
}

private fun buildShortLocation(property: Property): String {
    return listOf(property.locality, property.city)
        .filter { it.isNotBlank() }
        .joinToString(", ")
}

private val PreviewSendEnquiryUseCase = object : SendEnquiryUseCase {
    override suspend fun invoke(property: Property, message: String): com.realeapp.feature.search.domain.utils.Result<Unit> {
        return com.realeapp.feature.search.domain.utils.Result.Success(Unit)
    }
}

@Preview(showBackground = true)
@Composable
private fun EnquireBottomSheetPreview() {
    RealeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
        ) {
            EnquireBottomSheet(
                property = PreviewData.sampleProperty,
                onDismiss = {},
                viewModel = EnquireViewModel(PreviewSendEnquiryUseCase)
            )
        }
    }
}
