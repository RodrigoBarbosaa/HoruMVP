package com.example.horumvp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RegisterPropertyScreen(
    onRegisterPropertySuccess: () -> Unit,
    onErrorMessage: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val firestoreRepository = remember { FirestoreRepository(context) }
    val registerScreenState = remember { mutableStateOf(RegisterPropertyScreenState()) }
    val presenter = remember { RegisterPropertyPresenter(RegisterPropertyViewImpl(registerScreenState, onRegisterPropertySuccess), firestoreRepository) }

    // Lista de tipos de imóvel
    val propertyTypes = listOf("Casa", "Apartamento", "Comércio")
    var selectedPropertyType by remember { mutableStateOf(propertyTypes[0]) } // Valor inicial

    // Estado para a data do lembrete
    var hasReminder by remember { mutableStateOf(false) }
    var reminderDate by remember { mutableStateOf<Date?>(null) }

    RegisterPropertyView(
        state = registerScreenState.value,
        onRegisterPropertyClick = { name, address, rentPriceString, propertyType, reminderDate ->
            if (rentPriceString.isNotEmpty()) {
                presenter.registerProperty(name, address, rentPriceString, propertyType, reminderDate)
            } else {
                onErrorMessage("Preço de aluguel não pode ser vazio.")
            }
        },
        onErrorMessage = onErrorMessage,
        propertyTypes = propertyTypes,
        selectedPropertyType = selectedPropertyType,
        onPropertyTypeSelected = { selectedPropertyType = it },
        hasReminder = hasReminder,
        onHasReminderChanged = { hasReminder = it },
        reminderDate = reminderDate,
        onReminderDateChanged = { reminderDate = it },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPropertyView(
    state: RegisterPropertyScreenState,
    onRegisterPropertyClick: (String, String, String, String, Date?) -> Unit,  // Atualizado para incluir Date?
    onErrorMessage: (String) -> Unit,
    propertyTypes: List<String>,
    selectedPropertyType: String,
    onPropertyTypeSelected: (String) -> Unit,
    hasReminder: Boolean,
    onHasReminderChanged: (Boolean) -> Unit,
    reminderDate: Date?,
    onReminderDateChanged: (Date?) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var rentPrice by remember { mutableStateOf("") }

    // Para exibir a data selecionada no formato brasileiro
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    // Para o DatePicker
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Cadastrar Novo Imóvel", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome do Imóvel") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Endereço") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = rentPrice,
            onValueChange = { newValue ->
                // Filtra a entrada para aceitar apenas números e uma vírgula ou ponto para valores decimais
                if (newValue.all { it.isDigit() || it == '.' || it == ',' }) {
                    rentPrice = newValue
                }
            },
            label = { Text("Valor do Aluguel") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number // Isso mostra o teclado numérico
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de seleção para o tipo de imóvel (Radio Buttons)
        Text(text = "Tipo de Imóvel", style = MaterialTheme.typography.bodyMedium)

        // Radio buttons para selecionar o tipo de imóvel
        propertyTypes.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = selectedPropertyType == type,
                    onClick = { onPropertyTypeSelected(type) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = type, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Seção de lembrete
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = hasReminder,
                onCheckedChange = { onHasReminderChanged(it) }
            )
            Text("Agendar lembrete", style = MaterialTheme.typography.bodySmall)
        }

        // Exibir o seletor de data apenas se hasReminder for true
        if (hasReminder) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    text = reminderDate?.let { dateFormat.format(it) } ?: "Selecione uma data",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { showDatePicker = true }) {
                    Text("Escolher Data")
                }
            }
        }

        // DatePicker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = millis
                            onReminderDateChanged(calendar.time)
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        if (!state.errorMessage.isNullOrEmpty()) {
            Text(text = state.errorMessage ?: "", color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Button(
            onClick = {
                if (name.isNotEmpty() && address.isNotEmpty() && rentPrice.isNotEmpty()) {
                    val finalReminderDate = if (hasReminder) reminderDate else null
                    onRegisterPropertyClick(name, address, rentPrice, selectedPropertyType, finalReminderDate)
                } else {
                    onErrorMessage("Todos os campos são obrigatórios.")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text("Cadastrar Imóvel")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
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

    override fun showSuccessMessage(message: String) {
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