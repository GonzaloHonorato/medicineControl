package com.example.medicinecontrol

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Pantalla de acceso
@Composable
fun LoginView(onLogin: () -> Unit, onNavigateToRegister: () -> Unit, onNavigateToRecover: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("MedicineControl", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))
        
        TextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        
        TextField(
            value = password, 
            onValueChange = { password = it }, 
            label = { Text("Contraseña") }, 
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        
        if (error.isNotEmpty()) {
            Text(error, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                val user = Repository.usuarios.find { it.email == email && it.contrasena == password }
                if (user != null) onLogin() else error = "Email o contraseña incorrectos"
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
    }
}

// Pantalla de nuevo usuario
@Composable
fun RegisterView(onRegistered: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }
    var isLargeText by remember { mutableStateOf(false) }
    var reminderType by remember { mutableStateOf("Visual") }
    var expanded by remember { mutableStateOf(false) }

    val options = listOf("Visual", "Visual + Sonido")

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Crear Cuenta", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = pass, onValueChange = { pass = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = confirmPass, onValueChange = { confirmPass = it }, label = { Text("Confirmar contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            Checkbox(checked = acceptTerms, onCheckedChange = { acceptTerms = it })
            Text("Acepto los términos y condiciones", fontSize = 16.sp)
        }

        Text("Preferencia de accesibilidad:", fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = !isLargeText, onClick = { isLargeText = false })
            Text("Normal")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = isLargeText, onClick = { isLargeText = true })
            Text("Texto grande")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Tipo de recordatorio:", fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(reminderType)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { opt ->
                    DropdownMenuItem(text = { Text(opt) }, onClick = { reminderType = opt; expanded = false })
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                if (pass == confirmPass && pass.isNotEmpty() && acceptTerms) {
                    val success = Repository.agregarUsuario(User(nombre, email, pass, isLargeText, reminderType.contains("Visual")))
                    if (success) onRegistered()
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Registrarme", fontSize = 18.sp)
        }
    }
}

// Pantalla para recuperar acceso
@Composable
fun RecoverPasswordView(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Recuperar Contraseña", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Text("Se enviará un enlace con instrucciones para restablecer su contraseña.", fontSize = 16.sp)
        
        if (mensaje.isNotEmpty()) {
            Text(mensaje, color = Color(0xFF388E3C), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { mensaje = "Enlace enviado a $email" }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Enviar instrucciones")
        }
        
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Volver al inicio", fontSize = 16.sp)
        }
    }
}

// Pantalla principal de seguimiento
@Composable
fun HomeView(onAddMedication: () -> Unit) {
    val meds = Repository.medicamentos
    var now by remember { mutableStateOf(LocalTime.now()) }

    // Actualiza el tiempo cada segundo
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            delay(1000)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Próximos medicamentos", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        
        if (meds.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("No hay medicamentos programados", fontSize = 18.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(meds) { med ->
                    MedicationCard(med, now)
                }
            }
        }

        Button(
            onClick = onAddMedication,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(64.dp)
        ) {
            Text("Agregar medicamento", fontSize = 20.sp)
        }
    }
}

@Composable
fun MedicationCard(med: Medicamento, now: LocalTime) {
    val duration = Duration.between(now, med.horaInicial)
    val diffText = if (duration.isNegative) "Hora de la toma" else {
        val h = duration.toHours()
        val m = duration.toMinutes() % 60
        val s = duration.seconds % 60
        "Faltan %02d:%02d:%02d".format(h, m, s)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(med.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Dosis: ${med.dosis}", fontSize = 18.sp)
            Text("Programado: ${med.horaInicial.format(DateTimeFormatter.ofPattern("HH:mm"))}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(diffText, fontSize = 20.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        }
    }
}

// Pantalla para añadir medicamento
@Composable
fun MedicationFormView(onSaved: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var intervalo by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var repetir by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Registrar Medicamento", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        
        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del medicamento") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = dosis, onValueChange = { dosis = it }, label = { Text("Cantidad / Dosis") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = intervalo, 
            onValueChange = { if (it.all { c -> c.isDigit() }) intervalo = it }, 
            label = { Text("Cada cuántas horas") }, 
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = hora, onValueChange = { hora = it }, label = { Text("Hora inicio (Ej: 08:00)") }, modifier = Modifier.fillMaxWidth())
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
            Checkbox(checked = repetir, onCheckedChange = { repetir = it })
            Text("Repetir diariamente", fontSize = 18.sp)
        }

        Button(
            onClick = {
                try {
                    val t = LocalTime.parse(hora)
                    Repository.agregarMedicamento(Medicamento(nombre, dosis, intervalo.toIntOrNull() ?: 0, t, repetir))
                    onSaved()
                } catch (e: Exception) {
                    // Manejo simple de error en formato
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Guardar Medicamento", fontSize = 18.sp)
        }
        
        if (Repository.medicamentos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Medicamentos ingresados:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Repository.medicamentos.forEach {
                Text("• ${it.nombre} - ${it.dosis}", fontSize = 16.sp, modifier = Modifier.padding(vertical = 2.dp))
            }
        }
    }
}
