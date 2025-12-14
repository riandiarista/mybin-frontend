package com.example.mybin.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mybin.model.SetoranData

class SetoranViewModel : ViewModel() {
    private val _setoranList = mutableStateListOf<SetoranData>()
    val setoranList: List<SetoranData> get() = _setoranList

    init {
        // Dummy data
        _setoranList.add(
            SetoranData(
                id = "#20251016",
                tanggal = "16 Oktober 2025, 09:00 WIB",
                jenis = "5.0 kg Kertas & Kardus",
                lokasi = "Bank Sampah Sentral, Jakarta",
                status = "Diproses",
                totalKoin = 5000
            )
        )
        _setoranList.add(
            SetoranData(
                id = "#20251015",
                tanggal = "15 Oktober 2025, 14:30 WIB",
                jenis = "3.5 kg Sampah Campur (Plastik & Kertas)",
                lokasi = "Jl. Mahoni No. 5, Padang",
                status = "Selesai",
                totalKoin = 3500
            )
        )
    }

    fun addSetoran(setoran: SetoranData) {
        _setoranList.add(0, setoran)
    }
}
