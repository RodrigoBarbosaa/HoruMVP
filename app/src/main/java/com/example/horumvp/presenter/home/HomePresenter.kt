package com.example.horumvp.presenter.home

import com.example.horumvp.model.repository.AuthRepository
import com.example.horumvp.model.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomePresenter(
    private val view: HomeContract.View,
    private val authRepository: AuthRepository,
    private val propertyRepository: FirestoreRepository
) : HomeContract.Presenter {

    private var lastVisible: DocumentSnapshot? = null

    override fun logout() {
        authRepository.logout()
    }

    // corrotina com lazy loading para eficiencia
    override fun loadProperties() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        view.showLoading()  // Exibe o carregamento na interface

        // Executa a chamada para obter os imóveis
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Obtendo as propriedades usando o FirestoreRepository
                val (properties, lastVisibleDoc) = propertyRepository.getProperties(userId, lastVisible)

                // Atualiza a view com as propriedades carregadas
                view.displayProperties(properties)

                // Atualiza o último documento para carregar mais imóveis depois
                lastVisible = lastVisibleDoc

                // Controla a visibilidade do botão "Carregar mais"
                // Se houver mais imóveis para carregar, o botão "Carregar mais" será exibido
                view.toggleLoadMoreButton(properties.size == 3)
            } catch (e: Exception) {
                view.showLoginError(e.localizedMessage ?: "Erro ao carregar imóveis")
            } finally {
                view.hideLoading()  // Oculta a tela de carregamento
            }
        }
    }

    // Função para carregar mais imóveis
    override fun loadMoreProperties() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        view.showLoading()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Carrega mais imóveis a partir de `lastVisible`
                val (properties, lastVisibleDoc) = propertyRepository.getProperties(userId, lastVisible)
                view.addProperties(properties)

                // Atualiza o último documento para carregar a próxima página de imóveis
                lastVisible = lastVisibleDoc

                // Se houver mais imóveis para carregar, exibe o botão "Carregar mais"
                view.toggleLoadMoreButton(properties.size == 3)
            } catch (e: Exception) {
                view.showLoginError(e.localizedMessage ?: "Erro ao carregar mais imóveis")
            } finally {
                view.hideLoading()
            }
        }
    }

    override fun updatePaymentStatus(propertyId: String, newStatus: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val db = FirestoreRepository()
        db.updatePaymentStatus(userId, propertyId, newStatus)
    }
}