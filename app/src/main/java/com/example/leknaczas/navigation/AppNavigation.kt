package com.example.leknaczas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.leknaczas.ui.screens.HomeScreen
import com.example.leknaczas.ui.screens.LoginScreen
import com.example.leknaczas.ui.screens.RegisterScreen
import com.example.leknaczas.viewmodel.AuthViewModel
import com.example.leknaczas.viewmodel.LekViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    lekViewModel: LekViewModel = viewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = { navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }}
            )
        }

        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToHome = { navController.navigate("home") {
                    popUpTo("register") { inclusive = true }
                }}
            )
        }

        composable("home") {
            HomeScreen(
                lekViewModel = lekViewModel,
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}