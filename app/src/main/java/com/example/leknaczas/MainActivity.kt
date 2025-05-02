package com.example.leknaczas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.leknaczas.navigation.AppNavigation
import com.example.leknaczas.ui.theme.LeknaczasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LeknaczasTheme {
                AppNavigation()
            }
        }
    }
}