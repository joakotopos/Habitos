package com.example.habitos.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskValidationUtil {
    fun isTaskTitleValid(title: String): Boolean {
        return title.isNotBlank() && title.length >= 3
    }
}

class TaskValidationTest {

    private val validator = TaskValidationUtil()

    @Test
    fun `title is valid when not blank and long enough`() {
        assertTrue(validator.isTaskTitleValid("Mi tarea"))
    }

    @Test
    fun `title is invalid when blank`() {
        assertFalse(validator.isTaskTitleValid("   "))
    }

    @Test
    fun `title is invalid when too short`() {
        assertFalse(validator.isTaskTitleValid("a"))
    }
}