package com.example.techshop.views.components.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.techshop.models.CartItem
import com.example.techshop.ui.theme.Primary500

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),

        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hình ảnh sản phẩm
            AsyncImage(
                model = cartItem.productImage,
                contentDescription = cartItem.productName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Thông tin sản phẩm
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = cartItem.productName,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Hiển thị giá
                if (cartItem.price > 0) {
                    Text(
                        text = String.format("%,.0f₫", cartItem.price),
                        color = Primary500,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = cartItem.getFormattedPrice(),
                        color = Primary500,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                // Điều khiển số lượng
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(0xFFF0F0F0),
                                shape = RoundedCornerShape(3.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onDecreaseQuantity,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Giảm",
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Text(
                        text = cartItem.quantity.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(0xFFF0F0F0),
                                shape = RoundedCornerShape(3.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onIncreaseQuantity,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tăng",
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Nút xóa
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(28.dp)
                    .padding(start = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = Color.Red,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}