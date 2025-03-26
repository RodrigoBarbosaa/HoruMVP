package com.example.horumvp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController() // Cria o NavController
            NavGraph(navController = navController)
        }
    }
}

// As telas do compose não são classes, então não da pra importar nem fazer o controle de navegação
// pelo intent

// Estamos usando o Jetpack Navigation Controller

// Arquitetura utilizada: MVP

// Divide o código em: Model, View e Presenter

// Model == Gerencia os dados e a lógica de negócios.
// View == Exibe a interface gráfica e interage com o usuário.
// Presenter == Atua como intermediário entre o Model e a View.