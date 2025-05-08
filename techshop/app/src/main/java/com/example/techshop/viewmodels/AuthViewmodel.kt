package com.example.techshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techshop.repositorys.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val cartViewModel: CartViewModel
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> get() = _authState

    //
    val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid
    // Lưu giữ userId hiện tại



    fun loginWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.loginWithGoogle(idToken)
            if (result.isSuccess) {
                // Khi đăng nhập thành công, lấy userId và khởi tạo giỏ hàng
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    cartViewModel.initCart(userId)
                }
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    // trạng thái login
    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
