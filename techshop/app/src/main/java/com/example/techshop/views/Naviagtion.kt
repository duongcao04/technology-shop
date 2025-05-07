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
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.techshop.viewmodels.ProductViewModel
import com.example.techshop.viewmodels.ProfileViewModel
import com.example.techshop.views.common.BottomNavigation

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    profileViewModel: ProfileViewModel
) {
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
            startDestination = "product",
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
                // Make sure we're passing the correct parameters to ProductsScreen
                ProductsScreen(productViewModel, navController)
            }
            composable(
                "productDetail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(navController, productViewModel, productId, {})
            }
            composable("me") {
                ProfileScreen(navController, profileViewModel)
            }
        }
    }
}

// Hàm kiểm tra xem có nên hiển thị BottomNavigation hay không
@Composable
private fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return when (currentRoute) {
        "home", "product", "me" -> true
        else -> false
    }
}