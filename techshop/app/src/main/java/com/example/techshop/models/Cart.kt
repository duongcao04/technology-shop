package com.example.techshop.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Cart(
    var id: String = "",
    var userId: String = "",
    var totalAmount: Double = 0.0,
    var itemCount: Int = 0,
    var items: MutableMap<String, CartItem> = mutableMapOf(),
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
) {
    // Constructor không tham số (cần thiết cho Firebase)
    constructor() : this("", "", 0.0, 0, mutableMapOf(), 0L, 0L)

    // Phương thức loại trừ khỏi dữ liệu Firebase khi lưu
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "totalAmount" to totalAmount,
            "itemCount" to itemCount,
            "items" to items,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }

    // Cập nhật tổng số tiền và số lượng sản phẩm trong giỏ hàng
    fun updateTotals() {
        var sum = 0.0
        var count = 0

        items.values.forEach { cartItem ->
            sum += cartItem.totalPrice
            count += cartItem.quantity
        }

        totalAmount = sum
        itemCount = count
        updatedAt = System.currentTimeMillis()
    }

    // Hàm định dạng tổng giá thành chuỗi VND
    @Exclude
    fun getFormattedTotalAmount(): String {
        return String.format("%,.0f₫", totalAmount)
    }
}

@IgnoreExtraProperties
data class CartItem(
    var id: String = "",
    var productId: String = "",
    var productName: String = "",
    var productImage: String = "",
    var price: Double = 0.0,
    var quantity: Int = 0,
    var totalPrice: Double = 0.0,
) {
    // Constructor không tham số (cần thiết cho Firebase)
    constructor() : this("", "", "", "", 0.0, 0, 0.0)

    // Cập nhật tổng giá của item khi thay đổi số lượng
    fun updateTotalPrice() {
        totalPrice = price * quantity
    }

    // Tạo CartItem từ Product
    companion object {
        fun fromProduct(product: Product, quantity: Int = 1): CartItem {
            val cartItem = CartItem(
                id = product.id,
                productId = product.id,
                productName = product.name,
                productImage = product.imageUrl,
                price = product.getPriceAfterDiscount(),
                quantity = quantity,
            )
            cartItem.updateTotalPrice()
            return cartItem
        }
    }

    // Hàm định dạng giá thành chuỗi VND
    @Exclude
    fun getFormattedPrice(): String {
        return String.format("%,.0f₫", price)
    }

    // Hàm định dạng tổng giá thành chuỗi VND
    @Exclude
    fun getFormattedTotalPrice(): String {
        return String.format("%,.0f₫", totalPrice)
    }
}