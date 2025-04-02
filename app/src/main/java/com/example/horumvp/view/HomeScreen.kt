package com.example.horumvp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.horumvp.R
import com.example.horumvp.model.repository.AuthRepository
import com.example.horumvp.model.repository.FirestoreRepository
import com.example.horumvp.model.repository.Property
import com.example.horumvp.presenter.home.HomeContract
import com.example.horumvp.presenter.home.HomePresenter
import androidx.compose.material.icons.filled.Delete

@Composable
fun HomeScreen(navToLogin: () -> Unit, navToRegisterProperty: () -> Unit) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }
    val homeScreenState = remember { mutableStateOf(HomeScreenState()) }
    val propertyRepository = remember { FirestoreRepository(context) }
    val presenter = remember { HomePresenter(HomeViewImpl(homeScreenState, navToLogin), authRepository, propertyRepository, context) }

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

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            PropertyList(
                properties = state.properties,
                onPaymentStatusChange = { propertyId, newStatus ->
                    presenter.updatePaymentStatus(propertyId, newStatus)
                },
                modifier = Modifier.padding(top = 210.dp),
                onDeleteClick = { propertyId ->
                    presenter.deleteProperty(propertyId)
                },
            )
        }

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
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(properties.size) { index ->
            val property = properties[index]
            PropertyCard(
                property = property,
                onPaymentStatusChange = onPaymentStatusChange,
                onDeleteClick = onDeleteClick
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyCard(
    property: Property,
    onPaymentStatusChange: (String, Boolean) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    val emoji = when (property.propertyType) {
        "Casa" -> "🏠"
        "Apartamento" -> "🏙️"
        "Comércio" -> "🏬"
        else -> ""
    }

    var isChecked by remember { mutableStateOf(property.paymentStatus) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessModal by remember { mutableStateOf(false) }
    var showErrorModal by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showDeleteConfirmation by remember { mutableStateOf(false) }


    // Update loading state based on network call
    LaunchedEffect(property.paymentStatus) {
        isChecked = property.paymentStatus
        isLoading = false
    }

    // Show delete confirmation modal
    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteConfirmation = false
                onDeleteClick(property.propertyId)
            },
            onDismiss = {
                showDeleteConfirmation = false
            }
        )
    }

    // Loading Modal
    if (isLoading) {
        BasicAlertDialog(
            onDismissRequest = { /* Cannot be dismissed during loading */ },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Processando...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "${emoji} ${property.name}")
                IconButton(onClick = { showDeleteConfirmation = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Property"
                    )
                }
            }
            Text(text = "Endereço: ${property.address}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Preço: R$ ${property.rentPrice}")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Status de pagamento: ")
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { newCheckedStatus ->
                        // Prevent multiple simultaneous updates
                        if (!isLoading) {
                            // Show loading state
                            isLoading = true

                            // Call the presenter method
                            onPaymentStatusChange(property.propertyId, newCheckedStatus)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Tem certeza que deseja deletar essa propriedade?",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = onConfirm) {
                        Text("Confirmar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                }
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

    override fun onPaymentStatusUpdateCompleted(propertyId: String, success: Boolean, errorMessage: String?) {
        if (success) {
            // Update the local properties list with the new payment status
            state.value = state.value.copy(
                properties = state.value.properties.map { property ->
                    if (property.propertyId == propertyId) {
                        property.copy(paymentStatus = !property.paymentStatus)
                    } else {
                        property
                    }
                }
            )
        } else {
            // Handle error scenario
        }
    }

    override fun removeProperty(propertyId: String) {
        state.value = state.value.copy(
            properties = state.value.properties.filter { it.propertyId != propertyId }
        )
    }

}
