package com.example.techshop.views.components.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.techshop.models.Product
import com.example.techshop.ui.theme.Gray50
import com.example.techshop.ui.theme.Primary100
import com.example.techshop.ui.theme.Primary200
import com.example.techshop.ui.theme.Primary400
import com.example.techshop.ui.theme.Primary500
import com.example.techshop.ui.theme.Primary800

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column() {
            Box {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop,
                    error = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    DiscountTag("10")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                Text(
                    text = product.name,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Product Price
                if (product.discountPercent > 0) {

                    Column() {
                        Text(
                            text = product.getFormattedPrice(),
                            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal),
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray
                        )
                        AccessseliingPrice(product.getFormattedPrice())
                    }
                } else {
                    AccessseliingPrice(product.getFormattedPrice())
                }
            }
        }
    }
}

@Composable
fun AccessseliingPrice(price: String) {
    Text(
        text = price,
        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold),
        color = Primary500
    )
}

@Composable
fun DiscountTag(discount: String) {
    Text(
        text = "-${discount}%",
        style = MaterialTheme.typography.bodySmall,
        color = Primary100,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Primary500)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

@Composable
@Preview
fun ProductCardPreview() {
    ProductCard(
        product = Product(
            id = "1asdas",
            name = "Asus Tuf gaming F15 Pro 12400HAsus Tuf gaming F15 Pro 12400H",
            description = "Des",
            price = 100000.00,
            imageUrl = "",
            discountPercent = 10,
        ), {})
}