package com.example.techshop.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@IgnoreExtraProperties
data class Order(
    var id: String = "",
    var userId: String = "",
    var orderDate: Long = System.currentTimeMillis(),
    var totalPrice: Double = 0.0,
    var status: String = STATUS_PENDING,
    var name: String = "",
    var address: String = "",
    var phone: String = "",
    var paymentMethod: String = PAYMENT_COD,
    var items: MutableMap<String, OrderItem> = mutableMapOf()
) {
    // Constructor không tham số (cần thiết cho Firebase)
    constructor() : this("", "", 0L, 0.0, "", "", "", "", "", mutableMapOf())

    companion object {
        const val STATUS_PENDING = "Đang giao"
        const val STATUS_DELIVERED = "Đã giao"
        const val PAYMENT_COD = "Thanh toán khi nhận hàng"

        // Tạo Order từ Cart
        fun fromCart(cart: Cart, name: String, address: String, phone: String): Order {
            val order = Order(
                id = "", // ID sẽ được tạo bởi Firebase
                userId = cart.userId,
                orderDate = System.currentTimeMillis(),
                totalPrice = cart.totalAmount,
                status = STATUS_PENDING,
                name = name,
                address = address,
                phone = phone,
                paymentMethod = PAYMENT_COD
            )

            // Chuyển đổi CartItem thành OrderItem
            cart.items.forEach { (key, cartItem) ->
                val orderItem = OrderItem.fromCartItem(cartItem)
                order.items[key] = orderItem
            }

            return order
        }
    }

    // Phương thức loại trừ khỏi dữ liệu Firebase khi lưu
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "orderDate" to orderDate,
            "totalPrice" to totalPrice,
            "status" to status,
            "name" to name,
            "address" to address,
            "phone" to phone,
            "paymentMethod" to paymentMethod,
            "items" to items
        )
    }

    // Hàm định dạng giá thành chuỗi VND
    @Exclude
    fun getFormattedTotalPrice(): String {
        return String.format("%,.0f₫", totalPrice)
    }

    // Hàm định dạng ngày đặt hàng
    @Exclude
    fun getFormattedOrderDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(orderDate))
    }
}

@IgnoreExtraProperties
data class OrderItem(
    var id: String = "",
    var orderId: String = "",
    var productId: String = "",
    var productName: String = "",
    var productImage: String = "",
    var quantity: Int = 0,
    var price: Double = 0.0,
    var subtotal: Double = 0.0
) {
    // Constructor không tham số (cần thiết cho Firebase)
    constructor() : this("", "", "", "", "", 0, 0.0, 0.0)

    companion object {
        // Tạo OrderItem từ CartItem
        fun fromCartItem(cartItem: CartItem): OrderItem {
            return OrderItem(
                id = cartItem.id,
                orderId = "", // Sẽ được gán sau khi tạo Order
                productId = cartItem.productId,
                productName = cartItem.productName,
                productImage = cartItem.productImage,
                quantity = cartItem.quantity,
                price = cartItem.price,
                subtotal = cartItem.totalPrice
            )
        }
    }

    // Hàm định dạng giá thành chuỗi VND
    @Exclude
    fun getFormattedPrice(): String {
        return String.format("%,.0f₫", price)
    }

    // Hàm định dạng tổng giá thành chuỗi VND
    @Exclude
    fun getFormattedSubtotal(): String {
        return String.format("%,.0f₫", subtotal)
    }
}