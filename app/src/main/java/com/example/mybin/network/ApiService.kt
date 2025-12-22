package com.example.mybin.network

import com.example.mybin.model.SampahRequest
import com.example.mybin.model.SampahResponse
import com.example.mybin.model.UserProfileResponse
import retrofit2.Call
import retrofit2.http.*

// --- MODEL DATA SAMPAH (Detail Data) ---
data class Sampah(
    val id: Int,
    val user_id: Int,
    val jenis: String?,
    val berat: Float?,
    val detail: String?,
    val coin: Int?,
    val status: String?,
    val foto: String?,
    val sampah: SampahNestedDetail? = null
)

data class SampahNestedDetail(
    val jenis: String?,
    val coin: Int?
)

data class ListSampahResponse(
    val message: String,
    val data: List<Sampah>
)

// --- MODEL DATA SETORAN ---
data class SetoranRequest(
    val sampahIds: String,
    val totalKoin: Int,
    val lokasi: String
)

data class SetoranResponse(
    val message: String,
    val total_data: Int?,
    val data: List<Sampah>?
)

// --- MODEL DATA EXCHANGE (PENUKARAN POIN) ---
data class ExchangeRequest(
    val amount_poin: Int,
    val phone_number: String
)

data class ExchangeResponse(
    val message: String,
    val status: String,
    val current_balance: Int?,
    val data: ExchangeData?
)

data class ExchangeData(
    val id: Int,
    val amount_poin: Int,
    val amount_rupiah: Int,
    val phone_number: String,
    val status: String
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

// --- MODEL AUTH & FCM ---
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(
    val message: String,
    val token: String?,
    val user: UserDataLogin?
)
data class UserDataLogin(val id: Int, val username: String, val total_poin_user: Int)
data class FCMRequest(val fcm_token: String)

interface ApiService {

    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/auth/me")
    fun getUserProfile(
        @Header("Authorization") token: String
    ): Call<UserProfileResponse>

    @POST("api/auth/update-fcm")
    fun updateFCMToken(@Header("Authorization") token: String, @Body request: FCMRequest): Call<Void>

    // --- MODUL SAMPAH ---
    @POST("api/sampah")
    fun createSampah(@Header("Authorization") token: String, @Body request: SampahRequest): Call<SampahResponse>

    @GET("api/sampah")
    fun getSampah(@Header("Authorization") token: String): Call<ListSampahResponse>

    @PUT("api/sampah/{id}")
    fun updateSampah(@Path("id") id: String, @Header("Authorization") token: String, @Body request: SampahRequest): Call<SampahResponse>

    @DELETE("api/sampah/{id}")
    fun deleteSampah(@Path("id") id: String, @Header("Authorization") token: String): Call<SampahResponse>

    // --- MODUL SETORAN ---
    @POST("api/setoran")
    fun createSetoran(@Header("Authorization") token: String, @Body request: SetoranRequest): Call<SetoranResponse>

    @GET("api/setoran")
    fun getSetoran(@Header("Authorization") token: String): Call<SetoranResponse>

    // PERUBAHAN: Menambahkan endpoint DELETE untuk menghapus data setoran berdasarkan ID
    @DELETE("api/setoran/{id}")
    fun deleteSetoran(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<SetoranResponse>

    // --- MODUL LAPORAN (HISTORY) ---
    @GET("api/laporan/history")
    fun getLaporanHistory(@Header("Authorization") token: String): Call<ListSampahResponse>

    // --- MODUL EXCHANGE (REWARD) ---
    @POST("api/exchange")
    fun createExchange(
        @Header("Authorization") token: String,
        @Body request: ExchangeRequest
    ): Call<ExchangeResponse>

    // --- MODUL EDUKASI ---
    @POST("api/edukasi")
    fun createEdukasi(@Header("Authorization") token: String, @Body request: EdukasiRequest): Call<EdukasiResponse>

    @GET("api/edukasi")
    fun getEdukasi(): Call<ListEdukasiResponse>

    @PUT("api/edukasi/{id}")
    fun updateEdukasi(@Path("id") id: String, @Header("Authorization") token: String, @Body request: EdukasiRequest): Call<EdukasiResponse>

    @DELETE("api/edukasi/{id}")
    fun deleteEdukasi(@Path("id") id: String, @Header("Authorization") token: String): Call<EdukasiResponse>
}