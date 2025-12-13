package com.example.mybin.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mybin.model.SampahData

class SampahViewModel : ViewModel() {
    val sampahList = mutableStateListOf<SampahData>()

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
}