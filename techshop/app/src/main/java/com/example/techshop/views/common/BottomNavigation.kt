package com.example.techshop.views.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.techshop.ui.theme.Primary500


data class BottomNavItem(
    val id: Int,
    val name: String,
    val icon: ImageVector,
    val route: String
)

val BOTTOM_MENUS = listOf(
    BottomNavItem(1, "Trang chủ", Icons.Filled.Home, "home"),
    BottomNavItem(2, "Sản phẩm", Icons.Filled.Interests, "product"),
    BottomNavItem(4, "Tôi", Icons.Filled.PermIdentity, "me")
)

@Composable
fun BottomNavigation(navController: NavController) {
    Box(
        modifier = Modifier
            .height(130.dp)
            .padding(20.dp, 30.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            BOTTOM_MENUS.map { menuItem ->
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Column {
                        Icon(
                            imageVector = menuItem.icon,
                            contentDescription = menuItem.name,
                            modifier = Modifier.size(40.dp), // Kích thước icon
                            tint = Primary500 // Màu của icon
                        )
                        Text(menuItem.name)
                    }
                }
            }
        }
    }
}