package com.example.medicinecontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.medicinecontrol.ui.theme.MedicineControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicineControlTheme {
                var currentScreen by remember { mutableStateOf("login") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Modifier.padding(innerPadding).let { _ ->
                        // Navegacion simple por estados
                        when (currentScreen) {
                            "login" -> LoginView(
                                onLogin = { currentScreen = "home" },
                                onNavigateToRegister = { currentScreen = "register" },
                                onNavigateToRecover = { currentScreen = "recover" }
                            )
                            "register" -> RegisterView(
                                onRegistered = { currentScreen = "login" }
                            )
                            "recover" -> RecoverPasswordView(
                                onBack = { currentScreen = "login" }
                            )
                            "home" -> HomeView(
                                onAddMedication = { currentScreen = "add_med" }
                            )
                            "add_med" -> MedicationFormView(
                                onSaved = { currentScreen = "home" }
                            )
                        }
                    }
                }
            }
        }
    }
}
