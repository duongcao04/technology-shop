package com.example.techshop.repositorys

import android.util.Log
import com.example.techshop.models.Cart
import com.example.techshop.models.CartItem
import com.example.techshop.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CartRepository(
    private val auth: FirebaseAuth,
    private val database: DatabaseReference,
    private val productRepository: ProductRepository? = null
) {
    private val TAG = "CartRepository"
    private val cartsRef = database.child("carts")

    // Lấy giỏ hàng theo ID người dùng với Coroutines
    suspend fun getCartByUserIdAsync(userId: String): Result<Cart?> =
        suspendCoroutine { continuation ->
            try {
                cartsRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            if (snapshot.exists()) {
                                val cartMap = snapshot.value as? HashMap<*, *>

                                if (cartMap != null) {
                                    val cartId = snapshot.key ?: ""
                                    val items = mutableListOf<CartItem>()

                                    // Xử lý danh sách các mục trong giỏ hàng
                                    val itemsMap = cartMap["items"] as? HashMap<*, *>

                                    if (itemsMap != null) {
                                        for ((key, value) in itemsMap) {
                                            val productId = key.toString()
                                            val itemData = value as? HashMap<*, *>

                                            if (itemData != null) {
                                                val quantity = getIntValue(itemData["quantity"])
                                                items.add(CartItem(productId, quantity))
                                            }
                                        }
                                    }

                                    val totalAmount = getDoubleValue(cartMap["totalAmount"])

                                    val cart = Cart(
                                        id = cartId,
                                        userId = userId,
                                        items = items,
                                        totalAmount = totalAmount
                                    )

                                    continuation.resume(Result.success(cart))
                                } else {
                                    continuation.resume(Result.success(null))
                                }
                            } else {
                                continuation.resume(Result.success(null))
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Lỗi khi xử lý giỏ hàng: ${e.message}")
                            continuation.resumeWithException(e)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }
                })
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }

    // Lấy giỏ hàng theo ID người dùng
    fun getCartByUserId(userId: String, onSuccess: (Cart?) -> Unit, onError: (Exception) -> Unit) {
        try {
            cartsRef.orderByChild("userId").equalTo(userId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            if (snapshot.exists()) {
                                val cartMap = snapshot.value as? HashMap<*, *>

                                if (cartMap != null) {
                                    // lấy key của object
                                    val cartId = snapshot.value ?: ""
                                    val items = mutableListOf<CartItem>()

                                    // Xử lý danh sách các mục trong giỏ hàng
                                    val itemsMap = cartMap["items"] as? HashMap<*, *>

                                    if (itemsMap != null) {
                                        for ((key, value) in itemsMap) {
                                            val productId = key.toString()
                                            val itemData = value as? HashMap<*, *>

                                            if (itemData != null) {
                                                val quantity = getIntValue(itemData["quantity"])
                                                items.add(CartItem(productId, quantity))
                                            }
                                        }
                                    }

                                    val totalAmount = getDoubleValue(cartMap["totalAmount"])

                                    val cart = Cart(
                                        id = cartId,
                                        userId = userId,
                                        items = items,
                                        totalAmount = totalAmount
                                    )
                                    Log.d(TAG, snapshot.toString())
                                    Log.d(TAG, cartId.toString())
                                    Log.d(TAG, cartMap.toString())
                                    Log.d(TAG, cart.toString())
                                    onSuccess(cart)
                                } else {
                                    onSuccess(null)
                                }
                            } else {
                                onSuccess(null)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Lỗi khi xử lý giỏ hàng: ${e.message}")
                            onError(e)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onError(error.toException())
                    }
                })
        } catch (e: Exception) {
            onError(e)
        }
    }

    // Thêm sản phẩm vào giỏ hàng
    fun addItemToCart(
        userId: String,
        item: CartItem,
        onSuccess: (Cart) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            // Đầu tiên lấy giỏ hàng hiện tại
            cartsRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val cartExists = snapshot.exists()
                        val currentItems = mutableListOf<CartItem>()
                        var totalAmount = 0.0

                        // Nếu giỏ hàng đã tồn tại, lấy các mục hiện có
                        if (cartExists) {
                            val cartMap = snapshot.value as? HashMap<*, *>

                            if (cartMap != null) {
                                val itemsMap = cartMap["items"] as? HashMap<*, *>

                                if (itemsMap != null) {
                                    for ((key, value) in itemsMap) {
                                        val productId = key.toString()
                                        val itemData = value as? HashMap<*, *>

                                        if (itemData != null) {
                                            val quantity = getIntValue(itemData["quantity"])
                                            currentItems.add(CartItem(productId, quantity))
                                        }
                                    }
                                }

                                totalAmount = getDoubleValue(cartMap["totalAmount"])
                            }
                        }

                        // Kiểm tra và cập nhật số lượng nếu sản phẩm đã tồn tại
                        var itemExists = false
                        for (i in currentItems.indices) {
                            if (currentItems[i].productId == item.productId) {
                                val updatedQuantity = currentItems[i].quantity + item.quantity
                                currentItems[i] = CartItem(item.productId, updatedQuantity)
                                itemExists = true
                                break
                            }
                        }

                        // Nếu sản phẩm chưa tồn tại, thêm mới
                        if (!itemExists) {
                            currentItems.add(item)
                        }

                        // Tính lại tổng tiền
                        calculateTotalAmount(userId, currentItems, totalAmount) { newTotalAmount ->
                            // Cập nhật giỏ hàng vào database
                            updateCartInDatabase(
                                userId,
                                currentItems,
                                newTotalAmount,
                                onSuccess,
                                onError
                            )
                        }
                    } catch (e: Exception) {
                        onError(e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
        } catch (e: Exception) {
            onError(e)
        }
    }

    // Cập nhật số lượng sản phẩm
    fun updateCartItemQuantity(
        userId: String,
        productId: String,
        newQuantity: Int,
        onSuccess: (Cart) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            cartsRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (snapshot.exists()) {
                            val cartMap = snapshot.value as? HashMap<*, *>

                            if (cartMap != null) {
                                val currentItems = mutableListOf<CartItem>()
                                val itemsMap = cartMap["items"] as? HashMap<*, *>

                                if (itemsMap != null) {
                                    for ((key, value) in itemsMap) {
                                        val itemProductId = key.toString()
                                        val itemData = value as? HashMap<*, *>

                                        if (itemData != null) {
                                            val quantity = if (itemProductId == productId) {
                                                newQuantity
                                            } else {
                                                getIntValue(itemData["quantity"])
                                            }

                                            // Chỉ thêm các mục có số lượng > 0
                                            if (quantity > 0) {
                                                currentItems.add(CartItem(itemProductId, quantity))
                                            }
                                        }
                                    }
                                }

                                val totalAmount = getDoubleValue(cartMap["totalAmount"])

                                // Tính lại tổng tiền
                                calculateTotalAmount(
                                    userId,
                                    currentItems,
                                    totalAmount
                                ) { newTotalAmount ->
                                    // Cập nhật giỏ hàng vào database
                                    updateCartInDatabase(
                                        userId,
                                        currentItems,
                                        newTotalAmount,
                                        onSuccess,
                                        onError
                                    )
                                }
                            } else {
                                onError(Exception("Giỏ hàng không tồn tại"))
                            }
                        } else {
                            onError(Exception("Giỏ hàng không tồn tại"))
                        }
                    } catch (e: Exception) {
                        onError(e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
        } catch (e: Exception) {
            onError(e)
        }
    }

    // Xóa sản phẩm khỏi giỏ hàng
    fun removeItemFromCart(
        userId: String,
        productId: String,
        onSuccess: (Cart) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            cartsRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (snapshot.exists()) {
                            val cartMap = snapshot.value as? HashMap<*, *>

                            if (cartMap != null) {
                                val currentItems = mutableListOf<CartItem>()
                                val itemsMap = cartMap["items"] as? HashMap<*, *>

                                if (itemsMap != null) {
                                    for ((key, value) in itemsMap) {
                                        val itemProductId = key.toString()
                                        val itemData = value as? HashMap<*, *>

                                        // Chỉ thêm các sản phẩm khác productId cần xóa
                                        if (itemData != null && itemProductId != productId) {
                                            val quantity = getIntValue(itemData["quantity"])
                                            currentItems.add(CartItem(itemProductId, quantity))
                                        }
                                    }
                                }

                                val totalAmount = getDoubleValue(cartMap["totalAmount"])

                                // Tính lại tổng tiền
                                calculateTotalAmount(
                                    userId,
                                    currentItems,
                                    totalAmount
                                ) { newTotalAmount ->
                                    // Cập nhật giỏ hàng vào database
                                    updateCartInDatabase(
                                        userId,
                                        currentItems,
                                        newTotalAmount,
                                        onSuccess,
                                        onError
                                    )
                                }
                            } else {
                                onError(Exception("Giỏ hàng không tồn tại"))
                            }
                        } else {
                            onError(Exception("Giỏ hàng không tồn tại"))
                        }
                    } catch (e: Exception) {
                        onError(e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
        } catch (e: Exception) {
            onError(e)
        }
    }

    // Xóa toàn bộ giỏ hàng
    fun clearCart(userId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        try {
            cartsRef.child(userId).removeValue()
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    // Hàm tính lại tổng tiền dựa trên giá sản phẩm hiện tại
    private fun calculateTotalAmount(
        userId: String,
        items: List<CartItem>,
        currentTotalAmount: Double,
        onComplete: (Double) -> Unit
    ) {
        // Nếu có productRepository, tính toán giá dựa trên sản phẩm thực tế
        if (productRepository != null && items.isNotEmpty()) {
            var calculatedTotal = 0.0
            var processedItems = 0

            for (item in items) {
                productRepository.getProductById(
                    item.productId,
                    onSuccess = { product ->
                        if (product != null) {
                            // Tính giá sau khi áp dụng giảm giá (nếu có)
                            val price = product.price
                            val discountedPrice = if (product.discountPercent > 0) {
                                price * (1 - product.discountPercent / 100.0)
                            } else {
                                price
                            }

                            calculatedTotal += discountedPrice * item.quantity
                        }

                        processedItems++
                        if (processedItems == items.size) {
                            onComplete(calculatedTotal)
                        }
                    },
                    onError = {
                        // Nếu không lấy được giá sản phẩm, sử dụng tổng tiền hiện tại
                        processedItems++
                        if (processedItems == items.size) {
                            onComplete(currentTotalAmount)
                        }
                    }
                )
            }
        } else {
            // Nếu không có productRepository, giữ nguyên tổng tiền
            onComplete(currentTotalAmount)
        }
    }

    // Hàm hỗ trợ cập nhật giỏ hàng vào database
    private fun updateCartInDatabase(
        userId: String,
        items: List<CartItem>,
        totalAmount: Double,
        onSuccess: (Cart) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            // Kiểm tra giỏ hàng trống
            if (items.isEmpty()) {
                // Nếu không còn sản phẩm nào, xóa giỏ hàng
                clearCart(
                    userId,
                    onSuccess = {
                        val emptyCart = Cart(
                            id = userId,
                            userId = userId,
                            items = emptyList(),
                            totalAmount = 0.0
                        )
                        onSuccess(emptyCart)
                    },
                    onError = onError
                )
                return
            }

            // Tạo map cho các mục trong giỏ hàng
            val itemsMap = HashMap<String, Any>()
            for (item in items) {
                val itemData = HashMap<String, Any>()
                itemData["quantity"] = item.quantity
                itemsMap[item.productId] = itemData
            }

            // Tạo map cho giỏ hàng
            val cartData = HashMap<String, Any>()
            cartData["userId"] = userId
            cartData["items"] = itemsMap
            cartData["totalAmount"] = totalAmount

            // Cập nhật vào database
            cartsRef.child(userId).setValue(cartData)
                .addOnSuccessListener {
                    val cart = Cart(
                        id = userId,
                        userId = userId,
                        items = items,
                        totalAmount = totalAmount
                    )
                    onSuccess(cart)
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    // Cập nhật tổng giá trị đơn hàng
    fun updateCartTotalAmount(
        userId: String,
        newTotalAmount: Double,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            cartsRef.child(userId).child("totalAmount").setValue(newTotalAmount)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    // Hàm hỗ trợ lấy giá trị Double từ dữ liệu Firebase
    private fun getDoubleValue(value: Any?): Double {
        return when (value) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    // Hàm hỗ trợ lấy giá trị Int từ dữ liệu Firebase
    private fun getIntValue(value: Any?): Int {
        return when (value) {
            is Number -> value.toInt()
            is String -> value.toIntOrNull() ?: 0
            else -> 0
        }
    }

    // Extension function để lấy giá thực tế của sản phẩm
    suspend fun Product.getActualPrice(): Double {
        return if (this.discountPercent > 0) {
            this.price * (1 - this.discountPercent / 100.0)
        } else {
            this.price
        }
    }
}