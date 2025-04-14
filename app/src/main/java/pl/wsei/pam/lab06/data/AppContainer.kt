package pl.wsei.pam.lab06.data

import android.content.Context
import pl.wsei.pam.lab06.NotificationHandler
import pl.wsei.pam.lab06.model.CurrentDateProvider
import pl.wsei.pam.lab06.model.DefaultCurrentDateProvider

interface AppContainer {
    val todoTaskRepository: TodoTaskRepository
    val dateProvider: CurrentDateProvider
    val notificationHandler: NotificationHandler // ✅ DODANE
}

class AppDataContainer(private val context: Context): AppContainer {
    override val todoTaskRepository: TodoTaskRepository by lazy {
        DatabaseTodoTaskRepository(AppDatabase.getInstance(context).taskDao())
    }

    override val dateProvider: CurrentDateProvider by lazy {
        DefaultCurrentDateProvider()
    }

    override val notificationHandler: NotificationHandler by lazy {
        NotificationHandler(context) // ✅ DODANE
    }
}
