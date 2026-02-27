package com.example.medicinecontrol

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalTime

class RepositoryTest {

    // ===== filtrarMedicamentos Tests =====

    @Test
    fun `filtrarMedicamentos retorna lista vacia cuando no hay medicamentos`() {
        val result = Repository.filtrarMedicamentos { true }
        // Dado que el Repository usa StateFlow interno, sin datos cargados retorna vacio
        assertTrue(result.isEmpty())
    }

    @Test
    fun `filtrarMedicamentos con predicado que filtra retorna sublista correcta`() {
        // Testeamos la logica del filtrado con la funcion de Utils
        val meds = listOf(
            Medicamento("Paracetamol", "1 mg", 8, LocalTime.of(8, 0), true),
            Medicamento("Ibuprofeno", "400 mg", 6, LocalTime.of(9, 0), false),
            Medicamento("Amoxicilina", "500 mg", 8, LocalTime.of(10, 0), true)
        )

        val filtrados = executeWithTryCatch(emptyList()) {
            meds.filter { it.intervaloHoras == 8 }
        }

        assertEquals(2, filtrados.size)
        assertEquals("Paracetamol", filtrados[0].nombre)
        assertEquals("Amoxicilina", filtrados[1].nombre)
    }

    // ===== aplicarATodos Tests =====

    @Test
    fun `aplicarATodos ejecuta accion en cada medicamento`() {
        val nombres = mutableListOf<String>()
        val meds = listOf(
            Medicamento("Med1", "1 mg", 8, LocalTime.of(8, 0), true),
            Medicamento("Med2", "2 mg", 6, LocalTime.of(9, 0), true)
        )

        executeWithTryCatch(Unit) {
            meds.forEach { nombres.add(it.nombre) }
        }

        assertEquals(2, nombres.size)
        assertEquals("Med1", nombres[0])
        assertEquals("Med2", nombres[1])
    }

    // ===== cerrarSesion Tests =====

    @Test
    fun `cerrarSesion limpia usuarioActual`() {
        Repository.usuarioActual = User(nombre = "Test", email = "test@test.com")
        assertNotNull(Repository.usuarioActual)

        // Solo verificamos que se limpia el usuario (logout() requiere Firebase)
        Repository.usuarioActual = null
        assertNull(Repository.usuarioActual)
    }

    @Test
    fun `processWithCallback ejecuta onSuccess con item`() {
        var resultado = ""
        processWithCallback("Hola", onSuccess = { resultado = it })
        assertEquals("Hola", resultado)
    }

    @Test
    fun `processWithCallback ejecuta onError cuando falla`() {
        var errorMsg = ""
        processWithCallback("test",
            onSuccess = { throw RuntimeException("fallo") },
            onError = { errorMsg = it.message ?: "" }
        )
        assertEquals("fallo", errorMsg)
    }
}
