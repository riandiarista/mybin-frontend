package com.example.mybin.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mybin.model.BeritaItemData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BeritaViewModel : ViewModel() {
    private val _beritaList = mutableStateListOf<BeritaItemData>()
    val beritaList: List<BeritaItemData> get() = _beritaList

    init {
        // Dummy data awal
        _beritaList.add(BeritaItemData(title = "Barusan Nyetor Kesekian...", description = "Deskripsi detail untuk berita ini...", date = "16 Oktober 2025, 09:00 WIB", location = "", status = "Diterbitkan"))
        _beritaList.add(BeritaItemData(title = "Lagi Ada Pengumpulan Sampah...", description = "Deskripsi detail tentang pengumpulan sampah...", date = "15 Oktober 2025, 14:30 WIB", location = "Painan", status = "Diterbitkan"))
        _beritaList.add(BeritaItemData(title = "Selamat Hari Bumi", description = "Perayaan hari bumi...", date = "2 Oktober 2025, 08:30 WIB", location = "Padang", status = "Diterbitkan"))
    }

    fun addBerita(judul: String, deskripsi: String, lokasi: String, imageUri: String? = null) {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm z", Locale("id", "ID"))
        val currentDate = dateFormat.format(Date())
        val newBerita = BeritaItemData(
            title = judul,
            description = deskripsi,
            date = currentDate,
            location = lokasi,
            status = "Diterbitkan", // Default status menjadi Diterbitkan
            imageUri = imageUri
        )
        _beritaList.add(0, newBerita)
    }

    fun updateBerita(id: String, judul: String, deskripsi: String, lokasi: String, imageUri: String? = null) {
        val index = _beritaList.indexOfFirst { it.id == id }
        if (index != -1) {
            val oldBerita = _beritaList[index]
            _beritaList[index] = oldBerita.copy(
                title = judul, 
                description = deskripsi, 
                location = lokasi, 
                status = "Diterbitkan",
                imageUri = imageUri ?: oldBerita.imageUri // Keep old image if new one is not provided
            )
        }
    }

    fun deleteBerita(id: String) {
        val index = _beritaList.indexOfFirst { it.id == id }
        if (index != -1) {
            _beritaList.removeAt(index)
        }
    }

    fun getBeritaById(id: String): BeritaItemData? {
        return _beritaList.find { it.id == id }
    }
}
