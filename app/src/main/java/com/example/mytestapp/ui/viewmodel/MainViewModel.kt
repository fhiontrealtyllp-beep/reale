package com.example.mytestapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mytestapp.ui.navigation.AppScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _selectedTab = MutableStateFlow<AppScreen>(AppScreen.Search)
    val selectedTab: StateFlow<AppScreen> = _selectedTab.asStateFlow()

    fun selectTab(screen: AppScreen) {
        _selectedTab.value = screen
    }
}
