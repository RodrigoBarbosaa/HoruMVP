package com.example.horumvp.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await() // corrotinas
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun register(email: String, password: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun logout() {
        auth.signOut()
    }

     fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}

