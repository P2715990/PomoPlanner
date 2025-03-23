package com.example.pomoplanner.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.pomoplanner.model.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TasksTabViewModel : ViewModel() {
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

    val date: LocalDate = LocalDate.now()
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val formattedDate: String = formatter.format(date)

    private var _selectedDate by mutableStateOf(formattedDate)
    val selectedDate: String
        get() = _selectedDate

    private var _showCalendarPopup by mutableStateOf(false)
    val showCalendarPopup: Boolean
        get() = _showCalendarPopup

    private var _showAddTaskPopup by mutableStateOf(false)
    val showAddTaskPopup: Boolean
        get() = _showAddTaskPopup

    fun changeTaskIsCompleted(task: Task, isCompleted: Boolean) {
        _tasks.find { it.taskId == task.taskId }?.let { task ->
            task.taskIsCompleted = isCompleted
        }
        updateBadge()
    }

    fun removeTask(task: Task) {
        _tasks.remove(task)
        updateBadge()
    }

    fun updateBadge() {
        val remainingTasks: List<Task> = _tasks.filter { it.taskIsCompleted == false }
        if (remainingTasks.isEmpty()) {
            tasksTab.badgeAmount = null
        } else {
            tasksTab.badgeAmount = remainingTasks.size
        }
    }

    fun updateDate(date: String) {
        _selectedDate = date
    }

    fun setShowCalendarPopup(show: Boolean) {
        if (!show) {
            _showCalendarPopup = false
        } else if (showAddTaskPopup) {
            setShowAddTaskPopup(false)
            _showCalendarPopup = true
        } else {
            _showCalendarPopup = true
        }
    }

    fun setShowAddTaskPopup(show: Boolean) {
        if (!show) {
            _showAddTaskPopup = false
        } else if (showCalendarPopup) {
            setShowCalendarPopup(false)
            _showAddTaskPopup = true
        } else {
            _showAddTaskPopup = true
        }
    }

}