package com.example.pomoplanner.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

    private var _selectedProfile by mutableIntStateOf(1)
    val selectedProfile: Int
        get() = _selectedProfile

    private var _selectedDate by mutableStateOf(formattedDate)
    val selectedDate: String
        get() = _selectedDate

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

    private var _filteredCategory by mutableStateOf("All")
    val filteredCategory: String
        get() = _filteredCategory

    private var _filteredPriority by mutableStateOf("All")
    val filteredPriority: String
        get() = _filteredPriority

    private var _filteredStatus by mutableIntStateOf(2)
    val filteredStatus: Int
        get() = _filteredStatus

    // retrieve data from model

    fun getCurrentTasks() {
        _tasks = dbHelper.getTasks(selectedProfile, selectedDate, filteredCategory, filteredPriority, filteredStatus)
        updateBadge()
    }

    fun getCategoryOptions() {
        val taskCategories: List<String> = dbHelper.getTaskCategories(selectedProfile, selectedDate)
        _categoryOptions = listOf("All") + taskCategories
    }

    // add data to model

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
            getCurrentTasks()
            getCategoryOptions()
            setShowAddTaskPopup(false)
        }
    }

    // update data in model

    fun changeTaskIsCompleted(task: Task, isCompleted: Boolean) {
        _tasks.find { it.taskId == task.taskId }?.let { task ->
            task.taskIsCompleted = isCompleted
            dbHelper.updateTask(task)
        }
        updateBadge()
    }

    // delete data from model

    fun deleteTask(task: Task) {
        dbHelper.deleteTask(task)
        getCurrentTasks()
        updateBadge()
    }

    // update data in view model

    fun updateDate(date: String) {
        _selectedDate = date
        getCurrentTasks()
    }

    fun updateBadge() {
        val remainingTasks: List<Task> = _tasks.filter { it.taskIsCompleted == false }
        if (remainingTasks.isEmpty()) {
            tasksTab.badgeAmount = null
        } else {
            tasksTab.badgeAmount = remainingTasks.size
        }
    }

    fun updateFilters(category: String, priority: String, completion: Int) {
        _filteredCategory = category
        _filteredPriority = priority
        _filteredStatus = completion
        setShowFilterPopup(false)
    }

    fun resetFilters() {
        _filteredCategory = "All"
        _filteredPriority = "All"
        _filteredStatus = 2
        setShowFilterPopup(false)
    }

    // display and hide popups

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