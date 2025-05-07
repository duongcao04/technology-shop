package com.example.techshop.viewmodels

import androidx.lifecycle.ViewModel
import com.example.techshop.models.Product
import com.example.techshop.repositorys.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    // StateFlow cho danh sách sản phẩm
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    // StateFlow cho sản phẩm được chọn
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct

    // StateFlow cho trạng thái loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // StateFlow cho thông báo lỗi
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Hàm tải tất cả sản phẩm
    fun loadAllProducts() {
        _isLoading.value = true
        _error.value = null

        try {
            repository.getAllProducts(
                onSuccess = { productList ->
                    _products.value = productList
                    _isLoading.value = false
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

    // Hàm tải chi tiết sản phẩm theo ID
    fun loadProductDetails(productId: String) {
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