package pl.wsei.pam.lab06.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoTaskDao {

    @Insert
    suspend fun insertAll(vararg tasks: TodoTaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun removeById(id: Int)  // <- Zmienione na usuwanie po ID

    @Query("SELECT * FROM tasks")
    fun findAll(): Flow<List<TodoTaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun find(id: Int): Flow<TodoTaskEntity>

    @Update
    suspend fun update(task: TodoTaskEntity)
}
