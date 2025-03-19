package com.example.horumvp.presenter.registerUser

import com.example.horumvp.model.repository.AuthRepository

class RegisterUserPresenter(
    private val view: RegisterUserContract.View,
    private val authRepository: AuthRepository
) : RegisterUserContract.Presenter {

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