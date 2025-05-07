package com.example.techshop.repositorys

import android.util.Log
import com.example.techshop.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val database: DatabaseReference
) {
    val getCurrentUser get() = auth.currentUser

    // Lưu thông tin người dùng vào Realtime Database
    suspend fun saveUser(user: User): Result<Unit> {
        return try {
            val userRef = database.child("users").child(user.uid)
            userRef.setValue(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "saveUser error", e)
            Result.failure(e)
        }
    }

    // Đăng nhập bằng Google
    suspend fun loginWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("User null"))

            val userRef = database.child("users").child(firebaseUser.uid)
            val snapshot = userRef.get().await()

            if (!snapshot.exists()) {
                val newUser = User.fromFirebaseUser(firebaseUser)
                saveUser(newUser)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "loginWithGoogle error", e)
            Result.failure(e)
        }
    }

    // Lấy thông tin người dùng từ UID
    suspend fun getUserData(uid: String): Result<User> {
        return try {
            val snapshot = database.child("users").child(uid).get().await()
            val user = snapshot.getValue(User::class.java)
                ?: throw Exception("User data not found")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "getUserData error", e)
            Result.failure(e)
        }
    }

    // Đăng xuất
    fun signOut() {
        auth.signOut()
    }
}
