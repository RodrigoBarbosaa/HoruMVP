package com.example.horumvp.model.repository

import com.google.firebase.firestore.FirebaseFirestore


class FirestoreRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun insertProperty(userId: String, name: String, address: String, rentPrice: String, paymentStatus: Boolean, propertyType: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val propertyData = hashMapOf(
            "nome" to name,
            "endereco" to address,
            "preco" to rentPrice,
            "statusPagamento" to paymentStatus,
            "tipo" to propertyType, // Armazena o tipo de imóvel
            "userId" to userId
        )

        db.collection("properties")
            .document(userId)
            .collection("userProperties")
            .add(propertyData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Erro ao inserir uma propriedade")
            }
    }
}

