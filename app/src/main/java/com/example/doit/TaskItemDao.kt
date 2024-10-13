package com.example.doit

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskItemDao {
    @Query("SELECT * FROM task_table ORDER BY id DESC")
    fun allTaskItems(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskItem(taskItem: TaskItem)

    @Update
    suspend fun updateTaskItem(taskItem: TaskItem)

    @Delete
    suspend fun deleteTaskItem(taskItem: TaskItem)

    @Query("SELECT * FROM task_table WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskItem?

    @Query("SELECT * FROM task_table ORDER BY id DESC LIMIT 3")
    fun getLatestTasks(): Flow<List<TaskItem>>

    @Query("""
    SELECT * FROM task_table 
    WHERE taskName LIKE '%' || :query || '%' 
       
    ORDER BY id ASC
""")
    fun searchTasks(query: String): Flow<List<TaskItem>>

    @Query("SELECT COUNT(*) FROM task_table WHERE taskPriority = 'Low'")
    fun getLowTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM task_table WHERE taskPriority = 'Medium'")
    fun getMdTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM task_table WHERE taskPriority = 'High'")
    fun getHgTaskCount(): Flow<Int>

}