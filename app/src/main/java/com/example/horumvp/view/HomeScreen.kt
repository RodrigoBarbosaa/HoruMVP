package com.example.horumvp.view.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.horu.ui.auth.LoginScreenState
import com.example.horu.ui.auth.LoginView
import com.example.horu.ui.auth.LoginViewImpl
import com.example.horumvp.model.repository.AuthRepository
import com.example.horumvp.presenter.home.HomeContract
import com.example.horumvp.presenter.home.HomePresenter
import com.example.horumvp.presenter.login.LoginPresenter

@Composable
fun HomeScreen(navToLogin: () -> Unit, navToRegisterProperty: () -> Unit) {
    val authRepository = remember { AuthRepository() }
    val homeScreenState = remember { mutableStateOf(HomeScreenState()) }
    val presenter = remember { HomePresenter(HomeViewImpl(homeScreenState, navToLogin), authRepository) }

    HomeView(
        state = homeScreenState.value,
        onLogoutClick = { presenter.logout() },
        onRegisterPropertyClick = navToRegisterProperty
    )
}

@Composable
fun HomeView(
    state: HomeScreenState,
    onLogoutClick: () -> Unit,
    onRegisterPropertyClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Bem-vindo à Home!", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRegisterPropertyClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar novo Imóvel")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sair")
        }
    }
}

data class HomeScreenState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewImpl(
    private val state: MutableState<HomeScreenState>,
    private val onLogoutSuccess: () -> Unit
) : HomeContract.View {

    override fun onLogoutSuccess() {
        state.value = state.value.copy(isLoading = false)
        onLogoutSuccess() // Navega de volta para o login após o logout
    }
}

class HomePresenter(
    private val view: HomeContract.View,
    private val authRepository: AuthRepository
) : HomeContract.Presenter {

    override fun logout() {
        authRepository.logout()
        view.onLogoutSuccess() // Confirma o logout e chama a navegação
    }
}