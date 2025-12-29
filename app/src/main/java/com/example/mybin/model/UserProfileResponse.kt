package com.example.mybin.model

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UserData
)

data class UserData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,


    @SerializedName("total_poin_user")
    val totalPoinUser: Int
)