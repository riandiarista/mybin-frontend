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
import com.example.mybin.network.Sampah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SampahViewModel : ViewModel() {
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

    fun saveSampahToApi(
        jenis: String,
        berat: Float,
        detail: String,
        coin: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) {
            onError("Token otentikasi tidak ditemukan. Silakan login ulang.")
            return
        }

        val requestBody = SampahRequest(
            jenis = jenis,
            berat = berat,
            detail = detail,
            coin = coin,
            foto = null
        )

        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.createSampah(
                token = "Bearer $token",
                request = requestBody
            ).enqueue(object : Callback<SampahResponse> {
                override fun onResponse(call: Call<SampahResponse>, response: Response<SampahResponse>) {
                    if (response.isSuccessful) {
                        onSuccess("Data sampah berhasil disimpan ke database!")
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Respons error tidak dikenal"
                        onError("Gagal menyimpan data (Kode: ${response.code()}). Error: $errorBody")
                        Log.e("API_ERROR", "Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<SampahResponse>, t: Throwable) {
                    onError("Gagal terhubung ke server. Pesan: ${t.message}")
                    Log.e("API_FAIL", "Pesan: ${t.message}", t)
                }
            })
        }
    }

    /**
     * Fungsi untuk memuat data sampah milik user dari backend.
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
            ApiClient.instance.getSampah(
                token = "Bearer $token"
            ).enqueue(object : Callback<ListSampahResponse> {
                override fun onResponse(call: Call<ListSampahResponse>, response: Response<ListSampahResponse>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val remoteSampahList = response.body()?.data ?: emptyList()

                        // Kosongkan list lokal dan isi dengan data dari database
                        sampahList.clear()
                        remoteSampahList.forEach { sampah ->
                            // Konversi model backend (Sampah) ke model frontend (SampahData)
                            sampahList.add(
                                SampahData(
                                    id = sampah.id.toString(), // Gunakan ID dari database
                                    jenisSampah = sampah.jenis,
                                    detailSampah = sampah.detail ?: "Tidak ada detail",
                                    totalBobot = "${sampah.berat} Kg",
                                    imageUri = null, // Perlu implementasi terpisah untuk Uri/Foto
                                    estimasiKoin = sampah.coin
                                )
                            )
                        }
                        onSuccess("Berhasil memuat ${sampahList.size} data sampah dari database.")
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Respons error tidak dikenal"
                        onError("Gagal memuat data (Kode: ${response.code()}). Error: $errorBody")
                        Log.e("API_LOAD_ERROR", "Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<ListSampahResponse>, t: Throwable) {
                    isLoading = false
                    onError("Gagal terhubung ke server: ${t.message}")
                    Log.e("API_LOAD_FAIL", "Pesan: ${t.message}", t)
                }
            })
        }
    }

    /**
     * Fungsi untuk memperbarui data sampah yang ada di backend.
     */
    fun updateSampahInApi(
        id: String, // ID sampah yang akan diupdate
        jenis: String,
        berat: Float,
        detail: String,
        coin: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) {
            onError("Token otentikasi tidak ditemukan. Silakan login ulang.")
            return
        }

        val requestBody = SampahRequest(
            jenis = jenis,
            berat = berat,
            detail = detail,
            coin = coin,
            foto = null // Implementasi upload foto akan terpisah
        )

        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.updateSampah( // Panggil API Service Update
                id = id,
                token = "Bearer $token",
                request = requestBody
            ).enqueue(object : Callback<SampahResponse> {
                override fun onResponse(call: Call<SampahResponse>, response: Response<SampahResponse>) {
                    if (response.isSuccessful) {
                        // Setelah berhasil update di backend, panggil loadSampah untuk refresh list di UI
                        loadSampah(
                            onSuccess = { onSuccess("Data sampah ID $id berhasil diperbarui!") },
                            onError = { error -> onError("Data sampah ID $id berhasil diperbarui, tetapi gagal me-refresh list: $error") }
                        )
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Respons error tidak dikenal"
                        onError("Gagal memperbarui data (Kode: ${response.code()}). Error: $errorBody")
                        Log.e("API_UPDATE_ERROR", "Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<SampahResponse>, t: Throwable) {
                    onError("Gagal terhubung ke server. Pesan: ${t.message}")
                    Log.e("API_UPDATE_FAIL", "Pesan: ${t.message}", t)
                }
            })
        }
    }

    /**
     * Fungsi baru untuk menghapus data sampah dari backend.
     */
    fun deleteSampahInApi(
        id: String, // ID sampah yang akan dihapus
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = AuthTokenManager.authToken
        if (token.isNullOrEmpty()) {
            onError("Token otentikasi tidak ditemukan. Silakan login ulang.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.deleteSampah(
                id = id,
                token = "Bearer $token"
            ).enqueue(object : Callback<SampahResponse> {
                override fun onResponse(call: Call<SampahResponse>, response: Response<SampahResponse>) {
                    if (response.isSuccessful) {
                        // Refresh list setelah berhasil dihapus
                        loadSampah(
                            onSuccess = { onSuccess(response.body()?.message ?: "Data sampah berhasil dihapus!") },
                            onError = { error -> onError("Data sampah berhasil dihapus, tetapi gagal me-refresh list: $error") }
                        )
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Respons error tidak dikenal"
                        onError("Gagal menghapus data (Kode: ${response.code()}). Error: $errorBody")
                        Log.e("API_DELETE_ERROR", "Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<SampahResponse>, t: Throwable) {
                    onError("Gagal terhubung ke server. Pesan: ${t.message}")
                    Log.e("API_DELETE_FAIL", "Pesan: ${t.message}", t)
                }
            })
        }
    }
}