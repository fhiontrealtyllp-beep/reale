package com.realeapp.feature.search.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.realeapp.feature.search.domain.model.CarpetAreaRange
import com.realeapp.feature.search.domain.model.PriceRange
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterDialog(
    filter: PropertyFilter?,
    onDismiss: () -> Unit,
    onApply: (PropertyFilter) -> Unit,
    onReset: () -> Unit
) {
    var city by remember { mutableStateOf(filter?.city ?: "") }
    var locality by remember { mutableStateOf(filter?.localities?.joinToString(", ") ?: "") }
    var pincode by remember { mutableStateOf(filter?.pincode ?: "") }
    var selectedRentBuy by remember { mutableStateOf(filter?.rentBuy) }
    var selectedPropertyType by remember { mutableStateOf(filter?.propertyType) }

    var priceMin by remember { mutableStateOf(filter?.priceRange?.min?.toInt()?.toString() ?: "") }
    var priceMax by remember { mutableStateOf(filter?.priceRange?.max?.toInt()?.toString() ?: "") }

    var carpetMin by remember { mutableStateOf(filter?.carpetAreaRange?.min?.toInt()?.toString() ?: "") }
    var carpetMax by remember { mutableStateOf(filter?.carpetAreaRange?.max?.toInt()?.toString() ?: "") }

    var builtUpMin by remember { mutableStateOf(filter?.builtUpAreaRange?.min?.toInt()?.toString() ?: "") }
    var builtUpMax by remember { mutableStateOf(filter?.builtUpAreaRange?.max?.toInt()?.toString() ?: "") }

    var superBuiltUpMin by remember { mutableStateOf(filter?.superBuiltUpAreaRange?.min?.toInt()?.toString() ?: "") }
    var superBuiltUpMax by remember { mutableStateOf(filter?.superBuiltUpAreaRange?.max?.toInt()?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Filter Properties", color = Color(0xFFFBFBFB)) },
        containerColor = Color(0xFF1C2755),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    textStyle = TextStyle(color = Color(0xFFFBFBFB)),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = locality,
                    onValueChange = { locality = it },
                    label = { Text("Localities (comma separated)") },
                    textStyle = TextStyle(color = Color(0xFFFBFBFB)),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pincode,
                    onValueChange = { pincode = it },
                    label = { Text("Pincode") },
                    textStyle = TextStyle(color = Color(0xFFFBFBFB)),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = "Looking to", color = Color(0xFFFBFBFB))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RentBuy.entries.forEach { rentBuy ->
                        FilterChip(
                            selected = selectedRentBuy == rentBuy,
                            onClick = {
                                selectedRentBuy = if (selectedRentBuy == rentBuy) null else rentBuy
                            },
                            label = { Text(rentBuy.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFDD60D),
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF2B3C83),
                                labelColor = Color(0xFFFBFBFB)
                            )
                        )
                    }
                }

                Text(text = "Property Type", color = Color(0xFFFBFBFB))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        PropertyType.APARTMENT,
                        PropertyType.VILLA,
                        PropertyType.PLOT,
                        PropertyType.INDEPENDENT_HOUSE,
                        PropertyType.COMMERCIAL_OFFICE,
                        PropertyType.SHOP,
                        PropertyType.CO_WORKING
                    ).forEach { type ->
                        FilterChip(
                            selected = selectedPropertyType == type,
                            onClick = {
                                selectedPropertyType = if (selectedPropertyType == type) null else type
                            },
                            label = { Text(type.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFDD60D),
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF2B3C83),
                                labelColor = Color(0xFFFBFBFB)
                            )
                        )
                    }
                }

                RangeSliderFilter(
                    title = "Price Range",
                    minValue = priceMin,
                    maxValue = priceMax,
                    onMinValueChange = { priceMin = it },
                    onMaxValueChange = { priceMax = it },
                    sliderMin = 0f,
                    sliderMax = 10_00_00_000f,
                    step = 1_00_000f,
                    valueFormatter = { formatIndianPrice(it.toDouble()) }
                )

                RangeSliderFilter(
                    title = "Carpet Area (sqft)",
                    minValue = carpetMin,
                    maxValue = carpetMax,
                    onMinValueChange = { carpetMin = it },
                    onMaxValueChange = { carpetMax = it },
                    sliderMin = 0f,
                    sliderMax = 10_000f,
                    step = 50f,
                    valueFormatter = { "%.0f sqft".format(it) }
                )

                RangeSliderFilter(
                    title = "Built-up Area (sqft)",
                    minValue = builtUpMin,
                    maxValue = builtUpMax,
                    onMinValueChange = { builtUpMin = it },
                    onMaxValueChange = { builtUpMax = it },
                    sliderMin = 0f,
                    sliderMax = 10_000f,
                    step = 50f,
                    valueFormatter = { "%.0f sqft".format(it) }
                )

                RangeSliderFilter(
                    title = "Super Built-up Area (sqft)",
                    minValue = superBuiltUpMin,
                    maxValue = superBuiltUpMax,
                    onMinValueChange = { superBuiltUpMin = it },
                    onMaxValueChange = { superBuiltUpMax = it },
                    sliderMin = 0f,
                    sliderMax = 10_000f,
                    step = 50f,
                    valueFormatter = { "%.0f sqft".format(it) }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val localitiesList = locality.split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }

                    val priceRange = parseDoubleRange(priceMin, priceMax)?.let { PriceRange(it.first, it.second) }
                    val carpetAreaRange = parseDoubleRange(carpetMin, carpetMax)?.let { CarpetAreaRange(it.first, it.second) }
                    val builtUpAreaRange = parseDoubleRange(builtUpMin, builtUpMax)?.let { CarpetAreaRange(it.first, it.second) }
                    val superBuiltUpAreaRange = parseDoubleRange(superBuiltUpMin, superBuiltUpMax)?.let { CarpetAreaRange(it.first, it.second) }

                    onApply(
                        PropertyFilter(
                            city = city.takeIf { it.isNotBlank() },
                            localities = localitiesList,
                            pincode = pincode.takeIf { it.isNotBlank() },
                            rentBuy = selectedRentBuy,
                            propertyType = selectedPropertyType,
                            priceRange = priceRange,
                            carpetAreaRange = carpetAreaRange,
                            builtUpAreaRange = builtUpAreaRange,
                            superBuiltUpAreaRange = superBuiltUpAreaRange
                        )
                    )
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFDD60D))
            ) {
                Text(text = "Apply")
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = onReset,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) {
                    Text(text = "Reset")
                }
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFBFBFB))
                ) {
                    Text(text = "Cancel")
                }
            }
        }
    )
}

