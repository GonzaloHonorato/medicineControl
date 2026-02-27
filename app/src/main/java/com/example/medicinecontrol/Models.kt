package com.example.medicinecontrol

import com.google.firebase.firestore.DocumentSnapshot
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class User(
    val uid: String = "",
    val nombre: String,
    val email: String,
    val accesibilidadGrande: Boolean = false,
    val recordatorioVisual: Boolean = true
) {
    fun toMap(): Map<String, Any> = mapOf(
        "nombre" to nombre,
        "email" to email,
        "accesibilidadGrande" to accesibilidadGrande,
        "recordatorioVisual" to recordatorioVisual
    )

    companion object {
        fun fromDocument(doc: DocumentSnapshot): User? {
            return try {
                User(
                    uid = doc.id,
                    nombre = doc.getString("nombre") ?: "",
                    email = doc.getString("email") ?: "",
                    accesibilidadGrande = doc.getBoolean("accesibilidadGrande") ?: false,
                    recordatorioVisual = doc.getBoolean("recordatorioVisual") ?: true
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

// Catálogo de medicamentos guardados
data class MedicamentoCatalogo(
    val nombre: String,
    val dosis: String,
    val firestoreId: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "nombre" to nombre,
        "dosis" to dosis
    )

    companion object {
        fun fromDocument(doc: DocumentSnapshot): MedicamentoCatalogo? {
            return try {
                MedicamentoCatalogo(
                    nombre = doc.getString("nombre") ?: "",
                    dosis = doc.getString("dosis") ?: "",
                    firestoreId = doc.id
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

// Medicamento programado con horarios
data class Medicamento(
    val nombre: String,
    val dosis: String,
    val intervaloHoras: Int,
    val horaInicial: LocalTime,
    val repetirDiario: Boolean,
    val ultimaToma: LocalDateTime? = null,
    val historialTomas: MutableList<LocalDateTime> = mutableListOf(),
    val firestoreId: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "nombre" to nombre,
        "dosis" to dosis,
        "intervaloHoras" to intervaloHoras,
        "horaInicial" to horaInicial.format(DateTimeFormatter.ofPattern("HH:mm")),
        "repetirDiario" to repetirDiario,
        "ultimaToma" to ultimaToma?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        "historialTomas" to historialTomas.map { it.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
    )

    companion object {
        fun fromDocument(doc: DocumentSnapshot): Medicamento? {
            return try {
                val historialList = (doc.get("historialTomas") as? List<*>)
                    ?.mapNotNull { it as? String }
                    ?.mapNotNull {
                        try { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
                        catch (e: Exception) { null }
                    }
                    ?.toMutableList() ?: mutableListOf()

                val ultimaTomaStr = doc.getString("ultimaToma")
                val ultimaToma = ultimaTomaStr?.let {
                    try { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
                    catch (e: Exception) { null }
                }

                Medicamento(
                    nombre = doc.getString("nombre") ?: "",
                    dosis = doc.getString("dosis") ?: "",
                    intervaloHoras = (doc.getLong("intervaloHoras") ?: 8).toInt(),
                    horaInicial = LocalTime.parse(
                        doc.getString("horaInicial") ?: "08:00",
                        DateTimeFormatter.ofPattern("HH:mm")
                    ),
                    repetirDiario = doc.getBoolean("repetirDiario") ?: true,
                    ultimaToma = ultimaToma,
                    historialTomas = historialList,
                    firestoreId = doc.id
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
