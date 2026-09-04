package com.realeapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.realeapp.ui.navigation.AppScreen
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.MainBackground
import com.realeapp.ui.theme.TextPrimary

private val BOTTOM_NAV_HEIGHT = 120.dp

@Composable
fun BottomNavBar(
    tabs: List<AppScreen>,
    selectedTab: AppScreen,
    onTabSelected: (AppScreen) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(BOTTOM_NAV_HEIGHT)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        containerColor = MainBackground,
        tonalElevation = 0.dp
    ) {
        tabs.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = selectedTab == screen,
                onClick = { onTabSelected(screen) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Accent.copy(alpha = 0.18f),
                    selectedIconColor = Accent,
                    selectedTextColor = TextPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
        }
    }
}
