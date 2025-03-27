package com.example.horumvp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.horumvp.R
import com.example.horumvp.model.repository.AuthRepository
import com.example.horumvp.model.repository.FirestoreRepository
import com.example.horumvp.model.repository.Property
import com.example.horumvp.presenter.home.HomeContract
import com.example.horumvp.presenter.home.HomePresenter

@Composable
fun HomeScreen(navToLogin: () -> Unit, navToRegisterProperty: () -> Unit) {
    val authRepository = remember { AuthRepository() }
    val homeScreenState = remember { mutableStateOf(HomeScreenState()) }
    val propertyRepository = remember { FirestoreRepository() }
    val presenter = remember { HomePresenter(HomeViewImpl(homeScreenState, navToLogin), authRepository, propertyRepository) }

    // Carregar imóveis iniciais
    LaunchedEffect(true) {
        presenter.loadProperties()
    }

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo_image),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
        )

        Text(
            text = "Seus imóveis",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 120.dp, start = 16.dp)
        )

        if (state.isLoadMoreVisible) {
            Button(
                onClick = { presenter.loadMoreProperties() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 160.dp, start = 16.dp)
                    .heightIn(min = 40.dp)
            ) {
                Text("Carregar mais")
            }
        }

        // Exibir imóveis
        PropertyList(
            properties = state.properties,
            onPaymentStatusChange = { propertyId, newStatus ->
                presenter.updatePaymentStatus(propertyId, newStatus)
            },
            modifier = Modifier.padding(top = 210.dp)
        )

        // Botão de logout com ícone
        IconButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Logout"
            )
        }

        // Botão de cadastrar imóvel "+"
        FloatingActionButton(
            onClick = onRegisterPropertyClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Cadastrar Imóvel")
        }
    }
}



@Composable
fun PropertyList(
    properties: List<Property>,
    onPaymentStatusChange: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn (modifier = modifier) {
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
    val properties: List<Property> = emptyList(),
    val isLoadMoreVisible: Boolean = false
)

class HomeViewImpl(
    private val state: MutableState<HomeScreenState>,
    private val onLogoutSuccess: () -> Unit
) : HomeContract.View {

    override fun displayProperties(properties: List<Property>) {
        state.value = state.value.copy(properties = properties)
    }

    override fun addProperties(properties: List<Property>) {
        state.value = state.value.copy(properties = state.value.properties + properties)
    }

    override fun showLoginError(message: String) {
        state.value = state.value.copy(errorMessage = message)
    }

    override fun showLoading() {
        state.value = state.value.copy(isLoading = true)
    }

    override fun hideLoading() {
        state.value = state.value.copy(isLoading = false)
    }

    override fun toggleLoadMoreButton(show: Boolean) {
        state.value = state.value.copy(isLoadMoreVisible = show)
    }
    override fun onLogoutSuccess() {
        state.value = state.value.copy(isLoading = false)
    }
}
