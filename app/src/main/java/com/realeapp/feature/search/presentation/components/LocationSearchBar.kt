package com.realeapp.feature.search.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.realeapp.feature.search.domain.model.LocationSuggestion
import com.realeapp.feature.search.presentation.SearchDims
import com.realeapp.feature.search.presentation.SearchStrings
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.White

@Composable
fun LocationSearchBar(
    query: String,
    suggestions: List<LocationSuggestion>,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (LocationSuggestion) -> Unit,
    onFilterClick: (() -> Unit)? = null,
    onClearQuery: (() -> Unit)? = null,
    autoFocus: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(autoFocus) {
        if (autoFocus) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Column(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(SearchDims.SEARCH_HEIGHT)
                .clip(RoundedCornerShape(SearchDims.SEARCH_CORNER_RADIUS)),
            shape = RoundedCornerShape(SearchDims.SEARCH_CORNER_RADIUS),
            color = White,
            border = BorderStroke(SearchDims.BORDER_WIDTH, HomeSearchBarBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
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
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onFocusChanged { isFocused = it.isFocused },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Black),
                    cursorBrush = SolidColor(BrandBlue),
                    visualTransformation = VisualTransformation.None,
                    decorationBox = { innerTextField ->
                        if (query.isBlank()) {
                            Text(
                                text = SearchStrings.SEARCH_HINT,
                                color = HomeTextSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1
                            )
                        }
                        innerTextField()
                    }
                )

                if (query.isNotBlank() && onClearQuery != null) {
                    IconButton(onClick = onClearQuery) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = SearchStrings.CD_CLEAR,
                            tint = HomeTextSecondary,
                            modifier = Modifier.size(SearchDims.SEARCH_ICON_SIZE)
                        )
                    }
                }

                if (onFilterClick != null) {
                    Box(
                        modifier = Modifier
                            .width(SearchDims.SEARCH_DIVIDER_WIDTH)
                            .height(SearchDims.SEARCH_DIVIDER_HEIGHT)
                            .background(HomeSearchBarBorder)
                    )
                    IconButton(onClick = onFilterClick) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = SearchStrings.CD_FILTER_ICON,
                            tint = BrandBlue,
                            modifier = Modifier.size(SearchDims.SEARCH_ICON_SIZE)
                        )
                    }
                }
            }
        }

        if (isFocused && suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SearchDims.SUGGESTION_LIST_PADDING)
                    .heightIn(max = SearchDims.SUGGESTION_LIST_MAX_HEIGHT),
                shape = RoundedCornerShape(SearchDims.SUGGESTION_LIST_CORNER_RADIUS),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = SearchDims.SUGGESTION_LIST_ELEVATION)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    suggestions.forEachIndexed { index, suggestion ->
                        SuggestionItem(
                            suggestion = suggestion,
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onSuggestionSelected(suggestion)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (index < suggestions.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = SearchDims.SUGGESTION_ITEM_HORIZONTAL_PADDING)
                                    .height(SearchDims.SUGGESTION_ITEM_DIVIDER_HEIGHT)
                                    .background(HomeSearchBarBorder)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: LocationSuggestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(
                horizontal = SearchDims.SUGGESTION_ITEM_HORIZONTAL_PADDING,
                vertical = SearchDims.SUGGESTION_ITEM_VERTICAL_PADDING
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = SearchStrings.CD_SUGGESTION_LOCATION,
            tint = BrandBlue,
            modifier = Modifier.size(SearchDims.SUGGESTION_ITEM_ICON_SIZE)
        )
        Spacer(modifier = Modifier.width(SearchDims.SUGGESTION_ITEM_TEXT_SPACING))
        Text(
            text = suggestion.fullText,
            color = Black,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationSearchBarPreview() {
    RealeTheme {
        val sampleSuggestions = listOf(
            LocationSuggestion("1", "Panaji", "Goa, India", "Panaji, Goa, India"),
            LocationSuggestion("2", "Pune", "Maharashtra, India", "Pune, Maharashtra, India")
        )
        LocationSearchBar(
            query = "",
            suggestions = sampleSuggestions,
            onQueryChange = {},
            onSuggestionSelected = {},
            onFilterClick = {},
            onClearQuery = {},
            autoFocus = false,
            modifier = Modifier.padding(SearchDims.SCREEN_PADDING)
        )
    }
}
