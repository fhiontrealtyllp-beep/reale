package com.realeapp.feature.search.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.realeapp.feature.search.presentation.PropertiesStrings
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.White

internal object PillTabsDimens {
    val TAB_CORNER_RADIUS = 20.dp
    val TAB_HORIZONTAL_PADDING = 16.dp
    val TAB_VERTICAL_PADDING = 10.dp
    val TAB_SPACING = 10.dp
    val BORDER_WIDTH = 1.dp
}

/**
 * Reusable pill-style tab row with the same look as the search category tabs.
 */
@Composable
fun PillTabsRow(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PillTabsDimens.TAB_SPACING)
    ) {
        itemsIndexed(tabs) { index, label ->
            val selected = index == selectedIndex
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(PillTabsDimens.TAB_CORNER_RADIUS))
                    .clickable { onSelect(index) },
                shape = RoundedCornerShape(PillTabsDimens.TAB_CORNER_RADIUS),
                color = if (selected) BrandBlue else White,
                border = if (selected) null else BorderStroke(PillTabsDimens.BORDER_WIDTH, HomeSearchBarBorder)
            ) {
                Text(
                    text = label,
                    color = if (selected) OnBrandContent else Black,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(
                        horizontal = PillTabsDimens.TAB_HORIZONTAL_PADDING,
                        vertical = PillTabsDimens.TAB_VERTICAL_PADDING
                    )
                )
            }
        }
    }
}

private val previewTabs = listOf(
    PropertiesStrings.TAB_BUY,
    PropertiesStrings.TAB_RENT,
    PropertiesStrings.TAB_NEW_PROJECTS,
    PropertiesStrings.TAB_COMMERCIAL
)

@Preview(showBackground = true, name = "Pill Tabs Light")
@Composable
private fun PillTabsRowPreview() {
    RealeTheme(darkTheme = false) {
        PillTabsRow(
            tabs = previewTabs,
            selectedIndex = 0,
            onSelect = {}
        )
    }
}

@Preview(showBackground = true, name = "Pill Tabs Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PillTabsRowDarkPreview() {
    RealeTheme(darkTheme = true) {
        PillTabsRow(
            tabs = previewTabs,
            selectedIndex = 0,
            onSelect = {}
        )
    }
}
