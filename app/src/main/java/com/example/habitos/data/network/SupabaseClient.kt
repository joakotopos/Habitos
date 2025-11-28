package com.example.habitos.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {

    private const val BASE_URL = "https://pairixgqshzufmtnxqqj.supabase.co/rest/v1/"

    val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBhaXJpeGdxaHNodXpmbXRueHFxaiIsInJvbGUiOiJhbm9uIiwiaWF0IjoxNzE2NDEzMTI4LCJleHAiOjIwMzE5ODkxMjh9.Cg3Ct6mdsE65NU6bXUIsDw_B55g0dKH-i-v3RaiUoow"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val taskApiService: TaskApiService = retrofit.create(TaskApiService::class.java)
}