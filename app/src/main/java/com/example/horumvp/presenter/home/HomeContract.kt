package com.example.horumvp.presenter.home

interface HomeContract {
    interface View {
        fun onLogoutSuccess()
    }

    interface Presenter {
        fun logout()
    }
}