package com.example.horu.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.horumvp.model.repository.AuthRepository
import com.example.horumvp.presenter.login.LoginContract
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.horumvp.R

import com.example.horumvp.presenter.login.LoginPresenter

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit) {
    val authRepository = remember { AuthRepository() }
    val loginScreenState = remember { mutableStateOf(LoginScreenState()) }
    val presenter = remember { LoginPresenter(LoginViewImpl(loginScreenState, onLoginSuccess), authRepository) }

    LoginView(
        state = loginScreenState.value,
        onEmailChange = { loginScreenState.value = loginScreenState.value.copy(email = it) },
        onPasswordChange = { loginScreenState.value = loginScreenState.value.copy(password = it) },
        onLoginClick = { presenter.login(loginScreenState.value.email, loginScreenState.value.password) },
        onRegisterClick = onRegisterClick
    )
}

@Composable
fun LoginView(
    state: LoginScreenState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_image),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        if (state.errorMessage != null) {
            Text(text = state.errorMessage, color = Color.Red)
        }

        if (state.loginSuccess) {
            Text(text = "Login realizado com sucesso!", color = Color.Green)
        }

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Entrar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }
    }
}

data class LoginScreenState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val errorMessage: String? = null
)

// a implementaçào do contract é feita aqui.
// composable functions n conseguem implementar.

class LoginViewImpl(private val state: MutableState<LoginScreenState>,
                    private val onLoginSuccess: () -> Unit) : LoginContract.View {

    override fun showLoginSuccess() {
        state.value = state.value.copy(loginSuccess = true, isLoading = false, errorMessage = null)
        onLoginSuccess()
    }

    override fun showLoginError(message: String) {
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
fun PreviewLoginScreen() {
    LoginScreen(
        onLoginSuccess = { /* Handle login success */ },
        onRegisterClick = { /* Handle register click */ }
    )
}
