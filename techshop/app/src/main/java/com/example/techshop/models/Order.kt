package com.example.techshop.models

data class Order(
    val id: Int,
    val items: List<OrderItem>,
    val userId: String,
    val totalAmount: Number,
    val status: String,
    val address: String,
    val phoneNumber: String,
    val paymentStatus: String,
    val shippingFee: String,
    val subTotal: String,
    val createdAt: String,
) {
}

data class OrderItem(
    val productId: String,
    val quantity: Int,
) {}
