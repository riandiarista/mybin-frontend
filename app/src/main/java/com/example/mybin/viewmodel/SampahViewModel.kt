package com.example.mybin.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mybin.model.SampahData

class SampahViewModel : ViewModel() {
    private val _sampahList = mutableStateListOf<SampahData>()
    val sampahList: List<SampahData> get() = _sampahList

    init {
        // Dummy data for initial display
        _sampahList.addAll(listOf(
            SampahData(jenisSampah = "An Organik", detailSampah = "Botol Plastik", totalBobot = "1.5 Kg", imageUri = null),
            SampahData(jenisSampah = "Organik", detailSampah = "Rumput", totalBobot = "5.0 Kg", imageUri = null),
            SampahData(jenisSampah = "Sampah B3", detailSampah = "Baterai", totalBobot = "1.50 Kg", imageUri = null)
        ))
    }

    fun addSampah(jenisSampah: String, detailSampah: String, totalBobot: String, imageUri: Uri?) {
        val newSampah = SampahData(
            jenisSampah = jenisSampah,
            detailSampah = detailSampah,
            totalBobot = totalBobot,
            imageUri = imageUri
        )
        _sampahList.add(0, newSampah)
    }

    fun deleteSampah(id: String) {
        _sampahList.removeAll { it.id == id }
    }
}
