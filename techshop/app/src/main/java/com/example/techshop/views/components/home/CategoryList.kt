package com.example.techshop.views.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.techshop.R

data class Category(
    val id: Int,
    val name: String,
    val image: String, // Thay đổi từ String sang Int để sử dụng resource ID
    val bgColor: Color,
)

// Danh sách category với hình ảnh từ resource
val categoryList = listOf(
    Category(
        1,
        "Máy tính",
        "https://parspng.com/wp-content/uploads/2023/02/computerpng.parspng.com-4.png",
        Color(0xFFFCE7F3)
    ),
    Category(
        2,
        "Laptop",
        "https://royaltyfreefootages.com/upload/video/Apple%20laptop%20PNG%20image,%20transparent%20Apple%20laptop%20png%20image,%20Apple%20laptop%20png%20hd%20images%20download_1658745513.png",
        Color(0xFFFAE8FF)
    ),
    Category(
        3,
        "Điện thoại",
        "https://stratanetworks.com/wp-content/uploads/2023/09/iPhone-15-Blue.png",
        Color(0xFFE0E7FF)
    ),
    Category(
        4,
        "Phụ kiện",
        "https://stratanetworks.com/wp-content/uploads/2023/09/iPhone-15-Blue.png",
        Color(0xFFCFFAFE)
    ),
    Category(
        5,
        "Máy tính bảng",
        "https://stratanetworks.com/wp-content/uploads/2023/09/iPhone-15-Blue.png",
        Color(0xFFD1FAE5)
    ),
    Category(
        6,
        "Linh kiện",
        "https://stratanetworks.com/wp-content/uploads/2023/09/iPhone-15-Blue.png",
        Color(0xFFECFCCB)
    ),
)

@Composable
fun CategoryList(onCategoryClick: (Category) -> Unit = {}) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Tiêu đề
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Danh mục",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Text(
                text = "Xem tất cả",
                color = Color(0xFF4285F4),
                fontSize = 14.sp,
                modifier = Modifier.clickable { /* Handle see all click */ }
            )
        }

        // Danh sách category dạng carousel
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categoryList) { category ->
                CategoryItem(
                    category = category,
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
    ) {
        // Hình vuông bo góc với hình ảnh và chữ ở giữa
        Row(
            modifier = Modifier
                .height(60.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(category.bgColor)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Hình ảnh từ URL
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(category.image)
                    .crossfade(true)
                    .build(),
                contentDescription = category.name,
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit
            )

            // Tên category
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                text = category.name,
                fontSize = 12.sp,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Chú ý: Bạn cần tạo các tài nguyên drawable tương ứng với các category trong ứng dụng thực tế

@Preview(showBackground = true)
@Composable
fun CategoryListPreview() {
    CategoryList()
}