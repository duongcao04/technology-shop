package com.example.techshop.models

data class Cart(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val totalAmount: Number,
) {}

data class CartItem(
    val productId: String,
    val quantity: Int,
) {}
