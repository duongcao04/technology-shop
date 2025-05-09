package com.example.techshop.views
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.zIndex
import com.example.techshop.ui.theme.AccentBlack
import com.example.techshop.ui.theme.Primary500
import com.example.techshop.viewmodels.CartViewModel
import com.example.techshop.viewmodels.ProductViewModel
import com.example.techshop.views.components.productDetail.FloatingBottombar
import com.example.techshop.views.components.productDetail.Topbar
import com.example.techshop.views.components.products.AccessseliingPrice
import com.example.techshop.views.components.products.DiscountTag
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    viewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    productId: String,
    userId: String,

) {
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Thông báo thêm giỏ hàng thành công
    var showAddToCartSuccessDialog by remember { mutableStateOf(false) }

    // Load product details when the screen is first displayed
    LaunchedEffect(productId) {
        viewModel.loadProductDetails(productId)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(1f)
                ) {
                    Topbar(navController)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    // Product image
                    selectedProduct?.imageUrl?.let {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = selectedProduct?.name,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center)
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
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Product title and details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = selectedProduct?.name ?: "Tên sản phẩm",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Product Price
                if (selectedProduct?.discountPercent ?: 0 > 0) {  // Kiểm tra giảm giá
                    Row(verticalAlignment = Alignment.Bottom) {
                        AccessseliingPrice(selectedProduct?.getFormattedDiscountPrice() ?: "0.0")
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = selectedProduct?.getFormattedPrice() ?: "0.0",
                            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal),
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        DiscountTag((selectedProduct?.discountPercent ?: "0").toString())
                    }
                } else {
                    // Hiển thị giá nếu không có giảm giá
                    AccessseliingPrice(selectedProduct?.getFormattedPrice() ?: "0.0")
                }


                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 8.dp),
                    color = Color(0xffeeeffa),
                    thickness = 2.dp
                )

                // Description Section
                Text(
                    text = "Mô tả sản phẩm",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
                )

                Spacer(modifier = Modifier.height(7.dp))

                when {
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }

                    error != null -> {
                        Text(
                            text = error ?: "Lỗi không xác định",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Red
                        )
                    }

                    selectedProduct == null -> {
                        Text(
                            text = "Không tìm thấy sản phẩm",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    else -> {
                        Text(
                            text = selectedProduct?.description ?: "Không có mô tả",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Add extra space at bottom to ensure floating bar doesn't cover content
            Spacer(modifier = Modifier.height(80.dp))
        }

        // Floating bottom bar
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            FloatingBottombar(
                onAddToCart = {
                    selectedProduct?.let { product ->
                        // Thêm sản phẩm vào giỏ hàng
                        cartViewModel.addProductToCart(userId, product, 1)
                        // Hiển thị thông báo thành công
                        showAddToCartSuccessDialog = true
                    }
                }
            )
        }


        // Dialog thông báo thêm giỏ hàng thành công
        if (showAddToCartSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showAddToCartSuccessDialog = false },
                title = { Text("Thành công") },
                text = { Text("Đã thêm sản phẩm vào giỏ hàng") },
                confirmButton = {
                    Button(
                        onClick = {
                            showAddToCartSuccessDialog = false
                            // Chuyển đến màn hình giỏ hàng nếu muốn
                            // navController.navigate("cart")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary500)
                    ) {
                        Text("Đóng")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showAddToCartSuccessDialog = false
                            // Chuyển đến màn hình giỏ hàng
                            navController.navigate("cart")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlack)
                    ) {
                        Text("Đến giỏ hàng")
                    }
                }
            )
        }



    }
}