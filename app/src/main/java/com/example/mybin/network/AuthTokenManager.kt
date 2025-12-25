package com.example.mybin.network

import android.content.Context

object AuthTokenManager {
    var authToken: String? = null

    // Fungsi minimal agar LoginScreen.kt tidak error saat menyimpan token
    fun saveToken(context: Context, token: String) {
        this.authToken = token
        // Menyimpan ke HP agar user tidak perlu login terus menerus
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(context: Context): String? {
        return authToken
    }
}