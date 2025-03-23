package com.example.pomoplanner.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Task (
    val taskId: Int,
    val profileId: Int,
    val taskDate: String,
    val taskPriority: String,
    initialTaskIsCompleted: Boolean,
    val taskDetails: String
) {
    var taskIsCompleted by mutableStateOf(initialTaskIsCompleted)
}