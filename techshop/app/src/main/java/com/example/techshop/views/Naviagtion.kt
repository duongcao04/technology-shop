package com.example.techshop.views

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.techshop.viewmodels.AuthViewModel

@Composable
fun AppNavigation( authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {Splash(navController) }
        composable("login") { LoginScreen(authViewModel,navController) }
        composable("home") {HomeScreen(navController)
        }
    }
}
