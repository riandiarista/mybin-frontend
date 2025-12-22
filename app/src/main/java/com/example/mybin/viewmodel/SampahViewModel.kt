package com.example.mybin.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybin.model.SampahData
import com.example.mybin.model.SampahRequest
import com.example.mybin.model.SampahResponse
import com.example.mybin.network.ApiClient
import com.example.mybin.network.AuthTokenManager
import com.example.mybin.network.ListSampahResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SampahViewModel : ViewModel() {
    // List yang dipantau oleh UI Compose
    val sampahList = mutableStateListOf<SampahData>()

    // State untuk indikator loading di UI
    var isLoading by mutableStateOf(false)

    fun addSampah(sampah: SampahData) {
        sampahList.add(sampah)
    }

    fun deleteSampah(sampahId: String) {
        sampahList.removeAll { it.id == sampahId }
    }

    fun getSampahById(sampahId: String?): SampahData? {
        return sampahList.find { it.id == sampahId }
    }

    fun updateSampah(updatedSampah: SampahData) {
        val index = sampahList.indexOfFirst { it.id == updatedSampah.id }
        if (index != -1) {
            sampahList[index] = updatedSampah
        }
    }

    /**
     * Menyimpan data sampah baru ke API.
     */
    fun saveSampahToApi(
        jenis: String,
        berat: Float,
        detail: String,
        coin: Int,
        foto: String?, // Data Base64
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) {
            onError("Token otentikasi tidak ditemukan. Silakan login ulang.")
            return
        }

        // Sanitasi Base64: Menghapus spasi atau baris baru agar tidak rusak saat dikirim
        val sanitizedFoto = foto?.replace("\\s".toRegex(), "")

        val requestBody = SampahRequest(
            jenis = jenis,
            berat = berat,
            detail = detail,
            coin = coin,
            foto = sanitizedFoto
        )

        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.createSampah(
                token = "Bearer $token",
                request = requestBody
            ).enqueue(object : Callback<SampahResponse> {
                override fun onResponse(call: Call<SampahResponse>, response: Response<SampahResponse>) {
                    if (response.isSuccessful) {
                        // Memuat ulang data agar list di SampahkuScreen terupdate otomatis
                        loadSampah({ onSuccess("Data sampah berhasil disimpan!") }, { onError(it) })
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Respons error tidak dikenal"
                        onError("Gagal menyimpan data (Kode: ${response.code()})")
                        Log.e("API_ERROR", "Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<SampahResponse>, t: Throwable) {
                    onError("Gagal terhubung ke server: ${t.message}")
                }
            })
        }
    }

    /**
     * Memuat data sampah dari backend.
     */
    fun loadSampah(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) {
            onError("Token tidak ditemukan. Mohon login ulang.")
            return
        }

        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.getSampah(token = "Bearer $token").enqueue(object : Callback<ListSampahResponse> {
                override fun onResponse(call: Call<ListSampahResponse>, response: Response<ListSampahResponse>) {
                    // Pastikan update UI dilakukan di Main Thread
                    viewModelScope.launch(Dispatchers.Main) {
                        isLoading = false
                        if (response.isSuccessful) {
                            val remoteSampahList = response.body()?.data ?: emptyList()

                            // LOG PENTING: Untuk melacak apakah string foto terisi atau kosong (0)
                            remoteSampahList.forEachIndexed { index, s ->
                                Log.d("SAMP_VM", "Item $index | ID: ${s.id} | FotoLength: ${s.foto?.length ?: 0}")
                            }

                            sampahList.clear()
                            remoteSampahList.forEach { sampah ->
                                sampahList.add(
                                    SampahData(
                                        id = sampah.id.toString(),
                                        jenisSampah = sampah.jenis ?: "Tanpa Jenis",
                                        detailSampah = sampah.detail ?: "Tidak ada detail",
                                        totalBobot = "${sampah.berat ?: 0f} Kg",
                                        imageUri = null,
                                        estimasiKoin = sampah.coin ?: 0,
                                        foto = sampah.foto // Mengambil string Base64 dari backend
                                    )
                                )
                            }
                            onSuccess("Berhasil memuat data.")
                            Log.d("SAMP_VM", "Total data dimuat: ${sampahList.size} item")
                        } else {
                            onError("Gagal memuat data (Kode: ${response.code()})")
                        }
                    }
                }

                override fun onFailure(call: Call<ListSampahResponse>, t: Throwable) {
                    viewModelScope.launch(Dispatchers.Main) {
                        isLoading = false
                        onError("Koneksi gagal: ${t.message}")
                    }
                }
            })
        }
    }

    /**
     * Memperbarui data sampah di backend.
     */
    fun updateSampahInApi(
        id: String,
        jenis: String,
        berat: Float,
        detail: String,
        coin: Int,
        foto: String?,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) {
            onError("Token tidak ditemukan.")
            return
        }

        // Sanitasi Base64
        val sanitizedFoto = foto?.replace("\\s".toRegex(), "")

        val requestBody = SampahRequest(
            jenis = jenis,
            berat = berat,
            detail = detail,
            coin = coin,
            foto = sanitizedFoto
        )

        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.updateSampah(
                id = id,
                token = "Bearer $token",
                request = requestBody
            ).enqueue(object : Callback<SampahResponse> {
                override fun onResponse(call: Call<SampahResponse>, response: Response<SampahResponse>) {
                    if (response.isSuccessful) {
                        loadSampah(
                            onSuccess = { onSuccess("Berhasil diperbarui!") },
                            onError = { onError("Update sukses, tapi gagal refresh.") }
                        )
                    } else {
                        onError("Gagal update (Kode: ${response.code()})")
                    }
                }

                override fun onFailure(call: Call<SampahResponse>, t: Throwable) {
                    onError("Koneksi gagal: ${t.message}")
                }
            })
        }
    }

    /**
     * Menghapus data sampah dari backend.
     */
    fun deleteSampahInApi(
        id: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) {
            onError("Token tidak ditemukan.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.deleteSampah(
                id = id,
                token = "Bearer $token"
            ).enqueue(object : Callback<SampahResponse> {
                override fun onResponse(call: Call<SampahResponse>, response: Response<SampahResponse>) {
                    if (response.isSuccessful) {
                        loadSampah(
                            onSuccess = { onSuccess("Data berhasil dihapus!") },
                            onError = { onError("Hapus sukses, tapi gagal refresh.") }
                        )
                    } else {
                        onError("Gagal hapus (Kode: ${response.code()})")
                    }
                }

                override fun onFailure(call: Call<SampahResponse>, t: Throwable) {
                    onError("Koneksi gagal: ${t.message}")
                }
            })
        }
    }
}