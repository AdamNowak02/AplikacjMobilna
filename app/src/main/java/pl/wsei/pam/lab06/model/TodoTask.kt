package pl.wsei.pam.lab06.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "tasks")
data class TodoTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,               // Room nadpisze '0' wygenerowanym unikalnym id
    val title: String = "",
    val deadline: LocalDate = LocalDate.now(),
    val isDone: Boolean = false,
    val priority: Priority = Priority.Low
)
