package com.example.techshop.models

data class Product(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var imageUrl: String = "",
    var discountPercent: Int = 0
) {
    // Constructor không tham số (cần thiết cho Firebase)
    constructor() : this("", "", "", 0.0, "", 0)

    // Hàm tính giá sau khi giảm giá
    fun getPriceAfterDiscount(): Double {
        return price * (1 - discountPercent / 100.0)
    }

    // Hàm định dạng giá thành chuỗi VND
    fun getFormattedPrice(): String {
        return String.format("%,.0f₫", price)
    }

    // Hàm định dạng giá sau giảm giá
    fun getFormattedDiscountPrice(): String {
        return String.format("%,.0f₫", getPriceAfterDiscount())
    }
}