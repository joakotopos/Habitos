package com.example.habitos.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {

    private const val BASE_URL = "https://pairixgqshzufmtnxqqj.supabase.co/rest/v1/"
    private const val AUTH_BASE_URL = "https://pairixgqshzufmtnxqqj.supabase.co/"

    val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBhaXJpeGdxc2h6dWZtdG54cXFqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQyOTUzNTIsImV4cCI6MjA3OTg3MTM1Mn0.-7NhR2GMn6vi8XQUyj0UqdVK58_IidiYeIhPEq2vWIM"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Retrofit para REST API
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit para Auth API
    private val authRetrofit = Retrofit.Builder()
        .baseUrl(AUTH_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val taskApiService: TaskApiService = retrofit.create(TaskApiService::class.java)
    val authApiService: AuthApiService = authRetrofit.create(AuthApiService::class.java)
}