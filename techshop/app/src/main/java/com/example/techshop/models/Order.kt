package com.example.techshop.models

data class OrderItem(
    val productId: String,
    val quantity: Int,
) {}

data class Order(
    val id: Int,
    val orderItem: List<OrderItem>,
    val userId: String,
    val totalAmount: Number,
    val status: String,
    val createdAt: String,
) {}
