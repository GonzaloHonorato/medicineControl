package com.example.medicinecontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
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
                // Control de navegacion basico
                var screen by remember { mutableStateOf("login") }

                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (screen) {
                            "login" -> LoginView(
                                onLogin = { screen = "home" },
                                onNavigateToRegister = { screen = "register" },
                                onNavigateToRecover = { screen = "recover" }
                            )
                            "register" -> RegisterView(
                                onRegistered = { screen = "login" }
                            )
                            "recover" -> RecoverPasswordView(
                                onBack = { screen = "login" }
                            )
                            "home" -> HomeView(
                                onAddMedication = { screen = "add_med" }
                            )
                            "add_med" -> MedicationFormView(
                                onSaved = { screen = "home" }
                            )
                        }
                    }
                }
            }
        }
    }
}
