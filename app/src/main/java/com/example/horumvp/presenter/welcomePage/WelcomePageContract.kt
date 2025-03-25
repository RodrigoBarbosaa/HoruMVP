package com.example.horumvp.presenter.login

interface WelcomePageContract {
    // Define os métodos que a View deve implementar para receber atualizações do Presenter
    interface View {
        fun redirectToLogin()
        fun redirectToSignUp()

    }
    // Define os métodos que o Presenter deve ter para processar ações da View.
    interface Presenter {
        fun onLogin()
        fun onSignUp()
    }
}