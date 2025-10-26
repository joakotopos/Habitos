package com.example.habitos

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class RegisterActivity : AppCompatActivity() {

    // 'lateinit var' significa que la variable será inicializada más tarde
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button

    private lateinit var userPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 1. Inicializar vistas
        etUsername = findViewById(R.id.etRegisterUsername)
        etPassword = findViewById(R.id.etRegisterPassword)
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        // 2. Inicializar SharedPreferences
        userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // 3. Configurar listener (lambda)
        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // 4. Validaciones
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        // 5. Comprobar si el usuario ya existe
        if (userPrefs.contains(username)) {
            Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show()
            return
        }

        // 6. Guardar el nuevo usuario
        userPrefs.edit {
            putString(username, password)
        }


        Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
