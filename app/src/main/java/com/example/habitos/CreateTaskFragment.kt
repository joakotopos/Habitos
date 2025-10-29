package com.example.habitos

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import java.util.HashSet

class CreateTaskFragment : Fragment() {

    private lateinit var btnCreateTask: Button
    private var currentUsername: String? = null
    private var selectedImageUri: Uri? = null
    private lateinit var ivTaskImage: ImageView

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let {
                selectedImageUri = it
                // Need to take read persmission for persistable access
                requireActivity().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                ivTaskImage.setImageURI(selectedImageUri)
                ivTaskImage.visibility = View.VISIBLE
            }
        }
    }

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
        val btnSelectImage = dialogView.findViewById<Button>(R.id.btnSelectImage)
        val btnAddTask = dialogView.findViewById<Button>(R.id.btnAddTask)
        ivTaskImage = dialogView.findViewById(R.id.ivTaskImage)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Crear Tarea")
            .setView(dialogView)
            .create()

        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            selectImageLauncher.launch(intent)
        }

        btnAddTask.setOnClickListener {
            val taskContent = etTaskContent.text.toString().trim()
            if (taskContent.isNotEmpty()) {
                saveTask(taskContent, type, selectedImageUri?.toString())
            } else {
                Toast.makeText(requireContext(), "Escribe una tarea", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveTask(taskContent: String, type: String, imageUri: String?) {
        val taskPrefs = requireContext().getSharedPreferences("tasks_${type}_${currentUsername!!}", Context.MODE_PRIVATE)
        val id = System.currentTimeMillis().toString()
        val imagePath = imageUri ?: ""
        val newTask = "$id::$taskContent::false::$imagePath"

        val taskSet = taskPrefs.getStringSet("tasks", HashSet<String>())?.toMutableSet() ?: mutableSetOf()
        taskSet.add(newTask)

        taskPrefs.edit {
            putStringSet("tasks", taskSet)
        }

        Toast.makeText(requireContext(), "Tarea creada", Toast.LENGTH_SHORT).show()
        selectedImageUri = null // resetea para la siguiente tarea
    }
}
