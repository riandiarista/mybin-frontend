package com.example.mybin.network

// Menggunakan objek singleton sederhana untuk menyimpan token sementara
// (Di aplikasi produksi, gunakan DataStore/SharedPreferences)
object AuthTokenManager {
    var authToken: String? = null
}