package com.example.habitos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        android.util.Log.d("DailyTasksFragment", "UserId obtenido de sesión: $userId")
        android.util.Log.d("DailyTasksFragment", "AccessToken presente: ${accessToken != null}")

        if (userId == null || accessToken == null) {
            BubbleToast.show(requireActivity(), "Error: sesión no válida")
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
            BubbleToast.show(requireActivity(), "Error: ID de tarea no válido")
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

                BubbleToast.show(requireActivity(), "Tarea '${task.title}' eliminada")
                // Recargar desde servidor en background para sincronizar (sin mostrar loading)
                loadDailyTasks(showLoadingIndicator = false)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                android.util.Log.e("DailyTasksFragment", "Error HTTP ${e.code()}: $errorBody")
                BubbleToast.show(requireActivity(), "Error HTTP ${e.code()}: ${errorBody ?: e.message}", 3000)
            } catch (e: Exception) {
                android.util.Log.e("DailyTasksFragment", "Error al eliminar: ${e.message}", e)
                BubbleToast.show(requireActivity(), "Error al eliminar tarea: ${e.message}", 3000)
            }
        }
    }

    private fun markTaskAsCompleted(task: Task) {
        if (task.id == null) {
            BubbleToast.show(requireActivity(), "Error: ID de tarea no válido")
            return
        }

        // Alternar el estado de completado
        val newStatus = !task.isCompleted
        val updatedTask = task.copy(isCompleted = newStatus)
        
        // Actualizar UI inmediatamente sin parpadeo
        updateTaskInList(updatedTask)
        
        if (newStatus) {
            celebrate()
            BubbleToast.show(requireActivity(), "¡Tarea '${task.title}' completada!")
        } else {
            BubbleToast.show(requireActivity(), "Tarea '${task.title}' marcada como pendiente")
        }

        // Actualizar en el servidor en background
        lifecycleScope.launch {
            try {
                taskRepository.updateTaskCompletionStatus(task.id, newStatus)
            } catch (e: Exception) {
                BubbleToast.show(requireActivity(), "Error al actualizar tarea: ${e.message}", 3000)
                // Recargar para restaurar el estado correcto
                loadDailyTasks()
            }
        }
    }
    
    private fun updateTaskInList(updatedTask: Task) {
        val currentItems = taskAdapter.itemsList.toMutableList()
        val tasks = currentItems.filterIsInstance<TaskAdapter.TaskItem.TaskData>().map { it.task }.toMutableList()
        
        // Encontrar y actualizar la tarea
        val index = tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            tasks[index] = updatedTask
        }
        
        // Reorganizar: pendientes primero, completadas después
        val pendingTasks = tasks.filter { !it.isCompleted }
        val completedTasks = tasks.filter { it.isCompleted }
        
        val newItems = mutableListOf<TaskAdapter.TaskItem>()
        pendingTasks.forEach { task ->
            newItems.add(TaskAdapter.TaskItem.TaskData(task))
        }
        
        if (completedTasks.isNotEmpty()) {
            newItems.add(TaskAdapter.TaskItem.Header("✅ Tareas Completadas"))
            completedTasks.forEach { task ->
                newItems.add(TaskAdapter.TaskItem.TaskData(task))
            }
        }
        
        taskAdapter.updateItems(newItems)
    }

    private fun loadDailyTasks(showLoadingIndicator: Boolean = true) {
        if (userId == null) {
            BubbleToast.show(requireActivity(), "Error: sesión no válida")
            return
        }

        android.util.Log.d("DailyTasksFragment", "Cargando tareas para userId: $userId")

        lifecycleScope.launch {
            if (showLoadingIndicator) {
                showLoading(true)
            }
            try {
                val tasks = taskRepository.getDailyTasks(userId!!)
                
                android.util.Log.d("DailyTasksFragment", "Tareas cargadas: ${tasks.size} tareas")
                tasks.forEach { task ->
                    android.util.Log.d("DailyTasksFragment", "Tarea: ${task.title} - userId: ${task.userId}")
                }
                
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
                BubbleToast.show(requireActivity(), "Error al cargar tareas: ${e.message}", 3000)
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
