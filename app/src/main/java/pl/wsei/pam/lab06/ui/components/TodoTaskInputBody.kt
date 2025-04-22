import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.wsei.pam.lab06.NotificationHandler
import pl.wsei.pam.lab06.model.TodoTaskForm
import pl.wsei.pam.lab06.model.TodoTaskUiState
import pl.wsei.pam.lab06.ui.components.TodoTaskInputForm

@Composable
fun TodoTaskInputBody(
    todoUiState: TodoTaskUiState,
    onItemValueChange: (TodoTaskForm) -> Unit,
    modifier: Modifier = Modifier,
    notificationHandler: NotificationHandler // Dodanie notificationHandler jako parametru
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TodoTaskInputForm(
            item = todoUiState.todoTask,
            onValueChange = onItemValueChange,
            modifier = modifier,
            notificationHandler = notificationHandler,
            taskList = listOf(todoUiState.todoTask)
        )

        // 🔔 Przycisk testowy do wywołania powiadomienia
        Button(onClick = {
            notificationHandler.showSimpleNotification()
        }) {
            Text("🔔 Testuj powiadomienie")
        }
    }
}
