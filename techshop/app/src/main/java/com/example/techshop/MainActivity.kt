package com.example.techshop

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.techshop.repositories.OrderRepository
import com.example.techshop.repositorys.AuthRepository
import com.example.techshop.repositorys.CartRepository
import com.example.techshop.repositorys.ProductRepository
import com.example.techshop.ui.theme.TechshopTheme
import com.example.techshop.viewmodels.AuthViewModel
import com.example.techshop.viewmodels.CartViewModel
import com.example.techshop.viewmodels.OrderViewModel
import com.example.techshop.viewmodels.ProductViewModel
import com.example.techshop.viewmodels.ProfileViewModel
import com.example.techshop.views.AppNavigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Khởi tạo FirebaseAuth, GoogleSignInClient, Database
        val auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance().reference

        // Tạo repository và viewmodel
        val authRepository = AuthRepository(auth, database)
        val viewModel = AuthViewModel(authRepository,CartViewModel())

        // Khởi tạo ProductRepository và ProductViewModel
        val productRepository = ProductRepository(database)
        val productViewModel = ProductViewModel(productRepository)

        // Khởi tạo ProductRepository và ProductViewModel
        val profileRepository = AuthRepository(auth, database)

        val profileViewModel = ProfileViewModel(profileRepository)

// Khởi tạo ProductRepository và ProductViewModel
        val cartRepository = CartRepository()

        val cartViewmodel = CartViewModel()

        val orderRepository=OrderRepository()
        val orderViewModel=OrderViewModel()

        setContent {
            TechshopTheme {
                AppNavigation(viewModel, productViewModel, profileViewModel,cartViewmodel,orderViewModel)
            }
        }
    }
}