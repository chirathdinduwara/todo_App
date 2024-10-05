package com.example.doit

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class TaskItem(
    var name: String,
    var desc: String,
    var dueTime: LocalTime?,
    var completedDate: LocalDate?,
    var id: UUID = UUID.randomUUID()
) : Parcelable {


    @RequiresApi(Build.VERSION_CODES.O)
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        // Read dueTime as a String and convert back to LocalTime
        parcel.readString()?.let { LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME) },
        // Read completedDate as a String and convert back to LocalDate
        parcel.readString()?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) },
        UUID.fromString(parcel.readString() ?: "")
    )

    // Function to write to Parcel
    @RequiresApi(Build.VERSION_CODES.O)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(desc)
        // Convert dueTime to String before writing
        parcel.writeString(dueTime?.format(DateTimeFormatter.ISO_LOCAL_TIME))
        // Convert completedDate to String before writing
        parcel.writeString(completedDate?.format(DateTimeFormatter.ISO_LOCAL_DATE))
        parcel.writeString(id.toString())
    }

    // Required method
    override fun describeContents(): Int {
        return 0
    }

    // Companion object to handle Parcelable creation
    companion object CREATOR : Parcelable.Creator<TaskItem> {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun createFromParcel(parcel: Parcel): TaskItem {
            return TaskItem(parcel)
        }

        override fun newArray(size: Int): Array<TaskItem?> {
            return arrayOfNulls(size)
        }
    }

    // Other functions
    fun isCompleted() = completedDate != null
    fun imageResource(): Int = if (isCompleted()) R.drawable.check24 else R.drawable.unchecked_24
}
