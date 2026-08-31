package com.example.mytestapp.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mytestapp.ui.navigation.AppScreen

@Composable
fun BottomNavBar(
    tabs: List<AppScreen>,
    selectedTab: AppScreen,
    onTabSelected: (AppScreen) -> Unit
) {
    NavigationBar {
        tabs.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = selectedTab == screen,
                onClick = { onTabSelected(screen) }
            )
        }
    }
}
