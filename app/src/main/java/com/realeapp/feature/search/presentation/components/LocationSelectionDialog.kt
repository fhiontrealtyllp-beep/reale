package com.realeapp.feature.search.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.realeapp.feature.search.domain.model.LocationSuggestion
import com.realeapp.feature.search.presentation.SearchDims
import com.realeapp.feature.search.presentation.SearchStrings
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.FilterChipUnselectedContainer
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.OnAccentText
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.TextPrimary
import com.realeapp.ui.theme.White
import com.realeapp.util.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

private const val TAG = "LocationSelectionDialog"

@Composable
fun LocationSelectionDialog(
    title: String,
    initialQuery: String,
    suggestionProvider: suspend (String) -> List<LocationSuggestion>,
    onSuggestionSelected: (LocationSuggestion) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf(initialQuery) }
    var suggestions by remember { mutableStateOf(emptyList<LocationSuggestion>()) }

    LaunchedEffect(query) {
        delay(SearchStrings.SUGGESTIONS_DEBOUNCE_MS)
        try {
            suggestions = suggestionProvider(query)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(TAG, "Failed to load suggestions: ${e.message}", e)
            suggestions = emptyList()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SearchDims.DIALOG_PADDING),
            shape = RoundedCornerShape(SearchDims.DIALOG_CORNER_RADIUS),
            color = White
        ) {
            Column(
                modifier = Modifier.padding(SearchDims.DIALOG_CONTENT_PADDING),
                verticalArrangement = Arrangement.spacedBy(SearchDims.DIALOG_SECTION_SPACING)
            ) {
                Text(
                    text = title,
                    color = Black,
                    style = MaterialTheme.typography.titleMedium
                )

                LocationSearchBar(
                    query = query,
                    suggestions = suggestions,
                    onQueryChange = { query = it },
                    onSuggestionSelected = { suggestion ->
                        onSuggestionSelected(suggestion)
                        onDismiss()
                    },
                    onClearQuery = { query = "" },
                    autoFocus = true,
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = SearchStrings.DIALOG_CANCEL,
                        color = Accent
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipSelectionDialog(
    title: String,
    searchHint: String,
    initialQuery: String,
    selectedValue: String?,
    chipProvider: suspend (String) -> List<String>,
    onChipSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf(initialQuery) }
    var chips by remember { mutableStateOf(emptyList<String>()) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            delay(SearchStrings.SUGGESTIONS_DEBOUNCE_MS)
        }
        try {
            chips = chipProvider(query)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(TAG, "Failed to load chips: ${e.message}", e)
            chips = emptyList()
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SearchDims.DIALOG_PADDING),
            shape = RoundedCornerShape(SearchDims.DIALOG_CORNER_RADIUS),
            color = White
        ) {
            Column(
                modifier = Modifier.padding(SearchDims.DIALOG_CONTENT_PADDING),
                verticalArrangement = Arrangement.spacedBy(SearchDims.CHIP_DIALOG_SECTION_SPACING)
            ) {
                Text(
                    text = title,
                    color = Black,
                    style = MaterialTheme.typography.titleMedium
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SearchDims.CHIP_DIALOG_SEARCH_HEIGHT),
                    shape = RoundedCornerShape(SearchDims.CHIP_DIALOG_SEARCH_CORNER_RADIUS),
                    color = White,
                    border = BorderStroke(SearchDims.BORDER_WIDTH, HomeSearchBarBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SearchDims.SEARCH_HORIZONTAL_PADDING),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = SearchStrings.CD_SEARCH_ICON,
                            tint = HomeTextSecondary,
                            modifier = Modifier.size(SearchDims.SEARCH_ICON_SIZE)
                        )
                        Spacer(modifier = Modifier.width(SearchDims.SEARCH_CONTENT_SPACING))
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Black),
                            cursorBrush = SolidColor(BrandBlue),
                            decorationBox = { innerTextField ->
                                if (query.isBlank()) {
                                    Text(
                                        text = searchHint,
                                        color = HomeTextSecondary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1
                                    )
                                }
                                innerTextField()
                            }
                        )
                        if (query.isNotBlank()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = SearchStrings.CD_CLEAR,
                                    tint = HomeTextSecondary,
                                    modifier = Modifier.size(SearchDims.SEARCH_ICON_SIZE)
                                )
                            }
                        }
                    }
                }

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = SearchDims.CHIP_DIALOG_MAX_HEIGHT)
                        .verticalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(SearchDims.CHIP_DIALOG_CHIP_SPACING),
                    verticalArrangement = Arrangement.spacedBy(SearchDims.CHIP_DIALOG_CHIP_SPACING)
                ) {
                    chips.forEach { chip ->
                        val isSelected = chip.equals(selectedValue, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                keyboardController?.hide()
                                onChipSelected(chip)
                                onDismiss()
                            },
                            label = { Text(chip) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Accent,
                                selectedLabelColor = OnAccentText,
                                containerColor = FilterChipUnselectedContainer,
                                labelColor = TextPrimary
                            )
                        )
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = SearchStrings.DIALOG_CANCEL,
                        color = Accent
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Location Selection Dialog")
@Composable
private fun LocationSelectionDialogPreview() {
    RealeTheme {
        LocationSelectionDialog(
            title = SearchStrings.FILTER_CITY,
            initialQuery = "",
            suggestionProvider = { emptyList() },
            onSuggestionSelected = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, name = "Chip Selection Dialog")
@Composable
private fun ChipSelectionDialogPreview() {
    RealeTheme {
        ChipSelectionDialog(
            title = SearchStrings.FILTER_CITY,
            searchHint = SearchStrings.DIALOG_SEARCH_CITY_HINT,
            initialQuery = "",
            selectedValue = "Bengaluru",
            chipProvider = { listOf("Bengaluru", "Mumbai", "Delhi", "Chennai", "Hyderabad", "Pune", "Kolkata", "Goa") },
            onChipSelected = {},
            onDismiss = {}
        )
    }
}
