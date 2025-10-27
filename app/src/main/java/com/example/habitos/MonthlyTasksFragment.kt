package com.example.habitos

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import java.util.HashSet

class MonthlyTasksFragment : Fragment(), TaskCallbacks {

    private lateinit var lvTasks: ListView
    private lateinit var etNewTask: EditText
    private lateinit var btnAddTask: Button

    private var taskList = ArrayList<Task>()
    private lateinit var adapter: TaskAdapter

    private var currentUsername: String? = null
    private lateinit var taskPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_monthly_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUsername = arguments?.getString("CURRENT_USER")

        if (currentUsername == null) {
            Toast.makeText(requireContext(), "Error de sesión en Fragment", Toast.LENGTH_SHORT).show()
            return
        }

        // ¡LA CLAVE! Usamos un nombre de SharedPreferences diferente
        taskPrefs = requireContext().getSharedPreferences("tasks_monthly_${currentUsername!!}", Context.MODE_PRIVATE)

        // Inicializar vistas
        lvTasks = view.findViewById(R.id.lvTasks)
        etNewTask = view.findViewById(R.id.etNewTask)
        btnAddTask = view.findViewById(R.id.btnAddTask)

        // Inicializar adaptador
        adapter = TaskAdapter(requireContext(), taskList, this)
        lvTasks.adapter = adapter

        loadTasks()

        btnAddTask.setOnClickListener { addTask() }
    }

    private fun addTask() {
        val taskContent = etNewTask.text.toString().trim()
        if (taskContent.isEmpty()) {
            Toast.makeText(requireContext(), "Escribe una tarea", Toast.LENGTH_SHORT).show()
            return
        }

        val id = System.currentTimeMillis().toString()
        val newTask = Task(id, taskContent, false)

        taskList.add(newTask)
        adapter.notifyDataSetChanged()
        etNewTask.setText("")

        saveTasks()
    }

    private fun loadTasks() {
        taskList.clear()
        val taskSet = taskPrefs.getStringSet("tasks", HashSet<String>()) ?: HashSet<String>()

        taskSet.forEach { taskString ->
            val parts = taskString.split("::")
            if (parts.size == 3) {
                val task = Task(parts[0], parts[1], parts[2].toBoolean())
                taskList.add(task)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun saveTasks() {
        val taskSet = HashSet<String>()
        taskList.forEach { task ->
            taskSet.add("${task.id}::${task.content}::${task.isComplete}")
        }
        taskPrefs.edit {
            putStringSet("tasks", taskSet)
        }
    }

    override fun onTaskChanged() {
        saveTasks()
    }

    override fun onTaskDeleted(task: Task) {
        taskList.remove(task)
        adapter.notifyDataSetChanged()
        saveTasks()
        Toast.makeText(requireContext(), "Tarea eliminada", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(username: String): MonthlyTasksFragment {
            val fragment = MonthlyTasksFragment()
            val args = Bundle()
            args.putString("CURRENT_USER", username)
            fragment.arguments = args
            return fragment
        }
    }
}
