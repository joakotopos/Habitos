package com.example.habitos.data

import com.example.habitos.data.model.Task
import com.example.habitos.data.network.SupabaseClient

class TaskRepository(private val accessToken: String) {

    private val taskApiService = SupabaseClient.taskApiService
    private val apiKey = SupabaseClient.supabaseKey
    private val authorization = "Bearer $accessToken"

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

    suspend fun updateTask(taskId: String, updates: Map<String, @JvmSuppressWildcards Any>): Task? {
        val response = taskApiService.updateTask(apiKey, authorization, taskId = "eq.$taskId", task = updates)
        return response.firstOrNull()
    }

    suspend fun markTaskAsCompleted(taskId: String): Task? {
        val updates: Map<String, Any> = mapOf("is_completed" to true)
        return updateTask(taskId, updates)
    }

    suspend fun updateTaskCompletionStatus(taskId: String, isCompleted: Boolean): Task? {
        val updates: Map<String, Any> = mapOf("is_completed" to isCompleted)
        return updateTask(taskId, updates)
    }

    suspend fun deleteTask(taskId: String) {
        android.util.Log.d("TaskRepository", "Intentando eliminar tarea con ID: $taskId")
        try {
            taskApiService.deleteTask(apiKey, authorization, "eq.$taskId")
            android.util.Log.d("TaskRepository", "Tarea eliminada exitosamente")
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            android.util.Log.e("TaskRepository", "Error HTTP ${e.code()} al eliminar: $errorBody")
            throw e
        } catch (e: Exception) {
            android.util.Log.e("TaskRepository", "Error al eliminar tarea: ${e.message}", e)
            throw e
        }
    }
}