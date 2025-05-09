package com.example.techshop.views

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.techshop.ui.screens.CustomerInfoScreen
import com.example.techshop.ui.screens.OrderDetailsScreen
import com.example.techshop.ui.screens.OrderHistoryScreen
import com.example.techshop.ui.screens.ProfileScreen
import com.example.techshop.viewmodels.AuthViewModel
import com.example.techshop.viewmodels.CartViewModel
import com.example.techshop.viewmodels.OrderViewModel
import com.example.techshop.viewmodels.ProductViewModel
import com.example.techshop.viewmodels.ProfileViewModel
import com.example.techshop.views.common.BottomNavigation
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    profileViewModel: ProfileViewModel,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel
) {
    val navController = rememberAnimatedNavController()

    Scaffold(
        bottomBar = {
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            if (shouldShowBottomBar(currentRoute)) {
                authViewModel.currentUserId?.let {
                    BottomNavigation(
                        navController = navController,
                        cartViewModel = cartViewModel,
                        userId = it
                    )
                }
            }
        }
    ) { paddingValues ->
        AnimatedNavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(paddingValues)
        ) {
            // Splash screen với hiệu ứng fade in/out
            composable(
                route = "splash",
                enterTransition = {
                    fadeIn(animationSpec = tween(500))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(500))
                }
            ) {
                Splash(navController)
            }

            // Login screen với slide up animation
            composable(
                route = "login",
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 500, easing = EaseOutQuint)
                    )
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { fullHeight -> -fullHeight },
                        animationSpec = tween(durationMillis = 500, easing = EaseInQuint)
                    ) + fadeOut(animationSpec = tween(250))
                }
            ) {
                LoginScreen(authViewModel, navController)
            }

            // Home Screen với hiệu ứng scale + fade
            composable(
                route = "home",
                enterTransition = {
                    scaleIn(
                        initialScale = 0.9f,
                        animationSpec = tween(300, easing = EaseOutCubic)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    scaleOut(
                        targetScale = 1.1f,
                        animationSpec = tween(300, easing = EaseInCubic)
                    ) + fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    scaleIn(
                        initialScale = 1.1f,
                        animationSpec = tween(300, easing = EaseOutCubic)
                    ) + fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    scaleOut(
                        targetScale = 0.9f,
                        animationSpec = tween(300, easing = EaseInCubic)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                HomeScreen(navController, productViewModel)
            }

            // Products screen với slide transition
            composable(
                route = "product",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(durationMillis = 400, easing = EaseOutQuad)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 400, easing = EaseInQuad)
                    ) + fadeOut(animationSpec = tween(200))
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 400, easing = EaseOutQuad)
                    ) + fadeIn(animationSpec = tween(400))
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(durationMillis = 400, easing = EaseInQuad)
                    ) + fadeOut(animationSpec = tween(200))
                }
            ) {
                ProductsScreen(productViewModel, navController)
            }

            // Cart Screen
            composable(
                route = "cart",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(durationMillis = 400, easing = EaseOutQuad)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 400, easing = EaseInQuad)
                    ) + fadeOut(animationSpec = tween(200))
                }
            ) {
                val userId = authViewModel.currentUserId
                if (userId != null) {
                    CartScreen(navController, cartViewModel, userId)
                }
            }

            // Product Detail với shared element transition feeling
            composable(
                route = "productDetail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType }),
                enterTransition = {
                    scaleIn(
                        initialScale = 0.8f,
                        animationSpec = tween(350, easing = EaseOutCubic)
                    ) +
                            fadeIn(animationSpec = tween(350))
                },
                exitTransition = {
                    scaleOut(targetScale = 0.8f, animationSpec = tween(350, easing = EaseInCubic)) +
                            fadeOut(animationSpec = tween(350))
                }
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                val userId = authViewModel.currentUserId
                if (userId != null) {
                    ProductDetailScreen(
                        navController,
                        productViewModel,
                        cartViewModel,
                        productId,
                        userId
                    )
                }
            }

            // Profile screen
            composable(
                route = "me",
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { fullHeight -> -fullHeight / 3 },
                        animationSpec = tween(durationMillis = 400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { fullHeight -> -fullHeight / 3 },
                        animationSpec = tween(durationMillis = 400)
                    ) + fadeOut(animationSpec = tween(200))
                }
            ) {
                ProfileScreen(navController, profileViewModel, authViewModel)
            }

            // Customer Info Screen
            composable(
                route = "infoUser",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(durationMillis = 400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(durationMillis = 400)
                    ) + fadeOut(animationSpec = tween(200))
                }
            ) {
                CustomerInfoScreen(navController, profileViewModel)
            }

            // Order Success Screen với celebratory animation
            composable(
                route = "success",
                enterTransition = {
                    scaleIn(
                        initialScale = 0.2f,
                        animationSpec = tween(500, easing = EaseOutBack)
                    ) + fadeIn(animationSpec = tween(500))
                }
            ) {
                OrderSuccessScreen(navController)
            }

            // Order History Screen
            composable(
                route = "order_history/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType }),
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 400)
                    ) + fadeOut(animationSpec = tween(200))
                }
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                OrderHistoryScreen(
                    navController = navController,
                    orderViewModel = orderViewModel,
                    userId = userId
                )
            }

            // Order Details Screen
            composable(
                route = "order_details/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(durationMillis = 400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(durationMillis = 400)
                    ) + fadeOut(animationSpec = tween(200))
                }
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderDetailsScreen(
                    navController = navController,
                    orderViewModel = orderViewModel,
                    orderId = orderId
                )
            }

            // Checkout Screen
            composable(
                route = "checkout",
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 400, easing = EaseOutQuad)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 400, easing = EaseInQuad)
                    ) + fadeOut(animationSpec = tween(200))
                }
            ) {
                authViewModel.currentUserId?.let { userId ->
                    CheckoutScreen(
                        navController = navController,
                        cartViewModel = cartViewModel,
                        orderViewModel = orderViewModel,
                        userId = userId
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
        "home", "product", "cart", "me" -> true
        else -> false
    }
}