package com.example.mybin.model

import android.net.Uri
import java.util.UUID

data class SampahData(
    val id: String = UUID.randomUUID().toString(),
    val jenisSampah: String,
    val detailSampah: String,
    val totalBobot: String,
    val imageUri: Uri?, // Digunakan untuk menyimpan URI foto sementara dari kamera/galeri
    val estimasiKoin: Int,
    // PERUBAHAN PENTING: Tambahkan field foto untuk menampung string Base64 dari Backend
    val foto: String? = null
)