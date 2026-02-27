package com.example.medicinecontrol

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime

class UtilsTest {

    // ===== isValidEmail Tests =====

    @Test
    fun `isValidEmail con email valido retorna true`() {
        assertTrue("usuario@ejemplo.com".isValidEmail())
    }

    @Test
    fun `isValidEmail con email valido y subdominio retorna true`() {
        assertTrue("user@mail.example.com".isValidEmail())
    }

    @Test
    fun `isValidEmail con caracteres especiales permitidos retorna true`() {
        assertTrue("user+tag@ejemplo.com".isValidEmail())
        assertTrue("user_name@ejemplo.com".isValidEmail())
        assertTrue("user.name@ejemplo.com".isValidEmail())
    }

    @Test
    fun `isValidEmail con email invalido sin arroba retorna false`() {
        assertFalse("usuario.ejemplo.com".isValidEmail())
    }

    @Test
    fun `isValidEmail con email invalido sin dominio retorna false`() {
        assertFalse("usuario@".isValidEmail())
    }

    @Test
    fun `isValidEmail con email invalido sin TLD retorna false`() {
        assertFalse("usuario@ejemplo".isValidEmail())
    }

    @Test
    fun `isValidEmail con string vacio retorna false`() {
        assertFalse("".isValidEmail())
    }

    @Test
    fun `isValidEmail con espacios se trimea y valida`() {
        assertTrue("  user@test.com  ".isValidEmail())
    }

    // ===== toIntOrDefault Tests =====

    @Test
    fun `toIntOrDefault con numero valido retorna el numero`() {
        assertEquals(42, "42".toIntOrDefault())
    }

    @Test
    fun `toIntOrDefault con numero negativo retorna el numero`() {
        assertEquals(-5, "-5".toIntOrDefault())
    }

    @Test
    fun `toIntOrDefault con texto invalido retorna default`() {
        assertEquals(0, "abc".toIntOrDefault())
    }

    @Test
    fun `toIntOrDefault con texto invalido retorna custom default`() {
        assertEquals(10, "abc".toIntOrDefault(10))
    }

    @Test
    fun `toIntOrDefault con string vacio retorna default`() {
        assertEquals(0, "".toIntOrDefault())
    }

    // ===== formatFriendly Tests =====

    @Test
    fun `formatFriendly formatea correctamente`() {
        val fecha = LocalDateTime.of(2025, 3, 15, 14, 30)
        assertEquals("15/03/2025 14:30", fecha.formatFriendly())
    }

    @Test
    fun `formatFriendly con medianoche`() {
        val fecha = LocalDateTime.of(2025, 1, 1, 0, 0)
        assertEquals("01/01/2025 00:00", fecha.formatFriendly())
    }

    // ===== formatTime Tests =====

    @Test
    fun `formatTime formatea solo hora y minuto`() {
        val fecha = LocalDateTime.of(2025, 3, 15, 14, 30, 45)
        assertEquals("14:30", fecha.formatTime())
    }

    @Test
    fun `formatTime con hora de un digito`() {
        val fecha = LocalDateTime.of(2025, 1, 1, 8, 5)
        assertEquals("08:05", fecha.formatTime())
    }

    // ===== calcularProximaToma Tests =====

    @Test
    fun `calcularProximaToma sin ultimaToma empieza desde horaInicial`() {
        val ahora = LocalDateTime.of(2025, 3, 15, 7, 0)
        val med = Medicamento(
            nombre = "Test",
            dosis = "1 mg",
            intervaloHoras = 8,
            horaInicial = LocalTime.of(8, 0),
            repetirDiario = true
        )

        val proxima = med.calcularProximaToma(ahora)
        assertEquals(LocalDateTime.of(2025, 3, 15, 8, 0), proxima)
    }

    @Test
    fun `calcularProximaToma sin ultimaToma avanza si horaInicial ya paso`() {
        val ahora = LocalDateTime.of(2025, 3, 15, 10, 0)
        val med = Medicamento(
            nombre = "Test",
            dosis = "1 mg",
            intervaloHoras = 8,
            horaInicial = LocalTime.of(8, 0),
            repetirDiario = true
        )

        val proxima = med.calcularProximaToma(ahora)
        assertEquals(LocalDateTime.of(2025, 3, 15, 16, 0), proxima)
    }

    @Test
    fun `calcularProximaToma con ultimaToma suma intervalo`() {
        val ahora = LocalDateTime.of(2025, 3, 15, 12, 0)
        val med = Medicamento(
            nombre = "Test",
            dosis = "1 mg",
            intervaloHoras = 6,
            horaInicial = LocalTime.of(8, 0),
            repetirDiario = true,
            ultimaToma = LocalDateTime.of(2025, 3, 15, 10, 0)
        )

        val proxima = med.calcularProximaToma(ahora)
        assertEquals(LocalDateTime.of(2025, 3, 15, 16, 0), proxima)
    }

