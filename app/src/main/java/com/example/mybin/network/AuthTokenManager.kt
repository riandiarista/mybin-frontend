package com.example.mybin.network

import android.content.Context

object AuthTokenManager {
    var authToken: String? = null


    fun saveToken(context: Context, token: String) {
        this.authToken = token
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("auth_token", token).apply()
    }


    fun getToken(context: Context): String? {
        if (authToken == null) {
            val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            authToken = prefs.getString("auth_token", null)
        }
        return authToken
    }




    fun saveUsername(context: Context, username: String) {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("username", username).apply()
    }


    fun getUsername(context: Context): String? {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return prefs.getString("username", null)
    }
}