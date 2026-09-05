package com.realeapp.feature.search.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.FilterChipUnselectedContainer
import com.realeapp.ui.theme.TextPrimary

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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterSectionHeader(title)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val isSelected = option == selected
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelected(if (isSelected) null else option) },
                    label = { Text(optionLabel(option)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Accent,
                        selectedLabelColor = Black,
                        containerColor = FilterChipUnselectedContainer,
                        labelColor = TextPrimary
                    )
                )
            }
        }
    }
}
