package com.realeapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.realeapp.ui.navigation.AppScreen
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.White

private val BOTTOM_NAV_CORNER_RADIUS = 20.dp

@Composable
fun BottomNavBar(
    tabs: List<AppScreen>,
    selectedTab: AppScreen,
    onTabSelected: (AppScreen) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = BOTTOM_NAV_CORNER_RADIUS, topEnd = BOTTOM_NAV_CORNER_RADIUS)),
        containerColor = White,
        contentColor = Black,
        tonalElevation = 0.dp
    ) {
        tabs.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = selectedTab == screen,
                onClick = { onTabSelected(screen) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = BrandBlue.copy(alpha = 0.12f),
                    selectedIconColor = BrandBlue,
                    selectedTextColor = BrandBlue,
                    unselectedIconColor = HomeTextSecondary,
                    unselectedTextColor = HomeTextSecondary
                )
            )
        }
    }
}
