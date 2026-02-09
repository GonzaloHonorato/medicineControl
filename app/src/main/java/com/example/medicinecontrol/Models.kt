package com.example.medicinecontrol

import java.time.LocalDateTime
import java.time.LocalTime

data class User(
    val nombre: String,
    val email: String,
    val contrasena: String,
    val accesibilidadGrande: Boolean = false,
    val recordatorioVisual: Boolean = true
)

// Catálogo de medicamentos guardados
data class MedicamentoCatalogo(
    val nombre: String,
    val dosis: String
)

// Medicamento programado con horarios
data class Medicamento(
    val nombre: String,
    val dosis: String,
    val intervaloHoras: Int,
    val horaInicial: LocalTime,
    val repetirDiario: Boolean,
    var ultimaToma: LocalDateTime? = null,
    val historialTomas: MutableList<LocalDateTime> = mutableListOf()
)
