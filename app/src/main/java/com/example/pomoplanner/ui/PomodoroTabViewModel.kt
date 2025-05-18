package com.example.pomoplanner.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.pomoplanner.model.DBHelper

class PomodoroTabViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context
        get() = getApplication<Application>().applicationContext
    val dbHelper: DBHelper = DBHelper(context)

    private var _pomodoroTimerDuration by mutableIntStateOf(1500)
    val pomodoroTimerDuration: Int
        get() = _pomodoroTimerDuration

    private var _shortBreakTimerDuration by mutableIntStateOf(300)
    val shortBreakTimerDuration: Int
        get() = _shortBreakTimerDuration

    private var _longBreakTimerDuration by mutableIntStateOf(900)
    val longBreakTimerDuration: Int
        get() = _longBreakTimerDuration

    private var _longBreakInterval by mutableIntStateOf(4)
    val longBreakInterval: Int
        get() = _longBreakInterval

    private var _pomodoroTitle by mutableStateOf("Pomodoro - Time to Work!")
    val pomodoroTitle: String
        get() = _pomodoroTitle

    fun getSettings() {
        _pomodoroTimerDuration = dbHelper.getSetting("Pomodoro Timer Duration (Seconds)")
        _shortBreakTimerDuration = dbHelper.getSetting("Short Break Timer Duration (Seconds)")
        _longBreakTimerDuration = dbHelper.getSetting("Long Break Timer Duration (Seconds)")
        _longBreakInterval = dbHelper.getSetting("Long Break Interval")
    }

    fun setPomodoroTitle(title: String) {
        _pomodoroTitle = title
    }
}