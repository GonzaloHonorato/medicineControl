package com.example.medicinecontrol

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

object Repository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Usuario actualmente logueado
    var usuarioActual: User? = null

    private val _catalogoFlow = MutableStateFlow<List<MedicamentoCatalogo>>(emptyList())
    val catalogoFlow: StateFlow<List<MedicamentoCatalogo>> = _catalogoFlow

    private val _medicamentosFlow = MutableStateFlow<List<Medicamento>>(emptyList())
    val medicamentosFlow: StateFlow<List<Medicamento>> = _medicamentosFlow

    val catalogo: List<MedicamentoCatalogo> get() = _catalogoFlow.value
    val medicamentos: List<Medicamento> get() = _medicamentosFlow.value

    private fun getUserId(): String? = UserRepository.getCurrentUser()?.uid

    suspend fun cargarCatalogo() {
        val uid = getUserId() ?: return
        try {
            val snapshot = db.collection("users").document(uid)
                .collection("catalogo").get().await()
            val lista = snapshot.documents.mapNotNull { MedicamentoCatalogo.fromDocument(it) }
            _catalogoFlow.value = lista
        } catch (e: Exception) {
            _catalogoFlow.value = emptyList()
        }
    }

    suspend fun cargarMedicamentos() {
        val uid = getUserId() ?: return
        try {
            val snapshot = db.collection("users").document(uid)
                .collection("medicamentos").get().await()
            val lista = snapshot.documents.mapNotNull { Medicamento.fromDocument(it) }
            _medicamentosFlow.value = lista
        } catch (e: Exception) {
            _medicamentosFlow.value = emptyList()
        }
    }

    suspend fun agregarAlCatalogo(med: MedicamentoCatalogo) {
        val uid = getUserId() ?: return
        try {
            val docRef = db.collection("users").document(uid)
                .collection("catalogo").add(med.toMap()).await()
            val nuevo = med.copy(firestoreId = docRef.id)
            _catalogoFlow.value = _catalogoFlow.value + nuevo
        } catch (_: Exception) {}
    }

    suspend fun agregarMedicamento(med: Medicamento) {
        val uid = getUserId() ?: return
        try {
            val docRef = db.collection("users").document(uid)
                .collection("medicamentos").add(med.toMap()).await()
            val nuevo = med.copy(firestoreId = docRef.id)
            _medicamentosFlow.value = _medicamentosFlow.value + nuevo
        } catch (_: Exception) {}
    }

    suspend fun modificarMedicamento(med: Medicamento, transform: (Medicamento) -> Medicamento) {
        val uid = getUserId() ?: return
        try {
            val updated = transform(med)
            if (med.firestoreId.isNotEmpty()) {
                db.collection("users").document(uid)
                    .collection("medicamentos").document(med.firestoreId)
                    .set(updated.toMap()).await()
            }
            _medicamentosFlow.value = _medicamentosFlow.value.map {
                if (it.firestoreId == med.firestoreId && med.firestoreId.isNotEmpty()) updated
                else if (it == med) updated
                else it
            }
        } catch (_: Exception) {}
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
