package com.example.horumvp.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.horu.ui.auth.LoginScreen
import com.example.horumvp.view.home.HomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen (
                onLoginSuccess = { navController.navigate(Screen.Home.route) }
            )
        }
        composable("home") {
            HomeScreen (
                navToLogin = { navController.navigate("login") }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
}

