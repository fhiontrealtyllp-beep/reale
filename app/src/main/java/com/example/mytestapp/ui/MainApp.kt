package com.example.mytestapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytestapp.ui.components.BottomNavBar
import com.example.mytestapp.ui.navigation.AppScreen
import com.example.mytestapp.feature.search.presentation.SearchScreen
import com.example.mytestapp.ui.screens.AddScreen
import com.example.mytestapp.ui.screens.ProfileScreen
import com.example.mytestapp.ui.screens.SavedScreen
import com.example.mytestapp.ui.theme.MyTestAppTheme
import com.example.mytestapp.ui.viewmodel.MainViewModel

@Composable
fun MainApp(mainViewModel: MainViewModel = viewModel()) {
    val selectedTab by mainViewModel.selectedTab.collectAsState()

    MyTestAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomNavBar(
                    tabs = AppScreen.all,
                    selectedTab = selectedTab,
                    onTabSelected = mainViewModel::selectTab
                )
            }
        ) { innerPadding ->
            when (selectedTab) {
                AppScreen.Search -> SearchScreen(modifier = Modifier.padding(innerPadding))
                AppScreen.Saved -> SavedScreen(modifier = Modifier.padding(innerPadding))
                AppScreen.Add -> AddScreen(modifier = Modifier.padding(innerPadding))
                AppScreen.Profile -> ProfileScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}
