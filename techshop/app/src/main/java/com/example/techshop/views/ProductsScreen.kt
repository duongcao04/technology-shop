package com.example.techshop.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ViewModule
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.techshop.models.Product
import com.example.techshop.ui.theme.AccentBlack
import com.example.techshop.ui.theme.Primary100
import com.example.techshop.ui.theme.Primary500
import com.example.techshop.viewmodels.ProductViewModel
import com.example.techshop.views.components.products.ProductCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: ProductViewModel, navController: NavController) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    // UI states
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }
    var gridColumns by remember { mutableIntStateOf(2) }
    var sortOption by remember { mutableStateOf("Mới nhất") }

    // Filter states - chỉ giữ lại lọc theo giá
    var selectedPriceRange by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var showNewestOnly by remember { mutableStateOf(false) }

    val filteredProducts =
        remember(searchQuery, products, sortOption, selectedPriceRange, showNewestOnly) {
            var filtered = if (searchQuery.isBlank()) {
                products
            } else {
                // Chỉ tìm kiếm theo tên sản phẩm
                products.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }
            }

            // Lọc sản phẩm mới (giả sử là theo ID cao)
            if (showNewestOnly) {
                // Lấy 30% sản phẩm mới nhất
                val newProductsCount = (filtered.size * 0.3).toInt().coerceAtLeast(1)
                filtered = filtered.sortedByDescending { it.id }.take(newProductsCount)
            }

            // Lọc theo khoảng giá
            if (selectedPriceRange != null) {
                filtered = filtered.filter { product ->
                    val price = product.getPriceAfterDiscount()
                    price >= selectedPriceRange!!.first && price <= selectedPriceRange!!.second
                }
            }

            // Sắp xếp
            filtered = when (sortOption) {
                "Mới nhất" -> filtered.sortedByDescending { it.id }
                "Giá: Thấp → Cao" -> filtered.sortedBy { it.getPriceAfterDiscount() }
                "Giá: Cao → Thấp" -> filtered.sortedByDescending { it.getPriceAfterDiscount() }
                else -> filtered.sortedByDescending { it.id } // Mặc định: "Mới nhất"
            }

            filtered
        }

    // Load products when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadAllProducts()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Enhanced Top Bar with Search
            EnhancedTopBar(
                navController = navController,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                isSearchActive = isSearchActive,
                onSearchActiveChange = { isSearchActive = it },
                onToggleView = { gridColumns = if (gridColumns == 2) 1 else 2 },
                currentViewColumns = gridColumns,
                onFilterClick = { showFilters = !showFilters }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter and Sort Section
            AnimatedVisibility(
                visible = showFilters,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }),
                exit = fadeOut()
            ) {
                Column {
                    // Sort options
                    SimpleSortBar(
                        selectedOption = sortOption,
                        onOptionSelected = { sortOption = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Simplified filters (price range only and newest products)
                    SimplifiedFilters(
                        onPriceRangeSelected = { selectedPriceRange = it },
                        onNewProductsToggle = { showNewestOnly = it },
                        selectedPriceRange = selectedPriceRange,
                        showNewestOnly = showNewestOnly
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        LoadingIndicator()
                    }

                    error != null -> {
                        EnhancedErrorMessage(
                            message = error!!,
                            onRetry = { viewModel.loadAllProducts() }
                        )
                    }

                    filteredProducts.isEmpty() && searchQuery.isNotBlank() -> {
                        NoSearchResultsMessage(searchQuery)
                    }

                    filteredProducts.isEmpty() && (selectedPriceRange != null || showNewestOnly) -> {
                        NoFilterResultsMessage(onClearFilters = {
                            selectedPriceRange = null
                            showNewestOnly = false
                        })
                    }

                    products.isEmpty() -> {
                        EmptyProductsMessage()
                    }

                    else -> {
                        EnhancedProductGrid(
                            products = filteredProducts,
                            onProductClick = { productId ->
                                navController.navigate("productDetail/$productId")
                            },
                            gridState = gridState,
                            columns = gridColumns
                        )
                    }
                }
            }
        }

        // Scroll to top FAB - shows when scrolled down
        val showScrollToTop by remember {
            derivedStateOf { gridState.firstVisibleItemIndex > 5 }
        }

        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        gridState.animateScrollToItem(0)
                    }
                },
                containerColor = Primary500,
                contentColor = Color.White,
                modifier = Modifier.size(48.dp)
            ) {
                Text(
                    text = "↑",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedTopBar(
    navController: NavController,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    onToggleView: () -> Unit,
    currentViewColumns: Int,
    onFilterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .height(if (isSearchActive) 70.dp else 60.dp)
    ) {
        if (isSearchActive) {
            // Search mode UI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Tìm kiếm theo tên sản phẩm", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Primary500
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            onSearchQueryChange("")
                            onSearchActiveChange(false)
                        }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Primary500
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = Primary500,
                        focusedBorderColor = Primary500,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    singleLine = true
                )
            }
        } else {
            // Normal mode UI
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sản phẩm",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlack
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // Search icon
                    IconButton(
                        onClick = { onSearchActiveChange(true) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Primary100)
                    ) {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Primary500
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Grid view toggle
                    IconButton(
                        onClick = onToggleView,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Primary100)
                    ) {
                        Icon(
                            Icons.Outlined.ViewModule,
                            contentDescription = "Toggle view",
                            tint = Primary500
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Filter button
                    IconButton(
                        onClick = onFilterClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Primary500)
                    ) {
                        Icon(
                            Icons.Outlined.FilterAlt,
                            contentDescription = "Filter",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleSortBar(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val options = listOf("Mới nhất", "Giá: Thấp → Cao", "Giá: Cao → Thấp")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) },
                    label = {
                        Text(
                            text = option,
                            fontSize = 12.sp,
                            maxLines = 1,
                            fontWeight = if (selectedOption == option) FontWeight.Medium else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary500,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF5F5F5),
                        labelColor = AccentBlack
                    )
                )
            }
        }
    }
}

