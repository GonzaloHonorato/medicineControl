package com.example.medicinecontrol

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.medicinecontrol.ui.theme.MedicineControlTheme
import org.junit.Rule
import org.junit.Test

class MedicationFormUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun botonMicrofono_esVisible() {
        composeTestRule.setContent {
            MedicineControlTheme {
                MedicationFormView(onSaved = {}, onBack = {})
            }
        }

        composeTestRule.onNodeWithContentDescription("Dictar nombre por voz")
            .assertIsDisplayed()
    }

    @Test
    fun campoNombre_sePuedeLlenar() {
        composeTestRule.setContent {
            MedicineControlTheme {
                MedicationFormView(onSaved = {}, onBack = {})
            }
        }

        composeTestRule.onNodeWithText("Nombre")
            .performTextInput("Paracetamol")

        composeTestRule.onNodeWithText("Paracetamol")
            .assertIsDisplayed()
    }

    @Test
    fun chipsIntervalo_seSeleccionan() {
        composeTestRule.setContent {
            MedicineControlTheme {
                MedicationFormView(onSaved = {}, onBack = {})
            }
        }

        // Verificar que los chips de intervalo existen
        composeTestRule.onNodeWithText("4h").assertIsDisplayed()
        composeTestRule.onNodeWithText("6h").assertIsDisplayed()
        composeTestRule.onNodeWithText("8h").assertIsDisplayed()
        composeTestRule.onNodeWithText("12h").assertIsDisplayed()
        composeTestRule.onNodeWithText("24h").assertIsDisplayed()

        // Click en el chip de 4h
        composeTestRule.onNodeWithText("4h").performClick()
    }
}
