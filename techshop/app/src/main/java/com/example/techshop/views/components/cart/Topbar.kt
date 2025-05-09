package com.example.techshop.views.components.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Topbar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(top = 4.dp, start = 8.dp, end = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Giỏ hàng", style = TextStyle(fontSize = 16.sp))
            Text(
                "(52)",
                modifier = Modifier.padding(start = 2.dp),
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal)
            )
        }
    }
}