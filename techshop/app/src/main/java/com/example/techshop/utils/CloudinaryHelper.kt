package com.example.techshop.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import java.util.*

class CloudinaryUploader(context: Context) {

    init {
        try {
            MediaManager.get() // Thử lấy MediaManager trước
        } catch (e: Exception) {
            // Nếu chưa được init thì init
            val config: HashMap<String, String> = hashMapOf(
                "cloud_name" to "dwnwtkmhx",
                "api_key" to "818176439115388",
                "api_secret" to "vSmBf-p-xbxtMhtYuY_1i_rppV8"
            )
            MediaManager.init(context.applicationContext, config)
        }
    }

    fun uploadImage(uri: Uri, onResult: (Boolean, String?) -> Unit) {
        val publicId = "product_${System.currentTimeMillis()}"

        MediaManager.get().upload(uri)
            .option("public_id", publicId)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    Log.d("Cloudinary", "Uploading started...")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val imageUrl = resultData?.get("secure_url").toString()
                    Log.d("Cloudinary", "Upload successful: $imageUrl")
                    onResult(true, imageUrl)
                }

                override fun onError(requestId: String?, error: com.cloudinary.android.callback.ErrorInfo?) {
                    Log.e("Cloudinary", "Upload error: ${error?.description}")
                    onResult(false, null)
                }

                override fun onReschedule(requestId: String?, error: com.cloudinary.android.callback.ErrorInfo?) {
                    Log.e("Cloudinary", "Upload rescheduled: ${error?.description}")
                }
            })
            .dispatch()
    }
}
