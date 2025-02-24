package com.example.horumvp.model.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.horumvp.model.User

class FirestoreRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun addUser(user: User, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        db.collection("users").document(user.id).set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun getUser(userId: String, onSuccess: (User) -> Unit, onError: (Exception) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                user?.let { onSuccess(it) }
            }
            .addOnFailureListener { e -> onError(e) }
    }
}

