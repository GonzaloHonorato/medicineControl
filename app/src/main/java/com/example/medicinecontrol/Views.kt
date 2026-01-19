package com.example.medicinecontrol

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

@Composable
fun LoginView(onLogin: () -> Unit, onNavigateToRegister: () -> Unit, onNavigateToRecover: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }

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
            }, 
            label = { Text("Email") }, 
            isError = emailError,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        if (emailError) {
            Text("Email no válido", color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password, 
            onValueChange = { password = it }, 
            label = { Text("Contraseña") }, 
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        
        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                val cleanEmail = email.trim()
                if (cleanEmail.matches(emailRegex)) {
                    val user = Repository.usuarios.find { it.email == cleanEmail && it.contrasena == password }
                    if (user != null) onLogin() else errorMsg = "Usuario o clave incorrecta"
                } else {
                    emailError = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Ingresar", fontSize = 20.sp)
        }

        TextButton(onClick = onNavigateToRecover) {
            Text("¿Olvidaste tu contraseña?", fontSize = 16.sp)
        }
        TextButton(onClick = onNavigateToRegister) {
            Text("Crear cuenta", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                val demoUser = Repository.usuarios.firstOrNull()
                if (demoUser != null) {
                    email = demoUser.email
                    password = demoUser.contrasena
                    emailError = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Usar datos demo")
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
            modifier = Modifier.fillMaxWidth()
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
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
            modifier = Modifier.fillMaxWidth()
        )
        if (passError) {
            Text("La contraseña es obligatoria", color = Color.Red, fontSize = 12.sp)
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
            modifier = Modifier.fillMaxWidth()
        )
        if (confirmError) {
            Text("Debe confirmar la contraseña", color = Color.Red, fontSize = 12.sp)
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            Checkbox(checked = accept, onCheckedChange = { accept = it })
            Text("Acepto términos", fontSize = 16.sp)
        }

        Text("Accesibilidad:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = !largeText, onClick = { largeText = false })
            Text("Normal")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = largeText, onClick = { largeText = true })
            Text("Grande")
        }

        Text("Recordatorio:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        Box {
            OutlinedButton(onClick = { menuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                Text(recordatorio)
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                listOf("Visual", "Visual + Sonido").forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { recordatorio = it; menuOpen = false })
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                val cleanEmail = email.trim()
                val isEmailValid = cleanEmail.matches(emailRegex)
                val isValid = nombre.isNotBlank() && cleanEmail.isNotBlank() && isEmailValid && 
                              pass.isNotBlank() && confirm.isNotBlank() && pass == confirm && accept

                if (isValid) {
                    if (Repository.agregarUsuario(User(nombre, cleanEmail, pass, largeText, recordatorio == "Visual"))) {
                        onRegistered()
                    }
                } else {
                    if (nombre.isBlank()) nombreError = true
                    if (cleanEmail.isBlank() || !isEmailValid) emailError = true
                    if (pass.isBlank()) passError = true
                    if (confirm.isBlank()) confirmError = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Registrarme", fontSize = 18.sp)
        }

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
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
                }, 
                label = { Text("Email") },
                isError = isError,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            if (isError) {
                Text("Email no válido", color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Se enviará un enlace de recuperación a su correo.", fontSize = 16.sp)
            
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val cleanEmail = email.trim()
                    if (cleanEmail.matches(emailRegex)) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Instrucciones enviadas a $cleanEmail")
                        }
                    } else {
                        isError = true
                    }
                }, 
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Enviar instrucciones")
            }
            TextButton(onClick = onBack) {
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
    val meds = Repository.medicamentos
    var now by remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            delay(1000)
        }
    }

    // Ordenar medicamentos por el tiempo restante más corto
    val sortedMeds = meds.sortedBy { med ->
        calculateDurationUntilNext(med, now).toSeconds()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Próximos medicamentos", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(20.dp))
        
        if (sortedMeds.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("No hay registros", fontSize = 18.sp, color = Color.Gray)
            }
        } else {
            val next = sortedMeds.first()
            val remaining = sortedMeds.drop(1)

            Text("Próximo medicamento:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            MedicationCard(next, now, true)
            
            if (remaining.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Siguientes tomas:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                    items(remaining) { med ->
                        MedicationCard(med, now, false)
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
            Text("Agregar medicamento", fontSize = 22.sp)
        }
    }
}

@Composable
fun MedicationCard(med: Medicamento, now: LocalTime, isNext: Boolean) {
    val duration = calculateDurationUntilNext(med, now)
    
    val h = duration.toHours()
    val m = duration.toMinutes() % 60
    val s = duration.seconds % 60
    val timeStr = if (duration.isZero || (duration.toSeconds() < 5)) "¡Ahora!" 
                 else "Faltan %02d:%02d:%02d".format(h, m, s)

    val nextDoseTime = now.plus(duration)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isNext) CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) else CardDefaults.elevatedCardColors()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(med.nombre, fontSize = if (isNext) 28.sp else 22.sp, fontWeight = FontWeight.Bold)
            Text("Dosis: ${med.dosis}", fontSize = 18.sp)
            Text("Próxima toma: ${nextDoseTime.format(DateTimeFormatter.ofPattern("HH:mm"))}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = timeStr,
                fontSize = if (isNext) 26.sp else 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        }
    }
}

