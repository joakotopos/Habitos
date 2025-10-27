package com.example.habitos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnLogout: Button
    private lateinit var bottomNav: BottomNavigationView

    private var currentUsername: String? = null
    private lateinit var sessionPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Obtener el usuario de la sesión
        sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE)
        currentUsername = sessionPrefs.getString("CURRENT_USER", null)

        // 2. Seguridad: Si no hay usuario, volver al Login
        if (currentUsername == null) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 3. Inicializar vistas
        tvWelcome = findViewById(R.id.tvWelcome)
        btnLogout = findViewById(R.id.btnLogout)
        bottomNav = findViewById(R.id.bottom_navigation)

        // Plantilla de String
        tvWelcome.text = "Tareas de $currentUsername"

        // 4. Configurar Listeners
        btnLogout.setOnClickListener { logout() }
        bottomNav.setOnNavigationItemSelectedListener(navListener)

        // 5. Cargar el fragmento inicial (Diarias)
        if (savedInstanceState == null) {
            val dailyFragment = DailyTasksFragment.newInstance(currentUsername!!)
            loadFragment(dailyFragment)
        }
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment? = null

        when (item.itemId) {
            R.id.navigation_daily -> {
                selectedFragment = DailyTasksFragment.newInstance(currentUsername!!)
            }
            R.id.navigation_weekly -> {
                selectedFragment = WeeklyTasksFragment.newInstance(currentUsername!!)
            }
            R.id.navigation_monthly -> {
                selectedFragment = MonthlyTasksFragment.newInstance(currentUsername!!)
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
        sessionPrefs.edit { remove("CURRENT_USER") }
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
