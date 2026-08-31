package com.example.mytestapp.feature.search.presentation.components

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.mytestapp.feature.search.domain.model.PropertyFilter
import com.example.mytestapp.feature.search.domain.model.PropertyType
import com.example.mytestapp.feature.search.domain.model.RentBuy

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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val localitiesList = locality.split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                    onApply(
                        PropertyFilter(
                            city = city.takeIf { it.isNotBlank() },
                            localities = localitiesList,
                            pincode = pincode.takeIf { it.isNotBlank() },
                            rentBuy = selectedRentBuy,
                            propertyType = selectedPropertyType
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
