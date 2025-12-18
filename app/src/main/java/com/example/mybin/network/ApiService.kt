package com.example.mybin.network

import com.example.mybin.model.SampahRequest
import com.example.mybin.model.SampahResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.DELETE

// --- MODEL DATA SAMPAH ---
data class Sampah(
    val id: Int,
    val user_id: Int,
    val jenis: String,
    val berat: Float,
    val detail: String?,
    val coin: Int,
    val foto: String?
)

data class ListSampahResponse(
    val message: String,
    val data: List<Sampah>
)

// --- MODEL DATA EDUKASI (BERITA) ---
// Sesuaikan dengan model edukasi.js Anda
data class EdukasiRequest(
    val judul: String,
    val deskripsi: String,
    val lokasi: String?,
    val cover: String?
)

data class EdukasiResponse(
    val status: String,
    val message: String,
    val data: EdukasiData?
)

data class EdukasiData(
    val id: Int,
    val user_id: Int,
    val judul: String,
    val deskripsi: String,
    val lokasi: String?,
    val cover: String?,
    val createdAt: String
)

data class ListEdukasiResponse(
    val status: String,
    val data: List<EdukasiData>
)

// --- MODEL AUTH ---
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val message: String, val token: String?)

interface ApiService {

    // --- AUTH ---
    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // --- MODUL SAMPAH ---
    @POST("api/sampah")
    fun createSampah(
        @Header("Authorization") token: String,
        @Body request: SampahRequest
    ): Call<SampahResponse>

    @GET("api/sampah")
    fun getSampah(
        @Header("Authorization") token: String
    ): Call<ListSampahResponse>

    @PUT("api/sampah/{id}")
    fun updateSampah(
        @Path("id") id: String,
        @Header("Authorization") token: String,
        @Body request: SampahRequest
    ): Call<SampahResponse>

    @DELETE("api/sampah/{id}")
    fun deleteSampah(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Call<SampahResponse>

    // --- MODUL EDUKASI (BERITA) ---
    // Endpoint untuk Submit Berita Baru dari BuatBeritaScreen
    @POST("api/edukasi")
    fun createEdukasi(
        @Header("Authorization") token: String,
        @Body request: EdukasiRequest
    ): Call<EdukasiResponse>

    // Endpoint untuk Mengambil Daftar Berita di NewsScreen
    @GET("api/edukasi")
    fun getEdukasi(
        @Header("Authorization") token: String
    ): Call<ListEdukasiResponse>
}