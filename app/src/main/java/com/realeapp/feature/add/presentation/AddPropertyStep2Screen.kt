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
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
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
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.TextPrimary
import com.realeapp.ui.theme.TextSecondary

private val floorOptions = listOf("Ground") + (1..10).map { it.toString() } + listOf("10+")
private val totalFloorsOptions = (1..20).map { it.toString() } + listOf("20+")

@Composable
internal fun AddPropertyStep2Screen(
    form: PropertyForm,
    onBedroomCountChanged: (Int) -> Unit,
    onBathroomsChanged: (Int) -> Unit,
    onFurnishingChanged: (Furnishing?) -> Unit,
    onAgeChanged: (Age?) -> Unit,
    onFloorNoChanged: (String) -> Unit,
    onTotalFloorsChanged: (String) -> Unit,
    onFacingChanged: (Facing?) -> Unit,
    onAmenitiesChanged: (List<Amenity>) -> Unit,
    onBuiltUpAreaChanged: (String) -> Unit,
    onPlotAreaChanged: (String) -> Unit,
    onVideoChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onVideoChanged(it.toString()) }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Configuration
        SectionHeader("Configuration")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CountStepper(
                label = "Bedrooms",
                count = form.bedroomType.toBedroomCount(),
                onCountChange = onBedroomCountChanged,
                max = 7,
                modifier = Modifier.weight(1f)
            )
            CountStepper(
                label = "Bathrooms",
                count = form.bathrooms,
                onCountChange = onBathroomsChanged,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormTextField(
                value = form.builtUpArea,
                onValueChange = onBuiltUpAreaChanged,
                label = "Built-up Area (sq ft)",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.weight(1f)
            )
            FormTextField(
                value = form.plotArea,
                onValueChange = onPlotAreaChanged,
                label = "Plot Area (sq ft)",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.weight(1f)
            )
        }

        // Section: Property Features
        SectionHeader("Property Features")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormDropdown(
                label = "Furnishing",
                options = Furnishing.entries,
                selected = form.furnishing,
                optionLabel = { it.label },
                onSelected = onFurnishingChanged,
                modifier = Modifier.weight(1f)
            )
            FormDropdown(
                label = "Property Age",
                options = Age.entries,
                selected = form.age,
                optionLabel = { it.label },
                onSelected = onAgeChanged,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormDropdown(
                label = "Floor No.",
                options = floorOptions,
                selected = form.floorNo.ifBlank { null },
                optionLabel = { it },
                onSelected = onFloorNoChanged,
                modifier = Modifier.weight(1f)
            )
            FormDropdown(
                label = "Total Floors",
                options = totalFloorsOptions,
                selected = form.totalFloors.ifBlank { null },
                optionLabel = { it },
                onSelected = onTotalFloorsChanged,
                modifier = Modifier.weight(1f)
            )
        }

        // Section: Additional Features
        SectionHeader("Additional Features")

        AmenityFeatureGrid(
            selected = form.amenities,
            onToggle = { amenity ->
                val updated = if (amenity in form.amenities) {
                    form.amenities - amenity
                } else {
                    form.amenities + amenity
                }
                onAmenitiesChanged(updated)
            }
        )

        // Section: Facing
        FormDropdown(
            label = "Facing",
            options = Facing.entries,
            selected = form.facing,
            optionLabel = { it.label },
            onSelected = onFacingChanged,
            modifier = Modifier.fillMaxWidth()
        )

        // Section: Property Video
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FieldLabel(text = "Property Video (Optional)")
            VideoUploadBox(
                videoUrl = form.videoUrl,
                onPick = { videoPickerLauncher.launch("video/*") },
                onRemove = { onVideoChanged("") }
            )
        }
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
                .border(1.dp, TextSecondary, RoundedCornerShape(12.dp))
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
                    contentDescription = "Decrease $label",
                    tint = if (count > min) TextPrimary else TextSecondary
                )
            }
            Text(
                text = "$count",
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = { onCountChange(count + 1) },
                enabled = count < max
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase $label",
                    tint = if (count < max) TextPrimary else TextSecondary
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
    AmenityFeature(Amenity.PARKING, Icons.Default.LocalParking, "Parking"),
    AmenityFeature(Amenity.SWIMMING_POOL, Icons.Default.Pool, "Swimming Pool"),
    AmenityFeature(Amenity.GARDEN, Icons.Default.Grass, "Garden"),
    AmenityFeature(Amenity.POWER_BACKUP, Icons.Default.Power, "Power Backup"),
    AmenityFeature(Amenity.SECURITY, Icons.Default.Security, "Security"),
    AmenityFeature(Amenity.LIFT, Icons.Default.Elevator, "Lift"),
    AmenityFeature(Amenity.CLUB_HOUSE, Icons.Default.HolidayVillage, "Clubhouse"),
    AmenityFeature(Amenity.PET_FRIENDLY, Icons.Default.Pets, "Pet Friendly")
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
                                    if (isSelected) Accent.copy(alpha = 0.15f) else CardBackground,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Accent else TextSecondary.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = feature.icon,
                                contentDescription = feature.label,
                                tint = if (isSelected) Accent else TextPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = feature.label,
                            color = if (isSelected) Accent else TextPrimary,
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

@Composable
private fun VideoUploadBox(
    videoUrl: String,
    onPick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dashColor = TextSecondary
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .drawBehind {
                drawRoundRect(
                    color = dashColor,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f), 0f)
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onPick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (videoUrl.isBlank()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "Upload Video",
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "MP4, MOV (Max 100 MB)",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Video selected",
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove video",
                        tint = TextPrimary
                    )
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
