package pl.wsei.pam.lab06.data

import kotlinx.coroutines.flow.Flow
import pl.wsei.pam.lab06.model.TodoTask  // Zmiana importu do model

interface TodoTaskRepository {
    fun getAllAsStream(): Flow<List<TodoTask>>
    fun getItemAsStream(id: Int): Flow<TodoTask?>
    suspend fun insertItem(item: TodoTask)
    suspend fun deleteItem(item: TodoTask)
    suspend fun updateItem(item: TodoTask)
}
