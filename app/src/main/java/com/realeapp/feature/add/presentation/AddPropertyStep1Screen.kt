package com.realeapp.feature.add.presentation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.Villa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandRed
import com.realeapp.ui.theme.HomeCategoryUnselected
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.White

// Property types shown in the design's four-card row.
private val STEP1_PROPERTY_TYPES = listOf(
    PropertyType.APARTMENT,
    PropertyType.VILLA,
    PropertyType.PLOT,
    PropertyType.INDEPENDENT_HOUSE
)

// Listing type order in the design: For Sale first, For Rent second.
private val STEP1_LISTING_TYPES = listOf(RentBuy.BUY, RentBuy.RENT)

@Composable
internal fun AddPropertyStep1Screen(
    form: PropertyForm,
    onRentBuyChanged: (RentBuy) -> Unit,
    onPropertyTypeChanged: (PropertyType) -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onCityChanged: (String) -> Unit,
    onLocalityChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onUseMyLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AddDims.SECTION_SPACING)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AddDims.FIELD_LABEL_SPACING)) {
            Step1FieldLabel(text = AddStrings.LABEL_PROPERTY_TITLE, isRequired = true)
            Step1Field(
                value = form.title,
                onValueChange = onTitleChanged,
                placeholder = AddStrings.PLACEHOLDER_PROPERTY_TITLE,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(AddDims.FIELD_LABEL_SPACING)) {
            Step1FieldLabel(text = AddStrings.LABEL_PROPERTY_TYPE, isRequired = true)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AddDims.TYPE_CARD_SPACING)
            ) {
                STEP1_PROPERTY_TYPES.forEach { propertyType ->
                    PropertyTypeCard(
                        propertyType = propertyType,
                        isSelected = form.propertyType == propertyType,
                        onClick = { onPropertyTypeChanged(propertyType) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(AddDims.FIELD_LABEL_SPACING)) {
            Step1FieldLabel(text = AddStrings.LABEL_LISTING_TYPE, isRequired = true)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AddDims.LISTING_BUTTON_SPACING)
            ) {
                STEP1_LISTING_TYPES.forEach { rentBuy ->
                    ListingTypeButton(
                        rentBuy = rentBuy,
                        isSelected = form.rentBuy == rentBuy,
                        onClick = { onRentBuyChanged(rentBuy) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(AddDims.FIELD_LABEL_SPACING)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Step1FieldLabel(text = AddStrings.LABEL_LOCATION, isRequired = true)
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.clickable(onClick = onUseMyLocation),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MyLocation,
                        contentDescription = AddStrings.CD_MY_LOCATION_LINK,
                        tint = BrandBlue,
                        modifier = Modifier.size(AddDims.LOCATION_LINK_ICON_SIZE)
                    )
                    Spacer(modifier = Modifier.width(AddDims.LOCATION_LINK_ICON_TEXT_SPACING))
                    Text(
                        text = AddStrings.ACTION_USE_MY_LOCATION,
                        color = BrandBlue,
                        fontSize = AddDims.LOCATION_LINK_FONT_SIZE,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Step1Field(
                value = form.address,
                onValueChange = onAddressChanged,
                placeholder = AddStrings.LOCATION_FIELD_PLACEHOLDER,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = AddStrings.CD_SEARCH_LOCATION,
                        tint = HomeTextSecondary,
                        modifier = Modifier.size(AddDims.FIELD_ICON_SIZE)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AddDims.CITY_LOCALITY_SPACING)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AddDims.FIELD_LABEL_SPACING)
            ) {
                Step1FieldLabel(text = AddStrings.LABEL_CITY, isRequired = true)
                Step1Field(
                    value = form.city,
                    onValueChange = onCityChanged,
                    placeholder = AddStrings.SELECT_PREFIX + AddStrings.LABEL_CITY,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    )
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AddDims.FIELD_LABEL_SPACING)
            ) {
                Step1FieldLabel(text = AddStrings.LABEL_LOCALITY, isRequired = true)
                Step1Field(
                    value = form.locality,
                    onValueChange = onLocalityChanged,
                    placeholder = AddStrings.SELECT_PREFIX + AddStrings.LABEL_LOCALITY,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(AddDims.FIELD_LABEL_SPACING)) {
            Step1FieldLabel(text = AddStrings.LABEL_SHORT_DESCRIPTION, isRequired = true)
            Box(modifier = Modifier.fillMaxWidth()) {
                Step1Field(
                    value = form.description,
                    onValueChange = onDescriptionChanged,
                    placeholder = AddStrings.PLACEHOLDER_SHORT_DESCRIPTION,
                    minLines = AddDims.DESCRIPTION_MIN_LINES,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    )
                )
                Text(
                    text = "${form.description.length}${AddStrings.DESCRIPTION_COUNTER_SUFFIX}",
                    color = HomeTextSecondary,
                    fontSize = AddDims.DESCRIPTION_COUNTER_FONT_SIZE,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(AddDims.DESCRIPTION_COUNTER_PADDING)
                )
            }
        }
    }
}

@Composable
private fun Step1FieldLabel(
    text: String,
    isRequired: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            color = Black,
            fontSize = AddDims.FIELD_LABEL_FONT_SIZE,
            fontWeight = FontWeight.Medium
        )
        if (isRequired) {
            Text(
                text = AddStrings.REQUIRED_MARKER,
                color = BrandRed,
                fontSize = AddDims.FIELD_LABEL_FONT_SIZE
            )
        }
    }
}

