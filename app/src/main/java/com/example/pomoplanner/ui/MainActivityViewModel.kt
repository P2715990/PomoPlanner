package com.example.pomoplanner.ui

import android.app.Application
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.AndroidViewModel
import com.example.pomoplanner.model.DBHelper
import com.example.pomoplanner.model.Profile
import com.example.pomoplanner.model.Task
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.collections.List

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
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

    private var _selectedProfile by mutableStateOf<Profile?>(null)
    val selectedProfile: Profile?
        get() = _selectedProfile

    private var _filteredCategory by mutableStateOf("All")
    val filteredCategory: String
        get() = _filteredCategory

    private var _filteredPriority by mutableStateOf("All")
    val filteredPriority: String
        get() = _filteredPriority

    private var _filteredStatus by mutableIntStateOf(2)
    val filteredStatus: Int
        get() = _filteredStatus

    class TabBarItem(
        val title: String,
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector,
        initialIsDisabled: Boolean = false,
        initialBadgeAmount: Int? = null,
    ) {
        var isDisabled by mutableStateOf(initialIsDisabled)
        var badgeAmount by mutableStateOf(initialBadgeAmount)
    }

    // setup of individual navigation tabs
    val profileTab = TabBarItem(
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
    val tasksTab = TabBarItem(
        title = "Tasks",
        selectedIcon = Icons.AutoMirrored.Filled.List,
        unselectedIcon = Icons.AutoMirrored.Outlined.List,
        true
    )
    val pomodoroTab = TabBarItem(
        title = "Pomodoro",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications,
    )

    // creating list of navigation tabs
    val tabBarItems = listOf(profileTab, tasksTab, pomodoroTab)

    fun getCurrentTasks() {
        _tasks = listOf<Task>()

        if (selectedProfile != null) {
            if (selectedProfile!!.profileId != -1) {
                _tasks = dbHelper.getTasks(
                    selectedProfile!!.profileId,
                    selectedDate,
                    filteredCategory,
                    filteredPriority,
                    filteredStatus
                )
            }
        }

        updateBadge()
    }

    fun getSelectedProfile() {
        _selectedProfile = dbHelper.getSelectedProfile()
        tasksTab.isDisabled = _selectedProfile!!.profileId == -1
        getCurrentTasks()
    }

    fun swapSelectedProfile(profile: Profile) {
        if (selectedProfile != null) {
            _selectedProfile?.profileIsSelected = false
            dbHelper.updateProfile(selectedProfile!!)
        }
        profile.profileIsSelected = true
        dbHelper.updateProfile(profile)
        getSelectedProfile()
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
        getCurrentTasks()
    }

    fun resetFilters() {
        _filteredCategory = "All"
        _filteredPriority = "All"
        _filteredStatus = 2
        getCurrentTasks()
    }

    fun updateDate(dateMillis: Long?) {
        if (dateMillis != null) {
            val newDate: LocalDate =
                Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            val formattedNewDate: String = formatter.format(newDate)
            _selectedDate = formattedNewDate
            getCurrentTasks()
        }
    }
}