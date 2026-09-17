package com.realeapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realeapp.ui.navigation.AppScreen
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.ControlAccent
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.White

private val BOTTOM_NAV_HEIGHT = 68.dp
private val BOTTOM_NAV_HORIZONTAL_MARGIN = 20.dp
private val BOTTOM_NAV_BOTTOM_MARGIN = 16.dp
private val BOTTOM_NAV_SHADOW_ELEVATION = 16.dp
private val BOTTOM_NAV_BORDER_WIDTH = 1.dp
private const val BOTTOM_NAV_GLASS_ALPHA = 0.78f
private const val BOTTOM_NAV_BORDER_ALPHA = 0.10f
private const val BOTTOM_NAV_INDICATOR_ALPHA = 0.14f

/**
 * Extra bottom space scrollable content should reserve so the floating
 * glass nav capsule never covers the last item.
 */
val BOTTOM_NAV_CLEARANCE = 88.dp

// Floating frosted-glass capsule: translucent surface + hairline border +
// soft shadow. Content scrolls behind it, which sells the glass look.
@Composable
fun BottomNavBar(
    tabs: List<AppScreen>,
    selectedTab: AppScreen,
    onTabSelected: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = BOTTOM_NAV_HORIZONTAL_MARGIN)
            .navigationBarsPadding()
            .padding(bottom = BOTTOM_NAV_BOTTOM_MARGIN)
            .height(BOTTOM_NAV_HEIGHT),
        shape = CircleShape,
        color = White.copy(alpha = BOTTOM_NAV_GLASS_ALPHA),
        contentColor = Black,
        shadowElevation = BOTTOM_NAV_SHADOW_ELEVATION,
        border = BorderStroke(
            width = BOTTOM_NAV_BORDER_WIDTH,
            color = Black.copy(alpha = BOTTOM_NAV_BORDER_ALPHA)
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            tabs.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                    label = { Text(screen.title) },
                    selected = selectedTab == screen,
                    onClick = { onTabSelected(screen) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = ControlAccent.copy(alpha = BOTTOM_NAV_INDICATOR_ALPHA),
                        selectedIconColor = ControlAccent,
                        selectedTextColor = ControlAccent,
                        unselectedIconColor = HomeTextSecondary,
                        unselectedTextColor = HomeTextSecondary
                    )
                )
            }
        }
    }
}
