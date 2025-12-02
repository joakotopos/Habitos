package com.example.habitos.data.network

import com.example.habitos.data.model.Task
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
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
        @Header("Prefer") prefer: String = "return=representation",
        @Body task: Task
    ): List<Task>

    @PATCH("tasks")
    suspend fun updateTask(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Prefer") prefer: String = "return=representation",
        @Query("id") taskId: String,
        @Body task: Map<String, @JvmSuppressWildcards Any>
    ): List<Task>

    @DELETE("tasks")
    suspend fun deleteTask(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("id") taskId: String
    )
}