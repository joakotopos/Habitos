package com.example.habitos.data.model

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("options") val options: SignUpOptions? = null
)

data class SignUpOptions(
    @SerializedName("data") val data: UserMetadata
)

data class UserMetadata(
    @SerializedName("name") val name: String
)

data class SignInRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("user") val user: AuthUser
)

data class AuthUser(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("user_metadata") val userMetadata: Map<String, Any>? = null
)

