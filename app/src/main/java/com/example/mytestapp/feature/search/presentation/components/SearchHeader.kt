package com.example.mytestapp.feature.search.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytestapp.feature.search.domain.model.PropertyFilter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchHeader(
    filter: PropertyFilter?,
    onOpenFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val city = filter?.city
    val pincode = filter?.pincode
    val localities = filter?.localities.orEmpty()
    val hasPincodeOnly = !pincode.isNullOrBlank() && city.isNullOrBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when {
                    hasPincodeOnly -> "Pin Code: $pincode"
                    !city.isNullOrBlank() -> "You are in $city"
                    else -> "Select your City"
                },
                color = Color(0xFFFBFBFB),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenFilter() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!city.isNullOrBlank() && !hasPincodeOnly) {
            if (localities.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    localities.forEach { locality ->
                        AssistChip(
                            onClick = onOpenFilter,
                            label = { Text(text = locality, color = Color(0xFFFBFBFB), fontSize = 12.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFF2B3C83)
                            )
                        )
                    }
                }
            } else {
                Text(
                    text = "Select Localities",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onOpenFilter() }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onOpenFilter() }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFFFBFBFB)
            )
            Text(
                text = "Search properties...",
                color = Color(0xFFFBFBFB),
                fontSize = 16.sp
            )
        }
    }
}
