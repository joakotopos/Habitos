package com.example.habitos

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.habitos.data.AuthRepository
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToRegister: TextView
    private lateinit var progressBar: ProgressBar

    private val authRepository = AuthRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        // Verificar si ya hay una sesión activa
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Inicializar vistas
        etEmail = findViewById(R.id.etLoginEmail)
        etPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)
        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
        }

        btnLogin.setOnClickListener { loginUser() }

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            BubbleToast.show(this, "Por favor, rellena los campos")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            BubbleToast.show(this, "Por favor, ingresa un correo válido")
            return
        }

        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = authRepository.signIn(email, password)

                // Intentar obtener el nombre desde user_metadata o desde el perfil
                val userMetadataName = response.user.userMetadata?.get("name") as? String
                val userName = if (userMetadataName != null) {
                    userMetadataName
                } else {
                    // Si no está en metadata, intentar obtener del perfil
                    val profile = authRepository.getProfile(response.user.id, response.accessToken)
                    profile?.name ?: email.substringBefore("@")
                }

                // Guardar sesión
                sessionManager.saveSession(
                    userId = response.user.id,
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    email = response.user.email,
                    name = userName
                )

                BubbleToast.show(this@LoginActivity, "¡Bienvenido, $userName!")
                // Ir a MainActivity
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()

            } catch (e: Exception) {
                BubbleToast.show(
                    this@LoginActivity,
                    "Error al iniciar sesión: ${e.message}",
                    3000
                )
                e.printStackTrace()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        btnLogin.isEnabled = !isLoading
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
