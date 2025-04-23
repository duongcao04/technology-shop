package com.example.techshop.views

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.techshop.R
import com.example.techshop.ui.theme.Primary500
import com.example.techshop.ui.theme.Raleway
import com.example.techshop.utils.GoogleSignInUtils
import com.example.techshop.viewmodels.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(viewModel: AuthViewModel, navController: NavController) {
    // sư lý login
    val context = LocalContext.current
    val googleSignInClient = GoogleSignInUtils.getGoogleSignInClient(context)

    val authState = viewModel.authState.collectAsState().value
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    viewModel.loginWithGoogle(idToken)
                }
            } catch (e: Exception) {
                Log.e("LoginScreen", "Google Sign-In failed", e)
            }
        }
    }
    // Theo dõi trạng thái đăng nhập để chuyển màn hình(nếu thành công chuyển qua home)
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
    when (authState) {
        is AuthViewModel.AuthState.Loading -> CircularProgressIndicator()
        is AuthViewModel.AuthState.Error -> Text("Lỗi: ${(authState as AuthViewModel.AuthState.Error).message}")
        else -> {}
    }
    Box(
        modifier = Modifier
            .fillMaxSize(), // Ảnh phủ toàn màn hình
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "Ảnh local",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Nội dung hiển thị bên trên nền
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Đăng nhập", color = Color.Black, style = TextStyle(
                    fontSize = 52.sp,
                    fontWeight = FontWeight(800)
                ), fontFamily = Raleway
            )
            Text(
                "Chào mừng quay trở lại!",
                color = Color.Black, style = TextStyle(
                    fontSize = 19.sp,
                    fontWeight = FontWeight(400)
                ), modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp), fontFamily = Raleway
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {   googleSignInClient.signOut().addOnCompleteListener {
                    launcher.launch(googleSignInClient.signInIntent)
                } },
                modifier = Modifier
                    .height(61.dp)
                    .fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary500
                )
            ) {
                Row(){
                    Image(
                        painter = painterResource(id = R.drawable.icon_google_2),
                        contentDescription = "Google Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "Đăng nhập với Google", style = TextStyle(
                            fontSize = 19.sp,
                            fontWeight = FontWeight(500)
                        ),
                        modifier = Modifier.padding(bottom = 10.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}