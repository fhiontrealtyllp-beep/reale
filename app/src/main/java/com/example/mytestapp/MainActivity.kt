package com.example.mytestapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mytestapp.feature.search.di.SearchModule
import com.example.mytestapp.ui.MainApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SearchModule.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            MainApp()
        }
    }
}