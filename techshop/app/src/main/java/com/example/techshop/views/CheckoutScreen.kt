package com.example.techshop.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.techshop.ui.screens.Rose600
import com.example.techshop.ui.screens.Rose800
import com.example.techshop.ui.theme.Primary500
import com.example.techshop.viewmodels.CartViewModel
import com.example.techshop.viewmodels.OrderViewModel
import com.example.techshop.views.components.cart.CheckoutItemCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    userId: String
) {
    val cart by cartViewModel.cart.collectAsState()
    val isProcessingOrder by orderViewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    // Tạo một bản sao của cart để sử dụng khi đặt hàng
    // Tránh trường hợp cart bị null sau khi đặt hàng thành công
    val currentCart = remember(cart) { cart }

    // Các trường nhập liệu
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Validation
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage by orderViewModel.successMessage.collectAsState()
    val errorMessage by orderViewModel.errorMessage.collectAsState()

    // Trạng thái kiểm soát việc điều hướng
    var orderPlaced by remember { mutableStateOf(false) }

    // Theo dõi thông báo thành công hoặc lỗi
    LaunchedEffect(successMessage, errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(message = it)
            orderViewModel.clearError()
        }

        successMessage?.let {
            // Đánh dấu đơn hàng đã đặt thành công
            orderPlaced = true

            // Chuyển đến màn hình thành công
            navController.navigate("success") {
                // Xóa màn hình giỏ hàng và thanh toán khỏi back stack
                popUpTo("cart") { inclusive = true }
            }
            orderViewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.height(70.dp)) { // Giảm từ mặc định 64.dp xuống còn 48.dp
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Đặt hàng",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Rose600
                            )
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(

                        titleContentColor = Rose800
                    )
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tổng thanh toán:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = currentCart?.getFormattedTotalAmount() ?: "0₫",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary500
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // Validate các trường nhập liệu
                            var isValid = true

                            if (name.isBlank()) {
                                nameError = "Vui lòng nhập họ tên"
                                isValid = false
                            } else {
                                nameError = null
                            }

                            if (phone.isBlank()) {
                                phoneError = "Vui lòng nhập số điện thoại"
                                isValid = false
                            } else if (!phone.matches(Regex("^0\\d{9}$"))) {
                                phoneError = "Số điện thoại không hợp lệ"
                                isValid = false
                            } else {
                                phoneError = null
                            }

                            if (address.isBlank()) {
                                addressError = "Vui lòng nhập địa chỉ"
                                isValid = false
                            } else {
                                addressError = null
                            }

                            // Nếu dữ liệu hợp lệ, tiến hành đặt hàng
                            if (isValid && currentCart != null && !isProcessingOrder && !orderPlaced) {
                                scope.launch {
                                    orderViewModel.placeOrder(currentCart, name, address, phone)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary500),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        enabled = !isProcessingOrder && !orderPlaced && currentCart != null && currentCart.itemCount > 0
                    ) {
                        if (isProcessingOrder) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Đặt hàng",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F8F8))
        ) {
            if (isProcessingOrder) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Primary500
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Phần nhập thông tin
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Thông tin giao hàng",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )

                                // Nhập họ tên
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Họ và tên") },
                                    placeholder = { Text("Nhập họ và tên") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Tên"
                                        )
                                    },
                                    isError = nameError != null,
                                    supportingText = {
                                        nameError?.let { Text(it) }
                                    },
                                    singleLine = true,
                                    enabled = !isProcessingOrder && !orderPlaced
                                )

                                // Nhập số điện thoại
                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Số điện thoại") },
                                    placeholder = { Text("Nhập số điện thoại") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Điện thoại"
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    isError = phoneError != null,
                                    supportingText = {
                                        phoneError?.let { Text(it) }
                                    },
                                    singleLine = true,
                                    enabled = !isProcessingOrder
                                )

                                // Nhập địa chỉ
                                OutlinedTextField(
                                    value = address,
                                    onValueChange = { address = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Địa chỉ") },
                                    placeholder = { Text("Nhập địa chỉ giao hàng") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = "Địa chỉ"
                                        )
                                    },
                                    isError = addressError != null,
                                    supportingText = {
                                        addressError?.let { Text(it) }
                                    },
                                    enabled = !isProcessingOrder
                                )
                            }
                        }
                    }

                    // Phương thức thanh toán
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Phương thức thanh toán",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = true,
                                        onClick = { /* Chỉ có 1 phương thức */ },
                                        enabled = !isProcessingOrder
                                    )
                                    Text(
                                        text = "Thanh toán khi nhận hàng",
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Danh sách sản phẩm
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Sản phẩm đã chọn",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )

                                currentCart?.items?.values?.forEach { cartItem ->
                                    CheckoutItemCard(cartItem = cartItem)
                                    Divider()
                                }

                                // Tổng tiền
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Tổng (${currentCart?.itemCount ?: 0} sản phẩm):",
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = currentCart?.getFormattedTotalAmount() ?: "0₫",
                                        fontWeight = FontWeight.Bold,

                                    )
                                }
                                // Tổng tiền
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Phí vận chuyển:",
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "0₫",
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                // Tổng tiền
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Tổng cộng:",
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = currentCart?.getFormattedTotalAmount() ?: "0₫",
                                        fontWeight = FontWeight.Bold,
                                        color = Primary500
                                    )
                                }
                            }
                        }
                    }

                    // Thêm space để không bị che bởi bottom bar
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}