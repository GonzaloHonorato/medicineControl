package com.example.medicinecontrol

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.medicinecontrol.ui.fragments.HistorialContent
import com.example.medicinecontrol.ui.theme.MedicineControlTheme
import org.junit.Rule
import org.junit.Test

class HistorialUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mensajeVacio_cuandoNoHayTomas() {
        composeTestRule.setContent {
            MedicineControlTheme {
                HistorialContent()
            }
        }

        composeTestRule.onNodeWithText("No hay tomas registradas")
            .assertIsDisplayed()
    }

    @Test
    fun titulo_esVisible() {
        composeTestRule.setContent {
            MedicineControlTheme {
                HistorialContent()
            }
        }

        composeTestRule.onNodeWithText("Historial de Tomas")
            .assertIsDisplayed()
    }
}
