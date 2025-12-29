package com.example.mybin.model

import com.google.gson.annotations.SerializedName


data class SampahRequest(
    @SerializedName("jenis") val jenis: String,
    @SerializedName("berat") val berat: Float,
    @SerializedName("detail") val detail: String,
    @SerializedName("coin") val coin: Int,

    @SerializedName("foto") val foto: String?
)


data class SampahResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Sampah?
)


data class Sampah(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("jenis") val jenis: String,
    @SerializedName("berat") val berat: Float,
    @SerializedName("detail") val detail: String?,
    @SerializedName("coin") val coin: Int,
    @SerializedName("status") val status: String? = "selesai",

    @SerializedName("foto") val foto: String?
)