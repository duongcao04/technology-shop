package com.example.techshop.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.techshop.R

@Composable
fun Splash(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFFFFF)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.techshop_logo),
                    contentDescription = "TechShop Logo",
                    modifier = Modifier.size(160.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "TechShop",
                    style = MaterialTheme.typography.displayLarge.copy(
                        color = Color.Black, // Màu đen nổi bật
                        fontWeight = FontWeight.Bold, // Chữ đậm
                        fontSize = 36.sp, // Kích thước lớn
                        letterSpacing = 1.sp // Khoảng cách giữa các chữ
                    ),

                )
                // Dòng mô tả
                Text(
                    text = "Sản phẩm công nghệ cao cấp\ncho cuộc sống số của bạn",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
            // Phần nút bấm (giữ nguyên)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id=R.color.button_primary),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Bắt đầu",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Trải nghiệm sự khác biệt cùng TechShop! 😊",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF666666),
                            fontSize = 10.sp,
                        )
                    )
                }

        }
    }
}