package com.example.techshop.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.techshop.ui.screens.Rose600
import com.example.techshop.ui.screens.Rose800
import com.example.techshop.ui.theme.Primary500
import com.example.techshop.viewmodels.CartViewModel
import com.example.techshop.views.components.cart.CartItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    userId: String
) {
    val cart by cartViewModel.cart.collectAsState()
    val isLoading by cartViewModel.isLoading.collectAsState()
    val errorMessage by cartViewModel.errorMessage.collectAsState()

    // Theo dõi giỏ hàng khi màn hình được hiển thị
    LaunchedEffect(userId) {
        cartViewModel.initCart(userId)
    }

    // Hiển thị dialog xác nhận xóa
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<String?>(null) }
    var showClearCartDialog by remember { mutableStateOf(false) }

    // Hiển thị snackbar thông báo
    val snackbarHostState = remember { SnackbarHostState() }

    // Xử lý hiển thị thông báo lỗi nếu có
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(message = it)
            cartViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.height(70.dp)) {
                SmallTopAppBar(
                    title = {
                        Text(
                            text = "Giỏ hàng",
                            modifier = Modifier.fillMaxWidth(), // Căn rộng full chiều ngang
                            textAlign = TextAlign.Center,       // Căn giữa text
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Rose800
                    )
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (cart?.items?.isNotEmpty() == true) {
                CartBottomBar(
                    totalAmount = cart?.getFormattedTotalAmount() ?: "0₫",
                    itemCount = cart?.itemCount ?: 0,
                    onCheckout = {
                        // Xử lý chuyển đến màn hình thanh toán
                        navController.navigate("checkout")
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F8F8))
        ) {
            if (isLoading && cart == null) {
                // Hiển thị trạng thái đang tải
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Primary500
                )
            } else if (cart?.items?.isEmpty() == true) {
                // Hiển thị giỏ hàng trống
                EmptyCartView(
                    modifier = Modifier.align(Alignment.Center),
                    onBrowseProducts = {
                        navController.navigate("product") {
                            popUpTo("home")
                        }
                    }
                )
            } else {
                // Hiển thị danh sách sản phẩm trong giỏ hàng
                Column(modifier = Modifier.fillMaxSize()) {
                    // Nút "xóa tất cả" đặt ở đầu danh sách và căn phải
                    if (cart?.items?.isNotEmpty() == true) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()

                        ) {
                            TextButton(
                                onClick = { showClearCartDialog = true },

                                modifier = Modifier.align(Alignment.CenterEnd)  // Căn phải
                            ) {
                                Text(
                                    text = "Xóa tất cả",
                                    color = Color.Red,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }


                    // LazyColumn nằm trong Column
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(cart?.items?.values?.toList() ?: emptyList()) { cartItem ->
                            CartItemCard(
                                cartItem = cartItem,
                                onIncreaseQuantity = {
                                    cartViewModel.increaseItemQuantity(userId, cartItem.productId)
                                },
                                onDecreaseQuantity = {
                                    cartViewModel.decreaseItemQuantity(userId, cartItem.productId)
                                },
                                onDelete = {
                                    itemToDelete = cartItem.productId
                                    showDeleteDialog = true
                                }
                            )
                        }

                        // Thêm khoảng trống phía dưới để tránh bị che bởi bottom bar
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }

    // Dialog xác nhận xóa sản phẩm
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDelete?.let { productId ->
                            cartViewModel.removeItemFromCart(userId, productId)
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Xóa", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Dialog xác nhận xóa toàn bộ giỏ hàng
    if (showClearCartDialog) {
        AlertDialog(
            onDismissRequest = { showClearCartDialog = false },
            title = { Text("Xác nhận xóa giỏ hàng") },
            text = { Text("Bạn có chắc chắn muốn xóa toàn bộ giỏ hàng không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        cartViewModel.clearCart(userId)
                        showClearCartDialog = false
                    }
                ) {
                    Text("Xóa toàn bộ", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCartDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun CartBottomBar(
    totalAmount: String,
    itemCount: Int,
    onCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Tổng cộng:",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = totalAmount,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary500
                )
                Text(
                    text = "$itemCount sản phẩm",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = onCheckout,
                colors = ButtonDefaults.buttonColors(containerColor = Primary500),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Thanh toán",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EmptyCartView(
    modifier: Modifier = Modifier,
    onBrowseProducts: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Giỏ hàng trống",
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Giỏ hàng của bạn đang trống",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hãy thêm sản phẩm vào giỏ hàng để tiếp tục",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBrowseProducts,
            colors = ButtonDefaults.buttonColors(containerColor = Primary500),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Tiếp tục mua sắm",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}