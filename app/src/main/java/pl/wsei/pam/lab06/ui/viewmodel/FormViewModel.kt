package pl.wsei.pam.lab06.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import pl.wsei.pam.lab06.data.TodoTaskRepository
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import pl.wsei.pam.lab06.model.*

class FormViewModel(
    private val repository: TodoTaskRepository,
    private val dateProvider: CurrentDateProvider
) : ViewModel() {

    var todoTaskUiState by mutableStateOf(TodoTaskUiState())
        private set

    suspend fun save() {
        if (validate()) {
            repository.insertItem(todoTaskUiState.todoTask.toTodoTask())
        }
    }

    fun updateUiState(todoTaskForm: TodoTaskForm) {
        todoTaskUiState = TodoTaskUiState(
            todoTask = todoTaskForm,
            isValid = validate(todoTaskForm)
        )
    }

    private fun validate(uiState: TodoTaskForm = todoTaskUiState.todoTask): Boolean {
        return with(uiState) {
            title.isNotBlank() &&
                    LocalDateConverter.fromMillis(deadline).isAfter(dateProvider.currentDate)
        }
    }
}
