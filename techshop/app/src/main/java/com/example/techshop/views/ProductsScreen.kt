package com.example.techshop.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.techshop.models.Product
import com.example.techshop.ui.theme.Primary100
import com.example.techshop.viewmodels.ProductViewModel
import com.example.techshop.views.components.products.Topbar
import com.example.techshop.views.components.products.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: ProductViewModel, navController: NavController) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Load products when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadAllProducts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary100),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Topbar(navController)

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                error != null -> {
                    ErrorMessage(message = error!!)
                }

                products.isEmpty() -> {
                    Text(
                        text = "Không có sản phẩm nào",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    ProductGrid(
                        products = products,
                        onProductClick = { productId ->
                            // Navigate to product detail screen
                            navController.navigate("productDetail/$productId")
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun ProductGrid(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) // Bo góc Box
            .background(Color.White)
            .padding(top = 10.dp),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp
                    )
                ) // Bo góc LazyVerticalGrid
        ) {
            items(products) { product ->
                ProductCard(product = product, onClick = { onProductClick(product.id) })
            }
        }
    }

}


@Composable
fun ErrorMessage(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            tint = Color.Red,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            color = Color.Red,
            textAlign = TextAlign.Center
        )
    }
}