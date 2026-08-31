package com.example.mytestapp.feature.search.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mytestapp.feature.search.domain.model.Property

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyList(
    properties: List<Property>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasReachedEnd: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onLike: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= totalItems - 2 && totalItems > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    PullToRefreshBox(
        isRefreshing = isLoading && properties.isNotEmpty(),
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF141C3D))
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (properties.isEmpty() && !isLoading) {
                item {
                    EmptySearchResults(
                        modifier = Modifier.fillParentMaxSize()
                    )
                }
            } else {
                itemsIndexed(
                    items = properties,
                    key = { _, property -> property.id }
                ) { index, property ->
                    PropertyListItem(
                        property = property,
                        onLike = { onLike(property.documentId ?: property.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    PagedFooter(
                        showLoader = isLoadingMore && !hasReachedEnd,
                        showEndOfResults = hasReachedEnd && properties.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
