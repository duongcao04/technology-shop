package com.example.techshop.repositorys

import com.example.techshop.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val database: DatabaseReference
) {
    // hàm lưu thông tin tài khảon vào realyime database
    suspend fun saveUser(user: User): Result<Unit> {
        return try {
            val userRef = database.child("users").child(user.uid) // uid làm key
            userRef.setValue(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // hàm login với tài khoản Google dùng firebase
    suspend fun loginWithGoogle(idToken: String): Result<Unit>{
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user ?: return Result.failure(Exception("User null"))
            val uid = user.uid
            val name = user.displayName ?: "No Name"
            val email = user.email ?: "No Email"
            val photoUrl = user.photoUrl?.toString() ?: "null"
            val phoneNumber = user.phoneNumber ?: "null"
            val creationTimestamp = user.metadata?.creationTimestamp
            val userRef = database.child("users").child(uid)
            val snapshot = userRef.get().await()
            if (!snapshot.exists()) {
                val newUser = User(uid, name, email, photoUrl, phoneNumber, creationTimestamp)
                saveUser(newUser)
            }
            Result.success(Unit)
        }catch ( e: Exception){
            Result.failure(e)
        }
    }
}