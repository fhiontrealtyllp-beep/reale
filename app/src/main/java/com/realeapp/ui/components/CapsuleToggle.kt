package com.realeapp.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realeapp.ui.theme.ControlAccent
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.OnControlAccent
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.White

private val CAPSULE_TOGGLE_HEIGHT = 48.dp
private val CAPSULE_TOGGLE_INNER_PADDING = 4.dp
private val CAPSULE_TOGGLE_ELEVATION = 6.dp
private val CAPSULE_TOGGLE_FONT_SIZE = 14.sp

/**
 * Segmented capsule toggle: a white raised track holding flat segments where
 * the selected one fills with the accent color. Used for the Buy/Rent and
 * Residential/Commercial switches so they look identical across screens.
 *
 * @param options Segment labels, in order.
 * @param selectedIndex Index of the currently selected segment.
 * @param onSelect Callback invoked with the tapped segment index.
 * @param modifier Modifier to be applied to the toggle.
 */
@Composable
fun CapsuleToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(CAPSULE_TOGGLE_HEIGHT)
            .shadow(CAPSULE_TOGGLE_ELEVATION, CircleShape)
            // Shaped background instead of clip() so segment shadows aren't
            // clipped to the capsule bounds.
            .background(White, CircleShape)
            .padding(CAPSULE_TOGGLE_INNER_PADDING),
        horizontalArrangement = Arrangement.spacedBy(CAPSULE_TOGGLE_INNER_PADDING)
    ) {
        options.forEachIndexed { index, label ->
            CapsuleToggleSegment(
                label = label,
                isSelected = index == selectedIndex,
                onClick = { onSelect(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CapsuleToggleSegment(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(CircleShape)
            .background(if (isSelected) ControlAccent else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) OnControlAccent else HomeTextSecondary,
            fontSize = CAPSULE_TOGGLE_FONT_SIZE,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, name = "Capsule Toggle Light")
@Composable
private fun CapsuleTogglePreview() {
    RealeTheme(darkTheme = false) {
        CapsuleToggle(
            options = listOf("Rent", "Buy"),
            selectedIndex = 0,
            onSelect = {}
        )
    }
}

@Preview(showBackground = true, name = "Capsule Toggle Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CapsuleToggleDarkPreview() {
    RealeTheme(darkTheme = true) {
        CapsuleToggle(
            options = listOf("Rent", "Buy"),
            selectedIndex = 1,
            onSelect = {}
        )
    }
}
