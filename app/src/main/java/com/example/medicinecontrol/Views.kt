package com.example.medicinecontrol

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun LoginView(onLogin: () -> Unit, onNavigateToRegister: () -> Unit, onNavigateToRecover: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_app),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("MedicineControl", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(40.dp))

        TextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
                errorMsg = ""
            },
            label = { Text("Email") },
            isError = emailError,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = !isLoading
        )
        if (emailError) {
            Text("Email no válido", color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = {
                password = it
                errorMsg = ""
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                val cleanEmail = email.trim()
                if (cleanEmail.isValidEmail()) {
                    isLoading = true
                    errorMsg = ""
                    scope.launch {
                        UserRepository.login(cleanEmail, password).fold(
                            onSuccess = {
                                val profile = UserRepository.getUserProfile()
                                Repository.usuarioActual = profile
                                Repository.cargarCatalogo()
                                Repository.cargarMedicamentos()
                                isLoading = false
                                onLogin()
                            },
                            onFailure = { e ->
                                isLoading = false
                                errorMsg = when {
                                    e.message?.contains("no user record", ignoreCase = true) == true ||
                                    e.message?.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) == true ->
                                        "Usuario o clave incorrecta"
                                    e.message?.contains("badly formatted", ignoreCase = true) == true ->
                                        "Email no válido"
                                    e.message?.contains("network", ignoreCase = true) == true ->
                                        "Error de conexión. Verifica tu internet"
                                    else -> "Error: ${e.message}"
                                }
                            }
                        )
                    }
                } else {
                    emailError = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Ingresar", fontSize = 20.sp)
            }
        }

        TextButton(onClick = onNavigateToRecover, enabled = !isLoading) {
            Text("¿Olvidaste tu contraseña?", fontSize = 16.sp)
        }
        TextButton(onClick = onNavigateToRegister, enabled = !isLoading) {
            Text("Crear cuenta", fontSize = 16.sp)
        }
    }
}

