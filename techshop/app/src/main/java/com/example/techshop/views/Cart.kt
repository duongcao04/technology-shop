package com.example.techshop.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import coil.compose.AsyncImage



data class CartItem(
    val id: Int,
    val name: String,
    val description: String,
    val color: String,
    val size: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String // Nếu chưa có hình thật, dùng placeholder
)

@Composable
fun CartScreen(navController: NavController) {
    val fakeCartItems = listOf(
        CartItem(
            id = 1,
            name = "Floral Print Top",
            description = "Lorem ipsum dolor sit amet consectetur.",
            color = "Pink",
            size = "M",
            price = 17.00,
            quantity = 1,
            imageUrl = "https://cdn.tgdd.vn/Files/2022/07/24/1450033/laptop-man-hinh-full-hd-la-gi-kinh-nghiem-chon-mu-2.jpg" // link ảnh mẫu
        ),
        CartItem(
            id = 2,
            name = "Off-Shoulder Blouse",
            description = "Lorem ipsum dolor sit amet consectetur.",
            color = "White",
            size = "M",
            price = 17.00,
            quantity = 1,
            imageUrl = "https://cdn.tgdd.vn/Files/2020/12/26/1316150/thumb_800x501.jpg" // link ảnh mẫu khác
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Cart",
            style = TextStyle(fontSize = 24.sp),
            modifier = Modifier.fillMaxWidth()
        )

        AddressSection()
        CartItemsSection(fakeCartItems)
        TotalPriceSection(fakeCartItems)
    }
}

@Composable
fun AddressSection() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Địa chỉ giao hàng:",
            style = TextStyle(fontSize = 24.sp),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "123 Đường ABC, Quận XYZ",
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { /* xử lý chỉnh sửa địa chỉ */ },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0088FF))
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Chỉnh sửa",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun CartItemsSection(cartItems: List<CartItem>) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Sản phẩm trong giỏ hàng:",
            style = TextStyle(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth()
        )
        cartItems.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ảnh sản phẩm
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Thông tin sản phẩm
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.name, style = TextStyle(fontSize = 16.sp))
                    Text(text = "Màu: ${item.color} | Size: ${item.size}", fontSize = 14.sp)
                    Text(text = "Số lượng: ${item.quantity}", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.weight(1f)
                    .width(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Nhóm nút tăng/giảm số lượng
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Nút giảm
                        IconButton(
                            onClick = { /* TODO: Giảm số lượng */ },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(6.dp))
                        ) {
                            Text(text = "−", fontSize = 20.sp, color = Color.Black)
                        }

                        // Số lượng
                        Text(
                            text = "${item.quantity}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // Nút tăng
                        IconButton(
                            onClick = { /* TODO: Tăng số lượng */ },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(6.dp))
                        ) {
                            Text(text = "+", fontSize = 20.sp, color = Color.Black)
                        }
                    }

                    // Giá tiền
                    Text(
                        text = "${item.price} VND",
                        fontSize = 18.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(top = 15.dp, start = 10.dp)
                    )
                }

            }
        }
    }
}

@Composable
fun TotalPriceSection(cartItems: List<CartItem>) {
    val totalPrice = cartItems.sumOf { it.price }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Tổng tiền:",
                style = TextStyle(fontSize = 20.sp)
            )
            Text(
                text = "$totalPrice VND",
                style = TextStyle(fontSize = 18.sp)
            )
        }

        Button(
            onClick = { /* xử lý thanh toán */ },
            modifier = Modifier
                .width(140.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3388DC) // Màu nền nút
            )
        ) {
            Text(
                text = "Thanh toán",
                fontSize = 16.sp,
                color = Color.White // Màu chữ
            )
        }
    }
}


