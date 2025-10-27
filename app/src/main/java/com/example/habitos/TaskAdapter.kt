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

/**
 * Esta es la interfaz que MainActivity implementará para
 * que podamos "llamar" a la Activity desde el Adapter.
 */
interface TaskCallbacks {
    fun onTaskChanged()
    fun onTaskDeleted(task: Task)
}

/**
 * El Adapter ahora usa un constructor primario de Kotlin.
 * Pasamos 'context', 'taskList', y 'callbacks' como propiedades.
 * Heredamos de ArrayAdapter<Task> y le pasamos los parámetros requeridos.
 */
class TaskAdapter(
    context: Context,
    private val taskList: ArrayList<Task>,
    private val mCallbacks: TaskCallbacks
) : ArrayAdapter<Task>(context, 0, taskList) {

    // override fun... es la forma de Kotlin de sobrescribir un método
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // 1. Inflar el layout
        val listItem = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_task, parent, false)

        // 2. Obtener la tarea actual
        val currentTask = taskList[position]

        // 3. Obtener las vistas
        val cbTask = listItem.findViewById<CheckBox>(R.id.cbTask)
        val tvTaskContent = listItem.findViewById<TextView>(R.id.tvTaskContent)
        val btnDelete = listItem.findViewById<ImageButton>(R.id.btnDeleteTask)

        // 4. Poner los datos
        tvTaskContent.text = currentTask.content
        cbTask.isChecked = currentTask.isComplete

        // 5. Aplicar/quitar tachado
        if (currentTask.isComplete) {
            tvTaskContent.paintFlags = tvTaskContent.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            tvTaskContent.paintFlags = tvTaskContent.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // 6. Configurar Listeners

        cbTask.setOnCheckedChangeListener(null) // Evitar disparos accidentales
        cbTask.setOnCheckedChangeListener { _, isChecked ->
            currentTask.isComplete = isChecked
            // Aplicamos el cambio visual directamente aquí
            if (isChecked) {
                tvTaskContent.paintFlags = tvTaskContent.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                tvTaskContent.paintFlags = tvTaskContent.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            // Guardamos el estado
            mCallbacks.onTaskChanged()
        }

        btnDelete.setOnClickListener {
            mCallbacks.onTaskDeleted(currentTask)
        }

        return listItem
    }
}
