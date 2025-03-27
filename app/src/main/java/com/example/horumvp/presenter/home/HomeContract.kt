package com.example.horumvp.presenter.home

import com.example.horumvp.model.repository.Property

interface HomeContract {
    interface View {
        fun onLogoutSuccess()
        fun displayProperties(properties: List<Property>)
        fun addProperties(properties: List<Property>)
        fun showLoginError(message: String)
        fun showLoading()
        fun hideLoading()
        fun toggleLoadMoreButton(show: Boolean)
    }

    interface Presenter {
        fun logout()
        fun loadProperties()
        fun loadMoreProperties()
        fun updatePaymentStatus(propertyId: String, newStatus: Boolean)
    }
}