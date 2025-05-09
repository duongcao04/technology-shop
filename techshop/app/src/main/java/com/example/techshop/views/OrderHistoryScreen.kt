

package com.example.techshop.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.techshop.models.Order
import com.example.techshop.viewmodels.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    orderViewModel: OrderViewModel,
    userId: String
) {
    // Lấy danh sách đơn hàng khi màn hình được hiển thị
    LaunchedEffect(key1 = userId) {
        orderViewModel.getUserOrders(userId)
    }

    // Thu thập state từ ViewModel
    val orders by orderViewModel.orders.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()
    val errorMessage by orderViewModel.errorMessage.collectAsState()

    // Xử lý thông báo lỗi
    LaunchedEffect(key1 = errorMessage) {
        errorMessage?.let {
            // Hiển thị thông báo lỗi và sau đó xóa
            orderViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử đơn hàng") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (orders.isEmpty()) {
                EmptyOrdersMessage(modifier = Modifier.align(Alignment.Center))
            } else {
                OrdersList(
                    orders = orders,
                    onOrderClick = { orderId ->
                        // Điều hướng đến màn hình chi tiết đơn hàng
                        navController.navigate("order_details/$orderId")
                    }
                )
            }
        }
    }
}

@Composable
fun OrdersList(
    orders: List<Order>,
    onOrderClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(orders) { order ->
            OrderItem(order = order, onClick = { onOrderClick(order.id) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderItem(
    order: Order,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Mã đơn hàng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = " #${order.id.takeLast(6).uppercase()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ngày đặt hàng
            Text(
                text = "Ngày đặt: ${order.getFormattedOrderDate()}",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sản phẩm
            val itemCount = order.items.size
            Text(
                text = "$itemCount sản phẩm",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tổng tiền
            Text(
                text = "Tổng tiền: ${order.getFormattedTotalPrice()}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun OrderStatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status) {
        Order.STATUS_PENDING -> Color(0xFFFFF9C4) to Color(0xFF8B6914) // Vàng nhạt
        Order.STATUS_DELIVERED -> Color(0xFFD7FFD7) to Color(0xFF006400) // Xanh lá nhạt
        else -> Color(0xFFE0E0E0) to Color(0xFF505050) // Xám nhạt
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyOrdersMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bạn chưa có đơn hàng nào",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Các đơn hàng của bạn sẽ hiển thị ở đây",
            color = Color.Gray
        )
    }
}
