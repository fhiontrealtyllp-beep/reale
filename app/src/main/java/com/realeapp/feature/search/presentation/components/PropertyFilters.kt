package com.realeapp.feature.search.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.realeapp.ui.theme.HomeSearchBarBorder
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
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SearchDims.FILTER_PANEL_CORNER_RADIUS),
        color = White
    ) {
        Column(
            modifier = Modifier.padding(SearchDims.FILTER_PANEL_PADDING),
            verticalArrangement = Arrangement.spacedBy(SearchDims.FILTER_SECTION_SPACING)
        ) {
            //locality pin
           /* Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SearchDims.FILTER_ITEM_SPACING)
            ) {
                FilterTextField(
                    value = filter.localities.firstOrNull().orEmpty(),
                    onValueChange = { value ->
                        onFilterChange(filter.copy(localities = value.takeIf(String::isNotBlank)?.let(::listOf) ?: emptyList()))
                    },
                    label = SearchStrings.FILTER_LOCALITY,
                    modifier = Modifier.weight(1f)
                )
                FilterTextField(
                    value = filter.pincode.orEmpty(),
                    onValueChange = { onFilterChange(filter.copy(pincode = it.takeIf(String::isNotBlank))) },
                    label = SearchStrings.FILTER_PINCODE,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }*/

            FilterChipGroup(
                title = SearchStrings.FILTER_LISTING_INTENT,
                options = RentBuy.entries,
                selected = filter.rentBuy,
                onSelected = { onFilterChange(filter.copy(rentBuy = it, priceRange = null)) },
                optionLabel = { it.label }
            )
            val budgetOptions = if (filter.rentBuy == RentBuy.RENT) rentOptions else priceOptions
            FilterChipGroup(
                title = SearchStrings.SECTION_BUDGET,
                options = budgetOptions,
                selected = budgetOptions.find { it.matches(filter.priceRange?.min, filter.priceRange?.max) },
                onSelected = { option ->
                    onFilterChange(filter.copy(priceRange = option?.let { PriceRange(it.min, it.max) }))
                },
                optionLabel = { it.label }
            )
            FilterChipGroup(
                title = SearchStrings.FILTER_CATEGORY,
                options = ResidentialCommercial.entries,
                selected = filter.residentialCommercial,
                onSelected = { onFilterChange(filter.copy(residentialCommercial = it)) },
                optionLabel = { it.label }
            )
            FilterChipGroup(
                title = SearchStrings.FILTER_PROPERTY_TYPE,
                options = PropertyType.entries,
                selected = filter.propertyType,
                onSelected = { onFilterChange(filter.copy(propertyType = it)) },
                optionLabel = { it.label }
            )
            FilterChipGroup(
                title = SearchStrings.FILTER_BEDROOMS,
                options = BedroomType.entries,
                selected = filter.bedroomType,
                onSelected = { onFilterChange(filter.copy(bedroomType = it)) },
                optionLabel = { it.label }
            )

            FilterChipGroup(
                title = SearchStrings.FILTER_BATHROOMS,
                options = bathroomOptions,
                selected = filter.bathrooms,
                onSelected = { onFilterChange(filter.copy(bathrooms = it)) },
                optionLabel = Int::toString
            )

            FilterChipGroup(
                title = SearchStrings.FILTER_FURNISHING,
                options = Furnishing.entries,
                selected = filter.furnishing,
                onSelected = { onFilterChange(filter.copy(furnishing = it)) },
                optionLabel = { it.label }
            )
            FilterChipGroup(
                title = SearchStrings.FILTER_FACING,
                options = Facing.entries,
                selected = filter.facing,
                onSelected = { onFilterChange(filter.copy(facing = it)) },
                optionLabel = { it.label }
            )
            FilterChipGroup(
                title = SearchStrings.FILTER_AGE,
                options = Age.entries,
                selected = filter.age,
                onSelected = { onFilterChange(filter.copy(age = it)) },
                optionLabel = { it.label }
            )
            MultiFilterChipGroup(
                title = SearchStrings.FILTER_AMENITIES,
                options = Amenity.entries,
                selected = filter.amenities,
                onSelectionChange = { onFilterChange(filter.copy(amenities = it)) },
                optionLabel = { it.label }
            )

            AreaRangeFilter(
                title = SearchStrings.FILTER_CARPET_AREA,
                range = filter.carpetAreaRange,
                onRangeChange = { onFilterChange(filter.copy(carpetAreaRange = it)) }
            )
            AreaRangeFilter(
                title = SearchStrings.FILTER_BUILT_UP_AREA,
                range = filter.builtUpAreaRange,
                onRangeChange = { onFilterChange(filter.copy(builtUpAreaRange = it)) }
            )
            AreaRangeFilter(
                title = SearchStrings.FILTER_SUPER_BUILT_UP_AREA,
                range = filter.superBuiltUpAreaRange,
                onRangeChange = { onFilterChange(filter.copy(superBuiltUpAreaRange = it)) }
            )
        }
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

private fun RangeOption.matches(minimum: Double?, maximum: Double?): Boolean =
    minimum == min && maximum == max

@Preview(showBackground = true, name = "Property Filters")
@Composable
private fun PropertyFiltersPreview() {
    RealeTheme {
        PropertyFilters(
            filter = PropertyFilter(
                rentBuy = RentBuy.BUY,
                propertyType = PropertyType.APARTMENT
            ),
            onFilterChange = {}
        )
    }
}
