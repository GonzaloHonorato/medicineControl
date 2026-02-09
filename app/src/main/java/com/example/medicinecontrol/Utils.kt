package com.example.medicinecontrol

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.isValidEmail(): Boolean {
    return try {
        this.trim().matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"))
    } catch (e: Exception) {
        false
    }
}

fun String.toIntOrDefault(default: Int = 0): Int {
    return try {
        this.toInt()
    } catch (e: NumberFormatException) {
        default
    }
}

fun LocalDateTime.formatFriendly(): String {
    return try {
        this.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    } catch (e: Exception) {
        "Fecha inválida"
    }
}

fun LocalDateTime.formatTime(): String {
    return try {
        this.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        "00:00"
    }
}

fun Medicamento.calcularProximaToma(ahora: LocalDateTime = LocalDateTime.now()): LocalDateTime {
    if (this.ultimaToma == null) {
        val hoy = ahora.toLocalDate()
        var proximaToma = LocalDateTime.of(hoy, this.horaInicial)
        while (proximaToma.isBefore(ahora)) {
            proximaToma = proximaToma.plusHours(this.intervaloHoras.toLong())
        }
        return proximaToma
    }

    var proximaToma = this.ultimaToma!!.plusHours(this.intervaloHoras.toLong())
    while (proximaToma.isBefore(ahora)) {
        proximaToma = proximaToma.plusHours(this.intervaloHoras.toLong())
    }
    return proximaToma
}

fun Medicamento.estaAtrasado(ahora: LocalDateTime = LocalDateTime.now(), margenMinutos: Int = 15): Boolean {
    return try {
        val proximaToma = this.calcularProximaToma(ahora)
        ahora.isAfter(proximaToma.plusMinutes(margenMinutos.toLong()))
    } catch (e: Exception) {
        false
    }
}

fun List<Medicamento>.ordenarPorProximidad(): List<Medicamento> {
    return try {
        val ahora = LocalDateTime.now()
        this.sortedBy { it.calcularProximaToma(ahora) }
    } catch (e: Exception) {
        this
    }
}

fun <T> executeWithTryCatch(defaultValue: T, block: () -> T): T {
    return try {
        block()
    } catch (e: Exception) {
        defaultValue
    }
}

fun <T> List<T>.safeGet(index: Int): T? {
    return try {
        if (index in this.indices) this[index] else null
    } catch (e: Exception) {
        null
    }
}

fun validarCampos(vararg validaciones: Pair<Boolean, () -> Unit>): Boolean {
    var todosValidos = true
    validaciones.forEach { (esValido, onError) ->
        if (!esValido) {
            todosValidos = false
            executeWithTryCatch(Unit) { onError() }
        }
    }
    return todosValidos
}

inline fun <T> processWithCallback(
    item: T,
    onSuccess: (T) -> Unit,
    onError: (Exception) -> Unit = {}
) {
    try {
        onSuccess(item)
    } catch (e: Exception) {
        onError(e)
    }
}
