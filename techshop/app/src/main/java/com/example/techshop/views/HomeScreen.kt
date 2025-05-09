package com.example.techshop.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.techshop.ui.theme.Primary50
import com.example.techshop.ui.theme.Primary500
import com.example.techshop.views.components.home.Banner
import com.example.techshop.views.components.home.BestsellerProductsCarousel
import com.example.techshop.views.components.home.CategoryList
import com.example.techshop.views.components.home.NewProduct

import androidx.compose.foundation.lazy.LazyColumn
import com.example.techshop.viewmodels.ProductViewModel
import com.example.techshop.views.components.home.AboutUsSection
import com.example.techshop.views.components.home.HeadingWithSearch
import com.example.techshop.views.components.home.NewsletterSection
import com.example.techshop.views.components.home.PromotionsSection

@Composable
fun HomeScreen(navController: NavController, productViewModel: ProductViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Primary500, Primary50, Color.White)
                        )
                    )
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    HeadingWithSearch(navController)
                    Banner()
                }
            }
        }

        item {
            Box(modifier = Modifier.padding(start = 16.dp)) {
                CategoryList()
            }
        }


        item {
            Box(modifier = Modifier.padding(start = 16.dp)) {
                NewProduct(
                    title = "Sản phẩm đề xuất",
                    viewModel = productViewModel,
                    navController = navController
                )
            }
        }

        item {
            Box(modifier = Modifier.padding(start = 16.dp)) {
                BestsellerProductsCarousel(
                    viewModel = productViewModel,
                    navController = navController
                )
            }
        }

        item {
            Box(modifier = Modifier.padding(start = 16.dp)) {
                PromotionsSection(navController)
            }
        }

        // Trong HomeScreen
        item {
            Box(modifier = Modifier.padding(start = 16.dp)) {
                AboutUsSection(navController)
            }
        }

        item {
            Box(modifier = Modifier.padding(start = 16.dp)) {
                NewsletterSection()
            }
        }

    }
}