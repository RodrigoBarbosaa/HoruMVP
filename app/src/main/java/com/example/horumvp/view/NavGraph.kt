package com.example.horumvp.view

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.horu.ui.auth.LoginScreen
import com.example.horumvp.view.home.HomeScreen
import com.example.horumvp.view.RegisterPropertyScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen (
                onLoginSuccess = { navController.navigate(Screen.Home.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        composable("home") {
            HomeScreen (
                navToLogin = { navController.navigate("login") },
                navToRegisterProperty = {navController.navigate("registerProperty")}
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Screen.Home.route) }
            )
        }
        composable("registerProperty") {
            RegisterPropertyScreen(
                onRegisterPropertySuccess = { navController.navigate(Screen.Home.route) },
                onErrorMessage = {}
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Register : Screen("register")
    object RegisterProperty : Screen("registerProperty")
}

