package com.realeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.realeapp.core.theme.ThemeMode
import com.realeapp.core.theme.ThemePreferences
import com.realeapp.ui.navigation.AppScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    themePreferences: ThemePreferences
) : ViewModel() {
    private val _selectedTab = MutableStateFlow<AppScreen>(AppScreen.Home)
    val selectedTab: StateFlow<AppScreen> = _selectedTab.asStateFlow()

    val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode

    fun selectTab(screen: AppScreen) {
        _selectedTab.value = screen
    }
}
