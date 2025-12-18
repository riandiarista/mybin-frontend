package com.example.mybin.model

import java.util.UUID

data class BeritaItemData(
    val id: String = UUID.randomUUID().toString(),
    val judul: String,        // Ganti dari title
    val deskripsi: String = "", // Ganti dari description
    val lokasi: String,       // Ganti dari location
    val cover: String? = null,  // Ganti dari imageUri
    val status: String = "Diterbitkan",
    val date: String? = null
)