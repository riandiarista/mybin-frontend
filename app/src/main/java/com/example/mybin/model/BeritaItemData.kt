package com.example.mybin.model

import java.util.UUID

data class BeritaItemData(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val date: String,
    val location: String,
    val status: String,
    val imageUri: String? = null
)
