package com.example.techshop.views.components.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techshop.ui.theme.Primary50
import com.example.techshop.ui.theme.Primary500
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NewsletterSection() {
    var email by remember { mutableStateOf("") }
    var isSubscribed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Primary500,
                            Primary500.copy(alpha = 0.7f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Đăng ký nhận thông báo",
                    style = MaterialTheme.typography.h6.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Nhận thông tin về ưu đãi và sản phẩm mới nhất từ TechShop",
                    style = MaterialTheme.typography.body1.copy(
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!isSubscribed) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = {
                            Text(
                                "Email của bạn",
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f)
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // TODO: Subscribe logic
                            if (email.isNotEmpty()) {
                                scope.launch {
                                    isSubscribed = true
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:")
                                        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                                        putExtra(Intent.EXTRA_SUBJECT, "Đăng ký nhận email!")
                                        putExtra(Intent.EXTRA_TEXT, "Đăng ký nhận email từ TechShop")
                                    }
                                    if (emailIntent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(emailIntent)
                                    }
                                    // Reset after 5 seconds for demo purposes
                                    delay(5000)
                                    isSubscribed = false
                                    email = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Primary500
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Đăng ký ngay",
                            style = MaterialTheme.typography.button.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    // Success message
                    Card(
                        backgroundColor = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Đăng ký thành công! Cảm ơn bạn đã đăng ký.",
                            style = MaterialTheme.typography.body1.copy(
                                color = Primary500,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}