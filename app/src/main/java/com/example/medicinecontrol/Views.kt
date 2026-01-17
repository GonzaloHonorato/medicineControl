package com.example.medicinecontrol

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
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
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Pantalla de Login
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
        Text("MedicineControl", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))
        
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
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

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                val user = Repository.usuarios.find { it.email == email && it.contrasena == password }
                if (user != null) onLogin() else error = "Credenciales incorrectas"
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Ingresar", fontSize = 18.sp)
        }

        TextButton(onClick = onNavigateToRecover) {
            Text("¿Olvidaste tu contraseña?", fontSize = 16.sp)
        }
        
        TextButton(onClick = onNavigateToRegister) {
            Text("Crear cuenta", fontSize = 16.sp)
        }
    }
}

// Pantalla de Registro
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

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())) {
        Text("Registro", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = pass, onValueChange = { pass = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = confirmPass, onValueChange = { confirmPass = it }, label = { Text("Confirmar contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = acceptTerms, onCheckedChange = { acceptTerms = it })
            Text("Acepto términos", fontSize = 16.sp)
        }

        Text("Accesibilidad:", modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = !isLargeText, onClick = { isLargeText = false })
            Text("Normal")
            RadioButton(selected = isLargeText, onClick = { isLargeText = true })
            Text("Grande")
        }

        Text("Recordatorio:", fontWeight = FontWeight.Bold)
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

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (pass == confirmPass && acceptTerms) {
                    val success = Repository.agregarUsuario(User(nombre, email, pass, isLargeText, reminderType.contains("Visual")))
                    if (success) onRegistered()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Registrarme")
        }
    }
}

// Pantalla de Recuperar Contraseña
@Composable
fun RecoverPasswordView(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Recuperar contraseña", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Text("Se enviará un enlace de restablecimiento a su correo.", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { mensaje = "Instrucciones enviadas a $email" }, modifier = Modifier.fillMaxWidth()) {
            Text("Enviar instrucciones")
        }
        if (mensaje.isNotEmpty()) {
            Text(mensaje, color = Color.Green, modifier = Modifier.padding(top = 16.dp))
        }
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Volver al login")
        }
    }
}

// Pantalla Principal (Home)
@Composable
fun HomeView(onAddMedication: () -> Unit) {
    val meds = Repository.medicamentos
    val now = LocalTime.now()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Próximos medicamentos", fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        
        if (meds.isEmpty()) {
            Text("No hay medicamentos registrados.", fontSize = 18.sp)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(meds) { med ->
                    MedicationCard(med, now)
                }
            }
        }

        Button(
            onClick = onAddMedication,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(60.dp)
        ) {
            Text("Agregar medicamento", fontSize = 20.sp)
        }
    }
}

@Composable
fun MedicationCard(med: Medicamento, now: LocalTime) {
    val duration = Duration.between(now, med.horaInicial)
    val diff = if (duration.isNegative) "Ya pasó" else {
        val h = duration.toHours()
        val m = duration.toMinutes() % 60
        val s = duration.seconds % 60
        "Faltan %02d:%02d:%02d".format(h, m, s)
    }

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(med.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Dosis: ${med.dosis}", fontSize = 18.sp)
            Text("Hora: ${med.horaInicial.format(DateTimeFormatter.ofPattern("HH:mm"))}", fontSize = 18.sp)
            Text(diff, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
        }
    }
}

// Pantalla Formulario Medicamento
@Composable
fun MedicationFormView(onSaved: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var intervalo by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var repetir by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())) {
        Text("Nuevo Medicamento", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = dosis, onValueChange = { dosis = it }, label = { Text("Cantidad/Dosis") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = intervalo, onValueChange = { intervalo = it }, label = { Text("Intervalo (horas)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = hora, onValueChange = { hora = it }, label = { Text("Hora inicial (HH:mm)") }, modifier = Modifier.fillMaxWidth())
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = repetir, onCheckedChange = { repetir = it })
            Text("Repetir diario", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                try {
                    val t = LocalTime.parse(hora)
                    Repository.agregarMedicamento(Medicamento(nombre, dosis, intervalo.toInt(), t, repetir))
                    onSaved()
                } catch (e: Exception) {}
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Guardar")
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        Text("Resumen actual:", fontWeight = FontWeight.Bold)
        Repository.medicamentos.forEach {
            Text("- ${it.nombre} (${it.dosis})", fontSize = 16.sp)
        }
    }
}
