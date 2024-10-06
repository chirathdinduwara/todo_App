package com.example.doit

import android.app.Application


class TodoApplication: Application(){
    private val database by lazy { TaskItemDB.getDatabase(this) }
    val repo by lazy { TaskItemRepo(database.taskItemDao()) }
}