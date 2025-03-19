package com.example.horumvp.presenter.registerProperty

import com.example.horumvp.model.repository.FirestoreRepository
import com.example.horumvp.presenter.registerProperty.RegisterPropertyContract
import com.example.horumvp.model.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.rpc.context.AttributeContext.Auth

class RegisterPropertyPresenter(private val view: RegisterPropertyContract.View, private val repository: FirestoreRepository) : RegisterPropertyContract.Presenter {
    override fun registerProperty(name: String, address: String, rentPrice: String) {
        view.showLoading()

        // O status de pagamento é falso por padrão
        val paymentStatus = false
        val authRepository = AuthRepository()

        val currentUser = authRepository.getCurrentUser()
        val userId = currentUser?.uid

        if (userId != null) {
            repository.insertProperty(userId, name, address, rentPrice, paymentStatus,
                onSuccess = {
                    view.hideLoading()
                    view.showSuccessMessage()
                },
                onError = { message ->
                    view.hideLoading()
                    view.showErrorMessage(message)
                }
            )
        } else {
            // Caso não haja usuário autenticado
            view.hideLoading()
            view.showErrorMessage("Usuário não autenticado.")
        }
    }
}
