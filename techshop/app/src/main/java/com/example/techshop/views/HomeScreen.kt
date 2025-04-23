package com.example.techshop.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.techshop.views.common.BottomNavigation

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(bottomBar = {
        BottomNavigation(navController)
    }) { paddingValues ->
        Box(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {
            Text("Home Screen")
        }
    }
}