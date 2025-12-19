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

// --- MODEL DATA SETORAN (BARU) ---
// Model ini digunakan untuk mengirim data dari AddAddressScreen ke backend
data class SetoranRequest(
    val sampahIds: String, // Berisi ID sampah yang dipisah koma, misal: "1,2,3"
    val totalKoin: Int,
    val lokasi: String
)

data class SetoranResponse(
    val message: String,
    val total_data: Int?
)

// --- MODEL DATA EDUKASI (BERITA) ---
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

// --- MODEL FCM ---
data class FCMRequest(
    val fcm_token: String
)

interface ApiService {

    // --- AUTH & NOTIFIKASI ---
    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/auth/update-fcm")
    fun updateFCMToken(
        @Header("Authorization") token: String,
        @Body request: FCMRequest
    ): Call<Void>

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

    // --- MODUL SETORAN (BARU) ---
    // Endpoint untuk memproses data dari AddAddressScreen
    @POST("api/setoran")
    fun createSetoran(
        @Header("Authorization") token: String,
        @Body request: SetoranRequest
    ): Call<SetoranResponse>

    // --- MODUL EDUKASI (BERITA) ---
    @POST("api/edukasi")
    fun createEdukasi(
        @Header("Authorization") token: String,
        @Body request: EdukasiRequest
    ): Call<EdukasiResponse>

    @GET("api/edukasi")
    fun getEdukasi(
        @Header("Authorization") token: String
    ): Call<ListEdukasiResponse>
}