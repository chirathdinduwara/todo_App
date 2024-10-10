package com.example.doit

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
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
import android.widget.Switch
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
import java.util.Calendar

class AddTasks : AppCompatActivity() {

    private lateinit var addTaskHeading: TextView
    private lateinit var taskName: EditText

    private lateinit var taskPriority: Spinner
    private var dueTime: LocalTime? = null
    private lateinit var addButton: Button
    private lateinit var setTime: Button
    private lateinit var closeBtn: ImageButton
    private var taskItem: TaskItem? = null
    private var selectedPriority: String? = null
    private var reminderSwitch: Switch? = null

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

        taskPriority = findViewById(R.id.taskPriority)
        addButton = findViewById(R.id.addbtn)
        setTime = findViewById(R.id.setTime)
        closeBtn = findViewById(R.id.closeBtn)
        reminderSwitch = findViewById(R.id.reminderSwitch)



        val priorities = arrayOf("Low", "Medium", "High")




        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taskPriority.adapter = adapter

        taskPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedPriority = priorities[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedPriority = null
            }
        }


        val taskId = intent.getStringExtra("TASK_ID")?.toInt()

        if (taskId != null) {

            viewTask.getTaskById(taskId.toInt()).observe(this) { task ->
                if (task != null) {
                    taskItem = task
                    addTaskHeading.text = "Update a Task"
                    addButton.text = "Update"

                    taskName.setText(task.name)


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
        val taskPriority = selectedPriority
        val dueTimeString = if (dueTime == null) null else TaskItem.timeFormatter.format(dueTime)

        if (taskItem == null) {
            // Create new task
            taskItem = TaskItem(taskNameText, taskPriority, dueTimeString, null, reminderEnabled = reminderSwitch!!.isChecked)
            viewTask.addTask(taskItem!!) // Add the new task

            Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show()
        } else {
            // Update existing task
            taskItem?.apply {
                name = taskNameText
                priority = taskPriority
                reminderEnabled = reminderSwitch!!.isChecked // Update reminder status
            }
            viewTask.updateTask(taskItem!!) // Update the task

            Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show()
        }

        Log.d("AddTasks", "Saving Task: ${taskItem?.name} with Reminder: ${reminderSwitch!!.isChecked}")

        reminderSwitch?.setOnCheckedChangeListener { _, isChecked ->
            taskItem?.reminderEnabled = isChecked // Use safe call operator
            if (isChecked) {
                setTaskReminder(taskItem!!) // Set the reminder
            } else {
                cancelTaskReminder(taskItem!!) // Cancel the reminder
            }
            viewTask.updateTask(taskItem!!) // Update the task
        }
        // Set reminder if enabled
        if (reminderSwitch!!.isChecked) {
            setTaskReminder(taskItem!!) // Call to set reminder
        } else {
            cancelTaskReminder(taskItem!!) // Call to cancel reminder if it was previously set
        }

        // Clear input fields
        taskName.text.clear()

        finish()
    }


    private fun goBack() {
        finish()
    }

    private fun setTaskReminder(task: TaskItem) {
        if (task.reminderEnabled) {

            val notificationId = task.id
            val intent = Intent(this, ReminderReceiver::class.java).apply {
                putExtra("taskName", task.name) // Pass task details to the receiver
            }
            val pendingIntent = PendingIntent.getBroadcast(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT)


            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val triggerTime = System.currentTimeMillis() + 300
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    private fun cancelTaskReminder(task: TaskItem) {
        val notificationId = task.id
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

}
