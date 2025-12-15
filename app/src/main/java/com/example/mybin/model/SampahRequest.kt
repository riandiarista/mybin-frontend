package com.example.mybin.model

import com.google.gson.annotations.SerializedName

// 1. Model untuk mengirim data ke API (Body Request)
data class SampahRequest(
    val jenis: String,
    val berat: Float,
    val detail: String,
    val coin: Int,
    val foto: String? // Biarkan sebagai String, karena Uri tidak bisa langsung dikirim
)

// 2. Model untuk respon dari API
data class SampahResponse(
    val message: String,
    val data: Sampah? // Data yang dikembalikan setelah disimpan
)

data class Sampah(
    val id: Int,
    val user_id: Int,
    val jenis: String,
    val berat: Float,
    val detail: String?,
    val coin: Int,
    val foto: String?
)
