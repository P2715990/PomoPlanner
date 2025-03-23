package com.example.pomoplanner.model

class Task (
    val taskId: Int,
    val profileId: Int,
    val taskDate: String,
    val taskPriority: String,
    val taskIsCompleted: Boolean,
    val taskDetails: String
)