package com.example.medicinecontrol

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

object Repository {
    private const val TAG = "Repository"
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Usuario actualmente logueado
    var usuarioActual: User? = null

    private val _catalogoFlow = MutableStateFlow<List<MedicamentoCatalogo>>(emptyList())
    val catalogoFlow: StateFlow<List<MedicamentoCatalogo>> = _catalogoFlow

    private val _medicamentosFlow = MutableStateFlow<List<Medicamento>>(emptyList())
    val medicamentosFlow: StateFlow<List<Medicamento>> = _medicamentosFlow

    val catalogo: List<MedicamentoCatalogo> get() = _catalogoFlow.value
    val medicamentos: List<Medicamento> get() = _medicamentosFlow.value

    private fun getUserId(): String? {
        val uid = UserRepository.getCurrentUser()?.uid
        Log.d(TAG, "getUserId: $uid")
        return uid
    }

    suspend fun cargarCatalogo() {
        val uid = getUserId() ?: run {
            Log.e(TAG, "cargarCatalogo: uid es null")
            return
        }
        try {
            val snapshot = db.collection("users").document(uid)
                .collection("catalogo").get().await()
            val lista = snapshot.documents.mapNotNull { MedicamentoCatalogo.fromDocument(it) }
            Log.d(TAG, "cargarCatalogo: ${lista.size} items cargados")
            _catalogoFlow.value = lista
        } catch (e: Exception) {
            Log.e(TAG, "cargarCatalogo ERROR: ${e.message}", e)
            _catalogoFlow.value = emptyList()
        }
    }

    suspend fun cargarMedicamentos() {
        val uid = getUserId() ?: run {
            Log.e(TAG, "cargarMedicamentos: uid es null")
            return
        }
        try {
            val snapshot = db.collection("users").document(uid)
                .collection("medicamentos").get().await()
            val lista = snapshot.documents.mapNotNull { Medicamento.fromDocument(it) }
            Log.d(TAG, "cargarMedicamentos: ${lista.size} items cargados")
            _medicamentosFlow.value = lista
        } catch (e: Exception) {
            Log.e(TAG, "cargarMedicamentos ERROR: ${e.message}", e)
            _medicamentosFlow.value = emptyList()
        }
    }

    suspend fun agregarAlCatalogo(med: MedicamentoCatalogo) {
        val uid = getUserId() ?: run {
            Log.e(TAG, "agregarAlCatalogo: uid es null")
            return
        }
        try {
            Log.d(TAG, "agregarAlCatalogo: guardando ${med.nombre}")
            val docRef = db.collection("users").document(uid)
                .collection("catalogo").add(med.toMap()).await()
            Log.d(TAG, "agregarAlCatalogo: guardado con id ${docRef.id}")
            val nuevo = med.copy(firestoreId = docRef.id)
            _catalogoFlow.value = _catalogoFlow.value + nuevo
        } catch (e: Exception) {
            Log.e(TAG, "agregarAlCatalogo ERROR: ${e.message}", e)
        }
    }

    suspend fun agregarMedicamento(med: Medicamento) {
        val uid = getUserId() ?: run {
            Log.e(TAG, "agregarMedicamento: uid es null")
            return
        }
        try {
            val data = med.toMap()
            Log.d(TAG, "agregarMedicamento: guardando ${med.nombre}, data=$data")
            val docRef = db.collection("users").document(uid)
                .collection("medicamentos").add(data).await()
            Log.d(TAG, "agregarMedicamento: guardado con id ${docRef.id}")
            val nuevo = med.copy(firestoreId = docRef.id)
            _medicamentosFlow.value = _medicamentosFlow.value + nuevo
        } catch (e: Exception) {
            Log.e(TAG, "agregarMedicamento ERROR: ${e.message}", e)
        }
    }

    suspend fun modificarMedicamento(med: Medicamento, transform: (Medicamento) -> Medicamento) {
        val uid = getUserId() ?: run {
            Log.e(TAG, "modificarMedicamento: uid es null")
            return
        }
        try {
            val updated = transform(med)
            if (med.firestoreId.isNotEmpty()) {
                Log.d(TAG, "modificarMedicamento: actualizando ${med.firestoreId}")
                db.collection("users").document(uid)
                    .collection("medicamentos").document(med.firestoreId)
                    .set(updated.toMap()).await()
                Log.d(TAG, "modificarMedicamento: actualizado OK")
            } else {
                Log.w(TAG, "modificarMedicamento: firestoreId vacío, no se actualiza en Firestore")
            }
            _medicamentosFlow.value = _medicamentosFlow.value.map {
                if (it.firestoreId == med.firestoreId && med.firestoreId.isNotEmpty()) updated
                else if (it == med) updated
                else it
            }
        } catch (e: Exception) {
            Log.e(TAG, "modificarMedicamento ERROR: ${e.message}", e)
        }
    }

    fun filtrarMedicamentos(predicate: (Medicamento) -> Boolean): List<Medicamento> {
        return executeWithTryCatch(emptyList()) {
            _medicamentosFlow.value.filter(predicate)
        }
    }

    fun aplicarATodos(action: (Medicamento) -> Unit) {
        executeWithTryCatch(Unit) {
            _medicamentosFlow.value.forEach(action)
        }
    }

    fun cerrarSesion() {
        UserRepository.logout()
        usuarioActual = null
        _catalogoFlow.value = emptyList()
        _medicamentosFlow.value = emptyList()
    }
}
