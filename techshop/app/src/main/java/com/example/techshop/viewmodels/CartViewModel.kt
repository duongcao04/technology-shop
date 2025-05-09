package com.example.techshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techshop.models.Cart
import com.example.techshop.models.Product
import com.example.techshop.repositorys.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val cartRepository = CartRepository()

// Trạng thái dũ liệu giỏ hàng
    private val _cart = MutableStateFlow<Cart?>(null)
    val cart: StateFlow<Cart?> = _cart
// trạng thái load dữ liệu
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
// trang thái báo lỗi
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    // Khởi tạo và theo dõi giỏ hàng
    fun initCart(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // Đăng ký lắng nghe thay đổi giỏ hàng theo thời gian thực
                cartRepository.getCartFlow(userId).collectLatest { updatedCart ->
                    _cart.value = updatedCart
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Không thể tải giỏ hàng: ${e.message}"
                _isLoading.value = false
            }
        }
    }

// Thêm sản phẩm vào giỏ hàng với các tham số
fun addProductToCart(userId: String, product: Product, quantity: Int = 1) {
    viewModelScope.launch {
        try {
            _isLoading.value = true
            cartRepository.addProductToCart(userId, product, quantity)
            _isLoading.value = false
        } catch (e: Exception) {
            _errorMessage.value = "Không thể thêm sản phẩm: ${e.message}"
            _isLoading.value = false
        }
    }
}
    // Cập nhật số lượng sản phẩm
    fun updateItemQuantity(userId: String, productId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                cartRepository.updateCartItemQuantity(userId, productId, quantity)
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Không thể cập nhật số lượng: ${e.message}"
                _isLoading.value = false
            }
        }
    }


    // Xóa sản phẩm khỏi giỏ hàng
    fun removeItemFromCart(userId: String, productId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                cartRepository.removeItemFromCart(userId, productId)
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Không thể xóa sản phẩm: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Tăng số lượng sản phẩm
    fun increaseItemQuantity(userId: String, productId: String) {
        val currentCart = _cart.value ?: return
        val item = currentCart.items[productId] ?: return
        updateItemQuantity(userId, productId, item.quantity + 1)
    }

    // Giảm số lượng sản phẩm
    fun decreaseItemQuantity(userId: String, productId: String) {
        val currentCart = _cart.value ?: return
        val item = currentCart.items[productId] ?: return

        if (item.quantity > 1) {
            updateItemQuantity(userId, productId, item.quantity - 1)
        } else {
            removeItemFromCart(userId, productId)
        }
    }

    // Xóa toàn bộ giỏ hàng
    fun clearCart(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                cartRepository.clearCart(userId)
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Không thể xóa giỏ hàng: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Xóa thông báo lỗi
    fun clearError() {
        _errorMessage.value = null
    }
}