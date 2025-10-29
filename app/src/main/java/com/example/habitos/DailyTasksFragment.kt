package com.example.habitos

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color //para agregar diseño a futuro
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

class DailyTasksFragment : Fragment(), TaskCallbacks {

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
        // Inflamos el layout para este fragment
        return inflater.inflate(R.layout.fragment_daily_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtenemos el usuario de la sesión (MainActivity se lo pasará)
        currentUsername = arguments?.getString("CURRENT_USER")

        if (currentUsername == null) {
            Toast.makeText(requireContext(), "Error de sesión en Fragment", Toast.LENGTH_SHORT).show()
            return
        }

        // Usamos el contexto del fragment (requireContext()) para SharedPreferences
        taskPrefs = requireContext().getSharedPreferences("tasks_daily_${currentUsername!!}", Context.MODE_PRIVATE)

        // Inicializar vistas
        lvTasks = view.findViewById(R.id.lvTasks)
        konfettiView = view.findViewById(R.id.konfettiView)

        // Inicializar adaptador
        adapter = TaskAdapter(requireContext(), taskList, this)
        lvTasks.adapter = adapter

        // Cargar tareas
        loadTasks()
    }

    private fun loadTasks() {
        taskList.clear()
        val taskSet = taskPrefs.getStringSet("tasks", HashSet<String>()) ?: HashSet<String>()

        taskSet.forEach { taskString ->
            val parts = taskString.split("::")
            if (parts.size >= 3) { // Asegurarse de que haya al menos 3 partes
                val id = parts[0]
                val content = parts[1]
                val isComplete = parts[2].toBoolean()
                val imagePath = if (parts.size > 3) parts[3] else null
                val task = Task(id, content, isComplete, imagePath)
                taskList.add(task)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun saveTasks() {
        val taskSet = HashSet<String>()
        taskList.forEach { task ->
            taskSet.add("${task.id}::${task.content}::${task.isComplete}::${task.imagePath ?: ""}")
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

    // --- Callbacks de la interfaz ---
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
        fun newInstance(username: String): DailyTasksFragment {
            val fragment = DailyTasksFragment()
            val args = Bundle()
            args.putString("CURRENT_USER", username)
            fragment.arguments = args
            return fragment
        }
    }
}
