package com.example.techshop.views

import ProductDetailScreen
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
import com.example.techshop.models.Product
import com.example.techshop.views.common.BottomNavigation

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
            startDestination = "splash",
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
            composable(
                "detail"
            ) {
                val product = Product(
                    id = "-Nd7xKz9f2hTt1aBcD12",
                    name = "Laptop Lenovo Legion S7",
                    description = "Laptop gaming cao cấp với hiệu năng mạnh mẽ.",
                    price = 32000000,
                    imageUrl = "https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-plus_1_.png",
                    discountPercent = 10
                    // createdAt và updatedAt sẽ tự động dùng giá trị mặc định từ data class
                )
                ProductDetailScreen(navController, product, { })
            }
            composable("me") {
                ProfileScreen(navController)
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
