package com.example.medicinecontrol.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.medicinecontrol.HomeActivity
import com.example.medicinecontrol.MedicamentoCatalogo
import com.example.medicinecontrol.R
import com.example.medicinecontrol.Repository
import com.example.medicinecontrol.SpeechToTextButton
import com.example.medicinecontrol.ui.theme.MedicineControlTheme
import kotlinx.coroutines.launch

class MedicinesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ComposeView>(R.id.compose_view).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MedicineControlTheme {
                    MedicinesContent(
                        onNavigateToHome = {
                            (requireActivity() as? HomeActivity)?.navigateToHome()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MedicinesContent(onNavigateToHome: () -> Unit) {
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
            title = { Text("Nuevo Medicamento", style = MaterialTheme.typography.headlineSmall) },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.weight(1f)
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
                                    Text("Formato", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(formatoSeleccionado, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                        DropdownMenu(expanded = menuFormatoOpen, onDismissRequest = { menuFormatoOpen = false }) {
                            formatos.keys.forEach { formato ->
                                DropdownMenuItem(
                                    text = { Text(formato) },
                                    onClick = { formatoSeleccionado = formato; menuFormatoOpen = false }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = cantidadDosis,
                        onValueChange = { cantidadDosis = it },
                        label = { Text("Cantidad") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        suffix = { Text(formatos[formatoSeleccionado] ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                    )
                    if (isLoading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        if (nombre.isNotBlank() && cantidadDosis.isNotBlank()) {
                            isLoading = true
                            scope.launch {
                                val dosisCompleta = "$cantidadDosis ${formatos[formatoSeleccionado]}"
                                Repository.agregarAlCatalogo(MedicamentoCatalogo(nombre, dosisCompleta))
                                nombre = ""; cantidadDosis = ""; formatoSeleccionado = "Pastillas"
                                isLoading = false; showDialog = false
                            }
                        }
                    },
                    enabled = !isLoading
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false; nombre = ""; cantidadDosis = ""; formatoSeleccionado = "Pastillas" },
                    enabled = !isLoading
                ) { Text("Cancelar") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mis Medicamentos", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text("Catálogo de medicamentos guardados", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(20.dp))

        if (catalogo.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No hay medicamentos guardados", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text("Agrega tu primer medicamento al catálogo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(catalogo) { med ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(med.nombre, style = MaterialTheme.typography.titleMedium)
                                Text("Dosis: ${med.dosis}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        ExtendedFloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Agregar medicamento", style = MaterialTheme.typography.labelLarge) }
        )

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onNavigateToHome, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Volver al inicio")
        }
    }
}
