package com.example.pomoplanner.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.pomoplanner.model.DBHelper
import com.example.pomoplanner.model.Profile
import com.example.pomoplanner.model.Task

class TasksTabViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context
        get() = getApplication<Application>().applicationContext
    val dbHelper: DBHelper = DBHelper(context)

    private var _showFilterPopup by mutableStateOf(false)
    val showFilterPopup: Boolean
        get() = _showFilterPopup

    private var _showCalendarPopup by mutableStateOf(false)
    val showCalendarPopup: Boolean
        get() = _showCalendarPopup

    private var _showAddTaskPopup by mutableStateOf(false)
    val showAddTaskPopup: Boolean
        get() = _showAddTaskPopup

    private var _addTaskErrorMessage by mutableStateOf("")
    val addTaskErrorMessage: String
        get() = _addTaskErrorMessage

    private var _categoryOptions by mutableStateOf<List<String>>(listOf<String>("All"))
    val categoryOptions: List<String>
        get() = _categoryOptions

    fun getCategoryOptions(selectedProfile: Profile?, selectedDate: String) {
        if (selectedProfile != null) {
            val taskCategories: List<String> =
                dbHelper.getTaskCategories(selectedProfile.profileId, selectedDate)
            _categoryOptions = listOf("All") + taskCategories
        }
    }

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

            if (task.taskCategory == "All") {
                if (addTaskErrorMessage != "") {
                    _addTaskErrorMessage += "\n\n"
                }
                _addTaskErrorMessage += "Task Category Cannot Be \"All\""
            }
        }
        if (addTaskErrorMessage == "") {
            dbHelper.addTask(task)
            setShowAddTaskPopup(false)
        }
    }

    fun changeTaskIsCompleted(task: Task, isCompleted: Boolean) {
        task.taskIsCompleted = isCompleted
        dbHelper.updateTask(task)
    }

    fun deleteTask(task: Task) {
        dbHelper.deleteTask(task)
    }

    fun setShowFilterPopup(show: Boolean) {
        if (!show) {
            _showFilterPopup = false
        } else {
            setShowCalendarPopup(false)
            setShowAddTaskPopup(false)
            _showFilterPopup = true
        }
    }

    fun setShowCalendarPopup(show: Boolean) {
        if (!show) {
            _showCalendarPopup = false
        } else {
            setShowFilterPopup(false)
            setShowAddTaskPopup(false)
            _showCalendarPopup = true
        }
    }

    fun setShowAddTaskPopup(show: Boolean) {
        if (!show) {
            _showAddTaskPopup = false
        } else {
            setShowFilterPopup(false)
            setShowCalendarPopup(false)
            _addTaskErrorMessage = ""
            _showAddTaskPopup = true
        }
    }

}