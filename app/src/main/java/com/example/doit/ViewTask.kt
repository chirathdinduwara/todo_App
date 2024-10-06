package com.example.doit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.launch
import java.time.LocalDate

class ViewTask(private val repo: TaskItemRepo) : ViewModel() {

    var taskItems: LiveData<List<TaskItem>> = repo.allTaskItems.asLiveData()

    fun addTask(newTask: TaskItem) = viewModelScope.launch {
        repo.insertTaskItem(newTask)
    }

    fun updateTask(taskItem: TaskItem) = viewModelScope.launch {
        repo.updateTaskItem(taskItem)
    }

    fun deleteTask(taskItem: TaskItem) {
        viewModelScope.launch {
            repo.deleteTask(taskItem)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCompleted(taskItem: TaskItem) = viewModelScope.launch {
        if (!taskItem.isCompleted()) {
            taskItem.completedDateString = TaskItem.dateFormat.format(LocalDate.now())
            repo.updateTaskItem(taskItem)
        }
    }

    // Change the return type to LiveData<TaskItem?>
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTaskById(taskId: Int): LiveData<TaskItem?> {
        // Use MutableLiveData to hold the result
        val taskLiveData = MutableLiveData<TaskItem?>()

        viewModelScope.launch {
            taskLiveData.value = repo.getTaskById(taskId) // Now a suspend function
        }

        return taskLiveData
    }
}

class TaskItemModelFactory(private val repo: TaskItemRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewTask::class.java))
            return ViewTask(repo) as T
        throw IllegalArgumentException("Unknown Class")
    }
}
