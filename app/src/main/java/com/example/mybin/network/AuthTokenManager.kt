package com.example.mybin.network

import android.content.Context

// Menggunakan objek singleton sederhana untuk menyimpan token sementara
object AuthTokenManager {
    var authToken: String? = null

    // Tambahkan fungsi ini saja supaya tidak unresolved reference di tampilan lain
    fun getToken(context: Context): String? {
        return authToken
    }
}