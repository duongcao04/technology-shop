package com.example.techshop.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.techshop.viewmodels.ProfileViewModel
import com.example.techshop.ui.theme.* // Import màu từ file theme của bạn
import com.example.techshop.viewmodels.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    authViewModel: AuthViewModel
) {
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Lắng nghe sự thay đổi của authState để xử lý đăng xuất
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.SignedOut) {
            // Khi đăng xuất thành công, điều hướng về màn hình login
            navController.navigate("login") {
                // Xóa tất cả các màn hình khỏi back stack để người dùng không thể quay lại
                popUpTo(0) { inclusive = true }
            }
        }
    }



    // Đảm bảo tải dữ liệu người dùng khi hiển thị màn hình
    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    // Sử dụng màu nền cho toàn bộ màn hình
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = userState) {
            is ProfileViewModel.UserState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Rose600)
                }
            }
            is ProfileViewModel.UserState.Success -> {
                val user = state.user

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // User profile header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Rose100)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Profile image
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Rose300)
                            ) {
                                if (user.photoUrl != "null" && user.photoUrl?.isNotEmpty() == true) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(user.photoUrl)
                                                .crossfade(true)
                                                .build()
                                        ),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Rose500),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.displayName?.firstOrNull()?.toString() ?: "?",
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // User name
                            Text(
                                text = user.displayName ?: "No Name",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                color = Color.Black  // Thay đổi màu chữ thành đen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Profile menu options
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Rose100)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Thông tin cá nhân
                            ProfileMenuItem(
                                title = "Thông tin cá nhân",
                                icon = Icons.Default.Person,
                                onClick = {
                                    navController.navigate("infoUser")
                                }
                            )

                            Divider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Rose300
                            )
// Lịch sử đơn hàng
                            ProfileMenuItem(
                                title = "Lịch sử đơn hàng",
                                icon = Icons.Default.History,
                                onClick = {
                                    user?.uid?.let { userId ->
                                        navController.navigate("order_history/$userId")
                                    }
                                }
                            )

// Xóa phần ElevatedButton dưới vì đã chuyển logic lên trên

                            Divider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Rose300
                            )

                            ProfileMenuItem(
                                title = "Trung tâm hổ trợ",
                                icon = Icons.Default.Help,
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://nld.com.vn/cong-doan/tap-trung-ho-tro-nguoi-lao-dong-gap-kho-khan-20230618201426675.htm")
                                    }
                                    context.startActivity(intent)
                                }
                            )

                            Divider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Rose300
                            )

                            ProfileMenuItem(
                                title = "Trò chuyện với TechShop",
                                icon = Icons.Default.Chat,
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://www.facebook.com/quan.giaphong.79")
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Đăng xuất button
                    Button(
                        onClick = {
                            // Sử dụng AuthViewModel để đăng xuất thay vì ProfileViewModel
                            authViewModel.logout()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Rose600,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Đăng xuất",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            is ProfileViewModel.UserState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Rose50),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: ${state.message}",
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadCurrentUser() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Rose500,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            is ProfileViewModel.UserState.SignedOut -> {
                // Trạng thái này sẽ được xử lý bởi LaunchedEffect ở trên
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Rose50),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Rose600)
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Primary700 // Icon màu đen
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = Color.Black  // Chữ màu đen
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = Primary700  // Icon màu đen
        )
    }
}