private fun parseDoubleRange(minText: String, maxText: String): Pair<Double, Double>? {
    val min = minText.toDoubleOrNull()
    val max = maxText.toDoubleOrNull()
    return if (min != null && max != null && min <= max) min to max else null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangeSliderFilter(
    title: String,
    minValue: String,
    maxValue: String,
    onMinValueChange: (String) -> Unit,
    onMaxValueChange: (String) -> Unit,
    sliderMin: Float,
    sliderMax: Float,
    step: Float,
    valueFormatter: (Float) -> String
) {
    val minDouble = minValue.toDoubleOrNull()
    val maxDouble = maxValue.toDoubleOrNull()
    val hasInput = minValue.isNotBlank() || maxValue.isNotBlank()
    val isValid = !hasInput || (minDouble != null && maxDouble != null && minDouble <= maxDouble)

    val sliderValues = if (minDouble != null && maxDouble != null && minDouble <= maxDouble) {
        minDouble.toFloat().coerceIn(sliderMin, sliderMax)..maxDouble.toFloat().coerceIn(sliderMin, sliderMax)
    } else {
        sliderMin..sliderMax
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF243265), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color(0xFFFBFBFB),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "${valueFormatter(sliderValues.start)} – ${valueFormatter(sliderValues.endInclusive)}",
                color = Color(0xFFFDD60D),
                style = MaterialTheme.typography.labelLarge
            )
        }
        RangeSlider(
            value = sliderValues,
            onValueChange = { range ->
                val snappedMin = (sliderMin + ((range.start - sliderMin) / step).roundToInt() * step)
                    .coerceIn(sliderMin, sliderMax)
                val snappedMax = (sliderMin + ((range.endInclusive - sliderMin) / step).roundToInt() * step)
                    .coerceIn(sliderMin, sliderMax)
                onMinValueChange(snappedMin.toInt().toString())
                onMaxValueChange(snappedMax.toInt().toString())
            },
            modifier = Modifier.fillMaxWidth(),
            valueRange = sliderMin..sliderMax,
            steps = 0,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFDD60D),
                activeTrackColor = Color(0xFFFDD60D),
                inactiveTrackColor = Color(0xFF53649D)
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = minValue,
                onValueChange = onMinValueChange,
                label = { Text("Min") },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFFBFBFB)),
                singleLine = true,
                isError = hasInput && !isValid,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFDD60D),
                    unfocusedBorderColor = Color(0xFF7180B2),
                    focusedLabelColor = Color(0xFFFDD60D),
                    unfocusedLabelColor = Color(0xFFB9C1DF),
                    cursorColor = Color(0xFFFDD60D)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            )
            OutlinedTextField(
                value = maxValue,
                onValueChange = onMaxValueChange,
                label = { Text("Max") },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFFBFBFB)),
                singleLine = true,
                isError = hasInput && !isValid,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFDD60D),
                    unfocusedBorderColor = Color(0xFF7180B2),
                    focusedLabelColor = Color(0xFFFDD60D),
                    unfocusedLabelColor = Color(0xFFB9C1DF),
                    cursorColor = Color(0xFFFDD60D)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            )
        }
        if (hasInput && !isValid) {
            Text(
                text = "Enter a valid range",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
