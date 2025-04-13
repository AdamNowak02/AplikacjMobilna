package pl.wsei.pam.lab06.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import pl.wsei.pam.lab06.TodoApplication
import pl.wsei.pam.lab06.viewmodel.FormViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication
            ListViewModel(
                application = app,
                repository = app.container.todoTaskRepository
            )
        }
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication
            FormViewModel(
                repository = app.container.todoTaskRepository,
                dateProvider = app.container.dateProvider
            )
        }
    }
}
