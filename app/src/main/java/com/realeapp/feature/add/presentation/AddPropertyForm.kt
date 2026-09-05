package com.realeapp.feature.add.presentation

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.realeapp.feature.search.domain.model.Age
import com.realeapp.feature.search.domain.model.Amenity
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Facing
import com.realeapp.feature.search.domain.model.Furnishing
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.domain.model.ResidentialCommercial
import com.realeapp.util.isDebug
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.Error
import com.realeapp.ui.theme.OnAccent
import com.realeapp.ui.theme.TextPrimary
import com.realeapp.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddPropertyForm(
    uiState: AddUiState,
    viewModel: AddViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val form = uiState.form
    val context = LocalContext.current
    val isDebug = context.isDebug
    var showLocationPicker by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    // Full-screen location picker overlay shown from the form's location action.
    if (showLocationPicker) {
        LocationPickerDialog(
            initialLat = form.latitude,
            initialLng = form.longitude,
            onDismiss = { showLocationPicker = false },
            onConfirm = { lat, lng, city, locality, pincode, address ->
                viewModel.onLocationPicked(lat, lng, city, locality, pincode, address)
                showLocationPicker = false
            }
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val bytes = it.toJpegBytes()
            val filename = AddStrings.IMAGE_FILENAME_PREFIX + System.currentTimeMillis() + AddStrings.IMAGE_FILENAME_EXT
            viewModel.uploadImage(bytes, filename)
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
                    viewModel.uploadImages(imagesToUpload)
                }
            }
        }
    )

    // Image-source dialog overlay offering camera and gallery choices.
    if (showImageSourceDialog) {
        ImageSourceDialog(
            onCamera = {
                showImageSourceDialog = false
                cameraLauncher.launch(null)
            },
            onGallery = {
                showImageSourceDialog = false
                galleryLauncher.launch(AddStrings.IMAGE_MIME_FILTER)
            },
            onDismiss = { showImageSourceDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Listing Type
        item { SectionHeader(AddStrings.LABEL_LISTING_TYPE) }

        item {
            ToggleRow(
                options = RentBuy.entries,
                selected = form.rentBuy,
                optionLabel = { it.label },
                onSelected = { viewModel.onRentBuyChanged(it) }
            )
        }

        item {
            ToggleRow(
                options = ResidentialCommercial.entries,
                selected = form.residentialCommercial,
                optionLabel = { it.label },
                onSelected = { viewModel.onResidentialCommercialChanged(it) }
            )
        }

        item {
            val propertyTypes = remember(form.residentialCommercial) {
                PropertyType.entries.filter {
                    form.residentialCommercial == null || it.category == form.residentialCommercial
                }
            }
            FormDropdown(
                label = AddStrings.LABEL_PROPERTY_TYPE,
                options = propertyTypes,
                selected = form.propertyType,
                optionLabel = { it.label },
                onSelected = { viewModel.onPropertyTypeChanged(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Section: Basic Details
        item { SectionHeader(AddStrings.SECTION_BASIC_DETAILS) }

        item {
            FormTextField(
                value = form.title,
                onValueChange = viewModel::onTitleChanged,
                label = AddStrings.LABEL_TITLE,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
        }

        item {
            FormTextField(
                value = form.description,
                onValueChange = viewModel::onDescriptionChanged,
                label = AddStrings.LABEL_DESCRIPTION,
                minLines = 3,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )
        }

        item {
            FormTextField(
                value = form.price,
                onValueChange = viewModel::onPriceChanged,
                label = AddStrings.LABEL_PRICE,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                )
            )
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Section: Location
                SectionHeader(AddStrings.LABEL_LOCATION)

                Button(
                    onClick = { showLocationPicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CardBackground,
                        contentColor = TextPrimary
                    )
                ) {
                    Text(
                        text = if (form.latitude.isNotBlank() && form.longitude.isNotBlank()) {
                            AddStrings.ACTION_CHANGE_LOCATION
                        } else {
                            AddStrings.ACTION_PICK_ON_MAP
                        },
                        fontWeight = FontWeight.Medium
                    )
                }

                if (isDebug && form.latitude.isNotBlank() && form.longitude.isNotBlank()) {
                    Text(
                        text = AddStrings.LABEL_LATITUDE + ": " + form.latitude + ", " + AddStrings.LABEL_LONGITUDE + ": " + form.longitude,
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            FormTextField(
                value = form.city,
                onValueChange = viewModel::onCityChanged,
                label = AddStrings.LABEL_CITY,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
        }

        item {
            FormTextField(
                value = form.locality,
                onValueChange = viewModel::onLocalityChanged,
                label = AddStrings.LABEL_LOCALITY,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
        }

        item {
            FormTextField(
                value = form.pincode,
                onValueChange = viewModel::onPincodeChanged,
                label = AddStrings.LABEL_PINCODE,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
        }

        item {
            FormTextField(
                value = form.address,
                onValueChange = viewModel::onAddressChanged,
                label = AddStrings.LABEL_ADDRESS,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
        }

        // Section: Property Details
        item { SectionHeader(AddStrings.SECTION_PROPERTY_DETAILS) }

        item {
            FormDropdown(
                label = AddStrings.LABEL_BEDROOMS,
                options = BedroomType.entries,
                selected = form.bedroomType,
                optionLabel = { it.label },
                onSelected = { viewModel.onBedroomTypeChanged(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            FormDropdown(
                label = AddStrings.LABEL_FURNISHING,
                options = Furnishing.entries,
                selected = form.furnishing,
                optionLabel = { it.label },
                onSelected = { viewModel.onFurnishingChanged(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            FormDropdown(
                label = AddStrings.LABEL_FACING,
                options = Facing.entries,
                selected = form.facing,
                optionLabel = { it.label },
                onSelected = { viewModel.onFacingChanged(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            FormDropdown(
                label = AddStrings.LABEL_AGE,
                options = Age.entries,
                selected = form.age,
                optionLabel = { it.label },
                onSelected = { viewModel.onAgeChanged(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                text = AddStrings.LABEL_AMENITIES,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Amenity.entries.forEach { amenity ->
                    val selected = amenity in form.amenities
                    FilterChip(
                        selected = selected,
                        onClick = {
                            val updated = if (selected) {
                                form.amenities - amenity
                            } else {
                                form.amenities + amenity
                            }
                            viewModel.onAmenitiesChanged(updated)
                        },
                        label = { Text(amenity.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Accent,
                            selectedLabelColor = OnAccent,
                            containerColor = CardBackground,
                            labelColor = TextPrimary
                        )
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormTextField(
                    value = form.carpetArea,
                    onValueChange = viewModel::onCarpetAreaChanged,
                    label = AddStrings.LABEL_CARPET_AREA,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    value = form.builtUpArea,
                    onValueChange = viewModel::onBuiltUpAreaChanged,
                    label = AddStrings.LABEL_BUILT_UP,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    value = form.superBuiltUpArea,
                    onValueChange = viewModel::onSuperBuiltUpAreaChanged,
                    label = AddStrings.LABEL_SUPER_BUILT_UP,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            FormTextField(
                value = form.agentPhone,
                onValueChange = viewModel::onAgentPhoneChanged,
                label = AddStrings.LABEL_AGENT_PHONE,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImageUrlsSection(
                    images = form.images,
                    isUploadingImage = uiState.isUploadingImage,
                    onAddImages = { showImageSourceDialog = true },
                    onRemoveImage = viewModel::removeImageUrl,
                    modifier = Modifier.fillMaxWidth()
                )

                // Image upload error UI shown below the property image picker.
                if (uiState.imageUploadError != null) {
                    Text(
                        text = uiState.imageUploadError,
                        color = Error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Form validation summary UI shown when submission finds invalid fields.
        if (uiState.fieldErrors.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    uiState.fieldErrors.forEach { error ->
                        Text(
                            text = AddStrings.BULLET_PREFIX + error,
                            color = Error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        item {
            // Primary submit UI; a progress indicator replaces the label while submitting.
            Button(
                onClick = viewModel::submit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !uiState.isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor = OnAccent,
                    disabledContainerColor = TextSecondary,
                    disabledContentColor = OnAccent
                )
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(24.dp),
                        color = OnAccent,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = AddStrings.ACTION_SUBMIT_PROPERTY,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

