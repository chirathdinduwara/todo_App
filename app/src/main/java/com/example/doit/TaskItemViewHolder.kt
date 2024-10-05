package com.example.doit

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.doit.databinding.TaskItemBinding
import java.time.format.DateTimeFormatter


class TaskItemViewHolder(
    private val context: Context,
    private val binding: TaskItemBinding,
    private val clickListner: TaskItemClickListner
): RecyclerView.ViewHolder(binding.root) {
    @RequiresApi(Build.VERSION_CODES.O)
    private val timeformat = DateTimeFormatter.ofPattern("HH:mm")
    @RequiresApi(Build.VERSION_CODES.O)
    fun bindTaskItem(taskItem: TaskItem) {
        binding.taskName.text = taskItem.name

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
            if (taskItem.dueTime != null) {
                binding.dueTime.text = timeformat.format(taskItem.dueTime)
            } else {
                binding.dueTime.text = ""
            }

        }
    }