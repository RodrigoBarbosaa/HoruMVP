package com.example.horumvp.presenter.feed

interface HomeContract {
    interface View {
        fun onLogoutSuccess()
    }

    interface Presenter {
        fun logout()
    }
}