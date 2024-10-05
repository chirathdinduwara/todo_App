package com.example.doit


import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import java.time.LocalTime
import com.example.doit.ViewTask as viewTask


class AddTasks : AppCompatActivity() {

    private lateinit var addTaskHeading: TextView
    private lateinit var taskName: EditText
    private lateinit var taskDecs: EditText
    private var dueTime: LocalTime? = null
    private lateinit var addButton: Button
    private lateinit var setTime: Button
    private var taskItem: TaskItem? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tasks)
        enableEdgeToEdge()

        // Initialize views
        addTaskHeading = findViewById(R.id.addTaskHeading)
        taskName = findViewById(R.id.taskName)
        taskDecs = findViewById(R.id.taskDecs)
        addButton = findViewById(R.id.addbtn)
        setTime = findViewById(R.id.setTime)


        if (taskItem != null) {
            addTaskHeading.text = "Update a Task"
            taskName.setText(taskItem!!.name)
            taskDecs.setText(taskItem!!.desc)
            if ( taskItem!!.dueTime !=null ) {
                dueTime = taskItem!!.dueTime!!
                updateTimeBtn()
            }
        } else {
            addTaskHeading.text = "Add a Task"
        }

        // Handle button click
        addButton.setOnClickListener {
            saveData() // Call saveData when button is clicked
        }
        setTime.setOnClickListener{
            openTimePicker()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openTimePicker() {
        if (dueTime == null) {
            dueTime = LocalTime.now()
            val listner = TimePickerDialog.OnTimeSetListener{
                _, selectedHour, selectedMinute ->
                    dueTime = LocalTime.of(selectedHour, selectedMinute)
                    updateTimeBtn()
            }
            val dialog = TimePickerDialog(this, listner , dueTime!!.hour, dueTime!!.minute, true)
            dialog.setTitle("Enter Time")
            dialog.show()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTimeBtn() {
        setTime.text = String.format("%02d:%02d", dueTime!!.hour, dueTime!!.minute)
    }

    private fun saveData() {
        val taskNameText = taskName.text.toString()
        val taskDecs = taskDecs.text.toString()

        if (taskItem == null) {
            // Create new task
            val newTask = TaskItem(taskNameText, taskDecs, dueTime, null)
            viewTask.addTask(newTask)

            Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show()
        } else {
            // Update existing task
            viewTask.updateTask(taskItem!!.id, taskNameText, taskDecs, dueTime)

            Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show()
        }

        taskName.text.clear()
        //Decs
    }


}
