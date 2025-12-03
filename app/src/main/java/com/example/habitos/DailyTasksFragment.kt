package com.example.habitos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.habitos.data.TaskRepository
import com.example.habitos.data.model.Task
import com.example.habitos.databinding.FragmentDailyTasksBinding
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class DailyTasksFragment : Fragment() {

    private var _binding: FragmentDailyTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskRepository: TaskRepository
    private lateinit var sessionManager: SessionManager
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        userId = sessionManager.getUserId()
        val accessToken = sessionManager.getAccessToken()

        if (userId == null || accessToken == null) {
            Toast.makeText(requireContext(), "Error: sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        taskRepository = TaskRepository(accessToken)

        setupRecyclerView()
        loadDailyTasks()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            items = emptyList(),
            onTaskCompleted = { task -> markTaskAsCompleted(task) },
            onTaskDeleted = { task -> deleteTask(task) }
        )
        binding.rvTasks.adapter = taskAdapter
    }

    private fun deleteTask(task: Task) {
        if (task.id == null) {
            Toast.makeText(requireContext(), "Error: ID de tarea no válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Mostrar diálogo de confirmación
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Eliminar tarea")
            .setMessage("¿Estás seguro de que quieres eliminar '${task.title}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                performDeleteTask(task)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun performDeleteTask(task: Task) {
        lifecycleScope.launch {
            try {
                android.util.Log.d("DailyTasksFragment", "Eliminando tarea: ${task.id}")
                taskRepository.deleteTask(task.id!!)

                // Actualizar lista inmediatamente sin mostrar loading
                val currentItems = taskAdapter.itemsList.toMutableList()
                currentItems.removeAll { item -> 
                    item is TaskAdapter.TaskItem.TaskData && item.task.id == task.id 
                }
                taskAdapter.updateItems(currentItems)

                Toast.makeText(requireContext(), "Tarea '${task.title}' eliminada", Toast.LENGTH_SHORT).show()
                // Recargar desde servidor en background para sincronizar (sin mostrar loading)
                loadDailyTasks(showLoadingIndicator = false)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                android.util.Log.e("DailyTasksFragment", "Error HTTP ${e.code()}: $errorBody")
                Toast.makeText(
                    requireContext(),
                    "Error HTTP ${e.code()}: ${errorBody ?: e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                android.util.Log.e("DailyTasksFragment", "Error al eliminar: ${e.message}", e)
                Toast.makeText(requireContext(), "Error al eliminar tarea: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun markTaskAsCompleted(task: Task) {
        if (task.id == null) {
            Toast.makeText(requireContext(), "Error: ID de tarea no válido", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                taskRepository.markTaskAsCompleted(task.id)
                celebrate()
                Toast.makeText(requireContext(), "¡Tarea '${task.title}' completada!", Toast.LENGTH_SHORT).show()
                // Recargar tareas para reflejar el cambio
                loadDailyTasks()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al completar tarea: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadDailyTasks(showLoadingIndicator: Boolean = true) {
        if (userId == null) {
            Toast.makeText(requireContext(), "Error: sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (showLoadingIndicator) {
                showLoading(true)
            }
            try {
                val tasks = taskRepository.getDailyTasks(userId!!)
                
                // Separar tareas pendientes y completadas
                val pendingTasks = tasks.filter { !it.isCompleted }
                val completedTasks = tasks.filter { it.isCompleted }
                
                // Crear lista de items con secciones
                val items = mutableListOf<TaskAdapter.TaskItem>()
                
                // Agregar tareas pendientes
                pendingTasks.forEach { task ->
                    items.add(TaskAdapter.TaskItem.TaskData(task))
                }
                
                // Agregar sección de tareas completadas si hay tareas completadas
                if (completedTasks.isNotEmpty()) {
                    items.add(TaskAdapter.TaskItem.Header("✅ Tareas Completadas"))
                    completedTasks.forEach { task ->
                        items.add(TaskAdapter.TaskItem.TaskData(task))
                    }
                }
                
                taskAdapter.updateItems(items)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar tareas: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                if (showLoadingIndicator) {
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.rvTasks.isVisible = !isLoading
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
        binding.konfettiView.start(party)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
