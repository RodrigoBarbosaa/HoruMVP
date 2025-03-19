package com.example.horumvp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.horumvp.presenter.registerProperty.RegisterPropertyPresenter
import com.example.horumvp.presenter.registerProperty.RegisterPropertyContract
import com.example.horumvp.model.repository.FirestoreRepository

@Composable
fun RegisterPropertyScreen(
    onRegisterPropertySuccess: () -> Unit,
    onErrorMessage: (String) -> Unit
) {
    val firestoreRepository = remember { FirestoreRepository() }
    val RegisterPropertyScreenState = remember { mutableStateOf(RegisterPropertyScreenState()) }
    val presenter = remember { RegisterPropertyPresenter(RegisterPropertyViewImpl(RegisterPropertyScreenState, onRegisterPropertySuccess), firestoreRepository) }

    RegisterPropertyView(
        state = RegisterPropertyScreenState.value,
        onRegisterPropertyClick = {name, address, rentPrice ->
            presenter.registerProperty(name, address, rentPrice)
        },
        onErrorMessage = onErrorMessage
    )
}

@Composable
fun RegisterPropertyView(
    state: RegisterPropertyScreenState,
    onRegisterPropertyClick: (String, String, String) -> Unit,
    onErrorMessage: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var rentPrice by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Cadastrar Novo Imóvel", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome do Imóvel") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Endereço") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = rentPrice,
            onValueChange = { rentPrice = it },
            label = { Text("Preço do Aluguel") },
            modifier = Modifier.fillMaxWidth()
        )

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        if (!state.errorMessage.isNullOrEmpty()) {
            Text(text = state.errorMessage ?: "", color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Button(
            onClick = {
                onRegisterPropertyClick(name, address, rentPrice)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text("Cadastrar Imóvel")
        }
    }
}

data class RegisterPropertyScreenState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RegisterPropertyViewImpl(
    private val state: MutableState<RegisterPropertyScreenState>,
    private val onRegisterSuccess: () -> Unit
) : RegisterPropertyContract.View {

    override fun showSuccessMessage() {
        state.value = state.value.copy(isLoading = false)
        onRegisterSuccess()
    }

    override fun showErrorMessage(message: String) {
        state.value = state.value.copy(errorMessage = message, isLoading = false)
    }

    override fun showLoading() {
        state.value = state.value.copy(isLoading = true)
    }

    override fun hideLoading() {
        state.value = state.value.copy(isLoading = false)
    }
}


