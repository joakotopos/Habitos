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
import com.example.habitos.databinding.FragmentCreateTaskBinding
import kotlinx.coroutines.launch

class CreateTaskFragment : Fragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    private val taskRepository = TaskRepository()
    private lateinit var sessionManager: SessionManager
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
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

        binding.btnCreateTask.setOnClickListener {
            if (validateInput()) {
                createTask()
            }
        }
    }

    private fun validateInput(): Boolean {
        if (binding.etTaskTitle.text.isNullOrBlank()) {
            binding.tilTaskTitle.error = "El título no puede estar vacío"
            return false
        } else {
            binding.tilTaskTitle.error = null
        }
        return true
    }

    private fun createTask() {
        val title = binding.etTaskTitle.text.toString().trim()
        val description = binding.etTaskDescription.text.toString().trim()
        val type = if (binding.rbDaily.isChecked) "daily" else "weekly"

        if (userId == null) {
            Toast.makeText(requireContext(), "Error: sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        val newTask = Task(
            userId = userId!!,
            title = title,
            description = description,
            type = type
        )

        lifecycleScope.launch {
            showLoading(true)
            try {
                val createdTask = taskRepository.createTask(newTask)
                if (createdTask != null) {
                    Toast.makeText(requireContext(), "Tarea '${createdTask.title}' creada con éxito", Toast.LENGTH_SHORT).show()
                    clearForm()
                } else {
                    showError("No se pudo crear la tarea.")
                }
            } catch (e: Exception) {
                showError("Error al crear la tarea: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.btnCreateTask.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun clearForm() {
        binding.etTaskTitle.text?.clear()
        binding.etTaskDescription.text?.clear()
        binding.rbDaily.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
