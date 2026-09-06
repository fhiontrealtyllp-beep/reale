package com.realeapp.feature.add.presentation

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.BrandRed
import com.realeapp.ui.theme.Error
import com.realeapp.ui.theme.HomeCategoryUnselected
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.White
import java.io.ByteArrayOutputStream

@Composable
internal fun StepHeader(
    step: AddPropertyStep,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AddDims.STEP_TITLE_SUBTITLE_SPACING)
    ) {
        Text(
            text = step.shortLabel,
            color = Black,
            fontSize = AddDims.STEP_TITLE_FONT_SIZE,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = step.subtitle,
            color = HomeTextSecondary,
            fontSize = AddDims.STEP_SUBTITLE_FONT_SIZE
        )
    }
}

@Composable
internal fun StepIndicator(
    steps: List<AddPropertyStep>,
    currentStep: AddPropertyStep,
    modifier: Modifier = Modifier
) {
    // Each step column draws half-connector lines on both sides of its circle so
    // adjacent columns join into a continuous line that touches circle edges.
    Row(modifier = modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            val isCompleted = step.index < currentStep.index
            val isCurrent = step == currentStep
            val isReached = step.index <= currentStep.index

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AddDims.STEP_CIRCLE_SIZE),
                    contentAlignment = Alignment.Center
                ) {
                    if (index > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxWidth(0.5f)
                                .height(AddDims.STEP_CONNECTOR_HEIGHT)
                                .background(if (isReached) BrandBlue else HomeSearchBarBorder)
                        )
                    }
                    if (index < steps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxWidth(0.5f)
                                .height(AddDims.STEP_CONNECTOR_HEIGHT)
                                .background(if (isCompleted) BrandBlue else HomeSearchBarBorder)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(AddDims.STEP_CIRCLE_SIZE)
                            .clip(CircleShape)
                            .background(if (isReached) BrandBlue else White)
                            .then(
                                if (isReached) {
                                    Modifier
                                } else {
                                    Modifier.border(
                                        AddDims.STEP_CIRCLE_BORDER_WIDTH,
                                        HomeSearchBarBorder,
                                        CircleShape
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.index}",
                            color = if (isReached) White else HomeTextSecondary,
                            fontSize = AddDims.STEP_NUMBER_FONT_SIZE,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(AddDims.STEP_LABEL_TOP_SPACING))
                Text(
                    text = step.shortLabel,
                    color = if (isCurrent) BrandBlue else HomeTextSecondary,
                    fontSize = AddDims.STEP_LABEL_FONT_SIZE,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
internal fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        color = Black,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
internal fun FieldLabel(
    text: String,
    isRequired: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = Black,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        if (isRequired) {
            Text(
                text = AddStrings.REQUIRED_MARKER,
                color = BrandRed
            )
        }
    }
}

@Composable
internal fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AddDims.FIELD_LABEL_SPACING)
    ) {
        FieldLabel(text = label, isRequired = isRequired)
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(AddStrings.ENTER_PREFIX + label) },
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            maxLines = if (minLines > 1) 4 else 1,
            keyboardOptions = keyboardOptions,
            singleLine = minLines == 1,
            shape = RoundedCornerShape(AddDims.FIELD_CORNER_RADIUS),
            colors = formFieldColors(),
            trailingIcon = trailingIcon
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun <T> FormDropdown(
    label: String,
    options: List<T>,
    selected: T?,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AddDims.FIELD_LABEL_SPACING)
    ) {
        FieldLabel(text = label, isRequired = isRequired)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selected?.let { optionLabel(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(AddStrings.SELECT_PREFIX + label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(AddDims.FIELD_CORNER_RADIUS),
                colors = formFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionLabel(option)) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun <T> ToggleRow(
    options: List<T>,
    selected: T?,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            TextButton(
                onClick = { onSelected(option) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (isSelected) BrandBlue else HomeCategoryUnselected,
                    contentColor = if (isSelected) White else Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = optionLabel(option),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun <T> FilterChipGroup(
    options: List<T>,
    selected: List<T>,
    optionLabel: (T) -> String,
    onToggle: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option in selected
            FilterChip(
                selected = isSelected,
                onClick = { onToggle(option) },
                label = { Text(optionLabel(option)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandBlue,
                    selectedLabelColor = White,
                    containerColor = HomeCategoryUnselected,
                    labelColor = Black
                )
            )
        }
    }
}

@Composable
internal fun ContinueButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(AddDims.CONTINUE_BUTTON_HEIGHT),
        enabled = enabled,
        shape = RoundedCornerShape(AddDims.CONTINUE_BUTTON_CORNER_RADIUS),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandCoral,
            contentColor = White,
            disabledContainerColor = HomeTextSecondary,
            disabledContentColor = White
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.width(AddDims.PROGRESS_INDICATOR_SIZE),
                color = White,
                strokeWidth = AddDims.PROGRESS_INDICATOR_STROKE
            )
        } else {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = AddDims.CONTINUE_BUTTON_FONT_SIZE
            )
            Spacer(modifier = Modifier.width(AddDims.CONTINUE_BUTTON_ICON_TEXT_SPACING))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = AddStrings.CD_CONTINUE,
                modifier = Modifier.size(AddDims.CONTINUE_BUTTON_ICON_SIZE)
            )
        }
    }
}

