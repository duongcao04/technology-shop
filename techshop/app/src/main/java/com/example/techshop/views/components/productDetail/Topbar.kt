package com.example.techshop.views.components.productDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.techshop.R

@Composable
fun Topbar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(top = 4.dp, start = 8.dp, end = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .background(
                        color = Color(0xff716b6b).copy(alpha = 0.8f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(0.dp)
                    .aspectRatio(1f)
            ) {
                Icon(
                    modifier = Modifier.size(14.dp),
                    painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Row {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .background(
                            color = Color(0xff716b6b).copy(alpha = 0.8f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(0.dp)
                        .aspectRatio(1f)
                ) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        painter = painterResource(id = R.drawable.outline_share_24),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = { navController.navigate("cart") },
                    modifier = Modifier
                        .background(
                            color = Color(0xff716b6b).copy(alpha = 0.8f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(0.dp)
                        .aspectRatio(1f)
                ) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        painter = painterResource(id = R.drawable.outline_shopping_cart_24),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun TopbarPreview() {
    val navController = NavController(LocalContext.current)
    Topbar(navController)
}