package com.example.mybin.network

import com.example.mybin.model.SampahRequest
import com.example.mybin.model.SampahResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.GET // <--- DITAMBAHKAN

// Model data yang dikembalikan Backend (sesuai kolom database 'sampahs')
data class Sampah(
    val id: Int,
    val user_id: Int,
    val jenis: String,
    val berat: Float,
    val detail: String?,
    val coin: Int,
    val foto: String?
)

// Model untuk membungkus list data sampah dari respons GET /api/sampah
data class ListSampahResponse(
    val message: String,
    val data: List<Sampah>
)

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val message: String, val token: String?)

interface ApiService {
    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Endpoint untuk Tambah Sampah (POST /api/sampah)
    @POST("api/sampah")
    fun createSampah(
        @Header("Authorization") token: String,
        @Body request: SampahRequest
    ): Call<SampahResponse>

    // Endpoint baru untuk Ambil Daftar Sampah (GET /api/sampah)
    @GET("api/sampah")
    fun getSampah(
        // Rute ini terproteksi oleh auth middleware
        @Header("Authorization") token: String
    ): Call<ListSampahResponse>
}