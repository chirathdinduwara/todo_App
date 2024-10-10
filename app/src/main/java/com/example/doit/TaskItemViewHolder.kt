package com.example.doit

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.doit.databinding.TaskItemBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TaskItemViewHolder(
    private val context: Context,
    val binding: TaskItemBinding,
    val clickListner: TaskItemClickListner
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")


    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    fun bindTaskItem(taskItem: TaskItem) {
        binding.taskName.text = taskItem.name
        binding.taskPriority.text = taskItem.priority

        when (taskItem.priority) {
            "High" -> {
                binding.taskPriority.background = context.getDrawable(R.drawable.rounded_textbg_hpriority) // Replace with your drawable
            }
            "Medium" -> {
                binding.taskPriority.background = context.getDrawable(R.drawable.rounded_textbg_mpriority) // Replace with your drawable
            }
            "Low" -> {
                binding.taskPriority.background = context.getDrawable(R.drawable.rounded_textbg_lpriority) // Replace with your drawable
            }
        }

        // Set the strikethrough text effect
        if (taskItem.isCompleted()) {
            binding.taskName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.dueTime.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            binding.taskName.paintFlags = Paint.ANTI_ALIAS_FLAG // Remove strikethrough
            binding.dueTime.paintFlags = Paint.ANTI_ALIAS_FLAG // Remove strikethrough
        }

        // Set the image resource based on completion status
        binding.markStatus.setImageResource(taskItem.imageResource())

        // Handle click to mark as complete or edit task
        binding.markStatus.setOnClickListener {
            if (taskItem.isCompleted()) {
                clickListner.editTaskItem(taskItem) // Edit task if completed
            } else {
                clickListner.completeTaskItem(taskItem) // Mark as complete if not completed
            }
        }

        // Set due time if available
        taskItem.dueTime()?.let {
            binding.dueTime.text = timeFormat.format(LocalTime.parse(it.toString()))
        } ?: run {
            binding.dueTime.text = "" // Clear due time if null
        }

        // Set long click listener to navigate to AddTasks for editing
        binding.root.setOnLongClickListener {
            val intent = Intent(context, AddTasks::class.java).apply {
                putExtra("TASK_NAME", taskItem.name)
                putExtra("DUE_TIME", taskItem.dueTime()?.toString()) // Convert LocalTime to String
                putExtra("COMPLETED_DATE", taskItem.completedDate()?.toString()) // Convert LocalDate to String
                putExtra("TASK_ID", taskItem.id.toString()) // Pass the ID to recognize which task is being edited
            }
            context.startActivity(intent)
            true // Return true to indicate the long-click event has been handled
        }
    }


}


