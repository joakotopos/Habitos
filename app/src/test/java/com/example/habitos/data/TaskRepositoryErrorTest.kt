package com.example.habitos.data

import com.example.habitos.data.network.TaskApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

class TaskRepositoryErrorTest {

    private lateinit var taskRepository: TaskRepository
    private val mockApiService: TaskApiService = mock()

    @Before
    fun setup() {
        taskRepository = TaskRepository().apply {
            val apiServiceField = this::class.java.getDeclaredField("taskApiService")
            apiServiceField.isAccessible = true
            apiServiceField.set(this, mockApiService)
        }
    }

    @Test
    fun `getDailyTasks throws exception when api service fails`() = runTest {
        // Given
        val errorMessage = "Network Error"
        whenever(mockApiService.getTasks(any(), any(), any(), any()))
            .thenThrow(IOException(errorMessage))

        // When & Then
        assertThrows(IOException::class.java) {
            runTest { // runTest anidado para manejar la corrutina dentro de assertThrows
                taskRepository.getDailyTasks("any-user")
            }
        }
    }
}