package com.example.doit

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doit.databinding.FragmentTodoBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TodoFragment : Fragment(), TaskItemClickListner  {

    private lateinit var binding: FragmentTodoBinding
    private lateinit var viewTasks: ViewTask
    private lateinit var taskAdapter: TaskItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewTasks = ViewTask // Initialize the ViewTask object
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoBinding.inflate(inflater, container, false)

        // Setup the RecyclerView
        setRecycleView()

        val fab: FloatingActionButton = binding.addTask
        fab.setOnClickListener {
            val intent = Intent(activity, AddTasks::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun setRecycleView() {
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewTasks.taskItems.observe(viewLifecycleOwner) { taskItems ->
            taskAdapter = TaskItemAdapter(taskItems ?: emptyList(), this) // Pass the listener
            binding.taskRecyclerView.adapter = taskAdapter
        }
    }

    override fun editTaskItem(taskItem: TaskItem) {
        // Handle editing the task item
        val intent = Intent(requireContext(), AddTasks::class.java)
        intent.putExtra("TASK_ITEM", taskItem) // Pass the TaskItem to the AddTasks activity
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun completeTaskItem(taskItem: TaskItem) {
        ViewTask.setCompleted(taskItem) // Update the completion status in your data source
        taskAdapter.notifyDataSetChanged() // Notify the adapter to refresh the RecyclerView
        Toast.makeText(requireContext(), "${taskItem.name} marked as completed", Toast.LENGTH_SHORT).show()
    }
}
