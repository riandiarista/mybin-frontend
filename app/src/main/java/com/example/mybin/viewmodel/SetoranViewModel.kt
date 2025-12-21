package com.example.mybin.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybin.model.SetoranData
import com.example.mybin.model.UserProfileResponse
import com.example.mybin.network.ApiClient
import com.example.mybin.network.AuthTokenManager
import com.example.mybin.network.ExchangeRequest
import com.example.mybin.network.ExchangeResponse
import com.example.mybin.network.ListSampahResponse
import com.example.mybin.network.SetoranRequest
import com.example.mybin.network.SetoranResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetoranViewModel : ViewModel() {
    private val _setoranList = mutableStateListOf<SetoranData>()
    val setoranList: List<SetoranData> get() = _setoranList

    private val _laporanList = mutableStateListOf<SetoranData>()
    val laporanList: List<SetoranData> get() = _laporanList

    // State untuk menampung response mentah dari API Setoran
    private val _setoranResponse = MutableStateFlow<SetoranResponse?>(null)
    val setoranResponse: StateFlow<SetoranResponse?> = _setoranResponse

    // --- STATE FILTER ---
    var filterStatus by mutableStateOf("Semua")
    var filterJenis by mutableStateOf("Semua")

    // --- LOGIKA FILTER DINAMIS ---
    val filteredLaporanList: List<SetoranData>
        get() {
            return _laporanList.filter { item ->
                val matchesStatus = if (filterStatus == "Semua") true
                else item.status.equals(filterStatus, ignoreCase = true)

                val matchesJenis = if (filterJenis == "Semua") true
                else item.jenis.equals(filterJenis, ignoreCase = true)

                matchesStatus && matchesJenis
            }
        }

    var totalPoinUser by mutableStateOf(0)
        private set

    var isLoading by mutableStateOf(false)

    fun addSetoran(setoran: SetoranData) {
        _setoranList.add(0, setoran)
    }

    /**
     * FUNGSI BARU: Mengambil data sampah yang baru saja disetor dari Backend
     * Digunakan oleh DataSetoranScreen.kt untuk menampilkan data real-time.
     */
    fun getSetoran() {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) return

        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.getSetoran("Bearer $token").enqueue(object : Callback<SetoranResponse> {
                override fun onResponse(call: Call<SetoranResponse>, response: Response<SetoranResponse>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        _setoranResponse.value = response.body()
                        val remoteData = response.body()?.data ?: emptyList()

                        // Sinkronisasi ke list lokal untuk UI
                        _setoranList.clear()
                        remoteData.forEach { item ->
                            _setoranList.add(
                                SetoranData(
                                    id = item.id.toString(),
                                    tanggal = "Sedang Proses",
                                    jenis = item.sampah?.jenis ?: "Sampah Campuran",
                                    lokasi = "Lokasi Penjemputan",
                                    status = item.status ?: "menunggu",
                                    totalKoin = item.sampah?.coin ?: 0
                                )
                            )
                        }
                        Log.d("MyBin_Setoran", "Berhasil sinkronisasi data setoran")
                    }
                }

                override fun onFailure(call: Call<SetoranResponse>, t: Throwable) {
                    isLoading = false
                    Log.e("ERROR", "Gagal load data setoran: ${t.message}")
                }
            })
        }
    }

    fun loadUserBalance() {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.getUserProfile("Bearer $token").enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                    if (response.isSuccessful) {
                        val saldoBersih = response.body()?.data?.totalPoinUser ?: 0
                        totalPoinUser = saldoBersih
                    }
                }
                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    Log.e("ERROR", "Gagal load profile saldo: ${t.message}")
                }
            })
        }
    }

    fun loadLaporanHistory(onError: (String) -> Unit) {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) {
            onError("Sesi berakhir.")
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
                            val detailSampah = item.sampah
                            val statusStr = item.status ?: "pending"
                            val koin = detailSampah?.coin ?: item.coin ?: 0

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
                        loadUserBalance()
                    } else {
                        onError("Gagal memuat riwayat")
                    }
                }

                override fun onFailure(call: Call<ListSampahResponse>, t: Throwable) {
                    isLoading = false
                    onError("Koneksi gagal")
                }
            })
        }
    }

    fun submitExchange(
        amountPoin: Int,
        phoneNumber: String,
        onSuccess: (String, Int) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) {
            onError("Sesi berakhir.")
            return
        }

        isLoading = true
        val request = ExchangeRequest(amount_poin = amountPoin, phone_number = phoneNumber)

        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.createExchange("Bearer $token", request).enqueue(object : Callback<ExchangeResponse> {
                override fun onResponse(call: Call<ExchangeResponse>, response: Response<ExchangeResponse>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val resBody = response.body()
                        val saldoTerbaru = resBody?.current_balance ?: (totalPoinUser - amountPoin)
                        totalPoinUser = saldoTerbaru
                        onSuccess(resBody?.message ?: "Penukaran berhasil!", totalPoinUser)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Saldo tidak cukup"
                        onError(errorBody)
                    }
                }

                override fun onFailure(call: Call<ExchangeResponse>, t: Throwable) {
                    isLoading = false
                    onError("Koneksi gagal: ${t.message}")
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
            onError("Sesi berakhir.")
            return
        }

        val request = SetoranRequest(sampahIds = sampahIds, totalKoin = totalKoin, lokasi = lokasi)

        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.createSetoran("Bearer $token", request).enqueue(object : Callback<SetoranResponse> {
                override fun onResponse(call: Call<SetoranResponse>, response: Response<SetoranResponse>) {
                    if (response.isSuccessful) {
                        onSuccess(response.body()?.message ?: "Setoran berhasil!")
                        // Setelah sukses submit, panggil getSetoran untuk refresh list otomatis
                        getSetoran()
                    } else {
                        onError("Gagal memproses setoran")
                    }
                }

                override fun onFailure(call: Call<SetoranResponse>, t: Throwable) {
                    onError("Gagal terhubung ke server")
                }
            })
        }
    }
}