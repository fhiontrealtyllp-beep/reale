package com.realeapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
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
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BottomNavBackground
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.TextPrimary
import com.realeapp.ui.theme.White

private val BOTTOM_NAV_CORNER_RADIUS = 20.dp

@Composable
fun BottomNavBar(
    tabs: List<AppScreen>,
    selectedTab: AppScreen,
    onTabSelected: (AppScreen) -> Unit
) {
    val isHome = selectedTab == AppScreen.Home
    val background = if (isHome) White else BottomNavBackground
    val contentColor = if (isHome) Black else White

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = BOTTOM_NAV_CORNER_RADIUS, topEnd = BOTTOM_NAV_CORNER_RADIUS)),
        containerColor = background,
        contentColor = contentColor,
        tonalElevation = 0.dp
    ) {
        tabs.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = selectedTab == screen,
                onClick = { onTabSelected(screen) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = if (isHome) BrandBlue.copy(alpha = 0.12f) else Accent.copy(alpha = 0.18f),
                    selectedIconColor = if (isHome) BrandBlue else Accent,
                    selectedTextColor = if (isHome) BrandBlue else TextPrimary,
                    unselectedIconColor = if (isHome) HomeTextSecondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unselectedTextColor = if (isHome) HomeTextSecondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
        }
    }
}
