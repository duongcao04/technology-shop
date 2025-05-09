package com.example.techshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techshop.models.Cart
import com.example.techshop.models.Order
import com.example.techshop.repositories.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {

    // StateFlow cho danh sách đơn hàng
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // StateFlow cho chi tiết đơn hàng
    private val _orderDetails = MutableStateFlow<Order?>(null)
    val orderDetails: StateFlow<Order?> = _orderDetails.asStateFlow()

    // StateFlow cho trạng thái loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // StateFlow cho thông báo lỗi
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // StateFlow cho thông báo thành công
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // StateFlow cho ID đơn hàng mới đặt thành công
    private val _lastOrderId = MutableStateFlow<String?>(null)
    val lastOrderId: StateFlow<String?> = _lastOrderId.asStateFlow()

    // Đặt hàng mới với bảo vệ khỏi race conditions
    fun placeOrder(cart: Cart, name: String, address: String, phone: String) {
        // Đảm bảo không có nhiều yêu cầu đặt hàng cùng lúc
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Tạo bản sao của cart để tránh các vấn đề khi cart bị xóa
                val cartCopy = Cart(
                    id = cart.id,
                    userId = cart.userId,
                    items = HashMap(cart.items),
                    totalAmount = cart.totalAmount,
                    itemCount = cart.itemCount
                )

                val result = orderRepository.placeOrder(cartCopy, name, address, phone)

                if (result.isSuccess) {
                    val order = result.getOrNull()
                    _successMessage.value = "Đặt hàng thành công"
                    _lastOrderId.value = order?.id // Lưu ID đơn hàng mới
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Đặt hàng thất bại"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Đặt hàng thất bại"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Lấy danh sách đơn hàng của người dùng
    fun getUserOrders(userId: String) {
        if (userId.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            orderRepository.getUserOrders(userId)
                .catch { e ->
                    _errorMessage.value = e.message ?: "Không thể tải danh sách đơn hàng"
                    _isLoading.value = false
                }
                .collect { ordersList ->
                    _orders.value = ordersList
                    _isLoading.value = false
                }
        }
    }

    // Lấy chi tiết đơn hàng
    fun getOrderDetails(orderId: String) {
        if (orderId.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            orderRepository.getOrderDetails(orderId)
                .catch { e ->
                    _errorMessage.value = e.message ?: "Không thể tải thông tin đơn hàng"
                    _isLoading.value = false
                }
                .collect { order ->
                    _orderDetails.value = order
                    _isLoading.value = false
                }
        }
    }

    // Cập nhật trạng thái đơn hàng
    fun updateOrderStatus(orderId: String, status: String) {
        if (orderId.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = orderRepository.updateOrderStatus(orderId, status)
                if (result.isSuccess) {
                    _successMessage.value = "Cập nhật trạng thái thành công"
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Cập nhật trạng thái thất bại"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Cập nhật trạng thái thất bại"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Xóa thông báo lỗi
    fun clearError() {
        _errorMessage.value = null
    }

    // Xóa thông báo thành công
    fun clearSuccess() {
        _successMessage.value = null
    }

    // Xóa ID đơn hàng đã đặt
    fun clearLastOrderId() {
        _lastOrderId.value = null
    }
}