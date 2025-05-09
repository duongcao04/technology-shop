package com.example.techshop.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techshop.models.Product
import com.example.techshop.repositorys.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    // StateFlow cho danh sách sản phẩm
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // StateFlow cho sản phẩm được chọn
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // StateFlow cho trạng thái loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // StateFlow cho thông báo lỗi
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // StateFlow cho danh sách sản phẩm ngẫu nhiên
    private val _randomProducts = MutableStateFlow<List<Product>>(emptyList())
    val randomProducts: StateFlow<List<Product>> = _randomProducts.asStateFlow()

    // StateFlow cho danh sách sản phẩm bán chạy nhất
    private val _bestsellerProducts = MutableStateFlow<List<Product>>(emptyList())
    val bestsellerProducts: StateFlow<List<Product>> = _bestsellerProducts.asStateFlow()

    // Tham chiếu đến Firebase Database
    private val database = FirebaseDatabase.getInstance()
    private val ordersRef = database.getReference("orders")
    private val productsRef = database.getReference("products")

    init {
        // Tải dữ liệu ngay khi ViewModel được tạo
        loadAllProductsAndBestsellers()
    }

    // Hàm tải tất cả sản phẩm
    fun loadAllProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.getAllProducts(
                    onSuccess = { productList ->
                        _products.value = productList
                        _isLoading.value = false

                        // Nếu chưa có sản phẩm bán chạy, thử tải lại
                        if (_bestsellerProducts.value.isEmpty()) {
                            loadBestsellerProducts(5)
                        }
                    },
                    onError = { exception ->
                        _error.value = "Lỗi khi tải danh sách sản phẩm: ${exception.message}"
                        _isLoading.value = false
                        _products.value = emptyList()
                    }
                )
            } catch (e: Exception) {
                _error.value = "Lỗi không xác định: ${e.message}"
                _isLoading.value = false
                _products.value = emptyList()
            }
        }
    }

    // Hàm tải chi tiết sản phẩm theo ID
    fun loadProductDetails(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getProductById(
                productId = productId,
                onSuccess = { product ->
                    _selectedProduct.value = product
                    _isLoading.value = false
                },
                onError = { exception ->
                    _error.value = "Lỗi khi tải chi tiết sản phẩm: ${exception.message}"
                    _isLoading.value = false
                }
            )
        }
    }

    // Hàm lấy sản phẩm ngẫu nhiên
    fun loadRandomProducts(count: Int = 5) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.getRandomProducts(
                    limit = count,
                    onSuccess = { productList ->
                        _randomProducts.value = productList
                        _isLoading.value = false
                    },
                    onError = { exception ->
                        _error.value = "Lỗi khi tải sản phẩm ngẫu nhiên: ${exception.message}"
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                _error.value = "Lỗi không xác định khi tải sản phẩm ngẫu nhiên: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Tải tất cả sản phẩm và bestsellers
    fun loadAllProductsAndBestsellers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Tải tất cả sản phẩm trước
                repository.getAllProducts(
                    onSuccess = { productList ->
                        _products.value = productList
                        Log.d("ProductViewModel", "Đã tải ${productList.size} sản phẩm")

                        // Tải bestsellers sau khi có danh sách sản phẩm
                        loadBestsellerProducts(5)

                        // Tải random products
                        loadRandomProducts(6)

                        _isLoading.value = false
                    },
                    onError = { exception ->
                        _error.value = "Lỗi khi tải danh sách sản phẩm: ${exception.message}"
                        _isLoading.value = false
                        Log.e("ProductViewModel", "Lỗi tải sản phẩm: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _error.value = "Lỗi không xác định: ${e.message}"
                _isLoading.value = false
                Log.e("ProductViewModel", "Lỗi không xác định: ${e.message}", e)

                // Tải sản phẩm ngẫu nhiên nếu có lỗi
                loadRandomProducts(6)
            }
        }
    }

    // Hàm lấy sản phẩm bán chạy dựa trên dữ liệu orders
    fun loadBestsellerProducts(limit: Int = 5) {
        viewModelScope.launch {
            try {
                Log.d("ProductViewModel", "Bắt đầu tải bestseller products")

                // Lấy dữ liệu đơn hàng từ Firebase
                val ordersSnapshot = ordersRef.get().await()
                Log.d("ProductViewModel", "Số lượng đơn hàng: ${ordersSnapshot.childrenCount}")

                // Map để lưu trữ số lượng bán của mỗi sản phẩm
                val productSalesCount = mutableMapOf<String, Int>()

                // Phân tích dữ liệu đơn hàng
                for (orderSnapshot in ordersSnapshot.children) {
                    val itemsSnapshot = orderSnapshot.child("items")
                    Log.d(
                        "ProductViewModel",
                        "Đơn hàng ${orderSnapshot.key} có ${itemsSnapshot.childrenCount} sản phẩm"
                    )

                    for (itemSnapshot in itemsSnapshot.children) {
                        // Lấy ID sản phẩm trực tiếp từ key hoặc từ trường productId
                        val productId = itemSnapshot.child("productId").getValue(String::class.java)
                        val quantity = itemSnapshot.child("quantity").getValue(Int::class.java) ?: 1

                        if (productId != null) {
                            Log.d(
                                "ProductViewModel",
                                "Sản phẩm ID: $productId, Số lượng: $quantity"
                            )
                            productSalesCount[productId] =
                                (productSalesCount[productId] ?: 0) + quantity
                        }
                    }
                }

                Log.d("ProductViewModel", "Thống kê bán hàng: $productSalesCount")

                if (productSalesCount.isEmpty()) {
                    Log.d("ProductViewModel", "Không có dữ liệu bán hàng, sử dụng fallback")
                    loadFallbackBestsellers()
                    return@launch
                }

                // Sắp xếp sản phẩm theo số lượng bán (từ cao đến thấp)
                val bestsellerProductIds = productSalesCount.entries
                    .sortedByDescending { it.value }
                    .take(limit)
                    .map { it.key }

                Log.d("ProductViewModel", "ID sản phẩm bán chạy: $bestsellerProductIds")

                // Lấy chi tiết sản phẩm dựa trên ID
                val currentProducts = _products.value
                Log.d("ProductViewModel", "Số lượng sản phẩm hiện có: ${currentProducts.size}")

                if (currentProducts.isNotEmpty()) {
                    val bestsellers = bestsellerProductIds.mapNotNull { productId ->
                        currentProducts.find { it.id == productId }
                    }

                    Log.d("ProductViewModel", "Tìm thấy ${bestsellers.size} sản phẩm bán chạy")

                    if (bestsellers.isNotEmpty()) {
                        _bestsellerProducts.value = bestsellers
                    } else {
                        Log.d(
                            "ProductViewModel",
                            "Không tìm thấy sản phẩm bán chạy, sử dụng fallback"
                        )
                        loadFallbackBestsellers()
                    }
                } else {
                    // Nếu không có danh sách sản phẩm, tải trực tiếp từ Firebase
                    Log.d(
                        "ProductViewModel",
                        "Không có danh sách sản phẩm, tải trực tiếp từ Firebase"
                    )

                    try {
                        val productSnapshots = productsRef.get().await()
                        val products = mutableListOf<Product>()

                        for (productSnapshot in productSnapshots.children) {
                            val product = Product(
                                id = productSnapshot.key ?: "",
                                name = productSnapshot.child("name").getValue(String::class.java)
                                    ?: "",
                                price = productSnapshot.child("price").getValue(Long::class.java)
                                    ?.toDouble() ?: 0.0,
                                description = productSnapshot.child("description")
                                    .getValue(String::class.java) ?: "",
                                imageUrl = productSnapshot.child("imageUrl")
                                    .getValue(String::class.java) ?: "",
                                discountPercent = productSnapshot.child("discountPercent")
                                    .getValue(Int::class.java) ?: 0
                            )
                            products.add(product)
                        }

                        // Cập nhật danh sách sản phẩm
                        _products.value = products

                        // Tìm sản phẩm bestseller
                        val bestsellers = bestsellerProductIds.mapNotNull { productId ->
                            products.find { it.id == productId }
                        }

                        if (bestsellers.isNotEmpty()) {
                            _bestsellerProducts.value = bestsellers
                            Log.d(
                                "ProductViewModel",
                                "Đã tìm thấy ${bestsellers.size} sản phẩm bán chạy"
                            )
                        } else {
                            loadFallbackBestsellers()
                        }
                    } catch (e: Exception) {
                        Log.e("ProductViewModel", "Lỗi khi tải sản phẩm trực tiếp: ${e.message}", e)
                        loadFallbackBestsellers()
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Lỗi khi tải sản phẩm bán chạy: ${e.message}", e)
                loadFallbackBestsellers()
            }
        }
    }

    // Tải sản phẩm bán chạy fallback khi không có dữ liệu thực
    private fun loadFallbackBestsellers() {
        viewModelScope.launch {
            try {
                Log.d("ProductViewModel", "Tải fallback bestsellers")
                val currentProducts = _products.value

                if (currentProducts.isNotEmpty()) {
                    // Sử dụng 5 sản phẩm đầu tiên có giảm giá cao nhất làm bestseller
                    val fallbackBestsellers = currentProducts
                        .sortedByDescending { it.discountPercent }
                        .take(5)

                    _bestsellerProducts.value = fallbackBestsellers
                    Log.d(
                        "ProductViewModel",
                        "Đã tải ${fallbackBestsellers.size} sản phẩm fallback"
                    )
                } else {
                    // Tải trực tiếp từ repository nếu không có sản phẩm
                    repository.getRandomProducts(
                        limit = 5,
                        onSuccess = { randomProducts ->
                            _bestsellerProducts.value = randomProducts
                            Log.d(
                                "ProductViewModel",
                                "Đã tải ${randomProducts.size} sản phẩm ngẫu nhiên làm bestseller"
                            )
                        },
                        onError = { exception ->
                            Log.e(
                                "ProductViewModel",
                                "Lỗi khi tải sản phẩm ngẫu nhiên cho bestseller: ${exception.message}"
                            )
                            _bestsellerProducts.value = emptyList()
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Lỗi khi tải fallback bestsellers: ${e.message}", e)
                _bestsellerProducts.value = emptyList()
            }
        }
    }

    // Xóa thông báo lỗi
    fun clearError() {
        _error.value = null
    }
}