package com.example.medicinecontrol

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object UserRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("No se pudo iniciar sesión"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        email: String,
        password: String,
        nombre: String,
        accesibilidadGrande: Boolean,
        recordatorioVisual: Boolean
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                val profile = User(
                    uid = user.uid,
                    nombre = nombre,
                    email = email,
                    accesibilidadGrande = accesibilidadGrande,
                    recordatorioVisual = recordatorioVisual
                )
                db.collection("users").document(user.uid).set(profile.toMap()).await()
                Result.success(user)
            } else {
                Result.failure(Exception("No se pudo crear la cuenta"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recoverPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val doc = db.collection("users").document(uid).get().await()
            User.fromDocument(doc)
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        auth.signOut()
    }
}
