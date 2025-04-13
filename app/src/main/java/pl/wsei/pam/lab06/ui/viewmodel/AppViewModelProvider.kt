package pl.wsei.pam.lab06.ui.viewmodel

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import pl.wsei.pam.lab06.TodoApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val app = this[androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication
            ListViewModel(
                application = app,
                repository = app.container.todoTaskRepository
            )
        }
        initializer {
            val app = this[androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication
            FormViewModel(
                repository = app.container.todoTaskRepository
            )
        }
    }
}
