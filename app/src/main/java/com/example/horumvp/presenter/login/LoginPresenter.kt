package com.example.horumvp.presenter.login

import com.example.horumvp.model.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginPresenter(
    private val view: LoginContract.View,
    private val authRepository: AuthRepository
) : LoginContract.Presenter {

    // controla o fluxo de login e atualiza a view dos eventos
    override fun login(email: String, password: String) {
        // Exibe a tela de loading
        view.showLoading()

        // lógica de login em uma corrotina
        CoroutineScope(Dispatchers.Main).launch {
            try {
                authRepository.login(email, password).onSuccess {
                    view.hideLoading()
                    view.showLoginSuccess()
                }.onFailure { exception ->
                    view.hideLoading()
                    view.showLoginError(exception.localizedMessage ?: "Erro ao logar")
                }
            } catch (e: Exception) {
                view.hideLoading()
                view.showLoginError("Erro ao logar")
            }
        }
    }
}

