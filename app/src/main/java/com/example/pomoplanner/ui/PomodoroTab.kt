package com.example.pomoplanner.ui

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pomoplanner.R
import com.example.pomoplanner.ui.theme.Purple80
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

fun timerFormat(seconds: Int): String {
    return String.format(Locale.ROOT, "%02d:%02d", seconds / 60, seconds % 60)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroTab(
    // mainActivityViewModel: MainActivityViewModel,
    pomodoroTabViewModel: PomodoroTabViewModel,
) {
    pomodoroTabViewModel.getSettings()

    val navController = rememberNavController()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            PomoTopBar(
                pomodoroTabViewModel.pomodoroTitle,
                { show ->
                    pomodoroTabViewModel.setShowSettingsPopup(show)
                },
            )
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = pomodoroTabViewModel.timerState
        ) {
            composable("Pomodoro") {
                PomoTimerView(
                    navController,
                    { title ->
                        pomodoroTabViewModel.setPomodoroTitle(title)
                    },
                    pomodoroTabViewModel.timerTotal,
                    { seconds ->
                        pomodoroTabViewModel.setTimerTotal(seconds)
                    },
                    { pomodoroTabViewModel.resetTimerRemaining() },
                    pomodoroTabViewModel.longBreakInterval,
                    pomodoroTabViewModel.currentInterval,
                    { pomodoroTabViewModel.incrementCurrentInterval() },
                    { pomodoroTabViewModel.resetCurrentInterval() },
                    pomodoroTabViewModel.timerRemaining,
                    { pomodoroTabViewModel.decrementTimerRemaining() },
                    pomodoroTabViewModel.timerProgress,
                    { progress ->
                        pomodoroTabViewModel.setTimerProgress(progress)
                    },
                    pomodoroTabViewModel.timerIsRunning,
                    { isRunning ->
                        pomodoroTabViewModel.setTimerIsRunning(isRunning)
                    },
                    { state ->
                        pomodoroTabViewModel.updateTimerState(state)
                    },
                    pomodoroTabViewModel.shortBreakTimerDuration,
                    pomodoroTabViewModel.longBreakTimerDuration
                )
            }
            composable("Short Break") {
                BreakTimerView(
                    navController,
                    { title ->
                        pomodoroTabViewModel.setPomodoroTitle(title)
                    },
                    pomodoroTabViewModel.timerTotal,
                    { seconds ->
                        pomodoroTabViewModel.setTimerTotal(seconds)
                    },
                    { pomodoroTabViewModel.resetTimerRemaining() },
                    pomodoroTabViewModel.timerRemaining,
                    { pomodoroTabViewModel.decrementTimerRemaining() },
                    pomodoroTabViewModel.timerProgress,
                    { progress ->
                        pomodoroTabViewModel.setTimerProgress(progress)
                    },
                    pomodoroTabViewModel.timerIsRunning,
                    { isRunning ->
                        pomodoroTabViewModel.setTimerIsRunning(isRunning)
                    },
                    pomodoroTabViewModel.timerState,
                    { state ->
                        pomodoroTabViewModel.updateTimerState(state)
                    },
                    pomodoroTabViewModel.pomodoroTimerDuration
                )
            }
            composable("Long Break") {
                BreakTimerView(
                    navController,
                    { title ->
                        pomodoroTabViewModel.setPomodoroTitle(title)
                    },
                    pomodoroTabViewModel.timerTotal,
                    { seconds ->
                        pomodoroTabViewModel.setTimerTotal(seconds)
                    },
                    { pomodoroTabViewModel.resetTimerRemaining() },
                    pomodoroTabViewModel.timerRemaining,
                    { pomodoroTabViewModel.decrementTimerRemaining() },
                    pomodoroTabViewModel.timerProgress,
                    { progress ->
                        pomodoroTabViewModel.setTimerProgress(progress)
                    },
                    pomodoroTabViewModel.timerIsRunning,
                    { isRunning ->
                        pomodoroTabViewModel.setTimerIsRunning(isRunning)
                    },
                    pomodoroTabViewModel.timerState,
                    { state ->
                        pomodoroTabViewModel.updateTimerState(state)
                    },
                    pomodoroTabViewModel.pomodoroTimerDuration
                )
            }
        }
    }

    CustomPopupHelper(
        showPopup = pomodoroTabViewModel.showSettingsPopup,
        onClickOutside = { pomodoroTabViewModel.setShowSettingsPopup(false) },
        content = {
            SettingsView(
                pomodoroTabViewModel.pomodoroTimerDuration,
                pomodoroTabViewModel.shortBreakTimerDuration,
                pomodoroTabViewModel.longBreakTimerDuration,
                pomodoroTabViewModel.longBreakInterval,
                pomodoroTabViewModel.settingsErrorMessage,
                { p1, p2, p3, p4 ->
                    pomodoroTabViewModel.updatePomodoroSettings(p1, p2, p3, p4)
                },
                { pomodoroTabViewModel.resetPomodoroSettings() }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomoTopBar(
    title: String,
    onSettingsButtonClicked: (Boolean) -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = { onSettingsButtonClicked(true) }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PomoTimerView(
    navController: NavController,
    onActive: (String) -> Unit,
    timerDuration: Int,
    setTimerTotal: (Int) -> Unit,
    resetRemainingTime: () -> Unit,
    longBreakInterval: Int,
    currentInterval: Int,
    incrementCurrentInterval: () -> Unit,
    resetCurrentInterval: () -> Unit,
    remainingTime: Int,
    decrementRemainingTime: () -> Unit,
    progress: Float,
    setProgress: (Float) -> Unit,
    isRunning: Boolean,
    setIsRunning: (Boolean) -> Unit,
    updateTimerState: (String) -> Unit,
    shortBreakDuration: Int,
    longBreakDuration: Int,
) {
    val context = LocalContext.current
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.timer_alarm)
    }

    onActive("Pomodoro - Time to Work!")

    LaunchedEffect(isRunning, remainingTime) {
        while (remainingTime > 0 && isRunning) {
            setProgress(remainingTime.toFloat() / timerDuration.toFloat())
            delay(1000)
            decrementRemainingTime()
        }
        if (remainingTime <= 0) {
            setIsRunning(false)
            // notification manager show pomo done
            CoroutineScope(Dispatchers.Main).launch {
                mediaPlayer.start()
                delay(3000)
                mediaPlayer.stop()
            }
            incrementCurrentInterval()
            if (currentInterval >= longBreakInterval) {
                resetCurrentInterval()
                updateTimerState("Long Break")
                setTimerTotal(longBreakDuration)
                resetRemainingTime()
                setProgress(1f)
                navController.navigate("Long Break")
            } else {
                updateTimerState("Short Break")
                setTimerTotal(shortBreakDuration)
                resetRemainingTime()
                setProgress(1f)
                navController.navigate("Short Break")
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimerWidget(
            progress,
            remainingTime
        )

        Button(
            modifier = Modifier
                .width(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .padding(top = 24.dp),
            onClick = { setIsRunning(!isRunning) }
        ) {
            Text(
                text = if (isRunning) "Pause" else "Start",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun BreakTimerView(
    navController: NavController,
    onActive: (String) -> Unit,
    timerDuration: Int,
    setTimerTotal: (Int) -> Unit,
    resetRemainingTime: () -> Unit,
    remainingTime: Int,
    decrementRemainingTime: () -> Unit,
    progress: Float,
    setProgress: (Float) -> Unit,
    isRunning: Boolean,
    setIsRunning: (Boolean) -> Unit,
    timerState: String,
    updateTimerState: (String) -> Unit,
    pomodoroDuration: Int,
) {
    val context = LocalContext.current
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.timer_alarm)
    }

    onActive("$timerState - Take a Rest!")

    LaunchedEffect(isRunning, remainingTime) {
        while (remainingTime > 0 && isRunning) {
            setProgress(remainingTime.toFloat() / timerDuration.toFloat())
            delay(1000)
            decrementRemainingTime()
        }
        if (remainingTime <= 0) {
            setIsRunning(false)
            // notification manager show break done
            CoroutineScope(Dispatchers.Main).launch {
                mediaPlayer.start()
                delay(3000)
                mediaPlayer.stop()
            }
            updateTimerState("Pomodoro")
            setTimerTotal(pomodoroDuration)
            resetRemainingTime()
            setProgress(1f)
            navController.navigate("Pomodoro")
        }
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimerWidget(
            progress,
            remainingTime
        )

        Button(
            modifier = Modifier
                .width(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .padding(top = 24.dp),
            onClick = { setIsRunning(!isRunning) }
        ) {
            Text(
                text = if (isRunning) "Pause" else "Start",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun TimerWidget(
    progress: Float,
    remainingTime: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .size(250.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            8.dp
                        ),
                        shape = CircleShape
                    ),
                color = Purple80,
                strokeWidth = 12.dp,
            )
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = timerFormat(remainingTime),
                    fontSize = 48.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    pomodoroDuration: Int,
    shortBreakDuration: Int,
    longBreakDuration: Int,
    longBreakInterval: Int,
    settingsErrorMessage: String,
    onConfirmClicked: (Int, Int, Int, Int) -> Unit,
    onDefaultClicked: () -> Unit,
) {
    var pomoDurationText by remember { mutableStateOf("$pomodoroDuration") }
    var shortDurationText by remember { mutableStateOf("$shortBreakDuration") }
    var longDurationText by remember { mutableStateOf("$longBreakDuration") }
    var longIntervalText by remember { mutableStateOf("$longBreakInterval") }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pomodoro Settings",
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = pomoDurationText,
            onValueChange = { if (it.isDigitsOnly()) pomoDurationText = it },
            label = { Text("Pomodoro Duration (Seconds)") },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.NumberPassword
            )
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = shortDurationText,
            onValueChange = { if (it.isDigitsOnly()) shortDurationText = it },
            label = { Text("Short Break Duration (Seconds)") },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.NumberPassword
            )
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = longDurationText,
            onValueChange = { if (it.isDigitsOnly()) longDurationText = it },
            label = { Text("Long Break Duration (Seconds)") },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.NumberPassword
            )
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = longIntervalText,
            onValueChange = { if (it.isDigitsOnly()) longIntervalText = it },
            label = { Text("Long Break Interval") },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.NumberPassword
            )
        )

        Row(
            modifier = Modifier
                .height(intrinsicSize = IntrinsicSize.Max),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                modifier = Modifier
                    .weight(1F)
                    .padding(
                        end = 8.dp
                    )
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary),
                onClick = {
                    onDefaultClicked()
                },
                shape = RectangleShape
            ) {
                Text(
                    text = "Reset to Default",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            OutlinedButton(
                modifier = Modifier
                    .weight(1F)
                    .padding(
                        start = 8.dp
                    )
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary),
                onClick = {
                    onConfirmClicked(
                        pomoDurationText.toInt(),
                        shortDurationText.toInt(),
                        longDurationText.toInt(),
                        longIntervalText.toInt(),
                    )
                },
                shape = RectangleShape
            ) {
                Text(
                    text = "Confirm",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        if (settingsErrorMessage != "") {
            Text(
                text = settingsErrorMessage,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}