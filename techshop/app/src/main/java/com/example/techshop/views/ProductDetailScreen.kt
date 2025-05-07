import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.navigation.NavController
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.techshop.models.Product
import com.example.techshop.ui.theme.AccentBlack
import com.example.techshop.ui.theme.AccentWhite
import com.example.techshop.ui.theme.Gray50
import com.example.techshop.ui.theme.Primary500

@Composable
fun ProductDetailScreen(
    navController: NavController,
    product: Product,
    onAddToCart: () -> Unit
) {
    val mintGreen = Color(0xFF9ECECE)
    var isFavorite by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(3) } // Starting with quantity 3 as shown in image

    Scaffold(
        topBar = { TopBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top section with image and navigation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .shadow(
                            elevation = 1.dp,
                            shape = RoundedCornerShape(24.dp),
                            clip = true // cắt nội dung theo shape
                        )
                        .background(
                            color = mintGreen,
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    // Product image
                    product.imageUrl?.let {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center)
                                .clip(RoundedCornerShape(24.dp)),
                        )
                    } ?: run {
                        // Placeholder if no image is available
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(240.dp)
                                .align(Alignment.Center)
                                .background(Color.LightGray)
                        )
                    }
                }

                // Product title and details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp)
                ) {
                    // Product name
                    Text(
                        text = product.name ?: "Tên sản phẩm",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // Product count information (42 gummies in the example)
                    Text(
                        text = "Kho: 40",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Price and quantity selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Price
                        Text(
                            text = "$${product.price ?: "0.00"}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    // Product category
                    Text(
                        text = "Mô tả",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    // Product description
                    Text(
                        text = product.description
                            ?: "Eat one of these gummies per day. They taste like the delicious fruit punch of your youth, so this shouldn't be too hard to keep up with. ❤️",
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            FloatingBottomBar()
        }
    }
}

@Composable
fun TopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = AccentWhite,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Text("Detail", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp))

        // Favorite button
        IconButton(
            onClick = { },
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun FloatingBottomBar() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .clip(CircleShape)
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .clip(CircleShape)
                    .height(60.dp),
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.5f)
                        .padding(5.dp)
                        .clip(CircleShape),
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlack
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AddShoppingCart,
                        contentDescription = "Add cart",
                        tint = Color.White
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(5.dp)
                        .clip(CircleShape),
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary500
                    ),
                ) {
                    Text(
                        text = "Mua ngay",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Preview function (if needed)
@Composable
@Preview(showBackground = true)
fun ProductDetailScreenPreview() {
    val sampleProduct = Product(
        id = "123",
        name = "Gummy Vitamins",
        description = "Eat one of these gummies per day. They taste like the delicious fruit punch of your youth, so this shouldn't be too hard to keep up with. ❤️,Eat one of these gummies per day. They taste like the delicious fruit punch of your youth, so this shouldn't be too hard to keep up with. ❤️,Eat one of these gummies per day. They taste like the delicious fruit punch of your youth, so this shouldn't be too hard to keep up with. ❤️,Eat one of these gummies per day. They taste like the delicious fruit punch of your youth, so this shouldn't be too hard to keep up with. ❤️,Eat one of these gummies per day. They taste like the delicious fruit punch of your youth, so this shouldn't be too hard to keep up with. ❤️,Eat one of these gummies per day. They taste like the delicious fruit punch of your youth, so this shouldn't be too hard to keep up with. ❤️,Eat one of these gummies per day. They taste like the delicious fruit punch of your youth, so this shouldn't be too hard to keep up with. ❤️,Eat one of these gummies per day. They taste like the delicious fruit punch of your youth, so this shouldn't be too hard to keep up with. ❤️,",
        price = 19.00,
        imageUrl = "https://cdn.mos.cms.futurecdn.net/v2/t:0,l:439,cw:2632,ch:1974,q:80,w:2632/FUi2wwNdyFSwShZZ7LaqWf.jpg"
    )

    ProductDetailScreen(
        navController = rememberNavController(),
        product = sampleProduct,
        onAddToCart = { }
    )
}