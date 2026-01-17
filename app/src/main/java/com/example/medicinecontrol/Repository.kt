package com.example.medicinecontrol

import androidx.compose.runtime.mutableStateListOf

object Repository {
    val usuarios = mutableStateListOf<User>(
        User("Usuario 1", "test@test.com", "123456")
    )
    
    val medicamentos = mutableStateListOf<Medicamento>()

    fun agregarUsuario(user: User): Boolean {
        if (usuarios.size < 5) {
            usuarios.add(user)
            return true
        }
        return false
    }

    fun agregarMedicamento(med: Medicamento) {
        medicamentos.add(med)
    }
}
