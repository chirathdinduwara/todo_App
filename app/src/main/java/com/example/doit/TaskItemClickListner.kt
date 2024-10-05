package com.example.doit




interface TaskItemClickListner {

    fun editTaskItem(taskItem: TaskItem)
    fun completeTaskItem(taskItem: TaskItem)
}