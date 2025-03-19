package com.example.horumvp.model.repository

import com.google.firebase.firestore.FirebaseFirestore


class FirestoreRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun insertProperty(userId: String, name: String, address: String, rentprice: String, paymentstatus: Boolean, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val propertyData = hashMapOf(
            "nome" to name,
            "endereco" to address,
            "preco" to rentprice,
            "statusPagamento" to paymentstatus
        )

        // Referência para a coleção 'properties' dentro do documento de usuário
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

