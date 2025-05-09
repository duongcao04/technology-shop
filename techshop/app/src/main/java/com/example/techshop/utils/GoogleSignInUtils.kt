package com.example.techshop.utils

import android.content.Context
import com.example.techshop.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object  GoogleSignInUtils {
    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))  // Truyền context vào để lấy chuỗi từ resources
            .requestEmail()  // Yêu cầu email của người dùng
            .build()

        return GoogleSignIn.getClient(context, googleSignInOptions)
    }

}