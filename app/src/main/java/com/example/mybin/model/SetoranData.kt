package com.example.mybin.model


data class SetoranData(
    val id: String,
    val tanggal: String,
    val jenis: String,
    val lokasi: String,
    val status: String,
    val totalKoin: Int,
    val namaUser: String,

    

    val detailFoto: String? = null,


    val detailBerat: Float? = 0f
)