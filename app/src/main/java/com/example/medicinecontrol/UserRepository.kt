package com.example.medicinecontrol

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object UserRepository {
    private const val TAG = "UserRepository"
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "login: intentando con $email")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Log.d(TAG, "login: exitoso uid=${user.uid}")
                Result.success(user)
            } else {
                Log.e(TAG, "login: user es null después de signIn")
                Result.failure(Exception("No se pudo iniciar sesión"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "login ERROR: ${e.message}", e)
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
            Log.d(TAG, "register: creando cuenta $email")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Log.d(TAG, "register: cuenta creada uid=${user.uid}")
                val profile = User(
                    uid = user.uid,
                    nombre = nombre,
                    email = email,
                    accesibilidadGrande = accesibilidadGrande,
                    recordatorioVisual = recordatorioVisual
                )
                Log.d(TAG, "register: guardando perfil en Firestore")
                db.collection("users").document(user.uid).set(profile.toMap()).await()
                Log.d(TAG, "register: perfil guardado OK")
                Result.success(user)
            } else {
                Log.e(TAG, "register: user es null después de createUser")
                Result.failure(Exception("No se pudo crear la cuenta"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "register ERROR: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun recoverPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "recoverPassword ERROR: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return try {
            Log.d(TAG, "getUserProfile: leyendo perfil de ${firebaseUser.uid}")
            val doc = db.collection("users").document(firebaseUser.uid).get().await()
            if (doc.exists()) {
                Log.d(TAG, "getUserProfile: documento encontrado")
                User.fromDocument(doc)
            } else {
                Log.d(TAG, "getUserProfile: documento NO existe, creando perfil")
                val newProfile = User(
                    uid = firebaseUser.uid,
                    nombre = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "",
                    email = firebaseUser.email ?: ""
                )
                db.collection("users").document(firebaseUser.uid).set(newProfile.toMap()).await()
                Log.d(TAG, "getUserProfile: perfil creado OK")
                newProfile
            }
        } catch (e: Exception) {
            Log.e(TAG, "getUserProfile ERROR: ${e.message}", e)
            null
        }
    }

    fun logout() {
        auth.signOut()
    }
}
