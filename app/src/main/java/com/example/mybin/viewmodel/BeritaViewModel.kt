package com.example.mybin.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mybin.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeritaViewModel : ViewModel() {

    private val _beritaList = mutableStateOf<List<EdukasiData>>(emptyList())
    val beritaList: State<List<EdukasiData>> = _beritaList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // --- TAMBAHAN UNTUK ROLE CHECK ---
    private val _currentUsername = mutableStateOf<String?>(null)
    val currentUsername: State<String?> = _currentUsername

    init {
        fetchBerita()
    }

    /**
     * Memuat username dari AuthTokenManager ke dalam State ViewModel
     */
    fun loadCurrentUser(context: Context) {
        _currentUsername.value = AuthTokenManager.getUsername(context)
        Log.d("BeritaViewModel", "User loaded: ${_currentUsername.value}")
    }

    // --- KODE CRUD BERITA ---

    fun fetchBerita() {
        _isLoading.value = true
        ApiClient.instance.getEdukasi().enqueue(object : Callback<ListEdukasiResponse> {
            override fun onResponse(call: Call<ListEdukasiResponse>, response: Response<ListEdukasiResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _beritaList.value = response.body()?.data ?: emptyList()
                    Log.d("BeritaViewModel", "Fetch Success: ${_beritaList.value.size} items")
                } else {
                    Log.e("BeritaViewModel", "Fetch Failed: ${response.message()}")
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
                    fetchBerita()
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
                    fetchBerita()
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
                    fetchBerita()
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