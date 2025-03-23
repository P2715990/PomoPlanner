package com.example.pomoplanner.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomoplanner.model.Task
import com.example.pomoplanner.ui.theme.TaskGreen
import com.example.pomoplanner.ui.theme.TaskOrange
import com.example.pomoplanner.ui.theme.TaskRed

@Composable
fun TasksTab(
    tasksTabViewModel: TasksTabViewModel = viewModel()
) {
    TaskListView(
        tasks = tasksTabViewModel.tasks,
        onCheckedChange = { task, isCompleted ->
            tasksTabViewModel.changeTaskIsCompleted(task, isCompleted)
        },
        onDeleteClicked = { task ->
            tasksTabViewModel.removeTask(task)
        }
    )
}

@Composable
fun TaskListView(
    tasks: List<Task>,
    onCheckedChange: (Task, Boolean) -> Unit,
    onDeleteClicked: (Task) -> Unit,
) {
    LazyColumn(
        state = rememberLazyListState(),
        contentPadding = PaddingValues(16.dp)
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
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(20.dp),
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

                        IconButton(
                            modifier = Modifier
                                .then(Modifier.size(50.dp))
                                .border(1.dp, Color.Unspecified, shape = CircleShape)
                                .weight(1.0f),
                            onClick = { onDeleteClicked(task) }
                        ) {
                            Icon(Icons.Outlined.Delete, "Delete")
                        }
                    }
                }
            }
        }
    }
}