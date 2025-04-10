package com.example.horumvp.presenter.login

import com.example.horumvp.model.repository.AuthRepository
import com.example.horumvp.model.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth

class WelcomePagePresenter(
    private val view: WelcomePageContract.View,
) : WelcomePageContract.Presenter {
    override fun onLogin() { view.redirectToLogin() }
    override fun onSignUp() { view.redirectToSignUp() }
}

