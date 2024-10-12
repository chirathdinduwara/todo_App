import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.doit.AddTasks
import com.example.doit.TaskItem
import com.example.doit.TaskItemClickListner
import com.example.doit.TaskItemViewHolder
import com.example.doit.databinding.TaskItemBinding

class TaskItemAdapter(
    private var taskItems: MutableList<TaskItem>,
    private val clickListner: TaskItemClickListner
) : RecyclerView.Adapter<TaskItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = TaskItemBinding.inflate(from, parent, false)
        return TaskItemViewHolder(parent.context, binding, clickListner)
    }

    override fun getItemCount(): Int = taskItems.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        holder.bindTaskItem(taskItems[position])
    }

    // Method to update the task items
    fun updateTaskItems(newTaskItems: MutableList<TaskItem>) {
        taskItems = newTaskItems
        notifyDataSetChanged() // Notify the adapter to refresh the data
    }

    fun getItemAt(position: Int): TaskItem {
        return taskItems[position]
    }

    fun removeItem(position: Int) {
        taskItems.removeAt(position)
        notifyItemRemoved(position)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun TaskItemViewHolder.bindTaskItem(taskItem: TaskItem) {
    binding.taskName.text = taskItem.name

    // Set the click listener for editing
    binding.root.setOnClickListener {
        clickListner.editTaskItem(taskItem) // Implemented already
    }

    // Set the long click listener to navigate to AddTask for editing
    binding.root.setOnLongClickListener {
        val context = binding.root.context // Get the context from the view
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