@Composable
private fun Step1Field(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = HomeTextSecondary,
                fontSize = AddDims.FIELD_PLACEHOLDER_FONT_SIZE,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = leadingIcon,
        singleLine = minLines == 1,
        minLines = minLines,
        shape = RoundedCornerShape(AddDims.FIELD_CORNER_RADIUS),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = Black,
            fontSize = AddDims.FIELD_FONT_SIZE
        ),
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = HomeCategoryUnselected,
            unfocusedContainerColor = HomeCategoryUnselected,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = BrandBlue
        )
    )
}

@Composable
private fun PropertyTypeCard(
    propertyType: PropertyType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(AddDims.TYPE_CARD_HEIGHT)
            .clip(RoundedCornerShape(AddDims.TYPE_CARD_CORNER_RADIUS))
            .background(if (isSelected) BrandBlue.copy(alpha = 0.08f) else White)
            .border(
                width = if (isSelected) AddDims.TYPE_CARD_SELECTED_BORDER_WIDTH else AddDims.TYPE_CARD_BORDER_WIDTH,
                color = if (isSelected) BrandBlue else HomeSearchBarBorder,
                shape = RoundedCornerShape(AddDims.TYPE_CARD_CORNER_RADIUS)
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = propertyTypeIcon(propertyType),
            contentDescription = null,
            tint = if (isSelected) BrandBlue else HomeTextSecondary,
            modifier = Modifier.size(AddDims.TYPE_CARD_ICON_SIZE)
        )
        Spacer(modifier = Modifier.height(AddDims.TYPE_CARD_ICON_TEXT_SPACING))
        Text(
            text = propertyType.label,
            color = if (isSelected) BrandBlue else Black,
            fontSize = AddDims.TYPE_CARD_LABEL_FONT_SIZE,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ListingTypeButton(
    rentBuy: RentBuy,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = if (rentBuy == RentBuy.BUY) AddStrings.LISTING_FOR_SALE else AddStrings.LISTING_FOR_RENT
    val icon = if (rentBuy == RentBuy.BUY) Icons.Outlined.Sell else Icons.Outlined.Key

    Row(
        modifier = modifier
            .height(AddDims.LISTING_BUTTON_HEIGHT)
            .clip(RoundedCornerShape(AddDims.LISTING_BUTTON_CORNER_RADIUS))
            .background(if (isSelected) BrandBlue.copy(alpha = 0.08f) else White)
            .border(
                width = if (isSelected) AddDims.LISTING_BUTTON_SELECTED_BORDER_WIDTH else AddDims.LISTING_BUTTON_BORDER_WIDTH,
                color = if (isSelected) BrandBlue else HomeSearchBarBorder,
                shape = RoundedCornerShape(AddDims.LISTING_BUTTON_CORNER_RADIUS)
            )
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) BrandBlue else HomeTextSecondary,
            modifier = Modifier.size(AddDims.LISTING_BUTTON_ICON_SIZE)
        )
        Spacer(modifier = Modifier.width(AddDims.LISTING_BUTTON_ICON_TEXT_SPACING))
        Text(
            text = label,
            color = if (isSelected) BrandBlue else Black,
            fontSize = AddDims.LISTING_BUTTON_FONT_SIZE,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

private fun propertyTypeIcon(propertyType: PropertyType): ImageVector {
    return when (propertyType) {
        PropertyType.APARTMENT -> Icons.Outlined.Apartment
        PropertyType.VILLA -> Icons.Outlined.Villa
        PropertyType.PLOT,
        PropertyType.LAND -> Icons.Outlined.Landscape
        PropertyType.INDEPENDENT_HOUSE -> Icons.Outlined.House
        else -> Icons.Outlined.Apartment
    }
}
