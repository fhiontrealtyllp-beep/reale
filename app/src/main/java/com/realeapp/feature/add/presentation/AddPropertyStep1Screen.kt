package com.realeapp.feature.add.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.domain.model.ResidentialCommercial
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.Error
import com.realeapp.ui.theme.MainBackground
import com.realeapp.ui.theme.OnAccent
import com.realeapp.ui.theme.SurfaceLight
import com.realeapp.ui.theme.TextPrimary
import com.realeapp.ui.theme.TextSecondary
import com.realeapp.ui.theme.White

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AddPropertyStep1Screen(
    form: com.realeapp.feature.add.domain.model.PropertyForm,
    onRentBuyChanged: (RentBuy) -> Unit,
    onPropertyTypeChanged: (PropertyType) -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onCityChanged: (String) -> Unit,
    onLocalityChanged: (String) -> Unit,
    onPincodeChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onPickOnMap: () -> Unit,
    onUseMyLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormTextField(
            value = form.title,
            onValueChange = onTitleChanged,
            label = AddStrings.LABEL_PROPERTY_TITLE,
            isRequired = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FieldLabel(text = AddStrings.LABEL_PROPERTY_TYPE, isRequired = true)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PropertyType.entries.forEach { propertyType ->
                    PropertyTypeCard(
                        propertyType = propertyType,
                        isSelected = form.propertyType == propertyType,
                        onClick = { onPropertyTypeChanged(propertyType) }
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FieldLabel(text = AddStrings.LABEL_LISTING_TYPE, isRequired = true)
            ListingTypeToggle(
                selected = form.rentBuy,
                onSelected = onRentBuyChanged
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FieldLabel(text = AddStrings.LABEL_LOCATION, isRequired = true)
            OutlinedTextField(
                value = form.address,
                onValueChange = onAddressChanged,
                label = { Text(AddStrings.LOCATION_FIELD_PLACEHOLDER) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = formFieldColors(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                },
                trailingIcon = {
                    TextButton(
                        onClick = onUseMyLocation,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = AddStrings.ACTION_USE_MY_LOCATION,
                            color = Accent,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            Button(
                onClick = onPickOnMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CardBackground,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (form.latitude.isNotBlank() && form.longitude.isNotBlank()) {
                        AddStrings.ACTION_CHANGE_LOCATION_ON_MAP
                    } else {
                        AddStrings.ACTION_PICK_ON_MAP
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormTextField(
                value = form.city,
                onValueChange = onCityChanged,
                label = AddStrings.LABEL_CITY,
                isRequired = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.weight(1f)
            )
            FormTextField(
                value = form.locality,
                onValueChange = onLocalityChanged,
                label = AddStrings.LABEL_LOCALITY,
                isRequired = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.weight(1f)
            )
        }

        FormTextField(
            value = form.pincode,
            onValueChange = onPincodeChanged,
            label = AddStrings.LABEL_PINCODE,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FormTextField(
                value = form.description,
                onValueChange = onDescriptionChanged,
                label = AddStrings.LABEL_SHORT_DESCRIPTION,
                minLines = 3,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                )
            )
            Text(
                text = "${form.description.length}${AddStrings.DESCRIPTION_COUNTER_SUFFIX}",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun PropertyTypeCard(
    propertyType: PropertyType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (icon, iconColor) = when (propertyType.category) {
        ResidentialCommercial.RESIDENTIAL -> propertyTypeIcon(propertyType) to TextPrimary
        ResidentialCommercial.COMMERCIAL -> Icons.Default.Business to TextPrimary
    }
    val borderColor = if (isSelected) Accent else TextSecondary.copy(alpha = 0.3f)
    val backgroundColor = if (isSelected) Accent.copy(alpha = 0.15f) else CardBackground
    val textColor = if (isSelected) Accent else TextPrimary

    Card(
        modifier = Modifier
            .size(width = 96.dp, height = 88.dp)
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Accent else iconColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = propertyType.label,
                color = textColor,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 2,
                minLines = 2
            )
        }
    }
}

@Composable
private fun ListingTypeToggle(
    selected: RentBuy?,
    onSelected: (RentBuy) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RentBuy.entries.forEach { rentBuy ->
            val isSelected = selected == rentBuy
            val label = if (rentBuy == RentBuy.BUY) AddStrings.LISTING_FOR_SALE else AddStrings.LISTING_FOR_RENT
            val icon = if (rentBuy == RentBuy.BUY) Icons.Default.Home else Icons.Default.Home
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clickable { onSelected(rentBuy) }
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Accent else TextSecondary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Accent.copy(alpha = 0.15f) else CardBackground
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Accent else TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = label,
                        color = if (isSelected) Accent else TextPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun propertyTypeIcon(propertyType: PropertyType): ImageVector {
    return when (propertyType) {
        PropertyType.APARTMENT,
       /* PropertyType.STUDIO_APARTMENT,
        PropertyType.SERVICE_APARTMENT*/
             -> Icons.Default.Home
        PropertyType.VILLA,
        /*PropertyType.INDEPENDENT_HOUSE,
        PropertyType.FARM_HOUSE,
        PropertyType.BUILDER_FLOOR,
        PropertyType.PENTHOUSE,
        PropertyType.DUPLEX,
        PropertyType.ROW_HOUSE,
        PropertyType.PAYING_GUEST,
        PropertyType.HOSTEL*/ -> Icons.Default.Home
        PropertyType.PLOT,
        PropertyType.LAND,
      /*  PropertyType.COMMERCIAL_PLOT,
        PropertyType.COMMERCIAL_LAND*/ -> Icons.Default.Landscape
        else -> Icons.Default.Business
    }
}
