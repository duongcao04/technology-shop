package com.example.techshop.views.components.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.techshop.R

@Composable
fun Topbar(navController: NavController) {
    var text by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(top = 4.dp, start = 8.dp, end = 8.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(20.dp)
                    .padding(0.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ),
            ) {

            }

            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .background(Color.White, shape = CircleShape),
                textStyle = TextStyle(color = Color.Black, fontSize = 12.sp),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (text.isEmpty()) {
                            Text(
                                text = "Tìm kiếm ...",
                                style = TextStyle(color = Color.Gray, fontSize = 12.sp)
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}
