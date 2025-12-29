package com.example.mybin.model

import java.util.UUID

data class BeritaItemData(
    val id: String = UUID.randomUUID().toString(),
    val judul: String,
    val deskripsi: String = "",
    val lokasi: String,
    val cover: String? = null,
    val status: String = "Diterbitkan",
    val date: String? = null
)