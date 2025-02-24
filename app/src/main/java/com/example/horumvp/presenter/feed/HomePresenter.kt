package com.example.horumvp.presenter.feed

import com.example.horumvp.model.repository.AuthRepository


class HomePresenter(
    private val view: HomeContract.View,
    private val authRepository: AuthRepository
) : HomeContract.Presenter {

    override fun logout() {
        authRepository.logout()
        view.onLogoutSuccess() // Notifica a View que o logout foi realizado
    }
}