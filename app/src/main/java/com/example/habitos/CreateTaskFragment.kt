package com.example.habitos

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import java.util.HashSet

class CreateTaskFragment : Fragment() {

    private lateinit var btnCreateTask: Button
    private var currentUsername: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUsername = requireActivity().getSharedPreferences("SessionPrefs", Context.MODE_PRIVATE).getString("CURRENT_USER", null)

        btnCreateTask = view.findViewById(R.id.btnCreateTask)
        btnCreateTask.setOnClickListener {
            showTaskTypeDialog()
        }
    }

    private fun showTaskTypeDialog() {
        val options = arrayOf("Tarea Diaria", "Tarea Semanal")

        AlertDialog.Builder(requireContext())
            .setTitle("Tipo de Tarea")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> showCreateTaskDialog("daily")
                    1 -> showCreateTaskDialog("weekly")
                }
            }
            .show()
    }

    private fun showCreateTaskDialog(type: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_task, null)
        val etTaskContent = dialogView.findViewById<EditText>(R.id.etTaskContent)

        AlertDialog.Builder(requireContext())
            .setTitle("Crear Tarea")
            .setView(dialogView)
            .setPositiveButton("Crear") { dialog, _ ->
                val taskContent = etTaskContent.text.toString().trim()
                if (taskContent.isNotEmpty()) {
                    saveTask(taskContent, type)
                } else {
                    Toast.makeText(requireContext(), "Escribe una tarea", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun saveTask(taskContent: String, type: String) {
        val taskPrefs = requireContext().getSharedPreferences("tasks_${type}_${currentUsername!!}", Context.MODE_PRIVATE)
        val id = System.currentTimeMillis().toString()
        val newTask = "$id::$taskContent::false"

        val taskSet = taskPrefs.getStringSet("tasks", HashSet<String>())?.toMutableSet() ?: mutableSetOf()
        taskSet.add(newTask)

        taskPrefs.edit {
            putStringSet("tasks", taskSet)
        }

        Toast.makeText(requireContext(), "Tarea creada", Toast.LENGTH_SHORT).show()
    }
}
