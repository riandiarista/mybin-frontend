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

    private val _beritaList = mutableStateOf<List<EdukasiData>>(emptyList())
    val beritaList: State<List<EdukasiData>> = _beritaList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

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

    fun addBerita(
        token: String,
        judul: String,
        deskripsi: String,
        lokasi: String,
        cover: String?,
        onResult: (Boolean, String) -> Unit
    ) {
        _isLoading.value = true
        val request = EdukasiRequest(judul, deskripsi, lokasi, cover)

        ApiClient.instance.createEdukasi("Bearer $token", request).enqueue(object : Callback<EdukasiResponse> {
            override fun onResponse(call: Call<EdukasiResponse>, response: Response<EdukasiResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
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
     * PERBAIKAN: Fungsi untuk mengupdate berita yang sudah ada di Database.
     * Fungsi ini memanggil endpoint PUT api/edukasi/{id}
     */
    fun updateBerita(
        token: String,
        id: String,
        judul: String,
        deskripsi: String,
        lokasi: String,
        cover: String?,
        onResult: (Boolean, String) -> Unit
    ) {
        _isLoading.value = true
        val request = EdukasiRequest(judul, deskripsi, lokasi, cover)

        ApiClient.instance.updateEdukasi(id, "Bearer $token", request).enqueue(object : Callback<EdukasiResponse> {
            override fun onResponse(call: Call<EdukasiResponse>, response: Response<EdukasiResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    // Refresh list agar data yang diedit langsung muncul yang terbaru
                    fetchBerita(token)
                    onResult(true, "Berita berhasil diperbarui!")
                } else {
                    onResult(false, "Gagal memperbarui berita: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EdukasiResponse>, t: Throwable) {
                _isLoading.value = false
                onResult(false, "Terjadi kesalahan jaringan: ${t.message}")
            }
        })
    }

    /**
     * TAMBAHAN: Fungsi untuk menghapus berita dari Database.
     */
    fun deleteBerita(
        token: String,
        id: String,
        onResult: (Boolean, String) -> Unit
    ) {
        _isLoading.value = true
        ApiClient.instance.deleteEdukasi(id, "Bearer $token").enqueue(object : Callback<EdukasiResponse> {
            override fun onResponse(call: Call<EdukasiResponse>, response: Response<EdukasiResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    fetchBerita(token)
                    onResult(true, "Berita berhasil dihapus!")
                } else {
                    onResult(false, "Gagal menghapus berita")
                }
            }

            override fun onFailure(call: Call<EdukasiResponse>, t: Throwable) {
                _isLoading.value = false
                onResult(false, "Kesalahan jaringan: ${t.message}")
            }
        })
    }

    fun getBeritaById(id: String?): EdukasiData? {
        return _beritaList.value.find { it.id.toString() == id }
    }
}