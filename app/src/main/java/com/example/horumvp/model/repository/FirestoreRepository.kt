package com.example.horumvp.model.repository

import com.google.firebase.firestore.FirebaseFirestore


class FirestoreRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun insertProperty(userId: String, name: String, address: String, rentPrice: String, paymentStatus: Boolean, propertyType: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val propertyData = hashMapOf(
            "name" to name,
            "address" to address,
            "rentPrice" to rentPrice,
            "paymentStatus" to paymentStatus,
            "propertyType" to propertyType, // Armazena o tipo de imóvel
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

    fun getProperties(userId: String, callback: (List<Property>) -> Unit) {
        db.collection("properties")
            .document(userId)  // Busca no documento do usuário
            .collection("userProperties")  // Busca na subcoleção userProperties
            .get()
            .addOnSuccessListener { result ->
                val properties = result.map { document ->
                    Property(
                        userId = document.getString("userId") ?: "",
                        name = document.getString("name") ?: "",
                        address = document.getString("address") ?: "",
                        rentPrice = document.getString("rentPrice") ?: "0.0",
                        paymentStatus = document.getBoolean("paymentStatus") ?: false,
                        propertyType = document.getString("propertyType") ?: ""
                    )
                }
                callback(properties)
            }
            .addOnFailureListener { exception ->
                // Lidar com falhas
            }
    }
    // TODO: Lidar com erro e sucesso
    fun updatePaymentStatus(userId: String, propertyId: String, newStatus: Boolean) {
        db.collection("properties")
            .document(userId)  // Acesse o documento do usuário
            .collection("userProperties")  // Acesse a subcoleção de propriedades do usuário
            .document(propertyId)  // Documento específico da propriedade
            .update("paymentStatus", newStatus)  // Atualiza o status de pagamento
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


