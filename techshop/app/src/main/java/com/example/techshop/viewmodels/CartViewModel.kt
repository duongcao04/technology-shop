package com.example.techshop.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.techshop.models.Cart
import com.example.techshop.models.CartItem
import com.example.techshop.repositorys.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel(private val repository: CartRepository) : ViewModel() {

    // StateFlow cho giỏ hàng hiện tại
    private val _cart = MutableStateFlow<Cart?>(null)
    val cart: StateFlow<Cart?> = _cart

    // StateFlow cho trạng thái loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // StateFlow cho thông báo lỗi
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Hàm tải giỏ hàng của người dùng
    fun loadUserCart(userId: String) {
        _isLoading.value = true
        _error.value = null

        try {
            repository.getCartByUserId(
                userId = userId,
                onSuccess = { userCart ->
                    Log.d("CartViewModel", "Cart loaded: ${userCart?.items?.size ?: 0} items")
                    _cart.value = userCart
                    _isLoading.value = false
                },
                onError = { exception ->
                    Log.e("CartViewModel", "Error loading cart", exception)
                    _error.value = "Lỗi khi tải giỏ hàng: ${exception.message}"
                    _isLoading.value = false
                    _cart.value = null
                }
            )
        } catch (e: Exception) {
            Log.e("CartViewModel", "Exception in loadUserCart", e)
            _error.value = "Lỗi không xác định: ${e.message}"
            _isLoading.value = false
            _cart.value = null
        }
    }

    // Hàm thêm sản phẩm vào giỏ hàng
    fun addToCart(userId: String, productId: String, quantity: Int) {
        _isLoading.value = true
        _error.value = null

        Log.d("CartViewModel", "Adding product $productId to cart, quantity: $quantity")

        val cartItem = CartItem(productId, quantity)

        repository.addItemToCart(
            userId = userId,
            item = cartItem,
            onSuccess = { updatedCart ->
                Log.d("CartViewModel", "Product added to cart successfully")
                _cart.value = updatedCart
                _isLoading.value = false
            },
            onError = { exception ->
                Log.e("CartViewModel", "Error adding product to cart", exception)
                _error.value = "Lỗi khi thêm sản phẩm vào giỏ hàng: ${exception.message}"
                _isLoading.value = false
            }
        )
    }

    // Hàm cập nhật số lượng sản phẩm trong giỏ hàng
    fun updateCartItemQuantity(userId: String, productId: String, newQuantity: Int) {
        _isLoading.value = true
        _error.value = null

        Log.d("CartViewModel", "Updating quantity for product $productId to $newQuantity")

        repository.updateCartItemQuantity(
            userId = userId,
            productId = productId,
            newQuantity = newQuantity,
            onSuccess = { updatedCart ->
                Log.d("CartViewModel", "Quantity updated successfully")
                _cart.value = updatedCart
                _isLoading.value = false
            },
            onError = { exception ->
                Log.e("CartViewModel", "Error updating quantity", exception)
                _error.value = "Lỗi khi cập nhật số lượng sản phẩm: ${exception.message}"
                _isLoading.value = false
            }
        )
    }

    // Hàm xóa sản phẩm khỏi giỏ hàng
    fun removeFromCart(userId: String, productId: String) {
        _isLoading.value = true
        _error.value = null

        Log.d("CartViewModel", "Removing product $productId from cart")

        repository.removeItemFromCart(
            userId = userId,
            productId = productId,
            onSuccess = { updatedCart ->
                Log.d("CartViewModel", "Product removed successfully")
                _cart.value = updatedCart
                _isLoading.value = false
            },
            onError = { exception ->
                Log.e("CartViewModel", "Error removing product", exception)
                _error.value = "Lỗi khi xóa sản phẩm khỏi giỏ hàng: ${exception.message}"
                _isLoading.value = false
            }
        )
    }

    // Hàm xóa toàn bộ giỏ hàng
    fun clearCart(userId: String) {
        _isLoading.value = true
        _error.value = null

        Log.d("CartViewModel", "Clearing entire cart for user $userId")

        repository.clearCart(
            userId = userId,
            onSuccess = {
                Log.d("CartViewModel", "Cart cleared successfully")
                _cart.value = null
                _isLoading.value = false
            },
            onError = { exception ->
                Log.e("CartViewModel", "Error clearing cart", exception)
                _error.value = "Lỗi khi xóa giỏ hàng: ${exception.message}"
                _isLoading.value = false
            }
        )
    }

    // Hàm kiểm tra xem một sản phẩm có trong giỏ hàng hay không
    fun isProductInCart(productId: String): Boolean {
        val currentCart = _cart.value
        return currentCart?.items?.any { it.productId == productId } == true
    }

    // Hàm lấy số lượng một sản phẩm trong giỏ hàng
    fun getProductQuantity(productId: String): Int {
        val currentCart = _cart.value
        return currentCart?.items?.find { it.productId == productId }?.quantity ?: 0
    }

    // Hàm tính tổng số lượng sản phẩm trong giỏ hàng
    fun getCartItemCount(): Int {
        val currentCart = _cart.value
        return currentCart?.items?.sumOf { it.quantity } ?: 0
    }
}