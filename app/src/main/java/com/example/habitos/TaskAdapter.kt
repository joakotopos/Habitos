package com.example.habitos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.habitos.data.model.Task
import com.example.habitos.databinding.ItemTaskBinding
import com.example.habitos.databinding.ItemTaskSectionHeaderBinding

class TaskAdapter(
    private var items: List<TaskItem>,
    private val onTaskCompleted: (Task) -> Unit,
    private val onTaskDeleted: (Task) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_TASK = 1
    }

    // Clases selladas para representar los diferentes tipos de items
    sealed class TaskItem {
        data class Header(val title: String) : TaskItem()
        data class TaskData(val task: Task) : TaskItem()
    }

    // Exponer la lista de items para acceso externo
    val itemsList: List<TaskItem> get() = items

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TaskItem.Header -> VIEW_TYPE_HEADER
            is TaskItem.TaskData -> VIEW_TYPE_TASK
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemTaskSectionHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }
            VIEW_TYPE_TASK -> {
                val binding = ItemTaskBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                TaskViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is TaskItem.Header -> (holder as HeaderViewHolder).bind(item.title)
            is TaskItem.TaskData -> (holder as TaskViewHolder).bind(item.task)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<TaskItem>) {
        val diffCallback = TaskItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    private class TaskItemDiffCallback(
        private val oldList: List<TaskItem>,
        private val newList: List<TaskItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            
            return when {
                oldItem is TaskItem.Header && newItem is TaskItem.Header -> 
                    oldItem.title == newItem.title
                oldItem is TaskItem.TaskData && newItem is TaskItem.TaskData -> 
                    oldItem.task.id == newItem.task.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    // ViewHolder para los headers de sección
    class HeaderViewHolder(private val binding: ItemTaskSectionHeaderBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.tvSectionTitle.text = title
        }
    }

    // ViewHolder para las tareas
    inner class TaskViewHolder(private val binding: ItemTaskBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.tvTaskTitle.text = task.title
            binding.tvTaskDescription.text = task.description

            // Configurar indicador de tipo de tarea
            val indicatorDrawable = if (task.type == "daily") {
                binding.root.context.getDrawable(com.example.habitos.R.drawable.task_indicator_daily)
            } else {
                binding.root.context.getDrawable(com.example.habitos.R.drawable.task_indicator_weekly)
            }
            binding.vTaskTypeIndicator.background = indicatorDrawable

            // Configura el CheckBox sin disparar el listener al vincular
            binding.cbTaskCompleted.setOnCheckedChangeListener(null)
            binding.cbTaskCompleted.isChecked = task.isCompleted

            binding.cbTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
                onTaskCompleted(task)
            }

            // Configurar botón de eliminar
            binding.btnDeleteTask.setOnClickListener {
                onTaskDeleted(task)
            }

            // Animación de entrada
            binding.root.alpha = 0f
            binding.root.animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        }
    }
}
