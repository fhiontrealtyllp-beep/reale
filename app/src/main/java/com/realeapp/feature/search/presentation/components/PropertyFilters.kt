package com.realeapp.feature.search.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.realeapp.feature.search.domain.model.Age
import com.realeapp.feature.search.domain.model.Amenity
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.CarpetAreaRange
import com.realeapp.feature.search.domain.model.Facing
import com.realeapp.feature.search.domain.model.Furnishing
import com.realeapp.feature.search.domain.model.PriceRange
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.domain.model.ResidentialCommercial
import com.realeapp.feature.search.presentation.SearchDims
import com.realeapp.feature.search.presentation.SearchStrings
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.ControlAccent
import com.realeapp.ui.theme.HomeCategoryUnselected
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.OnControlAccent
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.White

private val bathroomOptions = (1..5).toList()

private data class RangeOption(
    val label: String,
    val min: Double,
    val max: Double
)

private val priceOptions = listOf(
    RangeOption(SearchStrings.FILTER_PRICE_UNDER_25_LAKH, 0.0, 2_500_000.0),
    RangeOption(SearchStrings.FILTER_PRICE_25_TO_50_LAKH, 2_500_000.0, 5_000_000.0),
    RangeOption(SearchStrings.FILTER_PRICE_50_LAKH_TO_1_CRORE, 5_000_000.0, 10_000_000.0),
    RangeOption(SearchStrings.FILTER_PRICE_1_TO_5_CRORE, 10_000_000.0, 50_000_000.0),
    RangeOption(SearchStrings.FILTER_PRICE_5_CRORE_PLUS, 50_000_000.0, Double.MAX_VALUE)
)

private val rentOptions = listOf(
    RangeOption(SearchStrings.FILTER_RENT_UNDER_10_THOUSAND, 0.0, 10_000.0),
    RangeOption(SearchStrings.FILTER_RENT_10_TO_25_THOUSAND, 10_000.0, 25_000.0),
    RangeOption(SearchStrings.FILTER_RENT_25_TO_50_THOUSAND, 25_000.0, 50_000.0),
    RangeOption(SearchStrings.FILTER_RENT_50_THOUSAND_PLUS, 50_000.0, Double.MAX_VALUE)
)

private val areaOptions = listOf(
    RangeOption(SearchStrings.FILTER_AREA_UNDER_500, 0.0, 500.0),
    RangeOption(SearchStrings.FILTER_AREA_500_TO_1000, 500.0, 1_000.0),
    RangeOption(SearchStrings.FILTER_AREA_1000_TO_2000, 1_000.0, 2_000.0),
    RangeOption(SearchStrings.FILTER_AREA_2000_PLUS, 2_000.0, Double.MAX_VALUE)
)

