package pl.wsei.pam.lab06

import java.time.LocalDate

// Enum do określenia priorytetu zadania
enum class Priority {
    High, Medium, Low
}

// Data class reprezentująca zadanie
data class TodoTask(
    val title: String,
    val deadline: LocalDate,
    val isDone: Boolean,
    val priority: Priority
)

// Funkcja zwracająca przykładową listę zadań
fun todoTasks(): List<TodoTask> {
    return listOf(
        TodoTask("Programming", LocalDate.of(2024, 4, 18), false, Priority.Low),
        TodoTask("Teaching", LocalDate.of(2024, 5, 12), false, Priority.High),
        TodoTask("Learning", LocalDate.of(2024, 6, 28), true, Priority.Low),
        TodoTask("Cooking", LocalDate.of(2024, 8, 18), false, Priority.Medium)
    )
}
