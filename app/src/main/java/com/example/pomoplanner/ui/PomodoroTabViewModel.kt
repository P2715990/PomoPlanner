package com.example.pomoplanner.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

    private var _timerState by mutableStateOf("Pomodoro")
    val timerState: String
        get() = _timerState

    private var _timerTotal by mutableIntStateOf(dbHelper.getSetting("Pomodoro Timer Duration (Seconds)"))
    val timerTotal: Int
        get() = _timerTotal

    private var _timerRemaining by mutableIntStateOf(timerTotal)
    val timerRemaining: Int
        get() = _timerRemaining

    private var _timerProgress by mutableFloatStateOf(1f)
    val timerProgress: Float
        get() = _timerProgress

    private var _timerIsRunning by mutableStateOf(false)
    val timerIsRunning: Boolean
        get() = _timerIsRunning

    private var _currentInterval by mutableIntStateOf(1)
    val currentInterval: Int
        get() = _currentInterval

    private var _showSettingsPopup by mutableStateOf(false)
    val showSettingsPopup: Boolean
        get() = _showSettingsPopup

    private var _settingsErrorMessage by mutableStateOf("")
    val settingsErrorMessage: String
        get() = _settingsErrorMessage

    fun resetTimer() {
        _timerState = "Pomodoro"
        _timerTotal = pomodoroTimerDuration
        _timerRemaining = timerTotal
        _timerProgress = 1f
        _timerIsRunning = false
        _currentInterval = 1
    }

    fun getSettings() {
        _pomodoroTimerDuration = dbHelper.getSetting("Pomodoro Timer Duration (Seconds)")
        _shortBreakTimerDuration = dbHelper.getSetting("Short Break Timer Duration (Seconds)")
        _longBreakTimerDuration = dbHelper.getSetting("Long Break Timer Duration (Seconds)")
        _longBreakInterval = dbHelper.getSetting("Long Break Interval")
    }

    fun setPomodoroTitle(title: String) {
        _pomodoroTitle = title
    }


    fun updateTimerState(state: String) {
        _timerState = state
    }

    fun setTimerTotal(seconds: Int) {
        _timerTotal = seconds
    }

    fun resetTimerRemaining() {
        _timerRemaining = timerTotal
    }

    fun decrementTimerRemaining() {
        _timerRemaining -= 1
    }

    fun skipTimer() {
        _timerRemaining = 0
    }

    fun setTimerProgress(progress: Float) {
        _timerProgress = progress
    }

    fun setTimerIsRunning(isRunning: Boolean) {
        _timerIsRunning = isRunning
    }

    fun incrementCurrentInterval() {
        _currentInterval += 1
    }

    fun resetCurrentInterval() {
        _currentInterval = 1
    }

    fun setShowSettingsPopup(show: Boolean) {
        _showSettingsPopup = show
    }

    fun resetPomodoroSettings() {
        dbHelper.resetDefaultSettings()
        setShowSettingsPopup(false)
        getSettings()
        resetTimer()
    }

    fun updatePomodoroSettings(
        pomodoroTimerDuration: Int,
        shortBreakTimerDuration: Int,
        longBreakTimerDuration: Int,
        longBreakInterval: Int
    ) {
        _settingsErrorMessage = ""

        if (pomodoroTimerDuration <= 0 || shortBreakTimerDuration <= 0 || longBreakTimerDuration <= 0) {
            _settingsErrorMessage += "Timer Duration Cannot Be Shorter Than 1 Second"
        }

        if (pomodoroTimerDuration > 3600 || shortBreakTimerDuration > 3600 || longBreakTimerDuration > 3600) {
            if (settingsErrorMessage != "") _settingsErrorMessage += "\n\n"
            _settingsErrorMessage += "Timer Duration Cannot Be Longer Than 3600 Seconds (60 Minutes)"
        }

        if (longBreakInterval <= 0 || longBreakInterval > 10) {
            if (settingsErrorMessage != "") _settingsErrorMessage += "\n\n"
            _settingsErrorMessage += "Long Break Interval Should Be Between 1-10"
        }

        if (settingsErrorMessage == "") {
            dbHelper.updateSetting("Pomodoro Timer Duration (Seconds)", pomodoroTimerDuration)
            dbHelper.updateSetting("Short Break Timer Duration (Seconds)", shortBreakTimerDuration)
            dbHelper.updateSetting("Long Break Timer Duration (Seconds)", longBreakTimerDuration)
            dbHelper.updateSetting("Long Break Interval", longBreakInterval)
            setShowSettingsPopup(false)
            getSettings()
            resetTimer()
        }
    }
}