@Composable
internal fun StepNavigationButtons(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    nextLabel: String = AddStrings.ACTION_CONTINUE,
    showPrevious: Boolean = true,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AddDims.NAV_BUTTON_SPACING)
    ) {
        if (showPrevious) {
            androidx.compose.material3.OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier
                    .weight(1f)
                    .height(AddDims.NAV_BUTTON_HEIGHT),
                shape = RoundedCornerShape(AddDims.NAV_BUTTON_CORNER_RADIUS),
                border = androidx.compose.foundation.BorderStroke(
                    AddDims.NAV_BUTTON_BORDER_WIDTH,
                    HomeSearchBarBorder
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Black,
                    containerColor = White
                )
            ) {
                Text(
                    text = AddStrings.ACTION_BACK,
                    fontWeight = FontWeight.Bold,
                    fontSize = AddDims.CONTINUE_BUTTON_FONT_SIZE
                )
            }
        }
        ContinueButton(
            text = nextLabel,
            onClick = onNext,
            isLoading = isLoading,
            modifier = if (showPrevious) Modifier.weight(2f) else Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun ValidationErrorList(
    errors: List<String>,
    modifier: Modifier = Modifier
) {
    if (errors.isNotEmpty()) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            errors.forEach { error ->
                Text(
                    text = AddStrings.BULLET_PREFIX + error,
                    color = Error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
internal fun formFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = Black,
    unfocusedTextColor = Black,
    focusedContainerColor = HomeCategoryUnselected,
    unfocusedContainerColor = HomeCategoryUnselected,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedLabelColor = BrandBlue,
    unfocusedLabelColor = HomeTextSecondary,
    cursorColor = BrandBlue
)

@Composable
internal fun ImageUrlsSection(
    images: List<String>,
    isUploadingImage: Boolean,
    onAddImages: () -> Unit,
    onRemoveImage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader(AddStrings.SECTION_PROPERTY_PHOTOS)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AddImageTile(
                isUploading = isUploadingImage,
                onClick = onAddImages
            )

            images.forEach { url ->
                ImagePreviewTile(
                    url = url,
                    onRemove = { onRemoveImage(url) }
                )
            }
        }
    }
}

@Composable
internal fun AddImageTile(
    isUploading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = BrandBlue,
                shape = RoundedCornerShape(12.dp)
            )
            .background(HomeCategoryUnselected)
            .clickable(enabled = !isUploading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isUploading) {
            CircularProgressIndicator(
                color = BrandBlue,
                modifier = Modifier.size(32.dp),
                strokeWidth = 2.dp
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = AddStrings.CD_ADD_IMAGES,
                    tint = BrandBlue,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = AddStrings.ACTION_ADD_PHOTOS,
                    color = BrandBlue,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
internal fun ImagePreviewTile(
    url: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(HomeCategoryUnselected)
    ) {
        AsyncImage(
            model = url,
            contentDescription = AddStrings.CD_PROPERTY_IMAGE,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = AddStrings.CD_REMOVE_IMAGE,
                tint = White
            )
        }
    }
}

@Composable
internal fun ImageSourceDialog(
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = AddStrings.DIALOG_IMAGE_SOURCE_TITLE,
                    color = Black,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCamera() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = AddStrings.CD_CAMERA,
                        tint = BrandBlue,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = AddStrings.IMAGE_SOURCE_CAMERA,
                        color = Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onGallery() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = AddStrings.CD_GALLERY,
                        tint = BrandBlue,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = AddStrings.IMAGE_SOURCE_GALLERY,
                        color = Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
internal fun rememberImageLaunchers(
    onUpload: (List<Pair<ByteArray, String>>) -> Unit
): ImageLaunchers {
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val bytes = it.toJpegBytes()
            val filename = AddStrings.IMAGE_FILENAME_PREFIX + System.currentTimeMillis() + AddStrings.IMAGE_FILENAME_EXT
            onUpload(listOf(bytes to filename))
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                val imagesToUpload = uris.mapIndexedNotNull { index, uri ->
                    val bytes = readBytesFromUri(context, uri)
                    if (bytes == null) return@mapIndexedNotNull null
                    val mime = try {
                        context.contentResolver.getType(uri)
                    } catch (e: Exception) {
                        null
                    } ?: AddStrings.IMAGE_MIME_DEFAULT
                    val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: AddStrings.IMAGE_EXT_DEFAULT
                    val filename = AddStrings.IMAGE_FILENAME_PREFIX + System.currentTimeMillis() + "_" + index + "." + ext
                    bytes to filename
                }
                if (imagesToUpload.isNotEmpty()) {
                    onUpload(imagesToUpload)
                }
            }
        }
    )

    return ImageLaunchers(
        camera = { cameraLauncher.launch(null) },
        gallery = { galleryLauncher.launch(AddStrings.IMAGE_MIME_FILTER) }
    )
}

internal data class ImageLaunchers(
    val camera: () -> Unit,
    val gallery: () -> Unit
)

internal fun Bitmap.toJpegBytes(): ByteArray {
    return ByteArrayOutputStream().use { stream ->
        compress(Bitmap.CompressFormat.JPEG, 90, stream)
        stream.toByteArray()
    }
}

internal fun readBytesFromUri(context: Context, uri: Uri): ByteArray? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
    } catch (e: Exception) {
        null
    }
}
