package com.example.horumvp.presenter.registerProperty

import java.util.Date

interface RegisterPropertyContract {
    interface View {
        fun showSuccessMessage(message: String)
        fun showErrorMessage(message: String)
        fun showLoading()
        fun hideLoading()
    }

    interface Presenter {
        fun registerProperty(
            name: String,
            address: String,
            rentPrice: String,
            propertyType: String,
            reminderDate: Date? = null
        )
    }
}
