package com.example.habitos

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import java.util.HashSet
import nl.dionsegijn.konfetti.xml.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class WeeklyTasksFragment : Fragment(), TaskCallbacks {

    private lateinit var lvTasks: ListView
    private lateinit var konfettiView: KonfettiView

    private var taskList = ArrayList<Task>()
    private lateinit var adapter: TaskAdapter

    private var currentUsername: String? = null
    private lateinit var taskPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weekly_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUsername = arguments?.getString("CURRENT_USER")

        if (currentUsername == null) {
            Toast.makeText(requireContext(), "Error de sesión en Fragment", Toast.LENGTH_SHORT).show()
            return
        }

        // ¡LA CLAVE! Usamos un nombre de SharedPreferences diferente
        taskPrefs = requireContext().getSharedPreferences("tasks_weekly_${currentUsername!!}", Context.MODE_PRIVATE)

        // Inicializar vistas
        lvTasks = view.findViewById(R.id.lvTasks)
        konfettiView = view.findViewById(R.id.konfettiView)

        // Inicializar adaptador
        adapter = TaskAdapter(requireContext(), taskList, this)
        lvTasks.adapter = adapter

        loadTasks()
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

    private fun celebrate() {
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = nl.dionsegijn.konfetti.core.Position.Relative(0.5, 0.3)
        )
        konfettiView.start(party)
    }

    override fun onTaskChanged(task: Task, isComplete: Boolean) {
        if (isComplete) {
            celebrate()
        }
        saveTasks()
    }

    override fun onTaskDeleted(task: Task) {
        taskList.remove(task)
        adapter.notifyDataSetChanged()
        saveTasks()
        Toast.makeText(requireContext(), "Tarea eliminada", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(username: String): WeeklyTasksFragment {
            val fragment = WeeklyTasksFragment()
            val args = Bundle()
            args.putString("CURRENT_USER", username)
            fragment.arguments = args
            return fragment
        }
    }
}
