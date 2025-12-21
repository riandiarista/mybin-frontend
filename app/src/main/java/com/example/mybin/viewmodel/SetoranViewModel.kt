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
    private val _setoranList = mutableStateListOf<SetoranData>()
    val setoranList: List<SetoranData> get() = _setoranList

    private val _laporanList = mutableStateListOf<SetoranData>()
    val laporanList: List<SetoranData> get() = _laporanList

    // --- STATE POIN OTOMATIS ---
    // State ini akan dipantau oleh MainMenu, LaporanScreen, dan ExchangeScreen
    var totalPoinUser by mutableStateOf(0)
        private set

    var isLoading by mutableStateOf(false)

    fun addSetoran(setoran: SetoranData) {
        _setoranList.add(0, setoran)
    }

    /**
     * Memuat riwayat laporan dan menghitung akumulasi poin secara otomatis
     * berdasarkan setoran yang berstatus 'selesai'.
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
                        var accumulatedPoin = 0 // Variabel penampung hitungan poin

                        remoteData.forEach { item ->
                            val detailSampah = item.sampah
                            val statusStr = item.status ?: "selesai"
                            val koin = detailSampah?.coin ?: item.coin ?: 0

                            // LOGIKA PERHITUNGAN: Hanya koin dari status 'selesai' yang dijumlahkan
                            if (statusStr.lowercase() == "selesai") {
                                accumulatedPoin += koin
                            }

                            _laporanList.add(
                                SetoranData(
                                    id = item.id.toString(),
                                    tanggal = "Terverifikasi",
                                    jenis = detailSampah?.jenis ?: item.jenis ?: "Jenis tidak diketahui",
                                    lokasi = "-",
                                    status = statusStr,
                                    totalKoin = koin
                                )
                            )
                        }

                        // Update state total poin agar UI di semua screen ter-refresh
                        totalPoinUser = accumulatedPoin

                        Log.d("API_LAPORAN_SUCCESS", "Data dimuat. Total Poin: $totalPoinUser")
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
                        // Refresh history agar poin langsung update setelah submit (jika backend langsung verifikasi)
                        loadLaporanHistory { }
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