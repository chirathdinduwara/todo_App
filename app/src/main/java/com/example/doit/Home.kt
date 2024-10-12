package com.example.doit

import TaskItemAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doit.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: ViewTask
    private lateinit var taskItemAdapter: TaskItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = (requireActivity().application as TodoApplication).repo
        val factory = TaskItemModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(ViewTask::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and Adapter
        taskItemAdapter = TaskItemAdapter(mutableListOf(), object : TaskItemClickListner {
            override fun editTaskItem(taskItem: TaskItem) {
                // No implementation needed for display only
            }

            override fun completeTaskItem(taskItem: TaskItem) {
                // No implementation needed for display only
            }
        })

        binding.upcomingLink.setOnClickListener {
            navigateToTodoFragment()
        }

        // Set the layout manager
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.taskRecyclerView.adapter = taskItemAdapter

        // Observe the task items and update the adapter
        viewModel.taskItems.observe(viewLifecycleOwner) { taskItems ->
            taskItemAdapter.updateTaskItems(taskItems.toMutableList())
        }

        // Observe the task count
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            launch {
                viewModel.getHgTaskCount().collect { count ->
                    binding.hPriorityTasks.text = "Task Count: $count"
                }
            }
            launch {
                viewModel.getMdTaskCount().collect { count ->
                    binding.mdPriorityTasks.text = "Task Count: $count"
                }
            }
            launch {
                viewModel.getLowTaskCount().collect { count ->
                    binding.lwPriorityTasks.text = "Task Count: $count"
                }
            }
        }
    }

    private fun navigateToTodoFragment() {
        val todoFragment = TodoFragment()


        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, todoFragment)
            .addToBackStack(null)
            .commit()
    }
}


