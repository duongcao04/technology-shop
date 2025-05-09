
package com.example.techshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.techshop.models.Order
import com.example.techshop.models.OrderItem
import com.example.techshop.ui.theme.Primary500
import com.example.techshop.ui.theme.Primary700
import com.example.techshop.viewmodels.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    navController: NavController,
    orderViewModel: OrderViewModel,
    orderId: String
) {
    // Lấy chi tiết đơn hàng khi màn hình được hiển thị
    LaunchedEffect(key1 = orderId) {
        orderViewModel.getOrderDetails(orderId)
    }

    // Thu thập state từ ViewModel
    val orderDetails by orderViewModel.orderDetails.collectAsState()
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
            Box(modifier = Modifier.height(70.dp)) { // Giảm từ mặc định 64.dp xuống còn 48.dp
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Chi tiết đơn hàng",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Rose600
                            )
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(

                        titleContentColor = Rose800
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (orderDetails == null) {
                Text(
                    text = "Không tìm thấy thông tin đơn hàng",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                orderDetails?.let { order ->
                    OrderContent(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderContent(order: Order) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mã đơn hàng và trạng thái
        item {
            OrderHeader(order = order)
        }

        // Thông tin người nhận
        item {
            RecipientInfo(order = order)
        }

        // Danh sách sản phẩm
        item {
            Text(
                text = "Sản phẩm đã đặt",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        // Hiển thị danh sách các sản phẩm trong đơn hàng
        items(order.items.values.toList()) { orderItem ->
            OrderProductItem(orderItem = orderItem)
        }

        // Thông tin thanh toán
        item {
            PaymentSummary(order = order)
        }
    }
}

@Composable
fun OrderHeader(order: Order) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ,colors = CardDefaults.cardColors(containerColor = Rose100)

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Đơn hàng #${order.id.takeLast(6).uppercase()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Đặt ngày: ${order.getFormattedOrderDate()}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                OrderStatusBadge(status = order.status)
            }
        }
    }
}

@Composable
fun RecipientInfo(order: Order) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ,colors = CardDefaults.cardColors(containerColor = Rose100)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Thông tin nhận hàng",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tên người nhận
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Primary700
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = order.name)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Số điện thoại
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = Primary700
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = order.phone)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Địa chỉ
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Primary700
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = order.address)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phương thức thanh toán
            Text(
                text = "Phương thức thanh toán: ${order.paymentMethod}",
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun OrderProductItem(orderItem: OrderItem) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ,colors = CardDefaults.cardColors(containerColor = Rose100)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hình ảnh sản phẩm
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(orderItem.productImage)
                    .crossfade(true)
                    .build(),
                contentDescription = orderItem.productName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Thông tin sản phẩm
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = orderItem.productName,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${orderItem.getFormattedPrice()} x ${orderItem.quantity}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            // Tổng tiền sản phẩm
            Text(
                text = orderItem.getFormattedSubtotal(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PaymentSummary(order: Order) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ,colors = CardDefaults.cardColors(containerColor = Rose100)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Tổng thanh toán",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Số lượng sản phẩm
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Số lượng sản phẩm:")
                Text(text = "${order.items.size} sản phẩm")
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Số lượng sản phẩm
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Phí vận chuyển:")
                Text(text = "0đ")
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Tổng tiền
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tổng cộng:",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.getFormattedTotalPrice(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Primary500
                )
            }
        }
    }
}
