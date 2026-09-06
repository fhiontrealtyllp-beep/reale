package com.realeapp.feature.add.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.HomeCategoryUnselected
import com.realeapp.ui.theme.White
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun AddPropertyStep5Screen(
    form: PropertyForm,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    isSubmitting: Boolean,
    modifier: Modifier = Modifier
) {
    var confirmed by remember { mutableStateOf(false) }
    var descriptionExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cover photo with an Edit shortcut back to the photos step.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(HomeCategoryUnselected)
        ) {
            if (form.images.isNotEmpty()) {
                AsyncImage(
                    model = form.images.first(),
                    contentDescription = AddStrings.CD_COVER_PHOTO,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = HomeTextSecondary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(White)
                    .clickable(onClick = onEdit)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = AddStrings.ACTION_EDIT,
                    color = BrandBlue,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = form.title.ifBlank { AddStrings.UNTITLED_PROPERTY },
            color = Black,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = locationText(form),
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Detail rows
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ReviewRow(label = AddStrings.LABEL_PROPERTY_TYPE, value = form.propertyType?.label.orEmpty())
            ReviewRow(label = AddStrings.LABEL_LISTING_TYPE, value = listingTypeLabel(form.rentBuy))
            ReviewRow(label = AddStrings.LABEL_PRICE, value = priceText(form))
            ReviewRow(label = AddStrings.LABEL_CONFIGURATION, value = configurationText(form))
            ReviewRow(label = AddStrings.LABEL_BUILT_UP_AREA, value = areaText(form.builtUpArea))
            ReviewRow(label = AddStrings.LABEL_FURNISHING, value = form.furnishing?.label.orEmpty())
            ReviewRow(label = AddStrings.LABEL_FACING, value = form.facing?.label.orEmpty())
            ReviewRow(label = AddStrings.LABEL_PROPERTY_AGE, value = form.age?.label.orEmpty())
            ReviewRow(label = AddStrings.LABEL_STATUS, value = form.propertyStatus)
            ReviewRow(label = AddStrings.LABEL_LOCATION, value = locationText(form))
        }

        if (form.description.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = AddStrings.LABEL_DESCRIPTION,
                    color = HomeTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = form.description,
                    color = Black,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (descriptionExpanded) Int.MAX_VALUE else 3
                )
                Text(
                    text = if (descriptionExpanded) AddStrings.ACTION_SHOW_LESS else AddStrings.ACTION_SHOW_MORE,
                    color = BrandBlue,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        descriptionExpanded = !descriptionExpanded
                    }
                )
            }
        }

        ReviewRow(label = AddStrings.LABEL_PHOTOS, value = photosText(form))

        // Confirmation checkbox required before publishing.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { confirmed = !confirmed },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = confirmed,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = BrandBlue,
                    uncheckedColor = HomeTextSecondary,
                    checkmarkColor = White
                )
            )
            Text(
                text = buildAnnotatedString {
                    append(AddStrings.CONFIRM_ACCURACY_PREFIX)
                    withStyle(SpanStyle(color = BrandBlue, fontWeight = FontWeight.Bold)) {
                        append(AddStrings.TERMS_AND_CONDITIONS)
                    }
                },
                color = Black,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(AddDims.NAV_BUTTON_BORDER_WIDTH, HomeSearchBarBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Black,
                    containerColor = HomeCategoryUnselected
                )
            ) {
                Text(
                    text = AddStrings.ACTION_BACK,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            ContinueButton(
                text = AddStrings.ACTION_PUBLISH_LISTING,
                onClick = onSubmit,
                enabled = confirmed,
                isLoading = isSubmitting,
                modifier = Modifier.weight(2f)
            )
        }
    }
}

@Composable
private fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = HomeTextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value.ifBlank { AddStrings.PLACEHOLDER_DASH },
            color = Black,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1.5f)
        )
    }
}

private fun listingTypeLabel(rentBuy: RentBuy?): String = when (rentBuy) {
    RentBuy.RENT -> AddStrings.LISTING_FOR_RENT
    RentBuy.BUY -> AddStrings.LISTING_FOR_SALE
    null -> AddStrings.PLACEHOLDER_DASH
}

private fun bedsCount(bedroomType: BedroomType?): Int = when (bedroomType) {
    BedroomType.ONE_RK, BedroomType.ONE_BHK, BedroomType.STUDIO_APARTMENT -> 1
    BedroomType.TWO_BHK -> 2
    BedroomType.THREE_BHK -> 3
    BedroomType.FOUR_BHK -> 4
    BedroomType.FIVE_BHK -> 5
    BedroomType.SIX_BHK -> 6
    BedroomType.SIX_PLUS_BHK -> 7
    null -> 0
}

private fun configurationText(form: PropertyForm): String {
    val beds = bedsCount(form.bedroomType)
    if (beds == 0 && form.bathrooms == 0) return AddStrings.PLACEHOLDER_DASH
    return "$beds${AddStrings.BEDS_BATHS_SEPARATOR}${form.bathrooms}${AddStrings.BATHS_SUFFIX}"
}

private fun priceText(form: PropertyForm): String {
    val price = form.price.toDoubleOrNull() ?: return form.price.ifBlank { AddStrings.PLACEHOLDER_DASH }
    val formatted = formatIndianPrice(price)
    val area = form.builtUpArea.toDoubleOrNull()?.takeIf { it > 0 }
        ?: return formatted
    val perSqFt = AddStrings.RUPEE_PREFIX + formatIndianNumber(price / area) + AddStrings.PER_SQFT_SUFFIX
    return "$formatted ($perSqFt)"
}

private fun formatIndianPrice(value: Double): String = when {
    value >= 1_00_00_000 -> AddStrings.RUPEE_PREFIX + trimToTwoDecimals(value / 1_00_00_000) + AddStrings.CRORE_SUFFIX
    value >= 1_00_000 -> AddStrings.RUPEE_PREFIX + trimToTwoDecimals(value / 1_00_000) + AddStrings.LAKH_SUFFIX
    else -> AddStrings.RUPEE_PREFIX + formatIndianNumber(value)
}

private fun trimToTwoDecimals(value: Double): String {
    return "%.2f".format(value).trimEnd('0').trimEnd('.')
}

private fun formatIndianNumber(value: Double): String {
    return NumberFormat.getNumberInstance(Locale(AddStrings.LOCALE_LANGUAGE, AddStrings.LOCALE_COUNTRY)).apply {
        maximumFractionDigits = 0
    }.format(value)
}

private fun areaText(area: String): String {
    val value = area.toDoubleOrNull() ?: return area.ifBlank { AddStrings.PLACEHOLDER_DASH }
    return formatIndianNumber(value) + AddStrings.SQ_FT_SUFFIX
}

private fun locationText(form: PropertyForm): String {
    return listOf(form.locality, form.city)
        .filter { it.isNotBlank() }
        .joinToString(", ")
        .ifBlank { AddStrings.PLACEHOLDER_DASH }
}

private fun photosText(form: PropertyForm): String {
    val photos = "${form.images.size}${AddStrings.PHOTOS_SUFFIX}"
    return if (form.videoUrl.isNotBlank()) photos + AddStrings.VIDEO_SUFFIX else photos
}
