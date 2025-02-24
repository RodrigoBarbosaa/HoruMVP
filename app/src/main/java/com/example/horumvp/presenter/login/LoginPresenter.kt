package com.example.horumvp.presenter.login

import com.example.horumvp.model.repository.AuthRepository

class LoginPresenter(
    private val view: LoginContract.View,
    private val authRepository: AuthRepository
) : LoginContract.Presenter {

    // controla o fluxo de login e atualiza a view dos eventos
    override fun login(email: String, password: String) {
        view.showLoading()
        authRepository.login(email, password) { success, message ->
            view.hideLoading()
            if (success) {
                view.showLoginSuccess()
            } else {
                view.showLoginError(message ?: "Erro desconhecido")
            }
        }
    }
}

