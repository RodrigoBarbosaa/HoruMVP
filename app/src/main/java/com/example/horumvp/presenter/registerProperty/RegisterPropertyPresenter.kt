package com.example.horumvp.presenter.registerProperty

import com.example.horumvp.model.repository.FirestoreRepository
import com.example.horumvp.model.repository.PropertyReminderWorker
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegisterPropertyPresenter(
    private val view: RegisterPropertyContract.View,
    private val repository: FirestoreRepository
) : RegisterPropertyContract.Presenter {

    override fun registerProperty(
        name: String,
        address: String,
        rentPrice: String,
        propertyType: String,
        reminderDate: Date?
    ) {
        view.showLoading()

        val paymentStatus = false

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            repository.insertProperty(userId, name, address, rentPrice, paymentStatus, propertyType, reminderDate,
                onSuccess = {
                    view.hideLoading()
                    val message = if (reminderDate != null) {
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                        "Imóvel cadastrado com sucesso. Lembrete agendado para ${dateFormat.format(reminderDate)}."
                    } else {
                        "Imóvel cadastrado com sucesso."
                    }
                    view.showSuccessMessage(message)
                },
                onError = { message ->
                    view.hideLoading()
                    view.showErrorMessage(message)
                }
            )
        } else {
            view.hideLoading()
            view.showErrorMessage("Usuário não autenticado.")
        }
    }
}