@Composable
fun RegisterView(onRegistered: () -> Unit, onNavigateToLogin: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var accept by remember { mutableStateOf(false) }
    var largeText by remember { mutableStateOf(false) }
    var recordatorio by remember { mutableStateOf("Visual") }
    var menuOpen by remember { mutableStateOf(false) }

    var nombreError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passError by remember { mutableStateOf(false) }
    var confirmError by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Registro", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = nombre,
            onValueChange = {
                nombre = it
                nombreError = false
            },
            label = { Text("Nombre") },
            isError = nombreError,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        if (nombreError) {
            Text("El nombre es obligatorio", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            label = { Text("Email") },
            isError = emailError,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = !isLoading
        )
        if (emailError) {
            Text("Email no válido u obligatorio", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = pass,
            onValueChange = {
                pass = it
                passError = false
            },
            label = { Text("Contraseña") },
            isError = passError,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        if (passError) {
            Text("La contraseña es obligatoria (mínimo 6 caracteres)", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = confirm,
            onValueChange = {
                confirm = it
                confirmError = false
            },
            label = { Text("Confirmar") },
            isError = confirmError,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        if (confirmError) {
            Text("Debe confirmar la contraseña", color = Color.Red, fontSize = 12.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            Checkbox(checked = accept, onCheckedChange = { accept = it }, enabled = !isLoading)
            Text("Acepto términos", fontSize = 16.sp)
        }

        Text("Accesibilidad:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = !largeText, onClick = { largeText = false }, enabled = !isLoading)
            Text("Normal")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = largeText, onClick = { largeText = true }, enabled = !isLoading)
            Text("Grande")
        }

        Text("Recordatorio:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        Box {
            OutlinedButton(onClick = { menuOpen = true }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) {
                Text(recordatorio)
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                listOf("Visual", "Visual + Sonido").forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { recordatorio = it; menuOpen = false })
                }
            }
        }

        if (errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMsg, color = Color.Red, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                val cleanEmail = email.trim()
                val isEmailValid = cleanEmail.isValidEmail()

                val esValido = validarCampos(
                    (nombre.isNotBlank()) to { nombreError = true },
                    (cleanEmail.isNotBlank() && isEmailValid) to { emailError = true },
                    (pass.isNotBlank() && pass.length >= 6) to { passError = true },
                    (confirm.isNotBlank()) to { confirmError = true },
                    (pass == confirm) to { confirmError = true },
                    (accept) to { }
                )

                if (esValido) {
                    isLoading = true
                    errorMsg = ""
                    scope.launch {
                        UserRepository.register(
                            email = cleanEmail,
                            password = pass,
                            nombre = nombre,
                            accesibilidadGrande = largeText,
                            recordatorioVisual = recordatorio == "Visual"
                        ).fold(
                            onSuccess = {
                                val profile = UserRepository.getUserProfile()
                                Repository.usuarioActual = profile
                                isLoading = false
                                onRegistered()
                            },
                            onFailure = { e ->
                                isLoading = false
                                errorMsg = when {
                                    e.message?.contains("already in use", ignoreCase = true) == true ->
                                        "Este email ya está registrado"
                                    e.message?.contains("weak password", ignoreCase = true) == true ->
                                        "La contraseña es muy débil (mínimo 6 caracteres)"
                                    e.message?.contains("badly formatted", ignoreCase = true) == true ->
                                        "Email no válido"
                                    else -> "Error: ${e.message}"
                                }
                            }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Registrarme", fontSize = 18.sp)
            }
        }

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = !isLoading
        ) {
            Text("Ya tiene una cuenta, inicie sesión", fontSize = 16.sp)
        }
    }
}

@Composable
fun RecoverPasswordView(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Recuperar acceso", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = {
                    email = it
                    isError = false
                    errorMsg = ""
                },
                label = { Text("Email") },
                isError = isError,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !isLoading
            )

            if (isError) {
                Text("Email no válido", color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
            }
            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start).padding(top = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Se enviará un enlace de recuperación a su correo.", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val cleanEmail = email.trim()
                    if (cleanEmail.isValidEmail()) {
                        isLoading = true
                        errorMsg = ""
                        scope.launch {
                            UserRepository.recoverPassword(cleanEmail).fold(
                                onSuccess = {
                                    isLoading = false
                                    snackbarHostState.showSnackbar("Instrucciones enviadas a $cleanEmail")
                                },
                                onFailure = { e ->
                                    isLoading = false
                                    errorMsg = when {
                                        e.message?.contains("no user record", ignoreCase = true) == true ->
                                            "No existe cuenta con este email"
                                        else -> "Error: ${e.message}"
                                    }
                                }
                            )
                        }
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Enviar instrucciones")
                }
            }
            TextButton(onClick = onBack, enabled = !isLoading) {
                Text("Volver", fontSize = 16.sp)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun HomeView(onAddMedication: () -> Unit) {
    val context = LocalContext.current
    val meds by Repository.medicamentosFlow.collectAsState()
    var now by remember { mutableStateOf(LocalTime.now()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var locationPermissionGranted by remember {
        mutableStateOf(LocationHelper.hasLocationPermission(context))
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions.values.any { it }
    }

    LaunchedEffect(Unit) {
        if (!locationPermissionGranted) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        while (true) {
            now = LocalTime.now()
            delay(1000)
        }
    }

    // Ordenar medicamentos por el tiempo restante más corto
    val sortedMeds = meds.ordenarPorProximidad()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Próximos medicamentos", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(20.dp))

        if (sortedMeds.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay medicamentos programados",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            val next = sortedMeds.first()
            val remaining = sortedMeds.drop(1)

            Text("Próximo medicamento:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            MedicationCard(
                med = next,
                now = now,
                isNext = true,
                onMarcarTomado = {
                    scope.launch {
                        val ahora = LocalDateTime.now()
                        val location = if (locationPermissionGranted) {
                            LocationHelper.getCurrentLocation(context)
                        } else null
                        val direccion = location?.let {
                            withContext(Dispatchers.IO) {
                                LocationHelper.getFullAddress(context, it.latitude, it.longitude)
                            }
                        }
                        val registro = RegistroToma(
                            fechaHora = ahora,
                            latitud = location?.latitude,
                            longitud = location?.longitude,
                            direccion = direccion
                        )
                        Repository.modificarMedicamento(next) { medicamento ->
                            val nuevasTomasList = medicamento.historialTomas.toMutableList()
                            nuevasTomasList.add(registro)
                            medicamento.copy(
                                ultimaToma = ahora,
                                historialTomas = nuevasTomasList
                            )
                        }
                        snackbarHostState.showSnackbar("${next.nombre} marcado como tomado")
                    }
                }
            )

            if (remaining.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Siguientes tomas:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                    items(remaining) { med ->
                        MedicationCard(
                            med = med,
                            now = now,
                            isNext = false,
                            onMarcarTomado = {
                                scope.launch {
                                    val ahora = LocalDateTime.now()
                                    val location = if (locationPermissionGranted) {
                                        LocationHelper.getCurrentLocation(context)
                                    } else null
                                    val direccion = location?.let {
                                        withContext(Dispatchers.IO) {
                                            LocationHelper.getFullAddress(context, it.latitude, it.longitude)
                                        }
                                    }
                                    val registro = RegistroToma(
                                        fechaHora = ahora,
                                        latitud = location?.latitude,
                                        longitud = location?.longitude,
                                        direccion = direccion
                                    )
                                    Repository.modificarMedicamento(med) { m ->
                                        val nuevasTomasList = m.historialTomas.toMutableList()
                                        nuevasTomasList.add(registro)
                                        m.copy(
                                            ultimaToma = ahora,
                                            historialTomas = nuevasTomasList
                                        )
                                    }
                                    snackbarHostState.showSnackbar("${med.nombre} marcado como tomado")
                                }
                            }
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }

            Button(
                onClick = onAddMedication,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(64.dp)
            ) {
                Text("Programar medicamento", fontSize = 22.sp)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun MedicationCard(med: Medicamento, now: LocalTime, isNext: Boolean, onMarcarTomado: () -> Unit) {
    val ahora = LocalDateTime.now()
    val atrasado = med.estaAtrasado(ahora)
    val proximaToma = med.calcularProximaToma(ahora)
    val duration = Duration.between(ahora, proximaToma)

    val h = duration.toHours().coerceAtLeast(0)
    val m = duration.toMinutes().coerceAtLeast(0) % 60
    val s = duration.seconds.coerceAtLeast(0) % 60

    val timeStr = when {
        atrasado -> "¡ATRASADO!"
        duration.isZero || (duration.toSeconds() < 5) -> "¡Ahora!"
        else -> "Faltan %02d:%02d:%02d".format(h, m, s)
    }

    val cardColor = when {
        atrasado -> CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
        isNext -> CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
        else -> CardDefaults.elevatedCardColors()
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = cardColor
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(med.nombre, fontSize = if (isNext) 28.sp else 22.sp, fontWeight = FontWeight.Bold)
            Text("Dosis: ${med.dosis}", fontSize = 18.sp)
            Text(
                "Próxima toma: ${proximaToma.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                fontSize = 18.sp
            )

            if (atrasado) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Estado: ATRASADO",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = timeStr,
                fontSize = if (isNext) 26.sp else 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = when {
                    atrasado -> MaterialTheme.colorScheme.error
                    isNext -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.secondary
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onMarcarTomado,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (atrasado) MaterialTheme.colorScheme.error
                                   else MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Marcar como tomado", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun calculateDurationUntilNext(med: Medicamento, now: LocalTime): Duration {
    val ahora = LocalDateTime.now()
    val proximaToma = calcularProximaToma(med, ahora)

    return Duration.between(ahora, proximaToma)
}

fun calcularProximaToma(med: Medicamento, ahora: LocalDateTime): LocalDateTime {
    // Si nunca se ha tomado, usar la hora inicial de hoy
    if (med.ultimaToma == null) {
        val hoy = ahora.toLocalDate()
        var proximaToma = LocalDateTime.of(hoy, med.horaInicial)

        // Si ya pasó, calcular la siguiente
        while (proximaToma.isBefore(ahora)) {
            proximaToma = proximaToma.plusHours(med.intervaloHoras.toLong())
        }
        return proximaToma
    }

    // Si ya se tomó, calcular desde la última toma
    var proximaToma = med.ultimaToma!!.plusHours(med.intervaloHoras.toLong())

    // Si ya pasó el tiempo, seguir sumando intervalos
    while (proximaToma.isBefore(ahora)) {
        proximaToma = proximaToma.plusHours(med.intervaloHoras.toLong())
    }

    return proximaToma
}

fun estaAtrasado(med: Medicamento, ahora: LocalDateTime): Boolean {
    val proximaToma = calcularProximaToma(med, ahora)
    val margenMinutos = 15 // 15 minutos de margen

    // Está atrasado si la próxima toma ya pasó hace más del margen
    return ahora.isAfter(proximaToma.plusMinutes(margenMinutos.toLong()))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationFormView(onSaved: () -> Unit, onBack: () -> Unit = {}) {
    var nombre by remember { mutableStateOf("") }
    var cantidadDosis by remember { mutableStateOf("") }
    var formatoSeleccionado by remember { mutableStateOf("Pastillas") }
    var menuFormatoOpen by remember { mutableStateOf(false) }
    var intervaloSelected by remember { mutableIntStateOf(8) }
    var selectedTime by remember { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }
    var showTimePicker by remember { mutableStateOf(false) }
    var diario by remember { mutableStateOf(true) }
    var showCatalogMenu by remember { mutableStateOf(false) }

    var nombreError by remember { mutableStateOf(false) }
    var dosisError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val catalogo by Repository.catalogoFlow.collectAsState()
    val medicamentos by Repository.medicamentosFlow.collectAsState()

    val formatos = mapOf(
        "Pastillas" to "pastilla(s)",
        "Comprimidos" to "mg",
        "Jarabe" to "ml",
        "Gotas" to "gotas",
        "Cápsulas" to "cápsula(s)",
        "Inyección" to "ml",
        "Sobres" to "sobre(s)"
    )

    val timeState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute
    )

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(timeState.hour, timeState.minute)
                    showTimePicker = false
                }) { Text("Confirmar") }
            }
        ) {
            TimePicker(state = timeState)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Programar Medicamento", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        if (catalogo.isNotEmpty()) {
            Box {
                OutlinedButton(
                    onClick = { showCatalogMenu = true },
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    Text("Seleccionar de mis medicamentos", fontSize = 18.sp)
                }
                DropdownMenu(
                    expanded = showCatalogMenu,
                    onDismissRequest = { showCatalogMenu = false }
                ) {
                    catalogo.forEach { med ->
                        DropdownMenuItem(
                            text = { Text("${med.nombre} - ${med.dosis}", fontSize = 16.sp) },
                            onClick = {
                                nombre = med.nombre
                                // Intentar extraer cantidad y formato de la dosis
                                val partes = med.dosis.split(" ", limit = 2)
                                if (partes.size == 2) {
                                    cantidadDosis = partes[0]
                                    // Buscar formato que coincida
                                    formatos.entries.find { it.value == partes[1] }?.let {
                                        formatoSeleccionado = it.key
                                    }
                                }
                                showCatalogMenu = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("O escribir manualmente:", fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = false
                },
                label = { Text("Nombre", fontSize = 18.sp) },
                isError = nombreError,
                modifier = Modifier.weight(1f).height(70.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
                enabled = !isLoading
            )
            SpeechToTextButton { text ->
                nombre = text
                nombreError = false
            }
        }
        if (nombreError) {
            Text("El nombre es obligatorio", color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box {
            OutlinedButton(
                onClick = { menuFormatoOpen = true },
                modifier = Modifier.fillMaxWidth().height(70.dp),
                enabled = !isLoading
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Seleccione formato", fontSize = 14.sp, color = Color.Gray)
                        Text(formatoSeleccionado, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("▼", fontSize = 20.sp)
                }
            }
            DropdownMenu(
                expanded = menuFormatoOpen,
                onDismissRequest = { menuFormatoOpen = false }
            ) {
                formatos.keys.forEach { formato ->
                    DropdownMenuItem(
                        text = { Text(formato, fontSize = 18.sp) },
                        onClick = {
                            formatoSeleccionado = formato
                            menuFormatoOpen = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = cantidadDosis,
            onValueChange = {
                cantidadDosis = it
                dosisError = false
            },
            label = { Text("Cantidad", fontSize = 18.sp) },
            isError = dosisError,
            modifier = Modifier.fillMaxWidth().height(70.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            suffix = { Text(formatos[formatoSeleccionado] ?: "", fontSize = 16.sp, color = Color.Gray) },
            enabled = !isLoading
        )
        if (dosisError) {
            Text("La cantidad es obligatoria", color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Intervalo de tomas (horas):", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(4, 6, 8, 12, 24).forEach { hours ->
                FilterChip(
                    selected = intervaloSelected == hours,
                    onClick = { intervaloSelected = hours },
                    label = { Text("${hours}h", fontSize = 16.sp, fontWeight = FontWeight.Medium) },
                    modifier = Modifier.height(48.dp),
                    enabled = !isLoading
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Hora de la primera toma:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { showTimePicker = true },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            enabled = !isLoading
        ) {
            Text(selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Checkbox(checked = diario, onCheckedChange = { diario = it }, enabled = !isLoading)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Repetir diariamente", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                val esValido = validarCampos(
                    (nombre.isNotBlank()) to { nombreError = true },
                    (cantidadDosis.isNotBlank()) to { dosisError = true }
                )

                if (esValido) {
                    isLoading = true
                    scope.launch {
                        val dosisCompleta = "$cantidadDosis ${formatos[formatoSeleccionado]}"
                        Repository.agregarMedicamento(Medicamento(nombre, dosisCompleta, intervaloSelected, selectedTime, diario))
                        isLoading = false
                        onSaved()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Guardar Medicamento", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            enabled = !isLoading
        ) {
            Text("Cancelar", fontSize = 18.sp)
        }

        if (medicamentos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Medicamentos ingresados:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            medicamentos.forEach {
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("• ${it.nombre} (${it.dosis}) cada ${it.intervaloHoras}h", fontSize = 16.sp, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

@Composable
fun MyMedicinesView(onNavigateToHome: () -> Unit = {}) {
    var showDialog by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var cantidadDosis by remember { mutableStateOf("") }
    var formatoSeleccionado by remember { mutableStateOf("Pastillas") }
    var menuFormatoOpen by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val catalogo by Repository.catalogoFlow.collectAsState()

    val formatos = mapOf(
        "Pastillas" to "pastilla(s)",
        "Comprimidos" to "mg",
        "Jarabe" to "ml",
        "Gotas" to "gotas",
        "Cápsulas" to "cápsula(s)",
        "Inyección" to "ml",
        "Sobres" to "sobre(s)"
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nuevo Medicamento", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre", fontSize = 16.sp) },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                        )
                        SpeechToTextButton { text -> nombre = text }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Box {
                        OutlinedButton(
                            onClick = { menuFormatoOpen = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text("Seleccione formato", fontSize = 12.sp, color = Color.Gray)
                                    Text(formatoSeleccionado, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                                Text("▼", fontSize = 16.sp)
                            }
                        }
                        DropdownMenu(
                            expanded = menuFormatoOpen,
                            onDismissRequest = { menuFormatoOpen = false }
                        ) {
                            formatos.keys.forEach { formato ->
                                DropdownMenuItem(
                                    text = { Text(formato, fontSize = 16.sp) },
                                    onClick = {
                                        formatoSeleccionado = formato
                                        menuFormatoOpen = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = cantidadDosis,
                        onValueChange = { cantidadDosis = it },
                        label = { Text("Cantidad", fontSize = 16.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        suffix = { Text(formatos[formatoSeleccionado] ?: "", fontSize = 14.sp, color = Color.Gray) }
                    )

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nombre.isNotBlank() && cantidadDosis.isNotBlank()) {
                            isLoading = true
                            scope.launch {
                                val dosisCompleta = "$cantidadDosis ${formatos[formatoSeleccionado]}"
                                Repository.agregarAlCatalogo(MedicamentoCatalogo(nombre, dosisCompleta))
                                nombre = ""
                                cantidadDosis = ""
                                formatoSeleccionado = "Pastillas"
                                isLoading = false
                                showDialog = false
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text("Guardar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        nombre = ""
                        cantidadDosis = ""
                        formatoSeleccionado = "Pastillas"
                    },
                    enabled = !isLoading
                ) {
                    Text("Cancelar", fontSize = 16.sp)
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mis Medicamentos", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(20.dp))

        if (catalogo.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay medicamentos guardados",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(catalogo) { med ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(med.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Dosis: ${med.dosis}", fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth().height(64.dp)
        ) {
            Text("Agregar medicamento", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onNavigateToHome,
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Volver al inicio", fontSize = 18.sp)
        }
    }
}

@Composable
fun HistorialView() {
    val medicamentos by Repository.medicamentosFlow.collectAsState()
    val allTomas = medicamentos.flatMap { med ->
        med.historialTomas.map { registro ->
            Triple(med.nombre, med.dosis, registro)
        }
    }.sortedByDescending { it.third.fechaHora }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Historial de Tomas", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(20.dp))

        if (allTomas.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay tomas registradas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allTomas) { (nombre, dosis, registro) ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text("Dosis: $dosis", fontSize = 16.sp)
                                Text(
                                    registro.fechaHora.formatFriendly(),
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                if (registro.direccion != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(registro.direccion, fontSize = 12.sp, color = Color.Gray)
                                    }
                                } else if (registro.latitud != null && registro.longitud != null) {
                                    Text(
                                        "%.4f, %.4f".format(registro.latitud, registro.longitud),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_agenda),
                                contentDescription = "Tomado",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyAccountView(onLogout: () -> Unit) {
    val context = LocalContext.current
    val usuario = Repository.usuarioActual
    var cityName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (LocationHelper.hasLocationPermission(context)) {
            val location = LocationHelper.getCurrentLocation(context)
            if (location != null) {
                cityName = withContext(Dispatchers.IO) {
                    LocationHelper.getCityName(context, location.latitude, location.longitude)
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mi Cuenta", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(40.dp))

        if (usuario != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Nombre", fontSize = 14.sp, color = Color.Gray)
                    Text(usuario.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Email", fontSize = 14.sp, color = Color.Gray)
                    Text(usuario.email, fontSize = 18.sp)

                    if (cityName != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Ubicación", fontSize = 14.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(cityName!!, fontSize = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Accesibilidad", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        if (usuario.accesibilidadGrande) "Texto grande" else "Texto normal",
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Recordatorio", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        if (usuario.recordatorioVisual) "Visual" else "Visual + Sonido",
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    Repository.cerrarSesion()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cerrar sesión", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Text("No hay usuario logueado", fontSize = 18.sp, color = Color.Gray)
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = { content() }
    )
}
