package com.example.horumvp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.horu.ui.auth.LoginScreenState
import com.example.horu.ui.auth.LoginView
import com.example.horu.ui.auth.LoginViewImpl
import com.example.horumvp.R
import com.example.horumvp.model.repository.AuthRepository
import com.example.horumvp.model.repository.FirestoreRepository
import com.example.horumvp.model.repository.Property
import com.example.horumvp.presenter.home.HomeContract
import com.example.horumvp.presenter.home.HomePresenter
import com.example.horumvp.presenter.login.LoginPresenter

@Composable
fun HomeScreen(navToLogin: () -> Unit, navToRegisterProperty: () -> Unit) {
    val authRepository = remember { AuthRepository() }
    val homeScreenState = remember { mutableStateOf(HomeScreenState()) }
    val propertyRepository = remember { FirestoreRepository() }
    val presenter = remember { HomePresenter(HomeViewImpl(homeScreenState, navToLogin), authRepository, propertyRepository) }

    // Carregar imóveis
    presenter.loadProperties()

    HomeView(
        state = homeScreenState.value,
        onLogoutClick = navToLogin,
        onRegisterPropertyClick = navToRegisterProperty,
        presenter = presenter
    )
}

@Composable
fun HomeView(
    state: HomeScreenState,
    onLogoutClick: () -> Unit,
    onRegisterPropertyClick: () -> Unit,
    presenter: HomeContract.Presenter
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

        // Exibir imóveis
        PropertyList(properties = state.properties, onPaymentStatusChange = { propertyId, newStatus ->
            presenter.updatePaymentStatus(propertyId, newStatus)
        })
    }
}

@Composable
fun PropertyList(
    properties: List<Property>,
    onPaymentStatusChange: (String, Boolean) -> Unit
) {
    LazyColumn {
        items(properties.size) { index ->
            val property = properties[index]
            PropertyCard(property = property, onPaymentStatusChange = onPaymentStatusChange)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PropertyCard(
    property: Property,
    onPaymentStatusChange: (String, Boolean) -> Unit
) {
    val emoji = when (property.propertyType) {
        "Casa" -> "🏠"
        "Apartamento" -> "🏙️"
        "Comércio" -> "🏬"
        else -> ""
    }

    var isChecked by remember { mutableStateOf(property.paymentStatus) }
    // Atualiza isChecked caso o property.paymentStatus mude
    LaunchedEffect(property.paymentStatus) {
        isChecked = property.paymentStatus
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${emoji} ${property.name} - ${property.address}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Preço: R$ ${property.rentPrice}")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Status de pagamento: ")
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = it
                        //TODO: Não está atualizando o banco de dados
                        onPaymentStatusChange(property.userId, isChecked) // Atualiza o status no banco de dados
                    }
                )
            }
        }
    }
}


data class HomeScreenState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val properties: List<Property> = emptyList()
)

class HomeViewImpl(
    private val state: MutableState<HomeScreenState>,
    private val onLogoutSuccess: () -> Unit
) : HomeContract.View {

    // Função que exibe as propriedades
    override fun displayProperties(properties: List<Property>) {
        state.value = state.value.copy(properties = properties)
    }

    // Função que chama o sucesso do logout
    override fun onLogoutSuccess() {
        state.value = state.value.copy(isLoading = false)
    }
}