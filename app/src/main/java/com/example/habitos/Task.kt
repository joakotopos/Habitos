package com.example.habitos

data class Task(
    val id: String,
    val content: String,
    var isComplete: Boolean,
    val imagePath: String? = null
)
