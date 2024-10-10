package com.example.doit

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Entity(tableName = "task_table")
class TaskItem(
    @ColumnInfo(name = "taskName") var name: String,
    @ColumnInfo(name = "taskPriority") var priority: String? = "Low",
    @ColumnInfo(name = "dueTime") var dueTimeString: String?,
    @ColumnInfo(name = "completedDate") var completedDateString: String?,
    @ColumnInfo(name = "reminderEnabled") var reminderEnabled: Boolean = false,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)  {

    @RequiresApi(Build.VERSION_CODES.O)
    fun completedDate(): LocalDate? = if (completedDateString == null) null
        else LocalDate.parse(completedDateString, dateFormat)

    @RequiresApi(Build.VERSION_CODES.O)
    fun dueTime(): LocalTime? = dueTimeString?.let {
        LocalTime.parse(it, timeFormatter)
    }

    // Other functions
    fun isCompleted() = completedDateString != null

    fun imageResource(): Int = if (isCompleted()) R.drawable.check24 else R.drawable.unchecked_24

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_TIME
        @RequiresApi(Build.VERSION_CODES.O)
        val dateFormat: DateTimeFormatter = DateTimeFormatter.ISO_DATE
    }
}
