package com.example.horumvp.model.repository

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.horumvp.model.repository.PropertyReminderWorker
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Calendar
import java.util.concurrent.TimeUnit

class FirestoreRepository (context: Context){
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val applicationContext: Context = context.applicationContext

    fun insertProperty(userId: String,
                       name: String,
                       address: String,
                       rentPrice: String,
                       paymentStatus: Boolean,
                       propertyType: String,
                       reminderDate: Date? = null,
                       onSuccess: () -> Unit,
                       onError: (String) -> Unit)
    {
        val propertyData = hashMapOf(
            "name" to name,
            "address" to address,
            "rentPrice" to rentPrice,
            "paymentStatus" to paymentStatus,
            "propertyType" to propertyType,
            "userId" to userId,
            "reminderDate" to reminderDate
        )

        db.collection("properties")
            .document(userId)
            .collection("userProperties")
            .add(propertyData)
            .addOnSuccessListener { documentReference ->

                val propertyId = documentReference.id

                // Se houver uma data de lembrete, agenda a notificação
                if (reminderDate != null) {
                    schedulePropertyReminder(
                        propertyId = propertyId,
                        userId = userId,
                        propertyName = name,
                        reminderDate = reminderDate,
                        reminderText = "Verificar o imóvel: $name"
                    )
                }

                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Erro ao inserir uma propriedade")
            }
    }

    private fun schedulePropertyReminder(
        propertyId: String,
        userId: String,
        propertyName: String,
        reminderDate: Date,
        reminderText: String
    ) {
        val currentDate = Calendar.getInstance().time

        // Calcular o atraso até a data especificada
        val delay = reminderDate.time - currentDate.time

        Log.d("NOTIFICATION DEBUG", "Delay = $delay")

        // Se a data já passou, não agendar
        if (delay <= 0) {
            // Exiba a notificação imediatamente se o delay for 0 ou negativo
            val notificationHelper = NotificationHelper(applicationContext)
            val notificationText = "Hoje é o dia de pagamento do imovel $propertyName"
            notificationHelper.showNotification(userId, propertyId, propertyName, notificationText)

            return
        }

        // Preparar os dados para o Worker
        val inputData = Data.Builder()
            .putString("PROPERTY_ID", propertyId)
            .putString("USER_ID", userId)
            .putString("PROPERTY_NAME", propertyName)
            .putString("NOTIFICATION_TEXT", reminderText)
            .build()

        // Criar a solicitação de trabalho com o delay calculado
        val reminderWorkRequest = OneTimeWorkRequestBuilder<PropertyReminderWorker>()
            .setInputData(inputData)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS) // Defina o delay calculado
            .build()

        // Agendar o trabalho com um identificador único baseado no propertyId
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                "property_reminder_${userId}_$propertyId",
                ExistingWorkPolicy.REPLACE,
                reminderWorkRequest
            )
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
                    propertyType = document.getString("propertyType") ?: "",
                    propertyId = document.id
                )
            }

            Pair(properties, result.documents.lastOrNull())
        } catch (e: Exception) {
            Pair(emptyList(), null)
        }
    }


    fun updatePaymentStatus(userId: String, propertyId: String, newStatus: Boolean, callback: (Boolean, String?) -> Unit) {
        db.collection("properties")
            .document(userId)
            .collection("userProperties")
            .document(propertyId)
            .update("paymentStatus", newStatus)
            .addOnSuccessListener {
                callback(true, null) // Sucesso
            }
            .addOnFailureListener { exception ->
                callback(false, exception.localizedMessage ?: "Erro desconhecido") // Erro
            }
    }
}

data class Property(
    val userId: String,
    val name: String,
    val address: String,
    val rentPrice: String,
    val paymentStatus: Boolean,
    val propertyType: String,
    val propertyId: String
)


