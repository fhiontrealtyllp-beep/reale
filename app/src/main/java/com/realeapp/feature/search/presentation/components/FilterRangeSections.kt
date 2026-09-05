package com.realeapp.feature.search.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.DialogSectionBackground
import com.realeapp.ui.theme.SliderTrackInactive
import com.realeapp.ui.theme.TextFieldUnfocusedBorder
import com.realeapp.ui.theme.TextFieldUnfocusedLabel
import com.realeapp.ui.theme.TextPrimary
import kotlin.math.roundToInt

@Composable
fun PriceFilterSection(
    minValue: String,
    maxValue: String,
    onMinValueChange: (String) -> Unit,
    onMaxValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    RangeFilterSection(
        title = "Price Range",
        minValue = minValue,
        maxValue = maxValue,
        onMinValueChange = onMinValueChange,
        onMaxValueChange = onMaxValueChange,
        sliderMin = 0f,
        sliderMax = 10_00_00_000f,
        step = 1_00_000f,
        valueFormatter = { formatIndianPrice(it.toDouble()) },
        modifier = modifier
    )
}

@Composable
fun AreaFilterSection(
    title: String,
    minValue: String,
    maxValue: String,
    onMinValueChange: (String) -> Unit,
    onMaxValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    RangeFilterSection(
        title = title,
        minValue = minValue,
        maxValue = maxValue,
        onMinValueChange = onMinValueChange,
        onMaxValueChange = onMaxValueChange,
        sliderMin = 0f,
        sliderMax = 10_000f,
        step = 50f,
        valueFormatter = { "%.0f sqft".format(it) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangeFilterSection(
    title: String,
    minValue: String,
    maxValue: String,
    onMinValueChange: (String) -> Unit,
    onMaxValueChange: (String) -> Unit,
    sliderMin: Float,
    sliderMax: Float,
    step: Float,
    valueFormatter: (Float) -> String,
    modifier: Modifier = Modifier
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
        modifier = modifier
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
