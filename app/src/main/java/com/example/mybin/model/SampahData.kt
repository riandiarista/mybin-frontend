package com.example.mybin.model

import android.net.Uri
import java.util.UUID

data class SampahData(
    val id: String = UUID.randomUUID().toString(),
    val jenisSampah: String,
    val detailSampah: String,
    val totalBobot: String,
    val imageUri: Uri?,
    val estimasiKoin: Int
)
