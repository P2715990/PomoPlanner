package com.example.pomoplanner.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.pomoplanner.model.Task

class TasksTabViewModel: ViewModel() {
    val task1 = Task(
        1,
        1,
        "23/03/2025",
        "Low",
        false,
        "Wash Dishes"
    )
    val task2 = Task(
        2,
        1,
        "23/03/2025",
        "High",
        false,
        "Pay Taxes"
    )
    val task3 = Task(
        3,
        1,
        "23/03/2025",
        "Moderate",
        false,
        "Take Out Bins"
    )
    val task4 = Task(
        4,
        1,
        "23/03/2025",
        "Low",
        true,
        "Call Sister"
    )
    val task5 = Task(
        5,
        1,
        "23/03/2025",
        "Low",
        false,
        "Long rant to test big task descriptions. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    )
    val task6 = Task(
        6,
        1,
        "23/03/2025",
        "Moderate",
        false,
        "Eat Dinner"
    )

    private val _tasks = mutableStateListOf(task1, task2, task3, task4, task5, task6)
    val tasks: List<Task>
        get() = _tasks

    fun changeTaskIsCompleted (task: Task, isCompleted: Boolean) {
        _tasks.find { it.taskId == task.taskId }?.let { task ->
            task.taskIsCompleted = isCompleted
        }
    }

    fun removeTask (task: Task) {
        _tasks.remove(task)
    }
}