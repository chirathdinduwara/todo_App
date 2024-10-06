package com.example.doit

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskItem::class], version = 1, exportSchema = false)
abstract class TaskItemDB: RoomDatabase() {
    abstract fun taskItemDao(): TaskItemDao

    companion object {
        @Volatile
        private var INSTANCE: TaskItemDB? = null

        fun getDatabase(context: Context): TaskItemDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskItemDB::class.java,
                    "Task_Item_Database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}