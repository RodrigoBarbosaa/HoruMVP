package com.example.horumvp.presenter.register

import com.example.horumvp.model.repository.AuthRepository

class RegisterPresenter(
    private val view: RegisterContract.View,
    private val authRepository: AuthRepository
) : RegisterContract.Presenter {

    override fun register(name: String, email: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            view.showRegisterError("Passwords do not match")
            return
        }

        view.showLoading()
        authRepository.register(email, password, {
            view.hideLoading()
            view.showRegisterSuccess()
        }, { exception ->
            view.hideLoading()
            view.showRegisterError(exception.localizedMessage ?: "Unknown error")
        })
    }
}