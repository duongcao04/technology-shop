package com.example.techshop.views.components.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techshop.models.CartItem

@Composable
fun CartCard(cartItem: CartItem) {
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
//            AsyncImage(
//                model = item.imageUrl,
//                contentDescription = item.name,
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(RoundedCornerShape(10.dp))
//            )

            Spacer(modifier = Modifier.width(16.dp))

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
                        text = "2",
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
                    text = "200000 VND",
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable
@Preview
fun CartCardPreview() {
}