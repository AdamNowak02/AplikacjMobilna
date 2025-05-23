package pl.wsei.pam.lab06.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.wsei.pam.lab06.NotificationHandler
import pl.wsei.pam.lab06.model.LocalDateConverter
import pl.wsei.pam.lab06.model.Priority
import pl.wsei.pam.lab06.model.TodoTask
import pl.wsei.pam.lab06.model.TodoTaskForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTaskInputForm(
    item: TodoTaskForm,
    modifier: Modifier = Modifier,
    onValueChange: (TodoTaskForm) -> Unit = {},
    enabled: Boolean = true,
    notificationHandler: NotificationHandler,
    taskList: List<TodoTaskForm>
) {
    // Funkcja konwertująca TodoTaskForm na TodoTask
    fun TodoTaskForm.toTodoTask(): TodoTask {
        val localDate = LocalDateConverter.fromMillis(this.deadline)
        return TodoTask(
            id = 0,
            title = this.title,
            deadline = localDate,
            isDone = this.isDone,
            priority = Priority.valueOf(this.priority)
        )
    }

    Column(modifier = modifier) {
        Text("Tytuł zadania")

        TextField(
            value = item.title,
            onValueChange = {
                onValueChange(item.copy(title = it))
            }
        )

        val datePickerState = rememberDatePickerState(
            initialDisplayMode = DisplayMode.Picker,
            yearRange = IntRange(2000, 2030),
            initialSelectedDateMillis = item.deadline,
        )
        var showDialog by remember { mutableStateOf(false) }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true },
            text = "Deadline: ${LocalDateConverter.fromMillis(item.deadline)}",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )

        if (showDialog) {
            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        datePickerState.selectedDateMillis?.let {
                            val updatedItem = item.copy(deadline = it)
                            onValueChange(updatedItem)

                            // Ustaw powiadomienie dla najbliższego zadania
                            val nearestTask = taskList.minByOrNull { it.deadline }
                            nearestTask?.let {
                                notificationHandler.scheduleAlarmForTask(it.toTodoTask())
                            }
                        }
                    }) {
                        Text("Pick")
                    }
                }
            ) {
                DatePicker(state = datePickerState, showModeToggle = true)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Priorytet:")
        Priority.values().forEach { priority ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                RadioButton(
                    selected = item.priority == priority.name,
                    onClick = {
                        onValueChange(item.copy(priority = priority.name))
                    }
                )
                Text(priority.name)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Checkbox(
                checked = item.isDone,
                onCheckedChange = { onValueChange(item.copy(isDone = it)) }
            )
            Text("Wykonane")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔔 Przycisk do testowania powiadomień
        Button(onClick = {
            notificationHandler.showSimpleNotification()
        }) {
            Text("🔔 Testuj powiadomienie")
        }
    }
}
