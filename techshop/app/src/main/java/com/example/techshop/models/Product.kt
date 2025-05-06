package com.example.techshop.models

import com.google.firebase.Timestamp

data class Product(
    var id: String = "",
    var name: String? = null,
    var description: String? = null,
    var price: Number? = null,
    var imageUrl: String? = null,
    var discountPercent: Number = 0,
    val createdAt: Timestamp = Timestamp.now(),  // Thời gian tạo sản phẩm
    val updatedAt: Timestamp = Timestamp.now()   // Thời gian cập nhật sản phẩm
)