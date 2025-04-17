package com.example.pomoplanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomoplanner.model.Task
import com.example.pomoplanner.ui.theme.TaskGreen
import com.example.pomoplanner.ui.theme.TaskOrange
import com.example.pomoplanner.ui.theme.TaskRed

@Composable
fun TasksTab(
    tasksTabViewModel: TasksTabViewModel = viewModel(),
) {
    tasksTabViewModel.getCurrentTasks()
    tasksTabViewModel.getCategoryOptions()
    tasksTabViewModel.updateBadge()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TaskTopBar(
                selectedDate = tasksTabViewModel.selectedDate,
                onFilterButtonClicked = { tasksTabViewModel.setShowFilterPopup(true) },
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
                tasksTabViewModel.deleteTask(task)
            },
            padding = innerPadding,
        )
    }

    CustomPopupHelper(
        showPopup = tasksTabViewModel.showFilterPopup,
        onClickOutside = { tasksTabViewModel.setShowFilterPopup(false) },
        content = {
            FilterView(
                tasksTabViewModel.categoryOptions,
                { category, priority, status ->
                    tasksTabViewModel.updateFilters(category, priority, status)
                },
                { tasksTabViewModel.resetFilters() }
            )
        }
    )

    CustomPopupHelper(
        showPopup = tasksTabViewModel.showCalendarPopup,
        onClickOutside = { tasksTabViewModel.setShowCalendarPopup(false) },
        content = {
            CalendarView(
                { dateMillis ->
                    tasksTabViewModel.updateDate(dateMillis)
                },
                { tasksTabViewModel.setShowCalendarPopup(false) }
            )
        }
    )

    CustomPopupHelper(
        showPopup = tasksTabViewModel.showAddTaskPopup,
        onClickOutside = { tasksTabViewModel.setShowAddTaskPopup(false) },
        content = {
            AddTaskView(
                { task ->
                    tasksTabViewModel.addTask(task)
                },
                tasksTabViewModel.selectedDate,
                tasksTabViewModel.selectedProfile,
                tasksTabViewModel.addTaskErrorMessage
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTopBar(
    selectedDate: String,
    onFilterButtonClicked: () -> Unit,
    onDateButtonClicked: () -> Unit,
    onAddTaskButtonClicked: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                selectedDate,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = { onFilterButtonClicked() }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Filter"
                )
            }
            IconButton(onClick = { onDateButtonClicked() }) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Date"
                )
            }
            IconButton(onClick = { onAddTaskButtonClicked() }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Task"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskListView(
    tasks: List<Task>,
    onCheckedChange: (Task, Boolean) -> Unit,
    onDeleteButtonClicked: (Task) -> Unit,
    padding: PaddingValues,
) {

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
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
                        .fillMaxSize(),
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

                        Column(
                            modifier = Modifier.weight(6.0f)
                        ) {
                            if (task.taskCategory != null) {
                                Text(
                                    style = MaterialTheme.typography.labelSmallEmphasized,
                                    fontStyle = FontStyle.Italic,
                                    text = task.taskCategory
                                )
                            }

                            Text(
                                text = task.taskDetails
                            )
                        }

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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterView(
    categoryOptions: List<String>,
    onFilterButtonClicked: (String, String, Int) -> Unit,
    onResetButtonClicked: () -> Unit,
) {
    val priorityOptions: List<String> = listOf("All", "Low", "Moderate", "High")

    val statusOptions: List<String> = listOf("All", "Not Completed", "Completed")

    var categoryExpanded by remember { mutableStateOf(false) }
    var categoryState = rememberTextFieldState(categoryOptions[0])
    var priorityExpanded by remember { mutableStateOf(false) }
    var priorityState = rememberTextFieldState(priorityOptions[0])
    var statusExpanded by remember { mutableStateOf(false) }
    var statusState = rememberTextFieldState(statusOptions[0])

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Filter Tasks",
            color = MaterialTheme.colorScheme.onBackground
        )

        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth(),
            expanded = categoryExpanded,
            onExpandedChange = { newValue ->
                categoryExpanded = newValue
            }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                state = categoryState,
                readOnly = true,
                lineLimits = TextFieldLineLimits.SingleLine,
                label = { Text("Task Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categoryOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            categoryState.setTextAndPlaceCursorAtEnd(option)
                            categoryExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth(),
            expanded = priorityExpanded,
            onExpandedChange = { newValue ->
                priorityExpanded = newValue
            }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                state = priorityState,
                readOnly = true,
                lineLimits = TextFieldLineLimits.SingleLine,
                label = { Text("Task Priority") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = priorityExpanded,
                onDismissRequest = { priorityExpanded = false }
            ) {
                priorityOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            priorityState.setTextAndPlaceCursorAtEnd(option)
                            priorityExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth(),
            expanded = statusExpanded,
            onExpandedChange = { newValue ->
                statusExpanded = newValue
            }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                state = statusState,
                readOnly = true,
                lineLimits = TextFieldLineLimits.SingleLine,
                label = { Text("Task Status") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false }
            ) {
                statusOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            statusState.setTextAndPlaceCursorAtEnd(option)
                            statusExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

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
                    onResetButtonClicked()
                },
                shape = RectangleShape
            ) {
                Text(
                    text = "Reset Filters",
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
                    onFilterButtonClicked(
                        categoryState.text.toString(),
                        priorityState.text.toString(),
                        when (statusState.text.toString()) {
                            "Not Completed" -> 0
                            "Completed" -> 1
                            else -> 2
                        }
                    )
                },
                shape = RectangleShape
            ) {
                Text(
                    text = "Confirm Filters",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarView(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                modifier = Modifier
                    .padding(
                        bottom = 8.dp,
                        end = 4.dp
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary),
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                },
                shape = RectangleShape
            ) {
                Text(
                    text = "Confirm",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        dismissButton = {
            Button(
                modifier = Modifier
                    .padding(
                        bottom = 8.dp,
                        start = 4.dp,
                        end = 8.dp
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary),
                onClick = {
                    onDismiss()
                },
                shape = RectangleShape
            ) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskView(
    onAddButtonClicked: (Task) -> Unit,
    selectedDate: String,
    selectedProfile: Int,
    addTaskErrorMessage: String,
) {
    val priorityOptions: List<String> = listOf("Low", "Moderate", "High")

    var taskDetailsText by remember { mutableStateOf("") }
    var taskCategoryText by remember { mutableStateOf("") }

    var priorityExpanded by remember { mutableStateOf(false) }
    var priorityState = rememberTextFieldState(priorityOptions[0])

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add New Task",
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = taskDetailsText,
            onValueChange = { taskDetailsText = it },
            label = { Text("Task Details") },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = taskCategoryText,
            onValueChange = { taskCategoryText = it },
            label = { Text("Task Category") },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        Row(
            modifier = Modifier
                .height(intrinsicSize = IntrinsicSize.Max),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .weight(3F)
                    .padding(end = 8.dp)
                    .fillMaxSize(),
                expanded = priorityExpanded,
                onExpandedChange = { newValue ->
                    priorityExpanded = newValue
                }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxSize(),
                    state = priorityState,
                    readOnly = true,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    label = { Text("Task Priority") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = priorityExpanded,
                    onDismissRequest = { priorityExpanded = false }
                ) {
                    priorityOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                priorityState.setTextAndPlaceCursorAtEnd(option)
                                priorityExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier
                    .weight(2F)
                    .padding(
                        start = 8.dp,
                        top = 8.dp
                    )
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary),
                onClick = {
                    onAddButtonClicked(
                        Task(
                            -1,
                            selectedProfile,
                            selectedDate,
                            priorityState.text.toString(),
                            false,
                            taskCategoryText,
                            taskDetailsText
                        )

                    )
                },
                shape = RectangleShape
            ) {
                Text(
                    text = "Add Task",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        if (addTaskErrorMessage != "") {
            Text(
                text = addTaskErrorMessage,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}