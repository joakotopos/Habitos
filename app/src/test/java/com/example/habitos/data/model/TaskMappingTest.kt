package com.example.habitos.data.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TaskMappingTest {

    private val gson = Gson()

    @Test
    fun `json from supabase is correctly mapped to Task data class`() {
        // Given
        val jsonString = """{
            "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
            "user_id": "user-123",
            "title": "Test a fondo de Supabase",
            "description": "Asegurarse que todo funcione.",
            "type": "weekly",
            "is_completed": false,
            "created_at": "2024-05-24T10:00:00Z",
            "updated_at": "2024-05-24T10:00:00Z"
        }"""

        // When
        val task = gson.fromJson(jsonString, Task::class.java)

        // Then
        assertEquals("a1b2c3d4-e5f6-7890-1234-567890abcdef", task.id)
        assertEquals("user-123", task.userId)
        assertEquals("Test a fondo de Supabase", task.title)
        assertEquals("weekly", task.type)
        assertFalse(task.isCompleted)
    }
}