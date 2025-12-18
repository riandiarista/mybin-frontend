package com.example.mybin.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mybin.network.ApiClient
import com.example.mybin.network.EdukasiData
import com.example.mybin.network.EdukasiRequest
import com.example.mybin.network.EdukasiResponse
import com.example.mybin.network.ListEdukasiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeritaViewModel : ViewModel() {

    // State untuk menampung daftar berita dari Database (Menggunakan EdukasiData)
    private val _beritaList = mutableStateOf<List<EdukasiData>>(emptyList())
    val beritaList: State<List<EdukasiData>> = _beritaList

    // State untuk status loading
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    /**
     * Fungsi untuk mengambil semua data edukasi dari Database
     */
    fun fetchBerita(token: String) {
        _isLoading.value = true
        ApiClient.instance.getEdukasi("Bearer $token").enqueue(object : Callback<ListEdukasiResponse> {
            override fun onResponse(call: Call<ListEdukasiResponse>, response: Response<ListEdukasiResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _beritaList.value = response.body()?.data ?: emptyList()
                }
            }

            override fun onFailure(call: Call<ListEdukasiResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("BeritaViewModel", "Error Fetch: ${t.message}")
            }
        })
    }

    /**
     * Fungsi utama untuk mengirim berita baru ke Database
     * Menggunakan standar: judul, deskripsi, lokasi, cover
     */
    fun addBerita(
        token: String,
        judul: String,      // Standar Baru
        deskripsi: String,  // Standar Baru
        lokasi: String,     // Standar Baru
        cover: String?,     // Standar Baru
        onResult: (Boolean, String) -> Unit
    ) {
        _isLoading.value = true

        // Memasukkan variabel ke Request yang akan dikirim ke Backend
        val request = EdukasiRequest(
            judul = judul,
            deskripsi = deskripsi,
            lokasi = lokasi,
            cover = cover
        )

        ApiClient.instance.createEdukasi("Bearer $token", request).enqueue(object : Callback<EdukasiResponse> {
            override fun onResponse(call: Call<EdukasiResponse>, response: Response<EdukasiResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    // Refresh data agar list di NewsScreen terupdate otomatis
                    fetchBerita(token)
                    onResult(true, "Berita berhasil diterbitkan!")
                } else {
                    onResult(false, "Gagal menerbitkan berita: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EdukasiResponse>, t: Throwable) {
                _isLoading.value = false
                onResult(false, "Terjadi kesalahan jaringan: ${t.message}")
            }
        })
    }

    /**
     * Mencari detail berita berdasarkan ID dari list yang sudah ada
     */
    fun getBeritaById(id: String?): EdukasiData? {
        // ID di database berbentuk Int, kita konversi id (String) dari navigasi ke Int
        return _beritaList.value.find { it.id.toString() == id }
    }
}