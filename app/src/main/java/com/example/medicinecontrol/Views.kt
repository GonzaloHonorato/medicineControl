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

@Composable
fun LoginView(onLogin: () -> Unit, onNavigateToRegister: () -> Unit, onNavigateToRecover: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("MedicineControl", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(40.dp))
        
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
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
                val user = Repository.usuarios.find { it.email == email && it.contrasena == password }
                if (user != null) onLogin() else errorMsg = "Usuario o clave incorrecta"
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

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Registro", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = pass, onValueChange = { pass = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = confirm, onValueChange = { confirm = it }, label = { Text("Confirmar") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        
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
                if (pass == confirm && accept && nombre.isNotBlank()) {
                    if (Repository.agregarUsuario(User(nombre, email, pass, largeText, recordatorio == "Visual"))) {
                        onRegistered()
                    }
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
    var enviado by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Recuperar acceso", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Text("Se enviará un enlace de recuperación a su correo.", fontSize = 16.sp)
        
        if (enviado) {
            Text("Enviado correctamente.", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { enviado = true }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Enviar instrucciones")
        }
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Volver", fontSize = 16.sp)
        }
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Próximos medicamentos", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(20.dp))
        
        if (meds.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("No hay registros", fontSize = 18.sp, color = Color.Gray)
            }
        } else {
            val sortedMeds = meds.sortedBy { it.horaInicial }
            val next = sortedMeds.first()
            val remaining = sortedMeds.drop(1)

            Text("Próximo ahora:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            MedicationCard(next, now, true)
            
            if (remaining.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Siguientes tomas:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
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
    val duration = Duration.between(now, med.horaInicial)
    val timeStr = if (duration.isNegative) "Ahora" else {
        val h = duration.toHours()
        val m = duration.toMinutes() % 60
        val s = duration.seconds % 60
        "Faltan %02d:%02d:%02d".format(h, m, s)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isNext) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) 
                 else CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(med.nombre, fontSize = if (isNext) 26.sp else 22.sp, fontWeight = FontWeight.Bold)
            Text("Dosis: ${med.dosis}", fontSize = 18.sp)
            Text("Hora: ${med.horaInicial.format(DateTimeFormatter.ofPattern("HH:mm"))}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(timeStr, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun MedicationFormView(onSaved: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var horas by remember { mutableStateOf("") }
    var horaIni by remember { mutableStateOf("") }
    var diario by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Nuevo Medicamento", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        
        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = dosis, onValueChange = { dosis = it }, label = { Text("Dosis") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = horas, onValueChange = { horas = it }, label = { Text("Intervalo (horas)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = horaIni, onValueChange = { horaIni = it }, label = { Text("Hora inicio (HH:mm)") }, modifier = Modifier.fillMaxWidth())
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
            Checkbox(checked = diario, onCheckedChange = { diario = it })
            Text("Repetir diario", fontSize = 18.sp)
        }

        Button(
            onClick = {
                try {
                    val time = LocalTime.parse(horaIni)
                    Repository.agregarMedicamento(Medicamento(nombre, dosis, horas.toIntOrNull() ?: 0, time, diario))
                    onSaved()
                } catch (e: Exception) {}
            },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Guardar", fontSize = 20.sp)
        }
        
        if (Repository.medicamentos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Lista actual:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Repository.medicamentos.forEach {
                Text("• ${it.nombre} - ${it.dosis}", fontSize = 16.sp)
            }
        }
    }
}
