package com.example.medicinecontrol

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicinecontrol.ui.fragments.HomeFragment
import com.example.medicinecontrol.ui.fragments.HistorialFragment
import com.example.medicinecontrol.ui.fragments.MedicinesFragment
import com.example.medicinecontrol.ui.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Cargar datos de Firestore
        lifecycleScope.launch {
            Repository.cargarCatalogo()
            Repository.cargarMedicamentos()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Cargar fragment inicial (Inicio)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_meds -> {
                    loadFragment(MedicinesFragment())
                    true
                }
                R.id.nav_historial -> {
                    loadFragment(HistorialFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun logout() {
        Repository.cerrarSesion()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun navigateToAddMed() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, com.example.medicinecontrol.ui.fragments.AddMedicationFragment())
            .addToBackStack(null)
            .commit()
    }

    fun navigateToHome() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_home
    }
}
