package com.example.mybin.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybin.model.SetoranData
import com.example.mybin.network.ApiClient
import com.example.mybin.network.AuthTokenManager
import com.example.mybin.network.SetoranRequest
import com.example.mybin.network.SetoranResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetoranViewModel : ViewModel() {
    private val _setoranList = mutableStateListOf<SetoranData>()
    val setoranList: List<SetoranData> get() = _setoranList

    // Fungsi untuk menambah data secara lokal (untuk dummy atau testing)
    fun addSetoran(setoran: SetoranData) {
        _setoranList.add(0, setoran)
    }

    /**
     * Langkah Selanjutnya: Fungsi untuk mengirim data setoran ke API Backend.
     * Fungsi ini akan memindahkan data dari tabel 'sampahs' ke 'setorans' di server.
     */
    fun submitSetoran(
        sampahIds: String,
        totalKoin: Int,
        lokasi: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        // 1. Ambil Token Autentikasi
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) {
            onError("Sesi berakhir. Silakan login kembali.")
            return
        }

        // 2. Siapkan Request Body sesuai model di ApiService.kt
        val request = SetoranRequest(
            sampahIds = sampahIds,
            totalKoin = totalKoin,
            lokasi = lokasi
        )

        // 3. Jalankan pemanggilan API di background thread
        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.createSetoran(
                token = "Bearer $token",
                request = request
            ).enqueue(object : Callback<SetoranResponse> {
                override fun onResponse(call: Call<SetoranResponse>, response: Response<SetoranResponse>) {
                    if (response.isSuccessful) {
                        // Jika berhasil, panggil callback sukses
                        onSuccess(response.body()?.message ?: "Setoran berhasil diproses!")
                    } else {
                        // Jika gagal dari sisi server (misal: ID tidak ditemukan)
                        val errorMsg = response.errorBody()?.string() ?: "Gagal memproses data"
                        onError("Error ${response.code()}: $errorMsg")
                        Log.e("API_SETORAN_ERROR", errorMsg)
                    }
                }

                override fun onFailure(call: Call<SetoranResponse>, t: Throwable) {
                    // Jika gagal koneksi/network error
                    onError("Gagal terhubung ke server: ${t.message}")
                    Log.e("API_SETORAN_FAIL", t.message ?: "Unknown failure")
                }
            })
        }
    }
}