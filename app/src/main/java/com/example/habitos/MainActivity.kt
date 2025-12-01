package com.example.habitos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnLogout: Button
    private lateinit var bottomNav: BottomNavigationView

    private lateinit var sessionManager: SessionManager
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        // Verificar sesión
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        userId = sessionManager.getUserId()
        val userName = sessionManager.getUserName() ?: sessionManager.getUserEmail()

        // Inicializar vistas
        tvWelcome = findViewById(R.id.tvWelcome)
        btnLogout = findViewById(R.id.btnLogout)
        bottomNav = findViewById(R.id.bottom_navigation)

        tvWelcome.text = "Tareas de $userName"

        btnLogout.setOnClickListener { logout() }
        bottomNav.setOnNavigationItemSelectedListener(navListener)

        // Cargar el fragmento inicial (Diarias)
        if (savedInstanceState == null) {
            loadFragment(DailyTasksFragment())
        }
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment? = null

        when (item.itemId) {
            R.id.navigation_daily -> {
                selectedFragment = DailyTasksFragment()
            }
            R.id.navigation_create -> {
                selectedFragment = CreateTaskFragment()
            }
            R.id.navigation_weekly -> {
                selectedFragment = WeeklyTasksFragment()
            }
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment)
            return@OnNavigationItemSelectedListener true
        }
        false
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun logout() {
        sessionManager.clearSession()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
