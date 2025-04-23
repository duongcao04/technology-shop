package com.example.techshop.views.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.techshop.R

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CarouselPager(items: List<Int>) {
    val pagerState = rememberPagerState()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            count = items.size,
            state = pagerState,
            modifier = Modifier.height(200.dp) // Thiết lập chiều cao cố định
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEEEEEE)), // Màu nền mặc định khi ảnh đang tải
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = items[page]), // Sử dụng items[page] thay vì page
                    contentDescription = "Banner image ${page + 1}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.padding(8.dp),
            activeColor = Color(0xFF3B82F6),
            inactiveColor = Color(0xFFBBDEFB)
        )
    }
}

@Composable
fun Banner() {
    val items = listOf(
        R.drawable.banner_1,
        R.drawable.banner_2,
        R.drawable.banner_3,
        R.drawable.banner_4
    )
    CarouselPager(items = items)
}

@Preview(showBackground = true)
@Composable
fun BannerPreview() {
    Banner()
}