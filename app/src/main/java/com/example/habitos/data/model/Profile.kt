package com.example.habitos.data.model

import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("created_at") val createdAt: String? = null
)

