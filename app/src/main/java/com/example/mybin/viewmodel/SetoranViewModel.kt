package com.example.mybin.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybin.model.SetoranData
import com.example.mybin.model.UserProfileResponse
import com.example.mybin.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetoranViewModel : ViewModel() {
    // List untuk tampilan utama dan laporan tetap menggunakan SetoranData (UI Model)
    private val _setoranList = mutableStateListOf<SetoranData>()
    val setoranList: List<SetoranData> get() = _setoranList

    private val _laporanList = mutableStateListOf<SetoranData>()
    val laporanList: List<SetoranData> get() = _laporanList

    private val _setoranResponse = MutableStateFlow<SetoranResponse?>(null)
    val setoranResponse: StateFlow<SetoranResponse?> = _setoranResponse

    var filterStatus by mutableStateOf("Semua")
    var filterJenis by mutableStateOf("Semua")

    val filteredLaporanList: List<SetoranData>
        get() = _laporanList.filter { item ->
            val matchesStatus = if (filterStatus == "Semua") true else item.status.equals(filterStatus, ignoreCase = true)
            val matchesJenis = if (filterJenis == "Semua") true else item.jenis.contains(filterJenis, ignoreCase = true)
            matchesStatus && matchesJenis
        }

    var totalPoinUser by mutableStateOf(0)
        private set

    var isLoading by mutableStateOf(false)

    fun fetchSetoran() = getSetoran()

    /**
     * getSetoran: Mengambil data setoran aktif.
     * Menggunakan data snapshot (detail_*) agar informasi tetap muncul
     * setelah data sampah asli di-hard delete.
     */
    fun getSetoran() {
        val token = AuthTokenManager.authToken ?: return
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
                            _setoranList.add(
                                SetoranData(
                                    id = item.id.toString(),
                                    tanggal = item.tanggal ?: "Sedang Proses",
                                    // PRIORITAS: Ambil dari detail_jenis (snapshot) jika sampahId null
                                    jenis = item.detail_jenis ?: item.sampah?.jenis ?: "Setoran Kolektif",
                                    lokasi = item.lokasi ?: "Lokasi Penjemputan",
                                    status = item.status ?: "menunggu",
                                    totalKoin = item.total_koin ?: 0,
                                    namaUser = item.user?.username ?: "Pengguna",
                                    // MENANGKAP SNAPSHOT FOTO DAN BERAT
                                    detailFoto = item.detail_foto,
                                    detailBerat = item.detail_berat ?: 0f
                                )
                            )
                        }
                    }
                }
                override fun onFailure(call: Call<SetoranResponse>, t: Throwable) {
                    isLoading = false
                    Log.e("SetoranVM", "Gagal fetch setoran: ${t.message}")
                }
            })
        }
    }

    fun updateStatus(setoranId: String, status: String) {
        val token = AuthTokenManager.authToken ?: return
        val idInt = setoranId.toIntOrNull() ?: return
        val request = UpdateStatusRequest(status = status)

        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.updateStatusSetoran(idInt, "Bearer $token", request).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) getSetoran() else isLoading = false
                }
                override fun onFailure(call: Call<Void>, t: Throwable) { isLoading = false }
            })
        }
    }

    fun deleteSetoran(setoranId: Int, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val token = AuthTokenManager.authToken ?: return
        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.deleteSetoran(setoranId, "Bearer $token").enqueue(object : Callback<SetoranResponse> {
                override fun onResponse(call: Call<SetoranResponse>, response: Response<SetoranResponse>) {
                    if (response.isSuccessful) {
                        _setoranList.removeAll { it.id == setoranId.toString() }
                        onSuccess(response.body()?.message ?: "Data dihapus")
                    } else onError("Gagal hapus")
                }
                override fun onFailure(call: Call<SetoranResponse>, t: Throwable) { onError("Error koneksi") }
            })
        }
    }

    fun loadUserBalance() {
        val token = AuthTokenManager.authToken ?: return
        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.getUserProfile("Bearer $token").enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                    if (response.isSuccessful) totalPoinUser = response.body()?.data?.totalPoinUser ?: 0
                }
                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {}
            })
        }
    }

    /**
     * loadLaporanHistory: Menampilkan riwayat penyetoran.
     * Menggunakan data snapshot agar riwayat koin dan jenis tetap akurat.
     */
    fun loadLaporanHistory(onError: (String) -> Unit) {
        val token = AuthTokenManager.authToken ?: return
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.getSetoran("Bearer $token").enqueue(object : Callback<SetoranResponse> {
                override fun onResponse(call: Call<SetoranResponse>, response: Response<SetoranResponse>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        _laporanList.clear()
                        val remoteData = response.body()?.data ?: emptyList()
                        remoteData.filter { it.status.equals("selesai", true) }.forEach { item ->
                            _laporanList.add(
                                SetoranData(
                                    id = item.id.toString(),
                                    tanggal = item.tanggal ?: "Terverifikasi",
                                    jenis = item.detail_jenis ?: item.sampah?.jenis ?: "Sampah Kolektif",
                                    lokasi = item.lokasi ?: "-",
                                    status = item.status ?: "selesai",
                                    totalKoin = item.total_koin ?: 0,
                                    namaUser = item.user?.username ?: "Pengguna",
                                    detailFoto = item.detail_foto,
                                    detailBerat = item.detail_berat ?: 0f
                                )
                            )
                        }
                        loadUserBalance()
                    }
                }
                override fun onFailure(call: Call<SetoranResponse>, t: Throwable) { isLoading = false }
            })
        }
    }

    fun submitExchange(amountPoin: Int, phoneNumber: String, onSuccess: (String, Int) -> Unit, onError: (String) -> Unit) {
        val token = AuthTokenManager.authToken ?: return
        isLoading = true
        val request = ExchangeRequest(amount_poin = amountPoin, phone_number = phoneNumber)
        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.createExchange("Bearer $token", request).enqueue(object : Callback<ExchangeResponse> {
                override fun onResponse(call: Call<ExchangeResponse>, response: Response<ExchangeResponse>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val resBody = response.body()
                        totalPoinUser = resBody?.current_balance ?: (totalPoinUser - amountPoin)
                        onSuccess(resBody?.message ?: "Berhasil!", totalPoinUser)
                    } else onError("Saldo tidak cukup")
                }
                override fun onFailure(call: Call<ExchangeResponse>, t: Throwable) { isLoading = false }
            })
        }
    }

    fun submitSetoran(sampahIds: String, totalKoin: Int, lokasi: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val token = AuthTokenManager.authToken ?: return
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            val request = SetoranRequest(sampahIds, totalKoin, lokasi)
            ApiClient.instance.createSetoran("Bearer $token", request).enqueue(object : Callback<SetoranResponse> {
                override fun onResponse(call: Call<SetoranResponse>, response: Response<SetoranResponse>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        getSetoran()
                        onSuccess(response.body()?.message ?: "Berhasil!")
                    } else {
                        onError("Gagal memproses setoran")
                    }
                }
                override fun onFailure(call: Call<SetoranResponse>, t: Throwable) {
                    isLoading = false
                    onError("Kesalahan koneksi: ${t.message}")
                }
            })
        }
    }
}