package com.example.medicinecontrol

import androidx.compose.runtime.mutableStateListOf

object Repository {
    val usuarios = mutableStateListOf<User>(
        User("Usuario 1", "test1@test.com", "123456"),
        User("Usuario 2", "test2@test.com", "123456"),
        User("Usuario 3", "test3@test.com", "123456"),
        User("Usuario 4", "test4@test.com", "123456"),
        User("Usuario 5", "test5@test.com", "123456")
    )

    // Catálogo de medicamentos guardados
    val catalogo = mutableStateListOf<MedicamentoCatalogo>()

    // Medicamentos programados
    val medicamentos = mutableStateListOf<Medicamento>()

    fun agregarUsuario(user: User): Boolean {
        usuarios.add(user)
        return true
    }

    fun agregarAlCatalogo(med: MedicamentoCatalogo) {
        catalogo.add(med)
    }

    fun agregarMedicamento(med: Medicamento) {
        medicamentos.add(med)
    }
}
