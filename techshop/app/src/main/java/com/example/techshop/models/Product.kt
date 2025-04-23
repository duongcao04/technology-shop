package com.example.techshop.models

data class Product(
    var id: String = "",
    var name: String? = null,
    var description: String? = null,
    var price: String? = null,
    var image: String? = null,
    var rating: Double = 4.5,
    var reviewCount: Int = 0,
    var discountPercent: Int = 0,
    var creationTimestamp: Long? = null,
)