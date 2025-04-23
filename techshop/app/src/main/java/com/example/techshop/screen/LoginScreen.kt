package com.example.techshop.screen

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.techshop.R
import com.example.techshop.ui.theme.Primary500
import com.example.techshop.ui.theme.Raleway

@Composable
@Preview(showBackground = true)
fun LoginScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(), // Ảnh phủ toàn màn hình
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "Ảnh local",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Nội dung hiển thị bên trên nền
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Đăng nhập", color = Color.Black, style = TextStyle(
                    fontSize = 52.sp,
                    fontWeight = FontWeight(800)
                ), fontFamily = Raleway
            )
            Text(
                "Chào mừng quay trở lại!",
                color = Color.Black, style = TextStyle(
                    fontSize = 19.sp,
                    fontWeight = FontWeight(400)
                ), modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp), fontFamily = Raleway
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = { },
                modifier = Modifier
                    .height(61.dp)
                    .fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary500
                )
            ) {
                Row(){
                    Image(
                        painter = painterResource(id = R.drawable.icon_google_2),
                        contentDescription = "Google Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "Đăng nhập với Google", style = TextStyle(
                            fontSize = 19.sp,
                            fontWeight = FontWeight(500)
                        ),
                        modifier = Modifier.padding(bottom = 10.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}