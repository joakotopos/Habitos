package com.example.habitos.data.model

import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("id") val id: String? = null, // Es nullable porque al crear una tarea, no tenemos el id
    @SerializedName("user_id") val userId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("type") val type: String, // "daily" o "weekly"
    @SerializedName("is_completed") val isCompleted: Boolean = false,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)