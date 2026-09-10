package com.realeapp.feature.search.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.realeapp.feature.search.domain.model.LocationSuggestion
import com.realeapp.feature.search.presentation.SearchDims
import com.realeapp.feature.search.presentation.SearchStrings
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.RealeTheme
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
