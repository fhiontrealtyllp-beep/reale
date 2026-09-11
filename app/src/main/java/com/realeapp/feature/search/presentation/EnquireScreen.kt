package com.realeapp.feature.search.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.realeapp.feature.search.domain.model.Property
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
import com.realeapp.ui.theme.White
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnquireBottomSheet(
    property: Property,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            onSend = { message ->
                if (property.agentPhone.isBlank()) {
                    Toast.makeText(context, EnquiryStrings.AGENT_PHONE_UNAVAILABLE, Toast.LENGTH_SHORT).show()
                } else {
                    val body = buildEnquiryMessage(property, message)
                    sendEnquirySms(context, property.agentPhone, body)
                }
                onDismiss()
            }
        )
    }
}

@Composable
private fun EnquireSheetContent(
    property: Property,
    onClose: () -> Unit,
    onSend: (String) -> Unit
) {
    val context = LocalContext.current

    var message by remember { mutableStateOf("") }
    var messageError by remember { mutableStateOf<String?>(null) }

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
            )
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

        Text(
            text = EnquiryStrings.TITLE,
            color = Black,
            fontSize = EnquiryDims.TITLE_FONT_SIZE,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(EnquiryDims.HEADER_SPACING))

        Text(
            text = EnquiryStrings.SUBTITLE,
            color = Gray,
            fontSize = EnquiryDims.SUBTITLE_FONT_SIZE
        )

        Spacer(modifier = Modifier.height(EnquiryDims.FORM_FIELD_SPACING))

        EnquiryTextField(
            value = message,
            onValueChange = {
                message = it
                messageError = null
            },
            label = EnquiryStrings.LABEL_MESSAGE,
            placeholder = EnquiryStrings.HINT_MESSAGE,
            leadingIcon = Icons.AutoMirrored.Filled.Chat,
            isError = messageError != null,
            supportingText = messageError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(EnquiryDims.BUTTON_TOP_SPACING))

        Button(
            onClick = {
                if (message.isBlank()) {
                    messageError = EnquiryStrings.ERROR_MESSAGE_REQUIRED
                } else {
                    onSend(message)
                    Toast.makeText(context, EnquiryStrings.BUTTON_SEND, Toast.LENGTH_SHORT).show()
                }
            },
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
                text = EnquiryStrings.BUTTON_SEND,
                fontWeight = FontWeight.Bold
            )
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
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
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

private fun buildEnquiryMessage(
    property: Property,
    message: String
): String {
    return String.format(
        Locale.getDefault(),
        EnquiryStrings.ENQUIRY_SMS_TEMPLATE,
        property.title,
        buildShortLocation(property),
        message.trim()
    )
}

private fun sendEnquirySms(context: Context, phone: String, body: String) {
    if (phone.isBlank()) return
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse(DetailStrings.SMS_URI_PREFIX + phone.trim())
        putExtra("sms_body", body)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
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
                onDismiss = {}
            )
        }
    }
}
