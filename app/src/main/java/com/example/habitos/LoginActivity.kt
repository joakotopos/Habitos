package com.example.habitos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToRegister: TextView

    private lateinit var userPrefs: SharedPreferences
    private lateinit var sessionPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 1. Inicializar SessionPrefs primero
        sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE)

        // 2. Verificar si ya hay una sesión activa
        if (sessionPrefs.contains("CURRENT_USER")) {
            // Si hay sesión, vamos directo a MainActivity
            // Esta es la sintaxis de Kotlin para un Intent:
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Cierra LoginActivity
            return   // Detiene la ejecución de onCreate
        }

        // --- Si no hay sesión, continuamos ---

        // 3. Inicializar vistas
        etUsername = findViewById(R.id.etLoginUsername)
        etPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)

        // 4. Inicializar UserPrefs
        userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // 5. Configurar listeners
        btnLogin.setOnClickListener { loginUser() }

        tvGoToRegister.setOnClickListener {
            // Inicia la actividad de registro
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // 6. Comprobar credenciales
        // getString() puede devolver null, por eso 'savedPassword' es de tipo String? (nullable)
        val savedPassword = userPrefs.getString(username, null)

        // Comparamos de forma segura (Kotlin es estricto con nulls)
        if (savedPassword != null && savedPassword == password) {
            // ¡Login exitoso!

            // 7. Guardar la sesión
            sessionPrefs.edit {
                putString("CURRENT_USER", username)
            }

            // Usamos plantillas de String ("... $username ...")
            Toast.makeText(this, "¡Bienvenido, $username!", Toast.LENGTH_SHORT).show()

            // 8. Ir a MainActivity
            startActivity(Intent(this, MainActivity::class.java))

            // 9. Cerrar LoginActivity
            finish()

        } else {
            // Error
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
        }
    }
}
