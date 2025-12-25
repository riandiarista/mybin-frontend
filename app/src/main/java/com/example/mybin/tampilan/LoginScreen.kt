package com.example.mybin.tampilan

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mybin.R
import com.example.mybin.network.ApiClient
import com.example.mybin.network.LoginRequest
import com.example.mybin.network.LoginResponse
import com.example.mybin.network.AuthTokenManager
import com.example.mybin.network.FCMRequest // Import model FCMRequest
import com.google.firebase.messaging.FirebaseMessaging // Import Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var privacyPolicyChecked by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val greenColor = Color(0xFF2EBD70)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.introawal),
                contentDescription = "MyBin Logo",
                modifier = Modifier.size(300.dp)
            )

            Text(
                text = "Selamat Datang!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = greenColor,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Username") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = privacyPolicyChecked,
                onCheckedChange = { privacyPolicyChecked = it },
                colors = CheckboxDefaults.colors(checkedColor = greenColor)
            )
            Text(text = "I have read the privacy policy.")
        }

        loginError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = greenColor)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (!privacyPolicyChecked) {
                    loginError = "Anda harus menyetujui kebijakan privasi."
                    return@Button
                }

                isLoading = true
                loginError = null
                val loginRequest = LoginRequest(username, password)

                ApiClient.instance.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            val token = responseBody?.token

                            if (token != null) {
                                // 1. Simpan Token JWT
                                AuthTokenManager.authToken = token
                                AuthTokenManager.saveToken(context, token)

                                // 2. KIRIM FCM TOKEN KE DATABASE (SINKRONISASI)
                                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val fcmToken = task.result
                                        Log.d("FCM_SYNC", "Mencoba sinkron token: $fcmToken")

                                        // Panggil endpoint PUT api/update-fcm-token
                                        ApiClient.instance.updateFCMToken("Bearer $token", FCMRequest(fcmToken))
                                            .enqueue(object : Callback<Void> {
                                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                                    if (response.isSuccessful) {
                                                        Log.d("FCM_SYNC", "✅ Token berhasil masuk database")
                                                    } else {
                                                        Log.e("FCM_SYNC", "❌ Gagal: ${response.code()}")
                                                    }
                                                }
                                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                                    Log.e("FCM_SYNC", "❌ Error koneksi API: ${t.message}")
                                                }
                                            })
                                    }
                                }
                            }

                            isLoading = false
                            Toast.makeText(context, "Login Berhasil", Toast.LENGTH_SHORT).show()

                            // NAVIGASI ROLE
                            if (username.trim().lowercase() == "superbin") {
                                navController.navigate("HomeAdmin") {
                                    popUpTo("LoginScreen") { inclusive = true }
                                }
                            } else {
                                navController.navigate("MainPage") {
                                    popUpTo("LoginScreen") { inclusive = true }
                                }
                            }
                        } else {
                            isLoading = false
                            loginError = "Login Gagal: Username atau Password salah"
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        isLoading = false
                        loginError = if (t is IOException) "Koneksi gagal, cek internet Anda" else t.message
                    }
                })
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = greenColor),
            enabled = !isLoading
        ) {
            Text(text = "Login", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}