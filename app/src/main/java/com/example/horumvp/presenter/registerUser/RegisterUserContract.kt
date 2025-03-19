package com.example.horumvp.presenter.registerUser;

interface RegisterUserContract {
    // Define os métodos que a View deve implementar para receber atualizações do Presenter
    interface View {
        fun showRegisterSuccess()
        fun showRegisterError(message: String)
        fun showLoading()
        fun hideLoading()
    }
    // Define os métodos que o Presenter deve ter para processar ações da View.
    interface Presenter {
        fun register(name: String, email: String, password: String, confirmPassword: String)
    }
}
