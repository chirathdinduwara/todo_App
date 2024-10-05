package com.example.doit

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.doit.databinding.TaskItemBinding


class TaskItemAdapter(
    private val taskItems: List<TaskItem>,
    private val clickListner: TaskItemClickListner
): RecyclerView.Adapter<TaskItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = TaskItemBinding.inflate(from, parent, false)
        return  TaskItemViewHolder(parent.context, binding, clickListner)
    }

    override fun getItemCount(): Int = taskItems.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        holder.bindTaskItem(taskItems[position])
    }
}