package com.example.habitos.data

import com.example.habitos.data.model.Task
import com.example.habitos.data.network.SupabaseClient

class TaskRepository {

    private val taskApiService = SupabaseClient.taskApiService
    private val apiKey = SupabaseClient.supabaseKey
    private val authorization = "Bearer ${SupabaseClient.supabaseKey}"

    suspend fun getDailyTasks(userId: String): List<Task> {
        return taskApiService.getTasks(apiKey, authorization, "eq.$userId", "eq.daily")
    }

    suspend fun getWeeklyTasks(userId: String): List<Task> {
        return taskApiService.getTasks(apiKey, authorization, "eq.$userId", "eq.weekly")
    }

    suspend fun createTask(task: Task): Task? {
        val response = taskApiService.createTask(apiKey, authorization, task = task)
        return response.firstOrNull()
    }
}