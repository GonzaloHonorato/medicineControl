package com.example.medicinecontrol

import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ModelsTest {

    // ===== RegistroToma Tests =====

    @Test
    fun `RegistroToma toMap serializa fechaHora en formato ISO`() {
        val fecha = LocalDateTime.of(2025, 3, 15, 10, 30, 0)
        val registro = RegistroToma(fechaHora = fecha, latitud = -33.45, longitud = -70.66, direccion = "Santiago, Chile")
        val map = registro.toMap()

        assertEquals("2025-03-15T10:30:00", map["fechaHora"])
        assertEquals(-33.45, map["latitud"])
        assertEquals(-70.66, map["longitud"])
        assertEquals("Santiago, Chile", map["direccion"])
    }

    @Test
    fun `RegistroToma toMap con campos nulos`() {
        val fecha = LocalDateTime.of(2025, 1, 1, 8, 0, 0)
        val registro = RegistroToma(fechaHora = fecha)
        val map = registro.toMap()

        assertEquals("2025-01-01T08:00:00", map["fechaHora"])
        assertNull(map["latitud"])
        assertNull(map["longitud"])
        assertNull(map["direccion"])
    }

    @Test
    fun `RegistroToma fromMap parsea datos validos completos`() {
        val map = mapOf<String, Any?>(
            "fechaHora" to "2025-03-15T10:30:00",
            "latitud" to -33.45,
            "longitud" to -70.66,
            "direccion" to "Santiago, Chile"
        )
        val registro = RegistroToma.fromMap(map)

        assertNotNull(registro)
        assertEquals(LocalDateTime.of(2025, 3, 15, 10, 30, 0), registro!!.fechaHora)
        assertEquals(-33.45, registro.latitud!!, 0.001)
        assertEquals(-70.66, registro.longitud!!, 0.001)
        assertEquals("Santiago, Chile", registro.direccion)
    }

    @Test
    fun `RegistroToma fromMap sin fechaHora retorna null`() {
        val map = mapOf<String, Any?>(
            "latitud" to -33.45,
            "longitud" to -70.66
        )
        assertNull(RegistroToma.fromMap(map))
    }

    @Test
    fun `RegistroToma fromMap con fechaHora invalida retorna null`() {
        val map = mapOf<String, Any?>(
            "fechaHora" to "fecha-invalida"
        )
        assertNull(RegistroToma.fromMap(map))
    }

    @Test
    fun `RegistroToma fromMap con campos opcionales nulos`() {
        val map = mapOf<String, Any?>(
            "fechaHora" to "2025-06-01T14:00:00",
            "latitud" to null,
            "longitud" to null,
            "direccion" to null
        )
        val registro = RegistroToma.fromMap(map)

        assertNotNull(registro)
        assertNull(registro!!.latitud)
        assertNull(registro.longitud)
        assertNull(registro.direccion)
    }

    @Test
    fun `RegistroToma fromMap con latitud como Long`() {
        val map = mapOf<String, Any?>(
            "fechaHora" to "2025-06-01T14:00:00",
            "latitud" to -33L,
            "longitud" to -70L
        )
        val registro = RegistroToma.fromMap(map)

        assertNotNull(registro)
        assertEquals(-33.0, registro!!.latitud!!, 0.001)
        assertEquals(-70.0, registro.longitud!!, 0.001)
    }

    // ===== Medicamento Tests =====

    @Test
    fun `Medicamento toMap serializa correctamente sin ultimaToma`() {
        val med = Medicamento(
            nombre = "Paracetamol",
            dosis = "2 pastilla(s)",
            intervaloHoras = 8,
            horaInicial = LocalTime.of(8, 0),
            repetirDiario = true
        )
        val map = med.toMap()

        assertEquals("Paracetamol", map["nombre"])
        assertEquals("2 pastilla(s)", map["dosis"])
        assertEquals(8, map["intervaloHoras"])
        assertEquals("08:00", map["horaInicial"])
        assertEquals(true, map["repetirDiario"])
        assertFalse(map.containsKey("ultimaToma"))
        assertEquals(emptyList<Map<String, Any?>>(), map["historialTomas"])
    }

    @Test
    fun `Medicamento toMap serializa ultimaToma cuando presente`() {
        val ultimaToma = LocalDateTime.of(2025, 3, 15, 10, 0, 0)
        val med = Medicamento(
            nombre = "Ibuprofeno",
            dosis = "400 mg",
            intervaloHoras = 6,
            horaInicial = LocalTime.of(9, 0),
            repetirDiario = false,
            ultimaToma = ultimaToma
        )
        val map = med.toMap()

        assertEquals("2025-03-15T10:00:00", map["ultimaToma"])
    }

    @Test
    fun `Medicamento toMap serializa historialTomas como lista de maps`() {
        val registro = RegistroToma(
            fechaHora = LocalDateTime.of(2025, 3, 15, 10, 0, 0),
            latitud = -33.45,
            longitud = -70.66
        )
        val med = Medicamento(
            nombre = "Amoxicilina",
            dosis = "1 cápsula(s)",
            intervaloHoras = 8,
            horaInicial = LocalTime.of(8, 0),
            repetirDiario = true,
            historialTomas = mutableListOf(registro)
        )
        val map = med.toMap()

        @Suppress("UNCHECKED_CAST")
        val historial = map["historialTomas"] as List<Map<String, Any?>>
        assertEquals(1, historial.size)
        assertEquals("2025-03-15T10:00:00", historial[0]["fechaHora"])
    }

    @Test
    fun `Medicamento fromDocument parsea formato Map nuevo`() {
        val doc = mock<DocumentSnapshot>()
        whenever(doc.id).thenReturn("doc123")
        whenever(doc.getString("nombre")).thenReturn("Paracetamol")
        whenever(doc.getString("dosis")).thenReturn("2 pastilla(s)")
        whenever(doc.getLong("intervaloHoras")).thenReturn(8L)
        whenever(doc.getString("horaInicial")).thenReturn("08:00")
        whenever(doc.getBoolean("repetirDiario")).thenReturn(true)
        whenever(doc.getString("ultimaToma")).thenReturn("2025-03-15T10:00:00")

        val historialMap = mapOf<String, Any?>(
            "fechaHora" to "2025-03-15T10:00:00",
            "latitud" to -33.45,
            "longitud" to -70.66,
            "direccion" to "Santiago"
        )
        whenever(doc.get("historialTomas")).thenReturn(listOf(historialMap))

        val med = Medicamento.fromDocument(doc)

        assertNotNull(med)
        assertEquals("Paracetamol", med!!.nombre)
        assertEquals("2 pastilla(s)", med.dosis)
        assertEquals(8, med.intervaloHoras)
        assertEquals(LocalTime.of(8, 0), med.horaInicial)
        assertTrue(med.repetirDiario)
        assertEquals(LocalDateTime.of(2025, 3, 15, 10, 0, 0), med.ultimaToma)
        assertEquals(1, med.historialTomas.size)
        assertEquals("Santiago", med.historialTomas[0].direccion)
        assertEquals("doc123", med.firestoreId)
    }

    @Test
    fun `Medicamento fromDocument parsea formato legacy String`() {
        val doc = mock<DocumentSnapshot>()
        whenever(doc.id).thenReturn("doc456")
        whenever(doc.getString("nombre")).thenReturn("Ibuprofeno")
        whenever(doc.getString("dosis")).thenReturn("400 mg")
        whenever(doc.getLong("intervaloHoras")).thenReturn(6L)
        whenever(doc.getString("horaInicial")).thenReturn("09:00")
        whenever(doc.getBoolean("repetirDiario")).thenReturn(false)
        whenever(doc.getString("ultimaToma")).thenReturn(null)
        whenever(doc.get("historialTomas")).thenReturn(listOf("2025-03-15T10:00:00"))

        val med = Medicamento.fromDocument(doc)

        assertNotNull(med)
        assertEquals(1, med!!.historialTomas.size)
        assertEquals(LocalDateTime.of(2025, 3, 15, 10, 0, 0), med.historialTomas[0].fechaHora)
        assertNull(med.historialTomas[0].latitud)
    }

    @Test
    fun `Medicamento fromDocument con campos faltantes usa defaults`() {
        val doc = mock<DocumentSnapshot>()
        whenever(doc.id).thenReturn("doc789")
        whenever(doc.getString("nombre")).thenReturn(null)
        whenever(doc.getString("dosis")).thenReturn(null)
        whenever(doc.getLong("intervaloHoras")).thenReturn(null)
        whenever(doc.getString("horaInicial")).thenReturn(null)
        whenever(doc.getBoolean("repetirDiario")).thenReturn(null)
        whenever(doc.getString("ultimaToma")).thenReturn(null)
        whenever(doc.get("historialTomas")).thenReturn(null)

        val med = Medicamento.fromDocument(doc)

        assertNotNull(med)
        assertEquals("", med!!.nombre)
        assertEquals("", med.dosis)
        assertEquals(8, med.intervaloHoras)
        assertEquals(LocalTime.of(8, 0), med.horaInicial)
        assertTrue(med.repetirDiario)
        assertNull(med.ultimaToma)
        assertTrue(med.historialTomas.isEmpty())
    }

    @Test
    fun `Medicamento fromDocument con historial mixto Map y String`() {
        val doc = mock<DocumentSnapshot>()
        whenever(doc.id).thenReturn("docMix")
        whenever(doc.getString("nombre")).thenReturn("Test")
        whenever(doc.getString("dosis")).thenReturn("1 mg")
        whenever(doc.getLong("intervaloHoras")).thenReturn(4L)
        whenever(doc.getString("horaInicial")).thenReturn("10:00")
        whenever(doc.getBoolean("repetirDiario")).thenReturn(true)
        whenever(doc.getString("ultimaToma")).thenReturn(null)

        val mapItem = mapOf<String, Any?>(
            "fechaHora" to "2025-01-01T08:00:00",
            "latitud" to -33.0,
            "longitud" to -70.0
        )
        whenever(doc.get("historialTomas")).thenReturn(listOf(mapItem, "2025-01-02T09:00:00"))

        val med = Medicamento.fromDocument(doc)

        assertNotNull(med)
        assertEquals(2, med!!.historialTomas.size)
        assertEquals(-33.0, med.historialTomas[0].latitud!!, 0.001)
        assertNull(med.historialTomas[1].latitud)
    }

    @Test
    fun `Medicamento fromDocument con ultimaToma invalida retorna null en ultimaToma`() {
        val doc = mock<DocumentSnapshot>()
        whenever(doc.id).thenReturn("docInvalid")
        whenever(doc.getString("nombre")).thenReturn("Test")
        whenever(doc.getString("dosis")).thenReturn("1 mg")
        whenever(doc.getLong("intervaloHoras")).thenReturn(8L)
        whenever(doc.getString("horaInicial")).thenReturn("08:00")
        whenever(doc.getBoolean("repetirDiario")).thenReturn(true)
        whenever(doc.getString("ultimaToma")).thenReturn("fecha-invalida")
        whenever(doc.get("historialTomas")).thenReturn(null)

        val med = Medicamento.fromDocument(doc)

        assertNotNull(med)
        assertNull(med!!.ultimaToma)
    }

    // ===== User Tests =====

    @Test
    fun `User toMap serializa todos los campos`() {
        val user = User(
            uid = "abc123",
            nombre = "Juan",
            email = "juan@test.com",
            accesibilidadGrande = true,
            recordatorioVisual = false
        )
        val map = user.toMap()

        assertEquals("Juan", map["nombre"])
        assertEquals("juan@test.com", map["email"])
        assertEquals(true, map["accesibilidadGrande"])
        assertEquals(false, map["recordatorioVisual"])
        assertFalse(map.containsKey("uid"))
    }

    @Test
    fun `User toMap con valores por defecto`() {
        val user = User(nombre = "Test", email = "test@test.com")
        val map = user.toMap()

        assertEquals(false, map["accesibilidadGrande"])
        assertEquals(true, map["recordatorioVisual"])
    }

    @Test
    fun `User fromDocument parsea datos completos`() {
        val doc = mock<DocumentSnapshot>()
        whenever(doc.id).thenReturn("uid123")
        whenever(doc.getString("nombre")).thenReturn("Maria")
        whenever(doc.getString("email")).thenReturn("maria@test.com")
        whenever(doc.getBoolean("accesibilidadGrande")).thenReturn(true)
        whenever(doc.getBoolean("recordatorioVisual")).thenReturn(false)

        val user = User.fromDocument(doc)

        assertNotNull(user)
        assertEquals("uid123", user!!.uid)
        assertEquals("Maria", user.nombre)
        assertEquals("maria@test.com", user.email)
        assertTrue(user.accesibilidadGrande)
        assertFalse(user.recordatorioVisual)
    }

    @Test
    fun `User fromDocument con campos nulos usa defaults`() {
        val doc = mock<DocumentSnapshot>()
        whenever(doc.id).thenReturn("uid456")
        whenever(doc.getString("nombre")).thenReturn(null)
        whenever(doc.getString("email")).thenReturn(null)
        whenever(doc.getBoolean("accesibilidadGrande")).thenReturn(null)
        whenever(doc.getBoolean("recordatorioVisual")).thenReturn(null)

        val user = User.fromDocument(doc)

        assertNotNull(user)
        assertEquals("", user!!.nombre)
        assertEquals("", user.email)
        assertFalse(user.accesibilidadGrande)
        assertTrue(user.recordatorioVisual)
    }

    // ===== MedicamentoCatalogo Tests =====

    @Test
    fun `MedicamentoCatalogo toMap serializa nombre y dosis`() {
        val med = MedicamentoCatalogo(nombre = "Aspirina", dosis = "500 mg", firestoreId = "cat123")
        val map = med.toMap()

        assertEquals("Aspirina", map["nombre"])
        assertEquals("500 mg", map["dosis"])
        assertFalse(map.containsKey("firestoreId"))
    }

    @Test
    fun `MedicamentoCatalogo fromDocument parsea correctamente`() {
        val doc = mock<DocumentSnapshot>()
        whenever(doc.id).thenReturn("cat456")
        whenever(doc.getString("nombre")).thenReturn("Omeprazol")
        whenever(doc.getString("dosis")).thenReturn("20 mg")

        val med = MedicamentoCatalogo.fromDocument(doc)

        assertNotNull(med)
        assertEquals("Omeprazol", med!!.nombre)
        assertEquals("20 mg", med.dosis)
        assertEquals("cat456", med.firestoreId)
    }

    @Test
    fun `MedicamentoCatalogo fromDocument con campos nulos`() {
        val doc = mock<DocumentSnapshot>()
        whenever(doc.id).thenReturn("catNull")
        whenever(doc.getString("nombre")).thenReturn(null)
        whenever(doc.getString("dosis")).thenReturn(null)

        val med = MedicamentoCatalogo.fromDocument(doc)

        assertNotNull(med)
        assertEquals("", med!!.nombre)
        assertEquals("", med.dosis)
    }
}
