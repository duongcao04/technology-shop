package com.example.techshop.views.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.techshop.models.Product
import com.example.techshop.viewmodels.ProductViewModel
import com.example.techshop.views.components.products.ProductCard

@Composable
fun NewProduct(
    title: String = "Sản phẩm đề xuất",
    viewModel: ProductViewModel,
    navController: NavController,
    onSeeAllClick: () -> Unit = {}
) {
    // Collect state from ViewModel
    val randomProducts by viewModel.randomProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Load random products when composable is first displayed
    LaunchedEffect(key1 = true) {
        viewModel.loadRandomProducts(5)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        // Title row with See All button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onSeeAllClick() }
            ) {
                Text(
                    text = "Xem tất cả", 
                    color = Color(0xFF4285F4),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { "product" }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Show loading indicator if loading
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                CircularProgressIndicator()
            }
        }
        // Show error message if there is an error
        else if (error != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Text(
                    text = error ?: "Đã xảy ra lỗi",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        // Show products if available
        else if (randomProducts.isNotEmpty()) {
            // Product cards
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(randomProducts) { product ->
                    // Bọc ProductCard trong một Box có kích thước cố định và sử dụng clipToBounds
                    Box(
                        modifier = Modifier
                            .width(170.dp) // Cố định chiều rộng cho mỗi item
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        // Sử dụng ProductCard nguyên bản, nhưng trong một không gian giới hạn
                        ProductCardWithCustomSize(
                            product = product,
                            onClick = {
                                navController.navigate("product_detail/${product.id}")
                            }
                        )
                    }
                }
            }
        }
        // Show empty state if no products
        else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Text(
                    text = "Không có sản phẩm để hiển thị",
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ProductCardWithCustomSize(product: Product, onClick: () -> Unit) {
    // Sử dụng một wrapper cho ProductCard
    ProductCard(
        product = product.copy(
            // Tạo một bản sao của product để sử dụng trong NewProduct
            // Điều này giúp khi bạn muốn thay đổi thuộc tính của sản phẩm đặc biệt cho NewProduct
            // nhưng không ảnh hưởng đến ProductCard trong ProductGrid
            // Nếu không cần thay đổi gì, không cần phải sử dụng copy() ở đây
        ),
        onClick = onClick
    )
}