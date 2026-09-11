package com.realeapp.feature.add.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.search.domain.model.Age
import com.realeapp.feature.search.domain.model.Amenity
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Facing
import com.realeapp.feature.search.domain.model.Furnishing
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.HomeCategoryUnselected
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.preview.PreviewData
import com.realeapp.ui.theme.RealeTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun AddPropertyStep2Screen(
    form: PropertyForm,
    onBedroomCountChanged: (Int) -> Unit,
    onBathroomsChanged: (Int) -> Unit,
    onFurnishingChanged: (Furnishing?) -> Unit,
    onAgeChanged: (Age?) -> Unit,
    onFacingChanged: (Facing?) -> Unit,
    onAmenitiesChanged: (List<Amenity>) -> Unit,
    onCarpetAreaChanged: (String) -> Unit,
    onBuiltUpAreaChanged: (String) -> Unit,
    onSuperBuiltUpAreaChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(AddStrings.SECTION_CONFIGURATION)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CountStepper(
                label = AddStrings.LABEL_BEDROOMS,
                count = form.bedroomType.toBedroomCount(),
                onCountChange = onBedroomCountChanged,
                max = 7,
                modifier = Modifier.weight(1f)
            )
            CountStepper(
                label = AddStrings.LABEL_BATHROOMS,
                count = form.bathrooms,
                onCountChange = onBathroomsChanged,
                modifier = Modifier.weight(1f)
            )
        }

        SectionHeader(AddStrings.SECTION_AREA_DETAILS)

        FormTextField(
            value = form.carpetArea,
            onValueChange = onCarpetAreaChanged,
            label = AddStrings.LABEL_CARPET_AREA_SQFT,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        FormTextField(
            value = form.builtUpArea,
            onValueChange = onBuiltUpAreaChanged,
            label = AddStrings.LABEL_BUILT_UP_AREA_SQFT,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        FormTextField(
            value = form.superBuiltUpArea,
            onValueChange = onSuperBuiltUpAreaChanged,
            label = AddStrings.LABEL_SUPER_BUILT_UP_AREA_SQFT,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        SectionHeader(AddStrings.SECTION_PROPERTY_FEATURES)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormDropdown(
                label = AddStrings.LABEL_FURNISHING,
                options = Furnishing.entries,
                selected = form.furnishing,
                optionLabel = { it.label },
                onSelected = onFurnishingChanged,
                modifier = Modifier.weight(1f)
            )
            FormDropdown(
                label = AddStrings.LABEL_PROPERTY_AGE,
                options = Age.entries,
                selected = form.age,
                optionLabel = { it.label },
                onSelected = onAgeChanged,
                modifier = Modifier.weight(1f)
            )
        }

        FormDropdown(
            label = AddStrings.LABEL_FACING,
            options = Facing.entries,
            selected = form.facing,
            optionLabel = { it.label },
            onSelected = onFacingChanged,
            modifier = Modifier.fillMaxWidth()
        )

        SectionHeader(AddStrings.SECTION_ADDITIONAL_FEATURES)

        AmenityFeatureGrid(
            selected = form.amenities,
            onToggle = { amenity ->
                onAmenitiesChanged(
                    if (amenity in form.amenities) form.amenities - amenity else form.amenities + amenity
                )
            }
        )
    }
}

@Composable
private fun CountStepper(
    label: String,
    count: Int,
    onCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 0,
    max: Int = 10
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FieldLabel(text = label)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, HomeTextSecondary, RoundedCornerShape(12.dp))
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onCountChange(count - 1) },
                enabled = count > min
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = AddStrings.CD_DECREASE_PREFIX + label,
                    tint = if (count > min) Black else HomeTextSecondary
                )
            }
            Text(
                text = "$count",
                color = Black,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = { onCountChange(count + 1) },
                enabled = count < max
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = AddStrings.CD_INCREASE_PREFIX + label,
                    tint = if (count < max) Black else HomeTextSecondary
                )
            }
        }
    }
}

private data class AmenityFeature(
    val amenity: Amenity,
    val icon: ImageVector,
    val label: String
)

private val additionalFeatures = listOf(
    AmenityFeature(Amenity.PARKING, Icons.Default.LocalParking, AddStrings.AMENITY_PARKING),
    AmenityFeature(Amenity.SWIMMING_POOL, Icons.Default.Pool, AddStrings.AMENITY_SWIMMING_POOL),
    AmenityFeature(Amenity.GARDEN, Icons.Default.Grass, AddStrings.AMENITY_GARDEN),
    AmenityFeature(Amenity.POWER_BACKUP, Icons.Default.Power, AddStrings.AMENITY_POWER_BACKUP),
    AmenityFeature(Amenity.SECURITY, Icons.Default.Security, AddStrings.AMENITY_SECURITY),
    AmenityFeature(Amenity.LIFT, Icons.Default.Elevator, AddStrings.AMENITY_LIFT),
    AmenityFeature(Amenity.CLUB_HOUSE, Icons.Default.HolidayVillage, AddStrings.AMENITY_CLUBHOUSE),
    AmenityFeature(Amenity.PET_FRIENDLY, Icons.Default.Pets, AddStrings.AMENITY_PET_FRIENDLY)
)

@Composable
private fun AmenityFeatureGrid(
    selected: List<Amenity>,
    onToggle: (Amenity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        additionalFeatures.chunked(4).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { feature ->
                    val isSelected = feature.amenity in selected
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onToggle(feature.amenity) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .background(
                                    if (isSelected) BrandBlue.copy(alpha = 0.15f) else HomeCategoryUnselected,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) BrandBlue else HomeTextSecondary.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = feature.icon,
                                contentDescription = feature.label,
                                tint = if (isSelected) BrandBlue else Black,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = feature.label,
                            color = if (isSelected) BrandBlue else Black,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
                repeat(4 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private fun BedroomType?.toBedroomCount(): Int = when (this) {
    BedroomType.ONE_RK, BedroomType.ONE_BHK, BedroomType.STUDIO_APARTMENT -> 1
    BedroomType.TWO_BHK -> 2
    BedroomType.THREE_BHK -> 3
    BedroomType.FOUR_BHK -> 4
    BedroomType.FIVE_BHK -> 5
    BedroomType.SIX_BHK -> 6
    BedroomType.SIX_PLUS_BHK -> 7
    null -> 0
}

@Preview(showBackground = true)
@Composable
private fun AddPropertyStep2ScreenPreview() {
    RealeTheme {
        AddPropertyStep2Screen(
            form = PreviewData.samplePropertyForm,
            onBedroomCountChanged = {},
            onBathroomsChanged = {},
            onFurnishingChanged = {},
            onAgeChanged = {},
            onFacingChanged = {},
            onAmenitiesChanged = {},
            onCarpetAreaChanged = {},
            onBuiltUpAreaChanged = {},
            onSuperBuiltUpAreaChanged = {}
        )
    }
}
