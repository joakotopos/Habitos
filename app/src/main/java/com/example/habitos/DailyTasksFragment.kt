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
    private val taskRepository = TaskRepository()
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

        if (userId == null) {
            Toast.makeText(requireContext(), "Error: sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        setupRecyclerView()
        loadDailyTasks()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(emptyList()) { task ->
            markTaskAsCompleted(task)
        }
        binding.rvTasks.adapter = taskAdapter
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

    private fun loadDailyTasks() {
        if (userId == null) {
            Toast.makeText(requireContext(), "Error: sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            showLoading(true)
            try {
                val tasks = taskRepository.getDailyTasks(userId!!)
                if (tasks.isNotEmpty()) {
                    taskAdapter.updateTasks(tasks)
                } else {
                    Toast.makeText(requireContext(), "No hay tareas diarias.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar tareas: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                showLoading(false)
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
        _binding = null // Limpiar la referencia al binding para evitar memory leaks
    }

}