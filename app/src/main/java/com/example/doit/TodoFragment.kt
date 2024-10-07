package com.example.doit

import TaskItemAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doit.databinding.FragmentTodoBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class TodoFragment : Fragment(), TaskItemClickListner {

    private lateinit var binding: FragmentTodoBinding
    private lateinit var viewTasks: ViewTask
    private lateinit var taskAdapter: TaskItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskItemDao = TaskItemDB.getDatabase(requireContext()).taskItemDao()
        val repository = TaskItemRepo(taskItemDao)
        val factory = TaskItemModelFactory(repository)
        viewTasks = ViewModelProvider(this, factory).get(ViewTask::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoBinding.inflate(inflater, container, false)

        // Setup the RecyclerView
        setRecycleView()
        // Setup the SearchView
        setupSearchView()


        val fab: FloatingActionButton = binding.addTask
        fab.setOnClickListener {
            val intent = Intent(activity, AddTasks::class.java)
            startActivity(intent)
        }


        return binding.root
    }

    private fun setRecycleView() {
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe search results instead of taskItems
        viewTasks.searchResults.observe(viewLifecycleOwner) { taskItems ->
            // Update the adapter with search results
            taskAdapter = TaskItemAdapter(taskItems?.toMutableList() ?: mutableListOf(), this)
            binding.taskRecyclerView.adapter = taskAdapter

            // Attach ItemTouchHelper for swipe to delete
            val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // Get the position of the item swiped
                    val position = viewHolder.adapterPosition
                    val taskItem = taskAdapter.getItemAt(position)

                    // Delete the task from ViewModel
                    viewTasks.deleteTask(taskItem)

                    // Show a toast
                    Toast.makeText(requireContext(), "${taskItem.name} deleted", Toast.LENGTH_SHORT).show()
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            })

            itemTouchHelper.attachToRecyclerView(binding.taskRecyclerView)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun editTaskItem(taskItem: TaskItem) {
        // Handle editing the task item
        val context = requireContext() // Get the valid context

        // Logging to check the values before passing
        Log.d("EditTaskItem", "Editing task: ${taskItem.name}, ID: ${taskItem.id}")

        // Create an Intent to start the AddTasks activity
        val intent = Intent(context, AddTasks::class.java).apply {
            putExtra("TASK_NAME", taskItem.name)
            putExtra("TASK_DESC", taskItem.desc)
            putExtra("DUE_TIME", taskItem.dueTime()?.toString()) // Convert LocalTime to String
            putExtra("COMPLETED_DATE", taskItem.completedDate()?.toString()) // Convert LocalDate to String
            putExtra("TASK_ID", taskItem.id.toString()) // Pass the ID
        }

        try {
            startActivity(intent) // Start the activity
        } catch (e: Exception) {
            Log.e("EditTaskItem", "Error starting AddTasks: ${e.message}", e) // Log the error
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun completeTaskItem(taskItem: TaskItem) {
        viewTasks.setCompleted(taskItem) // Update the completion status in your data source
        taskAdapter.notifyDataSetChanged() // Notify the adapter to refresh the RecyclerView
        Toast.makeText(requireContext(), "${taskItem.name} marked as completed", Toast.LENGTH_SHORT).show()
    }

    private fun setupSearchView() {
        val searchView: SearchView = binding.searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Optional: Handle search query submission
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewTasks.setSearchQuery(newText ?: "")

                return true
            }
        })
    }



}
