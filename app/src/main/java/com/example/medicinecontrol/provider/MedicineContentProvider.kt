package com.example.medicinecontrol.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.medicinecontrol.Medicamento
import com.example.medicinecontrol.Repository
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MedicineContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.medicinecontrol.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/medicamentos")

        private const val MEDICAMENTOS = 1
        private const val MEDICAMENTO_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "medicamentos", MEDICAMENTOS)
            addURI(AUTHORITY, "medicamentos/#", MEDICAMENTO_ID)
        }

        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_DOSIS = "dosis"
        const val COL_INTERVALO = "intervalo_horas"
        const val COL_HORA_INICIAL = "hora_inicial"
        const val COL_REPETIR_DIARIO = "repetir_diario"
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val columns = arrayOf(COL_ID, COL_NOMBRE, COL_DOSIS, COL_INTERVALO, COL_HORA_INICIAL, COL_REPETIR_DIARIO)
        val cursor = MatrixCursor(columns)

        val meds = Repository.medicamentos

        when (uriMatcher.match(uri)) {
            MEDICAMENTOS -> {
                meds.forEachIndexed { index, med ->
                    cursor.addRow(med.toRow(index))
                }
            }
            MEDICAMENTO_ID -> {
                val id = ContentUris.parseId(uri).toInt()
                if (id in meds.indices) {
                    cursor.addRow(meds[id].toRow(id))
                }
            }
        }

        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (uriMatcher.match(uri) != MEDICAMENTOS || values == null) return null

        val med = Medicamento(
            nombre = values.getAsString(COL_NOMBRE) ?: "",
            dosis = values.getAsString(COL_DOSIS) ?: "",
            intervaloHoras = values.getAsInteger(COL_INTERVALO) ?: 8,
            horaInicial = LocalTime.parse(
                values.getAsString(COL_HORA_INICIAL) ?: "08:00",
                DateTimeFormatter.ofPattern("HH:mm")
            ),
            repetirDiario = (values.getAsInteger(COL_REPETIR_DIARIO) ?: 1) == 1
        )

        // Agregar localmente (sin Firestore, el provider es solo lectura/local)
        val index = Repository.medicamentos.size
        context?.contentResolver?.notifyChange(uri, null)
        return ContentUris.withAppendedId(CONTENT_URI, index.toLong())
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            MEDICAMENTOS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.medicamentos"
            MEDICAMENTO_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.medicamentos"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    private fun Medicamento.toRow(index: Int): Array<Any> {
        return arrayOf(
            index,
            nombre,
            dosis,
            intervaloHoras,
            horaInicial.format(DateTimeFormatter.ofPattern("HH:mm")),
            if (repetirDiario) 1 else 0
        )
    }
}
