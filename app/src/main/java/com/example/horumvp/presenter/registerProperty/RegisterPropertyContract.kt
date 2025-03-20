package com.example.horumvp.presenter.registerProperty

interface RegisterPropertyContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showSuccessMessage()
        fun showErrorMessage(message: String)
    }

    interface Presenter {
        fun registerProperty(name: String, address: String, rentPrice: String, propertyType: String)
    }
}
