package com.example.mybin.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    // Fungsi ini yang dibutuhkan oleh MyFirebaseMessagingService
    fun getRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Tetap pertahankan ini jika screen lain (seperti Login) sudah memakainya
    val instance: ApiService by lazy {
        getRetrofitClient().create(ApiService::class.java)
    }
}