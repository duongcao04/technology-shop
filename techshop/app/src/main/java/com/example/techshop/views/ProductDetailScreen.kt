package com.example.techshop.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.techshop.ui.theme.AccentBlack
import com.example.techshop.ui.theme.Primary500
import com.example.techshop.viewmodels.CartViewModel
import com.example.techshop.viewmodels.ProductViewModel
import com.example.techshop.views.components.products.AccessseliingPrice
import com.example.techshop.views.components.products.DiscountTag
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    viewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    productId: String,
    userId: String,
) {
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // UI state
    var quantity by remember { mutableStateOf(1) }
    var showAddToCartSuccessDialog by remember { mutableStateOf(false) }
    var isAddingToCart by remember { mutableStateOf(false) }

    // Load product details when the screen is first displayed
    LaunchedEffect(productId) {
        viewModel.loadProductDetails(productId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary500)
                }
            }

            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = error ?: "Lỗi không xác định",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadProductDetails(productId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary500)
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
            }

            selectedProduct == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Không tìm thấy sản phẩm",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                // Main content when product is loaded
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Product image with gradient overlay and topbar
                    Box {
                        // Topbar with back button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .zIndex(1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { navController.navigateUp() },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.White.copy(alpha = 0.7f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowBackIosNew,
                                        contentDescription = "Quay lại",
                                        tint = AccentBlack
                                    )
                                }

                                IconButton(
                                    onClick = { navController.navigate("cart") },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.White.copy(alpha = 0.7f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ShoppingBag,
                                        contentDescription = "Giỏ hàng",
                                        tint = AccentBlack
                                    )
                                }
                            }
                        }

                        // Product Image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                        ) {
                            selectedProduct?.imageUrl?.let {
                                Image(
                                    painter = rememberAsyncImagePainter(it),
                                    contentDescription = selectedProduct?.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } ?: run {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.LightGray)
                                )
                            }
                        }
                    }

                    // Product Info Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-20).dp)
                            .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Product Name and Sales
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = selectedProduct?.name ?: "Tên sản phẩm",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlack,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Price section
                            if ((selectedProduct?.discountPercent ?: 0) > 0) {
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = selectedProduct?.getFormattedDiscountPrice() ?: "0₫",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE53935)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = selectedProduct?.getFormattedPrice() ?: "0₫",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        textDecoration = TextDecoration.LineThrough,
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    DiscountTag((selectedProduct?.discountPercent ?: "0").toString())
                                }
                            } else {
                                Text(
                                    text = selectedProduct?.getFormattedPrice() ?: "0₫",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE53935)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Product Highlights
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ProductHighlight(
                                    icon = Icons.Rounded.LocalShipping,
                                    text = "Miễn phí vận chuyển"
                                )

                                ProductHighlight(
                                    icon = Icons.Rounded.Shield,
                                    text = "Bảo hành 12 tháng"
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Divider(color = Color(0xFFEEEFFA))

                            Spacer(modifier = Modifier.height(20.dp))

                            // Product Description
                            Text(
                                text = "Mô tả sản phẩm",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlack
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = selectedProduct?.description ?: "Không có mô tả",
                                fontSize = 14.sp,
                                lineHeight = 24.sp,
                                color = Color.DarkGray
                            )

                            // Space for the bottom bar
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }

                // Floating bottom bar
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                            clip = false
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isAddingToCart = true
                                    selectedProduct?.let { product ->
                                        cartViewModel.addProductToCart(userId, product, quantity)
                                        delay(500) // Simulate network request
                                        showAddToCartSuccessDialog = true
                                        isAddingToCart = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary500,
                                contentColor = Color.White
                            ),
                            enabled = !isAddingToCart
                        ) {
                            if (isAddingToCart) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Thêm vào giỏ hàng",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Success Dialog
        if (showAddToCartSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showAddToCartSuccessDialog = false },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White,
                title = {
                    Text(
                        "Thành công",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column {
                        Text("Đã thêm ${quantity} sản phẩm vào giỏ hàng")

                        selectedProduct?.let { product ->
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = rememberAsyncImagePainter(product.imageUrl),
                                    contentDescription = product.name,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = product.name,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = if (product.discountPercent > 0)
                                            product.getFormattedDiscountPrice()
                                        else product.getFormattedPrice(),
                                        color = Color(0xFFE53935),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showAddToCartSuccessDialog = false
                            navController.navigate("cart")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary500)
                    ) {
                        Text("Đến giỏ hàng")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showAddToCartSuccessDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = AccentBlack
                        ),
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Text("Tiếp tục mua sắm")
                    }
                }
            )
        }
    }
}

@Composable
fun ProductHighlight(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary500,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}