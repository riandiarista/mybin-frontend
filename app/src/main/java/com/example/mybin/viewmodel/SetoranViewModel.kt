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
import com.example.mybin.network.UpdateStatusRequest
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

    private val _setoranResponse = MutableStateFlow<SetoranResponse?>(null)
    val setoranResponse: StateFlow<SetoranResponse?> = _setoranResponse

    var filterStatus by mutableStateOf("Semua")
    var filterJenis by mutableStateOf("Semua")

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

    fun fetchSetoran() {
        getSetoran()
    }

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

                        _setoranList.clear()
                        remoteData.forEach { item ->
                            Log.d("API_SETORAN", "ID: ${item.id}, User: ${item.user?.username}, Status: ${item.status}")

                            _setoranList.add(
                                SetoranData(
                                    id = item.id.toString(),
                                    tanggal = "Sedang Proses",
                                    jenis = item.sampah?.jenis ?: "Sampah Campuran",
                                    lokasi = item.lokasi ?: "Lokasi Penjemputan",
                                    status = item.status ?: "menunggu",
                                    totalKoin = item.sampah?.coin ?: 0,
                                    namaUser = item.user?.username ?: "Pengguna"
                                )
                            )
                        }
                    } else {
                        Log.e("API_ERROR", "Gagal load data: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<SetoranResponse>, t: Throwable) {
                    isLoading = false
                    Log.e("ERROR", "Gagal load data setoran: ${t.message}")
                }
            })
        }
    }

    // --- FUNGSI UPDATE STATUS (VERIFIKASI ADMIN) ---
    fun updateStatus(setoranId: String, status: String) {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) return

        val idInt = setoranId.toIntOrNull() ?: return
        val request = UpdateStatusRequest(status = status)

        isLoading = true
        // Gunakan Dispatchers.IO untuk request jaringan
        viewModelScope.launch(Dispatchers.IO) {
            // Karena backend mengirim res.json, kita tetap pakai Callback<Void>
            // namun pastikan rute di ApiClient benar.
            ApiClient.instance.updateStatusSetoran(idInt, "Bearer $token", request)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.d("SUCCESS", "Status ID $setoranId berhasil diupdate ke $status")

                            // Segera panggil fetchSetoran() untuk memperbarui tampilan
                            viewModelScope.launch(Dispatchers.Main) {
                                getSetoran()
                            }
                        } else {
                            isLoading = false
                            // Log body error untuk melihat apa yang salah di server
                            val errorMsg = response.errorBody()?.string()
                            Log.e("API_ERROR", "Gagal update: ${response.code()} -> $errorMsg")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        isLoading = false
                        Log.e("NETWORK_ERROR", "Gagal koneksi: ${t.message}")
                    }
                })
        }
    }

    fun deleteSetoran(setoranId: Int, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) {
            onError("Sesi berakhir.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.deleteSetoran(setoranId, "Bearer $token").enqueue(object : Callback<SetoranResponse> {
                override fun onResponse(call: Call<SetoranResponse>, response: Response<SetoranResponse>) {
                    if (response.isSuccessful) {
                        _setoranList.removeAll { it.id == setoranId.toString() }
                        onSuccess(response.body()?.message ?: "Data berhasil dihapus")
                    } else {
                        onError("Gagal menghapus data dari server")
                    }
                }

                override fun onFailure(call: Call<SetoranResponse>, t: Throwable) {
                    onError("Koneksi gagal: ${t.message}")
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
                                    totalKoin = koin,
                                    namaUser = item.user?.username ?: "Pengguna"
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