package com.realeapp.feature.search.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.DialogSectionBackground
import com.realeapp.ui.theme.Gray
import com.realeapp.ui.theme.SliderTrackInactive
import com.realeapp.ui.theme.TextFieldUnfocusedBorder
import com.realeapp.ui.theme.TextFieldUnfocusedLabel
import com.realeapp.ui.theme.TextPrimary
import kotlin.math.roundToInt

@Composable
fun FilterDialog(
    filter: PropertyFilter?,
    onDismiss: () -> Unit,
    onApply: (PropertyFilter) -> Unit,
    onReset: () -> Unit
) {
    var state by remember { mutableStateOf(filter.toFilterDialogState()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Filter Properties", color = TextPrimary) },
        containerColor = CardBackground,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.city,
                    onValueChange = { state = state.copy(city = it) },
                    label = { Text("City") },
                    textStyle = TextStyle(color = TextPrimary),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.locality,
                    onValueChange = { state = state.copy(locality = it) },
                    label = { Text("Localities (comma separated)") },
                    textStyle = TextStyle(color = TextPrimary),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.pincode,
                    onValueChange = { state = state.copy(pincode = it) },
                    label = { Text("Pincode") },
                    textStyle = TextStyle(color = TextPrimary),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                FilterChipGroup(
                    title = "Looking to",
                    options = RentBuy.entries,
                    selected = state.rentBuy,
                    onSelected = { state = state.copy(rentBuy = it) },
                    optionLabel = { it.label }
                )

                FilterChipGroup(
                    title = "Property Type",
                    options = listOf(
                        PropertyType.APARTMENT,
                        PropertyType.VILLA,
                        PropertyType.PLOT,
                       /* PropertyType.INDEPENDENT_HOUSE,
                        PropertyType.COMMERCIAL_OFFICE,
                        PropertyType.SHOP,
                        PropertyType.CO_WORKING*/
                    ),
                    selected = state.propertyType,
                    onSelected = { state = state.copy(propertyType = it) },
                    optionLabel = { it.label }
                )

                RangeSliderFilter(
                    title = "Price Range",
                    minValue = state.priceMin,
                    maxValue = state.priceMax,
                    onMinValueChange = { state = state.copy(priceMin = it) },
                    onMaxValueChange = { state = state.copy(priceMax = it) },
                    sliderMin = 0f,
                    sliderMax = 10_00_00_000f,
                    step = 1_00_000f,
                    valueFormatter = { formatIndianPrice(it.toDouble()) }
                )

                RangeSliderFilter(
                    title = "Carpet Area (sqft)",
                    minValue = state.carpetMin,
                    maxValue = state.carpetMax,
                    onMinValueChange = { state = state.copy(carpetMin = it) },
                    onMaxValueChange = { state = state.copy(carpetMax = it) },
                    sliderMin = 0f,
                    sliderMax = 10_000f,
                    step = 50f,
                    valueFormatter = { "%.0f sqft".format(it) }
                )

                RangeSliderFilter(
                    title = "Built-up Area (sqft)",
                    minValue = state.builtUpMin,
                    maxValue = state.builtUpMax,
                    onMinValueChange = { state = state.copy(builtUpMin = it) },
                    onMaxValueChange = { state = state.copy(builtUpMax = it) },
                    sliderMin = 0f,
                    sliderMax = 10_000f,
                    step = 50f,
                    valueFormatter = { "%.0f sqft".format(it) }
                )

                RangeSliderFilter(
                    title = "Super Built-up Area (sqft)",
                    minValue = state.superBuiltUpMin,
                    maxValue = state.superBuiltUpMax,
                    onMinValueChange = { state = state.copy(superBuiltUpMin = it) },
                    onMaxValueChange = { state = state.copy(superBuiltUpMax = it) },
                    sliderMin = 0f,
                    sliderMax = 10_000f,
                    step = 50f,
                    valueFormatter = { "%.0f sqft".format(it) }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onApply(state.toPropertyFilter()) },
                colors = ButtonDefaults.textButtonColors(contentColor = Accent)
            ) {
                Text(text = "Apply")
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = onReset,
                    colors = ButtonDefaults.textButtonColors(contentColor = Gray)
                ) {
                    Text(text = "Reset")
                }
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = TextPrimary)
                ) {
                    Text(text = "Cancel")
                }
            }
        }
    )
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
            .background(DialogSectionBackground, RoundedCornerShape(16.dp))
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
                color = TextPrimary,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "${valueFormatter(sliderValues.start)} – ${valueFormatter(sliderValues.endInclusive)}",
                color = Accent,
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
                thumbColor = Accent,
                activeTrackColor = Accent,
                inactiveTrackColor = SliderTrackInactive
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
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                singleLine = true,
                isError = hasInput && !isValid,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = TextFieldUnfocusedBorder,
                    focusedLabelColor = Accent,
                    unfocusedLabelColor = TextFieldUnfocusedLabel,
                    cursorColor = Accent
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
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                singleLine = true,
                isError = hasInput && !isValid,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = TextFieldUnfocusedBorder,
                    focusedLabelColor = Accent,
                    unfocusedLabelColor = TextFieldUnfocusedLabel,
                    cursorColor = Accent
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
