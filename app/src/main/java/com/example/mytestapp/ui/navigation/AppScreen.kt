package com.example.mytestapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppScreen(
    val title: String,
    val icon: ImageVector
) {
    Search("Search", Icons.Filled.Search),
    Saved("Saved", Icons.Filled.Favorite),
    Add("Add", Icons.Filled.Add),
    Profile("Profile", Icons.Filled.Person);

    companion object {
        val all = values().toList()
    }
}