@Composable
fun PropertyFilters(
    filter: PropertyFilter,
    onFilterChange: (PropertyFilter) -> Unit,
    citySuggestionsProvider: suspend (String) -> List<String> = { _ -> emptyList() },
    localitySuggestionsProvider: suspend (String, String) -> List<String> = { _, _ -> emptyList() },
    modifier: Modifier = Modifier
) {
    var showCityDialog by remember { mutableStateOf(false) }
    var showLocalityDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SearchDims.FILTER_PANEL_CORNER_RADIUS),
        color = White
    ) {
        Column(
            modifier = Modifier.padding(SearchDims.FILTER_PANEL_PADDING)
        ) {
            val effectiveRentBuy = filter.rentBuy ?: RentBuy.RENT
            val effectiveCategory = filter.residentialCommercial ?: ResidentialCommercial.RESIDENTIAL

            // Adds vertical space above the listing-type toggle title
            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))

            ListingTypeToggle(
                selected = effectiveRentBuy,
                onSelected = { onFilterChange(filter.copy(rentBuy = it, priceRange = null)) }
            )

            // Adds vertical space between the toggle title and the filter content
            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))

            val budgetOptions = if (effectiveRentBuy == RentBuy.RENT) rentOptions else priceOptions
            FilterChipGroup(
                title = SearchStrings.SECTION_BUDGET,
                options = budgetOptions,
                selected = budgetOptions.find { it.matches(filter.priceRange?.min, filter.priceRange?.max) },
                onSelected = { option ->
                    onFilterChange(filter.copy(priceRange = option?.let { PriceRange(it.min, it.max) }))
                },
                optionLabel = { it.label }
            )
            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))

            ResidentialCommercialToggle(
                selected = effectiveCategory,
                onSelected = { onFilterChange(filter.copy(residentialCommercial = it)) }
            )

            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))
            LocationSelector(
                label = SearchStrings.FILTER_CITY,
                value = filter.city,
                placeholder = SearchStrings.FILTER_SELECT_CITY,
                onClick = { showCityDialog = true }
            )
            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))

            LocationSelector(
                label = SearchStrings.FILTER_LOCALITY,
                value = filter.localities.firstOrNull(),
                placeholder = SearchStrings.FILTER_SELECT_LOCALITY,
                onClick = { showLocalityDialog = true }
            )

            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))
            FilterChipGroup(
                title = SearchStrings.FILTER_PROPERTY_TYPE,
                options = PropertyType.entries,
                selected = filter.propertyType,
                onSelected = { onFilterChange(filter.copy(propertyType = it)) },
                optionLabel = { it.label }
            )
            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))
            FilterChipGroup(
                title = SearchStrings.FILTER_BEDROOMS,
                options = BedroomType.entries,
                selected = filter.bedroomType,
                onSelected = { onFilterChange(filter.copy(bedroomType = it)) },
                optionLabel = { it.label }
            )
            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))

            FilterChipGroup(
                title = SearchStrings.FILTER_BATHROOMS,
                options = bathroomOptions,
                selected = filter.bathrooms,
                onSelected = { onFilterChange(filter.copy(bathrooms = it)) },
                optionLabel = Int::toString
            )
            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))

            FilterChipGroup(
                title = SearchStrings.FILTER_FURNISHING,
                options = Furnishing.entries,
                selected = filter.furnishing,
                onSelected = { onFilterChange(filter.copy(furnishing = it)) },
                optionLabel = { it.label }
            )
            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))

            FilterChipGroup(
                title = SearchStrings.FILTER_FACING,
                options = Facing.entries,
                selected = filter.facing,
                onSelected = { onFilterChange(filter.copy(facing = it)) },
                optionLabel = { it.label }
            )
            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))
            FilterChipGroup(
                title = SearchStrings.FILTER_AGE,
                options = Age.entries,
                selected = filter.age,
                onSelected = { onFilterChange(filter.copy(age = it)) },
                optionLabel = { it.label }
            )
            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))
            MultiFilterChipGroup(
                title = SearchStrings.FILTER_AMENITIES,
                options = Amenity.entries,
                selected = filter.amenities,
                onSelectionChange = { onFilterChange(filter.copy(amenities = it)) },
                optionLabel = { it.label }
            )

            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))
            AreaRangeFilter(
                title = SearchStrings.FILTER_CARPET_AREA,
                range = filter.carpetAreaRange,
                onRangeChange = { onFilterChange(filter.copy(carpetAreaRange = it)) }
            )
            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))
            AreaRangeFilter(
                title = SearchStrings.FILTER_BUILT_UP_AREA,
                range = filter.builtUpAreaRange,
                onRangeChange = { onFilterChange(filter.copy(builtUpAreaRange = it)) }
            )
            Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))
            AreaRangeFilter(
                title = SearchStrings.FILTER_SUPER_BUILT_UP_AREA,
                range = filter.superBuiltUpAreaRange,
                onRangeChange = { onFilterChange(filter.copy(superBuiltUpAreaRange = it)) }
            )
        }
    }

    if (showCityDialog) {
        ChipSelectionDialog(
            title = SearchStrings.FILTER_CITY,
            searchHint = SearchStrings.DIALOG_SEARCH_CITY_HINT,
            initialQuery = "",
            selectedValue = filter.city,
            chipProvider = citySuggestionsProvider,
            onChipSelected = { city ->
                onFilterChange(
                    filter.copy(
                        city = city,
                        cityLatLng = null,
                        localities = emptyList(),
                        pincode = null
                    )
                )
            },
            onDismiss = { showCityDialog = false }
        )
    }

    if (showLocalityDialog) {
        val selectedCity = filter.city.orEmpty()
        ChipSelectionDialog(
            title = SearchStrings.FILTER_LOCALITY,
            searchHint = SearchStrings.DIALOG_SEARCH_LOCALITY_HINT,
            initialQuery = "",
            selectedValue = filter.localities.firstOrNull(),
            chipProvider = { query -> localitySuggestionsProvider(selectedCity, query) },
            onChipSelected = { locality ->
                onFilterChange(
                    filter.copy(
                        localities = listOf(locality),
                        pincode = null
                    )
                )
            },
            onDismiss = { showLocalityDialog = false }
        )
    }
}

