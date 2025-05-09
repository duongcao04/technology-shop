package com.example.techshop.repositories

import com.example.techshop.models.Cart
import com.example.techshop.models.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OrderRepository {
    private val database = FirebaseDatabase.getInstance()
    private val ordersRef = database.getReference("orders")
    private val cartsRef = database.getReference("carts")

    // Tạo đơn hàng mới từ giỏ hàng và trả về đối tượng Order hoàn chỉnh
    suspend fun placeOrder(cart: Cart, name: String, address: String, phone: String): Result<Order> {
        return try {
            // Tạo order từ cart
            val order = Order.fromCart(cart, name, address, phone)

            // Tạo ID mới cho đơn hàng
            val orderKey = ordersRef.push().key ?: throw Exception("Không thể tạo ID đơn hàng")
            order.id = orderKey

            // Cập nhật orderId cho tất cả các orderItems
            order.items.values.forEach { item ->
                item.orderId = orderKey
            }

            // Lưu đơn hàng vào database
            ordersRef.child(orderKey).setValue(order).await()

            // Quan trọng: Tạo bản sao của giỏ hàng để tránh xung đột
            val cartId = cart.id

            // Xóa giỏ hàng sau khi đặt hàng thành công
            // Nhưng chỉ nếu ID hợp lệ
            if (cartId.isNotEmpty()) {
                cartsRef.child(cartId).removeValue().await()
            }

            // Trả về đơn hàng đã được tạo với ID
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lấy danh sách đơn hàng của một người dùng
    fun getUserOrders(userId: String): Flow<List<Order>> = callbackFlow {
        val listener = ordersRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val orders = mutableListOf<Order>()
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(Order::class.java)
                            order?.let { orders.add(it) }
                        }
                        // Sắp xếp theo thời gian đặt hàng, mới nhất lên đầu
                        orders.sortByDescending { it.orderDate }
                        trySend(orders)
                    } catch (e: Exception) {
                        close(e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose {
            try {
                ordersRef.orderByChild("userId").equalTo(userId).removeEventListener(listener)
            } catch (e: Exception) {
                // Xử lý trường hợp lỗi khi hủy listener
            }
        }
    }

    // Lấy thông tin chi tiết của một đơn hàng
    fun getOrderDetails(orderId: String): Flow<Order?> = callbackFlow {
        val listener = ordersRef.child(orderId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val order = snapshot.getValue(Order::class.java)
                        trySend(order)
                    } catch (e: Exception) {
                        close(e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose {
            try {
                ordersRef.child(orderId).removeEventListener(listener)
            } catch (e: Exception) {
                // Xử lý trường hợp lỗi khi hủy listener
            }
        }
    }

    // Cập nhật trạng thái đơn hàng
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            ordersRef.child(orderId).child("status").setValue(status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}