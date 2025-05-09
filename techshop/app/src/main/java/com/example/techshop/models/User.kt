package com.example.techshop.models

import com.google.firebase.auth.FirebaseUser

data class User(
    var uid: String = "",
    var displayName: String? = null,
    var email: String? = null,
    var photoUrl: String? = null,
    var phoneNumber: String? = null,
    var creationTimestamp: Long? = null,
) {
    companion object {
        fun fromFirebaseUser(firebaseUser: FirebaseUser): User {
            return User(
                uid = firebaseUser.uid,
                displayName = firebaseUser.displayName ?: "No Name",
                email = firebaseUser.email ?: "No Email",
                photoUrl = firebaseUser.photoUrl?.toString() ?: "null",
                phoneNumber = firebaseUser.phoneNumber ?: "null",
                creationTimestamp = firebaseUser.metadata?.creationTimestamp
            )
        }
    }
}
