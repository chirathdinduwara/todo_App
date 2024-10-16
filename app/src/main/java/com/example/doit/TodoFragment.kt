package com.example.doit

import TaskItemAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
    ): View {
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

        viewTasks.searchResults.observe(viewLifecycleOwner) { taskItems ->
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
                    val position = viewHolder.adapterPosition
                    val taskItem = taskAdapter.getItemAt(position)

                    viewTasks.deleteTask(taskItem)
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
        val context = requireContext()
        Log.d("EditTaskItem", "Editing task: ${taskItem.name}, ID: ${taskItem.id}")

        val intent = Intent(context, AddTasks::class.java).apply {
            putExtra("TASK_NAME", taskItem.name)
            putExtra("DUE_TIME", taskItem.dueTime()?.toString())
            putExtra("COMPLETED_DATE", taskItem.completedDate()?.toString())
            putExtra("TASK_ID", taskItem.id.toString())
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("EditTaskItem", "Error starting AddTasks: ${e.message}", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun completeTaskItem(taskItem: TaskItem) {
        viewTasks.setCompleted(taskItem)
        taskAdapter.notifyDataSetChanged()
        Toast.makeText(requireContext(), "${taskItem.name} marked as completed", Toast.LENGTH_SHORT).show()
    }

    private fun setupSearchView() {
        val searchView: SearchView = binding.searchView

        // Set up the search text field properties
        val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchText.setTextColor(Color.BLACK) // Typing text color
        searchText.setHintTextColor(Color.GRAY) // Hint text color

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewTasks.setSearchQuery(newText ?: "")
                return true
            }
        })
    }
}
