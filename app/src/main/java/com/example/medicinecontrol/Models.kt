package com.example.medicinecontrol

import java.time.LocalTime

data class User(
    val nombre: String,
    val email: String,
    val contrasena: String,
    val accesibilidadGrande: Boolean = false,
    val recordatorioVisual: Boolean = true
)

data class Medicamento(
    val nombre: String,
    val dosis: String,
    val intervaloHoras: Int,
    val horaInicial: LocalTime,
    val repetirDiario: Boolean
)
