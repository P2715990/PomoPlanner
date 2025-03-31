package com.example.pomoplanner.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.pomoplanner.model.DBHelper
import com.example.pomoplanner.model.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TasksTabViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context
        get() = getApplication<Application>().applicationContext
    val dbHelper: DBHelper = DBHelper(context)

    private var _tasks by mutableStateOf<List<Task>>(listOf<Task>())
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

    private var _addTaskErrorMessage by mutableStateOf("")
    val addTaskErrorMessage: String
        get() = _addTaskErrorMessage

    fun addTask(task: Task) {
        _addTaskErrorMessage = ""
        if (task.taskDetails == "") {
            _addTaskErrorMessage += "Please Enter Task Details"
        }
        if (task.taskDetails.length > 250) {
            _addTaskErrorMessage += "Task Details Shouldn't Be Longer Than 250 Characters"
        }

        if (task.taskCategory != null) {
            if (task.taskCategory.length > 50) {
                if (addTaskErrorMessage != "") {
                    _addTaskErrorMessage += "\n\n"
                }
                _addTaskErrorMessage += "Task Category Shouldn't Be Longer Than 50 Characters"
            }
        }
        if (addTaskErrorMessage == "") {
            dbHelper.addTask(task)
            getCurrentTasks(1, selectedDate)
            setShowAddTaskPopup(false)
        }
    }

    fun changeTaskIsCompleted(task: Task, isCompleted: Boolean) {
        _tasks.find { it.taskId == task.taskId }?.let { task ->
            task.taskIsCompleted = isCompleted
            dbHelper.updateTask(task)
        }
        updateBadge()
    }

    fun deleteTask(task: Task) {
        dbHelper.deleteTask(task)
        getCurrentTasks(1 /* TODO: IMPLEMENT PROFILE SYSTEM */, selectedDate)
        updateBadge()
    }

    fun getCurrentTasks(selectedProfile: Int, selectedDate: String) {
        _tasks = dbHelper.getTasks(selectedProfile, selectedDate)
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
        getCurrentTasks(1 /* TODO: IMPLEMENT PROFILE SYSTEM */, selectedDate)
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
            _addTaskErrorMessage = ""
            _showAddTaskPopup = true
        } else {
            _addTaskErrorMessage = ""
            _showAddTaskPopup = true
        }
    }

}