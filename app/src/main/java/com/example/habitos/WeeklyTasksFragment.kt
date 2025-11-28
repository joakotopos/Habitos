package com.example.habitos

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
import com.example.habitos.databinding.FragmentWeeklyTasksBinding
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class WeeklyTasksFragment : Fragment() {

    private var _binding: FragmentWeeklyTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter
    private val taskRepository = TaskRepository()

    // TODO: Reemplazar con el userId del usuario logueado cuando integres Supabase Auth
    private var userId: String = "a1b2c3d4-e5f6-7890-1234-567890abcdef" // UUID de prueba

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeeklyTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadWeeklyTasks()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(emptyList()) { task ->
            // Lógica para cuando una tarea se marca como completada
            celebrate()
            Toast.makeText(requireContext(), "¡Tarea '${task.title}' completada!", Toast.LENGTH_SHORT).show()
        }
        binding.rvTasks.adapter = taskAdapter
    }

    private fun loadWeeklyTasks() {
        lifecycleScope.launch {
            showLoading(true)
            try {
                val tasks = taskRepository.getWeeklyTasks(userId)
                if (tasks.isNotEmpty()) {
                    taskAdapter.updateTasks(tasks)
                } else {
                    Toast.makeText(requireContext(), "No hay tareas semanales.", Toast.LENGTH_SHORT).show()
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
        _binding = null
    }

    companion object {
        fun newInstance(username: String): WeeklyTasksFragment {
            return WeeklyTasksFragment()
        }
    }
}
