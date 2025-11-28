package com.example.habitos.logic

import com.example.habitos.data.model.Task
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskFilterUtil {
    fun filterCompletedTasks(tasks: List<Task>): List<Task> {
        return tasks.filter { it.isCompleted }
    }
}

class TaskFilterTest {

    private val filterUtil = TaskFilterUtil()

    @Test
    fun `filterCompletedTasks returns only completed tasks`() {
        // Given
        val tasks = listOf(
            Task(id = "1", userId = "user1", title = "Task 1", description = "", type = "daily", isCompleted = true),
            Task(id = "2", userId = "user1", title = "Task 2", description = "", type = "daily", isCompleted = false),
            Task(id = "3", userId = "user1", title = "Task 3", description = "", type = "weekly", isCompleted = true)
        )

        // When
        val result = filterUtil.filterCompletedTasks(tasks)

        // Then
        assertEquals(2, result.size)
        assertEquals("1", result[0].id)
        assertEquals("3", result[1].id)
    }

    @Test
    fun `filterCompletedTasks returns empty list when no tasks are completed`() {
        // Given
        val tasks = listOf(
            Task(id = "1", userId = "user1", title = "Task 1", description = "", type = "daily", isCompleted = false),
            Task(id = "2", userId = "user1", title = "Task 2", description = "", type = "daily", isCompleted = false)
        )

        // When
        val result = filterUtil.filterCompletedTasks(tasks)

        // Then
        assertEquals(0, result.size)
    }
}