package com.realeapp.feature.search.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.realeapp.feature.search.presentation.SearchDims
import com.realeapp.ui.theme.FilterChipContainer
import com.realeapp.ui.theme.FilterChipLabel
import com.realeapp.ui.theme.FilterChipSelectedContainer
import com.realeapp.ui.theme.FilterChipSelectedLabel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> FilterChipGroup(
    title: String,
    options: List<T>,
    selected: T?,
    onSelected: (T?) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SearchDims.FILTER_TITLE_TO_CHIPS_SPACING)
    ) {
        FilterSectionHeader(title)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SearchDims.FILTER_ITEM_SPACING)) {
            options.forEach { option ->
                val isSelected = option == selected
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelected(if (isSelected) null else option) },
                    label = { Text(optionLabel(option)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FilterChipSelectedContainer,
                        selectedLabelColor = FilterChipSelectedLabel,
                        containerColor = FilterChipContainer,
                        labelColor = FilterChipLabel
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> MultiFilterChipGroup(
    title: String,
    options: List<T>,
    selected: List<T>,
    onSelectionChange: (List<T>) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SearchDims.FILTER_TITLE_TO_CHIPS_SPACING)
    ) {
        FilterSectionHeader(title)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SearchDims.FILTER_ITEM_SPACING)) {
            options.forEach { option ->
                val isSelected = option in selected
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onSelectionChange(
                            if (isSelected) selected - option else selected + option
                        )
                    },
                    label = { Text(optionLabel(option)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FilterChipSelectedContainer,
                        selectedLabelColor = FilterChipSelectedLabel,
                        containerColor = FilterChipContainer,
                        labelColor = FilterChipLabel
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(SearchDims.FILTER_SECTION_SPACING))
    }
}
