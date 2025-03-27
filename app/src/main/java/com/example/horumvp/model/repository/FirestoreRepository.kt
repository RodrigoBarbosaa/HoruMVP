package com.example.horumvp.model.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class FirestoreRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun insertProperty(userId: String, name: String, address: String, rentPrice: String, paymentStatus: Boolean, propertyType: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val propertyData = hashMapOf(
            "name" to name,
            "address" to address,
            "rentPrice" to rentPrice,
            "paymentStatus" to paymentStatus,
            "propertyType" to propertyType,
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

    // Função suspensa para obter imóveis com paginação (3 por vez)
    suspend fun getProperties(userId: String, startAfter: DocumentSnapshot? = null): Pair<List<Property>, DocumentSnapshot?> {
        val query = db.collection("properties")
            .document(userId)
            .collection("userProperties")
            .limit(3)

        val snapshotQuery = if (startAfter != null) {
            query.startAfter(startAfter)
        } else {
            query
        }

        return try {
            val result = snapshotQuery.get().await()
            val properties = result.documents.map { document ->
                Property(
                    userId = document.getString("userId") ?: "",
                    name = document.getString("name") ?: "",
                    address = document.getString("address") ?: "",
                    rentPrice = document.getString("rentPrice") ?: "0.0",
                    paymentStatus = document.getBoolean("paymentStatus") ?: false,
                    propertyType = document.getString("propertyType") ?: ""
                )
            }

            Pair(properties, result.documents.lastOrNull())
        } catch (e: Exception) {
            Pair(emptyList(), null)
        }
    }

    // TODO: nao funciona
    fun updatePaymentStatus(userId: String, propertyId: String, newStatus: Boolean) {
        db.collection("properties")
            .document(userId)
            .collection("userProperties")
            .document(propertyId)
            .update("paymentStatus", newStatus)
            .addOnSuccessListener {
                // Sucesso na atualização
            }
            .addOnFailureListener { exception ->
                // Lidar com falhas
            }
    }
}

data class Property(
    val userId: String,
    val name: String,
    val address: String,
    val rentPrice: String,
    val paymentStatus: Boolean,
    val propertyType: String
)


