package com.example.techshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.techshop.R
import com.example.techshop.models.User
import com.example.techshop.viewmodels.ProfileViewModel

// Định nghĩa màu Rose từ bảng màu được cung cấp
val Rose50 = Color(0xFFFFF1F2)
val Rose100 = Color(0xFFFFE4E6)
val Rose200 = Color(0xFFFECDD3)
val Rose300 = Color(0xFFFDA4AF)
val Rose400 = Color(0xFFFB7185)
val Rose500 = Color(0xFFF43F5E)
val Rose600 = Color(0xFFE11D48)
val Rose700 = Color(0xFFBE123C)
val Rose800 = Color(0xFF9F1239)
val Rose900 = Color(0xFF881337)
val Rose950 = Color(0xFF4C0519)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerInfoScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    val roseGradient = Brush.verticalGradient(
        colors = listOf(Rose50, Rose100)
    )

    Scaffold(
        topBar = {
            Box(modifier = Modifier.height(48.dp)) { // Giảm từ mặc định 64.dp xuống còn 48.dp
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Thông tin khách hàng",
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
        }

    ) { paddingValues ->
        when (val state = userState) {
            is ProfileViewModel.UserState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Rose600)
                }
            }
            is ProfileViewModel.UserState.Success -> {
                CustomerInfoDisplay(
                    user = state.user,
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                )
            }
            is ProfileViewModel.UserState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Lỗi: ${state.message}",
                        color = Rose700
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
fun AvatarImage(imageUrl: String) {
    Box(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 4.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, Rose300, CircleShape)
                .background(Rose50)
        ) {
            // Sử dụng SubcomposeAsyncImage thay vì AsyncImage để xử lý error và loading
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Ảnh đại diện",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Rose400,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Rose400,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun CustomerInfoDisplay(
    user: User,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hiển thị avatar ở đầu trang
        if (user.photoUrl != null) {
            AvatarImage(imageUrl = user.photoUrl!!)
        } else {
            // Avatar mặc định nếu không có ảnh
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, Rose300, CircleShape)
                        .background(Rose50)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Rose400,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Thông tin cơ bản
        InfoSection(
            title = "Thông tin cơ bản",
            iconTint = Rose600
        ) {
            InfoItem(label = "Tên hiển thị", value = user.displayName ?: "Chưa cập nhật")
            InfoItem(label = "Email", value = user.email ?: "Chưa cập nhật")
            InfoItem(label = "Số điện thoại", value = user.phoneNumber ?: "Chưa cập nhật")
            InfoItem(
                label = "Ngày tạo tài khoản",
                value = user.creationTimestamp?.let {
                    java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(it))
                } ?: "Không xác định")
        }





    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoSection(
    title: String,
    iconTint: Color = Color.Unspecified,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Rose100),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Header với background gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Rose400, Rose600)
                        )
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Content
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Rose900,

            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp),
            color = Color.Black


        )

        Divider(
            modifier = Modifier.padding(top = 8.dp),
            color = Rose100,
            thickness = 1.dp
        )
    }
}