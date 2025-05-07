package com.example.techshop.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
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
            imageUrl = "https://cdn.tgdd.vn/Files/2022/07/24/1450033/laptop-man-hinh-full-hd-la-gi-kinh-nghiem-chon-mu-2.jpg"
        ),
        CartItem(
            id = 2,
            name = "Off-Shoulder Blouse",
            description = "Lorem ipsum dolor sit amet consectetur.",
            color = "White",
            size = "M",
            price = 17.00,
            quantity = 1,
            imageUrl = "https://cdn.tgdd.vn/Files/2020/12/26/1316150/thumb_800x501.jpg"
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // Đệm để không bị che bởi TotalPriceSection
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Giỏ Hàng",
                        style = TextStyle(fontSize = 24.sp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                AddressSection()
                CartItemsSection(fakeCartItems)
            }
        }

        // Tổng tiền cố định dưới cùng
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            TotalPriceSection(fakeCartItems)
        }
    }
}

@Composable
fun AddressSection() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Địa chỉ giao hàng:",
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "123 Đường ABC, Quận XYZ",
                style = TextStyle(fontSize = 12.sp),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { /* xử lý chỉnh sửa địa chỉ */ },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0088FF))
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Chỉnh sửa",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
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
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        cartItems.forEach { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(Color(0xFFFDFDFD), shape = RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(text = "Màu: ${item.color} | Size: ${item.size}", fontSize = 14.sp)
                        Text(text = "Số lượng: ${item.quantity}", fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .border(0.5.dp, Color.LightGray, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .border(0.5.dp, Color.LightGray)
                                    .background(Color.White)
                                    .clickable { /* giảm số lượng */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "−", fontSize = 18.sp, color = Color.Black)
                            }

                            Text(
                                text = "${item.quantity}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .border(0.5.dp, Color.LightGray)
                                    .background(Color.White)
                                    .clickable { /* tăng số lượng */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "+", fontSize = 18.sp, color = Color.Black)
                            }
                        }

                        Text(
                            text = "${item.price} VND",
                            fontSize = 16.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TotalPriceSection(cartItems: List<CartItem>) {
    val totalPrice = cartItems.sumOf { it.price * it.quantity }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Tổng tiền:",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                containerColor = Color(0xFF3388DC)
            )
        ) {
            Text(
                text = "Thanh toán",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}
