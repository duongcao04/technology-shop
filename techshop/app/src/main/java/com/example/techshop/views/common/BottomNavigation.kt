package com.example.techshop.views.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.techshop.ui.theme.Primary500

data class BottomNavItem(
    val id: Int,
    val name: String,
    val icon: ImageVector,
    val activeIcon: ImageVector,
    val route: String
)

// Updated menu items to match the plant app in the image with better icon choices
val BOTTOM_MENUS = listOf(
    BottomNavItem(1, "Trang chủ", Icons.Outlined.Home, activeIcon = Icons.Filled.Home, "home"),
    BottomNavItem(
        2,
        "Sản phẩm",
        Icons.Outlined.GridView,
        activeIcon = Icons.Filled.GridView,
        "product"
    ),
    BottomNavItem(4, "Tôi", Icons.Outlined.Person, Icons.Filled.Person, "me")
)

@Composable()
fun BottomNavigation(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Surface(
        tonalElevation = 8.dp,
        modifier = Modifier
            .height(132.dp)
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
            },
        color = Color.White,
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BOTTOM_MENUS.forEach { menuItem ->
                val isSelected = menuItem.route == currentRoute

                AnimatedNavItem(
                    item = menuItem,
                    isSelected = isSelected,
                    onItemClick = {
                        navController.navigate(menuItem.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AnimatedNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Animations
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )

    val iconSize by animateDpAsState(
        targetValue = if (isSelected) 33.dp else 30.dp,
        animationSpec = tween(300),
        label = "iconSize"
    )

    val boxPadding by animateDpAsState(
        targetValue = if (isSelected) 11.dp else 10.dp,
        animationSpec = tween(300),
        label = "boxPadding"
    )

    val elevationAnim by animateFloatAsState(
        targetValue = if (isSelected) 6f else 0f,
        animationSpec = tween(300),
        label = "elevation"
    )

    // Refined colors
    val icon = if (isSelected) item.activeIcon else item.icon
    val iconColor = if (isSelected) Color.White else Color.Black
    val textColor = if (isSelected) Primary500 else Color.Black
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
    val buttonColor = if (isSelected) Primary500 else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onItemClick
            )
            .padding(vertical = 18.dp)
    ) {
        Box(
            modifier = Modifier
                .scale(scale) // Apply scale animation to the entire Box
                .clip(CircleShape)
                .background(buttonColor)
                .padding(boxPadding)
                .graphicsLayer {
                    shadowElevation = elevationAnim
                    shape = CircleShape
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = item.name,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.name,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            letterSpacing = 0.2.sp,
            maxLines = 1,
            modifier = Modifier.scale(if (isSelected) 1.05f else 1f)
        )
    }
}

@Composable()
@Preview()
fun BottomNavigationPreview() {
    BottomNavigation(navController = NavController(LocalContext.current))
}