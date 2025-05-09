package com.example.techshop.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.techshop.models.CartItem
import com.example.techshop.models.Product
import com.example.techshop.ui.theme.Primary500
import com.example.techshop.viewmodels.CartViewModel
import com.example.techshop.viewmodels.ProductViewModel
import com.example.techshop.views.components.cart.Bottombar
import com.example.techshop.views.components.cart.Topbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    productViewModel: ProductViewModel,
    userId: String
) {
    // State từ ViewModel
    val cart by cartViewModel.cart.collectAsState()
    val products by productViewModel.products.collectAsState()
    val isLoading by cartViewModel.isLoading.collectAsState()
    val error by cartViewModel.error.collectAsState()

    Log.d("CART", cart.toString())

    // Lấy dữ liệu giỏ hàng và sản phẩm khi màn hình được tạo
    LaunchedEffect(key1 = Unit) {
        cartViewModel.loadUserCart(userId)
        productViewModel.loadAllProducts()
    }

    // Danh sách sản phẩm trong giỏ hàng với thông tin chi tiết
    val cartItemsWithDetails = remember(cart, products) {
        val itemDetails = mutableListOf<CartItemWithDetails>()

        cart?.items?.forEach { cartItem ->
            val product = products.find { it.id == cartItem.productId }
            if (product != null) {
                itemDetails.add(
                    CartItemWithDetails(
                        cartItem = cartItem,
                        product = product
                    )
                )
            }
        }

        itemDetails
    }

    // Tính tổng tiền
    val totalAmount = remember(cartItemsWithDetails) {
        cartItemsWithDetails.sumOf { it.product.price * it.cartItem.quantity }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Lỗi: $error", color = Color.Red)
                }
            }

            cartItemsWithDetails.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Giỏ hàng trống",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("home") },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary500)
                    ) {
                        Text("Tiếp tục mua sắm")
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp) // Đệm để không bị che bởi Bottombar
                ) {
                    Topbar()

                    Text(
                        text = "Sản phẩm trong giỏ hàng:",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(cartItemsWithDetails) { item ->
                            CartItemCard(
                                cartItemWithDetails = item,
                                onIncreaseQuantity = {
                                    cartViewModel.updateCartItemQuantity(
                                        userId,
                                        item.cartItem.productId,
                                        item.cartItem.quantity + 1
                                    )
                                },
                                onDecreaseQuantity = {
                                    if (item.cartItem.quantity > 1) {
                                        cartViewModel.updateCartItemQuantity(
                                            userId,
                                            item.cartItem.productId,
                                            item.cartItem.quantity - 1
                                        )
                                    }
                                },
                                onRemove = {
                                    cartViewModel.removeFromCart(userId, item.cartItem.productId)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                Box(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Bottombar(totalAmount.toLong())
                }
            }
        }
    }
}

// Lớp giữ dữ liệu sản phẩm trong giỏ hàng với thông tin chi tiết
data class CartItemWithDetails(
    val cartItem: CartItem,
    val product: Product
)

@Composable
fun CartItemCard(
    cartItemWithDetails: CartItemWithDetails,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onRemove: () -> Unit
) {
    val product = cartItemWithDetails.product
    val cartItem = cartItemWithDetails.cartItem

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hình ảnh sản phẩm
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Thông tin sản phẩm
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${product.price.toInt()} VND",
                    style = TextStyle(fontWeight = FontWeight.SemiBold, color = Primary500)
                )

                // Điều khiển số lượng
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút giảm số lượng
                    IconButton(
                        onClick = onDecreaseQuantity,
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.dp, Color.LightGray, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Giảm",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Số lượng
                    Text(
                        text = cartItem.quantity.toString(),
                        modifier = Modifier.padding(horizontal = 12.dp),
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )

                    // Nút tăng số lượng
                    IconButton(
                        onClick = onIncreaseQuantity,
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.dp, Color.LightGray, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tăng",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Nút xóa sản phẩm
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}