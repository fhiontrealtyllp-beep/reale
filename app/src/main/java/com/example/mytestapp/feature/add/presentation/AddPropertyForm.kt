package com.example.mytestapp.feature.add.presentation

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mytestapp.feature.search.domain.model.Age
import com.example.mytestapp.feature.search.domain.model.Amenity
import com.example.mytestapp.feature.search.domain.model.BedroomType
import com.example.mytestapp.feature.search.domain.model.Facing
import com.example.mytestapp.feature.search.domain.model.Furnishing
import com.example.mytestapp.feature.search.domain.model.PropertyType
import com.example.mytestapp.feature.search.domain.model.RentBuy
import com.example.mytestapp.feature.search.domain.model.ResidentialCommercial

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddPropertyForm(
    uiState: AddUiState,
    viewModel: AddViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val form = uiState.form
    var showLocationPicker by remember { mutableStateOf(false) }

    if (showLocationPicker) {
        LocationPickerDialog(
            initialLat = form.latitude,
            initialLng = form.longitude,
            onDismiss = { showLocationPicker = false },
            onConfirm = { lat, lng, city, locality, address ->
                viewModel.onLocationPicked(lat, lng, city, locality, address)
                showLocationPicker = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { SectionHeader("Listing Type") }

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
                label = "Property Type",
                options = propertyTypes,
                selected = form.propertyType,
                optionLabel = { it.label },
                onSelected = { viewModel.onPropertyTypeChanged(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { SectionHeader("Basic Details") }

        item {
            FormTextField(
                value = form.title,
                onValueChange = viewModel::onTitleChanged,
                label = "Title",
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
                label = "Description",
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
                label = "Price",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                )
            )
        }

        item {
            FormTextField(
                value = form.city,
                onValueChange = viewModel::onCityChanged,
                label = "City",
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
                label = "Locality",
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
                label = "Pincode",
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
                label = "Address",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SectionHeader("Location")

                Button(
                    onClick = { showLocationPicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1C2755),
                        contentColor = Color(0xFFFBFBFB)
                    )
                ) {
                    Text(
                        text = if (form.latitude.isNotBlank() && form.longitude.isNotBlank()) {
                            "Change Location"
                        } else {
                            "Pick on Map"
                        },
                        fontWeight = FontWeight.Medium
                    )
                }

                if (form.latitude.isNotBlank() && form.longitude.isNotBlank()) {
                    Text(
                        text = "Lat: ${form.latitude}, Lng: ${form.longitude}",
                        color = Color(0xFFFBFBFB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item { SectionHeader("Property Details") }

        item {
            FormDropdown(
                label = "Bedrooms",
                options = BedroomType.entries,
                selected = form.bedroomType,
                optionLabel = { it.label },
                onSelected = { viewModel.onBedroomTypeChanged(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            FormDropdown(
                label = "Furnishing",
                options = Furnishing.entries,
                selected = form.furnishing,
                optionLabel = { it.label },
                onSelected = { viewModel.onFurnishingChanged(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            FormDropdown(
                label = "Facing",
                options = Facing.entries,
                selected = form.facing,
                optionLabel = { it.label },
                onSelected = { viewModel.onFacingChanged(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            FormDropdown(
                label = "Age",
                options = Age.entries,
                selected = form.age,
                optionLabel = { it.label },
                onSelected = { viewModel.onAgeChanged(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                text = "Amenities",
                color = Color(0xFFFBFBFB),
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
                            selectedContainerColor = Color(0xFFFDD60D),
                            selectedLabelColor = Color(0xFF141C3D),
                            containerColor = Color(0xFF1C2755),
                            labelColor = Color(0xFFFBFBFB)
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
                    label = "Carpet Area",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    value = form.builtUpArea,
                    onValueChange = viewModel::onBuiltUpAreaChanged,
                    label = "Built-up",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    value = form.superBuiltUpArea,
                    onValueChange = viewModel::onSuperBuiltUpAreaChanged,
                    label = "Super Built-up",
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
                label = "Agent Phone",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )
        }

        item {
            FormTextField(
                value = form.images.joinToString(", "),
                onValueChange = { text ->
                    viewModel.onImagesChanged(text.split(",").map { it.trim() })
                },
                label = "Image URLs (comma separated)",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                )
            )
        }

        if (uiState.fieldErrors.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    uiState.fieldErrors.forEach { error ->
                        Text(
                            text = "• $error",
                            color = Color(0xFFFF6B6B),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = viewModel::submit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !uiState.isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDD60D),
                    contentColor = Color(0xFF141C3D),
                    disabledContainerColor = Color(0xFF8F9FDC),
                    disabledContentColor = Color(0xFF141C3D)
                )
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(24.dp),
                        color = Color(0xFF141C3D),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Submit Property",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color(0xFFFDD60D),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun <T> ToggleRow(
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
                    containerColor = if (isSelected) Color(0xFFFDD60D) else Color(0xFF1C2755),
                    contentColor = if (isSelected) Color(0xFF141C3D) else Color(0xFFFBFBFB)
                )
            ) {
                Text(
                    text = optionLabel(option),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> FormDropdown(
    label: String,
    options: List<T>,
    selected: T?,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected?.let { optionLabel(it) } ?: "Select $label",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        minLines = minLines,
        maxLines = if (minLines > 1) 4 else 1,
        keyboardOptions = keyboardOptions,
        singleLine = minLines == 1,
        colors = formFieldColors()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFFFBFBFB),
    unfocusedTextColor = Color(0xFFFBFBFB),
    focusedBorderColor = Color(0xFFFDD60D),
    unfocusedBorderColor = Color(0xFF8F9FDC),
    focusedLabelColor = Color(0xFFFDD60D),
    unfocusedLabelColor = Color(0xFF8F9FDC),
    cursorColor = Color(0xFFFDD60D)
)
