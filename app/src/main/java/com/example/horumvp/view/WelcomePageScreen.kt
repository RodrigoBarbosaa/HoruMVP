package com.example.horumvp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.horumvp.model.repository.AuthRepository
import com.example.horumvp.presenter.welcomePage.WelcomePageContract
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.horumvp.R

import com.example.horumvp.presenter.welcomePage.WelcomePagePresenter

@Composable
fun WelcomePageScreen(goToLogin: () -> Unit, goToSignUp: () -> Unit) {
    val view = remember {
      WelcomeViewImpl(
        goToLogin = goToLogin,
        gotToSignUp = goToSignUp
      ) 
    }

    val presenter = remember { WelcomePagePresenter(view) }
}

@Composable
fun WelcomePageView(
    onLogin: () -> Unit,
    onSignUp: () -> Unit
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

        Text(text = "Bem-vindo ao Horu!", modifier = Modifier.padding(bottom = 16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSignUp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }
    }
}

// a implementaçào do contract é feita aqui.
// composable functions n conseguem implementar.

class WelcomeViewImpl(
    private val goToLogin: () -> Unit,
    private val goToSignUp: () -> Unit
): WelcomePageContract.View {
    override fun redirectToLogin() {
        goToLogin()
    }

    override fun redirectToSignUp() {
        goToSignUp()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWelcomeScreen() {
    WelcomeScreen(
        goToLogin = {},
        goToSignUp = {}
    )
}
