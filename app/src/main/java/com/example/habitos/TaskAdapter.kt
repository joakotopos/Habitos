package com.example.habitos

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView

private const val VIEW_TYPE_TASK = 0
private const val VIEW_TYPE_HEADER = 1

interface TaskCallbacks {
    fun onTaskChanged(task: Task, isComplete: Boolean)
    fun onTaskDeleted(task: Task)
}

class TaskAdapter(
    context: Context,
    private val taskList: MutableList<Task>,
    private val mCallbacks: TaskCallbacks
) : ArrayAdapter<Task>(context, 0, taskList) {

    // Encuentra el índice de la primera tarea completada. -1 si no hay ninguna.
    private var firstCompletedIndex = -1

    init {
        sortAndPrepare()
    }

    private fun sortAndPrepare() {
        taskList.sortWith(compareBy { it.isComplete })
        firstCompletedIndex = taskList.indexOfFirst { it.isComplete }
    }

    override fun notifyDataSetChanged() {
        sortAndPrepare() // Re-ordena y encuentra el índice antes de volver a dibujar
        super.notifyDataSetChanged()
    }
    
    override fun getViewTypeCount(): Int = 2

    override fun getItemViewType(position: Int): Int {
        if (firstCompletedIndex != -1 && position == firstCompletedIndex) {
            return VIEW_TYPE_HEADER
        }
        return VIEW_TYPE_TASK
    }

    override fun getCount(): Int {
        // Si hay al menos una tarea completada, añadimos 1 para el encabezado
        return if (firstCompletedIndex != -1) taskList.size + 1 else taskList.size
    }

    override fun getItem(position: Int): Task? {
        if (firstCompletedIndex != -1) {
            return when {
                position < firstCompletedIndex -> taskList[position]
                position == firstCompletedIndex -> null // Para el encabezado
                else -> taskList[position - 1] // Desplazamiento de 1 para las posiciones después del encabezado
            }
        }
        return taskList[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            // Si la vista es nula o no es una vista de encabezado, infla una nueva.
            val headerView = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.list_header, parent, false)
            // Asegurarse de que no tenga un listener de click de una vista de tarea reciclada
            headerView.setOnClickListener(null)
            return headerView
        }
        
        // Es una vista de tarea
        val listItem = if (convertView != null && convertView.findViewById<CheckBox>(R.id.cbTask) != null) {
            convertView
        } else {
            LayoutInflater.from(context).inflate(R.layout.list_item_task, parent, false)
        }

        val currentTask = getItem(position)!!

        val cbTask = listItem.findViewById<CheckBox>(R.id.cbTask)
        val tvTaskContent = listItem.findViewById<TextView>(R.id.tvTaskContent)
        val btnDelete = listItem.findViewById<ImageButton>(R.id.btnDeleteTask)

        tvTaskContent.text = currentTask.content
        cbTask.isChecked = currentTask.isComplete

        if (currentTask.isComplete) {
            tvTaskContent.paintFlags = tvTaskContent.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            tvTaskContent.paintFlags = tvTaskContent.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        
        cbTask.setOnCheckedChangeListener(null) // ¡Importante!
        cbTask.setOnCheckedChangeListener { _, isChecked ->
            currentTask.isComplete = isChecked
            mCallbacks.onTaskChanged(currentTask, isChecked)
            // La llamada a notifyDataSetChanged() activará la re-ordenación
            notifyDataSetChanged()
        }

        btnDelete.setOnClickListener {
            mCallbacks.onTaskDeleted(currentTask)
        }

        return listItem
    }
}
