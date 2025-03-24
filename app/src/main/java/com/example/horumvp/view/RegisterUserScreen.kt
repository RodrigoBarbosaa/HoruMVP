package com.example.horumvp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.horumvp.presenter.registerUser.RegisterUserPresenter
import com.example.horumvp.model.repository.AuthRepository
import com.example.horumvp.presenter.registerUser.RegisterUserContract


@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    val registerScreenState = remember { mutableStateOf(RegisterScreenState()) }
    val presenter: RegisterUserPresenter = remember { RegisterUserPresenter(RegisterViewImpl(registerScreenState, onRegisterSuccess), AuthRepository()) }

    val isFormValid = remember(registerScreenState.value) {
        val state = registerScreenState.value
        state.name.isNotEmpty() && state.email.isNotEmpty() && state.password.isNotEmpty() && state.confirmPassword.isNotEmpty() && state.password == state.confirmPassword
    }

    RegisterView(
        state = registerScreenState.value,
        onNameChange = { registerScreenState.value = registerScreenState.value.copy(name = it) },
        onEmailChange = { registerScreenState.value = registerScreenState.value.copy(email = it) },
        onPasswordChange = { registerScreenState.value = registerScreenState.value.copy(password = it) },
        onConfirmPasswordChange = { registerScreenState.value = registerScreenState.value.copy(confirmPassword = it) },
        onRegisterClick = {
            if (isFormValid) {
                presenter.register(registerScreenState.value.name, registerScreenState.value.email, registerScreenState.value.password, registerScreenState.value.confirmPassword)
            } else {
                registerScreenState.value = registerScreenState.value.copy(errorMessage = "Preencha todos os campos corretamente.")
            }
        },
        isFormValid = isFormValid
    )
}

@Composable
fun RegisterView(
    state: RegisterScreenState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    isFormValid: Boolean
) {
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirma Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.errorMessage != null) {
            Text(text = state.errorMessage, color = Color.Red)
        }

        if (state.registerSuccess) {
            Text(text = "Cadastro realizado com sucesso!", color = Color.Green)
        }

        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid && !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Registrar")
            }
        }
    }
}

data class RegisterScreenState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewImpl(
    private val state: MutableState<RegisterScreenState>,
    private val onRegisterSuccess: () -> Unit
) : RegisterUserContract.View {

    override fun showRegisterSuccess() {
        state.value = state.value.copy(registerSuccess = true, isLoading = false, errorMessage = null)
        onRegisterSuccess()
    }

    override fun showRegisterError(message: String) {
        state.value = state.value.copy(errorMessage = message, isLoading = false)
    }

    override fun showLoading() {
        state.value = state.value.copy(isLoading = true)
    }

    override fun hideLoading() {
        state.value = state.value.copy(isLoading = false)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    RegisterScreen(onRegisterSuccess = {})
}