@Composable
private fun LocationSelector(
    label: String,
    value: String?,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayValue = value ?: placeholder
    val valueColor = if (value != null) Black else HomeTextSecondary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = SearchDims.FILTER_SELECTOR_HORIZONTAL_PADDING,
                vertical = SearchDims.FILTER_SELECTOR_VERTICAL_PADDING
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(SearchDims.FILTER_VALUE_SPACING)
        ) {
            Text(
                text = label,
                color = HomeTextSecondary,
            )
            Text(
                text = displayValue,
                color = valueColor,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = HomeTextSecondary,
            modifier = Modifier.size(SearchDims.FILTER_SELECTOR_ICON_SIZE)
        )
    }
}

@Composable
private fun FilterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(SearchDims.FILTER_FIELD_CORNER_RADIUS),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Accent,
            unfocusedBorderColor = HomeSearchBarBorder,
            focusedContainerColor = AppBackground,
            unfocusedContainerColor = AppBackground
        ),
        modifier = modifier
    )
}

@Composable
private fun AreaRangeFilter(
    title: String,
    range: CarpetAreaRange?,
    onRangeChange: (CarpetAreaRange?) -> Unit
) {
    FilterChipGroup(
        title = title,
        options = areaOptions,
        selected = areaOptions.find { it.matches(range?.min, range?.max) },
        onSelected = { option -> onRangeChange(option?.let { CarpetAreaRange(it.min, it.max) }) },
        optionLabel = { it.label }
    )
}

@Composable
private fun ListingTypeToggle(
    selected: RentBuy,
    onSelected: (RentBuy) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SearchDims.FILTER_TITLE_TO_CHIPS_SPACING)
    ) {
        FilterSectionHeader(SearchStrings.FILTER_LISTING_INTENT)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(SearchDims.FILTER_TOGGLE_ROW_HEIGHT)
                .clip(CircleShape)
                .background(HomeCategoryUnselected)
                .padding(SearchDims.FILTER_TOGGLE_INNER_PADDING)
        ) {
            CapsuleToggleSegment(
                label = RentBuy.RENT.label,
                isSelected = selected == RentBuy.RENT,
                onClick = { onSelected(RentBuy.RENT) },
                modifier = Modifier.weight(1f)
            )
            CapsuleToggleSegment(
                label = RentBuy.BUY.label,
                isSelected = selected == RentBuy.BUY,
                onClick = { onSelected(RentBuy.BUY) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ResidentialCommercialToggle(
    selected: ResidentialCommercial,
    onSelected: (ResidentialCommercial) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SearchDims.FILTER_TITLE_TO_CHIPS_SPACING)
    ) {
        FilterSectionHeader(SearchStrings.FILTER_CATEGORY)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(SearchDims.FILTER_TOGGLE_ROW_HEIGHT)
                .clip(CircleShape)
                .background(HomeCategoryUnselected)
                .padding(SearchDims.FILTER_TOGGLE_INNER_PADDING)
        ) {
            CapsuleToggleSegment(
                label = ResidentialCommercial.RESIDENTIAL.label,
                isSelected = selected == ResidentialCommercial.RESIDENTIAL,
                onClick = { onSelected(ResidentialCommercial.RESIDENTIAL) },
                modifier = Modifier.weight(1f)
            )
            CapsuleToggleSegment(
                label = ResidentialCommercial.COMMERCIAL.label,
                isSelected = selected == ResidentialCommercial.COMMERCIAL,
                onClick = { onSelected(ResidentialCommercial.COMMERCIAL) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CapsuleToggleSegment(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .clip(CircleShape)
            .background(if (isSelected) ControlAccent else Color.Transparent)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = if (isSelected) OnControlAccent else HomeTextSecondary,
                modifier = Modifier.size(SearchDims.FILTER_TOGGLE_ICON_SIZE)
            )
            Spacer(modifier = Modifier.width(SearchDims.FILTER_TOGGLE_ICON_TEXT_SPACING))
        }
        Text(
            text = label,
            color = if (isSelected) OnControlAccent else HomeTextSecondary,
            fontSize = SearchDims.FILTER_TOGGLE_FONT_SIZE,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

private fun RangeOption.matches(minimum: Double?, maximum: Double?): Boolean =
    minimum == min && maximum == max

@Preview(showBackground = true, name = "Property Filters")
@Preview(showBackground = true, name = "Property Filters Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PropertyFiltersPreview() {
    RealeTheme {
        PropertyFilters(
            filter = PropertyFilter(
                city = "Delhi",
                localities = listOf("Connaught Place"),
                rentBuy = RentBuy.BUY,
                propertyType = PropertyType.APARTMENT
            ),
            onFilterChange = {},
            citySuggestionsProvider = { _ -> emptyList() },
            localitySuggestionsProvider = { _, _ -> emptyList() }
        )
    }
}
