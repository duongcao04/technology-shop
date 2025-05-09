package com.example.techshop.views.components.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.techshop.ui.theme.Primary500

@Composable
fun PromotionsSection(navController: NavController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = null,
                    tint = Primary500,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ưu đãi đặc biệt",
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }

            TextButton(
                onClick = {
                    Toast.makeText(
                        context,
                        "Tính năng đang được cập nhật!",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Xem tất cả",
                    color = Primary500,
                    style = MaterialTheme.typography.button.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Primary500,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Promotions List
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(promotions) { promotion ->
                PromotionCard(promotion = promotion) {
                    Toast.makeText(
                        context,
                        "Tính năng đang được cập nhật!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

@Composable
fun PromotionCard(promotion: Promotion, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image or color gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = promotion.gradientColors
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Promotion title and subtitle
                Column {
                    Text(
                        text = promotion.title,
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = promotion.subtitle,
                        style = MaterialTheme.typography.body1.copy(
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    )
                }

                // Promotion details and expiry
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.3f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = promotion.code,
                            style = MaterialTheme.typography.button.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Text(
                        text = "Hết hạn: ${promotion.expiry}",
                        style = MaterialTheme.typography.caption.copy(
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

// Data model for promotions
data class Promotion(
    val id: String,
    val title: String,
    val subtitle: String,
    val code: String,
    val expiry: String,
    val gradientColors: List<Color>
)

// Sample promotions data
val promotions = listOf(
    Promotion(
        id = "promo1",
        title = "Giảm 20% cho học sinh, sinh viên",
        subtitle = "Áp dụng cho tất cả laptop & tablet",
        code = "STUDENT20",
        expiry = "31/05/2025",
        gradientColors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
    ),
    Promotion(
        id = "promo2",
        title = "Flash Sale Cuối Tuần",
        subtitle = "Giảm đến 30% cho hàng gaming",
        code = "FLASH30",
        expiry = "15/05/2025",
        gradientColors = listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))
    ),
    Promotion(
        id = "promo3",
        title = "Ưu đãi đặc biệt",
        subtitle = "Tặng tai nghe khi mua laptop",
        code = "GIFT4U",
        expiry = "20/05/2025",
        gradientColors = listOf(Color(0xFF11998E), Color(0xFF38EF7D))
    )
)