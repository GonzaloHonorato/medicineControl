package com.example.medicinecontrol

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.medicinecontrol.ui.fragments.ProfileContent
import com.example.medicinecontrol.ui.theme.MedicineControlTheme
import org.junit.Rule
import org.junit.Test

class ProfileUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun nombreUsuario_seMustra() {
        Repository.usuarioActual = User(
            uid = "test123",
            nombre = "Juan Test",
            email = "juan@test.com"
        )

        composeTestRule.setContent {
            MedicineControlTheme {
                ProfileContent(onLogout = {})
            }
        }

        composeTestRule.onNodeWithText("Hola, Juan Test")
            .assertIsDisplayed()
    }

    @Test
    fun botonCerrarSesion_existe() {
        Repository.usuarioActual = User(
            uid = "test123",
            nombre = "Test User",
            email = "test@test.com"
        )

        composeTestRule.setContent {
            MedicineControlTheme {
                ProfileContent(onLogout = {})
            }
        }

        composeTestRule.onNodeWithText("Cerrar Sesión")
            .assertIsDisplayed()
    }
}