@Composable
fun SimplifiedFilters(
    onPriceRangeSelected: (Pair<Double, Double>?) -> Unit,
    onNewProductsToggle: (Boolean) -> Unit,
    selectedPriceRange: Pair<Double, Double>?,
    showNewestOnly: Boolean
) {
    val priceRanges = listOf(
        "Tất cả" to null,
        "Dưới 1 triệu" to (0.0 to 1000000.0),
        "1 - 5 triệu" to (1000000.0 to 5000000.0),
        "5 - 10 triệu" to (5000000.0 to 10000000.0),
        "10 - 20 triệu" to (10000000.0 to 20000000.0),
        "Trên 20 triệu" to (20000000.0 to Double.MAX_VALUE)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Checkbox cho sản phẩm mới
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = showNewestOnly,
                    onCheckedChange = onNewProductsToggle,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Primary500,
                        uncheckedColor = Color.Gray
                    )
                )

                Text(
                    text = "Chỉ hiển thị sản phẩm mới",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = AccentBlack
                )
            }

            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // Price range section with explicit title
            Text(
                text = "Lọc theo giá",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = AccentBlack,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(160.dp)
            ) {
                items(priceRanges) { (label, range) ->
                    val isSelected = if (range == null && selectedPriceRange == null) {
                        true
                    } else if (range != null && selectedPriceRange != null) {
                        range.first == selectedPriceRange.first && range.second == selectedPriceRange.second
                    } else {
                        false
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Primary500 else Color(0xFFF5F5F5))
                            .clickable { onPriceRangeSelected(range) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = if (isSelected) Color.White else AccentBlack,
                            textAlign = TextAlign.Center,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reset filters button
            Button(
                onClick = {
                    onPriceRangeSelected(null)
                    onNewProductsToggle(false)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary500
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Xóa bộ lọc")
            }
        }
    }
}

@Composable
fun EnhancedProductGrid(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    gridState: androidx.compose.foundation.lazy.grid.LazyGridState,
    columns: Int
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color.White)
            .padding(top = 12.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = gridState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = Primary500,
            modifier = Modifier.size(48.dp),
            strokeWidth = 4.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Đang tải sản phẩm...",
            color = AccentBlack,
            fontSize = 16.sp
        )
    }
}

@Composable
fun EnhancedErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(0.8f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Warning,
            contentDescription = "Error",
            tint = Color(0xFFE53935),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            color = Color(0xFFE53935),
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary500
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Thử lại")
        }
    }
}

@Composable
fun EmptyProductsMessage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.ViewModule,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Không có sản phẩm nào",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = AccentBlack
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Các sản phẩm sẽ xuất hiện ở đây",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NoSearchResultsMessage(query: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Không tìm thấy kết quả",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = AccentBlack
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Không tìm thấy sản phẩm nào có tên \"$query\".\nVui lòng thử từ khóa khác.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NoFilterResultsMessage(onClearFilters: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Không tìm thấy sản phẩm",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = AccentBlack
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Không có sản phẩm nào phù hợp với bộ lọc hiện tại.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onClearFilters,
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary500
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Xóa bộ lọc")
        }
    }
}