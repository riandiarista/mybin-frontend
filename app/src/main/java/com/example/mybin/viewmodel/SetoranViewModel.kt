package com.example.mybin.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybin.model.SetoranData
import com.example.mybin.model.UserProfileResponse // Pastikan ini diimpor
import com.example.mybin.network.ApiClient
import com.example.mybin.network.AuthTokenManager
import com.example.mybin.network.ExchangeRequest
import com.example.mybin.network.ExchangeResponse
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

    //
    // Sekarang mengambil data langsung dari kolom total_poin_user di tabel User
    var totalPoinUser by mutableStateOf(0)
        private set

    var isLoading by mutableStateOf(false)

    fun addSetoran(setoran: SetoranData) {
        _setoranList.add(0, setoran)
    }

    /**
     * TAHAP 3: FUNGSI BARU - Load Saldo Langsung dari Profil User
     * Dipanggil saat aplikasi dibuka atau refresh saldo.
     */
    fun loadUserBalance() {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.getUserProfile("Bearer $token").enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                    if (response.isSuccessful) {
                        // SINKRONISASI: Ambil saldo bersih dari database (Total Selesai - Total Tukar)
                        val saldoBersih = response.body()?.data?.totalPoinUser ?: 0
                        totalPoinUser = saldoBersih
                        Log.d("MyBin_Balance", "Saldo: $totalPoinUser")
                    }
                }
                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    Log.e("ERROR", "Gagal load profile saldo: ${t.message}")
                }
            })
        }
    }

    /**
     * Memuat riwayat laporan (Hanya untuk daftar riwayat, tidak untuk hitung saldo lagi)
     */
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

                        // Setelah load history, sinkronkan juga saldo utama
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

    /**
     * FITUR PENUKARAN POIN (EXCHANGE) - VERSI REAL-TIME
     */
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

                        // UPDATE UI LANGSUNG: Backend mengirim current_balance yang sudah dipotong
                        val saldoTerbaru = resBody?.current_balance ?: (totalPoinUser - amountPoin)

                        // State ini akan membuat angka di layar Selection & Exchange berubah seketika
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

    /**
     * FITUR SUBMIT SETORAN
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
            onError("Sesi berakhir.")
            return
        }

        val request = SetoranRequest(sampahIds = sampahIds, totalKoin = totalKoin, lokasi = lokasi)

        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.createSetoran("Bearer $token", request).enqueue(object : Callback<SetoranResponse> {
                override fun onResponse(call: Call<SetoranResponse>, response: Response<SetoranResponse>) {
                    if (response.isSuccessful) {
                        onSuccess(response.body()?.message ?: "Setoran berhasil!")
                        // Saat setoran dibuat, saldo belum bertambah karena status masih 'menunggu'.
                        // Saldo baru bertambah di tabel user setelah Admin mengubah status menjadi 'selesai'.
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