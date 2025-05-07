package com.example.techshop.views

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.techshop.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Lắng nghe sự thay đổi của userState
    LaunchedEffect(userState) {
        if (userState is ProfileViewModel.UserState.SignedOut) {
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

    when (val state = userState) {
        is ProfileViewModel.UserState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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
                    shape = RoundedCornerShape(12.dp)
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
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
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
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.displayName?.firstOrNull()?.toString() ?: "?",
                                        color = Color.White,
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // User name
                        Text(
                            text = user.displayName ?: "No Name",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // User email
                        Text(
                            text = user.email ?: "No Email",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (user.phoneNumber != "null" && user.phoneNumber?.isNotEmpty() == true) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = user.phoneNumber ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Profile menu options
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Thông tin cá nhân
                        ProfileMenuItem(
                            title = "Thông tin cá nhân",
                            icon = Icons.Default.Person,
                            onClick = { /* Xử lý click */ }
                        )

                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )

                        // Lịch sử đơn hàng
                        ProfileMenuItem(
                            title = "Lịch sử đơn hàng",
                            icon = Icons.Default.History,
                            onClick = { /* Xử lý click */ }
                        )

                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )




                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )

                        // Cài đặt
                        ProfileMenuItem(
                            title = "Cài đặt",
                            icon = Icons.Default.Settings,
                            onClick = { /* Xử lý click */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Đăng xuất button
                Button(
                    onClick = { viewModel.signOut() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Sign Out"
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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadCurrentUser() }) {
                        Text("Retry")
                    }
                }
            }
        }

        is ProfileViewModel.UserState.SignedOut -> {
            // Trạng thái này sẽ được xử lý bởi LaunchedEffect ở trên
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}