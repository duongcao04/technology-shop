package com.example.techshop.views.components.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun CheckoutItemCard(
    cartItem: CartItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hình ảnh sản phẩm
        AsyncImage(
            model = cartItem.productImage,
            contentDescription = cartItem.productName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Thông tin sản phẩm
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = cartItem.productName,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Số lượng
            Text(
                text = "SL: ${cartItem.quantity}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // Giá
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = cartItem.getFormattedPrice(),
                color = Primary500,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            // Thành tiền (giá × số lượng)
            Text(
                text = String.format("%,.0f₫", cartItem.price * cartItem.quantity),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}