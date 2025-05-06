package com.example.techshop.views

import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.techshop.viewmodels.AuthViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.techshop.views.common.BottomNavigation
import com.example.techshop.screens.login.CartScreen


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            // Chỉ hiển thị BottomNavigation khi ở các màn hình cần thiết
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            if (shouldShowBottomBar(currentRoute)) {
                BottomNavigation(navController = navController)
            }
        }
    ) { paddingValues ->
        AnimatedNavHost(
            navController = navController,
            startDestination = "cart",
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
                slideInHorizontally { width -> width } + fadeIn()

            },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            composable("splash") {
                Splash(navController)
            }
            composable("login") {
                LoginScreen(authViewModel, navController)
            }
            composable("home") {
                HomeScreen(navController)
            }
            composable("product") {
                ProductsScreen(navController)
            }
            composable("me") {
                ProfileScreen(navController)
            }
            composable("cart") {
                CartScreen(navController)
            }
        }
    }
}

// Hàm kiểm tra xem có nên hiển thị BottomNavigation hay không
@Composable
private fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return when (currentRoute) {
        "home", "product", "me"-> true
        else -> false
    }
}
