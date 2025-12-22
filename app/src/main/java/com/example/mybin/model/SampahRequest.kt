package com.example.mybin.model

import com.google.gson.annotations.SerializedName

/**
 * 1. Model untuk mengirim data ke API (Body Request)
 * Digunakan saat POST (Create) dan PUT (Update) sampah.
 */
data class SampahRequest(
    @SerializedName("jenis") val jenis: String,
    @SerializedName("berat") val berat: Float,
    @SerializedName("detail") val detail: String,
    @SerializedName("coin") val coin: Int,
    // Field foto menampung string Base64 dari gambar yang diambil
    @SerializedName("foto") val foto: String?
)

/**
 * 2. Model untuk respon tunggal dari API
 * Digunakan saat mendapatkan feedback setelah simpan, update, atau delete data.
 */
data class SampahResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Sampah?
)

/**
 * 3. Model Detail Data Sampah
 * Mencerminkan struktur kolom di database (tabel sampahs).
 */
data class Sampah(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("jenis") val jenis: String,
    @SerializedName("berat") val berat: Float,
    @SerializedName("detail") val detail: String?,
    @SerializedName("coin") val coin: Int,
    @SerializedName("status") val status: String? = "selesai",
    // Field foto untuk sinkronisasi gambar dari backend ke UI
    @SerializedName("foto") val foto: String?
)