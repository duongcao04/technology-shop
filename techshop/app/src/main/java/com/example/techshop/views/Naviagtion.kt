package com.example.techshop.views

import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.techshop.viewmodels.ProductViewModel
import com.example.techshop.viewmodels.ProfileViewModel
import com.example.techshop.views.common.BottomNavigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    profileViewModel: ProfileViewModel,
    cartViewModel: CartViewModel
) {
    val navController = rememberNavController()

    // Lấy userId hiện tại từ Firebase Authentication
    var userId by remember { mutableStateOf("") }

    // Lắng nghe trạng thái đăng nhập và cập nhật userId
    LaunchedEffect(Unit) {
        val firebaseAuth = FirebaseAuth.getInstance()

        // Lấy userId hiện tại (nếu đã đăng nhập)
        firebaseAuth.currentUser?.let {
            userId = it.uid
        }

        // Lắng nghe thay đổi trạng thái đăng nhập
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            userId = user?.uid ?: ""
        }

        // Đăng ký lắng nghe
        firebaseAuth.addAuthStateListener(authStateListener)
    }

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
                ProductsScreen(productViewModel, navController)
            }
            composable("cart") {
                // Sử dụng userId hiện tại từ Firebase
                if (userId.isNotEmpty()) {
                    // Nếu đã đăng nhập, hiển thị giỏ hàng của người dùng
                    CartScreen(navController, cartViewModel, productViewModel, userId)
                } else {
                    // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
                    LaunchedEffect(Unit) {
                        navController.navigate("login")
                    }
                }
            }
            composable(
                "productDetail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(navController, productViewModel, productId) {
                    // Callback khi thêm vào giỏ hàng, kiểm tra đăng nhập
                    if (userId.isNotEmpty()) {
                        // Đã đăng nhập, thêm vào giỏ hàng
                        cartViewModel.addToCart(userId, productId, 1)
                    } else {
                        // Chưa đăng nhập, chuyển hướng đến trang đăng nhập
                        navController.navigate("login")
                    }
                }
            }
            composable("me") {
                ProfileScreen(navController, profileViewModel)
            }
            composable("infoUser") {
                CustomerInfoScreen(navController, profileViewModel)
            }
        }
    }
}

// Hàm kiểm tra xem có nên hiển thị BottomNavigation hay không
private fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return when (currentRoute) {
        "home", "product", "cart", "me" -> true
        else -> false
    }
}