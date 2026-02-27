package com.example.medicinecontrol

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.medicinecontrol.ui.theme.MedicineControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicineControlTheme {
                var screen by remember { mutableStateOf("splash") }

                // Auto-login: check if user is already logged in
                LaunchedEffect(Unit) {
                    val currentUser = UserRepository.getCurrentUser()
                    if (currentUser != null) {
                        val profile = UserRepository.getUserProfile()
                        Repository.usuarioActual = profile
                        goToHome()
                    } else {
                        screen = "login"
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (screen) {
                            "splash" -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            "login" -> LoginView(
                                onLogin = { goToHome() },
                                onNavigateToRegister = { screen = "register" },
                                onNavigateToRecover = { screen = "recover" }
                            )
                            "register" -> RegisterView(
                                onRegistered = { goToHome() },
                                onNavigateToLogin = { screen = "login" }
                            )
                            "recover" -> RecoverPasswordView(
                                onBack = { screen = "login" }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
