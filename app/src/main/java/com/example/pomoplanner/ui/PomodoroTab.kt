package com.example.pomoplanner.ui

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    mainActivityViewModel: MainActivityViewModel,
    pomodoroTabViewModel: PomodoroTabViewModel = viewModel(),
) {
    pomodoroTabViewModel.getSettings()

    val navController = rememberNavController()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = pomodoroTabViewModel.pomodoroTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = mainActivityViewModel.timerState
        ) {
            composable("Pomodoro") {
                PomoTimerView(
                    navController,
                    { title ->
                        pomodoroTabViewModel.setPomodoroTitle(title)
                    },
                    mainActivityViewModel.timerTotal,
                    { seconds ->
                        mainActivityViewModel.setTimerTotal(seconds)
                    },
                    { mainActivityViewModel.resetTimerRemaining() },
                    pomodoroTabViewModel.longBreakInterval,
                    mainActivityViewModel.currentInterval,
                    { mainActivityViewModel.incrementCurrentInterval() },
                    { mainActivityViewModel.resetCurrentInterval() },
                    mainActivityViewModel.timerRemaining,
                    { mainActivityViewModel.decrementTimerRemaining() },
                    mainActivityViewModel.timerProgress,
                    { progress ->
                        mainActivityViewModel.setTimerProgress(progress)
                    },
                    mainActivityViewModel.timerIsRunning,
                    { isRunning ->
                        mainActivityViewModel.setTimerIsRunning(isRunning)
                    },
                    { state ->
                        mainActivityViewModel.updateTimerState(state)
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
                    mainActivityViewModel.timerTotal,
                    { seconds ->
                        mainActivityViewModel.setTimerTotal(seconds)
                    },
                    { mainActivityViewModel.resetTimerRemaining() },
                    mainActivityViewModel.timerRemaining,
                    { mainActivityViewModel.decrementTimerRemaining() },
                    mainActivityViewModel.timerProgress,
                    { progress ->
                        mainActivityViewModel.setTimerProgress(progress)
                    },
                    mainActivityViewModel.timerIsRunning,
                    { isRunning ->
                        mainActivityViewModel.setTimerIsRunning(isRunning)
                    },
                    mainActivityViewModel.timerState,
                    { state ->
                        mainActivityViewModel.updateTimerState(state)
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
                    mainActivityViewModel.timerTotal,
                    { seconds ->
                        mainActivityViewModel.setTimerTotal(seconds)
                    },
                    { mainActivityViewModel.resetTimerRemaining() },
                    mainActivityViewModel.timerRemaining,
                    { mainActivityViewModel.decrementTimerRemaining() },
                    mainActivityViewModel.timerProgress,
                    { progress ->
                        mainActivityViewModel.setTimerProgress(progress)
                    },
                    mainActivityViewModel.timerIsRunning,
                    { isRunning ->
                        mainActivityViewModel.setTimerIsRunning(isRunning)
                    },
                    mainActivityViewModel.timerState,
                    { state ->
                        mainActivityViewModel.updateTimerState(state)
                    },
                    pomodoroTabViewModel.pomodoroTimerDuration
                )
            }
        }
    }
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

    LaunchedEffect(isRunning) {
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
                navController.navigate("Long Break")
            } else {
                updateTimerState("Short Break")
                setTimerTotal(shortBreakDuration)
                resetRemainingTime()
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

    LaunchedEffect(isRunning) {
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
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
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
