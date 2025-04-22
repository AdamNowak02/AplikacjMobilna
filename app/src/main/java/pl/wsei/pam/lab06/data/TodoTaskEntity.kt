package pl.wsei.pam.lab06.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.wsei.pam.lab06.model.Priority
import pl.wsei.pam.lab06.model.TodoTask
import java.time.LocalDate

@Entity(tableName = "tasks")
data class TodoTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // <- ważne: domyślnie 0, Room wygeneruje nowe id
    val title: String,
    val deadline: LocalDate,
    var isDone: Boolean,
    val priority: Priority
) {
    fun toModel(): TodoTask {
        return TodoTask(id, title, deadline, isDone, priority)
    }

    companion object {
        fun fromModel(model: TodoTask): TodoTaskEntity {
            return TodoTaskEntity(
                id = if (model.id == 0) 0 else model.id, // <- kluczowa linia!
                title = model.title,
                deadline = model.deadline,
                isDone = model.isDone,
                priority = model.priority
            )
        }
    }
}
