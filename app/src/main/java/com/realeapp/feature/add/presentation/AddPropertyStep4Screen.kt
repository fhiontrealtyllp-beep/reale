package com.realeapp.feature.add.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.OnAccent
import com.realeapp.ui.theme.TextPrimary
import com.realeapp.ui.theme.TextSecondary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val priceModeOptions = listOf("Total Price", "Price per sq ft")

private val additionalCostOptions = listOf(
    "Maintenance",
    "Govt. Taxes",
    "Parking Charges",
    "Club Membership",
    "Other"
)

private val propertyStatusOptions = listOf(
    "Ready to Move",
    "Under Construction",
    "New Launch"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddPropertyStep4Screen(
    form: PropertyForm,
    onPriceChanged: (String) -> Unit,
    onPriceModeChanged: (Boolean) -> Unit,
    onNegotiableChanged: (Boolean) -> Unit,
    onAdditionalCostsChanged: (String) -> Unit,
    onPropertyStatusChanged: (String) -> Unit,
    onPossessionDateChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onPossessionDateChanged(formatPossessionDate(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = Accent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Accent)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Price Details
        SectionHeader("Price Details")

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            FieldLabel(text = "Price", isRequired = true)
            OutlinedTextField(
                value = form.price,
                onValueChange = onPriceChanged,
                placeholder = { Text("e.g. 1,85,00,000") },
                leadingIcon = {
                    Text(
                        text = "₹",
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                colors = formFieldColors()
            )
        }

        ToggleRow(
            options = priceModeOptions,
            selected = if (form.pricePerSqFt) "Price per sq ft" else "Total Price",
            optionLabel = { it },
            onSelected = { onPriceModeChanged(it == "Price per sq ft") }
        )

        calculatedPriceText(form)?.let { text ->
            Text(
                text = text,
                color = Accent,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Negotiable",
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = form.negotiable,
                onCheckedChange = onNegotiableChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = OnAccent,
                    checkedTrackColor = Accent
                )
            )
        }

        FormDropdown(
            label = "Additional Costs (Optional)",
            options = additionalCostOptions,
            selected = form.additionalCosts.ifBlank { null },
            optionLabel = { it },
            onSelected = onAdditionalCostsChanged,
            modifier = Modifier.fillMaxWidth()
        )

        // Section: Availability
        SectionHeader("Availability")

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            FieldLabel(text = "Property Status", isRequired = true)
            propertyStatusOptions.forEach { status ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPropertyStatusChanged(status) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = form.propertyStatus == status,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Accent,
                            unselectedColor = TextSecondary
                        )
                    )
                    Text(
                        text = status,
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            FieldLabel(text = "Possession Date")
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = form.possessionDate,
                    onValueChange = {},
                    placeholder = { Text("Select Date") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Pick date",
                            tint = TextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    singleLine = true,
                    colors = formFieldColors()
                )
                // Click overlay so the read-only field opens the date picker.
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }
        }
    }
}

private fun calculatedPriceText(form: PropertyForm): String? {
    val price = form.price.toDoubleOrNull() ?: return null
    val area = form.builtUpArea.toDoubleOrNull()?.takeIf { it > 0 } ?: return null
    return if (form.pricePerSqFt) {
        "₹ ${formatIndianAmount(price * area)} total (calculated)"
    } else {
        "₹ ${formatIndianAmount(price / area)} per sq ft (calculated)"
    }
}

private fun formatIndianAmount(value: Double): String {
    return NumberFormat.getNumberInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
    }.format(value)
}

private fun formatPossessionDate(millis: Long): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
}
