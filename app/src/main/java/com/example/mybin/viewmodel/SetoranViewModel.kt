package com.example.mybin.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybin.model.SetoranData
import com.example.mybin.network.ApiClient
import com.example.mybin.network.AuthTokenManager
import com.example.mybin.network.ListSampahResponse
import com.example.mybin.network.SetoranRequest
import com.example.mybin.network.SetoranResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetoranViewModel : ViewModel() {
    // State untuk daftar setoran sementara/lokal
    private val _setoranList = mutableStateListOf<SetoranData>()
    val setoranList: List<SetoranData> get() = _setoranList

    // State untuk data laporan final (Selesai/Ditolak) dari API
    private val _laporanList = mutableStateListOf<SetoranData>()
    val laporanList: List<SetoranData> get() = _laporanList

    // State indikator loading
    var isLoading by mutableStateOf(false)

    fun addSetoran(setoran: SetoranData) {
        _setoranList.add(0, setoran)
    }

    /**
     * FUNGSI UTAMA: Memuat riwayat laporan dari API.
     * Mengakses data secara hirarkis: Objek Setoran -> Objek Sampah.
     */
    fun loadLaporanHistory(onError: (String) -> Unit) {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) {
            onError("Sesi berakhir. Silakan login kembali.")
            return
        }

        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.getLaporanHistory("Bearer $token").enqueue(object : Callback<ListSampahResponse> {
                override fun onResponse(call: Call<ListSampahResponse>, response: Response<ListSampahResponse>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val remoteData = response.body()?.data ?: emptyList()

                        _laporanList.clear()
                        remoteData.forEach { item ->
                            // FIX: Mengambil data dari objek nested 'sampah' hasil include Sequelize
                            val detailSampah = item.sampah

                            _laporanList.add(
                                SetoranData(
                                    id = item.id.toString(),
                                    tanggal = "Terverifikasi",
                                    // Ambil 'jenis' dari objek sampah, jika null fallback ke item.jenis
                                    jenis = detailSampah?.jenis ?: item.jenis ?: "Jenis tidak diketahui",
                                    lokasi = "-",
                                    status = item.status ?: "selesai",
                                    // Ambil 'coin' dari objek sampah, jika null fallback ke item.coin
                                    totalKoin = detailSampah?.coin ?: item.coin ?: 0
                                )
                            )
                        }
                        Log.d("API_LAPORAN_SUCCESS", "Berhasil memuat ${remoteData.size} laporan.")
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Gagal memuat laporan"
                        onError("Gagal memuat laporan (Kode: ${response.code()})")
                        Log.e("API_LAPORAN_ERROR", "Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<ListSampahResponse>, t: Throwable) {
                    isLoading = false
                    onError("Gagal terhubung ke server: ${t.message}")
                    Log.e("API_LAPORAN_FAIL", "Pesan: ${t.message}", t)
                }
            })
        }
    }

    /**
     * Mengirim data setoran baru ke Backend
     */
    fun submitSetoran(
        sampahIds: String,
        totalKoin: Int,
        lokasi: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) {
            onError("Sesi berakhir. Silakan login kembali.")
            return
        }

        val request = SetoranRequest(
            sampahIds = sampahIds,
            totalKoin = totalKoin,
            lokasi = lokasi
        )

        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.createSetoran(
                token = "Bearer $token",
                request = request
            ).enqueue(object : Callback<SetoranResponse> {
                override fun onResponse(call: Call<SetoranResponse>, response: Response<SetoranResponse>) {
                    if (response.isSuccessful) {
                        onSuccess(response.body()?.message ?: "Setoran berhasil diproses!")
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Gagal memproses data"
                        onError("Error ${response.code()}: $errorMsg")
                        Log.e("API_SETORAN_ERROR", errorMsg)
                    }
                }

                override fun onFailure(call: Call<SetoranResponse>, t: Throwable) {
                    onError("Gagal terhubung ke server: ${t.message}")
                    Log.e("API_SETORAN_FAIL", t.message ?: "Unknown failure")
                }
            })
        }
    }
}