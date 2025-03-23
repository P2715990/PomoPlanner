package com.example.pomoplanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomoplanner.model.Task
import com.example.pomoplanner.ui.theme.TaskGreen
import com.example.pomoplanner.ui.theme.TaskOrange
import com.example.pomoplanner.ui.theme.TaskRed

@Composable
fun TasksTab(
    tasksTabViewModel: TasksTabViewModel = viewModel(),
) {
    tasksTabViewModel.updateBadge()

    Scaffold(
        topBar = {
            TaskTopBar(
                tasksTabViewModel.selectedDate,
                onDateButtonClicked = { tasksTabViewModel.setShowCalendarPopup(true) },
                onAddTaskButtonClicked = { tasksTabViewModel.setShowAddTaskPopup(true) }
            )
        }
    ) { innerPadding ->
        TaskListView(
            tasks = tasksTabViewModel.tasks,
            onCheckedChange = { task, isCompleted ->
                tasksTabViewModel.changeTaskIsCompleted(task, isCompleted)
            },
            onDeleteButtonClicked = { task ->
                tasksTabViewModel.removeTask(task)
            },
            padding = innerPadding,
            showCalendarPopup = tasksTabViewModel.showCalendarPopup,
            onClickOutsideCalendar = { tasksTabViewModel.setShowCalendarPopup(false) },
            showAddTaskPopup = tasksTabViewModel.showAddTaskPopup,
            onClickOutsideAddTask = { tasksTabViewModel.setShowAddTaskPopup(false) }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTopBar(
    selectedDate: String,
    onDateButtonClicked: () -> Unit,
    onAddTaskButtonClicked: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                selectedDate,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { onDateButtonClicked() }) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Date"
                )
            }
        },
        actions = {
            IconButton(onClick = { onAddTaskButtonClicked() }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Task"
                )
            }
        }
    )
}

@Composable
fun TaskListView(
    tasks: List<Task>,
    onCheckedChange: (Task, Boolean) -> Unit,
    onDeleteButtonClicked: (Task) -> Unit,
    padding: PaddingValues,
    showCalendarPopup: Boolean,
    onClickOutsideCalendar: () -> Unit,
    showAddTaskPopup: Boolean,
    onClickOutsideAddTask: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(padding),
        state = rememberLazyListState(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tasks.forEachIndexed { index, task ->
            item {
                val checkBoxColorScheme: Color = when (task.taskPriority) {
                    "Low" -> TaskGreen
                    "Moderate" -> TaskOrange
                    "High" -> TaskRed
                    else -> Color.Unspecified
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            modifier = Modifier.weight(1.0f),
                            checked = task.taskIsCompleted,
                            colors = CheckboxColors(
                                checkedCheckmarkColor = checkBoxColorScheme,
                                uncheckedCheckmarkColor = checkBoxColorScheme,
                                checkedBoxColor = CheckboxDefaults.colors().checkedBoxColor,
                                uncheckedBoxColor = CheckboxDefaults.colors().uncheckedBoxColor,
                                disabledCheckedBoxColor = CheckboxDefaults.colors().disabledCheckedBoxColor,
                                disabledUncheckedBoxColor = CheckboxDefaults.colors().disabledUncheckedBoxColor,
                                disabledIndeterminateBoxColor = CheckboxDefaults.colors().disabledIndeterminateBoxColor,
                                checkedBorderColor = checkBoxColorScheme,
                                uncheckedBorderColor = checkBoxColorScheme,
                                disabledBorderColor = CheckboxDefaults.colors().disabledBorderColor,
                                disabledUncheckedBorderColor = CheckboxDefaults.colors().disabledUncheckedBorderColor,
                                disabledIndeterminateBorderColor = CheckboxDefaults.colors().disabledIndeterminateBorderColor
                            ),
                            onCheckedChange = { onCheckedChange(task, !task.taskIsCompleted) }
                        )

                        Text(
                            modifier = Modifier.weight(6.0f),
                            text = task.taskDetails
                        )

                        IconButton(onClick = { onDeleteButtonClicked(task) }
                        ) {
                            Icon(Icons.Outlined.Delete, "Delete")
                        }
                    }
                }

                if (index < tasks.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }

    PopupBox(
        modifier = Modifier,
        padding = padding,
        showPopup = showCalendarPopup,
        onClickOutside = onClickOutsideCalendar,
        content = { CalendarView() }
    )

    PopupBox(
        modifier = Modifier,
        padding = padding,
        showPopup = showAddTaskPopup,
        onClickOutside = onClickOutsideAddTask,
        content = { AddTaskView() }
    )
}

@Composable
fun PopupBox(
    modifier: Modifier,
    padding: PaddingValues,
    showPopup: Boolean,
    onClickOutside: () -> Unit,
    content: @Composable() () -> Unit,
) {
    if (showPopup) {
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.6F))
                .zIndex(10F),
            contentAlignment = Alignment.Center
        ) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { onClickOutside() }
            ) {
                Box(
                    modifier = modifier,
                    contentAlignment = Alignment.Center
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun CalendarView() {
    Text("TODO: Implement CalendarView")
}

@Composable
fun AddTaskView() {
    Text("TODO: Implement AddTaskView")
}