package com.example.leknaczas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.leknaczas.navigation.AppNavigation
import com.example.leknaczas.ui.theme.LekNaCzasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LekNaCzasTheme {
                AppNavigation()
            }
        }
    }
}