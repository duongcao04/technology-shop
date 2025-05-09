package com.example.techshop.views.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.techshop.ui.theme.Primary500

@Composable
fun AboutUsSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Header with modern gradient background
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Primary500,
                                Primary500.copy(alpha = 0.8f),
                                Color(0xFF2575FC)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo or Icon
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = "TechShop Logo",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "TechShop",
                        style = MaterialTheme.typography.h5.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "Định hướng công nghệ, dẫn đầu tương lai",
                        style = MaterialTheme.typography.body2.copy(
                            color = Color.White,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Cards - Chuyển thành Column cho thiết bị nhỏ
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatisticCard(
                icon = Icons.Default.People,
                value = "100K+",
                label = "Khách hàng tin tưởng",
                modifier = Modifier.fillMaxWidth()
            )

            StatisticCard(
                icon = Icons.Default.Inventory,
                value = "5000+",
                label = "Sản phẩm chất lượng",
                modifier = Modifier.fillMaxWidth()
            )

            StatisticCard(
                icon = Icons.Default.WorkspacePremium,
                value = "5*",
                label = "Đánh giá trung bình",
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About Content
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Về Chúng Tôi",
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "TechShop là đối tác tin cậy trong việc mang đến những sản phẩm công nghệ tốt nhất với giá cả hợp lý. Với hơn 5 năm kinh nghiệm trong ngành, chúng tôi cam kết cung cấp dịch vụ khách hàng xuất sắc và bảo hành chu đáo.",
                    style = MaterialTheme.typography.body2.copy(
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Justify
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = { navController.navigate("about_us_detail") },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Primary500,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tìm hiểu thêm")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Features Section
        Text(
            text = "Tại sao chọn TechShop?",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val features = listOf(
            Feature(
                icon = Icons.Default.LocalShipping,
                title = "Giao hàng miễn phí",
                description = "Cho đơn hàng từ 1 triệu",
                color = Color(0xFF4CAF50)
            ),
            Feature(
                icon = Icons.Default.Payment,
                title = "Thanh toán linh hoạt",
                description = "Hỗ trợ trả góp 0%",
                color = Color(0xFF2196F3)
            ),
            Feature(
                icon = Icons.Default.Security,
                title = "Bảo hành chính hãng",
                description = "Lên đến 36 tháng",
                color = Color(0xFFFF9800)
            ),
            Feature(
                icon = Icons.Default.Support,
                title = "Hỗ trợ 24/7",
                description = "Tư vấn nhiệt tình",
                color = Color(0xFF9C27B0)
            )
        )

        // Features Grid - 2 cột cho điện thoại
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            features.chunked(2).forEach { rowFeatures ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowFeatures.forEach { feature ->
                        FeatureCard(
                            feature = feature,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary500,
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold,
                        color = Primary500
                    )
                )

                Text(
                    text = label,
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

@Composable
fun FeatureCard(
    feature: Feature,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(feature.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    tint = feature.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = feature.title,
                style = MaterialTheme.typography.subtitle2.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = feature.description,
                style = MaterialTheme.typography.caption.copy(
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

// Data models
data class Feature(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val color: Color
)