package com.example.doit



import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


object ViewTask {

    var taskItems = MutableLiveData<MutableList<TaskItem>>()

    init {
        taskItems.value = mutableListOf()
    }

    fun addTask(newTask: TaskItem) {
        val list = taskItems.value
        list!!.add(newTask)
        taskItems.postValue(list)
    }

    fun updateTask(id: UUID,name: String, desc: String, dueTime: LocalTime?  ) {
        val list = taskItems.value
        val task = list!!.find  { it.id == id }
        task?.name = name
        task?.desc = desc
        task?.dueTime = dueTime
        taskItems.postValue(list)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun setCompleted(taskItem: TaskItem) {
        // Create a mutable copy of the current list
        val list = taskItems.value?.toMutableList() ?: mutableListOf()
        // Find the task to be updated based on its ID
        val task = list.find { it.id == taskItem.id }

        // Check if the task was found and is not already completed
        if (task != null && task.completedDate == null) {
            // Set the completedDate to the current date
            task.completedDate = LocalDate.now() // Use assignment
        }

        // Post the updated list back to LiveData
        taskItems.postValue(list)
    }


}