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
import com.example.pomoplanner.ui.theme.TaskGreen
import com.example.pomoplanner.ui.theme.TaskOrange
import com.example.pomoplanner.ui.theme.TaskRed

data class TaskItem(
    var priority: String,
    var task: String,
    var isComplete: Boolean = false
)

val task1 = TaskItem(
    priority = "Low",
    task = "Wash Dishes"
)
val task2 = TaskItem(
    priority = "High",
    task = "Pay Taxes"
)
val task3 = TaskItem(
    priority = "Moderate",
    task = "Take Out Bins"
)
val task4 = TaskItem(
    priority = "Low",
    task = "Call Sister"
)
val task5 = TaskItem(
    priority = "Low",
    task = "Long rant to test big task descriptions. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
)
val task6 = TaskItem(
    priority = "Moderate",
    task = "Eat Dinner"
)

val taskItems = listOf(task1, task2, task3, task4, task5, task6)

@Composable
fun TasksTab() {
    TaskListView(taskItems)
}

@Composable
fun TaskListView(taskItems: List<TaskItem>) {
    LazyColumn(
        state = rememberLazyListState(),
        contentPadding = PaddingValues(16.dp)
    ) {
        taskItems.forEach { taskItem ->
            item {
                TaskItemView(taskItem)
            }
        }
    }
}

@Composable
fun TaskItemView(taskItem: TaskItem) {
    val checkBoxColorScheme: Color = when (taskItem.priority) {
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
                checked = taskItem.isComplete,
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
                onCheckedChange = { /*TODO*/ }
            )

            Text(
                modifier = Modifier.weight(6.0f),
                text = taskItem.task
            )

            IconButton(
                modifier = Modifier
                    .then(Modifier.size(50.dp))
                    .border(1.dp, Color.Unspecified, shape = CircleShape)
                    .weight(1.0f),
                onClick = { /*TODO*/ }
            ) {
                Icon(Icons.Outlined.Delete, "Delete")
            }
        }
    }
}