    @Test
    fun `calcularProximaToma con ultimaToma avanza multiples intervalos`() {
        val ahora = LocalDateTime.of(2025, 3, 16, 10, 0)
        val med = Medicamento(
            nombre = "Test",
            dosis = "1 mg",
            intervaloHoras = 4,
            horaInicial = LocalTime.of(8, 0),
            repetirDiario = true,
            ultimaToma = LocalDateTime.of(2025, 3, 15, 8, 0)
        )

        val proxima = med.calcularProximaToma(ahora)
        assertEquals(LocalDateTime.of(2025, 3, 16, 12, 0), proxima)
    }

    // ===== estaAtrasado Tests =====

    @Test
    fun `estaAtrasado retorna false cuando proxima toma es futura`() {
        val ahora = LocalDateTime.of(2025, 3, 15, 7, 0)
        val med = Medicamento(
            nombre = "Test",
            dosis = "1 mg",
            intervaloHoras = 8,
            horaInicial = LocalTime.of(8, 0),
            repetirDiario = true
        )

        assertFalse(med.estaAtrasado(ahora))
    }

    @Test
    fun `estaAtrasado retorna false dentro del margen`() {
        val ahora = LocalDateTime.of(2025, 3, 15, 8, 10)
        val med = Medicamento(
            nombre = "Test",
            dosis = "1 mg",
            intervaloHoras = 24,
            horaInicial = LocalTime.of(8, 0),
            repetirDiario = true,
            ultimaToma = LocalDateTime.of(2025, 3, 14, 8, 0)
        )

        // proximaToma = 2025-03-15 08:00, ahora 08:10, margen 15min => no atrasado
        assertFalse(med.estaAtrasado(ahora))
    }

    @Test
    fun `estaAtrasado retorna false en borde exacto del margen`() {
        val ahora = LocalDateTime.of(2025, 3, 15, 8, 15)
        val med = Medicamento(
            nombre = "Test",
            dosis = "1 mg",
            intervaloHoras = 24,
            horaInicial = LocalTime.of(8, 0),
            repetirDiario = true,
            ultimaToma = LocalDateTime.of(2025, 3, 14, 8, 0)
        )

        // proximaToma = 2025-03-15 08:00, ahora 08:15 = exacto margen => isAfter(08:15) false
        assertFalse(med.estaAtrasado(ahora))
    }

    // ===== ordenarPorProximidad Tests =====

    @Test
    fun `ordenarPorProximidad ordena por proximidad`() {
        val ahora = LocalDateTime.of(2025, 3, 15, 10, 0)
        val med1 = Medicamento("B", "1 mg", 24, LocalTime.of(20, 0), true)
        val med2 = Medicamento("A", "1 mg", 4, LocalTime.of(8, 0), true)

        // med2 proxima: 12:00, med1 proxima: 20:00
        val sorted = listOf(med1, med2).sortedBy { it.calcularProximaToma(ahora) }
        assertEquals("A", sorted[0].nombre)
        assertEquals("B", sorted[1].nombre)
    }

    @Test
    fun `ordenarPorProximidad lista vacia retorna lista vacia`() {
        val result = emptyList<Medicamento>().ordenarPorProximidad()
        assertTrue(result.isEmpty())
    }

    // ===== validarCampos Tests =====

    @Test
    fun `validarCampos todos validos retorna true`() {
        val result = validarCampos(
            true to {},
            true to {}
        )
        assertTrue(result)
    }

    @Test
    fun `validarCampos con invalido retorna false y ejecuta callback`() {
        var errorCalled = false
        val result = validarCampos(
            true to {},
            false to { errorCalled = true }
        )
        assertFalse(result)
        assertTrue(errorCalled)
    }

    @Test
    fun `validarCampos ejecuta todos los callbacks de error`() {
        var error1 = false
        var error2 = false
        val result = validarCampos(
            false to { error1 = true },
            false to { error2 = true }
        )
        assertFalse(result)
        assertTrue(error1)
        assertTrue(error2)
    }

    // ===== executeWithTryCatch Tests =====

    @Test
    fun `executeWithTryCatch retorna valor del bloque exitoso`() {
        val result = executeWithTryCatch("default") { "valor" }
        assertEquals("valor", result)
    }

    @Test
    fun `executeWithTryCatch retorna default cuando lanza excepcion`() {
        val result = executeWithTryCatch("default") {
            throw RuntimeException("error")
        }
        assertEquals("default", result)
    }

    // ===== safeGet Tests =====

    @Test
    fun `safeGet con indice valido retorna elemento`() {
        val lista = listOf("a", "b", "c")
        assertEquals("b", lista.safeGet(1))
    }

    @Test
    fun `safeGet con indice fuera de rango retorna null`() {
        val lista = listOf("a", "b")
        assertNull(lista.safeGet(5))
    }

    @Test
    fun `safeGet con indice negativo retorna null`() {
        val lista = listOf("a", "b")
        assertNull(lista.safeGet(-1))
    }

    @Test
    fun `safeGet en lista vacia retorna null`() {
        val lista = emptyList<String>()
        assertNull(lista.safeGet(0))
    }
}
