package com.example.techshop.viewmodels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techshop.models.User
import com.example.techshop.repositorys.AuthRepository
import com.example.techshop.viewmodels.AuthViewModel.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class ProfileViewModel(private val authRepository: AuthRepository) : ViewModel() {
    // User data state
    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()
    // Current user data
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser
    // Initialize by loading current user
    init {
        loadCurrentUser()
    }
    fun loadCurrentUser() {
        val currentUserId = authRepository.getCurrentUser?.uid
        if (currentUserId != null) {
            _userState.value = UserState.Loading
            viewModelScope.launch {
                authRepository.getUserData(currentUserId)
                    .onSuccess { user ->
                        _currentUser.value = user
                        _userState.value = UserState.Success(user)
                    }
                    .onFailure { error ->
                        _userState.value = UserState.Error(error.message ?: "Unknown error")
                    }
            }
        } else {
            _userState.value = UserState.Error("User not authenticated")
        }
    }
//    // Đăng xuất
//    fun signOut() {
//        viewModelScope.launch {
//            try {
//                authRepository.signOut()
//                _userState.value = UserState.SignedOut
//            } catch (e: Exception) {
//
//            }
//        }
//    }

    // State for user data operations
    sealed class UserState {
        object Loading : UserState()
        data class Success(val user: User) : UserState()
        data class Error(val message: String) : UserState()
        object SignedOut : UserState()
    }
}