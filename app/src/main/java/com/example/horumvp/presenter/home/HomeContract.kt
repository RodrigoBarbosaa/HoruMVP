package com.example.horumvp.presenter.home

import com.example.horumvp.model.repository.Property

interface HomeContract {
    interface View {
        fun onLogoutSuccess()
        fun displayProperties(properties: List<Property>)
    }

    interface Presenter {
        fun logout()
        fun loadProperties()
        fun updatePaymentStatus(propertyId: String, newStatus: Boolean)
    }
}