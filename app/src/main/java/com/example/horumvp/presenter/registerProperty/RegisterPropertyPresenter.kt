package com.example.horumvp.presenter.registerProperty

import com.example.horumvp.model.repository.FirestoreRepository
import com.example.horumvp.presenter.registerProperty.RegisterPropertyContract
import com.example.horumvp.model.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.rpc.context.AttributeContext.Auth

class RegisterPropertyPresenter(
    private val view: RegisterPropertyContract.View,
    private val repository: FirestoreRepository
) : RegisterPropertyContract.Presenter {

    override fun registerProperty(name: String, address: String, rentPrice: String, propertyType: String) {
        view.showLoading()

        val paymentStatus = false
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            repository.insertProperty(userId, name, address, rentPrice, paymentStatus, propertyType,
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
            view.hideLoading()
            view.showErrorMessage("Usuário não autenticado.")
        }
    }
}
