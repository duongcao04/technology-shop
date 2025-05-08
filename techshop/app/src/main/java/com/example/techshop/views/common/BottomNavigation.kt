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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.techshop.ui.theme.Primary500
import com.example.techshop.viewmodels.CartViewModel

// Khai báo cho bottom navigation
data class BottomNavItem(
    val id: Int,
    val icon: ImageVector,
    val activeIcon: ImageVector,
    val route: String,
    val showBadge: Boolean = false
)

// Các đối tượng tên icon và action khi nhấn vào của từng đối tượng bottom
val BOTTOM_MENUS = listOf(
    BottomNavItem(1, Icons.Outlined.Home, activeIcon = Icons.Filled.Home, "home"),
    BottomNavItem(2, Icons.Outlined.GridView, activeIcon = Icons.Filled.GridView, "product"),
    BottomNavItem(3, Icons.Outlined.ShoppingCart, activeIcon = Icons.Filled.ShoppingCart, "cart", showBadge = true),
    BottomNavItem(4, Icons.Outlined.Person, Icons.Filled.Person, "me")
)

@Composable
fun BottomNavigation(
    navController: NavController,
    cartViewModel: CartViewModel,
    userId: String
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // Theo dõi giỏ hàng
    val cart by cartViewModel.cart.collectAsState()
    val cartItemCount = cart?.itemCount ?: 0

    // Biến để theo dõi việc giỏ hàng đã được khởi tạo chưa
    var cartInitialized by remember { mutableStateOf(false) }

    // Khởi tạo giỏ hàng khi userId thay đổi
    LaunchedEffect(userId) {
        userId?.let {
            if (!cartInitialized) {
                cartViewModel.initCart(it)
                cartInitialized = true
            }
        }
    }

    Surface(
        tonalElevation = 8.dp,
        modifier = Modifier
            .height(80.dp)
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
                .padding(10.dp, 0.dp, 10.dp, 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BOTTOM_MENUS.forEach { menuItem ->
                val isSelected = menuItem.route == currentRoute
                val badgeCount = if (menuItem.showBadge) cartItemCount else 0

                AnimatedNavItem(
                    item = menuItem,
                    isSelected = isSelected,
                    badgeCount = badgeCount,
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
    badgeCount: Int = 0,
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
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            // Icon với container
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
                    contentDescription = item.toString(),
                    tint = iconColor,
                    modifier = Modifier.size(size = 30.dp)
                )
            }

            // Badge
            if (item.showBadge && badgeCount > 0) {
                Badge(
                    count = badgeCount,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .zIndex(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun Badge(
    count: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Red
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(backgroundColor)
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}