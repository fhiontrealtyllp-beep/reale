package com.realeapp.feature.search.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.Outline
import com.realeapp.ui.theme.TextPrimary

@Composable
fun SearchHeader(
    onOpenFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onOpenFilter,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, Outline),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Accent
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Search properties...",
                color = TextPrimary.copy(alpha = 0.6f),
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Filter",
                tint = Accent
            )
        }
    }
}
