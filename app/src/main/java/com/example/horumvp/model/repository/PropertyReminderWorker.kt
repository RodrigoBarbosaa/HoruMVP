package com.example.horumvp.model.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.horumvp.R

class PropertyReminderWorker(context: Context, params: WorkerParameters)
    : Worker(context, params) {

    override fun doWork(): Result {
        val propertyId = inputData.getString("PROPERTY_ID") ?: return Result.failure()
        val userId = inputData.getString("USER_ID") ?: return Result.failure()
        val propertyName = inputData.getString("PROPERTY_NAME") ?: "Imóvel"
        val notificationText = inputData.getString("NOTIFICATION_TEXT")
            ?: "Você tem um lembrete para este imóvel"

        // Criar e mostrar a notificação
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showNotification(userId, propertyId, propertyName, notificationText)

        return Result.success()
    }
}

class NotificationHelper(private val context: Context) {

    fun showNotification(
        userId: String,
        propertyId: String,
        propertyName: String,
        notificationText: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Criar canal de notificação (necessário para Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "property_reminders",
                "Lembretes de Imóveis",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Construir a notificação
        val notification = NotificationCompat.Builder(context, "property_reminders")
            .setSmallIcon(R.drawable.logo_image) // Substitua pelo seu ícone
            .setContentTitle(propertyName)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // Mostrar a notificação
        notificationManager.notify(propertyId.hashCode(), notification)
    }
}
