package com.example.horumvp.presenter.home

import com.example.horumvp.model.repository.AuthRepository
import com.example.horumvp.model.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth


class HomePresenter(
    private val view: HomeContract.View,
    private val authRepository: AuthRepository,
    private val propertyRepository: FirestoreRepository
) : HomeContract.Presenter {

    override fun logout() {
        authRepository.logout()
        view.onLogoutSuccess() // Notifica a View que o logout foi realizado
    }

    override fun loadProperties() {
        //TODO: tratamento prévio no userID, nâo mandar se estiver vazio
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val db = FirestoreRepository()
        db.getProperties(userId) { properties ->
            view.displayProperties(properties)
        }
    }

    override fun updatePaymentStatus(propertyId: String, newStatus: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val db = FirestoreRepository()
        db.updatePaymentStatus(userId, propertyId, newStatus)
    }
}