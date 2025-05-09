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
import com.example.techshop.ui.screens.CustomerInfoScreen
import com.example.techshop.ui.screens.ProfileScreen
import com.example.techshop.viewmodels.CartViewModel
import com.example.techshop.viewmodels.OrderViewModel
import com.example.techshop.viewmodels.ProductViewModel
import com.example.techshop.viewmodels.ProfileViewModel
import com.example.techshop.views.common.BottomNavigation

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(authViewModel: AuthViewModel, productViewModel: ProductViewModel, profileViewModel: ProfileViewModel, cartViewModel: CartViewModel,orderViewModel: OrderViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            // Chỉ hiển thị BottomNavigation khi ở các màn hình cần thiết
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            if (shouldShowBottomBar(currentRoute)) {
                authViewModel.currentUserId?.let { BottomNavigation(navController = navController, cartViewModel = cartViewModel, userId = it) }
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
                HomeScreen(navController, productViewModel)
            }
            composable("product") {
                // Make sure we're passing the correct parameters to ProductsScreen
                ProductsScreen(productViewModel, navController)
            }
            composable("cart") {
                val userId = authViewModel.currentUserId
                if (userId != null) {
                    CartScreen(navController,cartViewModel,userId)
                }

            }
            composable(
                "productDetail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                val userId = authViewModel.currentUserId
                if (userId != null) {
                    ProductDetailScreen(navController, productViewModel,cartViewModel, productId,userId)
                }
            }
            composable("me") {
                ProfileScreen(navController, profileViewModel, authViewModel)
            }
            composable("infoUser") {
                CustomerInfoScreen(navController, profileViewModel)
            }
            composable("success") {
                OrderSuccessScreen(navController)
            }

            composable("checkout") {
                authViewModel.currentUserId?.let { it1 ->
                    CheckoutScreen(navController, cartViewModel, orderViewModel,
                        it1
                    )
                }
            }
        }
    }
}

// Hàm kiểm tra xem có nên hiển thị BottomNavigation hay không
@Composable
private fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return when (currentRoute) {
        "home", "product","cart" ,"me" -> true
        else -> false
    }
}