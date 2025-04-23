package com.example.techshop.models

data class User(
    var uid: String = "",
    var displayName: String? = null,
    var email: String? = null,
    var photoUrl: String? = null,
    var phoneNumber: String? = null,
    var creationTimestamp: Long? = null,
)