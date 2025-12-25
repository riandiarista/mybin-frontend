package com.example.mybin.model

/**
 * Model data untuk keperluan tampilan (UI) pada aplikasi MyBin.
 * Diperbarui untuk mendukung field snapshot agar detail sampah tetap muncul
 * meskipun data asli di tabel sampah sudah dihapus (Hard Delete).
 */
data class SetoranData(
    val id: String,
    val tanggal: String,
    val jenis: String,
    val lokasi: String,
    val status: String,
    val totalKoin: Int,
    val namaUser: String,

    // --- PENAMBAHAN FIELD UNTUK SNAPSHOT DETAIL ---
    /**
     * detailFoto menyimpan string Base64 gambar dari snapshot database.
     * Digunakan untuk menampilkan foto sampah di riwayat setoran.
     */
    val detailFoto: String? = null,

    /**
     * detailBerat menyimpan data berat dari snapshot database.
     */
    val detailBerat: Float? = 0f
)