fun calculateDurationUntilNext(med: Medicamento, now: LocalTime): Duration {
    val nowSec = now.toSecondOfDay()
    val startSec = med.horaInicial.toSecondOfDay()
    val intervalSec = med.intervaloHoras * 3600
    
    var diff = (startSec - nowSec).toLong()
    if (intervalSec > 0) {
        while (diff < 0) {
            diff += intervalSec
        }
    } else {
        if (diff < 0) diff += 86400
    }
    return Duration.ofSeconds(diff)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationFormView(onSaved: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var intervaloSelected by remember { mutableIntStateOf(8) }
    var selectedTime by remember { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }
    var showTimePicker by remember { mutableStateOf(false) }
    var diario by remember { mutableStateOf(true) }
    
    var nombreError by remember { mutableStateOf(false) }
    var dosisError by remember { mutableStateOf(false) }

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
        Text("Nuevo Medicamento", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        
        TextField(
            value = nombre, 
            onValueChange = { 
                nombre = it 
                nombreError = false
            }, 
            label = { Text("Nombre") }, 
            isError = nombreError,
            modifier = Modifier.fillMaxWidth()
        )
        if (nombreError) {
            Text("El nombre es obligatorio", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = dosis, 
            onValueChange = { 
                dosis = it 
                dosisError = false
            }, 
            label = { Text("Dosis") }, 
            isError = dosisError,
            modifier = Modifier.fillMaxWidth()
        )
        if (dosisError) {
            Text("La dosis es obligatoria", color = Color.Red, fontSize = 12.sp)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        Text("Intervalo de tomas (horas):", fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(4, 6, 8, 12, 24).forEach { hours ->
                FilterChip(
                    selected = intervaloSelected == hours,
                    onClick = { intervaloSelected = hours },
                    label = { Text("${hours}h") }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Hora de la primera toma:", fontWeight = FontWeight.Bold)
        OutlinedButton(
            onClick = { showTimePicker = true },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text(selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")), fontSize = 18.sp)
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
            Checkbox(checked = diario, onCheckedChange = { diario = it })
            Text("Repetir diariamente", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (nombre.isNotBlank() && dosis.isNotBlank()) {
                    Repository.agregarMedicamento(Medicamento(nombre, dosis, intervaloSelected, selectedTime, diario))
                    onSaved()
                } else {
                    if (nombre.isBlank()) nombreError = true
                    if (dosis.isBlank()) dosisError = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Guardar Medicamento", fontSize = 20.sp)
        }
        
        if (Repository.medicamentos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Medicamentos ingresados:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Repository.medicamentos.forEach {
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("• ${it.nombre} (${it.dosis}) cada ${it.intervaloHoras}h", fontSize = 16.sp, modifier = Modifier.padding(12.dp))
                }
            }
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
