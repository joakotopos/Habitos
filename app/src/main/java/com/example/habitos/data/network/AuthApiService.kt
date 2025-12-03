package com.example.habitos.data.network

import com.example.habitos.data.model.AuthResponse
import com.example.habitos.data.model.Profile
import com.example.habitos.data.model.SignInRequest
import com.example.habitos.data.model.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
    
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: SignUpRequest
    ): AuthResponse

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(
        @Header("apikey") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: SignInRequest
    ): AuthResponse    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("id") userId: String
    ): List<Profile>

    @POST("rest/v1/profiles")
    suspend fun createProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Prefer") prefer: String = "return=representation",
        @Body profile: Profile
    ): List<Profile>
}

