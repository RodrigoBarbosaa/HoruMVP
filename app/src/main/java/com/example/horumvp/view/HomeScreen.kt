package com.example.horumvp.view.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.horumvp.model.repository.AuthRepository
import com.example.horumvp.presenter.feed.HomeContract
import com.example.horumvp.presenter.feed.HomePresenter

@Composable
fun HomeScreen(navToLogin: () -> Unit) {
    val authRepository = remember { AuthRepository() }
    val presenter = remember { HomePresenter(object : HomeContract.View {
        override fun onLogoutSuccess() {
            navToLogin() // Navegação acontece apenas quando o Presenter confirma o logout
        }
    }, authRepository) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bem-vindo à Home!", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { presenter.logout() }) { //  botão aciona o Presenter
            Text(text = "Sair")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(navToLogin = {})
}
