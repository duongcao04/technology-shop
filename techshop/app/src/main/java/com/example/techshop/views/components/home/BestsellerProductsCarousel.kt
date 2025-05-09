package com.example.techshop.views.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.techshop.ui.theme.Primary500
import com.example.techshop.viewmodels.ProductViewModel

@Composable
fun BestsellerProductsCarousel(
    viewModel: ProductViewModel,
    navController: NavController
) {
    val bestsellerProducts by viewModel.bestsellerProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sản phẩm bán chạy",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )

            TextButton(onClick = { navController.navigate("product") }) {
                Text(
                    text = "Xem tất cả",
                    color = Primary500,
                    style = MaterialTheme.typography.button.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary500)
            }
        } else if (error != null) {
            // Hiển thị lỗi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không thể tải sản phẩm bán chạy: ${error ?: "Lỗi không xác định"}",
                    style = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.error
                    ),
                    textAlign = TextAlign.Center
                )
            }
        } else if (bestsellerProducts.isEmpty()) {
            // Hiển thị khi không có sản phẩm bán chạy
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có sản phẩm bán chạy",
                    style = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
        } else {
            // Hiển thị sản phẩm bán chạy
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(bestsellerProducts) { product ->
                    // Đối với sản phẩm bán chạy, hiển thị ProductCard thông thường với tag "Bán chạy"
                    ProductCard(
                        product = product,
                        onClick = {
                            navController.navigate("productDetail/${product.id}")
                        },
                        isBestseller = true // Hiển thị tag "Bán chạy"
                    )
                }
            }
        }
    }
}