package com.example.techshop.repositorys

import android.util.Log
import com.example.techshop.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ProductRepository(private val database: DatabaseReference) {
    private val TAG = "ProductRepository"
    private val productsRef = database.child("products")

    fun getAllProducts(onSuccess: (List<Product>) -> Unit, onError: (Exception) -> Unit) {
        try {
            productsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val productList = mutableListOf<Product>()

                    for (productSnapshot in snapshot.children) {
                        try {
                            val productId = productSnapshot.key ?: ""
                            val productMap = productSnapshot.value as? HashMap<*, *>

                            if (productMap != null) {
                                // Chuyển đổi từng trường dữ liệu
                                val name = productMap["name"] as? String ?: ""
                                val description = productMap["description"] as? String ?: ""
                                val price = getDoubleValue(productMap["price"])
                                val imageUrl = productMap["imageUrl"] as? String ?: ""
                                val discountPercent = getIntValue(productMap["discountPercent"])

                                // Tạo đối tượng Product
                                val product = Product(
                                    id = productId,
                                    name = name,
                                    description = description,
                                    price = price,
                                    imageUrl = imageUrl,
                                    discountPercent = discountPercent
                                )

                                productList.add(product)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Lỗi khi xử lý sản phẩm: ${e.message}")
                        }
                    }

                    onSuccess(productList)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun getProductById(productId: String, onSuccess: (Product?) -> Unit, onError: (Exception) -> Unit) {
        productsRef.child(productId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot.exists()) {
                        val productMap = snapshot.value as? HashMap<*, *>

                        if (productMap != null) {
                            // Chuyển đổi từng trường dữ liệu
                            val name = productMap["name"] as? String ?: ""
                            val description = productMap["description"] as? String ?: ""
                            val price = getDoubleValue(productMap["price"])
                            val imageUrl = productMap["imageUrl"] as? String ?: ""
                            val discountPercent = getIntValue(productMap["discountPercent"])

                            // Tạo đối tượng Product
                            val product = Product(
                                id = productId,
                                name = name,
                                description = description,
                                price = price,
                                imageUrl = imageUrl,
                                discountPercent = discountPercent
                            )

                            onSuccess(product)
                        } else {
                            onSuccess(null)
                        }
                    } else {
                        onSuccess(null)
                    }
                } catch (e: Exception) {
                    onError(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }
    // Hàm lấy 5 sản phẩm ngẫu nhiên
    fun getRandomProducts(limit: Int = 5, onSuccess: (List<Product>) -> Unit, onError: (Exception) -> Unit) {
        try {
            // Lấy tất cả sản phẩm trước
            productsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val allProducts = mutableListOf<Product>()

                    // Lấy tất cả sản phẩm từ snapshot
                    for (productSnapshot in snapshot.children) {
                        try {
                            val productId = productSnapshot.key ?: ""
                            val productMap = productSnapshot.value as? HashMap<*, *>

                            if (productMap != null) {
                                val name = productMap["name"] as? String ?: ""
                                val description = productMap["description"] as? String ?: ""
                                val price = getDoubleValue(productMap["price"])
                                val imageUrl = productMap["imageUrl"] as? String ?: ""
                                val discountPercent = getIntValue(productMap["discountPercent"])

                                val product = Product(
                                    id = productId,
                                    name = name,
                                    description = description,
                                    price = price,
                                    imageUrl = imageUrl,
                                    discountPercent = discountPercent
                                )

                                allProducts.add(product)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Lỗi khi xử lý sản phẩm: ${e.message}")
                        }
                    }

                    // Lấy ngẫu nhiên các sản phẩm
                    val randomProducts = if (allProducts.size <= limit) {
                        // Nếu tổng số sản phẩm ít hơn hoặc bằng limit, trả về tất cả
                        allProducts.shuffled()
                    } else {
                        // Ngược lại, chọn ngẫu nhiên số lượng sản phẩm theo limit
                        allProducts.shuffled().take(limit)
                    }

                    onSuccess(randomProducts)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
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
}