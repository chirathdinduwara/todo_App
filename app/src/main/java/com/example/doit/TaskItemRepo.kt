package com.example.doit

import androidx.annotation.WorkerThread
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow

class TaskItemRepo(private val taskItemDao: TaskItemDao) {

    val allTaskItems: Flow<List<TaskItem>> = taskItemDao.allTaskItems()

    @WorkerThread
    suspend fun insertTaskItem(taskItem: TaskItem) {
        taskItemDao.insertTaskItem(taskItem)
    }

    @WorkerThread
    suspend fun updateTaskItem(taskItem: TaskItem) {
        taskItemDao.updateTaskItem(taskItem)
    }

    @WorkerThread
    suspend fun deleteTask(taskItem: TaskItem) {
        taskItemDao.deleteTaskItem(taskItem)
    }

    @WorkerThread
    suspend fun getTaskById(taskId: Int): TaskItem? {
        return taskItemDao.getTaskById(taskId) // Now a suspend function
    }

    fun searchTasks(query: String): Flow<List<TaskItem>> {
        return taskItemDao.searchTasks(query)
    }

    fun getHgTaskCount(): Flow<Int> {
        return taskItemDao.getHgTaskCount()
    }

    fun getMdTaskCount(): Flow<Int> {
        return taskItemDao.getMdTaskCount()
    }

    fun getLowTaskCount(): Flow<Int> {
        return taskItemDao.getLowTaskCount()
    }
}
