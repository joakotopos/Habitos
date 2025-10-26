package com.example.habitos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import java.util.HashSet

// Implementamos la interfaz TaskCallbacks
class MainActivity : AppCompatActivity(), TaskCallbacks {

    private lateinit var tvWelcome: TextView
    private lateinit var btnLogout: Button
    private lateinit var lvTasks: ListView
    private lateinit var etNewTask: EditText
    private lateinit var btnAddTask: Button

    // Inicializamos la lista aquí
    private var taskList = ArrayList<Task>()
    private lateinit var adapter: TaskAdapter

    // 'currentUsername' puede ser nulo inicialmente
    private var currentUsername: String? = null
    private lateinit var sessionPrefs: SharedPreferences
    private lateinit var taskPrefs: SharedPreferences // SharedPreferences específico del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Obtener el usuario de la sesión
        sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE)
        currentUsername = sessionPrefs.getString("CURRENT_USER", null)

        // 2. Seguridad: Si no hay usuario, volver al Login
        if (currentUsername == null) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 3. ¡LA CLAVE! Abrimos el SharedPreferences de tareas
        // Usamos !! (operador de aserción no nula) porque ya comprobamos que no es nulo.
        taskPrefs = getSharedPreferences("tasks_${currentUsername!!}", MODE_PRIVATE)

        // 4. Inicializar vistas
        tvWelcome = findViewById(R.id.tvWelcome)
        btnLogout = findViewById(R.id.btnLogout)
        lvTasks = findViewById(R.id.lvTasks)
        etNewTask = findViewById(R.id.etNewTask)
        btnAddTask = findViewById(R.id.btnAddTask)

        // Plantilla de String
        tvWelcome.text = "Tareas de $currentUsername"

        // 5. Inicializar la lista y el adaptador
        // Pasamos "this" como el TaskCallbacks
        adapter = TaskAdapter(this, taskList, this)
        lvTasks.adapter = adapter

        // 6. Cargar tareas guardadas
        loadTasks()

        // 7. Configurar Listeners
        btnLogout.setOnClickListener { logout() }
        btnAddTask.setOnClickListener { addTask() }
    }

    private fun addTask() {
        val taskContent = etNewTask.text.toString().trim()
        if (taskContent.isEmpty()) {
            Toast.makeText(this, "Escribe una tarea", Toast.LENGTH_SHORT).show()
            return
        }

        // Creamos un ID único
        val id = System.currentTimeMillis().toString()
        val newTask = Task(id, taskContent, false)

        taskList.add(newTask)
        adapter.notifyDataSetChanged() // Avisa al adapter
        etNewTask.setText("") // Limpia el EditText

        saveTasks() // Guardamos la lista actualizada
    }

    private fun loadTasks() {
        taskList.clear()

        // getStringSet() puede devolver null. Usamos ?: para dar un HashSet vacío
        // si es nulo.
        val taskSet = taskPrefs.getStringSet("tasks", HashSet<String>()) ?: HashSet<String>()

        // Iteramos sobre el Set con un forEach
        taskSet.forEach { taskString ->
            val parts = taskString.split("::")
            if (parts.size == 3) {
                val task = Task(
                    parts[0], // id
                    parts[1], // content
                    parts[2].toBoolean() // isComplete
                )
                taskList.add(task)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun saveTasks() {
        // Creamos el Set a guardar
        val taskSet = HashSet<String>()

        // Convertimos nuestra List<Task> al formato String
        taskList.forEach { task ->
            // Plantillas de String
            taskSet.add("${task.id}::${task.content}::${task.isComplete}")
        }

        // Usamos la extensión KTX para guardar
        taskPrefs.edit {
            putStringSet("tasks", taskSet)
        }
    }

    private fun logout() {
        // Borramos la sesión
        sessionPrefs.edit {
            remove("CURRENT_USER")
        }

        // Volvemos al Login
        startActivity(Intent(this, LoginActivity::class.java))
        finish() // Cerramos MainActivity
    }

    // --- Métodos de la interfaz TaskCallbacks ---

    // 'override' es obligatorio
    override fun onTaskChanged() {
        saveTasks()
    }

    override fun onTaskDeleted(task: Task) {
        taskList.remove(task)
        adapter.notifyDataSetChanged()
        saveTasks()
        Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show()
    }
}
