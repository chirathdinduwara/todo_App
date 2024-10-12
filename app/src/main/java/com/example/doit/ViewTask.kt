package com.example.doit

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
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


    private val _searchQuery = MutableStateFlow("")


    val searchResults: LiveData<List<TaskItem>> = _searchQuery
        .debounce(300) // Wait for 300ms
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                repo.allTaskItems
            } else {
                repo.searchTasks(query)
            }
        }
        .asLiveData()

    fun setSearchQuery(query: String) {
        _searchQuery.value= query
        Log.d("SearchTasks", "Tasks: ${_searchQuery.toString()}")
    }

    fun getHgTaskCount(): Flow<Int> {
        return repo.getHgTaskCount()
    }

    fun getLowTaskCount(): Flow<Int> {
        return repo.getLowTaskCount()
    }

    fun getMdTaskCount(): Flow<Int> {
        return repo.getMdTaskCount()
    }




}

class TaskItemModelFactory(private val repo: TaskItemRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewTask::class.java))
            return ViewTask(repo) as T
        throw IllegalArgumentException("Unknown Class")
    }
}
