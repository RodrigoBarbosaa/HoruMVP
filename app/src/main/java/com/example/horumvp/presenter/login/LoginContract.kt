package com.example.horumvp.presenter.login

interface LoginContract {
    // Define os métodos que a View deve implementar para receber atualizações do Presenter
    interface View {
        fun showLoginSuccess()
        fun showLoginError(message: String)
        fun showLoading()
        fun hideLoading()
    }
    // Define os métodos que o Presenter deve ter para processar ações da View.
    interface Presenter {
        fun login(email: String, password: String)
    }
}