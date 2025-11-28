package com.example.habitos.data.network

import com.example.habitos.data.model.Task
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface TaskApiService {

    @GET("tasks")
    suspend fun getTasks(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("user_id") userId: String,
        @Query("type") type: String
    ): List<Task>

    @POST("tasks")
    suspend fun createTask(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Prefer") prefer: String = "return=representation", // Para que devuelva el objeto creado
        @Body task: Task
    ): List<Task> // Supabase devuelve un array con el objeto creado
}