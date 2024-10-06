package com.example.doit

import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import java.time.LocalTime
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddTasks : AppCompatActivity() {

    private lateinit var addTaskHeading: TextView
    private lateinit var taskName: EditText
    private lateinit var taskDecs: EditText
    private lateinit var taskPriority: Spinner
    private var dueTime: LocalTime? = null
    private lateinit var addButton: Button
    private lateinit var setTime: Button
    private lateinit var closeBtn: ImageButton
    private var taskItem: TaskItem? = null
    private var selectedPriority: String? = null

    private val viewTask: ViewTask by viewModels{
        TaskItemModelFactory((application as TodoApplication).repo)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tasks)
        enableEdgeToEdge()

        // Initialize views
        addTaskHeading = findViewById(R.id.addTaskHeading)
        taskName = findViewById(R.id.taskName)
        taskDecs = findViewById(R.id.taskDecs)
        taskPriority = findViewById(R.id.taskPriority)
        addButton = findViewById(R.id.addbtn)
        setTime = findViewById(R.id.setTime)
        closeBtn = findViewById(R.id.closeBtn)

        // Create an array of priorities
        val priorities = arrayOf("Low", "Medium", "High")

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taskPriority.adapter = adapter // Corrected variable name

        taskPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedPriority = priorities[position] // Get the selected priority
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedPriority = null // Handle case where no selection is made
            }
        }

        // Retrieve task details from intent extras for editing
        val taskId = intent.getStringExtra("TASK_ID")?.toInt()

        if (taskId != null) {
            // Observe the LiveData returned by getTaskById
            viewTask.getTaskById(taskId.toInt()).observe(this) { task ->
                if (task != null) {
                    taskItem = task // Now you can assign it since task is not null
                    addTaskHeading.text = "Update a Task"
                    addButton.text = "Update"

                    taskName.setText(task.name)
                    taskDecs.setText(task.desc)

                    task.dueTime()?.let {
                        dueTime = it
                        updateTimeBtn()
                    }
                } else {
                    // Handle the case where the task might not be found
                    Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            addTaskHeading.text = "Add a Task"
        }


        // Handle button click
        addButton.setOnClickListener {
            saveData()
        }
        setTime.setOnClickListener {
            openTimePicker()
        }

        closeBtn.setOnClickListener {
            goBack()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openTimePicker() {
        if (dueTime == null) {
            dueTime = LocalTime.now()
        }
        val listener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            dueTime = LocalTime.of(selectedHour, selectedMinute)
            updateTimeBtn()
        }
        val dialog = TimePickerDialog(this, listener, dueTime!!.hour, dueTime!!.minute, true)
        dialog.setTitle("Enter Time")
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTimeBtn() {
        setTime.text = String.format("%02d:%02d", dueTime!!.hour, dueTime!!.minute)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveData() {
        val taskNameText = taskName.text.toString()
        val taskDecsText = taskDecs.text.toString()
        val taskPriority = selectedPriority
        val dueTimeString = if (dueTime == null) null else TaskItem.timeFormatter.format(dueTime)

        if (taskItem == null) {
            // Create new task
            val newTask = TaskItem(taskNameText, taskDecsText, taskPriority, dueTimeString, null)
            viewTask.addTask(newTask)

            Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show()
        } else {
            // Update existing task
            taskItem?.name = taskNameText
            taskItem?.priority = taskPriority
            taskItem?.dueTimeString = dueTimeString
            viewTask.updateTask(taskItem!!)

            Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show()
        }

        Log.d("AddTasks", "Updating Task: ${taskItem?.name}")
        // Clear input fields
        taskName.text.clear()
        taskDecs.text.clear()

        finish()


    }

    private fun goBack() {
        finish()
    }
}
