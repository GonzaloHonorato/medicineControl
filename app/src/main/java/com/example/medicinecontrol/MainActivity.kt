package com.example.medicinecontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.medicinecontrol.ui.theme.MedicineControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicineControlTheme {
                var screen by remember { mutableStateOf("login") }
                var selectedTab by remember { mutableIntStateOf(0) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (screen == "home" || screen == "my_meds") {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                                    label = { Text("Inicio") },
                                    selected = selectedTab == 0,
                                    onClick = {
                                        selectedTab = 0
                                        screen = "home"
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.List, contentDescription = "Mis Medicamentos") },
                                    label = { Text("Mis Medicamentos") },
                                    selected = selectedTab == 1,
                                    onClick = {
                                        selectedTab = 1
                                        screen = "my_meds"
                                    }
                                )
                            }
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (screen) {
                            "login" -> LoginView(
                                onLogin = {
                                    screen = "home"
                                    selectedTab = 0
                                },
                                onNavigateToRegister = { screen = "register" },
                                onNavigateToRecover = { screen = "recover" }
                            )
                            "register" -> RegisterView(
                                onRegistered = {
                                    screen = "home"
                                    selectedTab = 0
                                },
                                onNavigateToLogin = { screen = "login" }
                            )
                            "recover" -> RecoverPasswordView(
                                onBack = { screen = "login" }
                            )
                            "home" -> HomeView(
                                onAddMedication = { screen = "add_med" }
                            )
                            "my_meds" -> MyMedicinesView(
                                onNavigateToHome = {
                                    screen = "home"
                                    selectedTab = 0
                                }
                            )
                            "add_med" -> MedicationFormView(
                                onSaved = { screen = "home" },
                                onBack = { screen = "home" }
                            )
                        }
                    }
                }
            }
        }
    }
}
