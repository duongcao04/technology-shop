package com.example.techshop.repositorys

import com.example.techshop.models.Cart
import com.example.techshop.models.CartItem
import com.example.techshop.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CartRepository
{
    private val database = FirebaseDatabase.getInstance()
    private val cartsRef = database.getReference("carts")
    ///
// lấy giỏ hàng của người dùng thông qua user id
    suspend fun getCartForUser(userId: String): Cart {
        val cartSnapshot = cartsRef.child(userId).get().await()
        return if (cartSnapshot.exists()) {
            cartSnapshot.getValue(Cart::class.java) ?: createNewCart(userId)
        } else {
            createNewCart(userId)
        }
    }
    // Lắng nghe thay đổi giỏ hàng (realtime)
    fun getCartFlow(userId: String): Flow<Cart> = callbackFlow {
        val cartListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cart = snapshot.getValue(Cart::class.java) ?: Cart(id = userId, userId = userId)
                trySend(cart)
            }
            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi
            }
        }
        val cartRef = cartsRef.child(userId)
        cartRef.addValueEventListener(cartListener)
        // Khi Flow bị hủy, gỡ bỏ listener
        awaitClose {
            cartRef.removeEventListener(cartListener)
        }
    }
    // Tạo giỏ hàng mới  với user người dùng
    private fun createNewCart(userId: String): Cart {
        val newCart = Cart(id = userId, userId = userId)
        cartsRef.child(userId).setValue(newCart)
        return newCart
    }

    // thêm sản phẩm vào giỏ hàng với userid và product id với mỗi lần thể là +1 quan tity
    suspend fun addProductToCart(userId: String, product: Product, quantity: Int = 1) {
        val cart = getCartForUser(userId)
        val cartItemId = product.id
        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        val existingItem = cart.items[cartItemId]
        if (existingItem != null) {
            // Cập nhật số lượng nếu sản phẩm đã có trong giỏ
            existingItem.quantity += quantity
            existingItem.updateTotalPrice()
        } else {
            // Thêm sản phẩm mới vào giỏ
            val newItem = CartItem.fromProduct(product, quantity)
            cart.items[cartItemId] = newItem
        }
        // Cập nhật tổng cộng
        cart.updateTotals()
        // Lưu vào Firebase
        cartsRef.child(userId).setValue(cart)
    }
    // Cập nhật số lượng sản phẩm
    suspend fun updateCartItemQuantity(userId: String, productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeItemFromCart(userId, productId)
            return
        }

        val cart = getCartForUser(userId)
        val cartItem = cart.items[productId]

        if (cartItem != null) {
            cartItem.quantity = quantity
            cartItem.updateTotalPrice()
            cart.updateTotals()

            // Lưu vào Firebase
            cartsRef.child(userId).setValue(cart)
        }
    }

    // Xóa sản phẩm khỏi giỏ hàng
    suspend fun removeItemFromCart(userId: String, productId: String) {
        val cart = getCartForUser(userId)

        if (cart.items.containsKey(productId)) {
            cart.items.remove(productId)
            cart.updateTotals()

            // Lưu vào Firebase
            cartsRef.child(userId).setValue(cart)
        }
    }

    // Xóa toàn bộ giỏ hàng
    suspend fun clearCart(userId: String) {
        val cart = getCartForUser(userId)
        cart.items.clear()
        cart.updateTotals()

        // Lưu vào Firebase
        cartsRef.child(userId).setValue(cart)
    }
}