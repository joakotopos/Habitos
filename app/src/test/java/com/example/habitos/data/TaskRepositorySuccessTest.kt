package com.example.habitos.data

import com.example.habitos.data.model.Task
import com.example.habitos.data.network.TaskApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class TaskRepositorySuccessTest {

    private lateinit var taskRepository: TaskRepository
    private val mockApiService: TaskApiService = mock()

    private val testUserId = "test-user-id"
    private val dailyTasks = listOf(
        Task(id = "1", userId = testUserId, title = "Daily Task 1", description = "", type = "daily"),
        Task(id = "2", userId = testUserId, title = "Daily Task 2", description = "", type = "daily")
    )

    @Before
    fun setup() {
        // Inyectamos el mock en una nueva instancia del repositorio
        taskRepository = TaskRepository().apply {
            // Esto es una forma simple de inyección para el test.
            // En un proyecto más grande, usaríamos inyección de dependencias (Hilt, Koin).
            val apiServiceField = this::class.java.getDeclaredField("taskApiService")
            apiServiceField.isAccessible = true
            apiServiceField.set(this, mockApiService)
        }
    }

    @Test
    fun `getDailyTasks returns tasks from api service on success`() = runTest {
        // Given
        whenever(mockApiService.getTasks(org.mockito.kotlin.any(), org.mockito.kotlin.any(), org.mockito.kotlin.any(), org.mockito.kotlin.any()))
            .thenReturn(dailyTasks)

        // When
        val result = taskRepository.getDailyTasks(testUserId)

        // Then
        assertEquals(2, result.size)
        assertEquals("Daily Task 1", result[0].title)
    }
}